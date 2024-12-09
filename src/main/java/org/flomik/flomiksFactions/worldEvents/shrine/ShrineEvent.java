package org.flomik.flomiksFactions.worldEvents.shrine;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.flomik.flomiksFactions.FlomiksFactions;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShrineEvent {

    private final FlomiksFactions plugin;
    private List<Location> shrineLocations;
    private Location activeShrineLocation;
    private File shrineFile;
    private FileConfiguration shrineConfig;

    public ShrineEvent(FlomiksFactions plugin) {
        this.plugin = plugin;
        this.shrineFile = new File(plugin.getDataFolder(), "shrines.yml");
        this.shrineConfig = YamlConfiguration.loadConfiguration(shrineFile);
        this.shrineLocations = new ArrayList<>();

        loadShrinesFromFile();
        startDailyEventScheduler();
    }


    public void loadShrinesFromFile() {
        if (shrineConfig.contains("shrines")) {
            List<?> locations = shrineConfig.getList("shrines");
            for (Object obj : locations) {
                if (obj instanceof String) {
                    shrineLocations.add(stringToLocation((String) obj));
                }
            }
        }
    }


    public void saveShrinesToFile() {
        List<String> locationStrings = new ArrayList<>();
        for (Location loc : shrineLocations) {
            locationStrings.add(locationToString(loc));
        }
        shrineConfig.set("shrines", locationStrings);

        try {
            shrineConfig.save(shrineFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Не удалось сохранить shrines.yml");
        }
    }


    private String locationToString(Location loc) {
        return loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }


    private Location stringToLocation(String locStr) {
        String[] parts = locStr.split(",");
        World world = Bukkit.getWorld(parts[0]);
        int x = Integer.parseInt(parts[1]);
        int y = Integer.parseInt(parts[2]);
        int z = Integer.parseInt(parts[3]);
        return new Location(world, x, y, z);
    }


    public void addShrineLocation(Player player) {
        Location loc = player.getLocation();
        shrineLocations.add(loc);
        saveShrinesToFile();


        buildShrine(loc);


        createWorldGuardRegion(player, loc);

        player.sendMessage("Точка Святилища Опыта добавлена в координатах: X=" + loc.getBlockX() + " Y=" + loc.getBlockY() + " Z=" + loc.getBlockZ());
    }


    private void createWorldGuardRegion(Player player, Location location) {
        World world = location.getWorld();
        Chunk chunk = location.getChunk();

        String regionName = "shrine_" + chunk.getX() + "_" + chunk.getZ();
        WorldGuard wg = WorldGuard.getInstance();
        RegionContainer container = wg.getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(world));

        if (regions != null) {

            BlockVector3 min = BlockVector3.at(chunk.getX() << 4, 0, chunk.getZ() << 4);
            BlockVector3 max = BlockVector3.at((chunk.getX() << 4) + 15, world.getMaxHeight(), (chunk.getZ() << 4) + 15);


            ProtectedCuboidRegion region = new ProtectedCuboidRegion(regionName, min, max);

            try {

                region.setFlag(Flags.BLOCK_BREAK, StateFlag.State.DENY);
                region.setFlag(Flags.BLOCK_PLACE, StateFlag.State.DENY);
                region.setFlag(Flags.TNT, StateFlag.State.DENY);
                region.setFlag(Flags.CREEPER_EXPLOSION, StateFlag.State.DENY);
                region.setFlag(Flags.MOB_SPAWNING, StateFlag.State.DENY);
                region.setFlag(Flags.PVP, StateFlag.State.ALLOW);


                regions.addRegion(region);


                DefaultDomain owners = region.getOwners();
                owners.addPlayer(player.getUniqueId());
                region.setOwners(owners);

                player.sendMessage("Регион чанка был успешно создан и защищен с помощью WorldGuard.");
            } catch (Exception e) {
                player.sendMessage("Не удалось создать регион: " + e.getMessage());
            }
        }
    }


    private void buildShrine(Location location) {
        World world = location.getWorld();

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                Block block = world.getBlockAt(location.clone().add(dx, -1, dz));
                block.setType(Material.GOLD_BLOCK);
            }
        }
    }


    public void deactivateShrine() {
        if (activeShrineLocation != null) {
            activeShrineLocation = null;
        }
    }


    public void deleteAllSanctuaries() {

        WorldGuard wg = WorldGuard.getInstance();
        for (World world : Bukkit.getWorlds()) {
            RegionContainer container = wg.getPlatform().getRegionContainer();
            RegionManager regions = container.get(BukkitAdapter.adapt(world));

            if (regions != null) {
                List<String> regionsToRemove = new ArrayList<>();


                for (ProtectedRegion region : regions.getRegions().values()) {
                    if (region.getId().startsWith("shrine_")) {
                        regionsToRemove.add(region.getId());
                    }
                }


                for (String regionId : regionsToRemove) {
                    try {
                        regions.removeRegion(regionId);
                        Bukkit.broadcastMessage("Регион шрайна " + regionId + " был удален.");
                    } catch (Exception e) {
                        plugin.getLogger().severe("Не удалось удалить регион: " + regionId);
                    }
                }
            }
        }


        shrineLocations.clear();
        saveShrinesToFile();

        Bukkit.broadcastMessage("Все точки Святилищ Опыта и приваты шрайнов были удалены.");
    }



    public Location getActiveShrineLocation() {
        return activeShrineLocation;
    }


    public void cancelShrineEvent() {
        if (activeShrineLocation == null) {
            Bukkit.broadcastMessage("Нет активного ивента Святилище Опыта для отмены.");
            return;
        }


        Bukkit.broadcastMessage("Ивент Святилище Опыта был отменен.");


        activeShrineLocation = null;
    }


    public void removeShrineLocation(Player player) {
        Location playerLocation = player.getLocation();
        Location locationToRemove = null;


        for (Location loc : shrineLocations) {
            if (loc.getBlockX() == playerLocation.getBlockX()
                    && loc.getBlockY() == playerLocation.getBlockY()
                    && loc.getBlockZ() == playerLocation.getBlockZ()) {
                locationToRemove = loc;
                break;
            }
        }

        if (locationToRemove != null) {
            shrineLocations.remove(locationToRemove);
            saveShrinesToFile();
            player.sendMessage("Точка Святилища Опыта на координатах X=" + locationToRemove.getBlockX()
                    + " Y=" + locationToRemove.getBlockY() + " Z=" + locationToRemove.getBlockZ() + " была удалена.");
        } else {
            player.sendMessage("Точка на текущих координатах не найдена.");
        }
    }


    public void startShrineEvent() {
        if (shrineLocations.isEmpty()) {
            Bukkit.broadcastMessage("Нет доступных точек Святилища Опыта для запуска.");
            return;
        }


        Random random = new Random();
        activeShrineLocation = shrineLocations.get(random.nextInt(shrineLocations.size()));


        buildShrine(activeShrineLocation);


        Bukkit.broadcastMessage(ChatColor.GREEN + "Ивент Святилище Опыта начался! Координаты святилища: " +
                "X: " + ChatColor.YELLOW + activeShrineLocation.getBlockX() + ChatColor.GREEN + ", Y: " + ChatColor.YELLOW + activeShrineLocation.getBlockY() + ChatColor.GREEN + ", Z: " + ChatColor.YELLOW + activeShrineLocation.getBlockZ());


        ShrineCaptureManager captureManager = new ShrineCaptureManager(this, plugin, plugin.clanManager);
        captureManager.startCaptureMechanism();
    }


    private void startDailyEventScheduler() {
        new BukkitRunnable() {
            @Override
            public void run() {

                ZoneId moscowZoneId = ZoneId.of("Europe/Moscow");
                LocalTime currentTime = LocalTime.now(moscowZoneId);
                LocalTime startTime = LocalTime.of(16, 0);
                LocalTime endTime = LocalTime.of(18, 0);


                if (currentTime.isAfter(startTime) && currentTime.isBefore(endTime)) {

                    Random random = new Random();
                    int randomMinutes = random.nextInt(120 + 1);


                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            deactivateShrine();
                            startShrineEvent();
                        }
                    }.runTaskLater(plugin, randomMinutes * 60L);
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 1200L);
    }

    public List<Location> getShrineLocations() {
        return new ArrayList<>(shrineLocations);
    }
}
