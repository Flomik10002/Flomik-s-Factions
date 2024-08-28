package org.flomik.flomiksFactions.commands.clan;

import java.util.HashSet;
import java.util.Set;

public class Clan {
    private static final int MAX_MEMBERS = 15; // Максимальное количество участников в клане

    private final String name;
    private final String owner;
    private final Set<String> members;

    public Clan(String name, String owner) {
        this.name = name;
        this.owner = owner;
        this.members = new HashSet<>();
        this.members.add(owner); // Владелец по умолчанию добавляется как участник
    }

    public Clan(String name, String owner, Set<String> members) {
        this.name = name;
        this.owner = owner;
        this.members = members;
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

    public void removeMember(String player) {
        members.remove(player);
    }

    // Новый метод для проверки, достигнут ли максимальный лимит участников
    public boolean isFull() {
        return members.size() >= MAX_MEMBERS;
    }

    public int getMaxPower() {
        return members.size() * 10; // Максимально допустимая сила: 10 на игрока
    }
}
