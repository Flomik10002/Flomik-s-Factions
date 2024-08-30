package org.flomik.flomiksFactions.commands.clan;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.flomik.flomiksFactions.FlomiksFactions;
import org.flomik.flomiksFactions.commands.clan.handlers.*;
import org.flomik.flomiksFactions.commands.clan.handlers.clanInteractions.AllyCommandHandler;
import org.flomik.flomiksFactions.commands.clan.handlers.clanInteractions.CreateCommandHandler;
import org.flomik.flomiksFactions.commands.clan.handlers.clanInteractions.DisbandCommandHandler;
import org.flomik.flomiksFactions.commands.clan.handlers.home.DelHomeCommandHandler;
import org.flomik.flomiksFactions.commands.clan.handlers.home.HomeCommandHandler;
import org.flomik.flomiksFactions.commands.clan.handlers.home.SetHomeCommandHandler;
import org.flomik.flomiksFactions.commands.clan.handlers.playerInteractions.*;
import org.flomik.flomiksFactions.commands.player.PlayerDataHandler;

import java.util.*;

import java.util.concurrent.ConcurrentHashMap;

public class ClanCommand implements CommandExecutor, TabCompleter {

    private final ConcurrentHashMap<String, Long> pendingDisbands = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<String>> pendingInvites = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<String>> pendingAllies = new ConcurrentHashMap<>();

    private final FlomiksFactions plugin;
    private final ClanManager clanManager;
    private final PlayerDataHandler playerDataHandler;
    private final CreateCommandHandler createHandler;
    private final DisbandCommandHandler disbandHandler;
    private final PromoteCommandHandler promoteHandler;
    private final DemoteCommandHandler demoteHandler;
    private final InviteCommandHandler inviteHandler;
    private final JoinCommandHandler joinHandler;
    private final KickCommandHandler kickHandler;
    private final ListCommandHandler listHandler;
    private final LeaveCommandHandler leaveHandler;
    private final InfoCommandHandler infoHandler;
    private final SetHomeCommandHandler setHomeHandler;
    private final DelHomeCommandHandler delHomeHandler;
    private final HomeCommandHandler homeHandler;
    private final AllyCommandHandler allyHandler;

    public ClanCommand(ClanManager clanManager, PlayerDataHandler playerDataHandler, FlomiksFactions plugin) {
        this.plugin = plugin;
        this.clanManager = clanManager;
        this.playerDataHandler = playerDataHandler;
        this.createHandler = new CreateCommandHandler(clanManager);
        this.disbandHandler = new DisbandCommandHandler(clanManager, pendingDisbands);
        this.promoteHandler = new PromoteCommandHandler(clanManager);
        this.demoteHandler = new DemoteCommandHandler(clanManager);
        this.inviteHandler = new InviteCommandHandler(clanManager, pendingInvites);
        this.joinHandler = new JoinCommandHandler(clanManager, pendingInvites);
        this.kickHandler = new KickCommandHandler(clanManager);
        this.listHandler = new ListCommandHandler(clanManager);
        this.leaveHandler = new LeaveCommandHandler(clanManager, pendingDisbands);
        this.infoHandler = new InfoCommandHandler(clanManager, playerDataHandler);
        this.setHomeHandler = new SetHomeCommandHandler(clanManager);
        this.delHomeHandler = new DelHomeCommandHandler(clanManager);
        this.homeHandler = new HomeCommandHandler(clanManager);
        this.allyHandler = new AllyCommandHandler(clanManager, pendingAllies);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length == 0) {
                showCommands(player);
                return true;
            }

