package org.flomik.flomiksFactions.commands.clan.handlers.home;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.flomik.flomiksFactions.commands.clan.Clan;
import org.flomik.flomiksFactions.commands.clan.ClanManager;

public class DelHomeCommandHandler {
    private final ClanManager clanManager;

    public DelHomeCommandHandler(ClanManager clanManager) {
        this.clanManager = clanManager;
    }

    public boolean handleCommand(Player player) {
        Clan clan = clanManager.getPlayerClan(player.getName());
        if (clan == null) {
            player.sendMessage(ChatColor.RED + "Вы не состоите в клане.");
            return false;
        }
        if (!player.getName().equals(clan.getOwner())) {
            player.sendMessage(ChatColor.RED + "Только Лидер клана может удалить точку дома.");
            return false;
        }

        clan.removeHome();
        clanManager.saveClan(clan);
        player.sendMessage(ChatColor.GREEN + "Точка дома клана удалена.");
        return true;
    }
}
