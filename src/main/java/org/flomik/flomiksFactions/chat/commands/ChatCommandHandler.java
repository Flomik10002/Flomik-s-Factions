package org.flomik.flomiksFactions.chat.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.flomik.flomiksFactions.clan.Clan;
import org.flomik.flomiksFactions.clan.ClanManager;

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


        for (String member : playerClan.getMembers()) {
            Player clanMember = Bukkit.getPlayer(member);
            if (clanMember != null && clanMember.isOnline()) {
                String clanPrefix = ChatColor.LIGHT_PURPLE + "[" + playerClan.getRole(playerName) + "] " + ChatColor.GREEN;
                clanMember.sendMessage(ChatColor.GREEN + "© "+ clanPrefix + playerName + ": " + ChatColor.RESET + message);
            }
        }

        return true;
    }
}

