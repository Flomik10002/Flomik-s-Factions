package org.flomik.FlomiksFactions.clan.commands.handlers.clanInteractions;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.flomik.FlomiksFactions.clan.Clan;
import org.flomik.FlomiksFactions.clan.managers.ClanManager;
import org.flomik.FlomiksFactions.clan.notifications.ClanNotificationService;
import org.flomik.FlomiksFactions.utils.UsageUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Обработчик команды /clan ally <название_клана>.
 * Предоставляет функционал:
 * - Предложение союза и его отмена,
 * - Принятие союза, если другая сторона уже сделала предложение,
 * - Расторжение союза (если уже союзники),
 * - Лимит союзов (максимум 3),
 * - Автоматическое истечение (таймер) предложения союза.
 */
public class AllyHandler {

    private final ClanManager clanManager;
    private final ClanNotificationService clanNotificationService;

    /**
     * pendingAllies хранит "имя_клана" -> список кланов, ожидающих подтверждения.
     * Пример: { "clanA" -> ["clanB", "clanC"] }, значит clanA ждет подтверждения от clanB и clanC.
     */
    private final ConcurrentHashMap<String, List<String>> pendingAllies;

    /**
     * @param clanManager           Менеджер кланов, для управления и обновления кланов.
     * @param clanNotificationService Сервис для отправки уведомлений, связанных с кланами.
     * @param pendingAllies         Потокобезопасная структура для отслеживания предложений союза.
     */
    public AllyHandler(final ClanManager clanManager,
                       final ClanNotificationService clanNotificationService,
                       final ConcurrentHashMap<String, List<String>> pendingAllies)
    {
        this.clanManager = clanManager;
        this.clanNotificationService = clanNotificationService;
        this.pendingAllies = pendingAllies;
    }

    /**
     * Обрабатывает команду /clan ally <название_клана>.
     *
     * @param player Игрок, который вводит команду
     * @param args   Аргументы команды, где args[1] - это название клана
     * @return true, если команда обработана (даже при ошибке).
     */
    public boolean handleCommand(final Player player, final String[] args) {
        // Если не указан параметр <название_клана>, показываем подсказку
        if (args.length <= 1) {
            UsageUtil.sendUsageMessage(player, "/clan ally <название>");
            return true;
        }

        // Название клана, с которым хотим вступить/отменить/принять союз
        final String allyClanName = args[1].trim().toLowerCase();

        // Получаем кланы из ClanManager
        final Clan playerClan = clanManager.getPlayerClan(player.getName());
        final Clan targetClan = clanManager.getClan(allyClanName);

        // Базовые проверки
        if (!checkBasicConditions(player, playerClan, targetClan)) {
            return true;
        }

        // Если уже союз, расторгаем
        if (playerClan.getAlliances().contains(targetClan.getName())) {
            terminateAlliance(playerClan, targetClan);
            return true;
        }

        // Проверяем, не было ли уже предложения от playerClan -> targetClan
        final String playerClanName = playerClan.getName();
        if (hasPendingAlliance(playerClanName, allyClanName)) {
            // Уже есть запрос (в pendingAllies) => отменяем
            cancelAllianceProposal(player, playerClan, targetClan, allyClanName);
        } else {
            // Проверяем, не предложил ли targetClan нам (взаимное)
            if (theyProposedBack(targetClan.getName(), playerClanName)) {
                acceptAlliance(playerClan, targetClan);
            } else {
                // Иначе создаём новое предложение
                proposeAlliance(player, playerClan, targetClan, allyClanName);
            }
        }

        return true;
    }

