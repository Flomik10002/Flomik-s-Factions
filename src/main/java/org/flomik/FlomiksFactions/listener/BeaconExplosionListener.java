package org.flomik.FlomiksFactions.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.flomik.FlomiksFactions.clan.nexus.BeaconManager;
import org.flomik.FlomiksFactions.database.BeaconDao;

import java.util.logging.Logger;

public class BeaconExplosionListener implements Listener {
    private final BeaconManager beaconManager;
    private final BeaconDao beaconDao;
    private final int tntDamageRadius;
    private final Logger logger = Bukkit.getLogger();

    public BeaconExplosionListener(BeaconManager beaconManager, BeaconDao beaconDao, int tntRadius) {
        this.beaconManager = beaconManager;
        this.beaconDao = beaconDao;
        this.tntDamageRadius = tntRadius;
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (!(event.getEntity() instanceof TNTPrimed)) return;

        Location explosionLoc = event.getLocation();
        beaconManager.getAllBeacons().stream()
                .filter(beacon -> beacon.getLocation().getWorld().equals(explosionLoc.getWorld()) &&
                        beacon.getLocation().distance(explosionLoc) <= tntDamageRadius)
                .forEach(beacon -> {
                    // Наносим урон маяку
                    int newHp = beacon.getHealth() - 1;
                    beacon.setHealth(newHp);
                    beaconDao.updateBeaconHp(beacon.getRegionId(), newHp);

                    // Сообщаем о состоянии маяка
                    if (newHp <= 0) {
                        Bukkit.broadcastMessage(ChatColor.RED + "Beacon of clan "
                                + ChatColor.YELLOW + beacon.getClanName()
                                + ChatColor.RED + " is now at 0 HP and can be captured!");
                    } else {
                        Bukkit.broadcastMessage(ChatColor.YELLOW + "Beacon of clan "
                                + beacon.getClanName() + " took TNT damage. HP=" + newHp);
                    }
                });

        // Убираем маяк из разрушаемых блоков
        event.blockList().removeIf(this::isBeaconBlock);
    }

    /**
     * Проверяет, является ли блок маяком.
     */
    private boolean isBeaconBlock(Block block) {
        return beaconManager.getAllBeacons().stream()
                .anyMatch(beacon -> beacon.getLocation().equals(block.getLocation()));
    }
}
