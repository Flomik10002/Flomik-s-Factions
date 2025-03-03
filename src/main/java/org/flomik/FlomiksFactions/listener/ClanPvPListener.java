package org.flomik.FlomiksFactions.listener; //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.flomik.FlomiksFactions.clan.Clan;
import org.flomik.FlomiksFactions.clan.managers.ClanManager;

public class ClanPvPListener implements Listener { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression

    private final ClanManager clanManager; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression

    public ClanPvPListener(JavaPlugin plugin, ClanManager clanManager) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        this.clanManager = clanManager;
        plugin.getServer().getPluginManager().registerEvents(this, plugin); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
    }

    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent event) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression

        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player attacker = (Player) event.getDamager(); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
            Player defender = (Player) event.getEntity(); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression


            Clan attackerClan = clanManager.getPlayerClan(attacker.getName()); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            Clan defenderClan = clanManager.getPlayerClan(defender.getName()); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression

            if (attackerClan != null && defenderClan != null) {
                if (attackerClan.equals(defenderClan)) { //NOPMD - suppressed AvoidDeeplyNestedIfStmts - TODO explain reason for suppression //NOPMD - suppressed AvoidDeeplyNestedIfStmts - TODO explain reason for suppression //NOPMD - suppressed AvoidDeeplyNestedIfStmts - TODO explain reason for suppression
                    event.setCancelled(true);
                    attacker.sendMessage(ChatColor.RED + "Вы состоите в одном клане с целью!");
                }

                if (attackerClan.getAlliances().contains(defenderClan.getName()) || //NOPMD - suppressed AvoidDeeplyNestedIfStmts - TODO explain reason for suppression //NOPMD - suppressed AvoidDeeplyNestedIfStmts - TODO explain reason for suppression //NOPMD - suppressed AvoidDeeplyNestedIfStmts - TODO explain reason for suppression
                        defenderClan.getAlliances().contains(attackerClan.getName())) {
                    event.setCancelled(true);
                    attacker.sendMessage(ChatColor.RED + "Вы состоите в альянсе с целью!");
                }
            }
        }
    }
}
