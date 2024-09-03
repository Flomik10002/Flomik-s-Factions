package org.flomik.flomiksFactions.commands.chat;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.flomik.flomiksFactions.commands.clan.Clan;
import org.flomik.flomiksFactions.commands.clan.ClanManager;

public class ChatCommandHandler implements CommandExecutor {
    private final ClanManager clanManager;

    public ChatCommandHandler(ClanManager clanManager) {
        this.clanManager = clanManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Эту команду могут использовать только игроки.");
            return true;
        }

        Player player = (Player) sender;
        String playerName = player.getName();
        Clan playerClan = null;

        // Поиск клана, к которому принадлежит игрок
        for (Clan clan : clanManager.getClans().values()) {
            if (clan.getMembers().contains(playerName)) {
                playerClan = clan;
                break;
            }
        }

        if (playerClan == null) {
            player.sendMessage("Вы не состоите в клане.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("Используйте: /cc <сообщение>");
            return true;
        }

        String message = String.join(" ", args);

        // Отправка сообщения всем членам клана
        for (String member : playerClan.getMembers()) {
            Player clanMember = Bukkit.getPlayer(member);
            if (clanMember != null && clanMember.isOnline()) {
                clanMember.sendMessage("[cc] " + playerName + ": " + message);
            }
        }

        return true;
    }
}

