package org.flomik.FlomiksFactions.player;

import org.bukkit.Color;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.flomik.FlomiksFactions.FlomiksFactions;
import org.flomik.FlomiksFactions.databases.PlayerDataDao;
import org.flomik.FlomiksFactions.databases.PlayerDatabaseManager;

import java.time.LocalDate;
import java.util.Set;

public class PlayerDataHandler {
    private final FlomiksFactions plugin;
    private final PlayerDatabaseManager playerDatabaseManager;
    private final PlayerDataDao playerDataDao;

    public PlayerDataHandler(FlomiksFactions plugin) {
        this.plugin = plugin;

        this.playerDatabaseManager = new PlayerDatabaseManager();
        playerDatabaseManager.initDatabase(plugin);
        playerDatabaseManager.createTables();

        this.playerDataDao = new PlayerDataDao(playerDatabaseManager);
    }

    public Set<String> getPurchasedColors(String playerName) {
        return playerDataDao.getPurchasedColors(playerName);
    }

    public void setPlayerEffectColor(String playerName, Color color) {
        playerDataDao.setPlayerEffectColor(playerName, color);
    }

    public void addPurchasedColor(String playerName, String colorName) {
        playerDataDao.addPurchasedColor(playerName, colorName);
    }

    public Color getPlayerEffectColor(String playerName) {
        return playerDataDao.getPlayerEffectColor(playerName);
    }

    public boolean hasPurchasedParticles(String playerName) {
        return playerDataDao.hasPurchasedParticles(playerName);
    }

    public boolean hasFirstJoinDate(String playerName) {
        return playerDataDao.hasFirstJoinDate(playerName);
    }

    public void setFirstJoinDate(String playerName, LocalDate date) {
        playerDataDao.setFirstJoinDate(playerName, date);
    }

    public boolean hasPlayerData(String playerName) {
        return playerDataDao.hasPlayerData(playerName);
    }

    public int getDeaths(Player player) {
        return player.getStatistic(Statistic.DEATHS);
    }

    public int getKills(Player player) {
        return player.getStatistic(Statistic.PLAYER_KILLS);
    }

    public void savePlayerAttributes(String playerName, int level, int strength, int maxStrength) {
        playerDataDao.savePlayerAttributes(playerName, level, strength, maxStrength);
    }

    public String getFirstJoinDate(String playerName) {
        return playerDataDao.getFirstJoinDate(playerName);
    }

    public int getPlayerLevel(String playerName) {
        return playerDataDao.getPlayerLevel(playerName);
    }

    public void setPlayerLevel(String playerName, int level) {
        playerDataDao.setPlayerLevel(playerName, level);
    }

    public int getPlayerStrength(String playerName) {
        return playerDataDao.getPlayerStrength(playerName);
    }

    public void setPlayerStrength(String playerName, int strength) {
        playerDataDao.setPlayerStrength(playerName, strength);
    }

    public void addPlayerStrength(String playerName, int strength) {
        playerDataDao.addPlayerStrength(playerName, strength);
    }

    public int getPlayerMaxStrength(String playerName) {
        return playerDataDao.getPlayerMaxStrength(playerName);
    }

    public void setPlayTime(String playerName, int ticksPlayed) {
        playerDataDao.setPlayTime(playerName, ticksPlayed);
    }

    public int getPlayTime(String playerName) {
        return playerDataDao.getPlayTime(playerName);
    }
}