            switch (args[0].toLowerCase()) {
                case "create":
                    return createHandler.handleCommand(player, args);
                case "disband":
                    return disbandHandler.handleCommand(player);
                case "promote":
                    return promoteHandler.handleCommand(player, args);
                case "demote":
                    return demoteHandler.handleCommand(player, args);
                case "invite":
                    return inviteHandler.handleCommand(player, args);
                case "join":
                    return joinHandler.handleCommand(player, args);
                case "kick":
                    return kickHandler.handleCommand(player, args);
                case "list":
                    return listHandler.handleCommand(player);
                case "leave":
                    return leaveHandler.handleCommand(player);
                case "info":
                    return infoHandler.handleCommand(player, args);
                case "sethome":
                    return setHomeHandler.handleCommand(player);
                case "delhome":
                    return delHomeHandler.handleCommand(player);
                case "home":
                    return homeHandler.handleCommand(player);
                case "ally":
                    return allyHandler.handleCommand(player, args);

                default:
                    player.sendMessage(ChatColor.YELLOW + "Неизвестная подкоманда. Использование: " + ChatColor.GOLD + "/clan" + ChatColor.YELLOW + " для списка команд.");
                    break;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Эту команду могут использовать только игроки.");
        }
        return true;
    }


    private void showCommands(Player player) {
        String commandsInfo = ChatColor.GREEN + "**** " + ChatColor.WHITE + "Доступные команды:" + ChatColor.GREEN + " ****" + "\n" +
                ChatColor.YELLOW + "/clan create <название> " + ChatColor.WHITE + "- Создать новый клан\n" +
                ChatColor.YELLOW + "/clan disband " + ChatColor.WHITE + "- Распустить клан\n" +
                ChatColor.YELLOW + "/clan ally <название> " + ChatColor.WHITE + "- предложить альянс клану\n" +
                ChatColor.YELLOW + "/clan leader <игрок> " + ChatColor.WHITE + "- Сделать игрока лидером\n" +
                ChatColor.YELLOW + "/clan promote <игрок> " + ChatColor.WHITE + "- Повысить игрока\n" +
                ChatColor.YELLOW + "/clan demote <игрок> " + ChatColor.WHITE + "- Понизить игрока\n" +
                ChatColor.YELLOW + "/clan invite <игрок> " + ChatColor.WHITE + "- Пригласить игрока в ваш клан\n" +
                ChatColor.YELLOW + "/clan join <название клана> " + ChatColor.WHITE + "- Присоединиться к клану\n" +
                ChatColor.YELLOW + "/clan leave " + ChatColor.WHITE + "- Покинуть клан\n" +
                ChatColor.YELLOW + "/clan kick <игрок> " + ChatColor.WHITE + "- Выгнать игрока из клана\n" +
                ChatColor.YELLOW + "/clan info <игрок> " + ChatColor.WHITE + "- Информация о клане игрока\n" +
                ChatColor.YELLOW + "/clan info <название> " + ChatColor.WHITE + "- Информация о клане\n" +
                ChatColor.YELLOW + "/clan info " + ChatColor.WHITE + "- Информация о клане, в котором состоишь\n" +
                ChatColor.YELLOW + "/clan home " + ChatColor.WHITE + "- Телепорт на точку дома\n" +
                ChatColor.YELLOW + "/clan sethome " + ChatColor.WHITE + "- Добавить точку дома\n" +
                ChatColor.YELLOW + "/clan delhome " + ChatColor.WHITE + "- Удалить точку дома\n" +
                ChatColor.YELLOW + "/clan list " + ChatColor.WHITE + "- Показать список всех кланов";
        player.sendMessage(commandsInfo);
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
        }else if (args.length == 2 && args[0].equalsIgnoreCase("ally")) {
            return getClanSuggestions(args[1]);
        } else if (args.length == 2 && args[0].equalsIgnoreCase("kick")) {
            return getPlayerSuggestions(args[1]);
        } else if (args.length == 2 && args[0].equalsIgnoreCase("info")) {
            return getPlayerSuggestions(args[1]);
        }else if (args.length == 2 && args[0].equalsIgnoreCase("promote")) {
            return getPlayerSuggestions(args[1]);
        }else if (args.length == 2 && args[0].equalsIgnoreCase("demote")) {
            return getPlayerSuggestions(args[1]);
        }else if (args.length == 2 && args[0].equalsIgnoreCase("leader")) {
            return getPlayerSuggestions(args[1]);
        }
        return new ArrayList<>();
    }

    private List<String> getSubCommandSuggestions(String input) {
        List<String> subCommands = Arrays.asList("create", "invite", "join", "list", "disband", "leave", "kick", "sethome", "delhome", "home", "info", "promote", "demote", "ally");
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