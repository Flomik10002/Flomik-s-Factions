package org.flomik.flomiksFactions.commands.clan.handlers.playerInteractions;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.flomik.flomiksFactions.commands.clan.Clan;
import org.flomik.flomiksFactions.commands.clan.ClanManager;

public class KickCommandHandler {

    private final ClanManager clanManager;

    public KickCommandHandler(ClanManager clanManager) {
        this.clanManager = clanManager;
    }

    public boolean handleCommand(Player player, String[] args) {
        if (args.length > 1) {
            String targetPlayerName = args[1];
            Clan clan = clanManager.getPlayerClan(player.getName());

            if (clan == null) {
                player.sendMessage(ChatColor.RED + "Вы не состоите в клане.");
                return true;
            }

            if (!clan.getOwner().equals(player.getName())) {
                player.sendMessage(ChatColor.RED + "Только лидер клана может исключать игроков.");
                return true;
            }

            if (clan.getMembers().contains(targetPlayerName)) {
                clan.removeMember(targetPlayerName);
                player.sendMessage(ChatColor.GREEN + "Игрок " + ChatColor.YELLOW + targetPlayerName + ChatColor.GREEN +" исключен из клана.");
                Player targetPlayer = player.getServer().getPlayer(targetPlayerName);
                if (targetPlayer != null) {
                    targetPlayer.sendMessage(ChatColor.RED + "Вы были исключены из клана " + ChatColor.GOLD + clan.getName()  + ChatColor.RED + ".");
                }
            } else {
                player.sendMessage(ChatColor.RED + "Игрок не найден в вашем клане.");
            }
        } else {
            player.sendMessage(ChatColor.YELLOW + "Использование: " + ChatColor.GOLD + "/clan kick <игрок>");
        }
        return true;
    }
}
