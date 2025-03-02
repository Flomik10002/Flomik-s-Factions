package org.flomik.FlomiksFactions.commands;

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
import org.flomik.FlomiksFactions.clan.Clan;
import org.flomik.FlomiksFactions.clan.managers.BeaconManager;
import org.flomik.FlomiksFactions.clan.managers.ClanManager;
import org.flomik.FlomiksFactions.commands.handlers.playerInteractions.HelpHandler;
import org.flomik.FlomiksFactions.commands.handlers.playerInteractions.InfoHandler;
import org.flomik.FlomiksFactions.commands.handlers.playerInteractions.ListHandler;
import org.flomik.FlomiksFactions.clan.managers.ChunkMenuManager;
import org.flomik.FlomiksFactions.commands.handlers.clanInteractions.*;
import org.flomik.FlomiksFactions.commands.handlers.home.DelHomeHandler;
import org.flomik.FlomiksFactions.commands.handlers.home.HomeHandler;
import org.flomik.FlomiksFactions.commands.handlers.home.SetHomeHandler;
import org.flomik.FlomiksFactions.commands.handlers.playerInteractions.*;
import org.flomik.FlomiksFactions.database.BeaconDao;
import org.flomik.FlomiksFactions.player.PlayerDataHandler;
import org.flomik.FlomiksFactions.worldEvents.shrine.event.ShrineEvent;

import java.util.*;

import java.util.concurrent.ConcurrentHashMap;

public class ClanCommand implements CommandExecutor, TabCompleter {

    private final ConcurrentHashMap<String, Long> pendingDisbands = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<String>> pendingAllies = new ConcurrentHashMap<>();

    private final ClanManager clanManager;
    private final BeaconManager beaconManager;
    private final BeaconDao beaconDao;
    private final CreateHandler createHandler;
    private final DisbandHandler disbandHandler;
    private final PromoteHandler promoteHandler;
    private final DemoteHandler demoteHandler;
    private final InviteHandler inviteHandler;
    private final JoinHandler joinHandler;
    private final KickHandler kickHandler;
    private final ListHandler listHandler;
    private final LeaveHandler leaveHandler;
    private final InfoHandler infoHandler;
    private final SetHomeHandler setHomeHandler;
    private final DelHomeHandler delHomeHandler;
    private final HomeHandler homeHandler;
    private final AllyHandler allyHandler;
    private final LeaderHandler leaderHandler;
    private final NameHandler renameHanler;
    private final DescriptionHandler descriptionHanler;
    private final ModerHandler moderHanler;
    private final ClaimRegionHandler claimRegionHandler;
    private final UnclaimRegionHandler unclaimRegionHandler;
    private final HelpHandler helpHandler;
    private final MapHandler mapHandler;
    private final BankCommandHandler bankCommandHandler;

    public ClanCommand(ClanManager clanManager, PlayerDataHandler playerDataHandler, FlomiksFactions plugin,
                       ChunkMenuManager chunkMenuManager, ShrineEvent shrineEvent, BeaconDao beaconDao, BeaconManager beaconManager) {
        this.clanManager = clanManager;
        this.beaconManager = beaconManager;
        this.beaconDao = beaconDao;
        this.createHandler = new CreateHandler(clanManager);
        this.disbandHandler = new DisbandHandler(clanManager, pendingDisbands);
        this.promoteHandler = new PromoteHandler(clanManager);
        this.demoteHandler = new DemoteHandler(clanManager);
        this.inviteHandler = new InviteHandler(clanManager);
        this.joinHandler = new JoinHandler(clanManager);
        this.kickHandler = new KickHandler(clanManager);
        this.listHandler = new ListHandler(clanManager);
        this.leaveHandler = new LeaveHandler(clanManager, pendingDisbands);
        this.infoHandler = new InfoHandler(clanManager, playerDataHandler);
        this.setHomeHandler = new SetHomeHandler(clanManager);
        this.delHomeHandler = new DelHomeHandler(clanManager);
        this.homeHandler = new HomeHandler(clanManager, plugin);
        this.allyHandler = new AllyHandler(clanManager, pendingAllies);
        this.leaderHandler = new LeaderHandler(clanManager);
        this.renameHanler = new NameHandler(clanManager);
        this.descriptionHanler = new DescriptionHandler(clanManager);
        this.moderHanler = new ModerHandler(clanManager);
        this.unclaimRegionHandler = new UnclaimRegionHandler(clanManager, beaconDao, beaconManager);
        this.claimRegionHandler = new ClaimRegionHandler(clanManager, unclaimRegionHandler, shrineEvent, beaconDao, beaconManager);
        this.helpHandler = new HelpHandler();
        this.mapHandler = new MapHandler(chunkMenuManager);
        this.bankCommandHandler = new BankCommandHandler(clanManager, plugin.getEconomy());
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
                case "rename":
                    return renameHanler.handleCommand(player, args);
                case "desc":
                    return descriptionHanler.handleCommand(player, args);
                case "moder":
                    return moderHanler.handleCommand(player, args);
                case "claim":
                    return claimRegionHandler.handleCommand(player, args);
                case "unclaim":
                    return unclaimRegionHandler.handleCommand(player, args);
                case "bank":
                    return bankCommandHandler.handleCommand(player, args);
                case "help":
                    return helpHandler.handleCommand(player, args);
                case "map":
                    return mapHandler.handleCommand(player);

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
        }else if (args.length == 2 && args[0].equalsIgnoreCase("bank")) {
            return getSuggestions(args[1], Arrays.asList("deposit", "withdraw"));
        }
        return new ArrayList<>();
    }

    private List<String> getSubCommandSuggestions(String input) {
        List<String> subCommands = Arrays.asList("create", "invite", "join", "list", "disband", "leave", "kick", "bank", "sethome",
                "delhome", "home", "info", "promote", "demote", "ally", "leader", "rename", "desc", "moder", "unclaim", "claim", "help", "map");
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