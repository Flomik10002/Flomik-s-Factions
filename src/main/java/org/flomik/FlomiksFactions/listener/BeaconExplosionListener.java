package org.flomik.FlomiksFactions.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.util.Vector;
import org.flomik.FlomiksFactions.clan.Beacon;
import org.flomik.FlomiksFactions.clan.managers.BeaconManager;
import org.flomik.FlomiksFactions.database.BeaconDao;

public class BeaconExplosionListener implements Listener {
    private final BeaconManager beaconManager;
    private final BeaconDao beaconDao;
    private final int tntDamageRadius;

    public BeaconExplosionListener(BeaconManager beaconManager, BeaconDao beaconDao, int tntRadius) {
        this.beaconManager = beaconManager;
        this.beaconDao = beaconDao;
        this.tntDamageRadius = tntRadius;
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (!(event.getEntity() instanceof TNTPrimed)) {
            return;
        }

        Location explosionLoc = event.getLocation();
        for (Beacon beacon : beaconManager.getAllBeacons()) {
            if (!beacon.getLocation().getWorld().equals(explosionLoc.getWorld())) continue;
            double distance = beacon.getLocation().distance(explosionLoc);
            if (distance <= tntDamageRadius) {
                int newHp = beacon.getHealth() - 1;
                beacon.setHealth(newHp);
                beaconDao.updateBeaconHp(beacon.getRegionId(), newHp);

                if (newHp <= 0) {
                    Bukkit.broadcastMessage(ChatColor.RED + "Beacon of clan "
                            + ChatColor.YELLOW + beacon.getClanName()
                            + ChatColor.RED + " is now at 0 HP and can be captured!");
                } else {
                    Bukkit.broadcastMessage(ChatColor.YELLOW + "Beacon of clan "
                            + beacon.getClanName() + " took TNT damage. HP=" + newHp);
                }
            }
        }
    }
}
