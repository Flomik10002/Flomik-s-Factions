package org.flomik.FlomiksFactions.database;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.flomik.FlomiksFactions.clan.Clan;
import org.flomik.FlomiksFactions.clan.nexus.Beacon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class BeaconDao {

    private final ClanDatabaseManager dbManager;
    private final Logger logger = Bukkit.getLogger();

    public BeaconDao(ClanDatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    /**
     * Получает идентификатор клана по его имени.
     * @param clanName имя клана
     * @return числовой идентификатор или -1, если клан не найден
     */
    private int fetchClanIdByName(String clanName) {
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
            logger.severe("Ошибка при получении id клана: " + ex.getMessage());
        }
        return -1;
    }

    /**
     * Вставляет новый маяк в базу данных.
     *
     * @param clan объект клана
     * @param loc локация маяка
     * @param regionId идентификатор региона WorldGuard, связанного с маяком
     * @param hp здоровье маяка (начальное значение обычно 5)
     */
    public void insertBeacon(Clan clan, Location loc, String regionId, int hp) {
        int clanId = fetchClanIdByName(clan.getName());
        if (clanId == -1) {
            logger.warning("Клан с именем " + clan.getName() + " не найден! Маяк не добавлен.");
            return;
        }

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
            logger.severe("Ошибка при вставке маяка: " + ex.getMessage());
        }
    }

    /**
     * Удаляет маяк по идентификатору региона.
     *
     * @param regionId идентификатор региона
     */
    public void deleteBeaconByRegionId(String regionId) {
        String sql = "DELETE FROM clan_beacons WHERE region_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, regionId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            logger.severe("Ошибка при удалении маяка: " + ex.getMessage());
        }
    }

    /**
     * Получает локацию маяка по идентификатору региона.
     *
     * @param regionId идентификатор региона
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
                    if (world == null) {
                        logger.warning("Мир " + worldName + " не найден. Невозможно получить локацию маяка.");
                        return null;
                    }
                    int x = rs.getInt("x");
                    int y = rs.getInt("y");
                    int z = rs.getInt("z");
                    return new Location(world, x, y, z);
                }
            }
        } catch (SQLException ex) {
            logger.severe("Ошибка при получении локации маяка: " + ex.getMessage());
        }
        return null;
    }

    /**
     * Обновляет здоровье маяка.
     *
     * @param regionId идентификатор региона
     * @param hp новое значение здоровья
     */
    public void updateBeaconHp(String regionId, int hp) {
        String sql = "UPDATE clan_beacons SET hp = ? WHERE region_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, hp);
            ps.setString(2, regionId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            logger.severe("Ошибка при обновлении здоровья маяка: " + ex.getMessage());
        }
    }

    /**
     * Получает текущее здоровье маяка по идентификатору региона.
     *
     * @param regionId идентификатор региона
     * @return здоровье маяка, если запись найдена, иначе -1
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
            logger.severe("Ошибка при получении здоровья маяка: " + ex.getMessage());
        }
        return -1;
    }

    /**
     * Проверяет, существует ли маяк по идентификатору региона.
     *
     * @param regionId идентификатор региона
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
            logger.severe("Ошибка при проверке существования маяка: " + ex.getMessage());
        }
        return false;
    }

    public List<Beacon> getAllBeacons() {
        List<Beacon> beacons = new ArrayList<>();
        String sql = "SELECT clan_id, world, x, y, z, region_id, hp FROM clan_beacons";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int clanId = rs.getInt("clan_id");
                String worldName = rs.getString("world");
                World world = Bukkit.getWorld(worldName);
                if (world == null) {
                    logger.warning("Мир " + worldName + " не найден. Пропускаем маяк.");
                    continue;
                }

                int x = rs.getInt("x");
                int y = rs.getInt("y");
                int z = rs.getInt("z");
                Location loc = new Location(world, x, y, z);
                String regionId = rs.getString("region_id");
                int hp = rs.getInt("hp");

                // По clan_id получаем имя клана
                String clanName = fetchClanNameById(clanId);
                if (clanName == null) {
                    logger.warning("Клан с ID " + clanId + " не найден. Пропускаем маяк.");
                    continue;
                }

                beacons.add(new Beacon(clanName, loc, hp, regionId));
            }

        } catch (SQLException ex) {
            logger.severe("Ошибка при получении маяков: " + ex.getMessage());
        }

        return beacons;
    }

    private String fetchClanNameById(int clanId) {
        String sql = "SELECT name FROM clans WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clanId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("name");
                }
            }
        } catch (SQLException ex) {
            logger.severe("Ошибка при получении имени клана по id: " + ex.getMessage());
        }
        return null;
    }
}
