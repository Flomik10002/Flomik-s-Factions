package org.flomik.flomiksFactions.commands.clan.handlers.home;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.flomik.flomiksFactions.commands.clan.Clan;
import org.flomik.flomiksFactions.commands.clan.ClanManager;

public class HomeCommandHandler {
    private final ClanManager clanManager;

    public HomeCommandHandler(ClanManager clanManager) {
        this.clanManager = clanManager;
    }

    public boolean handleCommand(Player player) {
        Clan clan = clanManager.getPlayerClan(player.getName());
        if (clan == null) {
            player.sendMessage(ChatColor.RED + "Вы не состоите в клане.");
            return false;
        }
        if (!clan.hasHome()) {
            player.sendMessage(ChatColor.RED + "Точка дома клана не установлена.");
            return false;
        }

        player.teleport(clan.getHome());
        player.sendMessage(ChatColor.GREEN + "Вы телепортированы к точке дома клана.");
        return true;
    }
}
