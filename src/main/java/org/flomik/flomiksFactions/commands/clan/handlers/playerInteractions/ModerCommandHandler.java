package org.flomik.flomiksFactions.commands.clan.handlers.playerInteractions;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.flomik.flomiksFactions.commands.clan.Clan;
import org.flomik.flomiksFactions.commands.clan.ClanManager;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ModerCommandHandler {

    private final ClanManager clanManager;

    public ModerCommandHandler(ClanManager clanManager) {
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
            if (!playerRole.equals("Лидер")) {
                player.sendMessage(ChatColor.RED + "Только Лидер может добавить Заместителя.");
                return false;
            }

            try {
                clan.moderMember(player.getName(), targetPromPlayerName);
                sendMessageToRole(clan, ChatColor.GREEN + "Игрок " + ChatColor.YELLOW + targetPromPlayerName + ChatColor.GREEN + " назначен заместителем.");
            } catch (IllegalArgumentException e) {
                player.sendMessage(ChatColor.RED + e.getMessage());
            }
            clanManager.saveClan(clan);
            return true;
        } else {
            player.sendMessage(ChatColor.YELLOW + "Использование: " + ChatColor.GOLD + "/clan moder <игрок>");
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
