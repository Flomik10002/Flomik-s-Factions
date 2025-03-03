package org.flomik.FlomiksFactions.player; //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression

import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.flomik.FlomiksFactions.FlomiksFactions;
import org.flomik.FlomiksFactions.database.PlayerDataDao;
import org.flomik.FlomiksFactions.database.PlayerDatabaseManager;

import java.time.LocalDate;

public class PlayerDataHandler { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private final FlomiksFactions plugin; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private final PlayerDatabaseManager playerDatabaseManager; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private final PlayerDataDao playerDataDao; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression

    public PlayerDataHandler(FlomiksFactions plugin) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        this.plugin = plugin;

        this.playerDatabaseManager = new PlayerDatabaseManager();
        playerDatabaseManager.initDatabase(plugin);
        playerDatabaseManager.createTables();

        this.playerDataDao = new PlayerDataDao(playerDatabaseManager);
    }

    public boolean hasFirstJoinDate(String playerName) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        return playerDataDao.hasFirstJoinDate(playerName);
    }

    public void setFirstJoinDate(String playerName, LocalDate date) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        playerDataDao.setFirstJoinDate(playerName, date);
    }

    public boolean hasPlayerData(String playerName) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        return playerDataDao.hasPlayerData(playerName);
    }

    public int getDeaths(Player player) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        return player.getStatistic(Statistic.DEATHS);
    }

    public int getKills(Player player) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        return player.getStatistic(Statistic.PLAYER_KILLS);
    }

    public void savePlayerAttributes(String playerName, int level, int strength, int maxStrength) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        playerDataDao.savePlayerAttributes(playerName, level, strength, maxStrength);
    }

    public String getFirstJoinDate(String playerName) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        return playerDataDao.getFirstJoinDate(playerName);
    }

    public int getPlayerLevel(String playerName) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        return playerDataDao.getPlayerLevel(playerName);
    }

    public void setPlayerLevel(String playerName, int level) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        playerDataDao.setPlayerLevel(playerName, level);
    }

    public int getPlayerStrength(String playerName) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        return playerDataDao.getPlayerStrength(playerName);
    }

    public void setPlayerStrength(String playerName, int strength) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        playerDataDao.setPlayerStrength(playerName, strength);
    }

    public void addPlayerStrength(String playerName, int strength) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        playerDataDao.addPlayerStrength(playerName, strength);
    }

    public int getPlayerMaxStrength(String playerName) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        return playerDataDao.getPlayerMaxStrength(playerName);
    }

    public void setPlayTime(String playerName, int ticksPlayed) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        playerDataDao.setPlayTime(playerName, ticksPlayed);
    }

    public int getPlayTime(String playerName) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        return playerDataDao.getPlayTime(playerName);
    }
}
