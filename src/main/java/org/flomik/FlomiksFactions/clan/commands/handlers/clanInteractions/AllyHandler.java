package org.flomik.FlomiksFactions.clan.commands.handlers.clanInteractions; //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.flomik.FlomiksFactions.clan.Clan;
import org.flomik.FlomiksFactions.clan.managers.ClanManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class AllyHandler { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression

    private final ClanManager clanManager; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private final ConcurrentHashMap<String, List<String>> pendingAllies; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression

    public AllyHandler(ClanManager clanManager, ConcurrentHashMap<String, List<String>> pendingAllies) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        this.clanManager = clanManager;
        this.pendingAllies = pendingAllies;
    }

    public boolean handleCommand(Player player, String[] args) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        if (args.length > 1) { //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression

            String allyClanName = args[1].trim().toLowerCase(); //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression

            Clan playerClan = clanManager.getPlayerClan(player.getName()); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            Clan allyClan = clanManager.getClan(allyClanName); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression

            if (playerClan == null) {
                player.sendMessage(ChatColor.RED + "Вы не состоите в клане.");
                return true; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
            }


            String playerRole = playerClan.getRole(player.getName()); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            if (!playerRole.equals("Лидер") && !playerRole.equals("Заместитель")) { //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression
                player.sendMessage(ChatColor.RED + "Только Лидер или Заместитель клана могут предлагать, отменять или расторгать союзы.");
                return true; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
            }


            if (playerClan.getAlliances().size() == 3) { //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression
                player.sendMessage(ChatColor.RED + "У вас превышен лимит альянсов.");
                return true; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
            }


            if (allyClan == null) {
                player.sendMessage(ChatColor.RED + "Клан с таким названием не существует.");
                return true; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
            }


            if (allyClan.getAlliances().size() == 3) { //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression
                player.sendMessage(ChatColor.RED + "У желаемого клана превышен лимит альянсов.");
                return true; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
            }


            if (playerClan.getName().equals(allyClan.getName())) {
                player.sendMessage(ChatColor.RED + "Вы не можете предложить союз своему же клану.");
                return true; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
            }


            if (playerClan.getAlliances().contains(allyClan.getName())) {

                playerClan.removeAlliance(allyClan);
                allyClan.removeAlliance(playerClan);
                clanManager.updateClan(playerClan);
                clanManager.updateClan(allyClan);

                Bukkit.broadcastMessage(ChatColor.GREEN + "Кланы " + ChatColor.YELLOW + allyClan.getName() + ChatColor.GREEN + " и " + ChatColor.YELLOW + playerClan.getName() + ChatColor.GREEN + " расторгли союз.");
                return true; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
            }

            String playerClanName = playerClan.getName(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            List<String> allies = pendingAllies.getOrDefault(playerClanName.toLowerCase(), new ArrayList<>()); //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression


            if (allies.contains(allyClanName)) {

                allies.remove(allyClanName);
                pendingAllies.put(playerClanName.toLowerCase(), allies); //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression
                player.sendMessage(ChatColor.GREEN + "Предложение о союзе с кланом " + ChatColor.YELLOW + allyClan.getName() + ChatColor.GREEN + " было отменено."); //NOPMD - suppressed AvoidDuplicateLiterals - TODO explain reason for suppression //NOPMD - suppressed AvoidDuplicateLiterals - TODO explain reason for suppression //NOPMD - suppressed AvoidDuplicateLiterals - TODO explain reason for suppression
                sendMessageToRole(allyClan, ChatColor.RED + "Предложение о союзе с кланом " + ChatColor.YELLOW + playerClan.getName() + ChatColor.RED + " было отменено.");
            } else {

                if (pendingAllies.getOrDefault(allyClan.getName().toLowerCase(), new ArrayList<>()).contains(playerClanName.toLowerCase())) { //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression

                    playerClan.addAlliances(allyClan);
                    allyClan.addAlliances(playerClan);
                    clanManager.updateClan(playerClan);
                    clanManager.updateClan(allyClan);


                    pendingAllies.get(allyClan.getName().toLowerCase()).remove(playerClanName.toLowerCase()); //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression
                    Bukkit.broadcastMessage(ChatColor.GREEN + "Кланы " + ChatColor.YELLOW + allyClan.getName() + ChatColor.GREEN + " и " + ChatColor.YELLOW + playerClan.getName() + ChatColor.GREEN + " заключили союз.");
                } else {

                    allies.add(allyClanName);
                    pendingAllies.put(playerClanName.toLowerCase(), allies); //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression
                    player.sendMessage(ChatColor.GREEN + "Предложение о союзе с кланом " + ChatColor.YELLOW + allyClan.getName() + ChatColor.GREEN + " отправлено.");
                    player.sendMessage(ChatColor.YELLOW + "Для отмены предложения о союзе повторите команду.");

                    TextComponent allyMessage = new TextComponent(ChatColor.GREEN + "Клан " + ChatColor.YELLOW + playerClan.getName() + ChatColor.GREEN + " предложил вам союз. Используйте "); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                    TextComponent allyCommand = new TextComponent(ChatColor.YELLOW + "/clan ally " + playerClan.getName()); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                    allyCommand.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/clan ally " + playerClan.getName()));
                    allyMessage.addExtra(allyCommand);
                    allyMessage.addExtra(new TextComponent(ChatColor.GREEN + " для принятия предложения."));
                    sendMessageToRole(allyClan, allyMessage);


                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            List<String> currentAllies = pendingAllies.get(playerClanName.toLowerCase()); //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression
                            if (currentAllies != null && currentAllies.contains(allyClanName)) {
                                currentAllies.remove(allyClanName);
                                if (currentAllies.isEmpty()) {
                                    pendingAllies.remove(playerClanName.toLowerCase()); //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression
                                } else {
                                    pendingAllies.put(playerClanName.toLowerCase(), currentAllies); //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression
                                }
                                player.sendMessage(ChatColor.RED + "Предложение о союзе с кланом " + ChatColor.GOLD + allyClan.getName() + ChatColor.RED + " истекло.");
                                sendMessageToRole(allyClan, ChatColor.RED + "Предложение о союзе от клана " + ChatColor.GOLD + playerClan.getName() + ChatColor.RED + " истекло.");
                            }
                        }
                    }.runTaskLater(Bukkit.getPluginManager().getPlugin("FlomiksFactions"), 5 * 60 * 20);
                }
            }
        } else {
            TextComponent usageMessage = new TextComponent(ChatColor.YELLOW + "Использование: "); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            TextComponent clickCommand = new TextComponent(ChatColor.GOLD + "/clan ally <название>"); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            clickCommand.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/clan ally "));
            usageMessage.addExtra(clickCommand);
            player.spigot().sendMessage(usageMessage);
        }
        return true;
    }

    private void sendMessageToRole(Clan clan, Object message) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        List<String> rolesToNotify = List.of("Лидер", "Заместитель"); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        for (String role : rolesToNotify) { //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            List<String> playersWithRole = clan.getPlayersWithRole(role); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            for (String playerName : playersWithRole) { //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                Player player = Bukkit.getPlayer(playerName); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                if (player != null) {
                    if (message instanceof TextComponent) {
                        player.spigot().sendMessage((TextComponent) message);
                    } else if (message instanceof String) {
                        player.sendMessage((String) message);
                    }
                }
            }
        }
    }
}
