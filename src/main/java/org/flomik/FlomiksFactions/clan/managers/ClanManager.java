package org.flomik.FlomiksFactions.clan.managers;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.flomik.FlomiksFactions.FlomiksFactions;
import org.flomik.FlomiksFactions.clan.Clan;
import org.flomik.FlomiksFactions.database.ClanDao;
import org.flomik.FlomiksFactions.database.InvitationDao;
import org.flomik.FlomiksFactions.player.PlayerDataHandler;

import java.util.*;

public class ClanManager {
    private final ClanDao clanDao;
    private final InvitationDao invitationDao;
    public Map<String, Clan> clans = new HashMap<>();

    public ClanManager(FlomiksFactions plugin, ClanDao clanDao, InvitationDao invitationDao) {
        this.clanDao = clanDao;
        this.invitationDao = invitationDao;
        loadClans();
    }

    public InvitationDao getInvitationDao() {
        return invitationDao;
    }

    public ClanDao getClanDao() {
        return clanDao;
    }

    public void addPlayerToClanRegionsAsMember(Player player, Clan clan) {
        UUID newMemberUUID = player.getUniqueId();
        String clanLeaderName = clan.getOwner();
        UUID clanLeaderUUID = Bukkit.getOfflinePlayer(clanLeaderName).getUniqueId();
        for (World world : Bukkit.getWorlds()) {
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionManager regionManager = container.get(BukkitAdapter.adapt(world));
            if (regionManager != null) {
                for (ProtectedRegion region : regionManager.getRegions().values()) {
                    if (region.getOwners().contains(clanLeaderUUID)) {
                        region.getMembers().addPlayer(newMemberUUID);
                    }
                }
            }
        }
    }

    public void addPlayerToClanRegionsAsOwner(Player player, Clan clan) {
        UUID newMemberUUID = player.getUniqueId();
        String clanLeaderName = clan.getOwner();
        UUID clanLeaderUUID = Bukkit.getOfflinePlayer(clanLeaderName).getUniqueId();
        for (World world : Bukkit.getWorlds()) {
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionManager regionManager = container.get(BukkitAdapter.adapt(world));
            if (regionManager != null) {
                for (ProtectedRegion region : regionManager.getRegions().values()) {
                    if (region.getOwners().contains(clanLeaderUUID)) {
                        region.getOwners().addPlayer(newMemberUUID);
                    }
                }
            }
        }
    }

    public void removePlayerFromClanRegions(Player playerToRemove, Clan clan) {
        UUID playerToRemoveUUID = playerToRemove.getUniqueId();
        String clanLeaderName = clan.getOwner();
        UUID clanOfflineLeaderUUID = Bukkit.getOfflinePlayer(clanLeaderName).getUniqueId();
        UUID clanLeaderUUID = clanOfflineLeaderUUID;
        Player leaderOnline = Bukkit.getPlayerExact(clanLeaderName);
        if (leaderOnline != null) {
            clanLeaderUUID = leaderOnline.getUniqueId();
        }

        for (World world : Bukkit.getWorlds()) {
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionManager regionManager = container.get(BukkitAdapter.adapt(world));
            if (regionManager != null) {
                for (ProtectedRegion region : regionManager.getRegions().values()) {
                    if (region.getOwners().contains(clanLeaderUUID) || region.getOwners().contains(clanOfflineLeaderUUID)) {
                        if (region.getOwners().contains(playerToRemoveUUID)) {
                            region.getOwners().removePlayer(playerToRemoveUUID);
                        }
                        if (region.getMembers().contains(playerToRemoveUUID)) {
                            region.getMembers().removePlayer(playerToRemoveUUID);
                        }
                    }
                }
            }
        }
    }

