package org.flomik.FlomiksFactions.database;

import org.bukkit.plugin.Plugin;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class BeaconDatabaseManager {
    private String url;

    public void initDatabase(Plugin plugin) {
        File dbFile = new File(plugin.getDataFolder(), "clans.db");
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        url = "jdbc:sqlite:" + dbFile.getAbsolutePath();
    }

    public void createTables() {
        try (Connection conn = DriverManager.getConnection(url)) {
            // Включаем поддержку внешних ключей
            try (Statement pragmaStmt = conn.createStatement()) {
                pragmaStmt.execute("PRAGMA foreign_keys = ON;");
            }

            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS clan_beacons (" +
                                " id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                " clan_id INT NOT NULL," +
                                " world TEXT NOT NULL," +
                                " x DOUBLE NOT NULL," +
                                " y DOUBLE NOT NULL," +
                                " z DOUBLE NOT NULL," +
                                " region_id TEXT NOT NULL," +
                                " hp INT NOT NULL DEFAULT 5," +
                                " FOREIGN KEY (clan_id) REFERENCES clans(id) ON DELETE CASCADE" +
                                ")"
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(url);
        try (Statement st = conn.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON;");
        }
        return conn;
    }

    public void close() {
        // Для SQLite не нужно закрывать что-то глобальное, т.к. мы открываем Connection на время операции
        // Можно оставить пустым
    }
}
