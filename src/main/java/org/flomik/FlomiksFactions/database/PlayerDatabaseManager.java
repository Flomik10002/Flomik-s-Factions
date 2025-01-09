package org.flomik.FlomiksFactions.database;

import org.bukkit.plugin.Plugin;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class PlayerDatabaseManager {
    private String url;

    public void initDatabase(Plugin plugin) {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        File dbFile = new File(plugin.getDataFolder(), "players.db");
        url = "jdbc:sqlite:" + dbFile.getAbsolutePath();
    }

    public void createTables() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS player_data (" +
                    "player_name TEXT PRIMARY KEY," +
                    "firstJoinDate TEXT," +
                    "level INT DEFAULT 1," +
                    "strength INT DEFAULT 0," +
                    "maxStrength INT DEFAULT 10," +
                    "playTime INT DEFAULT 0" +
                    ")");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url);
    }

    public void close() {
    }
}