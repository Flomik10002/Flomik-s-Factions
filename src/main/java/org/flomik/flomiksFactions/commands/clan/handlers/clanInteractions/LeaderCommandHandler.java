package org.flomik.flomiksFactions.commands.clan.handlers.clanInteractions;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.flomik.flomiksFactions.commands.clan.Clan;
import org.flomik.flomiksFactions.commands.clan.ClanManager;

public class LeaderCommandHandler {

    private final ClanManager clanManager;

    public LeaderCommandHandler(ClanManager clanManager) {
        this.clanManager = clanManager;
    }

    public boolean handleCommand(Player player, String[] args) {
        if (args.length > 1) {
            String newLeaderName = args[1];

            Clan playerClan = clanManager.getPlayerClan(player.getName());
            if (playerClan == null) {
                player.sendMessage(ChatColor.RED + "Вы не состоите в клане.");
                return true;
            }
            if (newLeaderName == null) {
                player.sendMessage(ChatColor.RED + "Игрок не найден.");
                return true;
            }

            // Проверка, что игрок является Лидером клана
            String playerRole = playerClan.getRole(player.getName());
            if (!playerRole.equals("Лидер")) {
                player.sendMessage(ChatColor.RED + "Только Лидер клана может передать права.");
                return true;
            }

            // Передача прав
            try {
                playerClan.transferLeadership(newLeaderName);
                clanManager.updateClan(playerClan);

                Player newLeader = player.getServer().getPlayer(newLeaderName);
                if (newLeader != null) {
                    newLeader.sendMessage(ChatColor.GREEN + "Вы теперь Лидер клана " + ChatColor.YELLOW + playerClan.getName() + ChatColor.GREEN + "!");
                }

                player.sendMessage(ChatColor.GREEN + "Вы передали права Лидера клана " + ChatColor.YELLOW + playerClan.getName() + ChatColor.GREEN + " игроку " + ChatColor.YELLOW + newLeaderName + ChatColor.GREEN + ".");
                Player oldLeader = player.getServer().getPlayer(player.getName());
                if (oldLeader != null) {
                    oldLeader.sendMessage(ChatColor.YELLOW + "Теперь вы Заместитель клана " + ChatColor.GOLD + playerClan.getName() + ChatColor.YELLOW + ".");
                }

            } catch (IllegalArgumentException e) {
                player.sendMessage(ChatColor.RED + e.getMessage());
            }

            return true;
        } else {
            TextComponent usageMessage = new TextComponent(ChatColor.YELLOW + "Использование: ");
            TextComponent clickCommand = new TextComponent(ChatColor.GOLD + "/clan leader <игрок>");
            clickCommand.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/clan leader "));
            usageMessage.addExtra(clickCommand);
            player.spigot().sendMessage(usageMessage);
        }
        return true;
    }
}
