package org.flomik.flomiksFactions.listener;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.flomik.flomiksFactions.clan.Clan;
import org.flomik.flomiksFactions.clan.ClanManager;

public class ClanPvPListener implements Listener {

    private final ClanManager clanManager;

    public ClanPvPListener(JavaPlugin plugin, ClanManager clanManager) {
        this.clanManager = clanManager;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent event) {

        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player attacker = (Player) event.getDamager();
            Player defender = (Player) event.getEntity();


            Clan attackerClan = clanManager.getPlayerClan(attacker.getName());
            Clan defenderClan = clanManager.getPlayerClan(defender.getName());

            if (attackerClan != null && defenderClan != null) {
                if (attackerClan.equals(defenderClan)) {
                    event.setCancelled(true);
                    attacker.sendMessage(ChatColor.RED + "Вы состоите в одном клане с целью!");
                }

                if (attackerClan.getAlliances().contains(defenderClan.getName()) ||
                        defenderClan.getAlliances().contains(attackerClan.getName())) {
                    event.setCancelled(true);
                    attacker.sendMessage(ChatColor.RED + "Вы состоите в альянсе с целью!");
                }
            }
        }
    }
}
