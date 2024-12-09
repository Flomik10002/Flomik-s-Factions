package org.flomik.FlomiksFactions.clan.commands;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.flomik.FlomiksFactions.clan.Clan;
import org.flomik.FlomiksFactions.clan.ClanManager;

import java.util.ArrayList;
import java.util.List;

public class ListCommandHandler {

    private final ClanManager clanManager;

    public ListCommandHandler(ClanManager clanManager) {
        this.clanManager = clanManager;
    }

    public boolean handleCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Команду может выполнить только игрок.");
            return false;
        }

        Player player = (Player) sender;

        if (clanManager.getClans().isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "Список кланов пуст");
            return false;
        }

        int page = 1;
        if (args.length > 1) {
            try {
                page = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Неверный номер страницы.");
                return false;
            }
        }

        int clansPerPage = 5;
        int totalClans = clanManager.getClans().size();
        int maxPages = (int) Math.ceil((double) totalClans / clansPerPage);

        if (page > maxPages || page <= 0) {
            player.sendMessage(ChatColor.RED + "Страница не найдена. Введите число от 1 до " + maxPages + ".");
            return false;
        }

        List<Clan> clansList = new ArrayList<>(clanManager.getClans().values());
        StringBuilder message = new StringBuilder(ChatColor.GREEN + "**** " + ChatColor.WHITE + "Список кланов (Страница " + page + "/" + maxPages + "):" + ChatColor.GREEN + " ****" + "\n");

        int startIndex = (page - 1) * clansPerPage;
        int endIndex = Math.min(startIndex + clansPerPage, totalClans);

        for (int i = startIndex; i < endIndex; i++) {
            Clan clan = clansList.get(i);
            int memberCount = clan.getMembers().size();
            int onlineMemberCount = getOnlineMembersCount(clan);


            message.append(ChatColor.AQUA).append(clan.getName())
                    .append(ChatColor.GOLD).append(" - Рейтинг: ").append(ChatColor.YELLOW).append("N/A")
                    .append(ChatColor.GOLD).append(" - Онлайн ").append(ChatColor.YELLOW).append(onlineMemberCount).append("/").append(memberCount)
                    .append(ChatColor.GOLD).append(" - Земли/Сила/Макс. Сила: ").append(ChatColor.YELLOW).append(clan.getLands()).append("/").append(clan.getStrength()).append("/").append(clan.getMembers().size() * 10).append("\n");
        }


        TextComponent prevPage = new TextComponent(ChatColor.GREEN + "[<< Пред]");
        TextComponent pageNumber = new TextComponent(ChatColor.YELLOW + " Страница " + page + "/" + maxPages + " ");
        TextComponent nextPage = new TextComponent(ChatColor.GREEN + "[След >>]");


        if (page > 1) {
            prevPage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan list " + (page - 1)));
        } else {
            prevPage.setColor(net.md_5.bungee.api.ChatColor.GRAY);
        }

        if (page < maxPages) {
            nextPage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan list " + (page + 1)));
        } else {
            nextPage.setColor(net.md_5.bungee.api.ChatColor.GRAY);
        }


        TextComponent mainMessage = new TextComponent(TextComponent.fromLegacyText(message.toString()));
        mainMessage.addExtra(prevPage);
        mainMessage.addExtra(pageNumber);
        mainMessage.addExtra(nextPage);


        player.spigot().sendMessage(mainMessage);
        return true;
    }


    private int getOnlineMembersCount(Clan clan) {
        int onlineCount = 0;
        for (String member : clan.getMembers()) {
            Player player = Bukkit.getPlayer(member);
            if (player != null && player.isOnline()) {
                onlineCount++;
            }
        }
        return onlineCount;
    }
}
