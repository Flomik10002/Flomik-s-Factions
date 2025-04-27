package org.flomik.FlomiksFactions.database;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * DAO для работы с базой данных игроков.
 * Управляет сохранением и получением данных о игроках.
 */
public class PlayerDataDao {

    private final PlayerDatabaseManager playerDatabaseManager;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Конструктор принимает менеджер базы данных.
     */
    public PlayerDataDao(PlayerDatabaseManager playerDatabaseManager) {
        this.playerDatabaseManager = playerDatabaseManager;
    }

    /**
     * Проверяет, существует ли запись о игроке в базе, и если нет — создает её.
     */
    private void ensurePlayerExists(String playerName) {
        String sql = "INSERT OR IGNORE INTO player_data (player_name) VALUES (?)";
        executeUpdate(sql, playerName);
    }

    /**
     * Универсальный метод выполнения UPDATE и INSERT SQL-запросов.
     * @param sql - SQL-запрос
     * @param params - параметры запроса
     */
    private void executeUpdate(String sql, Object... params) {
        try (Connection conn = playerDatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setParameters(ps, params);
            ps.executeUpdate();
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    /**
     * Универсальный метод выполнения SELECT SQL-запросов.
     * @param sql - SQL-запрос
     * @param params - параметры запроса
     * @return ResultSet с результатами запроса
     */
    private ResultSet executeQuery(String sql, Object... params) {
        try {
            Connection conn = playerDatabaseManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            setParameters(ps, params);
            return ps.executeQuery();
        } catch (SQLException e) {
            handleSQLException(e);
        }
        return null;
    }

    /**
     * Устанавливает параметры в PreparedStatement, автоматически определяя их тип.
     */
    private void setParameters(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            if (params[i] instanceof Integer) {
                ps.setInt(i + 1, (Integer) params[i]);
            } else if (params[i] instanceof String) {
                ps.setString(i + 1, (String) params[i]);
            }
        }
    }

    /**
     * Логирует ошибки SQL вместо простого вывода `printStackTrace()`.
     */
    private void handleSQLException(SQLException e) {
        System.err.println("Ошибка базы данных: " + e.getMessage());
        e.printStackTrace();
    }

    /**
     * Проверяет, существует ли игрок в базе данных.
     */
    public boolean hasPlayerData(String playerName) {
        String sql = "SELECT player_name FROM player_data WHERE player_name=?";
        try (ResultSet rs = executeQuery(sql, playerName)) {
            return rs != null && rs.next();
        } catch (SQLException e) {
            handleSQLException(e);
        }
        return false;
    }

    /**
     * Проверяет, есть ли у игрока дата первого входа.
     */
    public boolean hasFirstJoinDate(String playerName) {
        String sql = "SELECT firstJoinDate FROM player_data WHERE player_name=?";
        try (ResultSet rs = executeQuery(sql, playerName)) {
            return rs != null && rs.next() && rs.getString("firstJoinDate") != null;
        } catch (SQLException e) {
            handleSQLException(e);
        }
        return false;
    }

    /**
     * Устанавливает дату первого входа игрока.
     */
    public void setFirstJoinDate(String playerName, LocalDate date) {
        ensurePlayerExists(playerName);
        String sql = "UPDATE player_data SET firstJoinDate=? WHERE player_name=?";
        executeUpdate(sql, date.format(DATE_FORMATTER), playerName);
    }

    /**
     * Получает дату первого входа игрока, если её нет — возвращает "Неизвестно".
     */
    public String getFirstJoinDate(String playerName) {
        String sql = "SELECT firstJoinDate FROM player_data WHERE player_name=?";
        try (ResultSet rs = executeQuery(sql, playerName)) {
            if (rs != null && rs.next()) {
                return rs.getString("firstJoinDate") != null ? rs.getString("firstJoinDate") : "Неизвестно";
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
        return "Неизвестно";
    }

    /**
     * Сохраняет атрибуты игрока: уровень, силу, макс. силу.
     */
    public void savePlayerAttributes(String playerName, int level, int strength, int maxStrength) {
        ensurePlayerExists(playerName);
        String sql = "UPDATE player_data SET level=?, strength=?, maxStrength=? WHERE player_name=?";
        executeUpdate(sql, level, strength, maxStrength, playerName);
    }

    /**
     * Универсальный метод получения числовых атрибутов игрока (уровень, сила, макс. сила).
     */
    private int getPlayerAttribute(String playerName, String attribute, int defaultValue) {
        String sql = "SELECT " + attribute + " FROM player_data WHERE player_name=?";
        try (ResultSet rs = executeQuery(sql, playerName)) {
            if (rs != null && rs.next()) {
                return rs.getInt(attribute);
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
        return defaultValue;
    }

    /**
     * Получает уровень игрока (по умолчанию 1).
     */
    public int getPlayerLevel(String playerName) {
        return getPlayerAttribute(playerName, "level", 1);
    }

    /**
     * Получает силу игрока (по умолчанию 0).
     */
    public int getPlayerStrength(String playerName) {
        return getPlayerAttribute(playerName, "strength", 0);
    }

    /**
     * Получает максимальную силу игрока (по умолчанию 10).
     */
    public int getPlayerMaxStrength(String playerName) {
        return getPlayerAttribute(playerName, "maxStrength", 10);
    }

    /**
     * Устанавливает уровень игрока.
     */
    public void setPlayerLevel(String playerName, int level) {
        ensurePlayerExists(playerName);
        String sql = "UPDATE player_data SET level=? WHERE player_name=?";
        executeUpdate(sql, level, playerName);
    }

    /**
     * Устанавливает силу игрока.
     */
    public void setPlayerStrength(String playerName, int strength) {
        ensurePlayerExists(playerName);
        String sql = "UPDATE player_data SET strength=? WHERE player_name=?";
        executeUpdate(sql, strength, playerName);
    }

    /**
     * Добавляет силу игроку.
     */
    public void addPlayerStrength(String playerName, int strengthToAdd) {
        ensurePlayerExists(playerName);
        String sql = "UPDATE player_data SET strength = strength + ? WHERE player_name=?";
        executeUpdate(sql, strengthToAdd, playerName);
    }

    /**
     * Устанавливает время игры игрока.
     */
    public void setPlayTime(String playerName, int ticksPlayed) {
        ensurePlayerExists(playerName);
        String sql = "UPDATE player_data SET playTime=? WHERE player_name=?";
        executeUpdate(sql, ticksPlayed, playerName);
    }

    /**
     * Получает время игры игрока (по умолчанию 0).
     */
    public int getPlayTime(String playerName) {
        return getPlayerAttribute(playerName, "playTime", 0);
    }
}
