package org.flomik.FlomiksFactions.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.flomik.FlomiksFactions.clan.managers.ChunkMenuManager;

public class ChunkMenuCommand implements CommandExecutor {

    private final ChunkMenuManager chunkMenuManager;

    public ChunkMenuCommand(ChunkMenuManager chunkMenuManager) {
        this.chunkMenuManager = chunkMenuManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Эту команду может использовать только игрок.");
            return true;
        }

        Player player = (Player) sender;
        chunkMenuManager.openChunkMenu(player);
        return true;
    }
}
