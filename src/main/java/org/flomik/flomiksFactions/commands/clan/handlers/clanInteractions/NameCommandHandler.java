package org.flomik.flomiksFactions.commands.clan.handlers.clanInteractions;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.flomik.flomiksFactions.commands.clan.Clan;
import org.flomik.flomiksFactions.commands.clan.ClanManager;

public class NameCommandHandler {

    private final ClanManager clanManager;

    public NameCommandHandler(ClanManager clanManager) {
        this.clanManager = clanManager;
    }

    public boolean handleCommand(Player player, String[] args) {
        if (args.length > 1) {
            String newClanName = args[1].toLowerCase();;

            final int MAX_NAME_LENGTH = 14;

            boolean wasTruncated = newClanName.length() > MAX_NAME_LENGTH;
            if (wasTruncated) {
                newClanName = newClanName.substring(0, MAX_NAME_LENGTH);
            }

            Clan playerClan = clanManager.getPlayerClan(player.getName());
            if (playerClan == null) {
                player.sendMessage(ChatColor.RED + "Вы не состоите в клане.");
                return true;
            }

            // Проверка, что игрок является владельцем клана
            String playerRole = playerClan.getRole(player.getName());
            if (!playerRole.equals("Лидер")) {
                player.sendMessage(ChatColor.RED + "Только Лидер клана может переименовать клан.");
                return true;
            }

            // Проверка на допустимость нового имени (например, длина, уникальность и т.д.)
            if (newClanName == null || newClanName.isEmpty()) {
                player.sendMessage(ChatColor.RED + "Название клана не может быть пустым.");
                return true;
            }

            clanManager.sendClanMessage(playerClan, ChatColor.GREEN + "Клан " + ChatColor.YELLOW + playerClan.getName() + ChatColor.GREEN + " был переименован в " + ChatColor.YELLOW + newClanName + ChatColor.GREEN + ".");

            // Переименовываем клан
            playerClan.renameClan(newClanName);
            clanManager.updateClan(playerClan);

            // Опционально, можно уведомить всех членов клана о смене имени

            if (wasTruncated) {
                player.sendMessage(ChatColor.YELLOW + "Название было слишком длинным и было обрезано до 14 символов.");
            }

            return false;
        } else {
            TextComponent usageMessage = new TextComponent(ChatColor.YELLOW + "Использование: ");
            TextComponent clickCommand = new TextComponent(ChatColor.GOLD + "/clan name <название>");
            clickCommand.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/clan name "));
            usageMessage.addExtra(clickCommand);
            player.spigot().sendMessage(usageMessage);
        }
        return true;
    }
}
