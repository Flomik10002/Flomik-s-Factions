package org.flomik.FlomiksFactions.clan.managers;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.flomik.FlomiksFactions.FlomiksFactions;
import org.flomik.FlomiksFactions.clan.Clan;
import org.flomik.FlomiksFactions.database.ClanDao;
import org.flomik.FlomiksFactions.database.InvitationDao;
import org.flomik.FlomiksFactions.player.PlayerDataHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Класс-менеджер для управления кланами:
 * - хранение кланов в памяти (кэш),
 * - создание/удаление кланов,
 * - приглашения, вступление/выход,
 * - взаимодействие с регионами WorldGuard (добавление/удаление игроков).
 */
public class ClanManager {

    private final ClanDao clanDao;
    private final InvitationDao invitationDao;
    private final Logger logger = Bukkit.getLogger();  // Для логгирования предупреждений/ошибок

    // Потокобезопасная мапа: название клана (toLowerCase) -> объект клана
    private final Map<String, Clan> clans = new ConcurrentHashMap<>();

    public ClanManager(FlomiksFactions plugin, ClanDao clanDao, InvitationDao invitationDao) {
        this.clanDao = clanDao;
        this.invitationDao = invitationDao;
        loadClans();
    }

    public InvitationDao getInvitationDao() {
        return invitationDao;
    }

    public ClanDao getClanDao() {
        return clanDao;
    }

    // ===========================================================================
    //   Работа с регионами (WorldGuard)
    // ===========================================================================

