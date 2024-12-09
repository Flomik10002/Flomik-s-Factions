package org.flomik.FlomiksFactions.databases;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class InvitationDao {
    private final ClanDatabaseManager clanDatabaseManager;

    public InvitationDao(ClanDatabaseManager clanDatabaseManager) {
        this.clanDatabaseManager = clanDatabaseManager;
    }

    public Set<String> getInvitationsForPlayer(String playerName) {
        Set<String> invitations = new HashSet<>();
        String sql = "SELECT clan_name FROM invitations WHERE player_name=?";
        try (Connection conn = clanDatabaseManager.getConnection();
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
        try (Connection conn = clanDatabaseManager.getConnection();
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
        try (Connection conn = clanDatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, playerName);
            ps.setString(2, clanName.toLowerCase());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