    public void createClan(String name, String owner) {
        if (getClan(name) != null) {
            throw new IllegalArgumentException("Клан с таким названием уже существует.");
        }

        if (getPlayerClan(owner) != null) {
            throw new IllegalArgumentException("Вы уже участник клана.");
        }

        Date creationDate = new Date();
        String description = "";
        List<String> alliances = new ArrayList<>();
        int level = 0;
        int clanXp = 0;
        int land = 0;
        int strength = 0;
        int maxPower = 10;
        double balance = 0;
        Set<String> members = new HashSet<>();
        Map<String, String> memberRoles = new HashMap<>();
        List<String> claimedChunks = new ArrayList<>();
        members.add(owner);
        memberRoles.put(owner, "Лидер");

        Clan clan = new Clan(name, owner, members, memberRoles, creationDate, description, alliances, level, clanXp, balance, land, strength, maxPower, claimedChunks);
        clans.put(name.toLowerCase(), clan);
        clanDao.insertClan(clan);
    }

    public Clan getClan(String name) {
        return clans.get(name.toLowerCase());
    }

    public Clan getPlayerClan(String playerName) {
        for (Clan clan : clans.values()) {
            if (clan.getMembers().contains(playerName)) {
                return clan;
            }
        }
        return null;
    }

    public Clan getClanByChunk(Chunk chunk) {
        String chunkKey = chunk.getWorld().getName() + ":" + chunk.getX() + ":" + chunk.getZ();

        for (Clan c : clans.values()) {
            if (c.getRegionNames().contains(chunkKey)) {
                return c;
            }
        }
        return null;
    }

    public boolean isEnemyTerritory(Player player, Block block) {
        Clan blockClan = getClanByChunk(block.getChunk());
        if (blockClan == null) {
            return false;
        }

        Clan playerClan = getPlayerClan(player.getName());
        if (playerClan == null) {
            return true;
        }

        if (playerClan.equals(blockClan)) {
            return false;
        }

        boolean isAllied = (playerClan.getAlliances() != null && playerClan.getAlliances().contains(blockClan.getName())) ||
                (blockClan.getAlliances() != null && blockClan.getAlliances().contains(playerClan.getName()));

        return !isAllied;
    }

    public boolean isEnemyPlayers(Player p1, Player p2) {
        Clan c1 = getPlayerClan(p1.getName());
        Clan c2 = getPlayerClan(p2.getName());

        if (c1 == null && c2 == null) {
            return false;
        }

        if (c1 == null || c2 == null) {
            return true;
        }

        if (c1.equals(c2)) {
            return false;
        }

        if (c1.getAlliances().contains(c2.getName())
                || c2.getAlliances().contains(c1.getName())) {
            return false;
        }

    /*
      5) (Дополнительно, если нужно искать «глубинные» союзы)
         Проверяем, нет ли у c1 в списке allied-кланов,
         в которых состоит p2. Т. е. c1 дружит с некоторым allyClan,
         а allyClan == c2 или в allyClan есть p2.

         Примерно так (если хотите расширенную логику):
    */
        // for (String allyName : c1.getAlliances()) {
        //     Clan allyClan = getClan(allyName);
        //     if (allyClan != null) {
        //         // Если allyClan == c2 => уже covered выше, но можно повторить
        //         // Или если allyClan.getMembers().contains(p2.getName()) => значит p2 состоит в союзном клане
        //         if (allyClan.getMembers().contains(p2.getName())) {
        //             return false;
        //         }
        //     }
        // }
        //
        // Аналогично для c2: проверить все его союзные кланы, не содержит ли там p1.

        // Если ни одна проверка не говорит «друзья» — значит враги
        return true;
    }

    public void sendClanMessage(Clan clan, String message) {
        for (String member : clan.getMembers()) {
            Player clanMember = Bukkit.getPlayer(member);
            if (clanMember != null && clanMember.isOnline()) {
                clanMember.sendMessage(message);
            }
        }
    }