    /**
     * Приватный метод для единой логики добавления/удаления игрока в/из регионов.
     *
     * @param playerUUID     UUID игрока
     * @param clanLeaderUUID UUID лидера клана (чтобы знать, какие регионы принадлежат клану)
     * @param asOwner        если true – добавляем/удаляем игрока как Owner, иначе как Member
     * @param remove         если true – удаляем игрока из региона; если false – добавляем
     */
    private void updateRegionMembership(UUID playerUUID, UUID clanLeaderUUID, boolean asOwner, boolean remove) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        for (World world : Bukkit.getWorlds()) {
            RegionManager regionManager = container.get(BukkitAdapter.adapt(world));
            if (regionManager == null) {
                continue;
            }
            for (ProtectedRegion region : regionManager.getRegions().values()) {
                // Проверяем, принадлежит ли регион лидеру (то есть клану)
                if (region.getOwners().contains(clanLeaderUUID)) {
                    if (remove) {
                        // Удаляем игрока из Owners и Members
                        region.getOwners().removePlayer(playerUUID);
                        region.getMembers().removePlayer(playerUUID);
                    } else {
                        // Добавляем игрока в Owners или Members
                        if (asOwner) {
                            region.getOwners().addPlayer(playerUUID);
                        } else {
                            region.getMembers().addPlayer(playerUUID);
                        }
                    }
                }
            }
        }
    }

    /**
     * Удаляет регион из WorldGuard по его идентификатору.
     *
     * @param world    мир, в котором находится регион
     * @param regionId идентификатор региона
     */
    public void removeRegionById(World world, String regionId) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regionManager = container.get(BukkitAdapter.adapt(world));

        if (regionManager == null) {
            logger.warning("Не удалось получить RegionManager для мира " + world.getName());
            return;
        }

        if (!regionManager.hasRegion(regionId)) {
            logger.warning("Регион с ID " + regionId + " не найден в мире " + world.getName());
            return;
        }

        regionManager.removeRegion(regionId);
        logger.info("Регион " + regionId + " успешно удалён из мира " + world.getName());
    }

    public String getChunkId(Chunk chunk) {
        return chunk.getWorld().getName() + "_" + chunk.getX() + "_" + chunk.getZ();
    }

    /**
     * Проверяет, принадлежит ли чанк любому другому клану.
     */
    public boolean isClaimedByAnotherClan(String chunkId, Clan current) {
        // Пробегаем все кланы из clanManager
        for (Clan c : getAllClans()) {
            if (!c.equals(current) && c.hasClaimedChunk(chunkId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Удаляет регион по чанк + имя клана, если он есть.
     */
    public void removeWorldGuardRegion(Chunk chunk, String clanName) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager manager = container.get(BukkitAdapter.adapt(chunk.getWorld()));
        if (manager == null) return;

        String regionId = "clan_" + clanName + "_" + getChunkId(chunk);
        manager.removeRegion(regionId);
    }

    /**
     * Добавляет игрока в регионы клана в качестве Member.
     */
    public void addPlayerToClanRegionsAsMember(Player player, Clan clan) {
        UUID playerUUID = player.getUniqueId();
        UUID leaderUUID = Bukkit.getOfflinePlayer(clan.getOwner()).getUniqueId();
        updateRegionMembership(playerUUID, leaderUUID, false, false);
    }

    /**
     * Добавляет игрока в регионы клана в качестве Owner.
     */
    public void addPlayerToClanRegionsAsOwner(Player player, Clan clan) {
        UUID playerUUID = player.getUniqueId();
        UUID leaderUUID = Bukkit.getOfflinePlayer(clan.getOwner()).getUniqueId();
        updateRegionMembership(playerUUID, leaderUUID, true, false);
    }

    /**
     * Удаляет игрока из регионов клана (из Owners и Members).
     */
    public void removePlayerFromClanRegions(Player playerToRemove, Clan clan) {
        UUID playerUUID = playerToRemove.getUniqueId();
        // Если лидер онлайн, используем его онлайн-UUID; иначе офлайн
        Player leaderOnline = Bukkit.getPlayerExact(clan.getOwner());
        UUID leaderUUID = (leaderOnline != null)
                ? leaderOnline.getUniqueId()
                : Bukkit.getOfflinePlayer(clan.getOwner()).getUniqueId();

        updateRegionMembership(playerUUID, leaderUUID, false, true);
    }

    // ===========================================================================
    //   CRUD-операции с кланами + сохранение/загрузка
    // ===========================================================================

    /**
     * Создает новый клан с указанным именем и владельцем.
     */
    public void createClan(String name, String owner) {
        if (getClan(name) != null) {
            throw new IllegalArgumentException("Клан с таким названием уже существует.");
        }
        if (getPlayerClan(owner) != null) {
            throw new IllegalArgumentException("Вы уже участник клана.");
        }

        Date creationDate = new Date();
        Clan clan = new Clan(
                name,
                owner,
                new HashSet<>(Collections.singleton(owner)),
                new HashMap<>(Map.of(owner, "Лидер")),
                creationDate,
                "",
                new ArrayList<>(),  // alliances
                0,                  // level
                0,                  // xp
                0.0,                // balance
                0,                  // land
                0,                  // strength
                10,                 // maxPower
                new ArrayList<>()   // claimedChunks
        );

        clans.put(name.toLowerCase(), clan);
        clanDao.insertClan(clan);
    }

    /**
     * Полностью удаляет (дисбандит) клан.
     */
    public void disbandClan(String clanName) {
        Clan clan = getClan(clanName);
        if (clan == null) {
            throw new IllegalArgumentException("Клан не существует.");
        }
        clans.remove(clanName.toLowerCase());
        clanDao.deleteClan(clanName);
    }

    /**
     * Загружает кланы из БД в локальный кэш.
     */
    public void loadClans() {
        List<Clan> loadedClans = clanDao.getAllClans();
        clans.clear();
        for (Clan c : loadedClans) {
            clans.put(c.getName().toLowerCase(), c);
        }
    }

    /**
     * Сохраняет (insert/update) один клан в БД.
     */
    public void saveClan(Clan clan) {
        // Проверяем, есть ли клан в кэше
        Clan existing = getClan(clan.getName());
        if (existing == null) {
            clanDao.insertClan(clan);
        } else {
            clanDao.updateClan(clan);
        }
    }

    /**
     * Сохраняет все кланы из кэша.
     */
    public void saveAllClans() {
        clans.values().forEach(this::saveClan);
    }

    // ===========================================================================
    //   Поиск кланов
    // ===========================================================================

    /**
     * Находит клан по названию (регистронезависимо).
     */
    public Clan getClan(String name) {
        if (name == null) return null;
        return clans.get(name.toLowerCase());
    }

    /**
     * Ищет клан, в котором состоит указанный игрок,
     * используя Stream API вместо цикла for.
     */
    public Clan getPlayerClan(String playerName) {
        return clans.values().stream()
                .filter(clan -> clan.getMembers().contains(playerName))
                .findFirst()
                .orElse(null);
    }

    /**
     * Возвращает все кланы.
     */
    public Collection<Clan> getAllClans() {
        return clans.values();
    }

    /**
     * Ищет клан, в котором состоит игрок (аналог getPlayerClan).
     */
    public Clan getClanByPlayer(String playerName) {
        return getPlayerClan(playerName);
    }

    /**
     * Находит клан, которому принадлежит данный чанк (по ключу "world:x:z").
     */
    public Clan getClanByChunk(Chunk chunk) {
        String chunkKey = chunk.getWorld().getName() + ":" + chunk.getX() + ":" + chunk.getZ();
        return clans.values().stream()
                .filter(c -> c.getRegionNames().contains(chunkKey))
                .findFirst()
                .orElse(null);
    }

    // ===========================================================================
    //   Приглашения, вступление, выход
    // ===========================================================================

    /**
     * Приглашает игрока в клан (через InvitationDao).
     */
    public void invitePlayer(String clanName, String playerName) {
        Clan clan = getClan(clanName);
        if (clan == null) {
            throw new IllegalArgumentException("Клан не существует.");
        }
        if (clan.getMembers().contains(playerName)) {
            throw new IllegalArgumentException("Игрок уже в клане.");
        }
        if (clan.isFull()) {
            throw new IllegalArgumentException("Клан достиг максимального числа участников.");
        }

        Player player = Bukkit.getPlayer(playerName);
        if (player == null || !player.isOnline()) {
            throw new IllegalArgumentException("Игрок не онлайн.");
        }

        Set<String> invitations = invitationDao.getInvitationsForPlayer(playerName);
        if (!invitations.contains(clanName.toLowerCase())) {
            invitationDao.saveInvitation(playerName, clanName.toLowerCase());
        }
    }

    /**
     * Позволяет игроку вступить в клан, если у него есть приглашение.
     */
    public void joinClan(String clanName, String playerName) {
        Clan clan = getClan(clanName);
        if (clan == null) {
            throw new IllegalArgumentException("Клан не существует.");
        }

        // Проверяем, не в том ли он уже клане
        Clan playerClan = getPlayerClan(playerName);
        if (playerClan != null && playerClan.getName().equalsIgnoreCase(clan.getName())) {
            throw new IllegalArgumentException("Вы уже в этом клане.");
        }

        // Проверяем приглашение
        Set<String> invitations = invitationDao.getInvitationsForPlayer(playerName);
        if (!invitations.contains(clanName.toLowerCase())) {
            throw new IllegalArgumentException("У вас нет приглашения в этот клан.");
        }

        // Удаляем приглашение после принятия
        invitationDao.removeInvitation(playerName, clanName.toLowerCase());

        // Если игрок уже был в другом клане — убираем оттуда
        if (playerClan != null) {
            playerClan.removeMember(playerName);
            saveClan(playerClan);
        }

        // Проверяем лимит участников
        if (clan.isFull()) {
            throw new IllegalArgumentException("Клан достиг максимального числа участников.");
        }

        // Добавляем игрока
        clan.addMember(playerName);
        saveClan(clan);
    }

    /**
     * Игрок покидает клан. Если он лидер и в клане есть люди,
     * нужно сначала передать лидерку или распустить клан.
     */
    public void leaveClan(String playerName) {
        Clan playerClan = getPlayerClan(playerName);
        if (playerClan == null) {
            throw new IllegalArgumentException("Вы не состоите ни в каком клане.");
        }

        // Проверяем, не лидер ли это
        if (playerClan.getOwner().equals(playerName)) {
            // Если еще есть участники, лидер не может уйти
            if (playerClan.getMembers().size() > 1) {
                throw new IllegalArgumentException("Лидер не может покинуть клан, пока в нём есть другие участники.");
            } else {
                // Если он один — распускаем клан
                disbandClan(playerClan.getName());
            }
        } else {
            // Обычный участник просто покидает клан
            playerClan.removeMember(playerName);
            saveClan(playerClan);
        }
    }

    // ===========================================================================
    //   PvP и союз/вражда
    // ===========================================================================

    /**
     * Проверяем, является ли блок вражеской территорией.
     */
    public boolean isEnemyTerritory(Player player, Block block) {
        Clan blockClan = getClanByChunk(block.getChunk());
        if (blockClan == null) {
            // Если чанк не принадлежит ни одному клану — не враг
            return false;
        }
        Clan playerClan = getPlayerClan(player.getName());
        if (playerClan == null) {
            // Игрок без клана => все кланы враги
            return true;
        }
        if (playerClan.equals(blockClan)) {
            // Свой клан
            return false;
        }
        // Проверяем, нет ли союза
        boolean allied = (playerClan.getAlliances() != null && playerClan.getAlliances().contains(blockClan.getName()))
                || (blockClan.getAlliances() != null && blockClan.getAlliances().contains(playerClan.getName()));
        return !allied;
    }

    /**
     * Упрощённая логика проверки, являются ли 2 игрока врагами.
     */
    public boolean isEnemyPlayers(Player p1, Player p2) {
        Clan c1 = getPlayerClan(p1.getName());
        Clan c2 = getPlayerClan(p2.getName());

        // Оба без кланов => не враги
        if (c1 == null && c2 == null) return false;
        // Один без клана => враги
        if (c1 == null || c2 == null) return true;
        // Один клан => не враги
        if (c1.equals(c2)) return false;
        // Есть ли союз
        if (c1.getAlliances().contains(c2.getName()) || c2.getAlliances().contains(c1.getName())) {
            return false;
        }
        // Если всё остальное не сработало => враги
        return true;
    }

    // ===========================================================================
    //   Прочее
    // ===========================================================================

    /**
     * Проверяет, является ли игрок лидером/заместителем заданного клана.
     */
    public boolean isLeaderOrDeputy(Clan clan, Player player) {
        if (clan == null) return false;
        String role = clan.getRole(player.getName());
        return "Лидер".equals(role) || "Заместитель".equals(role);
    }

    /**
     * Обновляет силу клана на основе данных игрока.
     */
    public void updateStrengthForPlayer(String playerName, PlayerDataHandler playerDataHandler) {
        try {
            Clan clan = getClanByPlayer(playerName);
            if (clan != null) {
                clan.updateStrength(playerDataHandler);
                saveClan(clan);
            }
        } catch (Exception e) {
            logger.warning("Ошибка при обновлении силы клана: " + e.getMessage());
        }
    }

    /**
     * Обновляет клан (например, при переименовании), если имя поменялось — удаляем старый ключ.
     */
    public void updateClan(Clan clan) {
        String oldName = clan.getOldName();
        if (oldName != null && !oldName.equalsIgnoreCase(clan.getName())) {
            clans.remove(oldName.toLowerCase());
            clanDao.deleteClan(oldName);
        }
        clans.put(clan.getName().toLowerCase(), clan);
        saveClan(clan);
        clan.resetOldName();
    }

    /**
     * Возвращает карту (все кланы).
     */
    public Map<String, Clan> getClans() {
        return clans;
    }
}
