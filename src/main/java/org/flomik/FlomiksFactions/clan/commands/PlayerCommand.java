package org.flomik.FlomiksFactions.clan.commands; //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression

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
import org.flomik.FlomiksFactions.clan.managers.ClanManager;
import org.flomik.FlomiksFactions.player.PlayerDataHandler;

import java.util.*;

public class PlayerCommand implements CommandExecutor, TabCompleter { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private final PlayerDataHandler playerDataHandler; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private final ClanManager clanManager; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression

    public PlayerCommand(PlayerDataHandler playerDataHandler, ClanManager clanManager) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        this.playerDataHandler = playerDataHandler;
        this.clanManager = clanManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) { //NOPMD - suppressed NcssCount - TODO explain reason for suppression //NOPMD - suppressed NcssCount - TODO explain reason for suppression //NOPMD - suppressed NcssCount - TODO explain reason for suppression
        if (sender instanceof Player) {
            Player player = (Player) sender; //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression

            if (args.length == 0) {
                showCommands(player);
                return true; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
            }

            String subCommand = args[0].toLowerCase(); //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression
            switch (subCommand) { //NOPMD - suppressed TooFewBranchesForSwitch - TODO explain reason for suppression //NOPMD - suppressed TooFewBranchesForSwitch - TODO explain reason for suppression //NOPMD - suppressed TooFewBranchesForSwitch - TODO explain reason for suppression
                case "info":
                    String arg = args.length > 1 ? args[1] : ""; //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                    String playerName = ""; //NOPMD - suppressed UnusedAssignment - TODO explain reason for suppression //NOPMD - suppressed UnusedAssignment - TODO explain reason for suppression //NOPMD - suppressed UnusedAssignment - TODO explain reason for suppression
                    Clan curClan = null; //NOPMD - suppressed UnusedAssignment - TODO explain reason for suppression //NOPMD - suppressed UnusedAssignment - TODO explain reason for suppression //NOPMD - suppressed UnusedAssignment - TODO explain reason for suppression
                    String firstJoinDate = null; //NOPMD - suppressed UnusedAssignment - TODO explain reason for suppression //NOPMD - suppressed UnusedAssignment - TODO explain reason for suppression //NOPMD - suppressed UnusedAssignment - TODO explain reason for suppression
                    int ticksPlayed = 0; //NOPMD - suppressed UnusedAssignment - TODO explain reason for suppression //NOPMD - suppressed UnusedAssignment - TODO explain reason for suppression //NOPMD - suppressed UnusedAssignment - TODO explain reason for suppression

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
                            return true; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
                        }
                    }

                    int secondsPlayed = ticksPlayed / 20; //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression

                    int days = secondsPlayed / 86400; //NOPMD - suppressed UseUnderscoresInNumericLiterals - TODO explain reason for suppression //NOPMD - suppressed UseUnderscoresInNumericLiterals - TODO explain reason for suppression //NOPMD - suppressed UseUnderscoresInNumericLiterals - TODO explain reason for suppression
                    int hours = (secondsPlayed % 86400) / 3600; //NOPMD - suppressed UselessParentheses - TODO explain reason for suppression //NOPMD - suppressed UselessParentheses - TODO explain reason for suppression //NOPMD - suppressed UselessParentheses - TODO explain reason for suppression
                    int minutes = (secondsPlayed % 3600) / 60; //NOPMD - suppressed UselessParentheses - TODO explain reason for suppression //NOPMD - suppressed UselessParentheses - TODO explain reason for suppression //NOPMD - suppressed UselessParentheses - TODO explain reason for suppression

                    String playTimeMessage = String.format( //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                            "%dд, %dч, %dм.",
                            days, hours, minutes
                    );

                    StringBuilder info = new StringBuilder(); //NOPMD - suppressed InsufficientStringBufferDeclaration - TODO explain reason for suppression //NOPMD - suppressed InsufficientStringBufferDeclaration - TODO explain reason for suppression //NOPMD - suppressed InsufficientStringBufferDeclaration - TODO explain reason for suppression
                    info.append(ChatColor.GREEN).append("***** ").append(ChatColor.WHITE).append("Игрок ").append(playerName).append(ChatColor.GREEN).append(" *****\n"); //NOPMD - suppressed ConsecutiveAppendsShouldReuse - TODO explain reason for suppression //NOPMD - suppressed ConsecutiveAppendsShouldReuse - TODO explain reason for suppression //NOPMD - suppressed ConsecutiveAppendsShouldReuse - TODO explain reason for suppression
                    info.append(ChatColor.GOLD).append("Играет с: ").append(ChatColor.YELLOW).append(firstJoinDate).append("\n"); //NOPMD - suppressed ConsecutiveAppendsShouldReuse - TODO explain reason for suppression //NOPMD - suppressed ConsecutiveAppendsShouldReuse - TODO explain reason for suppression //NOPMD - suppressed ConsecutiveAppendsShouldReuse - TODO explain reason for suppression
                    info.append(ChatColor.GOLD).append("Онлайн: ").append(ChatColor.YELLOW).append(playTimeMessage).append("\n"); //NOPMD - suppressed ConsecutiveAppendsShouldReuse - TODO explain reason for suppression //NOPMD - suppressed ConsecutiveAppendsShouldReuse - TODO explain reason for suppression //NOPMD - suppressed ConsecutiveAppendsShouldReuse - TODO explain reason for suppression
                    info.append(ChatColor.GOLD).append("Сила/Макс. Сила: ").append(ChatColor.YELLOW).append(playerDataHandler.getPlayerStrength(playerName)).append("/").append(playerDataHandler.getPlayerMaxStrength(playerName)).append("\n"); //NOPMD - suppressed ConsecutiveAppendsShouldReuse - TODO explain reason for suppression //NOPMD - suppressed ConsecutiveAppendsShouldReuse - TODO explain reason for suppression //NOPMD - suppressed ConsecutiveAppendsShouldReuse - TODO explain reason for suppression
                    info.append(ChatColor.GOLD).append("Уровень: ").append(ChatColor.YELLOW).append(playerDataHandler.getPlayerLevel(playerName)).append("\n"); //NOPMD - suppressed AppendCharacterWithChar - TODO explain reason for suppression //NOPMD - suppressed AppendCharacterWithChar - TODO explain reason for suppression //NOPMD - suppressed AppendCharacterWithChar - TODO explain reason for suppression

                    if (curClan != null) {
                        TextComponent clanInfoComponent = new TextComponent(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                        clanInfoComponent.setText(ChatColor.GRAY + "[" + ChatColor.YELLOW + "-" + ChatColor.GRAY + "] " + ChatColor.YELLOW + curClan.getName());
                        clanInfoComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan info " + curClan.getName()));
                        clanInfoComponent.setHoverEvent(new net.md_5.bungee.api.chat.HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new Text("Нажмите, чтобы узнать больше о клане")));

                        info.append(ChatColor.GOLD).append("Клан: ").append(ChatColor.YELLOW).append(curClan.getRole(playerName)).append(ChatColor.GOLD).append(" в ");
                        player.spigot().sendMessage(new ComponentBuilder(info.toString()).append(clanInfoComponent).append(ChatColor.GOLD + " (" + ChatColor.GREEN + getOnlineMembersCount(curClan) + ChatColor.GRAY + "/" + ChatColor.YELLOW + curClan.getMembers().size() + ChatColor.GOLD + ")").create());
                    } else {
                        info.append(ChatColor.GOLD).append("Не состоит в клане.");
                        player.sendMessage(info.toString());
                    }
                    return true; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression

                default:
                    TextComponent usageMessage = new TextComponent(ChatColor.YELLOW + "Для списка команд: "); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                    TextComponent clickCommand = new TextComponent(ChatColor.GOLD + "/player "); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
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

    private int getOnlineMembersCount(Clan clan) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        int onlineCount = 0;
        for (String member : clan.getMembers()) { //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            Player player = Bukkit.getPlayer(member); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            if (player != null && player.isOnline()) {
                onlineCount++;
            }
        }
        return onlineCount;
    }

    private void showCommands(Player player) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        TextComponent headerMessage = new TextComponent(ChatColor.GREEN + "**** " + ChatColor.WHITE + "Доступные команды:" + ChatColor.GREEN + " ****\n"); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        TextComponent clickCommand = new TextComponent(ChatColor.GOLD + "/p info <игрок> "); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        clickCommand.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/p info "));
        TextComponent usageMessage = new TextComponent(ChatColor.WHITE + " - Информация о игроке"); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        headerMessage.addExtra(clickCommand);
        headerMessage.addExtra(usageMessage);
        player.spigot().sendMessage(headerMessage);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        if (args.length == 1) { //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression
            return getSubCommandSuggestions(args[0]); //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
        } else if (args.length == 2 && args[0].equalsIgnoreCase("info")) { //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression
            return getPlayerSuggestions(args[1]); //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
        }
        return new ArrayList<>();
    }

    private List<String> getSubCommandSuggestions(String input) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        List<String> subCommands = Arrays.asList("info"); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        return getSuggestions(input, subCommands);
    }

    private List<String> getPlayerSuggestions(String input) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        List<String> playerNames = new ArrayList<>(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        for (Player player : Bukkit.getOnlinePlayers()) { //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            if (player.getName().toLowerCase().startsWith(input.toLowerCase())) { //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression
                playerNames.add(player.getName());
            }
        }
        return playerNames;
    }

    private List<String> getSuggestions(String input, List<String> options) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        List<String> suggestions = new ArrayList<>(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        for (String option : options) { //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            if (option.toLowerCase().startsWith(input.toLowerCase())) { //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression
                suggestions.add(option);
            }
        }
        return suggestions;
    }
}