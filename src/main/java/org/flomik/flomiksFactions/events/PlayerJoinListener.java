package org.flomik.flomiksFactions.events;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.flomik.flomiksFactions.commands.player.PlayerDataHandler;

import java.time.LocalDate;

public class PlayerJoinListener implements Listener {

    private final PlayerDataHandler playerDataHandler;

    public PlayerJoinListener(PlayerDataHandler playerDataHandler) {
        this.playerDataHandler = playerDataHandler;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String playerName = event.getPlayer().getName();

        // Проверяем, есть ли дата первого захода у игрока
        if (!playerDataHandler.hasFirstJoinDate(playerName)) {
            // Если нет, сохраняем текущую дату
            LocalDate currentDate = LocalDate.now();
            playerDataHandler.setFirstJoinDate(playerName, currentDate);
        }

        // Обновляем статистику игрока при входе
        updatePlayerStatistics(event.getPlayer().getName());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String playerName = event.getPlayer().getName();

        // Получаем текущее время игры в тиках и сохраняем его
        int ticksPlayed = event.getPlayer().getStatistic(Statistic.PLAY_ONE_MINUTE);
        playerDataHandler.setPlayTime(playerName, ticksPlayed);

        // Сохраняем уровень, силу и максимальную силу игрока при выходе
        int level = playerDataHandler.getPlayerLevel(playerName);
        int strength = playerDataHandler.getPlayerStrength(playerName);
        int maxStrength = playerDataHandler.getPlayerMaxStrength(playerName);
        playerDataHandler.savePlayerAttributes(playerName, level, strength, maxStrength);
    }

    // Метод для обновления статистики игрока
    private void updatePlayerStatistics(String playerName) {
        // Получаем время игры в тиках и сохраняем его
        int ticksPlayed = Bukkit.getPlayer(playerName).getStatistic(Statistic.PLAY_ONE_MINUTE);
        playerDataHandler.setPlayTime(playerName, ticksPlayed);

        // Обновляем данные об уровне, силе и максимальной силе
        int level = playerDataHandler.getPlayerLevel(playerName);
        int strength = playerDataHandler.getPlayerStrength(playerName);
        int maxStrength = playerDataHandler.getPlayerMaxStrength(playerName);
        playerDataHandler.savePlayerAttributes(playerName, level, strength, maxStrength);
    }

    // Метод для запуска периодического обновления статистики всех игроков
    public void startPeriodicStatsUpdate(JavaPlugin plugin) {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            for (org.bukkit.entity.Player player : Bukkit.getOnlinePlayers()) {
                updatePlayerStatistics(player.getName());
            }
        }, 0L, 20L);
    }
}