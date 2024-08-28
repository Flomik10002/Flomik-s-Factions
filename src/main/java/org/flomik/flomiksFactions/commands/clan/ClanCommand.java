package org.flomik.flomiksFactions.commands.clan;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClanCommand implements CommandExecutor {

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

                case "invite":
                    Clan playerClan = clanManager.getPlayerClan(player.getName());
                    if (playerClan == null) {
                        player.sendMessage(ChatColor.RED + "Вы не состоите в клане.");
                        return true;
                    }
                    if (args.length > 1) {
                        String playerName = args[1];
                        try {
                            clanManager.invitePlayer(playerClan.getName(), playerName);
                            player.sendMessage(ChatColor.GREEN + "Приглашение в клан " + playerClan.getName() + " отправлено игроку " + playerName + "!");
                            Player invitedPlayer = player.getServer().getPlayer(playerName);
                            if (invitedPlayer != null) {
                                invitedPlayer.sendMessage(ChatColor.YELLOW + "Вам пришло приглашение в клан " + playerClan.getName() + " от игрока " + player.getName() + ". Используйте /clan join для принятия приглашения.");
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
        String commandsInfo = ChatColor.GREEN + "Доступные команды:\n" +
                ChatColor.YELLOW + "/clan create <название> " + ChatColor.WHITE + "- Создать новый клан\n" +
                ChatColor.YELLOW + "/clan invite <имя игрока> " + ChatColor.WHITE + "- Пригласить игрока в ваш клан\n" +
                ChatColor.YELLOW + "/clan join <название клана> " + ChatColor.WHITE + "- Присоединиться к клану\n" +
                ChatColor.YELLOW + "/clan list " + ChatColor.WHITE + "- Показать список всех кланов";
        player.sendMessage(commandsInfo);
    }

    private void listClans(Player player) {
        if (clanManager.getClans().isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "Список кланов пуст.");
            return;
        }

        StringBuilder message = new StringBuilder(ChatColor.GREEN + "**** Список кланов ****\n");
        for (Clan clan : clanManager.getClans().values()) {
            int memberCount = clan.getMembers().size();
            int onlineMemberCount = getOnlineMembersCount(clan);
            int maxStrength = memberCount * 10; // Максимально допустимая сила (10 на участника)

            // Форматирование строки с параметрами клана
            message.append(ChatColor.GOLD).append(clan.getName())
                    .append(ChatColor.YELLOW).append(" - Рейтинг: N/A")
                    .append(ChatColor.YELLOW).append(" - Онлайн ").append(onlineMemberCount).append("/").append(memberCount)
                    .append(ChatColor.YELLOW).append(" - Земли/Сила/Макс. Сила: 0/0/").append(maxStrength).append("\n");
        }
        message.append(ChatColor.GREEN + "**** Конец списка ****");
        player.sendMessage(message.toString());
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
}
