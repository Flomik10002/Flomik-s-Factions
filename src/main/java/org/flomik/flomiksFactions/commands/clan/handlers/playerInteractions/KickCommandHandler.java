package org.flomik.flomiksFactions.commands.clan.handlers.playerInteractions;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.flomik.flomiksFactions.commands.clan.Clan;
import org.flomik.flomiksFactions.commands.clan.ClanManager;

import java.util.List;

public class KickCommandHandler {

    private final ClanManager clanManager;

    public KickCommandHandler(ClanManager clanManager) {
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
                sendMessageToRole(clan, ChatColor.GREEN + "Игрок " + ChatColor.YELLOW + targetPlayerName + ChatColor.GREEN +" исключен из клана.");
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
    private void sendMessageToRole(Clan clan, String message) {
        try {
            // Отправляем сообщение только игрокам с ролями Лидер и Заместитель
            List<String> rolesToNotify = List.of("Лидер", "Заместитель");
            for (String role : rolesToNotify) {
                List<String> playersWithRole = clan.getPlayersWithRole(role);
                for (String playerName : playersWithRole) {
                    Player player = Bukkit.getPlayer(playerName);
                    if (player != null) { // Проверяем, что игрок онлайн
                        player.sendMessage(message);
                    }
                }
            }
        } finally {
        }
    }
}
