package org.flomik.FlomiksFactions.commands.handlers.home;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.flomik.FlomiksFactions.FlomiksFactions;
import org.flomik.FlomiksFactions.clan.Clan;
import org.flomik.FlomiksFactions.clan.managers.ClanManager;

public class HomeHandler {
    private final ClanManager clanManager;
    private final FlomiksFactions plugin;

    public HomeHandler(ClanManager clanManager, FlomiksFactions plugin) {
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


        player.sendMessage(ChatColor.YELLOW + "Телепортация начнется через 6 секунд...");


        new BukkitRunnable() {
            int countdown = 6;

            @Override
            public void run() {
                if (countdown > 0) {
                    if (countdown == 3 || countdown == 2 || countdown == 1) {

                        player.sendMessage(ChatColor.YELLOW + "Телепортация через " + countdown + " секунд...");
                    }
                    countdown--;
                } else {

                    player.teleport(clan.getHome());
                    player.sendMessage(ChatColor.GREEN + "Вы телепортированы к точке дома клана.");
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 20);

        return true;
    }
}
