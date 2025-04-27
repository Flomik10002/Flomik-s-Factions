package org.flomik.FlomiksFactions.clan.commands.handlers.playerInteractions;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.flomik.FlomiksFactions.clan.Clan;
import org.flomik.FlomiksFactions.clan.managers.ClanManager;
import org.flomik.FlomiksFactions.clan.notifications.ClanNotificationService;
import org.flomik.FlomiksFactions.utils.UsageUtil;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Обработчик команды /clan invite <игрок>.
 * Позволяет лидеру или заместителю отправлять и отзывать приглашения в клан.
 */
public class InviteHandler {

    private final ClanManager clanManager;
    private final ClanNotificationService notificationService; // Если хотим использовать сервис
    private final Logger logger = Bukkit.getLogger();

    /**
     * Конструктор принимает {@link ClanManager} для работы с кланами и приглашениями,
     * а также {@link ClanNotificationService} для рассылки сообщений (необязательно).
     */
    public InviteHandler(ClanManager clanManager,
                         ClanNotificationService notificationService) {
        this.clanManager = clanManager;
        this.notificationService = notificationService;
    }

    /**
     * Основной метод обработки команды.
     *
     * @param player Игрок, который ввел команду.
     * @param args   Аргументы команды.
     * @return true, если команда выполнена (даже если завершилась ошибкой).
     */
    public boolean handleCommand(Player player, String[] args) {
        // Если не указали ник игрока, выводим подсказку
        if (args.length <= 1) {
            // Вместо локальной логики:
            // sendUsageMessage(player);
            // используем UsageUtil
            UsageUtil.sendUsageMessage(player, "/clan invite <игрок>");
            return true;
        }

        // "Ник" игрока, которого приглашаем или отзываем приглашение
        String targetName = args[1];

        // Проверяем, состоит ли командующий в клане
        Clan clan = clanManager.getPlayerClan(player.getName());
        if (clan == null) {
            player.sendMessage(ChatColor.RED + "Вы не состоите в клане.");
            return true;
        }

        // Проверяем роль: только Лидер или Заместитель
        String playerRole = clan.getRole(player.getName());
        if (!isLeaderOrDeputy(playerRole)) {
            player.sendMessage(ChatColor.RED + "Только Лидер или Заместитель клана может отправлять и отзывать приглашения.");
            return true;
        }

        // Проверяем, не состоит ли целевой игрок уже в другом клане
        Clan targetClan = clanManager.getPlayerClan(targetName);
        if (targetClan != null) {
            player.sendMessage(ChatColor.RED + "Игрок уже состоит в другом клане!");
            return true;
        }

        // Проверяем, есть ли уже приглашение
        Set<String> invites = clanManager.getInvitationDao().getInvitationsForPlayer(targetName);
        String clanNameLower = clan.getName().toLowerCase();

        // Если приглашение уже есть — это отзыв
        if (invites.contains(clanNameLower)) {
            revokeInvitation(player, clan, targetName);
        } else {
            // Иначе отправляем новое приглашение
            sendNewInvitation(player, clan, targetName);
        }

        return true;
    }

    /**
     * Проверяет, является ли роль "Лидер" или "Заместитель".
     */
    private boolean isLeaderOrDeputy(String role) {
        return Objects.equals(role, "Лидер") || Objects.equals(role, "Заместитель");
    }

    /**
     * Отзывает существующее приглашение.
     */
    private void revokeInvitation(Player commandSender, Clan clan, String targetName) {
        String clanName = clan.getName();
        // Удаляем приглашение
        clanManager.getInvitationDao().removeInvitation(targetName, clanName.toLowerCase());

        notificationService.sendMessageToRoles(
                clan,
                List.of("Лидер", "Заместитель"),
                ChatColor.GREEN + "Приглашение в клан " + ChatColor.YELLOW + clanName
                        + ChatColor.GREEN + " было отменено для игрока " + ChatColor.YELLOW + targetName + ChatColor.GREEN + "."
        );

        // Уведомляем целевого игрока, если он онлайн
        Player invitedPlayer = commandSender.getServer().getPlayer(targetName);
        if (invitedPlayer != null) {
            invitedPlayer.sendMessage(ChatColor.RED + "Приглашение в клан " + ChatColor.YELLOW + clanName + " было отменено.");
        } else {
            commandSender.sendMessage(ChatColor.RED + "Игрок " + targetName + " не в сети.");
        }
    }

