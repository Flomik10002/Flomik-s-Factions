package org.flomik.FlomiksFactions.clan.commands.handlers.playerInteractions; //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.flomik.FlomiksFactions.clan.Clan;
import org.flomik.FlomiksFactions.clan.managers.ClanManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class LeaveHandler { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression

    private final ClanManager clanManager; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private final ConcurrentHashMap<String, Long> pendingDisbands; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression

    public LeaveHandler(ClanManager clanManager, ConcurrentHashMap<String, Long> pendingDisbands) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        this.clanManager = clanManager;
        this.pendingDisbands = pendingDisbands;
    }

    public boolean handleCommand(Player player) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        Clan clan = clanManager.getPlayerClan(player.getName()); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        if (clan == null) {
            player.sendMessage(ChatColor.RED + "Вы не состоите в клане.");
            return true; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
        }

        if (clan.getOwner().equals(player.getName())) {

            if (clan.getMembers().size() > 1) { //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression
                player.sendMessage(ChatColor.RED + "Лидер клана не может покинуть клан, пока в нем есть другие участники. Передайте руководство или распустите клан.");
                return true; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
            } else {

                if (pendingDisbands.containsKey(player.getName())) {

                    try {
                        for (Clan otherClan : clanManager.getAllClans()) { //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                            if (otherClan.getAlliances().contains(clan.getName())) {
                                otherClan.removeAlliance(clan);
                                clanManager.updateClan(otherClan);
                            }
                        }
                        clanManager.disbandClan(clan.getName());
                        pendingDisbands.remove(player.getName());
                        Bukkit.broadcastMessage(ChatColor.GREEN + "Клан " + ChatColor.YELLOW + clan.getName() + ChatColor.GREEN +" был успешно распущен.");

                        com.sk89q.worldedit.entity.Player wgPlayer = BukkitAdapter.adapt(player); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                        WorldGuard wg = WorldGuard.getInstance(); //NOPMD - suppressed ShortVariable - TODO explain reason for suppression //NOPMD - suppressed ShortVariable - TODO explain reason for suppression //NOPMD - suppressed ShortVariable - TODO explain reason for suppression
                        RegionContainer container = wg.getPlatform().getRegionContainer(); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
                        RegionManager regionManager = container.get(BukkitAdapter.adapt(player.getWorld())); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression

                        if (regionManager == null) {
                            return true; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
                        }

                        List<String> regionsToRemove = new ArrayList<>(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression

                        for (ProtectedRegion region : regionManager.getRegions().values()) { //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                            if (region.getOwners().contains(wgPlayer.getUniqueId())) {
                                regionsToRemove.add(region.getId());
                            }
                        }

                        for (String regionId : regionsToRemove) { //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                            regionManager.removeRegion(regionId);
                        }
                    } catch (IllegalArgumentException e) {
                        player.sendMessage(ChatColor.RED + e.getMessage());
                    }
                } else {

                    pendingDisbands.put(player.getName(), System.currentTimeMillis());
                    player.sendMessage(ChatColor.YELLOW + "Вы действительно хотите распустить клан? Повторите команду в течении 15 секунд для подтверждения.");
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (pendingDisbands.containsKey(player.getName())) {
                                player.sendMessage(ChatColor.RED + "Время для подтверждения истекло.");
                                pendingDisbands.remove(player.getName());
                            }
                        }
                    }.runTaskLater(Bukkit.getPluginManager().getPlugin("FlomiksFactions"), 300L);
                }
            }
        } else {

            try {
                clanManager.leaveClan(player.getName());
                clanManager.removePlayerFromClanRegions(player, clan);
                clanManager.sendClanMessage(clan, ChatColor.YELLOW + player.getName() + ChatColor.GREEN + " покидает ваш клан.");
                player.sendMessage(ChatColor.GREEN + "Вы успешно покинули клан " + ChatColor.YELLOW + clan.getName() + ChatColor.GREEN +".");
            } catch (IllegalArgumentException e) {
                player.sendMessage(ChatColor.RED + e.getMessage());
            }
        }
        return true;
    }
}
