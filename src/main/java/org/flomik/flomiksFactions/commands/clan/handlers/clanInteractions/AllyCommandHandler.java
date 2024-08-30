package org.flomik.flomiksFactions.commands.clan.handlers.clanInteractions;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.flomik.flomiksFactions.commands.clan.Clan;
import org.flomik.flomiksFactions.commands.clan.ClanManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class AllyCommandHandler {

    private final ClanManager clanManager;
    private final ConcurrentHashMap<String, List<String>> pendingAllies; // Хранит ожидающие альянсы

    public AllyCommandHandler(ClanManager clanManager, ConcurrentHashMap<String, List<String>> pendingAllies) {
        this.clanManager = clanManager;
        this.pendingAllies = pendingAllies;
    }

    public boolean handleCommand(Player player, String[] args) {
        if (args.length > 1) {
            String targetClanName = args[1].toLowerCase();
            Clan playerClan = clanManager.getPlayerClan(player.getName());
            Clan targetClan = clanManager.getClan(targetClanName);

            if (playerClan == null) {
                player.sendMessage(ChatColor.RED + "Вы не состоите в клане.");
                return true;
            }

            if (targetClan == null) {
                player.sendMessage(ChatColor.RED + "Клан с таким названием не существует.");
                return true;
            }

            if (playerClan.getName().equals(targetClanName)) {
                player.sendMessage(ChatColor.RED + "Вы не можете предложить союз своему же клану.");
                return true;
            }

            // Проверяем, что игрок является главой своего клана
            if (!playerClan.getOwner().equals(player.getName())) {
                player.sendMessage(ChatColor.RED + "Только глава клана может отправлять, принимать или отменять приглашения о союзе.");
                return true;
            }

            String playerClanName = playerClan.getName();

            // Проверяем, находятся ли кланы уже в альянсе
            if (playerClan.getAlliances().contains(targetClanName) || targetClan.getAlliances().contains(playerClanName)) {
                player.sendMessage(ChatColor.RED + "Эти кланы уже находятся в альянсе.");
                return true;
            }

            List<String> pendingInvitations = pendingAllies.getOrDefault(targetClanName, new ArrayList<>());

            if (pendingInvitations.contains(playerClanName)) {
                // Отмена предложения о союзе
                pendingInvitations.remove(playerClanName);
                if (pendingInvitations.isEmpty()) {
                    pendingAllies.remove(targetClanName);
                } else {
                    pendingAllies.put(targetClanName, pendingInvitations);
                }
                player.sendMessage(ChatColor.GREEN + "Предложение о союзе с кланом " + ChatColor.YELLOW + targetClanName + ChatColor.GREEN + " было отменено.");
                Player targetClanLeader = Bukkit.getPlayer(targetClan.getOwner());
                if (targetClanLeader != null) {
                    targetClanLeader.sendMessage(ChatColor.RED + "Предложение о союзе от клана " + ChatColor.YELLOW + playerClanName + ChatColor.RED + " было отменено.");
                }
            } else if (pendingAllies.containsKey(playerClanName) && pendingAllies.get(playerClanName).contains(targetClanName)) {
                // Принятие предложения о союзе
                List<String> senderClanInvitations = pendingAllies.get(playerClanName);
                senderClanInvitations.remove(targetClanName);
                if (senderClanInvitations.isEmpty()) {
                    pendingAllies.remove(playerClanName);
                } else {
                    pendingAllies.put(playerClanName, senderClanInvitations);
                }

                playerClan.addAlliances(targetClan);
                targetClan.addAlliances(playerClan);

                Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "Кланы " + ChatColor.YELLOW + playerClanName + ChatColor.GREEN + " и " + ChatColor.YELLOW + targetClanName + ChatColor.GREEN + " теперь в альянсе.");
            } else {
                // Отправка предложения о союзе
                pendingInvitations.add(playerClanName);
                pendingAllies.put(targetClanName, pendingInvitations);

                player.sendMessage(ChatColor.GREEN + "Предложение о союзе с кланом " + ChatColor.YELLOW + targetClanName + ChatColor.GREEN + " отправлено.");
                player.sendMessage(ChatColor.YELLOW + "Для отмены предложения о союзе повторите команду.");
                Player targetClanLeader = Bukkit.getPlayer(targetClan.getOwner());
                if (targetClanLeader != null) {
                    targetClanLeader.sendMessage(ChatColor.GREEN + "Клан " + ChatColor.YELLOW + playerClanName + ChatColor.GREEN + " предложил вам союз. Используйте " + ChatColor.YELLOW + "/clan ally " + playerClanName + ChatColor.GREEN + " для принятия предложения.");
                }

                // Запускаем задачу для автоматического удаления предложения через 5 минут
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        List<String> currentInvitations = pendingAllies.get(targetClanName);
                        if (currentInvitations != null && currentInvitations.contains(playerClanName)) {
                            currentInvitations.remove(playerClanName);
                            if (currentInvitations.isEmpty()) {
                                pendingAllies.remove(targetClanName);
                            } else {
                                pendingAllies.put(targetClanName, currentInvitations);
                            }
                            player.sendMessage(ChatColor.RED + "Предложение о союзе с кланом " + ChatColor.YELLOW + targetClanName + ChatColor.RED + " истекло.");
                            Player targetClanLeader = Bukkit.getPlayer(targetClan.getOwner());
                            if (targetClanLeader != null) {
                                targetClanLeader.sendMessage(ChatColor.RED + "Предложение о союзе от клана " + ChatColor.YELLOW + playerClanName + ChatColor.RED + " истекло.");
                            }
                        }
                    }
                }.runTaskLater(Bukkit.getPluginManager().getPlugin("FlomiksFactions"), 20 * 60 * 5); // 5 минут * 60 секунд * 20 тиков
            }
        } else {
            player.sendMessage(ChatColor.YELLOW + "Использование: " + ChatColor.GOLD + "/clan ally <название клана>");
        }
        return true;
    }
}
