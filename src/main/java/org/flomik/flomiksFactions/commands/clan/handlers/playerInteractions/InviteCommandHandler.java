package org.flomik.flomiksFactions.commands.clan.handlers.playerInteractions;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.flomik.flomiksFactions.commands.clan.Clan;
import org.flomik.flomiksFactions.commands.clan.ClanManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class clickCommandHandler {

    private final ClanManager clanManager;
    private final ConcurrentHashMap<String, List<String>> pendingInvites;

    public clickCommandHandler(ClanManager clanManager, ConcurrentHashMap<String, List<String>> pendingInvites) {
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
                sendMessageToRole(clan, ChatColor.GREEN + "Приглашение в клан " + ChatColor.YELLOW + clan.getName() + ChatColor.GREEN + " было отменено для игрока " + ChatColor.YELLOW + playerName + ChatColor.GREEN + ".");
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
                    sendMessageToRole(clan, ChatColor.GREEN + "Приглашение в клан " + ChatColor.YELLOW + clan.getName() + ChatColor.GREEN + " отправлено игроку " + ChatColor.YELLOW + playerName + ChatColor.GREEN + "!");
                    sendMessageToRole(clan, ChatColor.YELLOW + "Для отмены приглашения игроку " + ChatColor.GOLD + playerName + ChatColor.YELLOW + " повторите команду.");
                    Player invitedPlayer = player.getServer().getPlayer(playerName);
                    if (invitedPlayer != null) {
                        TextComponent message = new TextComponent(ChatColor.GREEN + "Вам пришло приглашение в клан " + ChatColor.YELLOW + clan.getName() + ChatColor.GREEN + " от игрока " + ChatColor.YELLOW + playerName + ChatColor.GREEN + ". Используйте ");
                        TextComponent joinCommand = new TextComponent(ChatColor.YELLOW + "/clan join " + clan.getName());
                        joinCommand.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/clan join " + clan.getName()));
                        message.addExtra(joinCommand);
                        message.addExtra(new TextComponent(ChatColor.GREEN + " для принятия приглашения."));

                        invitedPlayer.spigot().sendMessage(message);
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
                                sendMessageToRole(clan, ChatColor.RED + "Приглашение для игрока " + ChatColor.YELLOW + playerName + ChatColor.RED + " в клан " + ChatColor.YELLOW + clan.getName() + ChatColor.RED + " истекло.");
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
            TextComponent usageMessage = new TextComponent(ChatColor.YELLOW + "Использование: ");
            TextComponent clickCommand = new TextComponent(ChatColor.GOLD + "/clan invite <игрок>");
            clickCommand.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/clan invite "));
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
