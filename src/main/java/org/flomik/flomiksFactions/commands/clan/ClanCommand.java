package org.flomik.flomiksFactions.commands.clan;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.flomik.flomiksFactions.FlomiksFactions;
import org.flomik.flomiksFactions.commands.clan.handlers.*;
import org.flomik.flomiksFactions.commands.clan.handlers.clanInteractions.*;
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
    private final LeaderCommandHandler leaderHandler;
    private final NameCommandHandler nameHanler;
    private final DescCommandHandler descriptionHanler;
    private final ModerCommandHandler moderHanler;
    private final ClaimRegionCommandHandler claimRegionHandler;
    private final UnclaimRegionCommandHandler unclaimRegionHandler;

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
        this.leaderHandler = new LeaderCommandHandler(clanManager);
        this.nameHanler = new NameCommandHandler(clanManager);
        this.descriptionHanler = new DescCommandHandler(clanManager);
        this.moderHanler = new ModerCommandHandler(clanManager);
        this.unclaimRegionHandler = new UnclaimRegionCommandHandler(clanManager);
        this.claimRegionHandler = new ClaimRegionCommandHandler(clanManager, unclaimRegionHandler);
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
                    return listHandler.handleCommand(player, args);
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
                case "leader":
                    return leaderHandler.handleCommand(player, args);
                case "name":
                    return nameHanler.handleCommand(player, args);
                case "desc":
                    return descriptionHanler.handleCommand(player, args);
                case "moder":
                    return moderHanler.handleCommand(player, args);
                case "claim":
                    return claimRegionHandler.handleCommand(player);
                case "unclaim":
                    return unclaimRegionHandler.handleCommand(player, args);

                default:
                    TextComponent usageMessage = new TextComponent(ChatColor.YELLOW + "Неизвестная подкоманда. Для списка команд: ");
                    TextComponent clickCommand = new TextComponent(ChatColor.GOLD + "/clan");
                    clickCommand.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/clan "));
                    usageMessage.addExtra(clickCommand);
                    player.spigot().sendMessage(usageMessage);
                    break;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Эту команду могут использовать только игроки.");
        }
        return true;
    }

    void addCommand(TextComponent parent, String commandText, String commandSuggestion, String description) {
        TextComponent cmdComponent = new TextComponent("\n" +ChatColor.YELLOW + commandText);
        cmdComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, commandSuggestion));
        TextComponent descComponent = new TextComponent(ChatColor.WHITE + " - " + description); // Дополнительный перенос строки
        parent.addExtra(cmdComponent);
        parent.addExtra(descComponent);
    }


    private void showCommands(Player player) {
        TextComponent commandsInfo = new TextComponent(ChatColor.GREEN + "**** " + ChatColor.WHITE + "Доступные команды:" + ChatColor.GREEN + " ****");
        addCommand(commandsInfo, "/clan create <название>", "/clan create ", "Создать новый клан");
        addCommand(commandsInfo, "/clan name <название>", "/clan name ", "Поменять название клана");
        addCommand(commandsInfo, "/clan desc <описание>", "/clan desc ", "Поменять описание клана");
        addCommand(commandsInfo, "/clan claim", "/clan claim", "Заприватить чанк");
        addCommand(commandsInfo, "/clan unclaim", "/clan unclaim", "Убрать приват чанка");
        addCommand(commandsInfo, "/clan disband", "/clan disband", "Распустить клан");
        addCommand(commandsInfo, "/clan ally <название>", "/clan ally ", "Предложить альянс клану");
        addCommand(commandsInfo, "/clan leader <игрок>", "/clan leader ", "Сделать игрока лидером");
        addCommand(commandsInfo, "/clan moder <игрок>", "/clan moder ", "Повысить игрока до Заместителя");
        addCommand(commandsInfo, "/clan promote <игрок>", "/clan promote ", "Повысить игрока");
        addCommand(commandsInfo, "/clan demote <игрок>", "/clan demote ", "Понизить игрока");
        addCommand(commandsInfo, "/clan invite <игрок>", "/clan invite ", "Пригласить игрока в ваш клан");
        addCommand(commandsInfo, "/clan join <название клана>", "/clan join ", "Присоединиться к клану");
        addCommand(commandsInfo, "/clan leave", "/clan leave", "Покинуть клан");
        addCommand(commandsInfo, "/clan kick <игрок>", "/clan kick ", "Выгнать игрока из клана");
        addCommand(commandsInfo, "/clan info <игрок>", "/clan info ", "Информация о клане игрока");
        addCommand(commandsInfo, "/clan info <название>", "/clan info ", "Информация о клане");
        addCommand(commandsInfo, "/clan info", "/clan info ", "Информация о вашем клане");
        addCommand(commandsInfo, "/clan home", "/clan home", "Телепорт на точку дома");
        addCommand(commandsInfo, "/clan sethome", "/clan sethome", "Добавить точку дома");
        addCommand(commandsInfo, "/clan delhome", "/clan delhome", "Удалить точку дома");
        addCommand(commandsInfo, "/clan list", "/clan list", "Показать список всех кланов");
        player.spigot().sendMessage(commandsInfo);
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
        } else if (args.length == 2 && args[0].equalsIgnoreCase("promote")) {
            return getPlayerSuggestions(args[1]);
        }else if (args.length == 2 && args[0].equalsIgnoreCase("demote")) {
            return getPlayerSuggestions(args[1]);
        }else if (args.length == 2 && args[0].equalsIgnoreCase("leader")) {
            return getPlayerSuggestions(args[1]);
        }else if (args.length == 2 && args[0].equalsIgnoreCase("moder")) {
            return getPlayerSuggestions(args[1]);
        }
        return new ArrayList<>();
    }

    private List<String> getSubCommandSuggestions(String input) {
        List<String> subCommands = Arrays.asList("create", "invite", "join", "list", "disband", "leave", "kick", "sethome",
                "delhome", "home", "info", "promote", "demote", "ally", "leader", "name", "desc", "moder", "unclaim", "claim");
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