package org.flomik.FlomiksFactions.clan;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.flomik.FlomiksFactions.FlomiksFactions;
import org.flomik.FlomiksFactions.clan.commands.HelpCommandHandler;
import org.flomik.FlomiksFactions.clan.commands.InfoCommandHandler;
import org.flomik.FlomiksFactions.clan.commands.ListCommandHandler;
import org.flomik.FlomiksFactions.menu.MenuManager;
import org.flomik.FlomiksFactions.clan.commands.clanInteractions.*;
import org.flomik.FlomiksFactions.clan.commands.home.DelHomeCommandHandler;
import org.flomik.FlomiksFactions.clan.commands.home.HomeCommandHandler;
import org.flomik.FlomiksFactions.clan.commands.home.SetHomeCommandHandler;
import org.flomik.FlomiksFactions.clan.commands.playerInteractions.*;
import org.flomik.FlomiksFactions.player.PlayerDataHandler;
import org.flomik.FlomiksFactions.worldEvents.shrine.ShrineEvent;

import java.util.*;

import java.util.concurrent.ConcurrentHashMap;

public class ClanCommand implements CommandExecutor, TabCompleter {

    private final ConcurrentHashMap<String, Long> pendingDisbands = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<String>> pendingAllies = new ConcurrentHashMap<>();

    private final ClanManager clanManager;
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
    private final HelpCommandHandler helpCommandHandler;
    private final MapCommandHandler mapCommandHandler;

    public ClanCommand(ClanManager clanManager, PlayerDataHandler playerDataHandler, FlomiksFactions plugin, MenuManager menuManager, ShrineEvent shrineEvent) {
        this.clanManager = clanManager;
        this.createHandler = new CreateCommandHandler(clanManager);
        this.disbandHandler = new DisbandCommandHandler(clanManager, pendingDisbands);
        this.promoteHandler = new PromoteCommandHandler(clanManager);
        this.demoteHandler = new DemoteCommandHandler(clanManager);
        this.inviteHandler = new InviteCommandHandler(clanManager);
        this.joinHandler = new JoinCommandHandler(clanManager);
        this.kickHandler = new KickCommandHandler(clanManager);
        this.listHandler = new ListCommandHandler(clanManager);
        this.leaveHandler = new LeaveCommandHandler(clanManager, pendingDisbands);
        this.infoHandler = new InfoCommandHandler(clanManager, playerDataHandler);
        this.setHomeHandler = new SetHomeCommandHandler(clanManager);
        this.delHomeHandler = new DelHomeCommandHandler(clanManager);
        this.homeHandler = new HomeCommandHandler(clanManager, plugin);
        this.allyHandler = new AllyCommandHandler(clanManager, pendingAllies);
        this.leaderHandler = new LeaderCommandHandler(clanManager);
        this.nameHanler = new NameCommandHandler(clanManager);
        this.descriptionHanler = new DescCommandHandler(clanManager);
        this.moderHanler = new ModerCommandHandler(clanManager);
        this.unclaimRegionHandler = new UnclaimRegionCommandHandler(clanManager);
        this.claimRegionHandler = new ClaimRegionCommandHandler(clanManager, unclaimRegionHandler, shrineEvent);
        this.helpCommandHandler = new HelpCommandHandler();
        this.mapCommandHandler = new MapCommandHandler(menuManager);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length == 0) {
                TextComponent usageMessage = new TextComponent(ChatColor.YELLOW + "Неизвестная подкоманда. Для списка команд: ");
                TextComponent clickCommand = new TextComponent(ChatColor.GOLD + "/clan help");
                clickCommand.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/clan help "));
                usageMessage.addExtra(clickCommand);
                player.spigot().sendMessage(usageMessage);
                return false;
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
                case "help":
                    return helpCommandHandler.handleCommand(player, args);
                case "map":
                    return mapCommandHandler.handleCommand(player);

                default:
                    TextComponent usageMessage = new TextComponent(ChatColor.YELLOW + "Неизвестная подкоманда. Для списка команд: ");
                    TextComponent clickCommand = new TextComponent(ChatColor.GOLD + "/clan help");
                    clickCommand.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/clan help "));
                    usageMessage.addExtra(clickCommand);
                    player.spigot().sendMessage(usageMessage);
                    break;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Эту команду могут использовать только игроки.");
        }
        return true;
    }


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
                "delhome", "home", "info", "promote", "demote", "ally", "leader", "name", "desc", "moder", "unclaim", "claim", "help", "map");
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