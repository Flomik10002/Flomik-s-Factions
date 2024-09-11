package org.flomik.flomiksFactions.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.flomik.flomiksFactions.menu.chunkMenu.MenuManager;

public class MenuCommand implements CommandExecutor {

    private final MenuManager menuManager;

    public MenuCommand(MenuManager menuManager) {
        this.menuManager = menuManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Проверяем, является ли отправитель команды игроком
        if (!(sender instanceof Player)) {
            sender.sendMessage("Эту команду может использовать только игрок.");
            return true;
        }

        Player player = (Player) sender;

        // Открываем меню для игрока
        menuManager.openMenu(player);

        return true;
    }
}
