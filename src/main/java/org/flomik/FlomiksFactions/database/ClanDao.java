package org.flomik.FlomiksFactions.database;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.flomik.FlomiksFactions.clan.Clan;

import java.sql.*;
import java.util.*;
import java.util.Date;

public class ClanDao {
    private final ClanDatabaseManager clanDatabaseManager;

    public ClanDao(ClanDatabaseManager clanDatabaseManager) {
        this.clanDatabaseManager = clanDatabaseManager;
    }

    public Clan getClanByName(String name) {
        try (Connection conn = clanDatabaseManager.getConnection()) {
            String sql = "SELECT * FROM clans WHERE LOWER(name) = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, name.toLowerCase());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return mapRowToClan(conn, rs);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Clan> getAllClans() {
        List<Clan> result = new ArrayList<>();
        try (Connection conn = clanDatabaseManager.getConnection()) {
            String sql = "SELECT * FROM clans";
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Clan clan = mapRowToClan(conn, rs);
                    result.add(clan);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void insertClan(Clan clan) {
        String sql = "INSERT INTO clans (name, owner, creation_date, description, land, strength, level, clan_xp, balance, max_power, home_world, home_x, home_y, home_z, home_yaw, home_pitch) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = clanDatabaseManager.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, clan.getName());
                ps.setString(2, clan.getOwner());
                ps.setLong(3, clan.getCreationDate().getTime());
                ps.setString(4, clan.getDescription());
                ps.setInt(5, clan.getLands());
                ps.setInt(6, clan.getStrength());
                ps.setInt(7, clan.getLevel());
                ps.setInt(8, clan.getClanXp());
                ps.setDouble(9, clan.getBalance());
                ps.setInt(10, clan.getMaxPower());
                if (clan.hasHome()) {
                    ps.setString(11, clan.getHome().getWorld().getName());
                    ps.setDouble(12, clan.getHome().getX());
                    ps.setDouble(13, clan.getHome().getY());
                    ps.setDouble(14, clan.getHome().getZ());
                    ps.setFloat(15, clan.getHome().getYaw());
                    ps.setFloat(16, clan.getHome().getPitch());
                } else {
                    ps.setNull(11, Types.VARCHAR);
                    ps.setNull(12, Types.DOUBLE);
                    ps.setNull(13, Types.DOUBLE);
                    ps.setNull(14, Types.DOUBLE);
                    ps.setNull(15, Types.FLOAT);
                    ps.setNull(16, Types.FLOAT);
                }
                ps.executeUpdate();
                int clanId;
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    clanId = keys.getInt(1);
                }
                saveMembers(conn, clanId, clan);
                saveAlliances(conn, clanId, clan.getAlliances());
                saveChunks(conn, clanId, clan.getRegionNames());
                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                ex.printStackTrace();
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateClan(Clan clan) {
        String sql = "UPDATE clans SET owner = ?, creation_date = ?, description = ?, land = ?, strength = ?, level = ?, clan_xp = ?, balance = ?, max_power = ?, home_world = ?, home_x = ?, home_y = ?, home_z = ?, home_yaw = ?, home_pitch = ? " +
                "WHERE LOWER(name) = ?";
        try (Connection conn = clanDatabaseManager.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, clan.getOwner());
                ps.setLong(2, clan.getCreationDate().getTime());
                ps.setString(3, clan.getDescription());
                ps.setInt(4, clan.getLands());
                ps.setInt(5, clan.getStrength());
                ps.setInt(6, clan.getLevel());
                ps.setInt(7, clan.getClanXp());
                ps.setDouble(8, clan.getBalance());
                ps.setInt(9, clan.getMaxPower());
                if (clan.hasHome()) {
                    ps.setString(10, clan.getHome().getWorld().getName());
                    ps.setDouble(11, clan.getHome().getX());
                    ps.setDouble(12, clan.getHome().getY());
                    ps.setDouble(13, clan.getHome().getZ());
                    ps.setFloat(14, clan.getHome().getYaw());
                    ps.setFloat(15, clan.getHome().getPitch());
                } else {
                    ps.setNull(10, Types.VARCHAR);
                    ps.setNull(11, Types.DOUBLE);
                    ps.setNull(12, Types.DOUBLE);
                    ps.setNull(13, Types.DOUBLE);
                    ps.setNull(14, Types.FLOAT);
                    ps.setNull(15, Types.FLOAT);
                }
                ps.setString(16, clan.getName().toLowerCase());
                ps.executeUpdate();

                int clanId = getClanIdByName(conn, clan.getName());

                clearMembers(conn, clanId);
                saveMembers(conn, clanId, clan);

                clearAlliances(conn, clanId);
                saveAlliances(conn, clanId, clan.getAlliances());

                clearChunks(conn, clanId);
                saveChunks(conn, clanId, clan.getRegionNames());

                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                ex.printStackTrace();
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteClan(String clanName) {
        try (Connection conn = clanDatabaseManager.getConnection()) {
            String sql = "DELETE FROM clans WHERE LOWER(name)=?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, clanName.toLowerCase());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getClanIdByName(Connection conn, String name) throws SQLException {
        String sql = "SELECT id FROM clans WHERE LOWER(name)=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name.toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("id");
            }
        }
        return -1;
    }

    private void saveMembers(Connection conn, int clanId, Clan clan) throws SQLException {
        String insert = "INSERT INTO clan_members (clan_id, member_name, role) VALUES (?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(insert)) {
            for (String member : clan.getMembers()) {
                ps.setInt(1, clanId);
                ps.setString(2, member);
                ps.setString(3, clan.getRole(member));
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void saveAlliances(Connection conn, int clanId, List<String> alliances) throws SQLException {
        String insert = "INSERT INTO clan_alliances (clan_id, alliance) VALUES (?,?)";
        try (PreparedStatement ps = conn.prepareStatement(insert)) {
            for (String ally : alliances) {
                ps.setInt(1, clanId);
                ps.setString(2, ally);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void saveChunks(Connection conn, int clanId, List<String> chunks) throws SQLException {
        String insert = "INSERT INTO clan_chunks (clan_id, chunk_name) VALUES (?,?)";
        try (PreparedStatement ps = conn.prepareStatement(insert)) {
            for (String chunk : chunks) {
                ps.setInt(1, clanId);
                ps.setString(2, chunk);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void clearMembers(Connection conn, int clanId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM clan_members WHERE clan_id=?")) {
            ps.setInt(1, clanId);
            ps.executeUpdate();
        }
    }

    private void clearAlliances(Connection conn, int clanId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM clan_alliances WHERE clan_id=?")) {
            ps.setInt(1, clanId);
            ps.executeUpdate();
        }
    }

    private void clearChunks(Connection conn, int clanId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM clan_chunks WHERE clan_id=?")) {
            ps.setInt(1, clanId);
            ps.executeUpdate();
        }
    }

    private Clan mapRowToClan(Connection conn, ResultSet rs) throws SQLException {
        String name = rs.getString("name");
        String owner = rs.getString("owner");
        Date creationDate = new Date(rs.getLong("creation_date"));
        String description = rs.getString("description");
        int land = rs.getInt("land");
        int strength = rs.getInt("strength");
        int level = rs.getInt("level");
        int clanXp = rs.getInt("clan_xp");
        double balance = rs.getDouble("balance");
        int maxPower = rs.getInt("max_power");

        String homeWorld = rs.getString("home_world");
        Double homeX = (Double)rs.getObject("home_x");
        Double homeY = (Double)rs.getObject("home_y");
        Double homeZ = (Double)rs.getObject("home_z");
        Float homeYaw = (Float)rs.getObject("home_yaw");
        Float homePitch = (Float)rs.getObject("home_pitch");

        Location home = null;
        if (homeWorld != null) {
            World w = Bukkit.getWorld(homeWorld);
            if (w != null) {
                home = new Location(w, homeX, homeY, homeZ, homeYaw, homePitch);
            }
        }

        int clanId = rs.getInt("id");

        Set<String> members = loadMembers(conn, clanId);
        Map<String,String> roles = loadRoles(conn, clanId);
        List<String> alliances = loadAlliances(conn, clanId);
        List<String> chunks = loadChunks(conn, clanId);

        Clan clan = new Clan(name, owner, members, roles, creationDate, description, alliances, level, clanXp, balance, land, strength, maxPower, chunks);
        clan.setHome(home);
        clan.setBalance(balance);
        return clan;
    }

    private Set<String> loadMembers(Connection conn, int clanId) throws SQLException {
        Set<String> members = new HashSet<>();
        String sql = "SELECT member_name FROM clan_members WHERE clan_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clanId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    members.add(rs.getString("member_name"));
                }
            }
        }
        return members;
    }

    private Map<String,String> loadRoles(Connection conn, int clanId) throws SQLException {
        Map<String,String> roles = new HashMap<>();
        String sql = "SELECT member_name, role FROM clan_members WHERE clan_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clanId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    roles.put(rs.getString("member_name"), rs.getString("role"));
                }
            }
        }
        return roles;
    }

    private List<String> loadAlliances(Connection conn, int clanId) throws SQLException {
        List<String> alliances = new ArrayList<>();
        String sql = "SELECT alliance FROM clan_alliances WHERE clan_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clanId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    alliances.add(rs.getString("alliance"));
                }
            }
        }
        return alliances;
    }

    private List<String> loadChunks(Connection conn, int clanId) throws SQLException {
        List<String> chunks = new ArrayList<>();
        String sql = "SELECT chunk_name FROM clan_chunks WHERE clan_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clanId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    chunks.add(rs.getString("chunk_name"));
                }
            }
        }
        return chunks;
    }
}
