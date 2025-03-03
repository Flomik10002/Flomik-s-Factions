package org.flomik.FlomiksFactions.database; //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression

import org.bukkit.plugin.Plugin;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class PlayerDatabaseManager { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private String url; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression

    public void initDatabase(Plugin plugin) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        if (!plugin.getDataFolder().exists()) { //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
            plugin.getDataFolder().mkdirs(); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
        }

        File dbFile = new File(plugin.getDataFolder(), "players.db"); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        url = "jdbc:sqlite:" + dbFile.getAbsolutePath();
    }

    public void createTables() { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        try (Connection conn = DriverManager.getConnection(url)) {
            try (Statement pragmaStmt = conn.createStatement()) {
                pragmaStmt.execute("PRAGMA foreign_keys = ON;");
            }

            try (Statement stmt = conn.createStatement()) {

                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS player_data (" +
                        "player_name TEXT PRIMARY KEY," +
                        "firstJoinDate TEXT," +
                        "level INT DEFAULT 1," +
                        "strength INT DEFAULT 0," +
                        "maxStrength INT DEFAULT 10," +
                        "playTime INT DEFAULT 0" +
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

    public void close() { //NOPMD - suppressed UncommentedEmptyMethodBody - TODO explain reason for suppression //NOPMD - suppressed UncommentedEmptyMethodBody - TODO explain reason for suppression //NOPMD - suppressed UncommentedEmptyMethodBody - TODO explain reason for suppression
    }
}