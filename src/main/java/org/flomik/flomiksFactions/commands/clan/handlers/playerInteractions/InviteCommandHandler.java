package org.flomik.flomiksFactions.commands.clan.handlers.playerInteractions;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.flomik.flomiksFactions.commands.clan.Clan;
import org.flomik.flomiksFactions.commands.clan.ClanManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class InviteCommandHandler {

    private final ClanManager clanManager;
    private final ConcurrentHashMap<String, List<String>> pendingInvites;

    public InviteCommandHandler(ClanManager clanManager, ConcurrentHashMap<String, List<String>> pendingInvites) {
        this.clanManager = clanManager;
        this.pendingInvites = pendingInvites;
    }

    public boolean handleCommand(Player player, String[] args) {
        if (args.length > 1) {
            String playerName = args[1];
            Clan clan = clanManager.getPlayerClan(player.getName());
            if (clan == null) {
                player.sendMessage(ChatColor.RED + "Вы не состоите в клане.");
                return true;
            }

            // Проверка, что игрок является лидером клана
            String playerRole = clan.getRole(player.getName());
            if (!playerRole.equals("Лидер") && !playerRole.equals("Заместитель")) {
                player.sendMessage(ChatColor.RED + "Только Лидер или Заместитель клана может отправлять и отзывать приглашения.");
                return true;
            }

            List<String> invites = pendingInvites.getOrDefault(playerName, new ArrayList<>());

            if (invites.contains(clan.getName())) {
                // Отзываем приглашение
                invites.remove(clan.getName());
                pendingInvites.put(playerName, invites); // Обновляем список приглашений
                player.sendMessage(ChatColor.GREEN + "Приглашение в клан " + ChatColor.YELLOW + clan.getName() + ChatColor.GREEN + " было отменено для игрока " + ChatColor.YELLOW + playerName + ChatColor.GREEN + ".");
                Player invitedPlayer = player.getServer().getPlayer(playerName);
                if (invitedPlayer != null) {
                    invitedPlayer.sendMessage(ChatColor.RED + "Приглашение в клан " + ChatColor.YELLOW + clan.getName() + " было отменено.");
                }
            } else {
                // Отправляем приглашение
                try {
                    clanManager.invitePlayer(clan.getName(), playerName);
                    invites.add(clan.getName());
                    pendingInvites.put(playerName, invites); // Сохраняем обновлённый список приглашений
                    player.sendMessage(ChatColor.GREEN + "Приглашение в клан " + ChatColor.YELLOW + clan.getName() + ChatColor.GREEN + " отправлено игроку " + ChatColor.YELLOW + playerName + ChatColor.GREEN + "!");
                    player.sendMessage(ChatColor.YELLOW + "Для отмены приглашения игроку " + ChatColor.GOLD + playerName + ChatColor.YELLOW + " повторите команду.");
                    Player invitedPlayer = player.getServer().getPlayer(playerName);
                    if (invitedPlayer != null) {
                        invitedPlayer.sendMessage(ChatColor.GREEN + "Вам пришло приглашение в клан " + ChatColor.YELLOW + clan.getName() + ChatColor.GREEN + " от игрока " + ChatColor.YELLOW + player.getName() + ChatColor.GREEN + ". Используйте " + ChatColor.YELLOW + "/clan join <название>" + ChatColor.GREEN + " для принятия приглашения.");
                    }

                    // Запускаем задачу для автоматического удаления инвайта через 5 минут
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            List<String> currentInvites = pendingInvites.get(playerName);
                            if (currentInvites != null && currentInvites.contains(clan.getName())) {
                                currentInvites.remove(clan.getName());
                                if (currentInvites.isEmpty()) {
                                    pendingInvites.remove(playerName);
                                } else {
                                    pendingInvites.put(playerName, currentInvites);
                                }
                                player.sendMessage(ChatColor.RED + "Приглашение для игрока " + ChatColor.YELLOW + playerName + ChatColor.RED + " в клан " + ChatColor.YELLOW + clan.getName() + ChatColor.RED + " истекло.");
                                Player invitedPlayer = player.getServer().getPlayer(playerName);
                                if (invitedPlayer != null) {
                                    invitedPlayer.sendMessage(ChatColor.RED + "Ваше приглашение в клан " + ChatColor.YELLOW + clan.getName() + ChatColor.RED + " истекло.");
                                }
                            }
                        }
                    }.runTaskLater(Bukkit.getPluginManager().getPlugin("FlomiksFactions"), 5 * 60 * 20); // 5 минут * 60 секунд * 20 тиков

                } catch (IllegalArgumentException e) {
                    player.sendMessage(ChatColor.RED + e.getMessage());
                }
            }
        } else {
            player.sendMessage(ChatColor.YELLOW + "Использование: " + ChatColor.GOLD + "/clan invite <игрок>");
        }

        return true;
    }
}
