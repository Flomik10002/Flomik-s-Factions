package org.flomik.flomiksFactions.databases;

import org.bukkit.Color;
import org.flomik.flomiksFactions.databases.PlayerDatabaseManager;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

public class PlayerDataDao {

    private final PlayerDatabaseManager playerDatabaseManager;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public PlayerDataDao(PlayerDatabaseManager playerDatabaseManager) {
        this.playerDatabaseManager = playerDatabaseManager;
    }

    private void ensurePlayerExists(String playerName) throws SQLException {
        try (Connection conn = playerDatabaseManager.getConnection()) {
            String sql = "INSERT OR IGNORE INTO player_data (player_name) VALUES (?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, playerName);
                ps.executeUpdate();
            }
        }
    }

    public Set<String> getPurchasedColors(String playerName) {
        Set<String> colors = new HashSet<>();
        String sql = "SELECT color_name FROM player_colors WHERE player_name=?";
        try (Connection conn = playerDatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, playerName);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    colors.add(rs.getString("color_name"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return colors;
    }

    public void addDoubloons(String playerName, int amount) {
        String sql = "UPDATE player_data SET doubloons = doubloons + ? WHERE player_name=?";
        try (Connection conn = playerDatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ensurePlayerExists(playerName);
            ps.setInt(1, amount);
            ps.setString(2, playerName);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getDoubloons(String playerName) {
        String sql = "SELECT doubloons FROM player_data WHERE player_name=?";
        try (Connection conn = playerDatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, playerName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("doubloons");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setPlayerEffectColor(String playerName, Color color) {
        String sql = "UPDATE player_data SET purchasedEffect=? WHERE player_name=?";
        try (Connection conn = playerDatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ensurePlayerExists(playerName);
            ps.setInt(1, color.asRGB());
            ps.setString(2, playerName);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addPurchasedColor(String playerName, String colorName) {
        String sql = "INSERT OR IGNORE INTO player_colors (player_name, color_name) VALUES (?,?)";
        try (Connection conn = playerDatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ensurePlayerExists(playerName);
            ps.setString(1, playerName);
            ps.setString(2, colorName);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Color getPlayerEffectColor(String playerName) {
        if (!hasPurchasedParticles(playerName)) return null;
        String sql = "SELECT purchasedEffect FROM player_data WHERE player_name=?";
        try (Connection conn = playerDatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, playerName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int rgb = rs.getInt("purchasedEffect");
                    return Color.fromRGB(rgb);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean hasPurchasedParticles(String playerName) {
        String sql = "SELECT purchasedEffect FROM player_data WHERE player_name=?";
        try (Connection conn = playerDatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, playerName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getObject("purchasedEffect") != null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean hasFirstJoinDate(String playerName) {
        String sql = "SELECT firstJoinDate FROM player_data WHERE player_name=?";
        try (Connection conn = playerDatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, playerName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("firstJoinDate") != null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setFirstJoinDate(String playerName, LocalDate date) {
        String sql = "UPDATE player_data SET firstJoinDate=? WHERE player_name=?";
        try (Connection conn = playerDatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ensurePlayerExists(playerName);
            ps.setString(1, date.format(DATE_FORMATTER));
            ps.setString(2, playerName);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean hasPlayerData(String playerName) {
        String sql = "SELECT player_name FROM player_data WHERE player_name=?";
        try (Connection conn = playerDatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, playerName);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void savePlayerAttributes(String playerName, int level, int strength, int maxStrength) {
        String sql = "UPDATE player_data SET level=?, strength=?, maxStrength=? WHERE player_name=?";
        try (Connection conn = playerDatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ensurePlayerExists(playerName);
            ps.setInt(1, level);
            ps.setInt(2, strength);
            ps.setInt(3, maxStrength);
            ps.setString(4, playerName);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getFirstJoinDate(String playerName) {
        String sql = "SELECT firstJoinDate FROM player_data WHERE player_name=?";
        try (Connection conn = playerDatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, playerName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String date = rs.getString("firstJoinDate");
                    return date != null ? date : "Неизвестно";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Неизвестно";
    }

    public int getPlayerLevel(String playerName) {
        String sql = "SELECT level FROM player_data WHERE player_name=?";
        try (Connection conn = playerDatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, playerName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("level");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public void setPlayerLevel(String playerName, int level) {
        String sql = "UPDATE player_data SET level=? WHERE player_name=?";
        try (Connection conn = playerDatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ensurePlayerExists(playerName);
            ps.setInt(1, level);
            ps.setString(2, playerName);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getPlayerStrength(String playerName) {
        String sql = "SELECT strength FROM player_data WHERE player_name=?";
        try (Connection conn = playerDatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, playerName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("strength");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setPlayerStrength(String playerName, int strength) {
        String sql = "UPDATE player_data SET strength=? WHERE player_name=?";
        try (Connection conn = playerDatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ensurePlayerExists(playerName);
            ps.setInt(1, strength);
            ps.setString(2, playerName);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addPlayerStrength(String playerName, int strengthToAdd) {
        String sql = "UPDATE player_data SET strength = strength + ? WHERE player_name=?";
        try (Connection conn = playerDatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ensurePlayerExists(playerName);
            ps.setInt(1, strengthToAdd);
            ps.setString(2, playerName);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getPlayerMaxStrength(String playerName) {
        String sql = "SELECT maxStrength FROM player_data WHERE player_name=?";
        try (Connection conn = playerDatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, playerName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("maxStrength");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 10;
    }

    public void setPlayTime(String playerName, int ticksPlayed) {
        String sql = "UPDATE player_data SET playTime=? WHERE player_name=?";
        try (Connection conn = playerDatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ensurePlayerExists(playerName);
            ps.setInt(1, ticksPlayed);
            ps.setString(2, playerName);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getPlayTime(String playerName) {
        String sql = "SELECT playTime FROM player_data WHERE player_name=?";
        try (Connection conn = playerDatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, playerName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("playTime");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