    /**
     * Проверяет базовые условия перед работой с союзами:
     * - Игрок должен состоять в клане,
     * - Игрок должен иметь роль "Лидер" или "Заместитель",
     * - Количество союзов в обоих кланах не должно превышать 3,
     * - Нельзя предлагать союз самому себе (одному и тому же клану),
     * - Клан с указанным именем должен существовать.
     *
     * @return true, если все проверки пройдены, иначе выводит сообщение и возвращает false.
     */
    private boolean checkBasicConditions(final Player player, final Clan playerClan, final Clan targetClan) {
        if (playerClan == null) {
            player.sendMessage(ChatColor.RED + "Вы не состоите в клане.");
            return false;
        }

        final String playerRole = playerClan.getRole(player.getName());
        if (!"Лидер".equals(playerRole) && !"Заместитель".equals(playerRole)) {
            player.sendMessage(ChatColor.RED + "Только Лидер или Заместитель клана могут предлагать/расторгать союзы.");
            return false;
        }

        if (playerClan.getAlliances().size() >= 3) {
            player.sendMessage(ChatColor.RED + "У вас превышен лимит альянсов (макс. 3).");
            return false;
        }

        if (targetClan == null) {
            player.sendMessage(ChatColor.RED + "Клан с таким названием не существует.");
            return false;
        }

        if (targetClan.getAlliances().size() >= 3) {
            player.sendMessage(ChatColor.RED + "У клана '" + targetClan.getName() + "' превышен лимит альянсов.");
            return false;
        }

        if (playerClan.getName().equalsIgnoreCase(targetClan.getName())) {
            player.sendMessage(ChatColor.RED + "Вы не можете предложить союз своему же клану.");
            return false;
        }

        return true;
    }

    /**
     * Расторгнуть существующий союз.
     */
    private void terminateAlliance(final Clan playerClan, final Clan targetClan) {
        playerClan.removeAlliance(targetClan);
        targetClan.removeAlliance(playerClan);
        clanManager.updateClan(playerClan);
        clanManager.updateClan(targetClan);

        // Вместо "Bukkit.broadcastMessage(...)" вызываем сервис
        clanNotificationService.notifyAllianceBroken(playerClan, targetClan);
    }

    /**
     * Проверяет, есть ли в pendingAllies предложение от playerClan к targetClan.
     */
    private boolean hasPendingAlliance(final String playerClanName, final String allyClanName) {
        return pendingAllies
                .getOrDefault(playerClanName.toLowerCase(), new ArrayList<>())
                .contains(allyClanName);
    }

    /**
     * Проверяет, предложил ли targetClan уже союз playerClan (т.е. взаимное соглашение).
     */
    private boolean theyProposedBack(final String targetClanName, final String playerClanName) {
        return pendingAllies
                .getOrDefault(targetClanName.toLowerCase(), new ArrayList<>())
                .contains(playerClanName.toLowerCase());
    }

    /**
     * Отмена уже отправленного предложения союза.
     */
    private void cancelAllianceProposal(final Player player,
                                        final Clan playerClan,
                                        final Clan targetClan,
                                        final String allyClanName) {

        final String playerClanName = playerClan.getName().toLowerCase();
        final List<String> allies = pendingAllies.getOrDefault(playerClanName, new ArrayList<>());

        allies.remove(allyClanName);
        if (allies.isEmpty()) {
            pendingAllies.remove(playerClanName);
        } else {
            pendingAllies.put(playerClanName, allies);
        }

        // Сообщение игроку, что отменил
        player.sendMessage(ChatColor.GREEN + "Предложение о союзе с кланом "
                + ChatColor.YELLOW + targetClan.getName() + ChatColor.GREEN + " было отменено.");

        // Уведомляем целевой клан (лидеров/заместителей)
        clanNotificationService.sendMessageToRoles(
                targetClan,
                List.of("Лидер", "Заместитель"),
                ChatColor.RED + "Предложение о союзе с кланом " + ChatColor.YELLOW
                        + playerClan.getName() + ChatColor.RED + " было отменено."
        );
    }

    /**
     * При взаимном предложении обеих сторон (одна сделала предложение, вторая тоже),
     * кланы заключают союз.
     */
    private void acceptAlliance(final Clan playerClan, final Clan targetClan) {
        playerClan.addAlliances(targetClan);
        targetClan.addAlliances(playerClan);
        clanManager.updateClan(playerClan);
        clanManager.updateClan(targetClan);

        // Удаляем из pendingAllies у "targetClan"
        final String targetClanLower = targetClan.getName().toLowerCase();
        final List<String> targetPending = pendingAllies.getOrDefault(targetClanLower, new ArrayList<>());
        targetPending.remove(playerClan.getName().toLowerCase());
        if (targetPending.isEmpty()) {
            pendingAllies.remove(targetClanLower);
        } else {
            pendingAllies.put(targetClanLower, targetPending);
        }

        // Используем либо notifyAllianceFormed(...), либо broadcastClanEvent(...)
        // Создадим новый метод, например notifyAllianceFormed:
        clanNotificationService.notifyAllianceFormed(playerClan, targetClan);
    }