    /**
     * Отправляет новое приглашение (или ловит ошибку).
     */
    private void sendNewInvitation(Player commandSender, Clan clan, String targetName) {
        String clanName = clan.getName();

        try {
            // Отправляем приглашение через ClanManager
            clanManager.invitePlayer(clanName.toLowerCase(), targetName);

            // Уведомляем роль клана (Лидер/Заместитель)
            notificationService.sendMessageToRoles(
                    clan,
                    List.of("Лидер", "Заместитель"),
                    ChatColor.GREEN + "Приглашение в клан " + ChatColor.YELLOW + clanName
                            + ChatColor.GREEN + " отправлено игроку " + ChatColor.YELLOW + targetName + ChatColor.GREEN + "!"
            );
            notificationService.sendMessageToRoles(
                    clan,
                    List.of("Лидер", "Заместитель"),
                    ChatColor.YELLOW + "Для отмены приглашения игроку " + ChatColor.GOLD + targetName
                            + ChatColor.YELLOW + " повторите команду."
            );

            // Уведомляем целевого игрока
            Player invitedPlayer = commandSender.getServer().getPlayer(targetName);
            if (invitedPlayer != null) {
                sendInviteMessage(invitedPlayer, clan, commandSender.getName());
            } else {
                commandSender.sendMessage(ChatColor.RED + "Игрок " + targetName + " не в сети, но приглашение будет доступно.");
            }

            // Запускаем таймер истечения приглашения (5 минут)
            scheduleInvitationExpiration(clan, targetName, commandSender);

        } catch (IllegalArgumentException e) {
            commandSender.sendMessage(ChatColor.RED + e.getMessage());
             logger.warning("Ошибка при приглашении: " + e.getMessage());
        }
    }

    /**
     * Шаблонное сообщение целевому игроку о приглашении в клан.
     * Можно упростить использованием UsageUtil.buildClickableCommand(...)
     */
    private void sendInviteMessage(Player invitedPlayer, Clan clan, String inviterName) {
        // Можно собрать кусочки:
        TextComponent message = new TextComponent(
                ChatColor.GREEN + "Вам пришло приглашение в клан " + ChatColor.YELLOW + clan.getName()
                        + ChatColor.GREEN + " от игрока " + ChatColor.YELLOW + inviterName + ChatColor.GREEN + ". Используйте "
        );

        // Сокращаем код, используя UsageUtil:
        TextComponent clickable = UsageUtil.buildClickableCommand(
                "/clan join " + clan.getName(),   // отображаемое
                "/clan join " + clan.getName()    // кликабельное
        );
        message.addExtra(clickable);
        message.addExtra(new TextComponent(ChatColor.GREEN + " для принятия приглашения."));

        invitedPlayer.spigot().sendMessage(message);
    }

    /**
     * Планирует задачу, которая через 5 минут проверит, не осталось ли приглашение в БД,
     * и при необходимости удалит его.
     */
    private void scheduleInvitationExpiration(Clan clan, String targetName, Player commandSender) {
        final String clanName = clan.getName();

        new BukkitRunnable() {
            @Override
            public void run() {
                Set<String> currentInvites = clanManager.getInvitationDao().getInvitationsForPlayer(targetName);
                if (currentInvites.contains(clanName.toLowerCase())) {
                    // Удаляем просроченное приглашение
                    clanManager.getInvitationDao().removeInvitation(targetName, clanName.toLowerCase());

                    // Уведомляем клан
                    notificationService.sendMessageToRoles(
                            clan,
                            List.of("Лидер", "Заместитель"),
                            ChatColor.RED + "Приглашение для игрока " + ChatColor.YELLOW + targetName
                                    + ChatColor.RED + " в клан " + ChatColor.YELLOW + clanName + ChatColor.RED + " истекло."
                    );

                    // Уведомляем игрока
                    Player invitedPlayer = commandSender.getServer().getPlayer(targetName);
                    if (invitedPlayer != null) {
                        invitedPlayer.sendMessage(ChatColor.RED + "Ваше приглашение в клан "
                                + ChatColor.YELLOW + clanName + ChatColor.RED + " истекло.");
                    }
                }
            }
        }.runTaskLater(
                Bukkit.getPluginManager().getPlugin("FlomiksFactions"),
                5 * 60 * 20L
        );
    }
}
