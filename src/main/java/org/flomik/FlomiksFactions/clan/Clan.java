package org.flomik.FlomiksFactions.clan;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.flomik.FlomiksFactions.player.PlayerDataHandler;

import java.util.*;

public class Clan {
    private static final int MAX_MEMBERS = 15;
    private static final List<String> ROLE_ORDER = Arrays.asList("Рекрут", "Воин", "Заместитель", "Лидер");

    private final Map<String, String> memberRoles;
    private String name;
    private String oldName;
    private String owner;
    private final Set<String> members;
    private final Date creationDate;
    private String description;
    private final List<String> alliances;
    private int level;
    private int clanXp;
    private double balance;
    private int lands;
    private int strength;
    private final int maxPower;
    private final List<String> claimedChunks;
    private Location home;

    public Clan(String name, String owner, Set<String> members, Map<String, String> memberRoles,
                Date creationDate, String description, List<String> alliances, int level,
                int clanXp, double balance, int lands, int strength, int maxPower, List<String> claimedChunks) {
        this.name = name;
        this.oldName = null;
        this.owner = owner;
        this.members = new HashSet<>(members);
        this.memberRoles = new HashMap<>(memberRoles);
        this.creationDate = creationDate;
        this.description = description;
        this.alliances = new ArrayList<>(alliances);
        this.level = level;
        this.clanXp = clanXp;
        this.balance = 0.0;
        this.lands = lands;
        this.strength = strength;
        this.maxPower = maxPower;
        this.claimedChunks = new ArrayList<>(claimedChunks);
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double newBalance) {
        if (newBalance < 0) {
            throw new IllegalArgumentException("Баланс не может быть меньше 0!");
        }
        this.balance = newBalance;
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма должна быть больше 0.");
        }
        this.balance += amount;
    }

    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма должна быть больше 0.");
        }
        if (this.balance < amount) {
            throw new IllegalArgumentException("Недостаточно средств в казне клана.");
        }
        this.balance -= amount;
    }

    public int getRequiredXpForNextLevel(int currentLevel) {
        if (currentLevel <= 0) {
            return 7;
        }

        int requiredXp = 7;
        for (int lvl = 1; lvl < currentLevel; lvl++) {
            requiredXp += 7 + (lvl - 1) * 2;
        }
        return requiredXp;
    }

    public void addClanXp(int xp) {
        this.clanXp += xp;

        while (clanXp >= getRequiredXpForNextLevel(level)) {
            clanXp -= getRequiredXpForNextLevel(level);
            level++;
            Bukkit.broadcastMessage(ChatColor.GREEN + "Клан " + getName() + " достиг уровня " + level + "!");
        }
    }

    public int getClanXp() {
        return clanXp;
    }

    public boolean hasClaimedChunk(String chunkId) {
        return claimedChunks.contains(chunkId);
    }

    public void addClaimedChunk(String chunkId) {
        claimedChunks.add(chunkId);
    }

    public void removeClaimedChunk(String chunkId) {
        claimedChunks.remove(chunkId);
    }

    public void clearClaimedChunks() {
        claimedChunks.clear();
    }

    public void updateStrength(PlayerDataHandler playerDataHandler) {
        int totalStrength = 0;
        for (String member : members) {
            int playerStrength = playerDataHandler.getPlayerStrength(member);
            totalStrength += playerStrength;
        }
        this.strength = totalStrength;
    }

    public void removeAllianceByName(String clanName) {
        alliances.remove(clanName);
    }

    public void addAllianceByName(String clanName) {
        alliances.add(clanName);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwner() {
        return owner;
    }

    public void renameClan(String newName) {
        oldName = name;
        name = newName;
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
        if (newLeader.equals(owner)) {
            throw new IllegalArgumentException("Вы уже являетесь лидером клана.");
        }

        memberRoles.put(owner, "Заместитель");
        memberRoles.put(newLeader, "Лидер");
        owner = newLeader;
    }

    public List<String> getPlayersWithRole(String role) {
        List<String> playersWithRole = new ArrayList<>();
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
        String promotingPlayerRole = getRole(promotingPlayer);

        if (!promotingPlayerRole.equals("Лидер") && !promotingPlayerRole.equals("Заместитель")) {
            throw new IllegalArgumentException("У вас нет прав для повышения других участников.");
        }

        if (targetPlayer.equals(owner)) {
            throw new IllegalArgumentException("Роль Лидера клана невозможно изменить.");
        }

        String currentRole = getRole(targetPlayer);
        int currentIndex = ROLE_ORDER.indexOf(currentRole);

        if (currentIndex == -1) {
            throw new IllegalArgumentException("Неверная роль для повышения.");
        }

        if (currentIndex >= ROLE_ORDER.indexOf("Заместитель")) {
            throw new IllegalArgumentException("Игрок уже имеет наивысшую роль.");
        }

        if (promotingPlayerRole.equals("Заместитель") && currentIndex >= ROLE_ORDER.indexOf("Воин")) {
            throw new IllegalArgumentException("Заместитель может повысить игрока только до роли 'Воин'.");
        }

        String newRole = ROLE_ORDER.get(currentIndex + 1);
        setRole(targetPlayer, newRole);
    }

    public void moderMember(String moderingPlayer, String targetPlayer) {
        String moderingPlayerRole = getRole(moderingPlayer);

        if (!moderingPlayerRole.equals("Лидер")) {
            throw new IllegalArgumentException("Только Лидер может добавить Заместителя.");
        }

        if (targetPlayer.equals(owner)) {
            throw new IllegalArgumentException("Роль Лидера клана невозможно изменить.");
        }

        String currentRole = getRole(targetPlayer);
        int currentIndex = ROLE_ORDER.indexOf(currentRole);

        if (currentIndex == -1) {
            throw new IllegalArgumentException("Неверная роль для повышения.");
        }

        if (currentIndex >= ROLE_ORDER.indexOf("Заместитель")) {
            throw new IllegalArgumentException("Игрок уже имеет данную роль.");
        }

        String newRole = ROLE_ORDER.get(2);
        setRole(targetPlayer, newRole);
    }

    public void demoteMember(String demotingPlayer, String targetPlayer) {
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

        if (demotingPlayerRole.equals("Заместитель") && currentIndex > 1) {
            throw new IllegalArgumentException("Заместитель может понизить игрока только до роли 'Рекрут'.");
        }

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
        memberRoles.putIfAbsent(player, "Рекрут");
    }

    public boolean isFull() {
        return members.size() >= MAX_MEMBERS;
    }

    public int getMaxPower() {
        return maxPower;
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
        this.lands = claimedChunks.size();
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }
}
