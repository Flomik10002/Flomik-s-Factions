package org.flomik.flomiksFactions.commands.player;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.flomik.flomiksFactions.commands.clan.Clan;
import org.flomik.flomiksFactions.commands.clan.ClanManager;

import java.util.*;

public class PlayerCommand implements CommandExecutor, TabCompleter {
    private final PlayerDataHandler playerDataHandler;
    private final ClanManager clanManager;

    public PlayerCommand(PlayerDataHandler playerDataHandler, ClanManager clanManager) {
        this.playerDataHandler = playerDataHandler;
        this.clanManager = clanManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length == 0) {
                showCommands(player);
                return true;
            }

            String subCommand = args[0].toLowerCase();
            switch (subCommand) {
                case "info":
                    String arg = args.length > 1 ? args[1] : ""; // Получаем аргумент команды, если он есть

                    if (arg.isEmpty()) {
                        player.sendMessage(ChatColor.YELLOW + "Использование:" + ChatColor.GOLD + "/p info <игрок>");
                        return true;
                    }

                    org.flomik.flomiksFactions.commands.player.Player playerInfo = getPlayerInfo(arg);
                    Clan curClan = clanManager.getPlayerClan(arg);

                    if (playerInfo == null) {
                        player.sendMessage(ChatColor.RED + "Информация о игроке не найдена.");
                        return true;
                    }

                    // Если игрок был оффлайн, но зарегистрирован
                    String firstJoinDate = playerDataHandler.getFirstJoinDate(arg);
                    // Получаем информацию о времени игры из PlayerDataHandler
                    int ticksPlayed = playerDataHandler.getPlayTime(arg);

                    // Конвертируем тики в секунды
                    int secondsPlayed = ticksPlayed / 20;

                    // Конвертируем секунды в дни, часы и минуты (убираем секунды)
                    int days = secondsPlayed / 86400; // Количество секунд в дне
                    int hours = (secondsPlayed % 86400) / 3600;
                    int minutes = (secondsPlayed % 3600) / 60;

                    // Формируем сообщение
                    String playTimeMessage = String.format(
                            "%dд, %dч, %dм.", // Убрали отображение секунд
                            days, hours, minutes
                    );

                    StringBuilder info = new StringBuilder();
                    info.append(ChatColor.GREEN).append("***** ").append(ChatColor.WHITE).append("Игрок ").append(arg).append(ChatColor.GREEN).append(" *****\n");
                    info.append(ChatColor.GOLD).append("Играет с: ").append(ChatColor.YELLOW).append(firstJoinDate).append("\n");
                    info.append(ChatColor.GOLD).append("Онлайн: ").append(ChatColor.YELLOW).append(playTimeMessage).append("\n");
                    info.append(ChatColor.GOLD).append("Сила/Макс. Сила: ").append(ChatColor.YELLOW).append(playerInfo.getStrength()).append("/").append(playerInfo.getMaxPower()).append("\n");
                    info.append(ChatColor.GOLD).append("Уровень: ").append(ChatColor.YELLOW).append(playerInfo.getLevel()).append("\n");
                    if (curClan != null) {
                        info.append(ChatColor.GOLD).append("Клан: ").append(ChatColor.YELLOW).append(curClan.getRole(arg)).append(ChatColor.GOLD).append(" в ").append(ChatColor.GRAY).append("[").append(ChatColor.YELLOW).append("-").append(ChatColor.GRAY).append("] ").append(ChatColor.YELLOW).append(curClan.getName()).append(ChatColor.GOLD).append(" (").append(ChatColor.GREEN).append(getOnlineMembersCount(curClan)).append(ChatColor.GRAY).append("/").append(ChatColor.YELLOW).append(curClan.getMembers().size()).append(ChatColor.GOLD).append(")\n");
                    } else {
                        info.append(ChatColor.GOLD).append("Не состоит в клане.");
                    }

                    player.sendMessage(info.toString());
                    return true;

                default:
                    player.sendMessage(ChatColor.YELLOW + "Неизвестная подкоманда. Использование: " + ChatColor.GOLD + "/player" + ChatColor.YELLOW + " для списка команд.");
                    break;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Эту команду могут использовать только игроки.");
        }
        return true;
    }

    // Метод для подсчета количества онлайн участников
    private int getOnlineMembersCount(Clan clan) {
        int onlineCount = 0;
        for (String member : clan.getMembers()) {
            Player player = Bukkit.getPlayer(member);
            if (player != null && player.isOnline()) {
                onlineCount++;
            }
        }
        return onlineCount;
    }

    private String formatSection() {
        return ChatColor.GREEN + "**** " + ChatColor.WHITE + "Доступные команды:" + ChatColor.GREEN + " ****";
    }

    private void showCommands(Player player) {
        String commandsInfo = formatSection() + "\n" +
                ChatColor.YELLOW + "/player info <игрок> " + ChatColor.WHITE + "- Информация о игроке";
        player.sendMessage(commandsInfo);
    }

    private org.flomik.flomiksFactions.commands.player.Player getPlayerInfo(String playerName) {
        // Попробуйте получить информацию из PlayerDataHandler
        if (playerDataHandler.hasPlayerData(playerName)) {
            // Получаем уровень, силу и максимальную силу из PlayerDataHandler
            int level = playerDataHandler.getPlayerLevel(playerName); // Получаем уровень игрока
            int strength = playerDataHandler.getPlayerStrength(playerName); // Получаем текущую силу игрока
            int maxPower = playerDataHandler.getPlayerMaxStrength(playerName); // Получаем максимальную силу игрока

            // Создаем новый объект Player с полученными данными
            org.flomik.flomiksFactions.commands.player.Player playerInfo = new org.flomik.flomiksFactions.commands.player.Player(level, strength, maxPower);
            return playerInfo;
        } else {
            // Если данных нет, возвращаем null
            return null;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return getSubCommandSuggestions(args[0]);
        } else if (args.length == 2 && args[0].equalsIgnoreCase("info")) {
            return getPlayerSuggestions(args[1]);
        }
        return new ArrayList<>();
    }

    private List<String> getSubCommandSuggestions(String input) {
        List<String> subCommands = Arrays.asList("info");
        return getSuggestions(input, subCommands);
    }

    private List<String> getPlayerSuggestions(String input) {
        List<String> playerNames = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getName().toLowerCase().startsWith(input.toLowerCase())) {
                playerNames.add(player.getName());
            }
        }
        return playerNames;
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
