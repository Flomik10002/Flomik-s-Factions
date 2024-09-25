package org.flomik.flomiksFactions.menu;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.flomik.flomiksFactions.clan.Clan;
import org.flomik.flomiksFactions.clan.ClanManager;

public class MenuChecherPerm implements CommandExecutor {
    private final ClanManager clanManager;

    public MenuChecherPerm(ClanManager clanManager) {
        this.clanManager = clanManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Clan playerClan = clanManager.getPlayerClan(player.getName());

            if (playerClan == null) {
                player.sendMessage(ChatColor.RED + "Вы не состоите в клане.");
                return true;
            }

            String playerRole = playerClan.getRole(player.getName());
            if (!playerRole.equals("Лидер") && !playerRole.equals("Заместитель")) {
                player.sendMessage(ChatColor.RED + "Только Лидер и Заместитель клана могут настраивать клан.");
                return true;
            }

            // Выполняем команду от имени игрока
            player.performCommand("clansettings");
            return true;
        }
        return false;
    }
}
