package org.flomik.FlomiksFactions.commands.handlers.playerInteractions;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.flomik.FlomiksFactions.clan.Clan;
import org.flomik.FlomiksFactions.clan.managers.ClanManager;

public class KickHandler {

    private final ClanManager clanManager;

    public KickHandler(ClanManager clanManager) {
        this.clanManager = clanManager;
    }

    public boolean handleCommand(Player player, String[] args) {
        if (args.length > 1) {
            String targetPlayerName = args[1];
            Clan clan = clanManager.getPlayerClan(player.getName());
            Player targetPlayer = Bukkit.getPlayerExact(targetPlayerName);

            if (clan == null) {
                player.sendMessage(ChatColor.RED + "Вы не состоите в клане.");
                return true;
            }

            String playerRole = clan.getRole(player.getName());
            if (!playerRole.equals("Лидер") && !playerRole.equals("Заместитель")) {
                player.sendMessage(ChatColor.RED + "Только Лидер или Заместитель клана может исключать игроков.");
                return false;
            }

            if (targetPlayerName.equals(clan.getOwner())) {
                player.sendMessage(ChatColor.RED + "Заместитель не может исключить Лидера из клана.");
                return false;
            }

            if (clan.getMembers().contains(targetPlayerName)) {
                clan.removeMember(targetPlayerName);
                clanManager.saveClan(clan);
                clanManager.sendClanMessage(clan, ChatColor.GREEN + "Игрок " + ChatColor.YELLOW + targetPlayerName + ChatColor.GREEN +" исключен из клана.");
                clanManager.removePlayerFromClanRegions(targetPlayer, clan);
                if (targetPlayer != null) {
                    targetPlayer.sendMessage(ChatColor.RED + "Вы были исключены из клана " + ChatColor.GOLD + clan.getName()  + ChatColor.RED + ".");
                }
            } else {
                player.sendMessage(ChatColor.RED + "Игрок не найден в вашем клане.");
            }
        } else {
            TextComponent usageMessage = new TextComponent(ChatColor.YELLOW + "Использование: ");
            TextComponent clickCommand = new TextComponent(ChatColor.GOLD + "/clan kick <игрок>");
            clickCommand.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/clan kick "));
            usageMessage.addExtra(clickCommand);
            player.spigot().sendMessage(usageMessage);
        }
        return true;
    }
}
