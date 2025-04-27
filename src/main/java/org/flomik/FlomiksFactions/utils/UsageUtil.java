package org.flomik.FlomiksFactions.utils;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Утилитный класс для отправки подсказки (Usage) игроку.
 */
public final class UsageUtil {

    // Закрытый конструктор, чтобы нельзя было создать экземпляр
    private UsageUtil() {
    }

    /**
     * Отправляет подсказку по команде в формате:
     * "Использование: /clan moder <игрок>"
     * При этом при клике вставляется "/clan moder " (без <игрок>).
     *
     * @param player             Игрок, которому отсылаем подсказку.
     * @param command   Как хотим отобразить текст команды (например, "/clan moder <игрок>")
     */
    public static void sendUsageMessage(final Player player,
                                        final String command) {
        final TextComponent usageMessage = new TextComponent(ChatColor.YELLOW + "Использование: ");
        final TextComponent clickCommand = new TextComponent(ChatColor.GOLD + command);
        clickCommand.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, removeAngleBrackets(command)));
        usageMessage.addExtra(clickCommand);
        player.spigot().sendMessage(usageMessage);
    }

    public static TextComponent buildClickableCommand(final String displayed, final String commandToRun) {
        // displayed — то, что видит игрок (например "/clan ally FlomikFaction")
        // commandToRun — то, что вставится в чат ("/clan ally FlomikFaction") при клике
        TextComponent clickCommand = new TextComponent(ChatColor.YELLOW + displayed);
        clickCommand.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, commandToRun));
        return clickCommand;
    }


    private static String removeAngleBrackets(String input) {
        // Удаляем всё, что между < и >
        // (пробел остаётся, так что после "<игрок>" будет просто пустое место)
        return input.replaceAll("<[^>]*>", "");
    }
}
