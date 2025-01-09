package org.flomik.FlomiksFactions.commands.handlers.playerInteractions;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.flomik.FlomiksFactions.clan.Clan;
import org.flomik.FlomiksFactions.clan.managers.ClanManager;

public class ModerHandler {

    private final ClanManager clanManager;

    public ModerHandler(ClanManager clanManager) {
        this.clanManager = clanManager;
    }

    public boolean handleCommand(Player player, String[] args) {
        if (args.length > 1) {
            String targetPromPlayerName = args[1];
            Clan clan = clanManager.getPlayerClan(player.getName());
            Player targetPlayer = Bukkit.getPlayerExact(targetPromPlayerName);

            if (clan == null) {
                player.sendMessage(ChatColor.RED + "Не удалось найти ваш клан.");
                return false;
            }

            String playerRole = clan.getRole(player.getName());
            if (!playerRole.equals("Лидер")) {
                player.sendMessage(ChatColor.RED + "Только Лидер может добавить Заместителя.");
                return false;
            }

            try {
                clan.moderMember(player.getName(), targetPromPlayerName);
                clanManager.sendClanMessage(clan, ChatColor.GREEN + "Игрок " + ChatColor.YELLOW + targetPromPlayerName + ChatColor.GREEN + " назначен заместителем.");
                clanManager.removePlayerFromClanRegions(targetPlayer, clan);
                clanManager.addPlayerToClanRegionsAsOwner(targetPlayer, clan);
            } catch (IllegalArgumentException e) {
                player.sendMessage(ChatColor.RED + e.getMessage());
            }
            clanManager.saveClan(clan);
            return true;
        } else {
            TextComponent usageMessage = new TextComponent(ChatColor.YELLOW + "Использование: ");
            TextComponent clickCommand = new TextComponent(ChatColor.GOLD + "/clan moder <игрок>");
            clickCommand.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/clan moder "));
            usageMessage.addExtra(clickCommand);
            player.spigot().sendMessage(usageMessage);
        }
        return true;
    }
}
