package org.flomik.FlomiksFactions.database;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.flomik.FlomiksFactions.clan.Clan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO для хранения и управления данными маяка (блока привата).
 * В таблице clan_beacons должны быть следующие поля:
 * - clan_id (VARCHAR)
 * - world (VARCHAR)
 * - x (INT)
 * - y (INT)
 * - z (INT)
 * - region_id (VARCHAR)
 * - hp (INT)
 */
public class BeaconDao {

    private final ClanDatabaseManager dbManager;

    public BeaconDao(ClanDatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    /**
     * Looks up the integer `id` from the `clans` table by the clan's name.
     * @return the integer ID if found, or -1 if not found
     */
    private int fetchClanIdByName(String clanName) {
        // IMPORTANT: use "WHERE name = ?" because your `clans` table has 'name' column, NOT 'clan_name'
        String sql = "SELECT id FROM clans WHERE name = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, clanName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1; // indicates clan wasn't found
    }

    /**
     * Сохранение нового маяка в БД.
     *
     * @param clan    объект Clan (хранит имя клана), которому принадлежит маяк
     * @param loc     локация, где установлен маяк
     * @param regionId идентификатор WorldGuard-региона, связанного с маяком
     * @param hp      текущее здоровье маяка (изначально 5)
     */
    public void insertBeacon(Clan clan, Location loc, String regionId, int hp) {
        // 1) Fetch numeric clan_id from DB by clan name
        int clanId = fetchClanIdByName(clan.getName());
        if (clanId == -1) {
            // No row found for this clan in the DB
            Bukkit.getLogger().warning("No clan found with name " + clan.getName() + "! Beacon not inserted.");
            return;
        }

        // 2) Insert into clan_beacons using the numeric clanId
        String sql = "INSERT INTO clan_beacons (clan_id, world, x, y, z, region_id, hp) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, clanId);
            ps.setString(2, loc.getWorld().getName());
            ps.setInt(3, loc.getBlockX());
            ps.setInt(4, loc.getBlockY());
            ps.setInt(5, loc.getBlockZ());
            ps.setString(6, regionId);
            ps.setInt(7, hp);

            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Удаление маяка из БД по regionId.
     *
     * @param regionId идентификатор региона, связанного с маяком
     */
    public void deleteBeaconByRegionId(String regionId) {
        String sql = "DELETE FROM clan_beacons WHERE region_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, regionId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Получение локации маяка по regionId.
     *
     * @param regionId идентификатор региона, связанного с маяком
     * @return объект Location, если запись найдена, иначе null
     */
    public Location getBeaconLocationByRegionId(String regionId) {
        String sql = "SELECT world, x, y, z FROM clan_beacons WHERE region_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, regionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String worldName = rs.getString("world");
                    World world = Bukkit.getWorld(worldName);
                    if (world == null) return null;
                    int x = rs.getInt("x");
                    int y = rs.getInt("y");
                    int z = rs.getInt("z");
                    return new Location(world, x, y, z);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Обновление текущего хп маяка.
     *
     * @param regionId идентификатор региона, связанного с маяком
     * @param hp       новое значение хп
     */
    public void updateBeaconHp(String regionId, int hp) {
        String sql = "UPDATE clan_beacons SET hp = ? WHERE region_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, hp);
            ps.setString(2, regionId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Получение текущего хп маяка по regionId.
     *
     * @param regionId идентификатор региона, связанного с маяком
     * @return значение хп, если запись найдена, иначе -1
     */
    public int getBeaconHp(String regionId) {
        String sql = "SELECT hp FROM clan_beacons WHERE region_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, regionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("hp");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1;
    }

    /**
     * Проверка существования маяка в БД по regionId.
     *
     * @param regionId идентификатор региона, связанного с маяком
     * @return true, если запись найдена, иначе false
     */
    public boolean beaconExists(String regionId) {
        String sql = "SELECT 1 FROM clan_beacons WHERE region_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, regionId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
