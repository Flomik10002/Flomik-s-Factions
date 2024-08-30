package org.flomik.flomiksFactions.commands.clan.handlers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.flomik.flomiksFactions.commands.clan.Clan;
import org.flomik.flomiksFactions.commands.clan.ClanManager;

public class ListCommandHandler {

    private final ClanManager clanManager;

    public ListCommandHandler(ClanManager clanManager) {
        this.clanManager = clanManager;
    }

    public boolean handleCommand(Player player) {
        if (clanManager.getClans().isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "Список кланов пуст");
            return false;
        }

        StringBuilder message = new StringBuilder(ChatColor.GREEN + "**** " + ChatColor.WHITE + "Список кланов:" + ChatColor.GREEN + " ****" + "\n");
        for (Clan clan : clanManager.getClans().values()) {
            int memberCount = clan.getMembers().size();
            int onlineMemberCount = getOnlineMembersCount(clan);
            int maxStrength = memberCount * 10; // Максимально допустимая сила (10 на участника)

            // Форматирование строки с параметрами клана
            message.append(ChatColor.AQUA).append(clan.getName())  // Название клана
                    .append(ChatColor.GOLD).append(" - Рейтинг: ").append(ChatColor.YELLOW).append("N/A") // Рейтинг
                    .append(ChatColor.GOLD).append(" - Онлайн ").append(ChatColor.YELLOW).append(onlineMemberCount).append("/").append(memberCount) // Онлайн
                    .append(ChatColor.GOLD).append(" - Земли/Сила/Макс. Сила: ").append(ChatColor.YELLOW).append("0/0/").append(maxStrength).append("\n"); // Земли/Сила/Макс. Сила
        }
        player.sendMessage(message.toString());
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
