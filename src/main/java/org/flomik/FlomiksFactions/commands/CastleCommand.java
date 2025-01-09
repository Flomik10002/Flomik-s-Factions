package org.flomik.FlomiksFactions.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.flomik.FlomiksFactions.worldEvents.castle.config.CastleConfigManager;
import org.flomik.FlomiksFactions.worldEvents.castle.managers.CastleLootManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CastleCommand implements CommandExecutor, TabCompleter {
    private final CastleLootManager lootManager;

    public CastleCommand(CastleLootManager lootManager) {
        this.lootManager = lootManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("castle")) {
            if (args.length == 0) {
                sender.sendMessage("Используйте: /castle <reload | lootmenu>");
                return true;
            }

            switch (args[0].toLowerCase()) {
                case "reload":
                    CastleConfigManager.reload();
                    sender.sendMessage(CastleConfigManager.getString("messages.reloaded-config"));
                    break;

                case "lootmenu":
                    if (!(sender instanceof Player)) {
                        sender.sendMessage("ты не игрок.");
                        break;
                    }
                    Player player = (Player) sender;
                    Inventory lootMenu = player.getServer().createInventory(player, 54, "Лут в замке");
                    lootManager.getLootTable().keySet().forEach(lootMenu::addItem);
                    player.openInventory(lootMenu);
                    break;

                default:
                    sender.sendMessage("использование: /event <start|stop|reload|lootmenu>");
                    break;
            }

            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return getSubCommandSuggestions(args[0]);
        }
        return new ArrayList<>();
    }

    private List<String> getSubCommandSuggestions(String input) {
        List<String> subCommands = Arrays.asList("reload", "lootmenu");
        return getSuggestions(input, subCommands);
    }

    private List<String> getSuggestions(String input, List<String> options) {
        List<String> suggestions = new ArrayList<>();
        for (String option : options) {
            if (option.toLowerCase().startsWith(input.toLowerCase())) {
                suggestions.add(option);
            }
        }
        return suggestions;
    }
}
