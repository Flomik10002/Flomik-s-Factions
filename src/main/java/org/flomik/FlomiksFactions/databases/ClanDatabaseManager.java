package org.flomik.FlomiksFactions.databases;

import org.bukkit.plugin.Plugin;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ClanDatabaseManager {
    private String url;

    public void initDatabase(Plugin plugin) {
        File dbFile = new File(plugin.getDataFolder(), "clans.db");
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        url = "jdbc:sqlite:" + dbFile.getAbsolutePath();
    }

    public void createTables() {
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS clans (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT UNIQUE NOT NULL," +
                    "owner TEXT NOT NULL," +
                    "creation_date BIGINT NOT NULL," +
                    "description TEXT," +
                    "land INT," +
                    "strength INT," +
                    "level INT," +
                    "clan_xp INT," +
                    "max_power INT," +
                    "home_world TEXT," +
                    "home_x DOUBLE," +
                    "home_y DOUBLE," +
                    "home_z DOUBLE," +
                    "home_yaw FLOAT," +
                    "home_pitch FLOAT" +
                    ")");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS clan_members (" +
                    "clan_id INT NOT NULL," +
                    "member_name TEXT NOT NULL," +
                    "role TEXT NOT NULL," +
                    "PRIMARY KEY (clan_id, member_name)," +
                    "FOREIGN KEY (clan_id) REFERENCES clans(id) ON DELETE CASCADE" +
                    ")");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS clan_alliances (" +
                    "clan_id INT NOT NULL," +
                    "alliance TEXT NOT NULL," +
                    "PRIMARY KEY (clan_id, alliance)," +
                    "FOREIGN KEY (clan_id) REFERENCES clans(id) ON DELETE CASCADE" +
                    ")");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS clan_chunks (" +
                    "clan_id INT NOT NULL," +
                    "chunk_name TEXT NOT NULL," +
                    "PRIMARY KEY (clan_id, chunk_name)," +
                    "FOREIGN KEY (clan_id) REFERENCES clans(id) ON DELETE CASCADE" +
                    ")");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS invitations (" +
                    "player_name TEXT NOT NULL," +
                    "clan_name TEXT NOT NULL" +
                    ")");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS player_data (" +
                    "player_name TEXT PRIMARY KEY," +
                    "doubloons INT DEFAULT 0," +
                    "purchasedEffect INT," +
                    "firstJoinDate TEXT," +
                    "level INT DEFAULT 1," +
                    "strength INT DEFAULT 0," +
                    "maxStrength INT DEFAULT 10," +
                    "playTime INT DEFAULT 0" +
                    ")");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS player_colors (" +
                    "player_name TEXT NOT NULL," +
                    "color_name TEXT NOT NULL," +
                    "PRIMARY KEY (player_name, color_name)" +
                    ")");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url);
    }

    public void close() {
        // Для SQLite не нужно закрывать что-то глобальное, т.к. мы открываем Connection на время операции
        // Можно оставить пустым
    }
}
