package org.flomik.flomiksFactions.commands.clan;

import org.bukkit.Location;

import java.util.*;

public class Clan {
    private static final int MAX_MEMBERS = 15; // Максимальное количество участников в клане
    private static final List<String> ROLE_ORDER = Arrays.asList("Рядовой", "Воин", "Заместитель", "Лидер");

    private final Map<String, String> memberRoles; // Словарь ролей игроков
    private final String name;
    private String owner;
    private final Set<String> members;
    private final Date creationDate; // Дата создания клана
    private final String description; // Описание клана
    private final List<String> alliances; // Альянсы клана
    private final int level; // Уровень клана
    private int land; // Земли клана
    private int strength; // Сила клана
    private final int maxPower; // Максимальная сила клана
    private Location home;

    // Конструктор с maxPower
    public Clan(String name, String owner, Set<String> members, Map<String, String> memberRoles, Date creationDate, String description, List<String> alliances, int level, int land, int strength, int maxPower) {
        this.name = name;
        this.owner = owner;
        this.members = new HashSet<>(members); // Используем HashSet для хранения участников
        this.memberRoles = new HashMap<>(memberRoles); // Используем HashMap для хранения ролей
        this.creationDate = creationDate;
        this.description = description;
        this.alliances = new ArrayList<>(alliances); // Используем ArrayList для альянсов
        this.level = level;
        this.land = land;
        this.strength = strength;
        this.maxPower = maxPower; // Используем переданное значение maxPower
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getRole(String playerName) {
        return memberRoles.getOrDefault(playerName, "Не состоит в клане");
    }

    public void setRole(String playerName, String role) {
        if (ROLE_ORDER.contains(role)) {
            memberRoles.put(playerName, role);
        }
    }

    public void promoteMember(String playerName) {
        if (playerName.equals(owner)) {
            throw new IllegalArgumentException("Лидер клана не может изменить свою роль.");
        }

        String currentRole = getRole(playerName);
        int currentIndex = ROLE_ORDER.indexOf(currentRole);

        if (currentIndex == -1) {
            throw new IllegalArgumentException("Неверная роль для повышения.");
        }

        if (currentIndex == 2){
            throw new IllegalArgumentException("Игрок уже имеет наивысшую роль.");
        }

        if (currentIndex < ROLE_ORDER.size() - 1) {
            String newRole = ROLE_ORDER.get(currentIndex + 1);
            setRole(playerName, newRole);
        }
    }

    public void demoteMember(String playerName) {
        if (playerName.equals(owner)) {
            throw new IllegalArgumentException("Лидер клана не может изменить свою роль.");
        }

        String currentRole = getRole(playerName);
        int currentIndex = ROLE_ORDER.indexOf(currentRole);

        if (currentIndex == -1) {
            throw new IllegalArgumentException("Неверная роль для понижения.");
        }

        if (currentIndex > 0) {
            String newRole = ROLE_ORDER.get(currentIndex - 1);
            setRole(playerName, newRole);
        } else {
            throw new IllegalArgumentException("Игрок уже имеет минимальную роль.");
        }
    }

    public Map<String, String> getRoles() {
        return new HashMap<>(memberRoles);
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
        memberRoles.putIfAbsent(player, "Рядовой"); // При добавлении нового участника роль по умолчанию "Рядовой"
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

    public int getLand() {
        return land;
    }

    public void setLand(int land) {
        this.land = land;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }
}