package org.flomik.flomiksFactions.commands.clan;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.flomik.flomiksFactions.FlomiksFactions;
import org.flomik.flomiksFactions.commands.player.PlayerDataHandler;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ClanManager {

    private PlayerDataHandler playerDataHandler;
    private final File file;
    private final FileConfiguration config;
    public Map<String, Clan> clans = new HashMap<>();
    private final Map<String, Set<String>> invitations = new HashMap<>();

    public ClanManager(FlomiksFactions plugin) {
        this.file = new File(plugin.getDataFolder(), "clans.yml");
        this.config = YamlConfiguration.loadConfiguration(file);
        loadClans();
    }

    public void addPlayerToClanRegionsAsMember(Player player, Clan clan) {
        // Получаем UUID игрока
        UUID newMemberUUID = player.getUniqueId();

        // Получаем UUID главы клана
        String clanLeaderName = clan.getOwner(); // Предполагается, что есть метод для получения имени главы клана
        UUID clanLeaderUUID = Bukkit.getOfflinePlayer(clanLeaderName).getUniqueId();

        // Проходим по всем мирам сервера
        for (World world : Bukkit.getWorlds()) {
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionManager regionManager = container.get(BukkitAdapter.adapt(world));

            if (regionManager != null) {
                // Ищем все регионы, где глава клана является владельцем
                for (ProtectedRegion region : regionManager.getRegions().values()) {
                    if (region.getOwners().contains(clanLeaderUUID)) {
                        // Добавляем нового участника в регион как участника
                        region.getMembers().addPlayer(newMemberUUID);
                    }
                }
            }
        }
    }

    public void addPlayerToClanRegionsAsOwner(Player player, Clan clan) {
        // Получаем UUID игрока
        UUID newMemberUUID = player.getUniqueId();

        // Получаем UUID главы клана
        String clanLeaderName = clan.getOwner(); // Предполагается, что есть метод для получения имени главы клана
        UUID clanLeaderUUID = Bukkit.getOfflinePlayer(clanLeaderName).getUniqueId();

        // Проходим по всем мирам сервера
        for (World world : Bukkit.getWorlds()) {
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionManager regionManager = container.get(BukkitAdapter.adapt(world));

            if (regionManager != null) {
                // Ищем все регионы, где глава клана является владельцем
                for (ProtectedRegion region : regionManager.getRegions().values()) {
                    if (region.getOwners().contains(clanLeaderUUID)) {
                        // Добавляем нового участника в регион как участника
                        region.getOwners().addPlayer(newMemberUUID);
                    }
                }
            }
        }
    }

    public void removePlayerFromClanRegions(Player playerToRemove, Clan clan) {
        // Получаем UUID игрока, которого нужно удалить
        UUID playerToRemoveUUID = playerToRemove.getUniqueId();

        // Получаем UUID главы клана
        String clanLeaderName = clan.getOwner(); // Предполагается, что есть метод для получения имени главы клана

        UUID clanOfflineLeaderUUID = Bukkit.getOfflinePlayer(clanLeaderName).getUniqueId();
        UUID clanLeaderUUID = Bukkit.getPlayerExact(clanLeaderName).getUniqueId();

        // Проходим по всем мирам сервера
        for (World world : Bukkit.getWorlds()) {
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionManager regionManager = container.get(BukkitAdapter.adapt(world));

            if (regionManager != null) {
                // Ищем все регионы, где глава клана является владельцем
                for (ProtectedRegion region : regionManager.getRegions().values()) {
                    if (region.getOwners().contains(clanLeaderUUID) || region.getOwners().contains(clanOfflineLeaderUUID)) {
                        // Если игрок есть в списке владельцев, удаляем его
                        if (region.getOwners().contains(playerToRemoveUUID)) {
                            region.getOwners().removePlayer(playerToRemoveUUID);
                        }
                        // Если игрок есть в списке участников, удаляем его
                        if (region.getMembers().contains(playerToRemoveUUID)) {
                            region.getMembers().removePlayer(playerToRemoveUUID);
                        }
                    }
                }
            }
        }
    }

    public void createClan(String name, String owner) {
        name = name.toLowerCase(); // Приведение названия клана к нижнему регистру

        // Проверка, существует ли уже клан с таким именем
        if (clans.containsKey(name)) {
            throw new IllegalArgumentException("Клан с таким названием уже существует.");
        }

        // Проверка, состоит ли игрок уже в каком-либо клане
        if (getPlayerClan(owner) != null) {
            throw new IllegalArgumentException("Вы уже участник клана.");
        }
        // Параметры по умолчанию
        Date creationDate = new Date(); // Дата создания — текущая дата
        String description = ""; // Описание по умолчанию
        List<String> alliances = new ArrayList<>(); // Пустой список альянсов по умолчанию
        int level = 1; // Начальный уровень клана
        int land = 0; // Начальное количество земель
        int strength = 0; // Начальная сила
        int maxPower = 10; // Начальная максимальная сила (может быть вычислена позднее)

        // Инициализация коллекций
        Set<String> members = new HashSet<>();
        Map<String, String> memberRoles = new HashMap<>();

        List<String> claimedChunks = new ArrayList<>();

        // Добавление владельца в список участников и назначение ему роли
        members.add(owner);
        memberRoles.put(owner, "Лидер");

        // Создание нового клана
        Clan clan = new Clan(name, owner, members, memberRoles, creationDate, description, alliances, level, land, strength, maxPower, claimedChunks);

        // Добавление клана в коллекцию и сохранение
        clans.put(name, clan);
        saveClan(clan);
    }


    public Clan getClan(String name) {
        return clans.get(name.toLowerCase()); // Приведение названия клана к нижнему регистру
    }

    public Clan getPlayerClan(String playerName) {
        for (Clan clan : clans.values()) {
            if (clan.getMembers().contains(playerName)) {
                return clan;
            }
        }
        return null;
    }

    public void invitePlayer(String clanName, String playerName) {
        clanName = clanName.toLowerCase();
        Clan clan = getClan(clanName);

        if (clan == null) {
            throw new IllegalArgumentException("Клан не существует.");
        }

        // Проверка, что игрок не состоит уже в этом клане
        if (clan.getMembers().contains(playerName)) {
            throw new IllegalArgumentException("Игрок уже состоит в этом клане.");
        }

        // Проверка на максимальное количество участников в клане
        if (clan.isFull()) {
            throw new IllegalArgumentException("Клан уже достиг максимального количества участников.");
        }

        // Проверка, что игрок онлайн
        Player player = Bukkit.getPlayer(playerName);
        if (player == null || !player.isOnline()) {
            throw new IllegalArgumentException("Игрок не онлайн.");
        }

        if (!invitations.containsKey(playerName)) {
            invitations.put(playerName, new HashSet<>());
        }
        invitations.get(playerName).add(clanName);
        saveInvitations();
    }

    // Метод для поиска клана по игроку
    public Clan getClanByPlayer(String playerName) {
        for (Clan clan : clans.values()) {
            if (clan.getMembers().contains(playerName)) {
                return clan;
            }
        }
        return null;
    }

    // Метод для обновления силы клана по нику игрока
    public void updateStrengthForPlayer(String playerName, PlayerDataHandler playerDataHandler) {
        Clan clan = getClanByPlayer(playerName);
        if (clan != null) {
            clan.updateStrength(playerDataHandler);
        }
    }

    public void disbandClan(String clanName) {
        clanName = clanName.toLowerCase();
        Clan clan = getClan(clanName);
        if (clan == null) {
            throw new IllegalArgumentException("Клан не существует.");
        }



        clans.remove(clanName);
        config.set("clans." + clanName, null);
        saveConfig(); // Сохраняем изменения
    }


    public void joinClan(String clanName, String playerName) {
        clanName = clanName.toLowerCase();
        Clan clan = getClan(clanName);
        if (clan == null) {
            throw new IllegalArgumentException("Клан не существует.");
        }

        // Проверка на попытку присоединиться к собственному клану
        Clan playerClan = getPlayerClan(playerName);
        if (playerClan != null && playerClan.getName().equals(clanName)) {
            throw new IllegalArgumentException("Вы уже состоите в этом клане.");
        }

        Set<String> playerInvitations = invitations.get(playerName);
        if (playerInvitations == null || !playerInvitations.contains(clanName)) {
            throw new IllegalArgumentException("Вы не получили приглашение в этот клан.");
        }
        playerInvitations.remove(clanName);
        if (playerInvitations.isEmpty()) {
            invitations.remove(playerName);
        } else {
            saveInvitations();
        }

        // Удаляем игрока из предыдущего клана, если он состоит в каком-либо
        if (playerClan != null) {
            playerClan.removeMember(playerName);
            saveClan(playerClan);
        }

        // Проверка на максимальное количество участников в клане перед добавлением
        if (clan.isFull()) {
            throw new IllegalArgumentException("Клан уже достиг максимального количества участников.");
        }

        // Добавляем игрока в новый клан
        clan.addMember(playerName);
        saveClan(clan);
    }

    public Collection<Clan> getAllClans() {
        return clans.values();
    }

    // Обновление данных о клане
    public void updateClan(Clan clan) {
        // Удаляем старое название клана из карты и конфигурации, если название изменилось
        String oldName = clan.getOldName();
        if (oldName != null && !oldName.equals(clan.getName())) {
            clans.remove(oldName.toLowerCase());
            config.set("clans." + oldName.toLowerCase(), null); // Удаляем старое название из конфигурации
        }

        // Обновляем карту кланов с новым названием
        clans.put(clan.getName(), clan);

        // Сохраняем обновленные данные о клане
        saveClan(clan);
    }


    public Map<String, Clan> getClans() {
        return clans;
    }

    public void saveClan(Clan clan) {
        String path = "clans." + clan.getName().toLowerCase(); // Приведение названия клана к нижнему регистру

        // Сохранение данных о клане
        config.set(path + ".owner", clan.getOwner()); // Сохранение владельца клана
        config.set(path + ".creationDate", clan.getCreationDate().getTime()); // Сохранение даты создания клана в виде миллисекунд
        config.set(path + ".description", clan.getDescription()); // Сохранение описания клана
        config.set(path + ".land", clan.getLands()); // Сохранение количества земель клана
        config.set(path + ".strength", clan.getStrength()); // Сохранение текущей силы клана
        config.set(path + ".alliances", new ArrayList<>(clan.getAlliances())); // Сохранение списка альянсов
        config.set(path + ".level", clan.getLevel()); // Сохранение уровня клана
        config.set(path + ".maxPower", clan.getMaxPower()); // Сохранение максимальной силы клана

        config.set(path + ".members", new ArrayList<>(clan.getMembers())); // Сохранение списка членов клана

        // Сохранение ролей членов клана
        Map<String, String> memberRoles = new HashMap<>();
        for (String member : clan.getMembers()) {
            String role = clan.getRole(member); // Получение роли текущего члена клана
            memberRoles.put(member, role); // Добавление пары "игрок-роль" в мапу
        }
        config.set(path + ".roles", memberRoles); // Сохранение ролей членов клана в конфигурации

        // Сохранение точки дома клана
        if (clan.hasHome()) {
            Location home = clan.getHome();
            config.set(path + ".home.world", home.getWorld().getName());
            config.set(path + ".home.x", home.getX());
            config.set(path + ".home.y", home.getY());
            config.set(path + ".home.z", home.getZ());
            config.set(path + ".home.yaw", home.getYaw());
            config.set(path + ".home.pitch", home.getPitch());
        } else {
            config.set(path + ".home", null); // Удаление старой точки дома
        }

        List<String> chunks = clan.getRegionNames();
        config.set(path + ".chunks", chunks);
        saveConfig(); // Сохранение конфигурации
    }

    public void loadClans() {
        if (config.contains("clans")) {
            for (String clanName : config.getConfigurationSection("clans").getKeys(false)) {
                String lowerCaseClanName = clanName.toLowerCase(); // Приведение названия клана к нижнему регистру

                // Загрузка владельца
                String owner = config.getString("clans." + lowerCaseClanName + ".owner");

                // Загрузка участников и их ролей
                List<String> members = config.getStringList("clans." + lowerCaseClanName + ".members");
                Set<String> memberSet = new HashSet<>(members); // Преобразование списка в Set
                Map<String, String> memberRoles = new HashMap<>();
                ConfigurationSection rolesSection = config.getConfigurationSection("clans." + lowerCaseClanName + ".roles");
                if (rolesSection != null) {
                    for (String key : rolesSection.getKeys(false)) {
                        String role = rolesSection.getString(key);
                        memberRoles.put(key, role); // Загрузка роли члена
                    }
                }

                // Загрузка остальных данных
                Date creationDate = new Date(config.getLong("clans." + lowerCaseClanName + ".creationDate")); // Преобразование миллисекунд в дату
                String description = config.getString("clans." + lowerCaseClanName + ".description", "");
                List<String> alliances = config.getStringList("clans." + lowerCaseClanName + ".alliances");
                int level = config.getInt("clans." + lowerCaseClanName + ".level", 1);
                int land = config.getInt("clans." + lowerCaseClanName + ".land", 0);
                int strength = config.getInt("clans." + lowerCaseClanName + ".strength", 0);
                int maxPower = config.getInt("clans." + lowerCaseClanName + ".maxPower", memberSet.size() * 10); // Задаём maxPower как размер членов клана * 10, если нет сохранённого значения
                List<String> chunks = config.getStringList("clans." + lowerCaseClanName + ".chunks");

                // Загрузка точки дома
                Location home = null;
                if (config.contains("clans." + lowerCaseClanName + ".home")) {
                    String worldName = config.getString("clans." + lowerCaseClanName + ".home.world");
                    double x = config.getDouble("clans." + lowerCaseClanName + ".home.x");
                    double y = config.getDouble("clans." + lowerCaseClanName + ".home.y");
                    double z = config.getDouble("clans." + lowerCaseClanName + ".home.z");
                    float yaw = (float) config.getDouble("clans." + lowerCaseClanName + ".home.yaw");
                    float pitch = (float) config.getDouble("clans." + lowerCaseClanName + ".home.pitch");

                    World world = Bukkit.getWorld(worldName);
                    if (world != null) {
                        home = new Location(world, x, y, z, yaw, pitch);
                    }
                }

                // Создание объекта клана
                Clan clan = new Clan(
                        lowerCaseClanName, owner, memberSet, memberRoles, creationDate, description, alliances, level, land, strength, maxPower, chunks
                );
                clan.setHome(home);
                clans.put(lowerCaseClanName, clan);
            }
        }
        loadInvitations(); // Загрузка приглашений (если это необходимо)
    }

    public void saveConfig() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void leaveClan(String playerName) {
        Clan playerClan = getPlayerClan(playerName);
        if (playerClan == null) {
            throw new IllegalArgumentException("Вы не состоите в каком-либо клане.");
        }

        if (playerClan.getOwner().equals(playerName)) {
            if (playerClan.getMembers().size() > 1) {
                throw new IllegalArgumentException("Лидер клана не может покинуть клан, пока в нем есть другие участники. Передайте руководство или распустите клан.");
            } else {
                disbandClan(playerClan.getName());
            }
        } else {
            playerClan.removeMember(playerName);
            saveClan(playerClan);
        }
    }

    public void saveAllClans() {
        for (Clan clan : clans.values()) {
            saveClan(clan);
        }
    }

    private void loadInvitations() {
        if (config.contains("invitations")) {
            for (String playerName : config.getConfigurationSection("invitations").getKeys(false)) {
                List<String> invitationsList = config.getStringList("invitations." + playerName);
                invitations.put(playerName, new HashSet<>(invitationsList));
            }
        }
    }

    private void saveInvitations() {
        config.set("invitations", null); // Сначала очищаем старые данные
        for (Map.Entry<String, Set<String>> entry : invitations.entrySet()) {
            config.set("invitations." + entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        saveConfig(); // Сохраняем изменения после обновления
    }
}