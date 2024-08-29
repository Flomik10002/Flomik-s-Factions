package org.flomik.flomiksFactions.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;

public class SQLiteSetup {
    private static final String URL = "jdbc:sqlite:flomik_factions.db";

    public static void setupDatabase() {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {

            String sql = "CREATE TABLE IF NOT EXISTS clans (" +
                    "name TEXT PRIMARY KEY," +
                    "owner TEXT NOT NULL," +
                    "members TEXT NOT NULL," +
                    "memberRoles TEXT NOT NULL," +
                    "creationDate INTEGER NOT NULL," +
                    "description TEXT," +
                    "alliances TEXT," +
                    "level INTEGER NOT NULL," +
                    "land INTEGER NOT NULL," +
                    "strength INTEGER NOT NULL," +
                    "maxPower INTEGER NOT NULL," +
                    "homeX REAL," +
                    "homeY REAL," +
                    "homeZ REAL" +
                    ");";

            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}
