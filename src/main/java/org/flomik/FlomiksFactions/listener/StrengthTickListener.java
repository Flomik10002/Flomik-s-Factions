package org.flomik.FlomiksFactions.listener; //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.flomik.FlomiksFactions.FlomiksFactions;
import org.flomik.FlomiksFactions.player.PlayerDataHandler;

public class StrengthTickListener { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression

    private final PlayerDataHandler playerDataHandler; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression

    public StrengthTickListener(PlayerDataHandler playerDataHandler) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        this.playerDataHandler = playerDataHandler;
    }

    public void addStrength(FlomiksFactions plugin) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        new BukkitRunnable() {
            @Override
            public void run() {
                for (org.bukkit.entity.Player player : Bukkit.getOnlinePlayers()) { //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                    String playerName = player.getName(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression


                    int currentStrength = playerDataHandler.getPlayerStrength(playerName); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                    if (currentStrength < 10) { //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression
                        int newStrength = currentStrength + 1; //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                        playerDataHandler.savePlayerAttributes(playerName, playerDataHandler.getPlayerLevel(playerName), newStrength, playerDataHandler.getPlayerMaxStrength(playerName));
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L * 60 * 15);
    }
}
