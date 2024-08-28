package org.flomik.flomiksFactions.commands.clan;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ClanCommand implements CommandExecutor, TabCompleter {

    private final ConcurrentHashMap<String, Long> pendingDisbands = new ConcurrentHashMap<>();

    private final ClanManager clanManager;

    public ClanCommand(ClanManager clanManager) {
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
                case "create":
                    if (args.length > 1) {
                        String clanName = args[1].toLowerCase();
                        try {
                            clanManager.createClan(clanName, player.getName());
                            player.sendMessage(ChatColor.GREEN + "Клан " + clanName + " успешно создан!");
                        } catch (IllegalArgumentException e) {
                            player.sendMessage(ChatColor.RED + "Ошибка: " + e.getMessage());
                        }
                    } else {
                        player.sendMessage(ChatColor.YELLOW + "Пожалуйста, укажите название клана. Использование: /clan create <название>");
                    }
                    break;

                case "disband":
                    Clan playerClan = clanManager.getPlayerClan(player.getName());
                    if (playerClan == null) {
                        player.sendMessage(ChatColor.RED + "Вы не состоите в клане.");
                        return true;
                    }

                    if (pendingDisbands.containsKey(player.getName())) {
                        // Удаляем клан
                        clanManager.getClans().remove(playerClan.getName());
                        clanManager.saveAllClans();
                        pendingDisbands.remove(player.getName());
                        player.sendMessage(ChatColor.GREEN + "Клан " + playerClan.getName() + " был успешно распущен.");
                    } else {
                        // Запрашиваем подтверждение
                        pendingDisbands.put(player.getName(), System.currentTimeMillis());
                        player.sendMessage(ChatColor.YELLOW + "Вы действительно хотите распустить клан? Повторите команду в течении 10 секунд для подтверждения.");
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                player.sendMessage(ChatColor.RED + "Время для подтверждения истекло.");
                                pendingDisbands.remove(player.getName());
                            }
                        }.runTaskLater(Bukkit.getPluginManager().getPlugin("FlomiksFactions"), 200L); // 200L = 10 секунд
                    }
                    break;

                case "invite":
                    Clan currentClan = clanManager.getPlayerClan(player.getName());
                    if (currentClan == null) {
                        player.sendMessage(ChatColor.RED + "Вы не состоите в клане.");
                        return true;
                    }
                    if (args.length > 1) {
                        String playerName = args[1];
                        try {
                            clanManager.invitePlayer(currentClan.getName(), playerName);
                            player.sendMessage(ChatColor.GREEN + "Приглашение в клан " + currentClan.getName() + " отправлено игроку " + playerName + "!");
                            Player invitedPlayer = player.getServer().getPlayer(playerName);
                            if (invitedPlayer != null) {
                                invitedPlayer.sendMessage(ChatColor.YELLOW + "Вам пришло приглашение в клан " + currentClan.getName() + " от игрока " + player.getName() + ". Используйте /clan join для принятия приглашения.");
                            }
                        } catch (IllegalArgumentException e) {
                            player.sendMessage(ChatColor.RED + "Ошибка: " + e.getMessage());
                        }
                    } else {
                        player.sendMessage(ChatColor.YELLOW + "Использование: /clan invite <имя игрока>");
                    }
                    break;

                case "join":
                    if (args.length > 1) {
                        String clanName = args[1].toLowerCase();
                        try {
                            clanManager.joinClan(clanName, player.getName()); // Игрок указывает название клана
                            player.sendMessage(ChatColor.GREEN + "Вы успешно присоединились к клану " + clanName + "!");
                        } catch (IllegalArgumentException e) {
                            player.sendMessage(ChatColor.RED + "Ошибка: " + e.getMessage());
                        }
                    } else {
                        player.sendMessage(ChatColor.YELLOW + "Использование: /clan join <название клана>");
                    }
                    break;

                case "list":
                    listClans(player);
                    break;

                default:
                    player.sendMessage(ChatColor.RED + "Неизвестная подкоманда. Используйте /clan для списка команд.");
                    break;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Эту команду могут использовать только игроки.");
        }
        return true;
    }

    private void showCommands(Player player) {
        String commandsInfo = ChatColor.GREEN + formatSection("Доступные команды:") + "\n" +
                ChatColor.YELLOW + "/clan create <название> " + ChatColor.WHITE + "- Создать новый клан\n" +
                ChatColor.YELLOW + "/clan disband " + ChatColor.WHITE + "- Распустить клан\n" +
                ChatColor.YELLOW + "/clan invite <имя игрока> " + ChatColor.WHITE + "- Пригласить игрока в ваш клан\n" +
                ChatColor.YELLOW + "/clan join <название клана> " + ChatColor.WHITE + "- Присоединиться к клану\n" +
                ChatColor.YELLOW + "/clan list " + ChatColor.WHITE + "- Показать список всех кланов";
        player.sendMessage(commandsInfo);
    }


    private void listClans(Player player) {
        if (clanManager.getClans().isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "Список кланов пуст");
            return;
        }

        StringBuilder message = new StringBuilder(formatSection("Список кланов:") + "\n");
        for (Clan clan : clanManager.getClans().values()) {
            int memberCount = clan.getMembers().size();
            int onlineMemberCount = getOnlineMembersCount(clan);
            int maxStrength = memberCount * 10; // Максимально допустимая сила (10 на участника)

            // Форматирование строки с параметрами клана
            message.append(ChatColor.AQUA).append(clan.getName())  // Название клана
                    .append(ChatColor.GOLD).append(" - Рейтинг: ").append(ChatColor.YELLOW).append("N/A") // Рейтинг
                    .append(ChatColor.GOLD).append(" - Онлайн ").append(ChatColor.YELLOW).append(onlineMemberCount).append("/").append(memberCount) // Онлайн
                    .append(ChatColor.GOLD).append(" - Земли/Сила/Макс. Сила: ").append(ChatColor.YELLOW).append("0/0/").append(maxStrength).append("\n"); // Земли/Сила/Макс. Сила
        }
        player.sendMessage(message.toString());
    }

    private String formatSection(String title) {
        return ChatColor.GREEN + "**** " + ChatColor.WHITE + title + ChatColor.GREEN + " ****";
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

    // Получение списка всех кланов
    private List<String> getClanNames() {
        List<String> clanNames = new ArrayList<>();
        for (Clan clan : clanManager.getClans().values()) {
            clanNames.add(clan.getName());
        }
        return clanNames;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return getSubCommandSuggestions(args[0]);
        } else if (args.length == 2 && args[0].equalsIgnoreCase("invite")) {
            return getPlayerSuggestions(args[1]);
        } else if (args.length == 2 && args[0].equalsIgnoreCase("join")) {
            return getClanSuggestions(args[1]);
        }
        return new ArrayList<>();
    }

    private List<String> getSubCommandSuggestions(String input) {
        List<String> subCommands = Arrays.asList("create", "invite", "join", "list", "disband");
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

    private List<String> getClanSuggestions(String input) {
        List<String> clanNames = getClanNames();
        return getSuggestions(input, clanNames);
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
