package org.flomik.FlomiksFactions.commands.handlers.clanInteractions;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.flomik.FlomiksFactions.clan.Clan;
import org.flomik.FlomiksFactions.clan.managers.ClanManager;

public class NameHandler {

    private final ClanManager clanManager;

    public NameHandler(ClanManager clanManager) {
        this.clanManager = clanManager;
    }

    public boolean handleCommand(Player player, String[] args) {
        if (args.length > 1) {
            String newClanName = args[1];

            final int MAX_NAME_LENGTH = 14;


            boolean wasTruncated = newClanName.length() > MAX_NAME_LENGTH;
            if (wasTruncated) {
                newClanName = newClanName.substring(0, MAX_NAME_LENGTH);
            }


            if (!newClanName.matches("[a-zA-Z0-9.!?]+")) {
                player.sendMessage(ChatColor.RED + "Название клана может содержать только буквы, цифры и символы: .,!?");
                return true;
            }

            if (newClanName.toLowerCase().startsWith("shrine")) {
                player.sendMessage(ChatColor.RED + "АЙ АЙ АЙ НЕ ХОРОШО ТАК ДЕЛАТЬ!!");
                return true;
            }


            Clan playerClan = clanManager.getPlayerClan(player.getName());
            if (playerClan == null) {
                player.sendMessage(ChatColor.RED + "Вы не состоите в клане.");
                return true;
            }


            String playerRole = playerClan.getRole(player.getName());
            if (!playerRole.equals("Лидер")) {
                player.sendMessage(ChatColor.RED + "Только Лидер клана может переименовать клан.");
                return true;
            }


            if (newClanName == null || newClanName.isEmpty()) {
                player.sendMessage(ChatColor.RED + "Название клана не может быть пустым.");
                return true;
            }


            for (Clan existingClan : clanManager.getAllClans()) {
                if (existingClan.getName().equalsIgnoreCase(newClanName)) {
                    player.sendMessage(ChatColor.RED + "Клан с таким названием уже существует.");
                    return true;
                }
            }

            String oldClanName = playerClan.getName();


            updateAllianceNamesBeforeRename(oldClanName, newClanName);


            playerClan.renameClan(newClanName);
            clanManager.updateClan(playerClan);


            updateAllianceNamesAfterRename(oldClanName);


            clanManager.sendClanMessage(playerClan, ChatColor.GREEN + "Клан " + ChatColor.YELLOW + oldClanName + ChatColor.GREEN + " был переименован в " + ChatColor.YELLOW + newClanName + ChatColor.GREEN + ".");

            if (wasTruncated) {
                player.sendMessage(ChatColor.YELLOW + "Название было слишком длинным и было обрезано до 14 символов.");
            }

            return false;
        } else {
            TextComponent usageMessage = new TextComponent(ChatColor.YELLOW + "Использование: ");
            TextComponent clickCommand = new TextComponent(ChatColor.GOLD + "/clan rename <название>");
            clickCommand.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/clan rename "));
            usageMessage.addExtra(clickCommand);
            player.spigot().sendMessage(usageMessage);
        }
        return true;
    }


    private void updateAllianceNamesAfterRename(String oldClanName) {

        for (Clan clan : clanManager.getAllClans()) {
            if (clan.getAlliances().contains(oldClanName)) {
                clan.removeAllianceByName(oldClanName);
                clanManager.updateClan(clan);
            }
        }
    }


    private void updateAllianceNamesBeforeRename(String oldClanName, String newClanName) {

        for (Clan clan : clanManager.getAllClans()) {
            if (clan.getAlliances().contains(oldClanName)) {
                clan.addAllianceByName(newClanName);
                clanManager.updateClan(clan);
            }
        }
    }
}