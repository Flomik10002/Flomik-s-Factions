package org.flomik.flomiksFactions.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.flomik.flomiksFactions.commands.clan.Clan;
import org.flomik.flomiksFactions.commands.clan.ClanManager;

import java.util.List;

public class ClanPvPListener implements Listener {

    private final ClanManager clanManager;

    public ClanPvPListener(JavaPlugin plugin, ClanManager clanManager) {
        this.clanManager = clanManager;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        // Проверяем, что оба существа являются игроками
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player attacker = (Player) event.getDamager();
            Player defender = (Player) event.getEntity();

            // Получаем кланы игроков
            Clan attackerClan = clanManager.getPlayerClan(attacker.getName());
            Clan defenderClan = clanManager.getPlayerClan(defender.getName());

            // Проверяем, находятся ли оба игрока в одном клане
            if (attackerClan != null && defenderClan != null && attackerClan.equals(defenderClan)) {
                event.setCancelled(true); // Отменяем событие драки
            }
            for (Clan clan : clanManager.getClans().values()) {
                List<String> alliances = clan.getAlliances();
                for (String allyName : alliances) {
                    Clan allyClan = clanManager.getClans().get(allyName);
                    if (allyClan != null) {
                        event.setCancelled(true); // Отменяем событие драки
                    }
                }
            }
        }
    }
}
