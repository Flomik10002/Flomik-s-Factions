package org.flomik.FlomiksFactions.utils; //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.flomik.FlomiksFactions.FlomiksFactions;
import org.flomik.FlomiksFactions.clan.Clan;
import org.flomik.FlomiksFactions.clan.managers.ClanManager;
import org.flomik.FlomiksFactions.player.PlayerDataHandler;

public class Placeholders extends PlaceholderExpansion { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private final PlayerDataHandler playerDataHandler; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private final ClanManager clanManager; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression

    public Placeholders(FlomiksFactions plugin, PlayerDataHandler playerDataHandler, ClanManager clanManager) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        this.playerDataHandler = playerDataHandler;
        this.clanManager = clanManager;
        plugin.setupEconomy();
    }

    private int getOnlineClanMembersCount(Clan clan) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        int onlineCount = 0;
        for (String member : clan.getMembers()) { //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            Player onlinePlayer = Bukkit.getPlayer(member); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
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
    public String onPlaceholderRequest(Player player, String identifier) { //NOPMD - suppressed NPathComplexity - TODO explain reason for suppression //NOPMD - suppressed NPathComplexity - TODO explain reason for suppression
        if (player == null) {
            return ""; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
        }

        if (clanManager == null) {
            return "Ошибка: ClanManager не инициализирован"; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
        }

        if (identifier.equals("clan_name")) { //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression
            Clan clan = clanManager.getPlayerClan(player.getName()); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            return clan != null ? clan.getName() : "Нет клана"; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
        }

        if (identifier.equals("clan_role")) { //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression
            Clan clan = clanManager.getPlayerClan(player.getName()); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            return (clan != null) ? clan.getRole(player.getName()) : "Нет"; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
        }

        if (identifier.equals("clan_lands")) { //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression
            Clan clan = clanManager.getPlayerClan(player.getName()); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            int clanLands = (clan != null) ? clan.getLands() : 0; //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            return String.valueOf(clanLands); //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
        }

        if (identifier.equals("clan_level")) { //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression
            Clan clan = clanManager.getPlayerClan(player.getName()); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            int clanLevel = (clan != null) ? clan.getLevel() : 0; //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            return String.valueOf(clanLevel); //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
        }

        if (identifier.equals("clan_strenght")) { //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression
            Clan clan = clanManager.getPlayerClan(player.getName()); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            int clanStrength = (clan != null) ? clan.getStrength() : 0; //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            return String.valueOf(clanStrength); //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
        }

        if (identifier.equals("clan_online_members")) { //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression
            Clan clan = clanManager.getPlayerClan(player.getName()); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            int onlineClanMembers = (clan != null) ? getOnlineClanMembersCount(clan) : 0; //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            return String.valueOf(onlineClanMembers); //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
        }


        if (identifier.equals("player_level")) { //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression
            int level = playerDataHandler.getPlayerLevel(player.getName()); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            return String.valueOf(level); //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
        }


        if (identifier.equals("kills")) { //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression
            int kills = playerDataHandler.getKills(player); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            return String.valueOf(kills); //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
        }


        if (identifier.equals("deaths")) { //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression
            int deaths = playerDataHandler.getDeaths(player); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            return String.valueOf(deaths); //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
        }

        return null;
    }
}