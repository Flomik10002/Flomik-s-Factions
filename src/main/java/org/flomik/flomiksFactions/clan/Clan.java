package org.flomik.flomiksFactions.clan;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.flomik.flomiksFactions.player.PlayerDataHandler;

import java.util.*;

public class Clan {
    private PlayerDataHandler playerDataHandler;
    private static final int MAX_MEMBERS = 15; // Максимальное количество участников в клане
    private static final List<String> ROLE_ORDER = Arrays.asList("Рекрут", "Воин", "Заместитель", "Лидер");

    private final Map<String, String> memberRoles; // Словарь ролей игроков
    private String name;
    private String oldName;
    private String owner;
    private final Set<String> members;
    private final Date creationDate; // Дата создания клана
    private String description; // Описание клана
    private final List<String> alliances; // Альянсы клана
    private int level; // Уровень клана
    private int clanXp; // Уровень клана
    private int lands; // Земли клана
    private int strength; // Сила клана
    private final int maxPower; // Максимальная сила клана
    private final List<String> claimedChunks;
    private Location home;

    //Конструктор без clanManager
    public Clan(String name, String owner, Set<String> members, Map<String, String> memberRoles, Date creationDate, String description, List<String> alliances, int level, int clanXp, int lands, int strength, int maxPower, List<String> claimedChunks) {
        this.name = name;
        this.oldName = null;
        this.owner = owner;
        this.members = new HashSet<>(members); // Используем HashSet для хранения участников
        this.memberRoles = new HashMap<>(memberRoles); // Используем HashMap для хранения ролей
        this.creationDate = creationDate;
        this.description = description;
        this.alliances = new ArrayList<>(alliances); // Используем ArrayList для альянсов
        this.level = level;
        this.clanXp = clanXp;
        this.lands = lands;
        this.strength = strength;
        this.maxPower = maxPower; // Используем переданное значение maxPower
        this.claimedChunks = new ArrayList<>(claimedChunks); // Инициализация
        this.playerDataHandler = playerDataHandler;
    }

    // Метод для получения необходимого опыта на следующий уровень
    public int getRequiredXpForNextLevel(int currentLevel) {
        if (currentLevel <= 0) {
            return 7; // Для 1 уровня требуется 7 XP
        }

        // Рассчитываем XP для следующего уровня
        int requiredXp = 7;
        for (int level = 1; level < currentLevel; level++) {
            requiredXp += 7 + (level - 1) * 2;
        }

        return requiredXp;
    }

    // Метод для добавления опыта клану
    public void addClanXp(int xp) {
        this.clanXp += xp;

        // Проверяем, не достигли ли мы нового уровня
        while (clanXp >= getRequiredXpForNextLevel(level)) {
            clanXp -= getRequiredXpForNextLevel(level);
            level++;
            Bukkit.broadcastMessage(ChatColor.GREEN + "Клан " + getName() + " достиг уровня " + level + "!");
        }
    }


    // Получить текущее количество опыта клана
    public int getClanXp() {
        return clanXp;
    }

    // Метод для проверки, запривачен ли чанк данным кланом
    public boolean hasClaimedChunk(String chunkId) {
        return claimedChunks.contains(chunkId);
    }

    // Метод для добавления чанка в список занятых чанков
    public void addClaimedChunk(String chunkId) {
        claimedChunks.add(chunkId);
    }

    // Метод для удаления чанка из списка занятых чанков
    public void removeClaimedChunk(String chunkId) {
        claimedChunks.remove(chunkId);
    }

    public void clearClaimedChunks() {
        claimedChunks.clear();
    }


    public void updateStrength(PlayerDataHandler playerDataHandler) {
        int totalStrength = 0;

        for (String member : members) {
            // Получаем силу игрока из PlayerDataHandler
            int playerStrength = playerDataHandler.getPlayerStrength(member);
            totalStrength += playerStrength;
        }

        // Устанавливаем новую силу клана
        this.strength = totalStrength;
    }

    public void removeAllianceByName(String clanName) {
        alliances.remove(clanName); // Удаляем альянс по имени
    }

