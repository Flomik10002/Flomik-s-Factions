package org.flomik.flomiksFactions.commands.clan.handlers;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.flomik.flomiksFactions.commands.clan.Clan;
import org.flomik.flomiksFactions.commands.clan.ClanManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

        int page = 1; // Номер страницы по умолчанию
        if (args.length > 1) {
            try {
                page = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Неверный номер страницы.");
                return false;
            }
        }

        int clansPerPage = 1; // Количество кланов на странице
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

            // Форматирование строки с параметрами клана
            message.append(ChatColor.AQUA).append(clan.getName())  // Название клана
                    .append(ChatColor.GOLD).append(" - Рейтинг: ").append(ChatColor.YELLOW).append("N/A") // Рейтинг
                    .append(ChatColor.GOLD).append(" - Онлайн ").append(ChatColor.YELLOW).append(onlineMemberCount).append("/").append(memberCount) // Онлайн
                    .append(ChatColor.GOLD).append(" - Земли/Сила/Макс. Сила: ").append(ChatColor.YELLOW).append(clan.getLands()).append("/").append(clan.getStrength()).append("/").append(clan.getMembers().size() * 10).append("\n"); // Земли/Сила/Макс. Сила
        }

        // Создаем компоненты для навигации
        TextComponent prevPage = new TextComponent(ChatColor.GREEN + "[<< Пред]");
        TextComponent pageNumber = new TextComponent(ChatColor.YELLOW + " Страница " + page + "/" + maxPages + " ");
        TextComponent nextPage = new TextComponent(ChatColor.GREEN + "[След >>]");

        // Устанавливаем кликабельные события для стрелочек
        if (page > 1) {
            prevPage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan list " + (page - 1)));
        } else {
            prevPage.setColor(net.md_5.bungee.api.ChatColor.GRAY); // Если нет предыдущей страницы, делаем текст серым
        }

        if (page < maxPages) {
            nextPage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan list " + (page + 1)));
        } else {
            nextPage.setColor(net.md_5.bungee.api.ChatColor.GRAY); // Если нет следующей страницы, делаем текст серым
        }

        // Создаем основной компонент с текстом и навигацией
        TextComponent mainMessage = new TextComponent(TextComponent.fromLegacyText(message.toString()));
        mainMessage.addExtra(prevPage);
        mainMessage.addExtra(pageNumber);
        mainMessage.addExtra(nextPage);

        // Отправляем сообщение игроку
        player.spigot().sendMessage(mainMessage);
        return true;
    }

    // Метод для подсчета количества онлайн участников
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
