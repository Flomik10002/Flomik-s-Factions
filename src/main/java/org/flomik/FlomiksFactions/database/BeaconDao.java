package org.flomik.FlomiksFactions.database; //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.flomik.FlomiksFactions.clan.Clan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** //NOPMD - suppressed CommentSize - TODO explain reason for suppression //NOPMD - suppressed CommentSize - TODO explain reason for suppression //NOPMD - suppressed CommentSize - TODO explain reason for suppression
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

    private final ClanDatabaseManager dbManager; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression

    public BeaconDao(ClanDatabaseManager dbManager) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        this.dbManager = dbManager;
    }

    /**
     * Looks up the integer `id` from the `clans` table by the clan's name.
     * @return the integer ID if found, or -1 if not found
     */
    private int fetchClanIdByName(String clanName) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        // IMPORTANT: use "WHERE name = ?" because your `clans` table has 'name' column, NOT 'clan_name' //NOPMD - suppressed CommentSize - TODO explain reason for suppression //NOPMD - suppressed CommentSize - TODO explain reason for suppression //NOPMD - suppressed CommentSize - TODO explain reason for suppression
        String sql = "SELECT id FROM clans WHERE name = ?"; //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression

        try (Connection conn = dbManager.getConnection(); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
             PreparedStatement ps = conn.prepareStatement(sql)) { //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression

            ps.setString(1, clanName);
            try (ResultSet rs = ps.executeQuery()) { //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
                if (rs.next()) {
                    return rs.getInt("id"); //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace(); //NOPMD - suppressed AvoidPrintStackTrace - TODO explain reason for suppression //NOPMD - suppressed AvoidPrintStackTrace - TODO explain reason for suppression //NOPMD - suppressed AvoidPrintStackTrace - TODO explain reason for suppression
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
    public void insertBeacon(Clan clan, Location loc, String regionId, int hp) { //NOPMD - suppressed ShortVariable - TODO explain reason for suppression //NOPMD - suppressed ShortVariable - TODO explain reason for suppression //NOPMD - suppressed ShortVariable - TODO explain reason for suppression
        // 1) Fetch numeric clan_id from DB by clan name
        int clanId = fetchClanIdByName(clan.getName()); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        if (clanId == -1) {
            // No row found for this clan in the DB
            Bukkit.getLogger().warning("No clan found with name " + clan.getName() + "! Beacon not inserted."); //NOPMD - suppressed GuardLogStatement - TODO explain reason for suppression //NOPMD - suppressed GuardLogStatement - TODO explain reason for suppression //NOPMD - suppressed GuardLogStatement - TODO explain reason for suppression
            return;
        }

        // 2) Insert into clan_beacons using the numeric clanId
        String sql = "INSERT INTO clan_beacons (clan_id, world, x, y, z, region_id, hp) VALUES (?, ?, ?, ?, ?, ?, ?)"; //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        try (Connection conn = dbManager.getConnection(); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
             PreparedStatement ps = conn.prepareStatement(sql)) { //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression

            ps.setInt(1, clanId);
            ps.setString(2, loc.getWorld().getName()); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
            ps.setInt(3, loc.getBlockX());
            ps.setInt(4, loc.getBlockY());
            ps.setInt(5, loc.getBlockZ());
            ps.setString(6, regionId);
            ps.setInt(7, hp);

            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace(); //NOPMD - suppressed AvoidPrintStackTrace - TODO explain reason for suppression //NOPMD - suppressed AvoidPrintStackTrace - TODO explain reason for suppression //NOPMD - suppressed AvoidPrintStackTrace - TODO explain reason for suppression
        }
    }

    /**
     * Удаление маяка из БД по regionId.
     *
     * @param regionId идентификатор региона, связанного с маяком
     */
    public void deleteBeaconByRegionId(String regionId) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        String sql = "DELETE FROM clan_beacons WHERE region_id = ?"; //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        try (Connection conn = dbManager.getConnection(); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
             PreparedStatement ps = conn.prepareStatement(sql)) { //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
            ps.setString(1, regionId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace(); //NOPMD - suppressed AvoidPrintStackTrace - TODO explain reason for suppression //NOPMD - suppressed AvoidPrintStackTrace - TODO explain reason for suppression //NOPMD - suppressed AvoidPrintStackTrace - TODO explain reason for suppression
        }
    }

    /**
     * Получение локации маяка по regionId.
     *
     * @param regionId идентификатор региона, связанного с маяком
     * @return объект Location, если запись найдена, иначе null
     */
    public Location getBeaconLocationByRegionId(String regionId) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        String sql = "SELECT world, x, y, z FROM clan_beacons WHERE region_id = ?"; //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        try (Connection conn = dbManager.getConnection(); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
             PreparedStatement ps = conn.prepareStatement(sql)) { //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
            ps.setString(1, regionId);
            try (ResultSet rs = ps.executeQuery()) { //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
                if (rs.next()) {
                    String worldName = rs.getString("world"); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                    World world = Bukkit.getWorld(worldName); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                    if (world == null) return null; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
                    int x = rs.getInt("x"); //NOPMD - suppressed ShortVariable - TODO explain reason for suppression //NOPMD - suppressed ShortVariable - TODO explain reason for suppression //NOPMD - suppressed ShortVariable - TODO explain reason for suppression
                    int y = rs.getInt("y"); //NOPMD - suppressed ShortVariable - TODO explain reason for suppression //NOPMD - suppressed ShortVariable - TODO explain reason for suppression //NOPMD - suppressed ShortVariable - TODO explain reason for suppression
                    int z = rs.getInt("z"); //NOPMD - suppressed ShortVariable - TODO explain reason for suppression //NOPMD - suppressed ShortVariable - TODO explain reason for suppression //NOPMD - suppressed ShortVariable - TODO explain reason for suppression
                    return new Location(world, x, y, z); //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace(); //NOPMD - suppressed AvoidPrintStackTrace - TODO explain reason for suppression //NOPMD - suppressed AvoidPrintStackTrace - TODO explain reason for suppression //NOPMD - suppressed AvoidPrintStackTrace - TODO explain reason for suppression
        }
        return null;
    }

    /**
     * Обновление текущего хп маяка.
     *
     * @param regionId идентификатор региона, связанного с маяком
     * @param hp       новое значение хп
     */
    public void updateBeaconHp(String regionId, int hp) { //NOPMD - suppressed ShortVariable - TODO explain reason for suppression //NOPMD - suppressed ShortVariable - TODO explain reason for suppression //NOPMD - suppressed ShortVariable - TODO explain reason for suppression
        String sql = "UPDATE clan_beacons SET hp = ? WHERE region_id = ?"; //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        try (Connection conn = dbManager.getConnection(); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
             PreparedStatement ps = conn.prepareStatement(sql)) { //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
            ps.setInt(1, hp);
            ps.setString(2, regionId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace(); //NOPMD - suppressed AvoidPrintStackTrace - TODO explain reason for suppression //NOPMD - suppressed AvoidPrintStackTrace - TODO explain reason for suppression //NOPMD - suppressed AvoidPrintStackTrace - TODO explain reason for suppression
        }
    }

    /**
     * Получение текущего хп маяка по regionId.
     *
     * @param regionId идентификатор региона, связанного с маяком
     * @return значение хп, если запись найдена, иначе -1
     */
    public int getBeaconHp(String regionId) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        String sql = "SELECT hp FROM clan_beacons WHERE region_id = ?"; //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        try (Connection conn = dbManager.getConnection(); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
             PreparedStatement ps = conn.prepareStatement(sql)) { //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
            ps.setString(1, regionId);
            try (ResultSet rs = ps.executeQuery()) { //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
                if (rs.next()) {
                    return rs.getInt("hp"); //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace(); //NOPMD - suppressed AvoidPrintStackTrace - TODO explain reason for suppression //NOPMD - suppressed AvoidPrintStackTrace - TODO explain reason for suppression //NOPMD - suppressed AvoidPrintStackTrace - TODO explain reason for suppression
        }
        return -1;
    }

    /**
     * Проверка существования маяка в БД по regionId.
     *
     * @param regionId идентификатор региона, связанного с маяком
     * @return true, если запись найдена, иначе false
     */
    public boolean beaconExists(String regionId) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        String sql = "SELECT 1 FROM clan_beacons WHERE region_id = ?"; //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        try (Connection conn = dbManager.getConnection(); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
             PreparedStatement ps = conn.prepareStatement(sql)) { //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
            ps.setString(1, regionId);
            try (ResultSet rs = ps.executeQuery()) { //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
                return rs.next(); //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
            }
        } catch (SQLException ex) {
            ex.printStackTrace(); //NOPMD - suppressed AvoidPrintStackTrace - TODO explain reason for suppression //NOPMD - suppressed AvoidPrintStackTrace - TODO explain reason for suppression //NOPMD - suppressed AvoidPrintStackTrace - TODO explain reason for suppression
        }
        return false;
    }
}
