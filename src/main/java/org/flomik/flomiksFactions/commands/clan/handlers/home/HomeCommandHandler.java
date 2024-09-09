package org.flomik.flomiksFactions.commands.clan.handlers.home;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.flomik.flomiksFactions.FlomiksFactions;
import org.flomik.flomiksFactions.commands.clan.Clan;
import org.flomik.flomiksFactions.commands.clan.ClanManager;
import org.bukkit.plugin.java.JavaPlugin;

public class HomeCommandHandler {
    private final ClanManager clanManager;
    private final FlomiksFactions plugin;  // Для доступа к планировщику задач

    public HomeCommandHandler(ClanManager clanManager, FlomiksFactions plugin) {
        this.clanManager = clanManager;
        this.plugin = plugin;
    }

    public boolean handleCommand(Player player) {
        Clan clan = clanManager.getPlayerClan(player.getName());
        if (clan == null) {
            player.sendMessage(ChatColor.RED + "Вы не состоите в клане.");
            return false;
        }
        if (!clan.hasHome()) {
            player.sendMessage(ChatColor.RED + "Точка дома клана не установлена.");
            return false;
        }

        // Отправляем сообщение о начале отсчёта
        player.sendMessage(ChatColor.YELLOW + "Телепортация начнется через 6 секунд...");

        // Создаем новую задачу для отсчёта времени и телепортации
        new BukkitRunnable() {
            int countdown = 6; // Начальный отсчёт - 6 секунд

            @Override
            public void run() {
                if (countdown > 0) {
                    if (countdown == 3 || countdown == 2 || countdown == 1) {
                        // Сообщаем игроку о времени до телепортации
                        player.sendMessage(ChatColor.YELLOW + "Телепортация через " + countdown + " секунд...");
                    }
                    countdown--;
                } else {
                    // Телепортируем игрока по истечению времени
                    player.teleport(clan.getHome());
                    player.sendMessage(ChatColor.GREEN + "Вы телепортированы к точке дома клана.");
                    cancel(); // Останавливаем задачу
                }
            }
        }.runTaskTimer(plugin, 0, 20); // Запуск задачи, каждые 20 тиков (1 секунда)

        return true;
    }
}
