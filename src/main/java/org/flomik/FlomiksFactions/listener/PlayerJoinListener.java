package org.flomik.FlomiksFactions.listener;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.flomik.FlomiksFactions.FlomiksFactions;
import org.flomik.FlomiksFactions.clan.Clan;
import org.flomik.FlomiksFactions.clan.ClanManager;
import org.flomik.FlomiksFactions.player.PlayerDataHandler;
import org.flomik.FlomiksFactions.worldEvents.randomEvents.RandomEvent;

import java.time.LocalDate;
import java.util.Map;

public class PlayerJoinListener implements Listener {

    private final PlayerDataHandler playerDataHandler;
    private final ClanManager clanManager;
    private final FlomiksFactions plugin;

    public PlayerJoinListener(FlomiksFactions plugin, PlayerDataHandler playerDataHandler, ClanManager clanManager) {
        this.playerDataHandler = playerDataHandler;
        this.clanManager = clanManager;
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String playerName = event.getPlayer().getName();

        if (!playerDataHandler.hasFirstJoinDate(playerName)) {
            LocalDate currentDate = LocalDate.now();
            playerDataHandler.setFirstJoinDate(playerName, currentDate);
        }

        updatePlayerStatistics(event.getPlayer().getName());

        if (plugin.getEventManager().isRunning()) {
            RandomEvent currentEvent = plugin.getEventManager().getCurrentEvent();
            if (currentEvent.getBossBar() != null) {
                currentEvent.getBossBar().addPlayer(event.getPlayer());
            }
        }
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