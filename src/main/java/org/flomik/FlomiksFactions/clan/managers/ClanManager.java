package org.flomik.FlomiksFactions.clan.managers; //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression

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

public class ClanManager { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private final ClanDao clanDao; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private final InvitationDao invitationDao; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    public Map<String, Clan> clans = new HashMap<>(); //NOPMD - suppressed UseConcurrentHashMap - TODO explain reason for suppression //NOPMD - suppressed UseConcurrentHashMap - TODO explain reason for suppression //NOPMD - suppressed UseConcurrentHashMap - TODO explain reason for suppression

    public ClanManager(FlomiksFactions plugin, ClanDao clanDao, InvitationDao invitationDao) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        this.clanDao = clanDao;
        this.invitationDao = invitationDao;
        loadClans(); //NOPMD - suppressed ConstructorCallsOverridableMethod - TODO explain reason for suppression //NOPMD - suppressed ConstructorCallsOverridableMethod - TODO explain reason for suppression //NOPMD - suppressed ConstructorCallsOverridableMethod - TODO explain reason for suppression
    }

    public InvitationDao getInvitationDao() {
        return invitationDao;
    }

    public ClanDao getClanDao() {
        return clanDao;
    }

    public void addPlayerToClanRegionsAsMember(Player player, Clan clan) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        UUID newMemberUUID = player.getUniqueId(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        String clanLeaderName = clan.getOwner(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        UUID clanLeaderUUID = Bukkit.getOfflinePlayer(clanLeaderName).getUniqueId(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        for (World world : Bukkit.getWorlds()) { //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer(); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
            RegionManager regionManager = container.get(BukkitAdapter.adapt(world)); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            if (regionManager != null) {
                for (ProtectedRegion region : regionManager.getRegions().values()) { //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                    if (region.getOwners().contains(clanLeaderUUID)) {
                        region.getMembers().addPlayer(newMemberUUID);
                    }
                }
            }
        }
    }

    public void addPlayerToClanRegionsAsOwner(Player player, Clan clan) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        UUID newMemberUUID = player.getUniqueId(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        String clanLeaderName = clan.getOwner(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        UUID clanLeaderUUID = Bukkit.getOfflinePlayer(clanLeaderName).getUniqueId(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        for (World world : Bukkit.getWorlds()) { //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer(); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
            RegionManager regionManager = container.get(BukkitAdapter.adapt(world)); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            if (regionManager != null) {
                for (ProtectedRegion region : regionManager.getRegions().values()) { //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                    if (region.getOwners().contains(clanLeaderUUID)) {
                        region.getOwners().addPlayer(newMemberUUID);
                    }
                }
            }
        }
    }

    public void removePlayerFromClanRegions(Player playerToRemove, Clan clan) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        UUID playerToRemoveUUID = playerToRemove.getUniqueId(); //NOPMD - suppressed LongVariable - TODO explain reason for suppression //NOPMD - suppressed LongVariable - TODO explain reason for suppression //NOPMD - suppressed LongVariable - TODO explain reason for suppression
        String clanLeaderName = clan.getOwner(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        UUID clanOfflineLeaderUUID = Bukkit.getOfflinePlayer(clanLeaderName).getUniqueId(); //NOPMD - suppressed LongVariable - TODO explain reason for suppression //NOPMD - suppressed LongVariable - TODO explain reason for suppression //NOPMD - suppressed LongVariable - TODO explain reason for suppression
        UUID clanLeaderUUID = clanOfflineLeaderUUID;
        Player leaderOnline = Bukkit.getPlayerExact(clanLeaderName); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        if (leaderOnline != null) {
            clanLeaderUUID = leaderOnline.getUniqueId();
        }

        for (World world : Bukkit.getWorlds()) { //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer(); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
            RegionManager regionManager = container.get(BukkitAdapter.adapt(world)); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            if (regionManager != null) {
                for (ProtectedRegion region : regionManager.getRegions().values()) { //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                    if (region.getOwners().contains(clanLeaderUUID) || region.getOwners().contains(clanOfflineLeaderUUID)) {
                        if (region.getOwners().contains(playerToRemoveUUID)) { //NOPMD - suppressed AvoidDeeplyNestedIfStmts - TODO explain reason for suppression //NOPMD - suppressed AvoidDeeplyNestedIfStmts - TODO explain reason for suppression //NOPMD - suppressed AvoidDeeplyNestedIfStmts - TODO explain reason for suppression
                            region.getOwners().removePlayer(playerToRemoveUUID);
                        }
                        if (region.getMembers().contains(playerToRemoveUUID)) { //NOPMD - suppressed AvoidDeeplyNestedIfStmts - TODO explain reason for suppression //NOPMD - suppressed AvoidDeeplyNestedIfStmts - TODO explain reason for suppression //NOPMD - suppressed AvoidDeeplyNestedIfStmts - TODO explain reason for suppression
                            region.getMembers().removePlayer(playerToRemoveUUID);
                        }
                    }
                }
            }
        }
    }

    public void createClan(String name, String owner) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        if (getClan(name) != null) {
            throw new IllegalArgumentException("Клан с таким названием уже существует.");
        }

        if (getPlayerClan(owner) != null) {
            throw new IllegalArgumentException("Вы уже участник клана.");
        }

        Date creationDate = new Date(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        String description = ""; //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        List<String> alliances = new ArrayList<>(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        int level = 0; //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        int clanXp = 0; //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        int land = 0; //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        int strength = 0; //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        int maxPower = 10; //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        double balance = 0; //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        Set<String> members = new HashSet<>(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        Map<String, String> memberRoles = new HashMap<>(); //NOPMD - suppressed UseConcurrentHashMap - TODO explain reason for suppression //NOPMD - suppressed UseConcurrentHashMap - TODO explain reason for suppression //NOPMD - suppressed UseConcurrentHashMap - TODO explain reason for suppression
        List<String> claimedChunks = new ArrayList<>(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        members.add(owner);
        memberRoles.put(owner, "Лидер");

        Clan clan = new Clan(name, owner, members, memberRoles, creationDate, description, alliances, level, clanXp, balance, land, strength, maxPower, claimedChunks); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        clans.put(name.toLowerCase(), clan); //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression
        clanDao.insertClan(clan);
    }

    public Clan getClan(String name) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        return clans.get(name.toLowerCase()); //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression
    }

    public Clan getPlayerClan(String playerName) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        for (Clan clan : clans.values()) { //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            if (clan.getMembers().contains(playerName)) {
                return clan; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
            }
        }
        return null;
    }

    public Clan getClanByChunk(Chunk chunk) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        String chunkKey = chunk.getWorld().getName() + ":" + chunk.getX() + ":" + chunk.getZ(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression

        for (Clan c : clans.values()) { //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            if (c.getRegionNames().contains(chunkKey)) {
                return c; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
            }
        }
        return null;
    }

    public boolean isEnemyTerritory(Player player, Block block) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        Clan blockClan = getClanByChunk(block.getChunk()); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        if (blockClan == null) {
            return false; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
        }

        Clan playerClan = getPlayerClan(player.getName()); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        if (playerClan == null) {
            return true; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
        }

        if (playerClan.equals(blockClan)) {
            return false; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
        }

        boolean isAllied = (playerClan.getAlliances() != null && playerClan.getAlliances().contains(blockClan.getName())) || //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                (blockClan.getAlliances() != null && blockClan.getAlliances().contains(playerClan.getName()));

        return !isAllied;
    }

    public boolean isEnemyPlayers(Player p1, Player p2) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        Clan c1 = getPlayerClan(p1.getName()); //NOPMD - suppressed ShortVariable - TODO explain reason for suppression //NOPMD - suppressed ShortVariable - TODO explain reason for suppression //NOPMD - suppressed ShortVariable - TODO explain reason for suppression
        Clan c2 = getPlayerClan(p2.getName()); //NOPMD - suppressed ShortVariable - TODO explain reason for suppression //NOPMD - suppressed ShortVariable - TODO explain reason for suppression //NOPMD - suppressed ShortVariable - TODO explain reason for suppression

        if (c1 == null && c2 == null) {
            return false; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
        }

        if (c1 == null || c2 == null) {
            return true; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
        }

        if (c1.equals(c2)) {
            return false; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
        }

        if (c1.getAlliances().contains(c2.getName()) //NOPMD - suppressed SimplifyBooleanReturns - TODO explain reason for suppression //NOPMD - suppressed SimplifyBooleanReturns - TODO explain reason for suppression //NOPMD - suppressed SimplifyBooleanReturns - TODO explain reason for suppression
                || c2.getAlliances().contains(c1.getName())) {
            return false; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
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
        //         // Или если allyClan.getMembers().contains(p2.getName()) => значит p2 состоит в союзном клане //NOPMD - suppressed CommentSize - TODO explain reason for suppression //NOPMD - suppressed CommentSize - TODO explain reason for suppression //NOPMD - suppressed CommentSize - TODO explain reason for suppression
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

    public void sendClanMessage(Clan clan, String message) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        for (String member : clan.getMembers()) { //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            Player clanMember = Bukkit.getPlayer(member); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            if (clanMember != null && clanMember.isOnline()) {
                clanMember.sendMessage(message);
            }
        }
    }

    public void invitePlayer(String clanName, String playerName) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        Clan clan = getClan(clanName); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        if (clan == null) {
            throw new IllegalArgumentException("Клан не существует.");
        }
        if (clan.getMembers().contains(playerName)) {
            throw new IllegalArgumentException("Игрок уже состоит в этом клане.");
        }
        if (clan.isFull()) {
            throw new IllegalArgumentException("Клан уже достиг максимального количества участников.");
        }
        Player player = Bukkit.getPlayer(playerName); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        if (player == null || !player.isOnline()) {
            throw new IllegalArgumentException("Игрок не онлайн.");
        }

        Set<String> playerInvitations = invitationDao.getInvitationsForPlayer(playerName); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        if (!playerInvitations.contains(clanName.toLowerCase())) { //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression
            invitationDao.saveInvitation(playerName, clanName.toLowerCase()); //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression
        }
    }

    public Clan getClanByPlayer(String playerName) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        return getPlayerClan(playerName);
    }

    public void updateStrengthForPlayer(String playerName, PlayerDataHandler playerDataHandler) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        Clan clan = getClanByPlayer(playerName); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        if (clan != null) {
            clan.updateStrength(playerDataHandler);
            saveClan(clan);
        }
    }

    public void disbandClan(String clanName) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        Clan clan = getClan(clanName); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        if (clan == null) {
            throw new IllegalArgumentException("Клан не существует.");
        }
        clans.remove(clanName.toLowerCase()); //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression
        clanDao.deleteClan(clanName);
    }

    public void joinClan(String clanName, String playerName) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        Clan clan = getClan(clanName); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        if (clan == null) {
            throw new IllegalArgumentException("Клан не существует.");
        }

        Clan playerClan = getPlayerClan(playerName); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        if (playerClan != null && playerClan.getName().equalsIgnoreCase(clan.getName())) {
            throw new IllegalArgumentException("Вы уже состоите в этом клане.");
        }

        Set<String> playerInvitations = invitationDao.getInvitationsForPlayer(playerName); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        if (!playerInvitations.contains(clanName.toLowerCase())) { //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression
            throw new IllegalArgumentException("Вы не получили приглашение в этот клан.");
        }

        invitationDao.removeInvitation(playerName, clanName.toLowerCase()); //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression

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

    public Collection<Clan> getAllClans() { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        return clans.values();
    }

    public void updateClan(Clan clan) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        String oldName = clan.getOldName(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        if (oldName != null && !oldName.equalsIgnoreCase(clan.getName())) {
            clans.remove(oldName.toLowerCase()); //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression
            clanDao.deleteClan(oldName);
        }
        clans.put(clan.getName().toLowerCase(), clan); //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression
        saveClan(clan);
        clan.resetOldName();
    }

    public Map<String, Clan> getClans() {
        return clans;
    }

    public void saveClan(Clan clan) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        // Проверяем, есть ли клан с таким именем в кэше
        Clan existing = getClan(clan.getName()); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        if (existing == null) {
            // Значит нужно вставить
            clanDao.insertClan(clan);
        } else {
            clanDao.updateClan(clan);
        }
    }

    public void loadClans() { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        List<Clan> loadedClans = clanDao.getAllClans(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        clans.clear();
        for (Clan c : loadedClans) { //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            clans.put(c.getName().toLowerCase(), c); //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression
        }
    }

    public void leaveClan(String playerName) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        Clan playerClan = getPlayerClan(playerName); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        if (playerClan == null) {
            throw new IllegalArgumentException("Вы не состоите в каком-либо клане.");
        }

        if (playerClan.getOwner().equals(playerName)) {
            if (playerClan.getMembers().size() > 1) { //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression
                throw new IllegalArgumentException("Лидер клана не может покинуть клан, пока в нем есть другие участники. Передайте руководство или распустите клан.");
            } else {
                disbandClan(playerClan.getName());
            }
        } else {
            playerClan.removeMember(playerName);
            saveClan(playerClan);
        }
    }

    public void saveAllClans() { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        for (Clan clan : clans.values()) { //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            saveClan(clan);
        }
    }
}
