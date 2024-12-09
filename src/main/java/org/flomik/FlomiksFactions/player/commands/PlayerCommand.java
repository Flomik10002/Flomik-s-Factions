package org.flomik.FlomiksFactions.player.commands;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.flomik.FlomiksFactions.clan.Clan;
import org.flomik.FlomiksFactions.clan.ClanManager;
import org.flomik.FlomiksFactions.player.PlayerDataHandler;

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
                    String arg = args.length > 1 ? args[1] : "";
                    String playerName = "";
                    Clan curClan = null;
                    String firstJoinDate = null;
                    int ticksPlayed = 0;

                    if (arg.isEmpty()) {
                        curClan = clanManager.getPlayerClan(player.getName());
                        firstJoinDate = playerDataHandler.getFirstJoinDate(player.getName());
                        ticksPlayed = playerDataHandler.getPlayTime(player.getName());
                        playerName = player.getName();
                    } else {
                        if (playerDataHandler.hasPlayerData(arg)) {
                            curClan = clanManager.getPlayerClan(arg);
                            playerName = arg;
                            firstJoinDate = playerDataHandler.getFirstJoinDate(arg);
                            ticksPlayed = playerDataHandler.getPlayTime(arg);
                        } else {
                            player.sendMessage("Игрок не найден");
                            return true;
                        }
                    }

                    int secondsPlayed = ticksPlayed / 20;

                    int days = secondsPlayed / 86400;
                    int hours = (secondsPlayed % 86400) / 3600;
                    int minutes = (secondsPlayed % 3600) / 60;

                    String playTimeMessage = String.format(
                            "%dд, %dч, %dм.",
                            days, hours, minutes
                    );

                    StringBuilder info = new StringBuilder();
                    info.append(ChatColor.GREEN).append("***** ").append(ChatColor.WHITE).append("Игрок ").append(playerName).append(ChatColor.GREEN).append(" *****\n");
                    info.append(ChatColor.GOLD).append("Играет с: ").append(ChatColor.YELLOW).append(firstJoinDate).append("\n");
                    info.append(ChatColor.GOLD).append("Онлайн: ").append(ChatColor.YELLOW).append(playTimeMessage).append("\n");
                    info.append(ChatColor.GOLD).append("Сила/Макс. Сила: ").append(ChatColor.YELLOW).append(playerDataHandler.getPlayerStrength(playerName)).append("/").append(playerDataHandler.getPlayerMaxStrength(playerName)).append("\n");
                    info.append(ChatColor.GOLD).append("Уровень: ").append(ChatColor.YELLOW).append(playerDataHandler.getPlayerLevel(playerName)).append("\n");

                    if (curClan != null) {
                        TextComponent clanInfoComponent = new TextComponent();
                        clanInfoComponent.setText(ChatColor.GRAY + "[" + ChatColor.YELLOW + "-" + ChatColor.GRAY + "] " + ChatColor.YELLOW + curClan.getName());
                        clanInfoComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan info " + curClan.getName()));
                        clanInfoComponent.setHoverEvent(new net.md_5.bungee.api.chat.HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new Text("Нажмите, чтобы узнать больше о клане")));

                        info.append(ChatColor.GOLD).append("Клан: ").append(ChatColor.YELLOW).append(curClan.getRole(playerName)).append(ChatColor.GOLD).append(" в ");
                        player.spigot().sendMessage(new ComponentBuilder(info.toString()).append(clanInfoComponent).append(ChatColor.GOLD + " (" + ChatColor.GREEN + getOnlineMembersCount(curClan) + ChatColor.GRAY + "/" + ChatColor.YELLOW + curClan.getMembers().size() + ChatColor.GOLD + ")").create());
                    } else {
                        info.append(ChatColor.GOLD).append("Не состоит в клане.");
                        player.sendMessage(info.toString());
                    }
                    return true;

                default:
                    TextComponent usageMessage = new TextComponent(ChatColor.YELLOW + "Для списка команд: ");
                    TextComponent clickCommand = new TextComponent(ChatColor.GOLD + "/player ");
                    clickCommand.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/player "));
                    usageMessage.addExtra(clickCommand);
                    player.spigot().sendMessage(usageMessage);
                    break;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Эту команду могут использовать только игроки.");
        }
        return true;
    }

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

    private void showCommands(Player player) {
        TextComponent headerMessage = new TextComponent(ChatColor.GREEN + "**** " + ChatColor.WHITE + "Доступные команды:" + ChatColor.GREEN + " ****\n");
        TextComponent clickCommand = new TextComponent(ChatColor.GOLD + "/p info <игрок> ");
        clickCommand.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/p info "));
        TextComponent usageMessage = new TextComponent(ChatColor.WHITE + " - Информация о игроке");
        headerMessage.addExtra(clickCommand);
        headerMessage.addExtra(usageMessage);
        player.spigot().sendMessage(headerMessage);
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