    public void addAllianceByName(String clanName) {
        alliances.add(clanName); // Добавляем новый альянс по имени
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwner() {
        return owner;
    }

    public void renameClan(String newName) {
        oldName = name; // Сохраняем старое название
        name = newName; // Устанавливаем новое название
    }

    public String getOldName() {
        return oldName;
    }

    public void resetOldName() {
        this.oldName = null;
    }

    public void transferLeadership(String newLeader) {
        if (!members.contains(newLeader)) {
            throw new IllegalArgumentException("Игрок не является членом клана.");
        }

        // Проверка, что новый лидер не является текущим лидером
        if (newLeader.equals(owner)) {
            throw new IllegalArgumentException("Вы уже являетесь лидером клана.");
        }

        // Устанавливаем нового лидера и обновляем роли
        memberRoles.put(owner, "Заместитель"); // Бывшему лидеру присваивается роль Заместитель
        memberRoles.put(newLeader, "Лидер"); // Новому лидеру присваивается роль Лидер
        owner = newLeader; // Обновляем владельца клана
    }


    public List<String> getPlayersWithRole(String role) {
        List<String> playersWithRole = new ArrayList<>();

        // Проверяем, что указанная роль существует в списке ролей
        if (ROLE_ORDER.contains(role)) {
            for (Map.Entry<String, String> entry : memberRoles.entrySet()) {
                if (entry.getValue().equals(role)) {
                    playersWithRole.add(entry.getKey());
                }
            }
        }

        return playersWithRole;
    }


    public String getRole(String playerName) {
        return memberRoles.getOrDefault(playerName, "Не состоит в клане");
    }

    public void setRole(String playerName, String role) {
        if (ROLE_ORDER.contains(role)) {
            memberRoles.put(playerName, role);
        } else {
            throw new IllegalArgumentException("Неверная роль: " + role);
        }
    }

    public void promoteMember(String promotingPlayer, String targetPlayer) {
        // Получаем роль игрока, который делает повышение
        String promotingPlayerRole = getRole(promotingPlayer);

        // Проверка, что только Лидер или Заместитель могут повышать роли
        if (!promotingPlayerRole.equals("Лидер") && !promotingPlayerRole.equals("Заместитель")) {
            throw new IllegalArgumentException("У вас нет прав для повышения других участников.");
        }

        // Проверка, что Лидер не может изменить свою роль
        if (targetPlayer.equals(owner)) {
            throw new IllegalArgumentException("Роль Лидера клана невозможно изменить.");
        }

        // Получаем текущую роль целевого игрока и её индекс
        String currentRole = getRole(targetPlayer);
        int currentIndex = ROLE_ORDER.indexOf(currentRole);

        // Проверка на валидность текущей роли
        if (currentIndex == -1) {
            throw new IllegalArgumentException("Неверная роль для повышения.");
        }

        if (currentIndex >= ROLE_ORDER.indexOf("Заместитель")) {
            throw new IllegalArgumentException("Игрок уже имеет наивысшую роль.");
        }

        // Проверка, что Заместитель не может повысить игрока до Заместителя
        if (promotingPlayerRole.equals("Заместитель") && currentIndex >= ROLE_ORDER.indexOf("Воин")) {
            throw new IllegalArgumentException("Заместитель может повысить игрока только до роли 'Воин'.");
        }

        // Определяем новую роль для повышения
        String newRole = ROLE_ORDER.get(currentIndex + 1);
        // Устанавливаем новую роль для игрока
        setRole(targetPlayer, newRole);
    }

    public void moderMember(String moderingPlayer, String targetPlayer) {
        // Получаем роль игрока, который делает повышение
        String moderingPlayerRole = getRole(moderingPlayer);

        // Проверка, что только Лидер может назначить Зама
        if (!moderingPlayerRole.equals("Лидер")) {
            throw new IllegalArgumentException("Только Лидер может добавить Заместителя.");
        }

        // Проверка, что Лидер не может изменить свою роль
        if (targetPlayer.equals(owner)) {
            throw new IllegalArgumentException("Роль Лидера клана невозможно изменить.");
        }

        // Получаем текущую роль целевого игрока и её индекс
        String currentRole = getRole(targetPlayer);
        int currentIndex = ROLE_ORDER.indexOf(currentRole);

        // Проверка на валидность текущей роли
        if (currentIndex == -1) {
            throw new IllegalArgumentException("Неверная роль для повышения.");
        }

        if (currentIndex >= ROLE_ORDER.indexOf("Заместитель")) {
            throw new IllegalArgumentException("Игрок уже имеет данную роль.");
        }

        // Определяем новую роль для повышения
        String newRole = ROLE_ORDER.get(2);
        // Устанавливаем новую роль для игрока
        setRole(targetPlayer, newRole);
    }

    public void demoteMember(String demotingPlayer, String targetPlayer) {
        // Check if the demoting player is the owner or has a Deputy role
        String demotingPlayerRole = getRole(demotingPlayer);

        if (!demotingPlayerRole.equals("Лидер") && !demotingPlayerRole.equals("Заместитель")) {
            throw new IllegalArgumentException("У вас нет прав для понижения других участников.");
        }

        if (targetPlayer.equals(owner)) {
            throw new IllegalArgumentException("Лидер клана не может изменить свою роль.");
        }

        String currentRole = getRole(targetPlayer);
        int currentIndex = ROLE_ORDER.indexOf(currentRole);

        if (currentIndex == -1) {
            throw new IllegalArgumentException("Неверная роль для понижения.");
        }

        if (currentIndex == 0) {
            throw new IllegalArgumentException("Игрок уже имеет минимальную роль.");
        }

        // Restrict Deputy from demoting higher than "Воин"
        if (demotingPlayerRole.equals("Заместитель") && currentIndex > 1) {
            throw new IllegalArgumentException("Заместитель может понизить игрока только до роли 'Рекрут'.");
        }

        // Demote to the previous role in the order
        String newRole = ROLE_ORDER.get(currentIndex - 1);
        setRole(targetPlayer, newRole);
    }

    public List<String> getRegionNames() {
        return claimedChunks;
    }

    public List<String> getAlliances() {
        return new ArrayList<>(alliances);
    }

    public void addAlliances(Clan clan) {
        alliances.add(clan.getName());
    }

    // Удаляем альянс
    public void removeAlliance(Clan clan) {
        alliances.remove(clan.getName());
    }

    public Location getHome() {
        return home;
    }

    public void setHome(Location home) {
        this.home = home;
    }

    public void removeHome() {
        this.home = null;
    }

    public boolean hasHome() {
        return home != null;
    }

    public void removeMember(String player) {
        members.remove(player);
        memberRoles.remove(player);
    }

    public String getName() {
        return name;
    }

    public Set<String> getMembers() {
        return new HashSet<>(members);
    }

    public void addMember(String player) {
        members.add(player);
        memberRoles.putIfAbsent(player, "Рекрут"); // При добавлении нового участника роль по умолчанию "Рекрут"
    }

    public boolean isFull() {
        return members.size() >= MAX_MEMBERS;
    }

    public int getMaxPower() {
        return maxPower; // Используем сохранённое значение
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public String getDescription() {
        return description;
    }

    public int getLevel() {
        return level;
    }

    public int getLands() {
        return lands;
    }

    public void setLands(int lands) {
        this.lands = lands;
    }

    public void updateLands() {
        this.lands = claimedChunks.size(); // Количество земель — это размер списка чанков
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }
}