package org.flomik.FlomiksFactions.clan.notifications;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.flomik.FlomiksFactions.clan.Clan;
import org.flomik.FlomiksFactions.clan.managers.ClanManager;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Сервис для отправки уведомлений, связанных с кланами.
 * Обеспечивает централизованную логику оповещений.
 */
public class ClanNotificationService {

    private final ClanManager clanManager;
    private final Logger logger = Bukkit.getLogger(); // Или свой логгер

    /**
     * Конструктор принимает {@link ClanManager}, чтобы в случае необходимости
     * иметь доступ к дополнительным данным (напр. загрузка клана и т.д.).
     */
    public ClanNotificationService(ClanManager clanManager) {
        this.clanManager = clanManager;
    }

    // ===========================================================================
    //   БАЗОВЫЕ МЕТОДЫ ОТПРАВКИ
    // ===========================================================================

    /**
     * Отправляет простое текстовое сообщение всем участникам клана.
     */
    public void sendClanMessage(Clan clan, String message) {
        clan.getMembers().stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .filter(Player::isOnline)
                .forEach(p -> p.sendMessage(message));
    }

    /**
     * Отправляет сообщение только определённым ролям клана (например: Лидеру, Заместителю).
     *
     * @param clan    Клан, чьим ролям отправляем
     * @param roles   Список ролей ("Лидер", "Заместитель" и т.д.)
     * @param message Может быть обычной строкой или TextComponent
     */
    public void sendMessageToRoles(Clan clan, List<String> roles, Object message) {
        for (String role : roles) {
            List<String> playersWithRole = clan.getPlayersWithRole(role);
            for (String playerName : playersWithRole) {
                Player target = Bukkit.getPlayer(playerName);
                if (target != null && target.isOnline()) {
                    if (message instanceof String) {
                        target.sendMessage((String) message);
                    } else if (message instanceof TextComponent) {
                        target.spigot().sendMessage((TextComponent) message);
                    }
                }
            }
        }
    }

    /**
     * Широковещательное сообщение для всего сервера (при важных событиях).
     */
    public void broadcastClanEvent(String eventMessage) {
        Bukkit.getServer().broadcastMessage(eventMessage);
    }

    // ===========================================================================
    //   МЕТОДЫ ДЛЯ ОТДЕЛЬНЫХ ИГРОВЫХ СОБЫТИЙ
    // ===========================================================================

    /**
     * Уведомление о том, что кланы только что заключили союз.
     */
    public void notifyAllianceFormed(Clan clan1, Clan clan2) {
        // Уведомление в общий чат
        broadcastClanEvent(ChatColor.GREEN + "Кланы " + ChatColor.YELLOW + clan1.getName()
                + ChatColor.GREEN + " и " + ChatColor.YELLOW + clan2.getName()
                + ChatColor.GREEN + " заключили союз!");
    }

    /**
     * Уведомление о том, что кланы расторгли союз.
     */
    public void notifyAllianceBroken(Clan clan1, Clan clan2) {
        broadcastClanEvent(ChatColor.RED + "Кланы " + ChatColor.YELLOW + clan1.getName()
                + ChatColor.RED + " и " + ChatColor.YELLOW + clan2.getName()
                + ChatColor.RED + " расторгли союз.");
    }

    /**
     * Уведомление о том, что клан распущен.
     */
    public void notifyDisband(Clan clan) {
        broadcastClanEvent(ChatColor.RED + "Клан " + ChatColor.YELLOW + clan.getName()
                + ChatColor.RED + " был распущен!");
    }

    /**
     * Уведомление о том, что клан отправил приглашение определённому игроку.
     */
    public void notifyInvitationSent(Clan fromClan, String targetPlayerName) {
        // Шлём сообщение только роли Лидер/Заместитель в "отправляющем" клане
        sendMessageToRoles(
                fromClan,
                List.of("Лидер", "Заместитель"),
                ChatColor.GREEN + "Приглашение в клан " + fromClan.getName()
                        + " отправлено игроку " + targetPlayerName
        );
        // Игрока тоже можно уведомить, если он онлайн
        Player target = Bukkit.getPlayer(targetPlayerName);
        if (target != null && target.isOnline()) {
            target.sendMessage(ChatColor.GREEN + "Вы получили приглашение в клан "
                    + ChatColor.YELLOW + fromClan.getName());
        }
    }

    /**
     * Уведомление о том, что приглашение в клан истекло (по таймеру).
     */
    public void notifyInvitationExpired(Clan fromClan, String targetPlayerName) {
        sendMessageToRoles(
                fromClan,
                List.of("Лидер", "Заместитель"),
                ChatColor.RED + "Приглашение для игрока " + targetPlayerName
                        + " в клан " + fromClan.getName() + " истекло."
        );
        Player target = Bukkit.getPlayer(targetPlayerName);
        if (target != null && target.isOnline()) {
            target.sendMessage(ChatColor.RED + "Ваше приглашение в клан "
                    + ChatColor.YELLOW + fromClan.getName() + ChatColor.RED + " истекло.");
        }
    }

    // Другие методы уведомлений можно добавлять по мере необходимости
    // (например, notifyPlayerLeftClan, notifyClanLeaderChanged и т.д.)
}
