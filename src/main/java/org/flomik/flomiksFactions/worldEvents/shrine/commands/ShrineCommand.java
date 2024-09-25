package org.flomik.flomiksFactions.worldEvents.shrine.commands;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.flomik.flomiksFactions.worldEvents.shrine.ShrineEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShrineCommand implements CommandExecutor, TabCompleter {

    private final ShrineEvent shrineEvent;

    public ShrineCommand(ShrineEvent shrineEvent) {
        this.shrineEvent = shrineEvent;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("shrine")) {
            if (args.length == 0) {
                sender.sendMessage("Используйте: /shrine <start | cancel | add | remove | deleteall | list>");
                return true;
            }

            String subCommand = args[0].toLowerCase();

            switch (subCommand) {
                case "start":
                    if (!(sender instanceof Player)) {
                        sender.sendMessage("Команду может использовать только игрок.");
                        return true;
                    }
                    Player player = (Player) sender;
                    shrineEvent.startShrineEvent();
                    player.sendMessage("Ивент Святилище был запущен вручную.");
                    break;

                case "add":
                    if (!(sender instanceof Player)) {
                        sender.sendMessage("Команду может использовать только игрок.");
                        return true;
                    }
                    Player playerAdd = (Player) sender;
                    shrineEvent.addShrineLocation(playerAdd); // Добавляем новую точку
                    playerAdd.sendMessage("Новая точка святилища была добавлена.");
                    break;

                case "deleteall":
                    shrineEvent.deleteAllSanctuaries(); // Завершаем все точки
                    sender.sendMessage("Все активные святилища были завершены.");
                    break;

                case "cancel":
                    shrineEvent.cancelShrineEvent();
                    break;

                case "remove":
                    if (!(sender instanceof Player)) {
                        sender.sendMessage("Команду может использовать только игрок.");
                        return true;
                    }
                    Player playerRemove = (Player) sender;
                    shrineEvent.removeShrineLocation(playerRemove);
                    break;

                case "list":
                    // Проверяем, что отправитель — игрок
                    if (!(sender instanceof Player)) {
                        sender.sendMessage("Команду может использовать только игрок.");
                        return true;
                    }

                    Player playerList = (Player) sender;
                    List<Location> shrineLocations = shrineEvent.getShrineLocations();

                    if (shrineLocations.isEmpty()) {
                        playerList.sendMessage("Нет доступных точек святилищ.");
                    } else {
                        playerList.sendMessage("Список точек святилищ:");
                        for (Location shrineLocation : shrineLocations) {
                            sendShrineLocation(playerList, shrineLocation);
                        }
                    }
                    break;
    
                default:
                    sender.sendMessage("Неизвестная подкоманда. Используйте: /shrine <start | cancel | add | remove | deleteall | list>");
                    break;
            }
            return true;
        }
        return false;
    }

    // Метод для отправки кликабельного сообщения с координатами шрайна
    private void sendShrineLocation(Player player, Location location) {
        String coordinates = String.format("%d, %d, %d", location.getBlockX(), location.getBlockY(), location.getBlockZ());

        TextComponent message = new TextComponent("Точка святилища: " + ChatColor.GREEN + "[" + coordinates + "]");
        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                String.format("/tp %s %d %d %d", player.getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ())));

        player.spigot().sendMessage(message);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return getSubCommandSuggestions(args[0]);
        }
        return new ArrayList<>();
    }

    private List<String> getSubCommandSuggestions(String input) {
        List<String> subCommands = Arrays.asList("start", "cancel", "add", "remove", "deleteall", "list");
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
