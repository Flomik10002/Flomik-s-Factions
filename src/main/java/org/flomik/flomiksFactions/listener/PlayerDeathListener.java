package org.flomik.flomiksFactions.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.flomik.flomiksFactions.player.PlayerDataHandler;

public class PlayerDeathListener implements Listener {

    private final PlayerDataHandler playerDataHandler;

    public PlayerDeathListener(PlayerDataHandler playerDataHandler) {
        this.playerDataHandler = playerDataHandler;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        String playerName = event.getEntity().getName();
        int currentStrength = playerDataHandler.getPlayerStrength(playerName);
        int newStrength = Math.max(currentStrength - 4, -5);
        playerDataHandler.savePlayerAttributes(playerName, playerDataHandler.getPlayerLevel(playerName), newStrength, playerDataHandler.getPlayerMaxStrength(playerName));
    }
}
