package org.flomik.FlomiksFactions.clan; //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.flomik.FlomiksFactions.player.PlayerDataHandler;

import java.util.*;

public class Clan { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private static final int MAX_MEMBERS = 15; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private static final List<String> ROLE_ORDER = Arrays.asList("Рекрут", "Воин", "Заместитель", "Лидер"); //NOPMD - suppressed AvoidDuplicateLiterals - TODO explain reason for suppression //NOPMD - suppressed AvoidDuplicateLiterals - TODO explain reason for suppression //NOPMD - suppressed AvoidDuplicateLiterals - TODO explain reason for suppression

    private final Map<String, String> memberRoles; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private String name; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private String oldName; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private String owner; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private final Set<String> members; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private final Date creationDate; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private String description; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private final List<String> alliances; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private int level; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private int clanXp; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private double balance; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private int lands; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private int strength; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private final int maxPower; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private final List<String> claimedChunks; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private Location home; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression

    public Clan(String name, String owner, Set<String> members, Map<String, String> memberRoles, //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
                Date creationDate, String description, List<String> alliances, int level, //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
                int clanXp, double balance, int lands, int strength, int maxPower, List<String> claimedChunks) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        this.name = name;
        this.oldName = null; //NOPMD - suppressed NullAssignment - TODO explain reason for suppression //NOPMD - suppressed NullAssignment - TODO explain reason for suppression //NOPMD - suppressed NullAssignment - TODO explain reason for suppression
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

