package org.flomik.FlomiksFactions.clan.commands; //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;
import org.bukkit.command.TabCompleter;
import org.flomik.FlomiksFactions.FlomiksFactions;
import org.flomik.FlomiksFactions.worldEvents.randomEvents.managers.RandomEventManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventCommand implements CommandExecutor, TabCompleter { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private final RandomEventManager eventManager; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private final FlomiksFactions plugin; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression

    public EventCommand(RandomEventManager eventManager, FlomiksFactions plugin) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        this.eventManager = eventManager;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        if (args.length < 1) { //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression
            sender.sendMessage(ChatColor.RED + "Использование: /event <start|stop> <название_ивента>");
            return false; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
        }

        String action = args[0].toLowerCase(); //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression

        switch (action) {
            case "start" -> {
                if (args.length < 2) { //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression
                    sender.sendMessage(ChatColor.RED + "Использование: /event start <название_ивента>");
                    sender.sendMessage(ChatColor.YELLOW + "Доступные ивенты: fisherman, diver, runner, shrine, castle");
                    return false; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
                }
                String eventName = args[1].toLowerCase(); //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression
                handleStartEvent(sender, eventName);
            }
            case "stop" -> {
                if (args.length < 2) { //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression
                    sender.sendMessage(ChatColor.RED + "Использование: /event stop <название_ивента>");
                    sender.sendMessage(ChatColor.YELLOW + "Доступные ивенты: fisherman, diver, runner, shrine, castle");
                    return false; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
                }
                String eventName = args[1].toLowerCase(); //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression
                handleStopEvent(sender, eventName);
            }
            default -> {
                sender.sendMessage(ChatColor.RED + "Неверная команда. Использование: /event <start|stop> <название_ивента>");
                return false; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
            }
        }
        return true;
    }

    private void handleStartEvent(CommandSender sender, String eventName) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        switch (eventName) {
            case "fisherman", "diver", "runner" -> {
                if (eventManager.isRunning()) {
                    sender.sendMessage(ChatColor.RED + "Ивент уже запущен. Остановите текущий ивент перед запуском нового.");
                    return;
                }
                eventManager.startEvent(eventName);
            }
            case "shrine" -> {
                plugin.getShrineEvent().startShrineEvent(); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
                sender.sendMessage(ChatColor.GREEN + "Ивент Святилище был запущен вручную!");
            }
            case "castle" -> {
                plugin.getCastleEvent().start(); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
                sender.sendMessage(ChatColor.GREEN + "Ивент Замок был запущен вручную!");
            }
            default -> sender.sendMessage(ChatColor.RED + "Неизвестный ивент: " + eventName);
        }
    }

    private void handleStopEvent(CommandSender sender, String eventName) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        switch (eventName) {
            case "fisherman", "diver", "runner" -> {
                if (!eventManager.isRunning()) {
                    sender.sendMessage(ChatColor.RED + "Сейчас нет активных ивентов.");
                    return;
                }
                eventManager.stopEvent();
            }
            case "shrine" -> {
                plugin.getShrineEvent().cancelShrineEvent(); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
                sender.sendMessage(ChatColor.GREEN + "Ивент Святилище был остановлен!");
            }
            case "castle" -> {
                plugin.getCastleEvent().endEvent(); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
                sender.sendMessage(ChatColor.GREEN + "Ивент Замок был остановлен!");
            }
            default -> sender.sendMessage(ChatColor.RED + "Неизвестный ивент: " + eventName);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        if (args.length == 1) { //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression
            return getSubCommandSuggestions(args[0]); //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
        } else if (args.length == 2) { //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression
            return getEventSuggestions(args[1]); //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
        }
        return new ArrayList<>();
    }

    private List<String> getEventSuggestions(String input) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        List<String> events = Arrays.asList("fisherman", "diver", "runner", "shrine", "castle"); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        return getSuggestions(input, events);
    }

    private List<String> getSubCommandSuggestions(String input) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        List<String> subCommands = Arrays.asList("start", "stop"); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
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
