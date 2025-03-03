package org.flomik.FlomiksFactions.clan.commands.handlers.playerInteractions; //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.flomik.FlomiksFactions.clan.Clan;
import org.flomik.FlomiksFactions.clan.managers.ClanManager;
import org.flomik.FlomiksFactions.player.PlayerDataHandler;

import java.text.SimpleDateFormat;

public class InfoHandler { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private final ClanManager clanManager; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private final PlayerDataHandler playerDataHandler; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression

    public InfoHandler(ClanManager clanManager, PlayerDataHandler playerDataHandler) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        this.clanManager = clanManager;
        this.playerDataHandler = playerDataHandler;
    }

    public boolean handleCommand(Player player, String[] args) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        String arg = args.length > 1 ? args[1] : ""; //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        Clan clan = null; //NOPMD - suppressed UnusedAssignment - TODO explain reason for suppression //NOPMD - suppressed UnusedAssignment - TODO explain reason for suppression //NOPMD - suppressed UnusedAssignment - TODO explain reason for suppression

        if (arg.isEmpty()) {

            clan = clanManager.getPlayerClan(player.getName());
            if (clan == null) {
                player.sendMessage(ChatColor.RED + "Вы не состоите в клане.");
                return true; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
            }
        } else {


            if (playerDataHandler.hasPlayerData(arg)) {

                clan = clanManager.getPlayerClan(arg);
                if (clan == null) {
                    player.sendMessage(ChatColor.RED + "Игрок не состоит в клане.");
                    return true; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
                }
            } else {

                clan = clanManager.getClan(arg.toLowerCase()); //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression
                if (clan == null) {
                    player.sendMessage(ChatColor.RED + "Клан с таким названием не найден.");
                    return true; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
                }
            }
        }


        StringBuilder info = new StringBuilder(); //NOPMD - suppressed InsufficientStringBufferDeclaration - TODO explain reason for suppression //NOPMD - suppressed InsufficientStringBufferDeclaration - TODO explain reason for suppression //NOPMD - suppressed InsufficientStringBufferDeclaration - TODO explain reason for suppression
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); //NOPMD - suppressed SimpleDateFormatNeedsLocale - TODO explain reason for suppression //NOPMD - suppressed SimpleDateFormatNeedsLocale - TODO explain reason for suppression //NOPMD - suppressed SimpleDateFormatNeedsLocale - TODO explain reason for suppression

        info.append(ChatColor.GREEN).append("**** ").append(ChatColor.WHITE).append("Информация о клане: ").append(clan.getName()).append(ChatColor.GREEN).append(" ****\n"); //NOPMD - suppressed ConsecutiveAppendsShouldReuse - TODO explain reason for suppression //NOPMD - suppressed ConsecutiveAppendsShouldReuse - TODO explain reason for suppression //NOPMD - suppressed ConsecutiveAppendsShouldReuse - TODO explain reason for suppression
        info.append(ChatColor.GOLD).append("Дата создания: ").append(ChatColor.YELLOW).append(dateFormat.format(clan.getCreationDate())).append("\n"); //NOPMD - suppressed ConsecutiveAppendsShouldReuse - TODO explain reason for suppression //NOPMD - suppressed ConsecutiveAppendsShouldReuse - TODO explain reason for suppression //NOPMD - suppressed ConsecutiveAppendsShouldReuse - TODO explain reason for suppression
        info.append(ChatColor.GOLD).append("Описание: ").append(ChatColor.YELLOW).append(clan.getDescription()).append("\n"); //NOPMD - suppressed ConsecutiveAppendsShouldReuse - TODO explain reason for suppression //NOPMD - suppressed ConsecutiveAppendsShouldReuse - TODO explain reason for suppression //NOPMD - suppressed ConsecutiveAppendsShouldReuse - TODO explain reason for suppression
        info.append(ChatColor.GOLD).append("Земли/Сила/Макс. Сила: ").append(ChatColor.YELLOW).append(clan.getLands()).append("/").append(clan.getStrength()).append("/").append(clan.getMembers().size() * 10).append("\n"); //NOPMD - suppressed ConsecutiveAppendsShouldReuse - TODO explain reason for suppression //NOPMD - suppressed ConsecutiveAppendsShouldReuse - TODO explain reason for suppression //NOPMD - suppressed ConsecutiveAppendsShouldReuse - TODO explain reason for suppression
        info.append(ChatColor.GOLD).append("Альянсы: ").append(ChatColor.YELLOW).append(String.join(", ", clan.getAlliances())).append("\n"); //NOPMD - suppressed ConsecutiveAppendsShouldReuse - TODO explain reason for suppression //NOPMD - suppressed ConsecutiveAppendsShouldReuse - TODO explain reason for suppression //NOPMD - suppressed ConsecutiveAppendsShouldReuse - TODO explain reason for suppression

        info.append(ChatColor.GOLD).append("Уровень: ").append(ChatColor.YELLOW).append(clan.getLevel()).append(ChatColor.GOLD + " (" + ChatColor.GREEN + clan.getClanXp() + ChatColor.GRAY + "/" + ChatColor.YELLOW + clan.getRequiredXpForNextLevel(clan.getLevel()) + ChatColor.GOLD + ") до след. lvl").append("\n"); //NOPMD - suppressed InefficientStringBuffering - TODO explain reason for suppression //NOPMD - suppressed InefficientStringBuffering - TODO explain reason for suppression //NOPMD - suppressed InefficientStringBuffering - TODO explain reason for suppression
        info.append(ChatColor.GOLD).append("Онлайн: ").append(ChatColor.YELLOW).append(getOnlineMembersCount(clan)).append("/").append(clan.getMembers().size()).append("\n"); //NOPMD - suppressed ConsecutiveAppendsShouldReuse - TODO explain reason for suppression //NOPMD - suppressed ConsecutiveAppendsShouldReuse - TODO explain reason for suppression //NOPMD - suppressed ConsecutiveAppendsShouldReuse - TODO explain reason for suppression

        info.append(ChatColor.GOLD).append("Состав клана: ");
        for (String member : clan.getMembers()) { //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            String playerRole = clan.getRole(member); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                info.append(ChatColor.GREEN).append(member)
                        .append(ChatColor.WHITE).append(" - ")
                        .append(ChatColor.DARK_GREEN).append(playerRole)
                        .append(ChatColor.WHITE).append(" | ");

        }

        player.sendMessage(info.toString());
        return true;
    }

    private int getOnlineMembersCount(Clan clan) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        int onlineCount = 0;
        for (String member : clan.getMembers()) { //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            Player player = Bukkit.getPlayer(member); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            if (player != null && player.isOnline()) {
                onlineCount++;
            }
        }
        return onlineCount;
    }
}

