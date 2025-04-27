package org.flomik.FlomiksFactions.clan.commands; //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression

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

public class  CastleCommand implements CommandExecutor, TabCompleter { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private final CastleLootManager lootManager; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression

    public CastleCommand(CastleLootManager lootManager) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        this.lootManager = lootManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        if (command.getName().equalsIgnoreCase("castle")) { //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression
            if (args.length == 0) {
                sender.sendMessage("Используйте: /castle <reload | lootmenu>");
                return true; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
            }

            switch (args[0].toLowerCase()) { //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression
                case "reload":
                    CastleConfigManager.reload();
                    sender.sendMessage(CastleConfigManager.getString("messages.reloaded-config"));
                    break;

                case "lootmenu":
                    if (!(sender instanceof Player)) {
                        sender.sendMessage("ты не игрок.");
                        break;
                    }
                    Player player = (Player) sender; //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                    Inventory lootMenu = player.getServer().createInventory(player, 54, "Лут в замке"); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                    lootManager.getLootTable().keySet().forEach(lootMenu::addItem);
                    player.openInventory(lootMenu);
                    break;

                default:
                    sender.sendMessage("использование: /event <start|stop|reload|lootmenu>");
                    break;
            }

            return true; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        if (args.length == 1) { //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression
            return getSubCommandSuggestions(args[0]); //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
        }
        return new ArrayList<>();
    }

    private List<String> getSubCommandSuggestions(String input) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        List<String> subCommands = Arrays.asList("reload", "lootmenu"); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        return getSuggestions(input, subCommands);
    }

    private List<String> getSuggestions(String input, List<String> options) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        List<String> suggestions = new ArrayList<>(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        for (String option : options) { //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            if (option.toLowerCase().startsWith(input.toLowerCase())) { //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression
                suggestions.add(option);
            }
        }
        return suggestions;
    }
}
