package org.flomik.FlomiksFactions.worldEvents.shrine.event; //NOPMD - suppressed CouplingBetweenObjects - TODO explain reason for suppression //NOPMD - suppressed CouplingBetweenObjects - TODO explain reason for suppression //NOPMD - suppressed CouplingBetweenObjects - TODO explain reason for suppression

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

public class ShrineEvent { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression

    private final FlomiksFactions plugin; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private List<Location> shrineLocations; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private Location activeShrineLocation; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private final ShrineConfigManager configManager; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression

    public ShrineEvent(FlomiksFactions plugin, ShrineConfigManager configManager) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        this.plugin = plugin;
        this.configManager = configManager;
        this.shrineLocations = new ArrayList<>();

        loadShrinesFromFile(); //NOPMD - suppressed ConstructorCallsOverridableMethod - TODO explain reason for suppression //NOPMD - suppressed ConstructorCallsOverridableMethod - TODO explain reason for suppression //NOPMD - suppressed ConstructorCallsOverridableMethod - TODO explain reason for suppression
        startDailyEventScheduler();
    }

    public void loadShrinesFromFile() { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        List<String> locationStrings = configManager.get().getStringList("shrines.locations"); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        for (String locStr : locationStrings) { //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            shrineLocations.add(stringToLocation(locStr));
        }
    }

    public void saveShrinesToFile() { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        List<String> locationStrings = new ArrayList<>(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        for (Location loc : shrineLocations) { //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            locationStrings.add(locationToString(loc));
        }
        configManager.get().set("shrines.locations", locationStrings);
        configManager.save();
    }

    private String locationToString(Location loc) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        return loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ(); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
    }

    private Location stringToLocation(String locStr) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        String[] parts = locStr.split(","); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        World world = Bukkit.getWorld(parts[0]); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        int x = Integer.parseInt(parts[1]); //NOPMD - suppressed ShortVariable - TODO explain reason for suppression //NOPMD - suppressed ShortVariable - TODO explain reason for suppression //NOPMD - suppressed ShortVariable - TODO explain reason for suppression
        int y = Integer.parseInt(parts[2]); //NOPMD - suppressed ShortVariable - TODO explain reason for suppression //NOPMD - suppressed ShortVariable - TODO explain reason for suppression //NOPMD - suppressed ShortVariable - TODO explain reason for suppression
        int z = Integer.parseInt(parts[3]); //NOPMD - suppressed ShortVariable - TODO explain reason for suppression //NOPMD - suppressed ShortVariable - TODO explain reason for suppression //NOPMD - suppressed ShortVariable - TODO explain reason for suppression
        return new Location(world, x, y, z);
    }

    public void addShrineLocation(Player player) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        Location loc = player.getLocation(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
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

    private void createWorldGuardRegion(Player player, Location location) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        World world = location.getWorld(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        Chunk chunk = location.getChunk(); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression

        String regionName = "shrine_" + chunk.getX() + "_" + chunk.getZ(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        WorldGuard wg = WorldGuard.getInstance(); //NOPMD - suppressed ShortVariable - TODO explain reason for suppression //NOPMD - suppressed ShortVariable - TODO explain reason for suppression //NOPMD - suppressed ShortVariable - TODO explain reason for suppression
        RegionContainer container = wg.getPlatform().getRegionContainer(); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
        RegionManager regions = container.get(BukkitAdapter.adapt(world)); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression

        if (regions != null) {
            BlockVector3 min = BlockVector3.at(chunk.getX() << 4, 0, chunk.getZ() << 4); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            BlockVector3 max = BlockVector3.at((chunk.getX() << 4) + 15, world.getMaxHeight(), (chunk.getZ() << 4) + 15); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression

            ProtectedCuboidRegion region = new ProtectedCuboidRegion(regionName, min, max); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression

            try {
                region.setFlag(Flags.BLOCK_BREAK, StateFlag.State.DENY);
                region.setFlag(Flags.BLOCK_PLACE, StateFlag.State.DENY);
                region.setFlag(Flags.TNT, StateFlag.State.DENY);
                region.setFlag(Flags.CREEPER_EXPLOSION, StateFlag.State.DENY);
                region.setFlag(Flags.MOB_SPAWNING, StateFlag.State.DENY);
                region.setFlag(Flags.PVP, StateFlag.State.ALLOW);

                regions.addRegion(region);

                DefaultDomain owners = region.getOwners(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                owners.addPlayer(player.getUniqueId());
                region.setOwners(owners);

                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        configManager.get().getString("messages.region-created")));
            } catch (Exception e) { //NOPMD - suppressed AvoidCatchingGenericException - TODO explain reason for suppression //NOPMD - suppressed AvoidCatchingGenericException - TODO explain reason for suppression //NOPMD - suppressed AvoidCatchingGenericException - TODO explain reason for suppression
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        configManager.get().getString("messages.region-error"))+e.getMessage());
            }
        }
    }

    private void buildShrine(Location location) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        World world = location.getWorld(); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
        Material shrineBlock = Material.valueOf(configManager.get().getString("settings.shrine-block", "GOLD_BLOCK")); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                Block block = world.getBlockAt(location.clone().add(dx, -1, dz)); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
                block.setType(shrineBlock);
            }
        }
    }

    public void deactivateShrine() { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        if (activeShrineLocation != null) {
            activeShrineLocation = null; //NOPMD - suppressed NullAssignment - TODO explain reason for suppression //NOPMD - suppressed NullAssignment - TODO explain reason for suppression //NOPMD - suppressed NullAssignment - TODO explain reason for suppression
        }
    }

    public void deleteAllSanctuaries() { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        WorldGuard wg = WorldGuard.getInstance(); //NOPMD - suppressed ShortVariable - TODO explain reason for suppression //NOPMD - suppressed ShortVariable - TODO explain reason for suppression //NOPMD - suppressed ShortVariable - TODO explain reason for suppression
        for (World world : Bukkit.getWorlds()) { //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            RegionContainer container = wg.getPlatform().getRegionContainer(); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
            RegionManager regions = container.get(BukkitAdapter.adapt(world)); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression

            if (regions != null) {
                List<String> regionsToRemove = new ArrayList<>(); //NOPMD - suppressed AvoidInstantiatingObjectsInLoops - TODO explain reason for suppression //NOPMD - suppressed AvoidInstantiatingObjectsInLoops - TODO explain reason for suppression //NOPMD - suppressed AvoidInstantiatingObjectsInLoops - TODO explain reason for suppression

                for (ProtectedRegion region : regions.getRegions().values()) { //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                    if (region.getId().startsWith("shrine_")) {
                        regionsToRemove.add(region.getId());
                    }
                }

                for (String regionId : regionsToRemove) { //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                    try {
                        regions.removeRegion(regionId);
                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                                configManager.get().getString("messages.region-removed")
                                        .replace("{regionId}", regionId)));
                    } catch (Exception e) { //NOPMD - suppressed AvoidCatchingGenericException - TODO explain reason for suppression //NOPMD - suppressed AvoidCatchingGenericException - TODO explain reason for suppression //NOPMD - suppressed AvoidCatchingGenericException - TODO explain reason for suppression
                        plugin.getLogger().severe("Unable to delete region: " + regionId); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
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

    public void cancelShrineEvent() { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        if (activeShrineLocation == null) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                    configManager.get().getString("messages.no-active-event")));
            return;
        }

        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                configManager.get().getString("messages.event-canceled")));

        activeShrineLocation = null; //NOPMD - suppressed NullAssignment - TODO explain reason for suppression //NOPMD - suppressed NullAssignment - TODO explain reason for suppression //NOPMD - suppressed NullAssignment - TODO explain reason for suppression
    }


    public void removeShrineLocation(Player player) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        Location playerLocation = player.getLocation(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        Location locationToRemove = null;

        for (Location loc : shrineLocations) { //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
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


    public void startShrineEvent() { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        if (shrineLocations.isEmpty()) {
            Bukkit.broadcastMessage(ChatColor.RED + configManager.get().getString("messages.is-empty"));
            return;
        }

        Random random = new Random(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        activeShrineLocation = shrineLocations.get(random.nextInt(shrineLocations.size()));

        buildShrine(activeShrineLocation);

        String startMessage = configManager.get().getString("messages.start"); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                startMessage.replace("{x}", String.valueOf(activeShrineLocation.getBlockX()))
                        .replace("{y}", String.valueOf(activeShrineLocation.getBlockY()))
                        .replace("{z}", String.valueOf(activeShrineLocation.getBlockZ()))));

        ShrineEventManager captureManager = new ShrineEventManager(this, plugin.getClanNotificationService(), plugin, plugin.getClanManager()); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        captureManager.startCaptureMechanism();
    }

    private void startDailyEventScheduler() {
        new BukkitRunnable() {
            @Override
            public void run() {
                ZoneId zoneId = ZoneId.of(configManager.get().getString("settings.timezone", "Europe/Moscow")); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                LocalTime currentTime = LocalTime.now(zoneId); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                LocalTime startTime = LocalTime.parse(configManager.get().getString("settings.start-time")); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                LocalTime endTime = LocalTime.parse(configManager.get().getString("settings.end-time")); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression

                if (currentTime.isAfter(startTime) && currentTime.isBefore(endTime)) {

                    Random random = new Random(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                    int randomMinutes = random.nextInt(120 + 1); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression


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
