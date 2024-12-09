package org.flomik.flomiksFactions.clan.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.flomik.flomiksFactions.clan.Clan;
import org.flomik.flomiksFactions.clan.ClanManager;
import org.flomik.flomiksFactions.player.PlayerDataHandler;

import java.text.SimpleDateFormat;

public class InfoCommandHandler {
    private final ClanManager clanManager;
    private final PlayerDataHandler playerDataHandler;

    public InfoCommandHandler(ClanManager clanManager, PlayerDataHandler playerDataHandler) {
        this.clanManager = clanManager;
        this.playerDataHandler = playerDataHandler;
    }

    public boolean handleCommand(Player player, String[] args) {
        String arg = args.length > 1 ? args[1] : "";
        Clan clan = null;

        if (arg.isEmpty()) {

            clan = clanManager.getPlayerClan(player.getName());
            if (clan == null) {
                player.sendMessage(ChatColor.RED + "Вы не состоите в клане.");
                return true;
            }
        } else {


            if (playerDataHandler.hasPlayerData(arg)) {

                clan = clanManager.getPlayerClan(arg);
                if (clan == null) {
                    player.sendMessage(ChatColor.RED + "Игрок не состоит в клане.");
                    return true;
                }
            } else {

                clan = clanManager.getClan(arg.toLowerCase());
                if (clan == null) {
                    player.sendMessage(ChatColor.RED + "Клан с таким названием не найден.");
                    return true;
                }
            }
        }


        StringBuilder info = new StringBuilder();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        info.append(ChatColor.GREEN).append("**** ").append(ChatColor.WHITE).append("Информация о клане: ").append(clan.getName()).append(ChatColor.GREEN).append(" ****\n");
        info.append(ChatColor.GOLD).append("Дата создания: ").append(ChatColor.YELLOW).append(dateFormat.format(clan.getCreationDate())).append("\n");
        info.append(ChatColor.GOLD).append("Описание: ").append(ChatColor.YELLOW).append(clan.getDescription()).append("\n");
        info.append(ChatColor.GOLD).append("Земли/Сила/Макс. Сила: ").append(ChatColor.YELLOW).append(clan.getLands()).append("/").append(clan.getStrength()).append("/").append(clan.getMembers().size() * 10).append("\n");
        info.append(ChatColor.GOLD).append("Альянсы: ").append(ChatColor.YELLOW).append(String.join(", ", clan.getAlliances())).append("\n");

        info.append(ChatColor.GOLD).append("Уровень: ").append(ChatColor.YELLOW).append(clan.getLevel()).append(ChatColor.GOLD + " (" + ChatColor.GREEN + clan.getClanXp() + ChatColor.GRAY + "/" + ChatColor.YELLOW + clan.getRequiredXpForNextLevel(clan.getLevel()) + ChatColor.GOLD + ") до след. lvl").append("\n");
        info.append(ChatColor.GOLD).append("Онлайн: ").append(ChatColor.YELLOW).append(getOnlineMembersCount(clan)).append("/").append(clan.getMembers().size()).append("\n");

        info.append(ChatColor.GOLD).append("Состав клана: ");
        for (String member : clan.getMembers()) {
            String playerRole = clan.getRole(member);
                info.append(ChatColor.GREEN).append(member)
                        .append(ChatColor.WHITE).append(" - ")
                        .append(ChatColor.DARK_GREEN).append(playerRole)
                        .append(ChatColor.WHITE).append(" | ");

        }

        player.sendMessage(info.toString());
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

