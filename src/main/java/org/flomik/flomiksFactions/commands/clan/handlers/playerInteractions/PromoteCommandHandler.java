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
            if (clanManager.getPlayerClan(player.getName()) == null) {
                player.sendMessage(ChatColor.RED + "Вы не состоите в клане.");
                return false;
            }

            String targetPromPlayerName = args[1];
            Clan clan = clanManager.getPlayerClan(player.getName());

            if (clan == null) {
                player.sendMessage(ChatColor.RED + "Не удалось найти ваш клан.");
                return false;
            }

            if (!player.getName().equals(clan.getOwner())) {
                player.sendMessage(ChatColor.RED + "Только лидер клана может повысить ранг.");
                return false;
            }

            try {
                clan.promoteMember(targetPromPlayerName);
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
