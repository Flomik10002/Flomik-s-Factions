package org.flomik.FlomiksFactions.commands.handlers.clanInteractions;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.flomik.FlomiksFactions.clan.Clan;
import org.flomik.FlomiksFactions.clan.managers.BeaconManager;
import org.flomik.FlomiksFactions.clan.managers.ClanManager;
import org.flomik.FlomiksFactions.database.BeaconDao;

import java.util.Map;

public class UnclaimRegionHandler {

    private final ClanManager clanManager;
    private final BeaconDao beaconDao;
    private final BeaconManager beaconManager;

    public UnclaimRegionHandler(ClanManager clanManager, BeaconDao beaconDao, BeaconManager beaconManager) {
        this.clanManager = clanManager;
        this.beaconDao = beaconDao;
        this.beaconManager = beaconManager;
    }


    public boolean handleCommand(Player player, String[] args) {
        Clan clan = clanManager.getPlayerClan(player.getName());

        if (clan == null || !isLeaderOrDeputy(player, clan)) {
            player.sendMessage(ChatColor.RED + "У вас нет прав для выполнения этой команды.");
            return true;
        }

        if (args.length > 1 && args[1].equalsIgnoreCase("all")) {

            unclaimAllRegionsForClan(player, clan);
        } else {

            unclaimCurrentChunk(player, clan);
        }

        return true;
    }


    private void unclaimCurrentChunk(Player player, Clan clan) {
        Chunk chunk = player.getLocation().getChunk();
        String chunkId = getChunkId(chunk);


        if (!clan.hasClaimedChunk(chunkId)) {
            player.sendMessage(ChatColor.RED + "Этот чанк не принадлежит вашему клану.");
            return;
        }


        removeWorldGuardRegion(chunk, clan.getName());

        String regionId = "clan_" + clan.getName() + "_" + chunkId;
        beaconDao.deleteBeaconByRegionId(regionId);
        beaconManager.removeBeacon(regionId);


        if (isHomeInChunk(clan, chunk)) {
            clan.removeHome();
            player.sendMessage(ChatColor.YELLOW + "Точка дома была удалена, так как она находилась в этом привате.");
        }


        clan.removeClaimedChunk(chunkId);
        clanManager.sendClanMessage(clan, ChatColor.GREEN + "Игрок " + ChatColor.YELLOW + player.getName() + ChatColor.GREEN + " убрал приват с чанка!");
    }

    public void removeRegionById(World world, String regionId) {
        WorldGuard wg = WorldGuard.getInstance();
        RegionContainer container = wg.getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(world));

        if (regions != null) {
            regions.removeRegion(regionId);
        }
    }


    private void unclaimAllRegionsForClan(Player player, Clan clan) {
        WorldGuard wg = WorldGuard.getInstance();
        RegionContainer container = wg.getPlatform().getRegionContainer();

        for (World world : player.getServer().getWorlds()) {
            RegionManager regions = container.get(BukkitAdapter.adapt(world));

            if (regions != null) {
                for (Map.Entry<String, ProtectedRegion> entry : regions.getRegions().entrySet()) {
                    ProtectedRegion region = entry.getValue();


                    if (region.getId().startsWith("clan_" + clan.getName()) && isLeaderOrDeputy(player, clan)) {
                        regions.removeRegion(region.getId());

                        Location homeLocation = clan.getHome();
                        if (homeLocation != null && isHomeInRegion(region, homeLocation)) {
                            clan.removeHome();
                            clanManager.sendClanMessage(clan,ChatColor.YELLOW + "Точка дома была удалена, так как она находилась в одном из удалённых приватов.");
                        }
                    }
                }
                clan.clearClaimedChunks();
            }
        }
        clanManager.sendClanMessage(clan, ChatColor.GREEN + "Игрок " + ChatColor.YELLOW + player.getName() + ChatColor.GREEN + " убрал все приваты!");
    }


    private boolean isLeaderOrDeputy(Player player, Clan clan) {
        String playerRole = clan.getRole(player.getName());
        return playerRole.equals("Лидер") || playerRole.equals("Заместитель");
    }


    private String getChunkId(Chunk chunk) {
        return chunk.getWorld().getName() + "_" + chunk.getX() + "_" + chunk.getZ();
    }


    public void removeWorldGuardRegion(Chunk chunk, String clanName) {
        WorldGuard wg = WorldGuard.getInstance();
        RegionContainer container = wg.getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(chunk.getWorld()));
        String regionId = "clan_" + clanName + "_" + getChunkId(chunk);
        if (regions != null) {
            regions.removeRegion(regionId);
        }
    }


    private boolean isHomeInChunk(Clan clan, Chunk chunk) {
        Location homeLocation = clan.getHome();
        if (homeLocation == null) {
            return false;
        }

        return homeLocation.getChunk().equals(chunk);
    }


    private boolean isHomeInRegion(ProtectedRegion region, Location homeLocation) {
        String regionId = region.getId();
        String homeChunkId = getChunkId(homeLocation.getChunk());
        return regionId.contains(homeChunkId);
    }
}