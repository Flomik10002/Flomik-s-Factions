package org.flomik.flomiksFactions.commands.clan.handlers.playerInteractions;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.flomik.flomiksFactions.commands.clan.Clan;
import org.flomik.flomiksFactions.commands.clan.ClanManager;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class JoinCommandHandler {

    private final ClanManager clanManager;
    private final ConcurrentHashMap<String, List<String>> pendingInvites;

    public JoinCommandHandler(ClanManager clanManager, ConcurrentHashMap<String, List<String>> pendingInvites) {
        this.clanManager = clanManager;
        this.pendingInvites = pendingInvites;
    }

    public boolean handleCommand(Player player, String[] args) {
        if (args.length > 1) {
            String clanName = args[1].toLowerCase();
            Clan invitedClan = clanManager.getClan(clanName);

            if (invitedClan == null) {
                player.sendMessage(ChatColor.RED + "Клан с таким названием не существует.");
                return true;
            }

            String invitedPlayer = player.getName();
            List<String> invites = pendingInvites.get(invitedPlayer);

            if (invites != null && invites.contains(clanName)) {
                // Принимаем приглашение
                try {
                    clanManager.joinClan(clanName, invitedPlayer);
                    invites.remove(clanName); // Удаляем приглашение после успешного присоединения
                    if (invites.isEmpty()) {
                        pendingInvites.remove(invitedPlayer); // Удаляем игрока из карты, если у него больше нет приглашений
                    } else {
                        pendingInvites.put(invitedPlayer, invites); // Обновляем список приглашений
                    }
                    player.sendMessage(ChatColor.GREEN + "Вы успешно присоединились к клану " + ChatColor.YELLOW + clanName + ChatColor.GREEN + "!");
                    sendMessageToRole(invitedClan, ChatColor.YELLOW + player.getName() + ChatColor.GREEN + " присоединился к вашему клану " + ChatColor.YELLOW + clanName + ChatColor.GREEN + "!");
                } catch (IllegalArgumentException e) {
                    player.sendMessage(ChatColor.RED + e.getMessage());
                }
            } else {
                player.sendMessage(ChatColor.RED + "Вы не получили приглашение в этот клан.");
            }
        } else {
            player.sendMessage(ChatColor.YELLOW + "Использование: " + ChatColor.GOLD + "/clan join <название клана>");
        }
        return true;
    }

    private void sendMessageToRole(Clan clan, String message) {
        try {
            // Отправляем сообщение только игрокам с ролями Лидер и Заместитель
            List<String> rolesToNotify = List.of("Лидер", "Заместитель", "Воин", "Рекрут");
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
