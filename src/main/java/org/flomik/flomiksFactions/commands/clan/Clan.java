package org.flomik.flomiksFactions.commands.clan;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class Clan {
    private static final int MAX_MEMBERS = 15; // Максимальное количество участников в клане

    private final Map<String, String> memberRoles; // Словарь ролей игроков
    private final String name;
    public String owner;
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
        this.maxPower = members.size() * 10; // Используем переданное значение maxPower
    }

    public void setOwner(String newOwner) {
        // Проверяем, что новый владелец действительно является членом клана
        if (!members.contains(newOwner)) {
            throw new IllegalArgumentException("Игрок не является членом клана.");
        }

        // Обновляем владельца
        this.owner = newOwner;
    }

    public List<String> getAlliances() {
        return new ArrayList<>(alliances);
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

    public String getRole(String playerName) {
        return memberRoles.getOrDefault(playerName, "Рядовой");
    }

    public void removeMember(String player) {
        members.remove(player);
        memberRoles.remove(player);
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    public Set<String> getMembers() {
        return members;
    }

    public void addMember(String player) {
        members.add(player);
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