    /**
     * Отправляем новое предложение союза.
     */
    private void proposeAlliance(final Player player,
                                 final Clan playerClan,
                                 final Clan targetClan,
                                 final String allyClanName) {

        // Сохраняем предложение в pendingAllies
        final String playerClanLower = playerClan.getName().toLowerCase();
        final List<String> allies = pendingAllies.getOrDefault(playerClanLower, new ArrayList<>());
        allies.add(allyClanName);
        pendingAllies.put(playerClanLower, allies);

        // Сообщаем игроку, что отправлено предложение
        player.sendMessage(ChatColor.GREEN
                + "Предложение о союзе с кланом "
                + ChatColor.YELLOW + targetClan.getName()
                + ChatColor.GREEN + " отправлено.");
        player.sendMessage(ChatColor.YELLOW
                + "Для отмены предложения о союзе повторите команду.");

        // Формируем общее сообщение
        TextComponent allyMessage = new TextComponent(
                ChatColor.GREEN
                        + "Клан " + ChatColor.YELLOW + playerClan.getName()
                        + ChatColor.GREEN
                        + " предложил вам союз. Используйте "
        );

        // Создаём кликабельную часть командой
        TextComponent clickableCommand = UsageUtil.buildClickableCommand(
                "/clan ally " + playerClan.getName(), // что видит игрок
                "/clan ally " + playerClan.getName()  // что вставится в чат при клике
        );

        // Склеиваем
        allyMessage.addExtra(clickableCommand);
        allyMessage.addExtra(new TextComponent(ChatColor.GREEN + " для принятия предложения."));

        // Отправляем лидеру/заместителям целевого клана
        clanNotificationService.sendMessageToRoles(
                targetClan,
                List.of("Лидер", "Заместитель"),
                allyMessage
        );

        // Запускаем таймер истечения (5 минут)
        startProposalExpirationTimer(player, playerClan, targetClan.getName().toLowerCase());
    }


    /**
     * Запускает задачу, которая через 5 минут удалит предложение, если оно не принято/отменено.
     */
    private void startProposalExpirationTimer(final Player sender, final Clan playerClan, final String targetClanName) {
        final String playerClanNameLower = playerClan.getName().toLowerCase();

        new BukkitRunnable() {
            @Override
            public void run() {
                final List<String> currentAllies = pendingAllies.get(playerClanNameLower);
                if (currentAllies != null && currentAllies.contains(targetClanName)) {
                    // Удаляем предложение
                    currentAllies.remove(targetClanName);
                    if (currentAllies.isEmpty()) {
                        pendingAllies.remove(playerClanNameLower);
                    } else {
                        pendingAllies.put(playerClanNameLower, currentAllies);
                    }

                    // Уведомляем самого отправителя
                    sender.sendMessage(ChatColor.RED + "Предложение о союзе с кланом "
                            + ChatColor.GOLD + targetClanName + ChatColor.RED + " истекло.");

                    // Уведомляем целевой клан
                    final Clan targetClan = clanManager.getClan(targetClanName);
                    if (targetClan != null) {
                        clanNotificationService.sendMessageToRoles(
                                targetClan,
                                List.of("Лидер", "Заместитель"),
                                ChatColor.RED + "Предложение о союзе от клана " + ChatColor.GOLD
                                        + playerClan.getName() + ChatColor.RED + " истекло."
                        );
                    }
                }
            }
        }.runTaskLater(
                Bukkit.getPluginManager().getPlugin("FlomiksFactions"),
                5 * 60 * 20L  // 5 минут
        );
    }
}
