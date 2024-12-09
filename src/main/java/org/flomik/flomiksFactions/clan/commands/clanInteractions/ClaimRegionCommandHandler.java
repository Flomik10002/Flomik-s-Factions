package org.flomik.flomiksFactions.clan.commands.clanInteractions;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.flomik.flomiksFactions.clan.Clan;
import org.flomik.flomiksFactions.clan.ClanManager;
import org.flomik.flomiksFactions.worldEvents.shrine.ShrineEvent;

public class ClaimRegionCommandHandler {

    private final ClanManager clanManager;
    private final UnclaimRegionCommandHandler unclaimRegionCommandHandler;
    private final ShrineEvent shrineEvent;

    public ClaimRegionCommandHandler(ClanManager clanManager, UnclaimRegionCommandHandler unclaimRegionCommandHandler, ShrineEvent shrineEvent) {
        this.clanManager = clanManager;
        this.unclaimRegionCommandHandler = unclaimRegionCommandHandler;
        this.shrineEvent = shrineEvent;
    }

    public boolean handleCommand(Player player) {
        Clan clan = clanManager.getPlayerClan(player.getName());

        if (clan == null || !isLeaderOrDeputy(player, clan)) {
            player.sendMessage(ChatColor.RED + "У вас нет прав для выполнения этой команды.");
            return true;
        }

        Chunk chunk = player.getLocation().getChunk();
        String chunkId = getChunkId(chunk);

        if (isShrineChunk(chunk)) {
            player.sendMessage(ChatColor.RED + "Этот чанк является точкой Святилища и не может быть приватизирован.");
            return true;
        }

        if (isChunkClaimed(chunkId, clan)) {
            player.sendMessage(ChatColor.YELLOW + "Этот чанк уже занят вашим кланом.");
            return true;
        }

        if (isEnoughStrength(clan)) {
            player.sendMessage(ChatColor.YELLOW + "У вашего клана недостаточно силы.");
            return true;
        }

        if (isChunkClaimedByAnotherClan(chunkId, clan)) {
            for (Clan oldClan : clanManager.clans.values()) {
                if (!oldClan.equals(clan) && oldClan.hasClaimedChunk(chunkId)) {
                    if (oldClan.getLands() > oldClan.getStrength())
                    {
                        oldClan.removeClaimedChunk(chunkId);
                        unclaimRegionCommandHandler.removeWorldGuardRegion(chunk, oldClan.getName());

                        addWorldGuardRegion(chunk, clan.getName(), player);
                        clan.addClaimedChunk(chunkId);

                        clanManager.sendClanMessage(clan, ChatColor.GREEN + "Игрок " + ChatColor.YELLOW + player.getName() + ChatColor.GREEN + " захватил территорию клана " + ChatColor.YELLOW + oldClan.getName() + ChatColor.GREEN + "!");
                        clanManager.sendClanMessage(oldClan, ChatColor.RED + "Клан " + ChatColor.GOLD + clan.getName() + ChatColor.RED + " захватил один чанк вашей территории!");
                        return true;
                    }
                }
            }
        }


        if (isChunkClaimedByAnotherClan(chunkId, clan)) {
            player.sendMessage(ChatColor.RED + "Этот чанк уже занят кланом.");
            return true;
        }


        addWorldGuardRegion(chunk, clan.getName(), player);


        clan.addClaimedChunk(chunkId);
        clanManager.sendClanMessage(clan, ChatColor.GREEN + "Игрок " + ChatColor.YELLOW + player.getName() + ChatColor.GREEN + " заприватил территорию!");

        return true;
    }

    private boolean isEnoughStrength(Clan clan) {
        if (clan.getStrength() <= clan.getLands()){
            return true;
        }
        return false;
    }

    private boolean isLeaderOrDeputy(Player player, Clan clan) {
        String playerRole = clan.getRole(player.getName());
        return playerRole.equals("Лидер") || playerRole.equals("Заместитель");
    }

    private String getChunkId(Chunk chunk) {

        return chunk.getWorld().getName() + "_" + chunk.getX() + "_" + chunk.getZ();
    }

