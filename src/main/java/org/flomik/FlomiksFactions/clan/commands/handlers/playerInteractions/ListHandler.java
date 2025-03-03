package org.flomik.FlomiksFactions.clan.commands.handlers.playerInteractions; //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.flomik.FlomiksFactions.clan.Clan;
import org.flomik.FlomiksFactions.clan.managers.ClanManager;

import java.util.ArrayList;
import java.util.List;

public class ListHandler { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression

    private final ClanManager clanManager; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression

    public ListHandler(ClanManager clanManager) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        this.clanManager = clanManager;
    }

    public boolean handleCommand(CommandSender sender, String[] args) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Команду может выполнить только игрок.");
            return false; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
        }

        Player player = (Player) sender; //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression

        if (clanManager.getClans().isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "Список кланов пуст");
            return false; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
        }

        int page = 1;
        if (args.length > 1) { //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression
            try {
                page = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Неверный номер страницы.");
                return false; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
            }
        }

        int clansPerPage = 5; //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        int totalClans = clanManager.getClans().size(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        int maxPages = (int) Math.ceil((double) totalClans / clansPerPage); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression

        if (page > maxPages || page <= 0) {
            player.sendMessage(ChatColor.RED + "Страница не найдена. Введите число от 1 до " + maxPages + ".");
            return false; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
        }

        List<Clan> clansList = new ArrayList<>(clanManager.getClans().values()); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        StringBuilder message = new StringBuilder(ChatColor.GREEN + "**** " + ChatColor.WHITE + "Список кланов (Страница " + page + "/" + maxPages + "):" + ChatColor.GREEN + " ****" + "\n"); //NOPMD - suppressed InefficientStringBuffering - TODO explain reason for suppression //NOPMD - suppressed InefficientStringBuffering - TODO explain reason for suppression //NOPMD - suppressed InefficientStringBuffering - TODO explain reason for suppression

        int startIndex = (page - 1) * clansPerPage; //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        int endIndex = Math.min(startIndex + clansPerPage, totalClans); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression

        for (int i = startIndex; i < endIndex; i++) {
            Clan clan = clansList.get(i); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            int memberCount = clan.getMembers().size(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            int onlineMemberCount = getOnlineMembersCount(clan); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression


            message.append(ChatColor.AQUA).append(clan.getName())
                    .append(ChatColor.GOLD).append(" - Рейтинг: ").append(ChatColor.YELLOW).append("N/A")
                    .append(ChatColor.GOLD).append(" - Онлайн ").append(ChatColor.YELLOW).append(onlineMemberCount).append("/").append(memberCount) //NOPMD - suppressed AppendCharacterWithChar - TODO explain reason for suppression //NOPMD - suppressed AppendCharacterWithChar - TODO explain reason for suppression //NOPMD - suppressed AppendCharacterWithChar - TODO explain reason for suppression
                    .append(ChatColor.GOLD).append(" - Земли/Сила/Макс. Сила: ").append(ChatColor.YELLOW).append(clan.getLands()).append("/").append(clan.getStrength()).append("/").append(clan.getMembers().size() * 10).append("\n"); //NOPMD - suppressed AppendCharacterWithChar - TODO explain reason for suppression //NOPMD - suppressed AppendCharacterWithChar - TODO explain reason for suppression //NOPMD - suppressed AppendCharacterWithChar - TODO explain reason for suppression
        }


        TextComponent prevPage = new TextComponent(ChatColor.GREEN + "[<< Пред]"); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        TextComponent pageNumber = new TextComponent(ChatColor.YELLOW + " Страница " + page + "/" + maxPages + " "); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        TextComponent nextPage = new TextComponent(ChatColor.GREEN + "[След >>]"); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression


        if (page > 1) { //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression
            prevPage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan list " + (page - 1)));
        } else {
            prevPage.setColor(net.md_5.bungee.api.ChatColor.GRAY);
        }

        if (page < maxPages) {
            nextPage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan list " + (page + 1)));
        } else {
            nextPage.setColor(net.md_5.bungee.api.ChatColor.GRAY);
        }


        TextComponent mainMessage = new TextComponent(TextComponent.fromLegacyText(message.toString())); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        mainMessage.addExtra(prevPage);
        mainMessage.addExtra(pageNumber);
        mainMessage.addExtra(nextPage);


        player.spigot().sendMessage(mainMessage);
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
