package org.flomik.flomiksFactions.clan.commands.clanInteractions;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.flomik.flomiksFactions.clan.Clan;
import org.flomik.flomiksFactions.clan.ClanManager;

public class NameCommandHandler {

    private final ClanManager clanManager;

    public NameCommandHandler(ClanManager clanManager) {
        this.clanManager = clanManager;
    }

    public boolean handleCommand(Player player, String[] args) {
        if (args.length > 1) {
            String newClanName = args[1];

            final int MAX_NAME_LENGTH = 14;

            // Если имя клана слишком длинное, обрезаем его до 14 символов
            boolean wasTruncated = newClanName.length() > MAX_NAME_LENGTH;
            if (wasTruncated) {
                newClanName = newClanName.substring(0, MAX_NAME_LENGTH);
            }

            // Регулярное выражение для проверки, чтобы название содержало только буквы, цифры и допустимые символы .,!?:
            if (!newClanName.matches("[a-zA-Z0-9.!?]+")) {
                player.sendMessage(ChatColor.RED + "Название клана может содержать только буквы, цифры и символы: .,!?");
                return true;
            }

            if (newClanName.toLowerCase().startsWith("shrine")) {
                player.sendMessage(ChatColor.RED + "АЙ АЙ АЙ НЕ ХОРОШО ТАК ДЕЛАТЬ!!");
                return true;
            }

            // Получаем текущий клан игрока
            Clan playerClan = clanManager.getPlayerClan(player.getName());
            if (playerClan == null) {
                player.sendMessage(ChatColor.RED + "Вы не состоите в клане.");
                return true;
            }

            // Проверяем, является ли игрок Лидером клана
            String playerRole = playerClan.getRole(player.getName());
            if (!playerRole.equals("Лидер")) {
                player.sendMessage(ChatColor.RED + "Только Лидер клана может переименовать клан.");
                return true;
            }

            // Проверка на допустимость нового имени
            if (newClanName == null || newClanName.isEmpty()) {
                player.sendMessage(ChatColor.RED + "Название клана не может быть пустым.");
                return true;
            }

            // Проверяем, существует ли уже клан с таким названием
            for (Clan existingClan : clanManager.getAllClans()) {
                if (existingClan.getName().equalsIgnoreCase(newClanName)) {
                    player.sendMessage(ChatColor.RED + "Клан с таким названием уже существует.");
                    return true;
                }
            }

            String oldClanName = playerClan.getName(); // Сохраняем оригинальное имя до переименования

            // Обновляем альянсы после переименования
            updateAllianceNamesBeforeRename(oldClanName, newClanName);

            // Переименовываем клан
            playerClan.renameClan(newClanName);
            clanManager.updateClan(playerClan); // Сохраняем изменения

            // Обновляем альянсы до переименования
            updateAllianceNamesAfterRename(oldClanName);

            // Отправляем сообщение о переименовании
            clanManager.sendClanMessage(playerClan, ChatColor.GREEN + "Клан " + ChatColor.YELLOW + oldClanName + ChatColor.GREEN + " был переименован в " + ChatColor.YELLOW + newClanName + ChatColor.GREEN + ".");

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

    // Метод для удаления старого имени из альянсов до переименования
    private void updateAllianceNamesAfterRename(String oldClanName) {
        // Проходим по всем кланам и ищем те, кто состоит в альянсе с переименовываемым кланом
        for (Clan clan : clanManager.getAllClans()) {
            if (clan.getAlliances().contains(oldClanName)) {
                clan.removeAllianceByName(oldClanName); // Удаляем старое имя клана
                clanManager.updateClan(clan); // Сохраняем изменения
            }
        }
    }

    // Метод для добавления нового имени в альянсы после переименования
    private void updateAllianceNamesBeforeRename(String oldClanName, String newClanName) {
        // Проходим по всем кланам и обновляем альянсы с новым именем клана
        for (Clan clan : clanManager.getAllClans()) {
            if (clan.getAlliances().contains(oldClanName)) {
                clan.addAllianceByName(newClanName); // Добавляем новое имя клана
                clanManager.updateClan(clan); // Сохраняем изменения
            }
        }
    }
}