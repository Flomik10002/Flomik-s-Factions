package org.flomik.flomiksFactions.commands.clan.handlers.clanInteractions;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.flomik.flomiksFactions.commands.clan.Clan;
import org.flomik.flomiksFactions.commands.clan.ClanManager;

import java.util.Arrays;

public class DescCommandHandler {

    private final ClanManager clanManager;

    public DescCommandHandler(ClanManager clanManager) {
        this.clanManager = clanManager;
    }

    public boolean handleCommand(Player player, String[] args) {
        if (args.length > 1) {
            String newDescription = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

            final int MAX_DESCRIPTION_LENGTH = 48;


            boolean wasTruncated = newDescription.length() > MAX_DESCRIPTION_LENGTH;
            if (wasTruncated) {
                newDescription = newDescription.substring(0, MAX_DESCRIPTION_LENGTH);
            }

            Clan playerClan = clanManager.getPlayerClan(player.getName());
            if (playerClan == null) {
                player.sendMessage(ChatColor.RED + "Вы не состоите в клане.");
                return true;
            }

            // Проверка, что игрок является владельцем клана
            String playerRole = playerClan.getRole(player.getName());
            if (!playerRole.equals("Лидер") && !playerRole.equals("Заместитель")) {
                player.sendMessage(ChatColor.RED + "Только Лидер и Заместитель клана можгут переименовать клан.");
                return true;
            }

            if (!newDescription.matches("[a-zA-Z0-9.!?]+")) {
                player.sendMessage(ChatColor.RED + "Описание клана может содержать только буквы, цифры и символы: .,!?");
                return true;
            }

            // Переименовываем клан
            playerClan.setDescription(newDescription);
            clanManager.updateClan(playerClan);

            // Опционально, можно уведомить всех членов клана о смене имени
            clanManager.sendClanMessage(playerClan, ChatColor.GREEN + "Описание клана успешно обновлено!");

            if (wasTruncated) {
                player.sendMessage(ChatColor.YELLOW + "Описание было слишком длинным и было обрезано до 48 символов.");
            }

            return false;
        } else {
            TextComponent usageMessage = new TextComponent(ChatColor.YELLOW + "Использование: ");
            TextComponent clickCommand = new TextComponent(ChatColor.GOLD + "/clan desc <описание>");
            clickCommand.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/clan desc "));
            usageMessage.addExtra(clickCommand);
            player.spigot().sendMessage(usageMessage);
        }
        return true;
    }
}
