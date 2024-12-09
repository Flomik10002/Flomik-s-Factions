package org.flomik.FlomiksFactions.listener;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.flomik.FlomiksFactions.FlomiksFactions;
import org.flomik.FlomiksFactions.player.PlayerDataHandler;

public class StrengthTickTask {

    private final PlayerDataHandler playerDataHandler;

    public StrengthTickTask(PlayerDataHandler playerDataHandler) {
        this.playerDataHandler = playerDataHandler;
    }

    public void addStrength(FlomiksFactions plugin) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (org.bukkit.entity.Player player : Bukkit.getOnlinePlayers()) {
                    String playerName = player.getName();


                    int currentStrength = playerDataHandler.getPlayerStrength(playerName);
                    if (currentStrength < 10) {
                        int newStrength = currentStrength + 1;
                        playerDataHandler.savePlayerAttributes(playerName, playerDataHandler.getPlayerLevel(playerName), newStrength, playerDataHandler.getPlayerMaxStrength(playerName));
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L * 60 * 15);
    }
}
