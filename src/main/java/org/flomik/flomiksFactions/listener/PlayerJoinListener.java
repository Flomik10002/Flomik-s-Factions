package org.flomik.flomiksFactions.listener;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.flomik.flomiksFactions.FlomiksFactions;
import org.flomik.flomiksFactions.clan.Clan;
import org.flomik.flomiksFactions.clan.ClanManager;
import org.flomik.flomiksFactions.player.PlayerDataHandler;

import java.time.LocalDate;
import java.util.Map;

public class PlayerJoinListener implements Listener {

    private final PlayerDataHandler playerDataHandler;
    private final ClanManager clanManager;

    public PlayerJoinListener(PlayerDataHandler playerDataHandler, ClanManager clanManager) {
        this.playerDataHandler = playerDataHandler;
        this.clanManager = clanManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String playerName = event.getPlayer().getName();

        if (!playerDataHandler.hasFirstJoinDate(playerName)) {
            LocalDate currentDate = LocalDate.now();
            playerDataHandler.setFirstJoinDate(playerName, currentDate);
        }

        updatePlayerStatistics(event.getPlayer().getName());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String playerName = event.getPlayer().getName();
        updatePlayerStatistics(playerName);
    }

    private void updatePlayerStatistics(String playerName) {
        int ticksPlayed = Bukkit.getPlayer(playerName).getStatistic(Statistic.PLAY_ONE_MINUTE);
        playerDataHandler.setPlayTime(playerName, ticksPlayed);
        int level = playerDataHandler.getPlayerLevel(playerName);
        int strength = playerDataHandler.getPlayerStrength(playerName);
        int maxStrength = playerDataHandler.getPlayerMaxStrength(playerName);
        playerDataHandler.savePlayerAttributes(playerName, level, strength, maxStrength);
        clanManager.updateStrengthForPlayer(playerName, playerDataHandler);
    }

    public void startPeriodicStatsUpdate(FlomiksFactions plugin) {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            for (org.bukkit.entity.Player player : Bukkit.getOnlinePlayers()) {
                updatePlayerStatistics(player.getName());
            }
            Map<String, Clan> clans = clanManager.getClans();
            for (Clan clan : clans.values()) {
                clan.updateLands();
            }
        }, 0L, 20L);
    }
}