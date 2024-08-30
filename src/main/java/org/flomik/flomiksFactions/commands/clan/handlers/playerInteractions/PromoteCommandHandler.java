package org.flomik.flomiksFactions.commands.clan.handlers.playerInteractions;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.flomik.flomiksFactions.commands.clan.Clan;
import org.flomik.flomiksFactions.commands.clan.ClanManager;

public class PromoteCommandHandler {

    private final ClanManager clanManager;

    public PromoteCommandHandler(ClanManager clanManager) {
        this.clanManager = clanManager;
    }

    public boolean handleCommand(Player player, String[] args) {
        if (args.length > 1) {
            String targetPromPlayerName = args[1];
            Clan clan = clanManager.getPlayerClan(player.getName());

            if (clan == null) {
                player.sendMessage(ChatColor.RED + "Не удалось найти ваш клан.");
                return false;
            }

            String playerRole = clan.getRole(player.getName());
            if (!playerRole.equals("Лидер") && !playerRole.equals("Заместитель")) {
                player.sendMessage(ChatColor.RED + "Только Лидер или Заместитель могут повышать ранг.");
                return false;
            }

            try {
                clan.promoteMember(player.getName(), targetPromPlayerName);
                player.sendMessage(ChatColor.GREEN + "Игрок " + targetPromPlayerName + " повышен в должности.");
            } catch (IllegalArgumentException e) {
                player.sendMessage(ChatColor.RED + e.getMessage());
            }
            clanManager.saveClan(clan);
            return true;
        } else {
            player.sendMessage(ChatColor.YELLOW + "Использование: " + ChatColor.GOLD + "/clan promote <игрок>");
        }
        return true;
    }
}
