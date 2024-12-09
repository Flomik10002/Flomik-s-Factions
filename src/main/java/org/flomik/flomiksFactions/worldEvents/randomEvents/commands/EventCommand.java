package org.flomik.flomiksFactions.worldEvents.randomEvents.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;
import org.bukkit.command.TabCompleter;
import org.flomik.flomiksFactions.FlomiksFactions;
import org.flomik.flomiksFactions.worldEvents.randomEvents.RandomEventManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventCommand implements CommandExecutor, TabCompleter {
    private final RandomEventManager eventManager;
    private final FlomiksFactions plugin;

    public EventCommand(RandomEventManager eventManager, FlomiksFactions plugin) {
        this.eventManager = eventManager;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Использование: /event <start|stop> <название_ивента>");
            return false;
        }

        String action = args[0].toLowerCase();

        switch (action) {
            case "start" -> {
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Использование: /event start <название_ивента>");
                    sender.sendMessage(ChatColor.YELLOW + "Доступные ивенты: fisherman, diver, runner, shrine");
                    return false;
                }
                String eventName = args[1].toLowerCase();
                handleStartEvent(sender, eventName);
            }
            case "stop" -> {
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Использование: /event stop <название_ивента>");
                    sender.sendMessage(ChatColor.YELLOW + "Доступные ивенты: fisherman, diver, runner, shrine");
                    return false;
                }
                String eventName = args[1].toLowerCase();
                handleStopEvent(sender, eventName);
            }
            default -> {
                sender.sendMessage(ChatColor.RED + "Неверная команда. Использование: /event <start|stop> <название_ивента>");
                return false;
            }
        }
        return true;
    }

    private void handleStartEvent(CommandSender sender, String eventName) {
        switch (eventName) {
            case "fisherman", "diver", "runner" -> {
                if (eventManager.isRunning()) {
                    sender.sendMessage(ChatColor.RED + "Ивент уже запущен. Остановите текущий ивент перед запуском нового.");
                    return;
                }
                eventManager.startEvent(eventName);
            }
            case "shrine" -> {
                plugin.getShrineEvent().startShrineEvent();
                sender.sendMessage(ChatColor.GREEN + "Ивент Святилище был запущен вручную!");
            }
            default -> sender.sendMessage(ChatColor.RED + "Неизвестный ивент: " + eventName);
        }
    }

    private void handleStopEvent(CommandSender sender, String eventName) {
        switch (eventName) {
            case "fisherman", "diver", "runner" -> {
                if (!eventManager.isRunning()) {
                    sender.sendMessage(ChatColor.RED + "Сейчас нет активных ивентов.");
                    return;
                }
                eventManager.stopEvent();
            }
            case "shrine" -> {
                plugin.getShrineEvent().cancelShrineEvent();
                sender.sendMessage(ChatColor.GREEN + "Ивент Святилище был остановлен!");
            }
            default -> sender.sendMessage(ChatColor.RED + "Неизвестный ивент: " + eventName);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return getSubCommandSuggestions(args[0]);
        } else if (args.length == 2) {
            return getEventSuggestions(args[1]);
        }
        return new ArrayList<>();
    }

    private List<String> getEventSuggestions(String input) {
        List<String> events = Arrays.asList("fisherman", "diver", "runner", "shrine");
        return getSuggestions(input, events);
    }

    private List<String> getSubCommandSuggestions(String input) {
        List<String> subCommands = Arrays.asList("start", "stop");
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
