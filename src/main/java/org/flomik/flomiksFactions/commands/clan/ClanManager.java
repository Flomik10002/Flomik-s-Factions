package org.flomik.flomiksFactions.commands.clan;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.flomik.flomiksFactions.FlomiksFactions;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ClanManager {

    private final File file;
    private final FileConfiguration config;
    private final Map<String, Clan> clans = new HashMap<>();
    private final Map<String, Set<String>> invitations = new HashMap<>();

    public ClanManager(FlomiksFactions plugin) {
        this.file = new File(plugin.getDataFolder(), "clans.yml");
        this.config = YamlConfiguration.loadConfiguration(file);
        loadClans();
    }

    public void createClan(String name, String owner) {
        name = name.toLowerCase(); // Приведение названия клана к нижнему регистру
        if (clans.containsKey(name)) {
            throw new IllegalArgumentException("Клан с таким названием уже существует.");
        }
        if (getPlayerClan(owner) != null) {
            throw new IllegalArgumentException("Вы уже участник клана."); // Изменено сообщение об ошибке
        }
        Clan clan = new Clan(name, owner);
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

        if (!invitations.containsKey(playerName)) {
            invitations.put(playerName, new HashSet<>());
        }
        invitations.get(playerName).add(clanName);
        saveInvitations();
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

    public Map<String, Clan> getClans() {
        return clans;
    }

    public void saveClan(Clan clan) {
        String path = "clans." + clan.getName().toLowerCase(); // Приведение названия клана к нижнему регистру
        config.set(path + ".owner", clan.getOwner());
        config.set(path + ".members", new ArrayList<>(clan.getMembers()));
        saveConfig();
    }

    public void loadClans() {
        if (config.contains("clans")) {
            for (String clanName : config.getConfigurationSection("clans").getKeys(false)) {
                String lowerCaseClanName = clanName.toLowerCase(); // Приведение названия клана к нижнему регистру
                String owner = config.getString("clans." + lowerCaseClanName + ".owner");
                List<String> membersList = config.getStringList("clans." + lowerCaseClanName + ".members");
                Set<String> members = new HashSet<>(membersList);
                Clan clan = new Clan(lowerCaseClanName, owner, members);
                clans.put(lowerCaseClanName, clan);
            }
        }
        loadInvitations();
    }

    public void saveConfig() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
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
        for (Map.Entry<String, Set<String>> entry : invitations.entrySet()) {
            config.set("invitations." + entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        saveConfig();
    }
}
