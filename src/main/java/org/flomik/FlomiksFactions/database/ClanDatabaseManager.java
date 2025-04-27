package org.flomik.FlomiksFactions.database; //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression

import org.bukkit.plugin.Plugin;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ClanDatabaseManager { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private String url; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression

    public void initDatabase(Plugin plugin) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        File dbFile = new File(plugin.getDataFolder(), "clans.db"); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        if (!plugin.getDataFolder().exists()) { //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
            plugin.getDataFolder().mkdirs(); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
        }

        url = "jdbc:sqlite:" + dbFile.getAbsolutePath();
    }

    public void createTables() { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        try (Connection conn = DriverManager.getConnection(url)) {
            try (Statement pragmaStmt = conn.createStatement()) {
                pragmaStmt.execute("PRAGMA foreign_keys = ON;");
            }

            try (Statement stmt = conn.createStatement()) {

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
                        "balance REAL DEFAULT 0," +
                        "max_power INT," +
                        "home_world TEXT," +
                        "home_x DOUBLE," +
                        "home_y DOUBLE," +
                        "home_z DOUBLE," +
                        "home_yaw FLOAT," +
                        "home_pitch FLOAT" +
                        ")");

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
            }


        } catch (SQLException e) {
            e.printStackTrace(); //NOPMD - suppressed AvoidPrintStackTrace - TODO explain reason for suppression //NOPMD - suppressed AvoidPrintStackTrace - TODO explain reason for suppression //NOPMD - suppressed AvoidPrintStackTrace - TODO explain reason for suppression
        }
    }

    public Connection getConnection() throws SQLException { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        Connection conn = DriverManager.getConnection(url); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        try (Statement st = conn.createStatement()) { //NOPMD - suppressed ShortVariable - TODO explain reason for suppression //NOPMD - suppressed ShortVariable - TODO explain reason for suppression //NOPMD - suppressed ShortVariable - TODO explain reason for suppression
            st.execute("PRAGMA foreign_keys = ON;");
        }
        return conn;
    }

    public void close() { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        // Для SQLite не нужно закрывать что-то глобальное, т.к. мы открываем Connection на время операции //NOPMD - suppressed CommentSize - TODO explain reason for suppression //NOPMD - suppressed CommentSize - TODO explain reason for suppression //NOPMD - suppressed CommentSize - TODO explain reason for suppression
        // Можно оставить пустым
    }
}
