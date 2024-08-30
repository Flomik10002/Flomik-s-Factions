package org.flomik.flomiksFactions.commands.clan.handlers.clanInteractions;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.flomik.flomiksFactions.commands.clan.ClanManager;

public class CreateCommandHandler {

    private final ClanManager clanManager;

    public CreateCommandHandler(ClanManager clanManager) {
        this.clanManager = clanManager;
    }

    public boolean handleCommand(Player player, String[] args) {
        if (args.length > 1) {
            String clanName = args[1].toLowerCase();
            try {
                clanManager.createClan(clanName, player.getName());
                player.sendMessage(ChatColor.GREEN + "Клан " + clanName + " успешно создан!");
            } catch (IllegalArgumentException e) {
                player.sendMessage(ChatColor.RED + e.getMessage());
            }
        } else {
            player.sendMessage(ChatColor.YELLOW + "Пожалуйста, укажите название клана. Использование: " + ChatColor.GOLD + "/clan create <название>");
        }
        return true;
    }
}
