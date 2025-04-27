package org.flomik.FlomiksFactions.clan.commands.handlers.playerInteractions;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.flomik.FlomiksFactions.clan.Clan;
import org.flomik.FlomiksFactions.clan.managers.ClanManager;
import org.flomik.FlomiksFactions.clan.notifications.ClanNotificationService;
import org.flomik.FlomiksFactions.utils.UsageUtil;

/**
 * Класс обработчика команды `/clan moder`.
 * Позволяет лидеру назначить игрока заместителем клана.
 */
public class ModerHandler {
    private final ClanManager clanManager;
    private final ClanNotificationService clanNotificationService;

    /**
     * Конструктор принимает менеджер клана.
     */
    public ModerHandler(ClanManager clanManager, ClanNotificationService clanNotificationService) {
        this.clanManager = clanManager;
        this.clanNotificationService = clanNotificationService;
    }

    /**
     * Обрабатывает команду `/clan moder <игрок>`.
     * @param player - Игрок, использующий команду.
     * @param args - Аргументы команды.
     * @return true, если команда выполнена успешно.
     */
    public boolean handleCommand(Player player, String[] args) {
        if (args.length <= 1) {
            UsageUtil.sendUsageMessage(player, "/clan moder <игрок>");
            return true;
        }

        String targetPlayerName = args[1];
        Clan clan = clanManager.getPlayerClan(player.getName());

        if (clan == null) {
            player.sendMessage(ChatColor.RED + "Не удалось найти ваш клан.");
            return false;
        }

        if (!"Лидер".equals(clan.getRole(player.getName()))) {
            player.sendMessage(ChatColor.RED + "Только Лидер может назначить Заместителя.");
            return false;
        }

        Player targetPlayer = Bukkit.getPlayerExact(targetPlayerName);
        if (targetPlayer == null) {
            player.sendMessage(ChatColor.RED + "Игрок " + targetPlayerName + " не найден в сети.");
            return false;
        }

        try {
            clan.moderMember(player.getName(), targetPlayerName);
            clanNotificationService.sendClanMessage(clan, ChatColor.GREEN + "Игрок " + ChatColor.YELLOW + targetPlayerName + ChatColor.GREEN + " назначен заместителем.");
            updatePlayerClanRegions(targetPlayer, clan);
        } catch (IllegalArgumentException e) {
            player.sendMessage(ChatColor.RED + e.getMessage());
        }

        clanManager.saveClan(clan);
        return true;
    }

    /**
     * Обновляет владение регионами клана после изменения ранга игрока.
     */
    private void updatePlayerClanRegions(Player targetPlayer, Clan clan) {
        clanManager.removePlayerFromClanRegions(targetPlayer, clan);
        clanManager.addPlayerToClanRegionsAsOwner(targetPlayer, clan);
    }
}