    private boolean isChunkClaimedByAnotherClan(String chunkId, Clan currentClan) {
        for (Clan clan : clanManager.clans.values()) {
            if (!clan.equals(currentClan) && clan.hasClaimedChunk(chunkId)) {
                return true;
            }
        }
        return false;
    }

    private boolean isChunkClaimed(String chunkId, Clan currentClan) {
        for (Clan clan : clanManager.clans.values()) {
            if (clan.equals(currentClan) && clan.hasClaimedChunk(chunkId)) {
                return true;
            }
        }
        return false;
    }

    private void addWorldGuardRegion(Chunk chunk, String clanName, Player player) {
        Clan clan = clanManager.getPlayerClan(player.getName());
        WorldGuard wg = WorldGuard.getInstance();
        RegionContainer container = wg.getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(chunk.getWorld()));


        String regionId = "clan_" + clanName + "_" + chunk.getWorld().getName() + "_" + chunk.getX() + "_" + chunk.getZ();

        BlockVector3 min = BlockVector3.at(chunk.getX() << 4, 0, chunk.getZ() << 4);
        BlockVector3 max = BlockVector3.at((chunk.getX() << 4) + 15, chunk.getWorld().getMaxHeight(), (chunk.getZ() << 4) + 15);
        ProtectedCuboidRegion region = new ProtectedCuboidRegion(regionId, min, max);


        region.setFlag(Flags.INTERACT, StateFlag.State.ALLOW);
        region.setFlag(Flags.PVP, StateFlag.State.ALLOW);
        region.setFlag(Flags.TNT, StateFlag.State.ALLOW);
        region.setFlag(Flags.CREEPER_EXPLOSION, StateFlag.State.DENY);
        region.setFlag(Flags.MOB_SPAWNING, StateFlag.State.DENY);

        if (regions != null) {
            regions.addRegion(region);
            addMembers(clan, region);
        }
    }

    public void addMembers(Clan clan, ProtectedRegion region) {
        for (String member : clan.getMembers()) {
            String playerRole = clan.getRole(member);
            Player player = Bukkit.getPlayerExact(member);
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(member);


            if (player != null && (playerRole.equals("Лидер") || playerRole.equals("Заместитель"))) {

                if (!region.getOwners().contains(player.getUniqueId())) {

                    DefaultDomain owners = region.getOwners();


                    owners.addPlayer(player.getUniqueId());


                    region.setOwners(owners);
                }
            } if (player != null &&  playerRole.equals("Воин")) {

                if (!region.getMembers().contains(player.getUniqueId())) {

                    DefaultDomain members = region.getMembers();


                    members.addPlayer(player.getUniqueId());


                    region.setMembers(members);
                }
            } if (offlinePlayer != null && (playerRole.equals("Лидер") || playerRole.equals("Заместитель"))) {

                if (!region.getOwners().contains(offlinePlayer.getUniqueId())) {

                    DefaultDomain owners = region.getOwners();


                    owners.addPlayer(offlinePlayer.getUniqueId());


                    region.setOwners(owners);
                }
            } if (offlinePlayer != null && playerRole.equals("Воин")) {

                if (!region.getMembers().contains(offlinePlayer.getUniqueId())) {

                    DefaultDomain members = region.getMembers();


                    members.addPlayer(offlinePlayer.getUniqueId());


                    region.setMembers(members);
                }
            }
        }
    }

    private boolean isShrineChunk(Chunk chunk) {
        World world = chunk.getWorld();
        WorldGuard wg = WorldGuard.getInstance();
        RegionContainer container = wg.getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(world));

        if (regions == null) {
            return false;
        }


        for (ProtectedRegion region : regions.getRegions().values()) {

            if (region.getId().startsWith("shrine_")) {
                BlockVector3 min = region.getMinimumPoint();
                BlockVector3 max = region.getMaximumPoint();


                int chunkMinX = chunk.getX() << 4;
                int chunkMinZ = chunk.getZ() << 4;
                int chunkMaxX = chunkMinX + 15;
                int chunkMaxZ = chunkMinZ + 15;

                if (min.getX() <= chunkMaxX && max.getX() >= chunkMinX &&
                        min.getZ() <= chunkMaxZ && max.getZ() >= chunkMinZ) {
                    return true;
                }
            }
        }
        return false;
    }
}
