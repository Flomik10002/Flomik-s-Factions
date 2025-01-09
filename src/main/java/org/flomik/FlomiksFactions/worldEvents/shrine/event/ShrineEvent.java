package org.flomik.FlomiksFactions.worldEvents.shrine.event;

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
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.flomik.FlomiksFactions.FlomiksFactions;
import org.flomik.FlomiksFactions.worldEvents.shrine.config.ShrineConfigManager;
import org.flomik.FlomiksFactions.worldEvents.shrine.managers.ShrineEventManager;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShrineEvent {

    private final FlomiksFactions plugin;
    private List<Location> shrineLocations;
    private Location activeShrineLocation;
    private final ShrineConfigManager configManager;

    public ShrineEvent(FlomiksFactions plugin, ShrineConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.shrineLocations = new ArrayList<>();

        loadShrinesFromFile();
        startDailyEventScheduler();
    }

    public void loadShrinesFromFile() {
        List<String> locationStrings = configManager.get().getStringList("shrines.locations");
        for (String locStr : locationStrings) {
            shrineLocations.add(stringToLocation(locStr));
        }
    }

    public void saveShrinesToFile() {
        List<String> locationStrings = new ArrayList<>();
        for (Location loc : shrineLocations) {
            locationStrings.add(locationToString(loc));
        }
        configManager.get().set("shrines.locations", locationStrings);
        configManager.save();
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

        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                configManager.get().getString("messages.added")
                        .replace("{x}", String.valueOf(loc.getBlockX()))
                        .replace("{y}", String.valueOf(loc.getBlockY()))
                        .replace("{z}", String.valueOf(loc.getBlockZ()))));
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

                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        configManager.get().getString("messages.region-created")));
            } catch (Exception e) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        configManager.get().getString("messages.region-error"))+e.getMessage());
            }
        }
    }

    private void buildShrine(Location location) {
        World world = location.getWorld();
        Material shrineBlock = Material.valueOf(configManager.get().getString("settings.shrine-block", "GOLD_BLOCK"));

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                Block block = world.getBlockAt(location.clone().add(dx, -1, dz));
                block.setType(shrineBlock);
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
                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                                configManager.get().getString("messages.region-removed")
                                        .replace("{regionId}", regionId)));
                    } catch (Exception e) {
                        plugin.getLogger().severe("Unable to delete region: " + regionId);
                    }
                }
            }
        }

        shrineLocations.clear();
        saveShrinesToFile();

        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                configManager.get().getString("messages.all-sanctuaries-removed")));
    }

    public Location getActiveShrineLocation() {
        return activeShrineLocation;
    }

    public void cancelShrineEvent() {
        if (activeShrineLocation == null) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                    configManager.get().getString("messages.no-active-event")));
            return;
        }

        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                configManager.get().getString("messages.event-canceled")));

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
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    configManager.get().getString("messages.sanctuary-removed")
                            .replace("{x}", String.valueOf(locationToRemove.getBlockX()))
                            .replace("{y}", String.valueOf(locationToRemove.getBlockY()))
                            .replace("{z}", String.valueOf(locationToRemove.getBlockZ()))));
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    configManager.get().getString("messages.sanctuary-not-found")));
        }
    }


    public void startShrineEvent() {
        if (shrineLocations.isEmpty()) {
            Bukkit.broadcastMessage(ChatColor.RED + configManager.get().getString("messages.is-empty"));
            return;
        }

        Random random = new Random();
        activeShrineLocation = shrineLocations.get(random.nextInt(shrineLocations.size()));

        buildShrine(activeShrineLocation);

        String startMessage = configManager.get().getString("messages.start");
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                startMessage.replace("{x}", String.valueOf(activeShrineLocation.getBlockX()))
                        .replace("{y}", String.valueOf(activeShrineLocation.getBlockY()))
                        .replace("{z}", String.valueOf(activeShrineLocation.getBlockZ()))));

        ShrineEventManager captureManager = new ShrineEventManager(this, plugin, plugin.getClanManager());
        captureManager.startCaptureMechanism();
    }

    private void startDailyEventScheduler() {
        new BukkitRunnable() {
            @Override
            public void run() {
                ZoneId zoneId = ZoneId.of(configManager.get().getString("settings.timezone", "Europe/Moscow"));
                LocalTime currentTime = LocalTime.now(zoneId);
                LocalTime startTime = LocalTime.parse(configManager.get().getString("settings.start-time"));
                LocalTime endTime = LocalTime.parse(configManager.get().getString("settings.end-time"));

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
