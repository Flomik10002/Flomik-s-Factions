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
    private final ConcurrentHashMap<String, List<String>> pendingAllies;

    public AllyCommandHandler(ClanManager clanManager, ConcurrentHashMap<String, List<String>> pendingAllies) {
        this.clanManager = clanManager;
        this.pendingAllies = pendingAllies;
    }

    public boolean handleCommand(Player player, String[] args) {
        if (args.length > 1) {
            String allyClanName = args[1].toLowerCase();
            Clan playerClan = clanManager.getPlayerClan(player.getName());
            Clan allyClan = clanManager.getClan(allyClanName);

            if (playerClan == null) {
                player.sendMessage(ChatColor.RED + "Вы не состоите в клане.");
                return true;
            }

            // Проверка, что игрок является Лидером или Заместителем клана
            String playerRole = playerClan.getRole(player.getName());
            if (!playerRole.equals("Лидер") && !playerRole.equals("Заместитель")) {
                player.sendMessage(ChatColor.RED + "Только Лидер или Заместитель клана могут предлагать, отменять или расторгать союзы.");
                return true;
            }

            if (allyClan == null) {
                player.sendMessage(ChatColor.RED + "Клан с таким названием не существует.");
                return true;
            }

            if (playerClan.getName().equals(allyClan.getName())) {
                player.sendMessage(ChatColor.RED + "Вы не можете предложить союз своему же клану.");
                return true;
            }

            // Проверяем, являются ли кланы уже союзниками
            if (playerClan.getAlliances().contains(allyClan.getName())) {
                // Расторгаем альянс
                playerClan.removeAlliance(allyClan);
                allyClan.removeAlliance(playerClan);
                clanManager.updateClan(playerClan);
                clanManager.updateClan(allyClan);

                player.sendMessage(ChatColor.GREEN + "Вы успешно расторгли союз с кланом " + ChatColor.YELLOW + allyClan.getName() + ChatColor.GREEN + ".");
                sendMessageToRole(allyClan, "Клан " + ChatColor.YELLOW + playerClan.getName() + ChatColor.RED + " расторг союз с вашим кланом.");
                return true;
            }

            String playerClanName = playerClan.getName();
            List<String> allies = pendingAllies.getOrDefault(playerClanName, new ArrayList<>());

            if (allies.contains(allyClanName)) {
                // Отзываем предложение о союзе
                allies.remove(allyClanName);
                pendingAllies.put(playerClanName, allies); // Обновляем список предложений
                player.sendMessage(ChatColor.GREEN + "Предложение о союзе с кланом " + ChatColor.YELLOW + allyClanName + ChatColor.GREEN + " было отменено.");
                sendMessageToRole(allyClan, "Предложение о союзе с кланом " + ChatColor.YELLOW + playerClanName + ChatColor.RED + " было отменено.");
            } else {
                // Проверяем, нет ли уже отправленного предложения от другого клана
                if (pendingAllies.getOrDefault(allyClan.getName(), new ArrayList<>()).contains(playerClanName)) {
                    // Принятие предложения о союзе
                    playerClan.addAlliances(allyClan);
                    allyClan.addAlliances(playerClan);
                    clanManager.updateClan(playerClan);
                    clanManager.updateClan(allyClan);

                    // Удаляем предложение после принятия
                    pendingAllies.get(allyClan.getName()).remove(playerClanName);
                    player.sendMessage(ChatColor.GREEN + "Вы приняли предложение о союзе с кланом " + ChatColor.YELLOW + allyClan.getName() + ChatColor.GREEN + ".");
                    sendMessageToRole(allyClan, "Клан " + ChatColor.YELLOW + playerClan.getName() + ChatColor.GREEN + " принял ваше предложение о союзе.");
                } else {
                    // Отправляем предложение о союзе
                    allies.add(allyClanName);
                    pendingAllies.put(playerClanName, allies); // Сохраняем обновлённый список предложений
                    player.sendMessage(ChatColor.GREEN + "Предложение о союзе с кланом " + ChatColor.YELLOW + allyClanName + ChatColor.GREEN + " отправлено.");
                    player.sendMessage(ChatColor.YELLOW + "Для отмены предложения о союзе повторите команду.");
                    sendMessageToRole(allyClan,ChatColor.GREEN + "Клан " + ChatColor.YELLOW + playerClan.getName() + ChatColor.GREEN + " предложил вам союз. Используйте " + ChatColor.YELLOW + "/clan ally " + playerClan.getName() + ChatColor.GREEN + " для принятия предложения.");

                    // Запускаем задачу для автоматического удаления предложения через 5 минут
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            List<String> currentAllies = pendingAllies.get(playerClanName);
                            if (currentAllies != null && currentAllies.contains(allyClanName)) {
                                currentAllies.remove(allyClanName);
                                if (currentAllies.isEmpty()) {
                                    pendingAllies.remove(playerClanName);
                                } else {
                                    pendingAllies.put(playerClanName, currentAllies);
                                }
                                player.sendMessage(ChatColor.RED + "Предложение о союзе с кланом " + ChatColor.YELLOW + allyClanName + ChatColor.RED + " истекло.");
                                sendMessageToRole(allyClan, "Предложение о союзе от клана " + ChatColor.GOLD + playerClanName + ChatColor.RED + " истекло.");
                            }
                        }
                    }.runTaskLater(Bukkit.getPluginManager().getPlugin("FlomiksFactions"), 20 * 60); // 5 минут * 60 секунд * 20 тиков
                }
            }
        } else {
            player.sendMessage(ChatColor.YELLOW + "Использование: " + ChatColor.GOLD + "/clan ally <название клана>");
        }
        return true;
    }

    private void sendMessageToRole(Clan clan, String message) {
        try {
            // Отправляем сообщение только игрокам с ролями Лидер и Заместитель
            List<String> rolesToNotify = List.of("Лидер", "Заместитель");
            for (String role : rolesToNotify) {
                List<String> playersWithRole = clan.getPlayersWithRole(role);
                for (String playerName : playersWithRole) {
                    Player player = Bukkit.getPlayer(playerName);
                    if (player != null) { // Проверяем, что игрок онлайн
                        player.sendMessage(message);
                    }
                }
            }
        } finally {
        }
    }
}
