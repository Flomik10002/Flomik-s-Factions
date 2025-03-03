package org.flomik.FlomiksFactions.clan.commands.handlers.clanInteractions; //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.flomik.FlomiksFactions.clan.Clan;
import org.flomik.FlomiksFactions.clan.managers.ClanManager;

public class NameHandler { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression

    private final ClanManager clanManager; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression

    public NameHandler(ClanManager clanManager) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        this.clanManager = clanManager;
    }

    public boolean handleCommand(Player player, String[] args) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        if (args.length > 1) { //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression
            String newClanName = args[1];

            final int MAX_NAME_LENGTH = 14; //NOPMD - suppressed LocalVariableNamingConventions - TODO explain reason for suppression //NOPMD - suppressed LocalVariableNamingConventions - TODO explain reason for suppression //NOPMD - suppressed LocalVariableNamingConventions - TODO explain reason for suppression


            boolean wasTruncated = newClanName.length() > MAX_NAME_LENGTH; //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            if (wasTruncated) {
                newClanName = newClanName.substring(0, MAX_NAME_LENGTH);
            }


            if (!newClanName.matches("[a-zA-Z0-9.!?]+")) {
                player.sendMessage(ChatColor.RED + "Название клана может содержать только буквы, цифры и символы: .,!?");
                return true; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
            }

            if (newClanName.toLowerCase().startsWith("shrine")) { //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression
                player.sendMessage(ChatColor.RED + "АЙ АЙ АЙ НЕ ХОРОШО ТАК ДЕЛАТЬ!!");
                return true; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
            }


            Clan playerClan = clanManager.getPlayerClan(player.getName()); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            if (playerClan == null) {
                player.sendMessage(ChatColor.RED + "Вы не состоите в клане.");
                return true; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
            }


            String playerRole = playerClan.getRole(player.getName()); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            if (!playerRole.equals("Лидер")) { //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression
                player.sendMessage(ChatColor.RED + "Только Лидер клана может переименовать клан.");
                return true; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
            }


            if (newClanName == null || newClanName.isEmpty()) {
                player.sendMessage(ChatColor.RED + "Название клана не может быть пустым.");
                return true; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
            }


            for (Clan existingClan : clanManager.getAllClans()) { //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                if (existingClan.getName().equalsIgnoreCase(newClanName)) {
                    player.sendMessage(ChatColor.RED + "Клан с таким названием уже существует.");
                    return true; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
                }
            }

            String oldClanName = playerClan.getName(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression


            updateAllianceNamesBeforeRename(oldClanName, newClanName);


            playerClan.renameClan(newClanName);
            clanManager.updateClan(playerClan);


            updateAllianceNamesAfterRename(oldClanName);


            clanManager.sendClanMessage(playerClan, ChatColor.GREEN + "Клан " + ChatColor.YELLOW + oldClanName + ChatColor.GREEN + " был переименован в " + ChatColor.YELLOW + newClanName + ChatColor.GREEN + ".");

            if (wasTruncated) {
                player.sendMessage(ChatColor.YELLOW + "Название было слишком длинным и было обрезано до 14 символов.");
            }

            return false; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
        } else {
            TextComponent usageMessage = new TextComponent(ChatColor.YELLOW + "Использование: "); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            TextComponent clickCommand = new TextComponent(ChatColor.GOLD + "/clan rename <название>"); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            clickCommand.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/clan rename "));
            usageMessage.addExtra(clickCommand);
            player.spigot().sendMessage(usageMessage);
        }
        return true;
    }


    private void updateAllianceNamesAfterRename(String oldClanName) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression

        for (Clan clan : clanManager.getAllClans()) { //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            if (clan.getAlliances().contains(oldClanName)) {
                clan.removeAllianceByName(oldClanName);
                clanManager.updateClan(clan);
            }
        }
    }


    private void updateAllianceNamesBeforeRename(String oldClanName, String newClanName) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression

        for (Clan clan : clanManager.getAllClans()) { //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            if (clan.getAlliances().contains(oldClanName)) {
                clan.addAllianceByName(newClanName);
                clanManager.updateClan(clan);
            }
        }
    }
}