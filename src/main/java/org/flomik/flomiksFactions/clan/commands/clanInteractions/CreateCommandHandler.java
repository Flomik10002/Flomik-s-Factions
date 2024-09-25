package org.flomik.flomiksFactions.clan.commands.clanInteractions;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.flomik.flomiksFactions.clan.ClanManager;

public class CreateCommandHandler {

    private final ClanManager clanManager;

    public CreateCommandHandler(ClanManager clanManager) {
        this.clanManager = clanManager;
    }

    public boolean handleCommand(Player player, String[] args) {
        if (args.length > 1) {
            String clanName = args[1];

            final int MAX_NAME_LENGTH = 14;

            boolean wasTruncated = clanName.length() > MAX_NAME_LENGTH;
            if (wasTruncated) {
                clanName = clanName.substring(0, MAX_NAME_LENGTH);
            }

            // Регулярное выражение для проверки, чтобы название содержало только буквы, цифры и допустимые символы .,!?:
            if (!clanName.matches("[a-zA-Z0-9.!?]+")) {
                player.sendMessage(ChatColor.RED + "Название клана может содержать только буквы, цифры и символы: .,!?");
                return true;
            }

            if (clanName.toLowerCase().startsWith("shrine")) {
                player.sendMessage(ChatColor.RED + "АЙ АЙ АЙ НЕ ХОРОШО ТАК ДЕЛАТЬ!!");
                return true;
            }

            try {
                clanManager.createClan(clanName, player.getName());
                Bukkit.broadcastMessage(ChatColor.GREEN + "Клан " + ChatColor.YELLOW + clanName + ChatColor.GREEN + " успешно создан!");

                if (wasTruncated) {
                    player.sendMessage(ChatColor.YELLOW + "Название было слишком длинным и было обрезано до 14 символов.");
                }
            } catch (IllegalArgumentException e) {
                player.sendMessage(ChatColor.RED + e.getMessage());
            }
        } else {
            // Сообщение об использовании команды
            TextComponent usageMessage = new TextComponent(ChatColor.YELLOW + "Пожалуйста, укажите название клана. Использование: ");
            TextComponent clickCommand = new TextComponent(ChatColor.GOLD + "/clan create <название>");
            clickCommand.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/clan create "));
            usageMessage.addExtra(clickCommand);
            player.spigot().sendMessage(usageMessage);
        }
        return true;
    }
}
