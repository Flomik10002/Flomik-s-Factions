package org.flomik.flomiksFactions.clan.commands.home;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.flomik.flomiksFactions.clan.Clan;
import org.flomik.flomiksFactions.clan.ClanManager;

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
        String playerRole = clan.getRole(player.getName());
        if (!playerRole.equals("Лидер") && !playerRole.equals("Заместитель")) {
            player.sendMessage(ChatColor.RED + "Только Лидер или Заместитель может удалить точку дома.");
            return false;
        }

        clan.removeHome();
        clanManager.saveClan(clan);
        player.sendMessage(ChatColor.GREEN + "Точка дома клана удалена.");
        return true;
    }
}
