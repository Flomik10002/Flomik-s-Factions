package org.flomik.FlomiksFactions.clan.commands.clanInteractions;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.flomik.FlomiksFactions.clan.Clan;
import org.flomik.FlomiksFactions.clan.ClanManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class AllyCommandHandler {

    private final ClanManager clanManager;
    private final ConcurrentHashMap<String, List<String>> pendingAllies;

    public AllyCommandHandler(ClanManager clanManager, ConcurrentHashMap<String, List<String>> pendingAllies) {
        this.clanManager = clanManager;
        this.pendingAllies = pendingAllies;
    }

    public boolean handleCommand(Player player, String[] args) {
        if (args.length > 1) {

            String allyClanName = args[1].trim().toLowerCase();

            Clan playerClan = clanManager.getPlayerClan(player.getName());
            Clan allyClan = clanManager.getClan(allyClanName);

            if (playerClan == null) {
                player.sendMessage(ChatColor.RED + "Вы не состоите в клане.");
                return true;
            }


            String playerRole = playerClan.getRole(player.getName());
            if (!playerRole.equals("Лидер") && !playerRole.equals("Заместитель")) {
                player.sendMessage(ChatColor.RED + "Только Лидер или Заместитель клана могут предлагать, отменять или расторгать союзы.");
                return true;
            }


            if (playerClan.getAlliances().size() == 3) {
                player.sendMessage(ChatColor.RED + "У вас превышен лимит альянсов.");
                return true;
            }


            if (allyClan == null) {
                player.sendMessage(ChatColor.RED + "Клан с таким названием не существует.");
                return true;
            }


            if (allyClan.getAlliances().size() == 3) {
                player.sendMessage(ChatColor.RED + "У желаемого клана превышен лимит альянсов.");
                return true;
            }


            if (playerClan.getName().equals(allyClan.getName())) {
                player.sendMessage(ChatColor.RED + "Вы не можете предложить союз своему же клану.");
                return true;
            }


            if (playerClan.getAlliances().contains(allyClan.getName())) {

                playerClan.removeAlliance(allyClan);
                allyClan.removeAlliance(playerClan);
                clanManager.updateClan(playerClan);
                clanManager.updateClan(allyClan);

                Bukkit.broadcastMessage(ChatColor.GREEN + "Кланы " + ChatColor.YELLOW + allyClan.getName() + ChatColor.GREEN + " и " + ChatColor.YELLOW + playerClan.getName() + ChatColor.GREEN + " расторгли союз.");
                return true;
            }

            String playerClanName = playerClan.getName();
            List<String> allies = pendingAllies.getOrDefault(playerClanName.toLowerCase(), new ArrayList<>());


            if (allies.contains(allyClanName)) {

                allies.remove(allyClanName);
                pendingAllies.put(playerClanName.toLowerCase(), allies);
                player.sendMessage(ChatColor.GREEN + "Предложение о союзе с кланом " + ChatColor.YELLOW + allyClan.getName() + ChatColor.GREEN + " было отменено.");
                sendMessageToRole(allyClan, ChatColor.RED + "Предложение о союзе с кланом " + ChatColor.YELLOW + playerClan.getName() + ChatColor.RED + " было отменено.");
            } else {

                if (pendingAllies.getOrDefault(allyClan.getName().toLowerCase(), new ArrayList<>()).contains(playerClanName.toLowerCase())) {

                    playerClan.addAlliances(allyClan);
                    allyClan.addAlliances(playerClan);
                    clanManager.updateClan(playerClan);
                    clanManager.updateClan(allyClan);


                    pendingAllies.get(allyClan.getName().toLowerCase()).remove(playerClanName.toLowerCase());
                    Bukkit.broadcastMessage(ChatColor.GREEN + "Кланы " + ChatColor.YELLOW + allyClan.getName() + ChatColor.GREEN + " и " + ChatColor.YELLOW + playerClan.getName() + ChatColor.GREEN + " заключили союз.");
                } else {

                    allies.add(allyClanName);
                    pendingAllies.put(playerClanName.toLowerCase(), allies);
                    player.sendMessage(ChatColor.GREEN + "Предложение о союзе с кланом " + ChatColor.YELLOW + allyClan.getName() + ChatColor.GREEN + " отправлено.");
                    player.sendMessage(ChatColor.YELLOW + "Для отмены предложения о союзе повторите команду.");

                    TextComponent allyMessage = new TextComponent(ChatColor.GREEN + "Клан " + ChatColor.YELLOW + playerClan.getName() + ChatColor.GREEN + " предложил вам союз. Используйте ");
                    TextComponent allyCommand = new TextComponent(ChatColor.YELLOW + "/clan ally " + playerClan.getName());
                    allyCommand.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/clan ally " + playerClan.getName()));
                    allyMessage.addExtra(allyCommand);
                    allyMessage.addExtra(new TextComponent(ChatColor.GREEN + " для принятия предложения."));
                    sendMessageToRole(allyClan, allyMessage);


                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            List<String> currentAllies = pendingAllies.get(playerClanName.toLowerCase());
                            if (currentAllies != null && currentAllies.contains(allyClanName)) {
                                currentAllies.remove(allyClanName);
                                if (currentAllies.isEmpty()) {
                                    pendingAllies.remove(playerClanName.toLowerCase());
                                } else {
                                    pendingAllies.put(playerClanName.toLowerCase(), currentAllies);
                                }
                                player.sendMessage(ChatColor.RED + "Предложение о союзе с кланом " + ChatColor.GOLD + allyClan.getName() + ChatColor.RED + " истекло.");
                                sendMessageToRole(allyClan, ChatColor.RED + "Предложение о союзе от клана " + ChatColor.GOLD + playerClan.getName() + ChatColor.RED + " истекло.");
                            }
                        }
                    }.runTaskLater(Bukkit.getPluginManager().getPlugin("FlomiksFactions"), 5 * 60 * 20);
                }
            }
        } else {
            TextComponent usageMessage = new TextComponent(ChatColor.YELLOW + "Использование: ");
            TextComponent clickCommand = new TextComponent(ChatColor.GOLD + "/clan ally <название>");
            clickCommand.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/clan ally "));
            usageMessage.addExtra(clickCommand);
            player.spigot().sendMessage(usageMessage);
        }
        return true;
    }

    private void sendMessageToRole(Clan clan, Object message) {
        List<String> rolesToNotify = List.of("Лидер", "Заместитель");
        for (String role : rolesToNotify) {
            List<String> playersWithRole = clan.getPlayersWithRole(role);
            for (String playerName : playersWithRole) {
                Player player = Bukkit.getPlayer(playerName);
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
