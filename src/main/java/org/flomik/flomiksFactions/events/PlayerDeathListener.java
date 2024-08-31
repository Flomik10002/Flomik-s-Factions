package org.flomik.flomiksFactions.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.flomik.flomiksFactions.FlomiksFactions;
import org.flomik.flomiksFactions.commands.clan.Clan;
import org.flomik.flomiksFactions.commands.player.PlayerDataHandler;

public class PlayerDeathListener implements Listener {

    private final PlayerDataHandler playerDataHandler;

    public PlayerDeathListener(PlayerDataHandler playerDataHandler) {
        this.playerDataHandler = playerDataHandler;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        String playerName = event.getEntity().getName();

        // Получаем текущую силу игрока
        int currentStrength = playerDataHandler.getPlayerStrength(playerName);

        // Уменьшаем силу на 4
        int newStrength = Math.max(currentStrength - 4, -5); // Убедитесь, что сила не становится < -5

        // Сохраняем обновленную силу
        playerDataHandler.savePlayerAttributes(playerName, playerDataHandler.getPlayerLevel(playerName), newStrength, playerDataHandler.getPlayerMaxStrength(playerName));
    }
}
