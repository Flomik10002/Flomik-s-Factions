package org.flomik.flomiksFactions.databases;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class InvitationDao {
    private final DatabaseManager databaseManager;

    public InvitationDao(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public Set<String> getInvitationsForPlayer(String playerName) {
        Set<String> invitations = new HashSet<>();
        String sql = "SELECT clan_name FROM invitations WHERE player_name=?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, playerName);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    invitations.add(rs.getString("clan_name"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return invitations;
    }

    public void saveInvitation(String playerName, String clanName) {
        String sql = "INSERT INTO invitations (player_name, clan_name) VALUES (?,?)";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, playerName);
            ps.setString(2, clanName.toLowerCase());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeInvitation(String playerName, String clanName) {
        String sql = "DELETE FROM invitations WHERE player_name=? AND clan_name=?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, playerName);
            ps.setString(2, clanName.toLowerCase());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