    public void invitePlayer(String clanName, String playerName) {
        Clan clan = getClan(clanName);
        if (clan == null) {
            throw new IllegalArgumentException("Клан не существует.");
        }
        if (clan.getMembers().contains(playerName)) {
            throw new IllegalArgumentException("Игрок уже состоит в этом клане.");
        }
        if (clan.isFull()) {
            throw new IllegalArgumentException("Клан уже достиг максимального количества участников.");
        }
        Player player = Bukkit.getPlayer(playerName);
        if (player == null || !player.isOnline()) {
            throw new IllegalArgumentException("Игрок не онлайн.");
        }

        Set<String> playerInvitations = invitationDao.getInvitationsForPlayer(playerName);
        if (!playerInvitations.contains(clanName.toLowerCase())) {
            invitationDao.saveInvitation(playerName, clanName.toLowerCase());
        }
    }

    public Clan getClanByPlayer(String playerName) {
        return getPlayerClan(playerName);
    }

    public void updateStrengthForPlayer(String playerName, PlayerDataHandler playerDataHandler) {
        Clan clan = getClanByPlayer(playerName);
        if (clan != null) {
            clan.updateStrength(playerDataHandler);
            saveClan(clan);
        }
    }

    public void disbandClan(String clanName) {
        Clan clan = getClan(clanName);
        if (clan == null) {
            throw new IllegalArgumentException("Клан не существует.");
        }
        clans.remove(clanName.toLowerCase());
        clanDao.deleteClan(clanName);
    }

    public void joinClan(String clanName, String playerName) {
        Clan clan = getClan(clanName);
        if (clan == null) {
            throw new IllegalArgumentException("Клан не существует.");
        }

        Clan playerClan = getPlayerClan(playerName);
        if (playerClan != null && playerClan.getName().equalsIgnoreCase(clan.getName())) {
            throw new IllegalArgumentException("Вы уже состоите в этом клане.");
        }

        Set<String> playerInvitations = invitationDao.getInvitationsForPlayer(playerName);
        if (!playerInvitations.contains(clanName.toLowerCase())) {
            throw new IllegalArgumentException("Вы не получили приглашение в этот клан.");
        }

        invitationDao.removeInvitation(playerName, clanName.toLowerCase());

        if (playerClan != null) {
            playerClan.removeMember(playerName);
            saveClan(playerClan);
        }

        if (clan.isFull()) {
            throw new IllegalArgumentException("Клан уже достиг максимального количества участников.");
        }

        clan.addMember(playerName);
        saveClan(clan);
    }

    public Collection<Clan> getAllClans() {
        return clans.values();
    }

    public void updateClan(Clan clan) {
        String oldName = clan.getOldName();
        if (oldName != null && !oldName.equalsIgnoreCase(clan.getName())) {
            clans.remove(oldName.toLowerCase());
            clanDao.deleteClan(oldName);
        }
        clans.put(clan.getName().toLowerCase(), clan);
        saveClan(clan);
        clan.resetOldName();
    }

    public Map<String, Clan> getClans() {
        return clans;
    }

    public void saveClan(Clan clan) {
        // Проверяем, есть ли клан с таким именем в кэше
        Clan existing = getClan(clan.getName());
        if (existing == null) {
            // Значит нужно вставить
            clanDao.insertClan(clan);
        } else {
            clanDao.updateClan(clan);
        }
    }

    public void loadClans() {
        List<Clan> loadedClans = clanDao.getAllClans();
        clans.clear();
        for (Clan c : loadedClans) {
            clans.put(c.getName().toLowerCase(), c);
        }
    }

    public void leaveClan(String playerName) {
        Clan playerClan = getPlayerClan(playerName);
        if (playerClan == null) {
            throw new IllegalArgumentException("Вы не состоите в каком-либо клане.");
        }

        if (playerClan.getOwner().equals(playerName)) {
            if (playerClan.getMembers().size() > 1) {
                throw new IllegalArgumentException("Лидер клана не может покинуть клан, пока в нем есть другие участники. Передайте руководство или распустите клан.");
            } else {
                disbandClan(playerClan.getName());
            }
        } else {
            playerClan.removeMember(playerName);
            saveClan(playerClan);
        }
    }

    public void saveAllClans() {
        for (Clan clan : clans.values()) {
            saveClan(clan);
        }
    }
}