    public void setBalance(double newBalance) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        if (newBalance < 0) {
            throw new IllegalArgumentException("Баланс не может быть меньше 0!");
        }
        this.balance = newBalance;
    }

    public void deposit(double amount) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма должна быть больше 0.");
        }
        this.balance += amount;
    }

    public void withdraw(double amount) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма должна быть больше 0.");
        }
        if (this.balance < amount) {
            throw new IllegalArgumentException("Недостаточно средств в казне клана.");
        }
        this.balance -= amount;
    }

    public int getRequiredXpForNextLevel(int currentLevel) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        if (currentLevel <= 0) {
            return 7; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
        }

        int requiredXp = 7;
        for (int lvl = 1; lvl < currentLevel; lvl++) {
            requiredXp += 7 + (lvl - 1) * 2;
        }
        return requiredXp;
    }

    public void addClanXp(int xp) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
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

    public boolean hasClaimedChunk(String chunkId) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        return claimedChunks.contains(chunkId);
    }

    public void addClaimedChunk(String chunkId) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        claimedChunks.add(chunkId);
    }

    public void removeClaimedChunk(String chunkId) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        claimedChunks.remove(chunkId);
    }

    public void clearClaimedChunks() { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        claimedChunks.clear();
    }

    public void updateStrength(PlayerDataHandler playerDataHandler) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        int totalStrength = 0;
        for (String member : members) { //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            int playerStrength = playerDataHandler.getPlayerStrength(member); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            totalStrength += playerStrength;
        }
        this.strength = totalStrength;
    }

    public void removeAllianceByName(String clanName) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        alliances.remove(clanName);
    }

    public void addAllianceByName(String clanName) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        alliances.add(clanName);
    }

    public void setDescription(String description) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        this.description = description;
    }

    public String getOwner() {
        return owner;
    }

    public void renameClan(String newName) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        oldName = name;
        name = newName;
    }

    public String getOldName() {
        return oldName;
    }

    public void resetOldName() { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        this.oldName = null; //NOPMD - suppressed NullAssignment - TODO explain reason for suppression //NOPMD - suppressed NullAssignment - TODO explain reason for suppression //NOPMD - suppressed NullAssignment - TODO explain reason for suppression
    }

    public void transferLeadership(String newLeader) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
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

    public List<String> getPlayersWithRole(String role) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        List<String> playersWithRole = new ArrayList<>(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        if (ROLE_ORDER.contains(role)) {
            for (Map.Entry<String, String> entry : memberRoles.entrySet()) { //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                if (entry.getValue().equals(role)) {
                    playersWithRole.add(entry.getKey());
                }
            }
        }
        return playersWithRole;
    }

    public String getRole(String playerName) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        return memberRoles.getOrDefault(playerName, "Не состоит в клане");
    }

    public void setRole(String playerName, String role) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        if (ROLE_ORDER.contains(role)) {
            memberRoles.put(playerName, role);
        } else {
            throw new IllegalArgumentException("Неверная роль: " + role);
        }
    }

    public void promoteMember(String promotingPlayer, String targetPlayer) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        String promotingPlayerRole = getRole(promotingPlayer); //NOPMD - suppressed LongVariable - TODO explain reason for suppression //NOPMD - suppressed LongVariable - TODO explain reason for suppression //NOPMD - suppressed LongVariable - TODO explain reason for suppression

        if (!promotingPlayerRole.equals("Лидер") && !promotingPlayerRole.equals("Заместитель")) { //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression
            throw new IllegalArgumentException("У вас нет прав для повышения других участников.");
        }

        if (targetPlayer.equals(owner)) {
            throw new IllegalArgumentException("Роль Лидера клана невозможно изменить.");
        }

        String currentRole = getRole(targetPlayer); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        int currentIndex = ROLE_ORDER.indexOf(currentRole); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression

        if (currentIndex == -1) {
            throw new IllegalArgumentException("Неверная роль для повышения.");
        }

        if (currentIndex >= ROLE_ORDER.indexOf("Заместитель")) {
            throw new IllegalArgumentException("Игрок уже имеет наивысшую роль.");
        }

        if (promotingPlayerRole.equals("Заместитель") && currentIndex >= ROLE_ORDER.indexOf("Воин")) { //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression
            throw new IllegalArgumentException("Заместитель может повысить игрока только до роли 'Воин'.");
        }

        String newRole = ROLE_ORDER.get(currentIndex + 1); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        setRole(targetPlayer, newRole);
    }

    public void moderMember(String moderingPlayer, String targetPlayer) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        String moderingPlayerRole = getRole(moderingPlayer); //NOPMD - suppressed LongVariable - TODO explain reason for suppression //NOPMD - suppressed LongVariable - TODO explain reason for suppression //NOPMD - suppressed LongVariable - TODO explain reason for suppression

        if (!moderingPlayerRole.equals("Лидер")) { //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression
            throw new IllegalArgumentException("Только Лидер может добавить Заместителя.");
        }

        if (targetPlayer.equals(owner)) {
            throw new IllegalArgumentException("Роль Лидера клана невозможно изменить.");
        }

        String currentRole = getRole(targetPlayer); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        int currentIndex = ROLE_ORDER.indexOf(currentRole); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression

        if (currentIndex == -1) {
            throw new IllegalArgumentException("Неверная роль для повышения.");
        }

        if (currentIndex >= ROLE_ORDER.indexOf("Заместитель")) {
            throw new IllegalArgumentException("Игрок уже имеет данную роль.");
        }

        String newRole = ROLE_ORDER.get(2); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        setRole(targetPlayer, newRole);
    }

    public void demoteMember(String demotingPlayer, String targetPlayer) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        String demotingPlayerRole = getRole(demotingPlayer); //NOPMD - suppressed LongVariable - TODO explain reason for suppression //NOPMD - suppressed LongVariable - TODO explain reason for suppression //NOPMD - suppressed LongVariable - TODO explain reason for suppression

        if (!demotingPlayerRole.equals("Лидер") && !demotingPlayerRole.equals("Заместитель")) { //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression
            throw new IllegalArgumentException("У вас нет прав для понижения других участников.");
        }

        if (targetPlayer.equals(owner)) {
            throw new IllegalArgumentException("Лидер клана не может изменить свою роль.");
        }

        String currentRole = getRole(targetPlayer); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        int currentIndex = ROLE_ORDER.indexOf(currentRole); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression

        if (currentIndex == -1) {
            throw new IllegalArgumentException("Неверная роль для понижения.");
        }

        if (currentIndex == 0) {
            throw new IllegalArgumentException("Игрок уже имеет минимальную роль.");
        }

        if (demotingPlayerRole.equals("Заместитель") && currentIndex > 1) { //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression
            throw new IllegalArgumentException("Заместитель может понизить игрока только до роли 'Рекрут'.");
        }

        String newRole = ROLE_ORDER.get(currentIndex - 1); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        setRole(targetPlayer, newRole);
    }

    public List<String> getRegionNames() { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        return claimedChunks;
    }

    public List<String> getAlliances() {
        return new ArrayList<>(alliances);
    }

    public void addAlliances(Clan clan) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        alliances.add(clan.getName());
    }

    public void removeAlliance(Clan clan) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        alliances.remove(clan.getName());
    }

    public Location getHome() {
        return home;
    }

    public void setHome(Location home) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        this.home = home;
    }

    public void removeHome() { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        this.home = null; //NOPMD - suppressed NullAssignment - TODO explain reason for suppression //NOPMD - suppressed NullAssignment - TODO explain reason for suppression //NOPMD - suppressed NullAssignment - TODO explain reason for suppression
    }

    public boolean hasHome() { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        return home != null;
    }

    public void removeMember(String player) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        members.remove(player);
        memberRoles.remove(player);
    }

    public String getName() {
        return name;
    }

    public Set<String> getMembers() {
        return new HashSet<>(members);
    }

    public void addMember(String player) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        members.add(player);
        memberRoles.putIfAbsent(player, "Рекрут");
    }

    public boolean isFull() { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
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

    public void setLands(int lands) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        this.lands = lands;
    }

    public void updateLands() { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        this.lands = claimedChunks.size();
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        this.strength = strength;
    }
}
