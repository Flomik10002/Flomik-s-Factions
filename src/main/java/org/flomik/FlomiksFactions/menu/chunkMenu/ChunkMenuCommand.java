package org.flomik.FlomiksFactions.menu.chunkMenu;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.flomik.FlomiksFactions.menu.MenuManager;

public class ChunkMenuCommand implements CommandExecutor {

    private final MenuManager menuManager;

    public ChunkMenuCommand(MenuManager menuManager) {
        this.menuManager = menuManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Эту команду может использовать только игрок.");
            return true;
        }

        Player player = (Player) sender;
        menuManager.openChunkMenu(player);
        return true;
    }
}
