package org.flomik.FlomiksFactions.clan.commands.handlers.playerInteractions; //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.flomik.FlomiksFactions.clan.Clan;
import org.flomik.FlomiksFactions.clan.managers.ClanManager;
import org.flomik.FlomiksFactions.clan.notifications.ClanNotificationService;

import java.util.Objects;

public class DemoteHandler { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression

    private final ClanManager clanManager; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private final ClanNotificationService clanNotificationService;

    public DemoteHandler(ClanManager clanManager, ClanNotificationService clanNotificationService) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        this.clanManager = clanManager;
        this.clanNotificationService = clanNotificationService;
    }

    public boolean handleCommand(Player player, String[] args) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        if (args.length > 1) { //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression
            String targetDemPlayerName = args[1]; //NOPMD - suppressed LongVariable - TODO explain reason for suppression //NOPMD - suppressed LongVariable - TODO explain reason for suppression //NOPMD - suppressed LongVariable - TODO explain reason for suppression
            Clan clan = clanManager.getPlayerClan(player.getName()); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression

            if (clan == null) {
                player.sendMessage(ChatColor.RED + "Не удалось найти ваш клан.");
                return false; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
            }

            String playerRole = clan.getRole(player.getName()); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            if (!playerRole.equals("Лидер") && !playerRole.equals("Заместитель")) { //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression
                player.sendMessage(ChatColor.RED + "Только Лидер или Заместитель могут понижать ранг.");
                return false; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
            }

            try {
                clan.demoteMember(player.getName(), targetDemPlayerName);
                clanNotificationService.sendClanMessage(clan, ChatColor.GREEN + "Игрок " + ChatColor.YELLOW + targetDemPlayerName + ChatColor.GREEN + " понижен в должности.");

                String targetPlayerRole = clan.getRole(targetDemPlayerName); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                Player targetPlayer = Bukkit.getPlayerExact(targetDemPlayerName); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                if(Objects.equals(targetPlayerRole, "Воин")) {
                    this.clanManager.removePlayerFromClanRegions(targetPlayer, clan);
                    this.clanManager.addPlayerToClanRegionsAsMember(targetPlayer, clan);
                }
                if(Objects.equals(targetPlayerRole, "Рекрут")) {
                    this.clanManager.removePlayerFromClanRegions(targetPlayer, clan);
                }
            } catch (IllegalArgumentException e) {
                player.sendMessage(ChatColor.RED + e.getMessage());
            }
            clanManager.saveClan(clan);
            return true; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
        } else {
            TextComponent usageMessage = new TextComponent(ChatColor.YELLOW + "Использование: "); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            TextComponent clickCommand = new TextComponent(ChatColor.GOLD + "/clan demote <игрок>"); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            clickCommand.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/clan demote "));
            usageMessage.addExtra(clickCommand);
            player.spigot().sendMessage(usageMessage);
        }
        return true;
    }
}
