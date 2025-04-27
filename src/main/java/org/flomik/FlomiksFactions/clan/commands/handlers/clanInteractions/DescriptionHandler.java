package org.flomik.FlomiksFactions.clan.commands.handlers.clanInteractions; //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.flomik.FlomiksFactions.clan.Clan;
import org.flomik.FlomiksFactions.clan.managers.ClanManager;
import org.flomik.FlomiksFactions.clan.notifications.ClanNotificationService;

import java.util.Arrays;

public class DescriptionHandler { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression

    private final ClanManager clanManager; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private final ClanNotificationService clanNotificationService;

    public DescriptionHandler(ClanManager clanManager, ClanNotificationService clanNotificationService) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        this.clanManager = clanManager;
        this.clanNotificationService = clanNotificationService;
    }

    public boolean handleCommand(Player player, String[] args) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        if (args.length > 1) { //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression
            String newDescription = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

            final int MAX_DESCRIPTION_LENGTH = 48; //NOPMD - suppressed LongVariable - TODO explain reason for suppression //NOPMD - suppressed LongVariable - TODO explain reason for suppression //NOPMD - suppressed LongVariable - TODO explain reason for suppression


            boolean wasTruncated = newDescription.length() > MAX_DESCRIPTION_LENGTH; //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            if (wasTruncated) {
                newDescription = newDescription.substring(0, MAX_DESCRIPTION_LENGTH);
            }

            Clan playerClan = clanManager.getPlayerClan(player.getName()); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            if (playerClan == null) {
                player.sendMessage(ChatColor.RED + "Вы не состоите в клане.");
                return true; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
            }


            String playerRole = playerClan.getRole(player.getName()); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            if (!playerRole.equals("Лидер") && !playerRole.equals("Заместитель")) { //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression
                player.sendMessage(ChatColor.RED + "Только Лидер и Заместитель клана могут переименовать клан.");
                return true; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
            }

            if (!newDescription.matches("[a-zA-Z0-9.!?]+")) {
                player.sendMessage(ChatColor.RED + "Описание клана может содержать только буквы, цифры и символы: .,!?");
                return true; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
            }


            playerClan.setDescription(newDescription);
            clanManager.updateClan(playerClan);


            clanNotificationService.sendClanMessage(playerClan, ChatColor.GREEN + "Описание клана успешно обновлено!");

            if (wasTruncated) {
                player.sendMessage(ChatColor.YELLOW + "Описание было слишком длинным и было обрезано до 48 символов.");
            }

            return false; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
        } else {
            TextComponent usageMessage = new TextComponent(ChatColor.YELLOW + "Использование: "); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            TextComponent clickCommand = new TextComponent(ChatColor.GOLD + "/clan desc <описание>"); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            clickCommand.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/clan desc "));
            usageMessage.addExtra(clickCommand);
            player.spigot().sendMessage(usageMessage);
        }
        return true;
    }
}
