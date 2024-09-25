package org.flomik.flomiksFactions.listener;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.flomik.flomiksFactions.clan.Clan;
import org.flomik.flomiksFactions.clan.ClanManager;

public class ChatPrefixListener implements Listener {
    private final ClanManager clanManager;

    public ChatPrefixListener(ClanManager clanManager) {
        this.clanManager = clanManager;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();
        Clan playerClan = null;

        // Поиск клана, к которому принадлежит игрок
        for (Clan clan : clanManager.getClans().values()) {
            if (clan.getMembers().contains(playerName)) {
                playerClan = clan;
                break;
            }
        }

        if (playerClan != null) {
            // Формирование префикса с названием клана
//            String clanPrefix = ChatColor.GREEN + "[" + playerClan.getName() + "] " + ChatColor.RESET;
//            // Изменение сообщения
//            String message = event.getMessage();
//            event.setFormat(clanPrefix + "<" + playerName + "> " + message);

            String clanPrefix = ChatColor.GREEN + "[" + playerClan.getName() + "] " + ChatColor.RESET;
            // Изменение сообщения
            String message = event.getMessage();
            event.setFormat(clanPrefix + playerName + ": " + message);
        } else {
            String message = event.getMessage();
            event.setFormat(playerName + ": " + message);
        }
    }
}
