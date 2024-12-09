package org.flomik.flomiksFactions;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.flomik.flomiksFactions.clan.Clan;
import org.flomik.flomiksFactions.clan.ClanManager;
import org.flomik.flomiksFactions.player.PlayerDataHandler;

public class Placeholders extends PlaceholderExpansion {
    private final PlayerDataHandler playerDataHandler;
    private final ClanManager clanManager;
    private static Economy economy = null;

    public Placeholders(PlayerDataHandler playerDataHandler, ClanManager clanManager) {
        this.playerDataHandler = playerDataHandler;
        this.clanManager = clanManager;
        setupEconomy();
    }

    private boolean setupEconomy() {
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    public double getBalance(Player player) {
        if (economy != null) {
            return economy.getBalance(player);
        }
        return 0.0;
    }

    private int getOnlineClanMembersCount(Clan clan) {
        int onlineCount = 0;
        for (String member : clan.getMembers()) {
            Player onlinePlayer = Bukkit.getPlayer(member);
            if (onlinePlayer != null && onlinePlayer.isOnline()) {
                onlineCount++;
            }
        }
        return onlineCount;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getIdentifier() {
        return "flomiksfactions";
    }

    @Override
    public String getAuthor() {
        return "Flomik";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) {
            return "";
        }

        if (clanManager == null) {
            return "Ошибка: ClanManager не инициализирован";
        }

        if (identifier.equals("clan_name")) {
            Clan clan = clanManager.getPlayerClan(player.getName());
            return clan != null ? clan.getName() : "Нет клана";
        }

        if (identifier.equals("clan_role")) {
            Clan clan = clanManager.getPlayerClan(player.getName());
            return (clan != null) ? clan.getRole(player.getName()) : "Нет";
        }

        if (identifier.equals("clan_lands")) {
            Clan clan = clanManager.getPlayerClan(player.getName());
            int clanLands = (clan != null) ? clan.getLands() : 0;
            return String.valueOf(clanLands);
        }

        if (identifier.equals("clan_level")) {
            Clan clan = clanManager.getPlayerClan(player.getName());
            int clanLevel = (clan != null) ? clan.getLevel() : 0;
            return String.valueOf(clanLevel);
        }

        if (identifier.equals("clan_strenght")) {
            Clan clan = clanManager.getPlayerClan(player.getName());
            int clanStrength = (clan != null) ? clan.getStrength() : 0;
            return String.valueOf(clanStrength);
        }

        if (identifier.equals("clan_online_members")) {
            Clan clan = clanManager.getPlayerClan(player.getName());
            int onlineClanMembers = (clan != null) ? getOnlineClanMembersCount(clan) : 0;
            return String.valueOf(onlineClanMembers);
        }


        if (identifier.equals("player_level")) {
            int level = playerDataHandler.getPlayerLevel(player.getName());
            return String.valueOf(level);
        }


        if (identifier.equals("kills")) {
            int kills = playerDataHandler.getKills(player);
            return String.valueOf(kills);
        }


        if (identifier.equals("deaths")) {
            int deaths = playerDataHandler.getDeaths(player);
            return String.valueOf(deaths);
        }


        if (identifier.equals("balance")) {
            double balance = getBalance(player);
            return String.format("%,.2f", balance);
        }


        if (identifier.equals("doubloons")) {
            int doubloons = playerDataHandler.getDoubloons(player.getName());
            return String.valueOf(doubloons);
        }

        return null;
    }
}