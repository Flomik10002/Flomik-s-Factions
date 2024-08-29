package org.flomik.flomiksFactions.commands.clan;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import java.text.SimpleDateFormat;
import java.util.concurrent.ConcurrentHashMap;

public class ClanCommand implements CommandExecutor, TabCompleter {

    private final ConcurrentHashMap<String, Long> pendingDisbands = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> pendingInvites = new ConcurrentHashMap<>();
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

                    if (!playerClan.getOwner().equals(player.getName())) {
                        player.sendMessage(ChatColor.RED + "Только владелец клана может его распустить.");
                        return true;
                    }

                    if (pendingDisbands.containsKey(player.getName())) {
                        // Удаляем клан
                        clanManager.disbandClan(playerClan.getName());
                        pendingDisbands.remove(player.getName());
                        player.sendMessage(ChatColor.GREEN + "Клан " + ChatColor.YELLOW + playerClan.getName() + ChatColor.GREEN + " был успешно распущен.");
                    } else {
                        // Запрашиваем подтверждение
                        pendingDisbands.put(player.getName(), System.currentTimeMillis());
                        player.sendMessage(ChatColor.YELLOW + "Вы действительно хотите распустить клан? Повторите команду в течении 10 секунд для подтверждения.");
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (pendingDisbands.containsKey(player.getName())) {
                                    player.sendMessage(ChatColor.RED + "Время для подтверждения истекло.");
                                    pendingDisbands.remove(player.getName());
                                }
                            }
                        }.runTaskLater(Bukkit.getPluginManager().getPlugin("FlomiksFactions"), 200L); // 200L = 10 секунд
                    }
                    break;

                case "kick":
                    if (args.length > 1) {
                        String targetName = args[1];
                        Clan currentClan = clanManager.getPlayerClan(player.getName());

                        if (currentClan == null) {
                            player.sendMessage(ChatColor.RED + "Вы не состоите в клане.");
                            return true;
                        }

                        try {
                            clanManager.kickPlayer(currentClan.getName(), player.getName(), targetName);
                            player.sendMessage(ChatColor.GREEN + "Игрок " + ChatColor.YELLOW +  targetName + ChatColor.GREEN + " был выгнан из клана " + ChatColor.YELLOW + currentClan.getName() + ChatColor.GREEN + ".");
                            Player kickedPlayer = Bukkit.getPlayer(targetName);
                            if (kickedPlayer != null) {
                                kickedPlayer.sendMessage(ChatColor.RED + "Вы были выгнаны из клана " + ChatColor.GOLD + currentClan.getName() + ChatColor.RED + ".");
                            }
                        } catch (IllegalArgumentException e) {
                            player.sendMessage(ChatColor.RED + "Ошибка: " + e.getMessage());
                        }
                    } else {
                        player.sendMessage(ChatColor.YELLOW + "Использование: /clan kick <имя игрока>");
                    }
                    break;


                case "invite":
                    if (args.length > 1) {
                        String playerName = args[1];
                        Clan currentClan = clanManager.getPlayerClan(player.getName());
                        if (currentClan == null) {
                            player.sendMessage(ChatColor.RED + "Вы не состоите в клане.");
                            return true;
                        }

                        if (pendingInvites.containsKey(playerName) && pendingInvites.get(playerName).equals(currentClan.getName())) {
                            // Отзываем приглашение
                            pendingInvites.remove(playerName);
                            player.sendMessage(ChatColor.GREEN + "Приглашение в клан " + ChatColor.YELLOW + currentClan.getName() + ChatColor.GREEN +" было отменено для игрока " + ChatColor.YELLOW + playerName + ChatColor.GREEN +".");
                            Player invitedPlayer = player.getServer().getPlayer(playerName);
                            if (invitedPlayer != null) {
                                invitedPlayer.sendMessage(ChatColor.RED + "Приглашение в клан " + ChatColor.YELLOW + currentClan.getName() + " было отменено.");
                            }
                        } else {
                            // Отправляем приглашение
                            try {
                                clanManager.invitePlayer(currentClan.getName(), playerName);
                                pendingInvites.put(playerName, currentClan.getName());
                                player.sendMessage(ChatColor.GREEN + "Приглашение в клан " + ChatColor.YELLOW + currentClan.getName() + ChatColor.GREEN + " отправлено игроку " + ChatColor.YELLOW + playerName + ChatColor.GREEN + "!");
                                player.sendMessage(ChatColor.YELLOW + "Для отмены приглашения игроку "+ ChatColor.GOLD + playerName + ChatColor.YELLOW + " повторите команду.");
                                Player invitedPlayer = player.getServer().getPlayer(playerName);
                                if (invitedPlayer != null) {
                                    invitedPlayer.sendMessage(ChatColor.YELLOW + "Вам пришло приглашение в клан " + ChatColor.YELLOW + currentClan.getName() + ChatColor.GREEN +" от игрока " + ChatColor.YELLOW + player.getName() + ChatColor.GREEN +". Используйте /clan join <название> для принятия приглашения.");
                                }
                            } catch (IllegalArgumentException e) {
                                player.sendMessage(ChatColor.RED + "Ошибка: " + e.getMessage());
                            }
                        }
                    } else {
                        player.sendMessage(ChatColor.YELLOW + "Использование: /clan invite <имя игрока>");
                    }
                    break;

                case "leave":
                    Clan currentClan = clanManager.getPlayerClan(player.getName());
                    if (currentClan == null) {
                        player.sendMessage(ChatColor.RED + "Вы не состоите в клане.");
                        return true;
                    }

                    if (currentClan.getOwner().equals(player.getName())) {
                        // Если владелец клана и в нем больше одного участника (не только владелец)
                        if (currentClan.getMembers().size() > 1) {
                            player.sendMessage(ChatColor.RED + "Владелец клана не может покинуть клан, пока в нем есть другие участники. Передайте руководство или распустите клан.");
                            return true;
                        } else {
                            // Подтверждение удаления только если владелец и в клане только он один
                            if (pendingDisbands.containsKey(player.getName())) {
                                // Выполняем удаление клана
                                try {
                                    clanManager.disbandClan(currentClan.getName());
                                    pendingDisbands.remove(player.getName());
                                    player.sendMessage(ChatColor.GREEN + "Клан " + ChatColor.YELLOW + currentClan.getName() + ChatColor.GREEN +" был успешно распущен.");
                                } catch (IllegalArgumentException e) {
                                    player.sendMessage(ChatColor.RED + "Ошибка: " + e.getMessage());
                                }
                            } else {
                                // Запрашиваем подтверждение
                                pendingDisbands.put(player.getName(), System.currentTimeMillis());
                                player.sendMessage(ChatColor.YELLOW + "Вы действительно хотите распустить клан? Повторите команду в течении 10 секунд для подтверждения.");
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        if (pendingDisbands.containsKey(player.getName())) {
                                            player.sendMessage(ChatColor.RED + "Время для подтверждения истекло.");
                                            pendingDisbands.remove(player.getName());
                                        }
                                    }
                                }.runTaskLater(Bukkit.getPluginManager().getPlugin("FlomiksFactions"), 200L); // 200L = 10 секунд
                            }
                        }
                    } else {
                        // Игрок не является владельцем
                        try {
                            clanManager.leaveClan(player.getName());
                            player.sendMessage(ChatColor.GREEN + "Вы успешно покинули клан " + ChatColor.YELLOW + currentClan.getName() + ChatColor.GREEN +".");
                        } catch (IllegalArgumentException e) {
                            player.sendMessage(ChatColor.RED + "Ошибка: " + e.getMessage());
                        }
                    }
                    break;

                case "join":
                    if (args.length > 1) {
                        String clanName = args[1].toLowerCase();
                        Clan invitedClan = clanManager.getClan(clanName);

                        if (invitedClan == null) {
                            player.sendMessage(ChatColor.RED + "Клан с таким названием не существует.");
                            return true;
                        }

                        String invitedPlayer = player.getName();
                        if (pendingInvites.containsKey(invitedPlayer) && pendingInvites.get(invitedPlayer).equals(clanName)) {
                            // Принимаем приглашение
                            try {
                                clanManager.joinClan(clanName, invitedPlayer);
                                pendingInvites.remove(invitedPlayer); // Удаляем приглашение после успешного присоединения
                                player.sendMessage(ChatColor.GREEN + "Вы успешно присоединились к клану " + ChatColor.YELLOW + clanName + ChatColor.GREEN + "!");
                                Player invitingPlayer = Bukkit.getPlayer(invitedClan.getOwner());
                                if (invitingPlayer != null) {
                                    invitingPlayer.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.GREEN + " присоединился к вашему клану " + ChatColor.YELLOW + clanName + ChatColor.GREEN +"!");
                                }
                            } catch (IllegalArgumentException e) {
                                player.sendMessage(ChatColor.RED + "Ошибка: " + e.getMessage());
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "Вы не получили приглашение в этот клан.");
                        }
                    } else {
                        player.sendMessage(ChatColor.YELLOW + "Использование: /clan join <название клана>");
                    }
                    break;

                case "list":
                    listClans(player);
                    break;



                case "ally":

                case "info":
                    String arg = args.length > 1 ? args[1] : ""; // Получаем аргумент команды, если он есть
                    Clan curClan = null;

                    if (arg.isEmpty()) {
                        // Если нет аргументов, показываем информацию о клане игрока
                        curClan = clanManager.getPlayerClan(player.getName());
                        if (curClan == null) {
                            player.sendMessage(ChatColor.RED + "Вы не состоите в клане.");
                            return true;
                        }
                    } else if (args.length == 2) {
                        // Если аргумент есть, проверяем, является ли он именем игрока или названием клана
                        if (Bukkit.getPlayer(arg) != null) {
                            // Если аргумент - это имя игрока, показываем информацию о клане этого игрока
                            curClan = clanManager.getPlayerClan(arg);
                            if (curClan == null) {
                                player.sendMessage(ChatColor.RED + "Игрок не состоит в клане.");
                                return true;
                            }
                        } else {
                            // Иначе, если это название клана, показываем информацию о данном клане
                            curClan = clanManager.getClan(arg.toLowerCase()); // Предполагаем, что `getClan` принимает название клана в нижнем регистре
                            if (curClan == null) {
                                player.sendMessage(ChatColor.RED + "Клан с таким названием не найден.");
                                return true;
                            }
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "Неверное количество аргументов.");
                        return true;
                    }

                    // Формирование информации о клане
                    StringBuilder info = new StringBuilder();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                    info.append(ChatColor.GREEN).append("**** ").append(ChatColor.WHITE).append("Информация о клане: ").append(curClan.getName()).append(ChatColor.GREEN).append(" ****\n");
                    info.append(ChatColor.GOLD).append("Дата создания: ").append(ChatColor.WHITE).append(dateFormat.format(curClan.getCreationDate())).append("\n");
                    info.append(ChatColor.GOLD).append("Описание: ").append(ChatColor.WHITE).append(curClan.getDescription()).append("\n");
                    info.append(ChatColor.GOLD).append("Земли/Сила/Макс. Сила: ").append(ChatColor.WHITE).append(curClan.getLand()).append("/").append(curClan.getStrength()).append("/").append(curClan.getMembers().size() * 10).append("\n");
                    info.append(ChatColor.GOLD).append("Альянсы: ").append(ChatColor.WHITE).append(String.join(", ", curClan.getAlliances())).append("\n");
                    info.append(ChatColor.GOLD).append("Уровень: ").append(ChatColor.WHITE).append(curClan.getLevel()).append("\n");
                    info.append(ChatColor.GOLD).append("Онлайн: ").append(ChatColor.WHITE).append(getOnlineMembersCount(curClan)).append("/").append(curClan.getMembers().size()).append("\n");

                    info.append(ChatColor.GOLD).append("Состав клана:\n");
                    for (String member : curClan.getMembers()) {
                        String playerName = member; // Имя игрока
                        String playerRole = curClan.getRole(playerName); // Роль игрока

                        // Формируем строку для текущего игрока с цветовым форматированием
                        info.append(ChatColor.YELLOW).append(playerName)
                                .append(ChatColor.WHITE).append(" - ")
                                .append(ChatColor.GRAY).append(playerRole)
                                .append("\n");
                    }

                    player.sendMessage(info.toString());
                    return true;

                case "sethome":
                    Clan sethomeClan = clanManager.getPlayerClan(player.getName());
                    if (!player.getName().equals(sethomeClan.getOwner())) {
                        player.sendMessage(ChatColor.RED + "Только владелец клана может установить точку дома.");
                        return false;
                    }

                    sethomeClan.setHome(player.getLocation());
                    clanManager.saveClan(sethomeClan);
                    player.sendMessage(ChatColor.GREEN + "Точка дома клана установлена.");
                    return true;

                case "leader":
                    if (args.length == 1) {
                        String newLeaderName = args[0];
                        Clan ownClan = clanManager.getPlayerClan(player.getName());

                        if (ownClan == null) {
                            player.sendMessage(ChatColor.RED + "Вы не состоите в клане.");
                            return false;
                        }

                        if (!ownClan.getOwner().equals(player.getName())) {
                            player.sendMessage(ChatColor.RED + "Только владелец клана может передать права.");
                            return false;
                        }

                        Player newLeader = player.getServer().getPlayer(newLeaderName);
                        if (newLeader == null || !newLeader.isOnline()) {
                            player.sendMessage(ChatColor.RED + "Игрок с таким именем не найден или не в сети.");
                            return false;
                        }

                        if (ownClan.getMembers().contains(newLeaderName)) {
                            try {
                                transferLeadership(ownClan.getName(), player.getName(), newLeaderName);
                                player.sendMessage(ChatColor.GREEN + "Вы успешно передали права владельца клана игроку " + ChatColor.YELLOW + newLeaderName + ChatColor.GREEN + ".");
                                newLeader.sendMessage(ChatColor.GREEN + "Вам переданы права владельца клана " + ChatColor.YELLOW + ownClan.getName() + ChatColor.GREEN + ".");
                            } catch (IllegalArgumentException e) {
                                player.sendMessage(ChatColor.RED + "Ошибка: " + e.getMessage());
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "Игрок " + newLeaderName + " не является членом вашего клана.");
                        }
                    } else {
                        player.sendMessage(ChatColor.YELLOW + "Использование: /clan leader <игрок>");
                    }
                    break;

                case "delhome":
                    Clan delhomeClan = clanManager.getPlayerClan(player.getName());
                    if (!player.getName().equals(delhomeClan.getOwner())) {
                        player.sendMessage(ChatColor.RED + "Только владелец клана может удалить точку дома.");
                        return false;
                    }

                    delhomeClan.removeHome();
                    clanManager.saveClan(delhomeClan);
                    player.sendMessage(ChatColor.GREEN + "Точка дома клана удалена.");
                    return true;

                case "home":
                    Clan homeClan = clanManager.getPlayerClan(player.getName());
                    if (!homeClan.hasHome()) {
                        player.sendMessage(ChatColor.RED + "Точка дома клана не установлена.");
                        return false;
                    }

                    player.teleport(homeClan.getHome());
                    player.sendMessage(ChatColor.GREEN + "Вы телепортированы к точке дома клана.");
                    return true;

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
                ChatColor.YELLOW + "/clan leave " + ChatColor.WHITE + "- Покинуть клан\n" +
                ChatColor.YELLOW + "/clan kick <имя игрока> " + ChatColor.WHITE + "- Выгнать игрока из клана\n" +
                ChatColor.YELLOW + "/clan info <имя игрока> " + ChatColor.WHITE + "- Информация о клане игрока\n" +
                ChatColor.YELLOW + "/clan info <название> " + ChatColor.WHITE + "- Информация о клане\n" +
                ChatColor.YELLOW + "/clan info " + ChatColor.WHITE + "- Информация о клане, в котором состоишь\n" +
                ChatColor.YELLOW + "/clan home " + ChatColor.WHITE + "- Телепорт на точку дома\n" +
                ChatColor.YELLOW + "/clan sethome " + ChatColor.WHITE + "- Добавить точку дома\n" +
                ChatColor.YELLOW + "/clan delhome " + ChatColor.WHITE + "- Удалить точку дома\n" +
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
        } else if (args.length == 2 && args[0].equalsIgnoreCase("kick")) {
            return getPlayerSuggestions(args[1]);
        } else if (args.length == 2 && args[0].equalsIgnoreCase("info")) {
            return getPlayerSuggestions(args[1]);
        }
        return new ArrayList<>();
    }

    private List<String> getSubCommandSuggestions(String input) {
        List<String> subCommands = Arrays.asList("create", "invite", "join", "list", "disband", "leave", "kick", "sethome", "delhome", "home", "info");
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

    public void transferLeadership(String clanName, String currentOwnerName, String newLeaderName) {
        Clan clan = clanManager.getClan(clanName);
        if (clan == null) {
            throw new IllegalArgumentException("Клан с таким названием не найден.");
        }

        if (!clan.getOwner().equals(currentOwnerName)) {
            throw new IllegalArgumentException("Вы не являетесь владельцем этого клана.");
        }

        if (!clan.getMembers().contains(newLeaderName)) {
            throw new IllegalArgumentException("Игрок не является членом клана.");
        }

        clan.setOwner(newLeaderName);
        clanManager.saveClan(clan);
    }

}
