package org.flomik.FlomiksFactions.listener; //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material; //NOPMD - suppressed UnnecessaryImport - TODO explain reason for suppression //NOPMD - suppressed UnnecessaryImport - TODO explain reason for suppression //NOPMD - suppressed UnnecessaryImport - TODO explain reason for suppression
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.util.Vector; //NOPMD - suppressed UnnecessaryImport - TODO explain reason for suppression //NOPMD - suppressed UnnecessaryImport - TODO explain reason for suppression //NOPMD - suppressed UnnecessaryImport - TODO explain reason for suppression
import org.flomik.FlomiksFactions.clan.Beacon;
import org.flomik.FlomiksFactions.clan.managers.BeaconManager;
import org.flomik.FlomiksFactions.database.BeaconDao;

public class BeaconExplosionListener implements Listener { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private final BeaconManager beaconManager; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private final BeaconDao beaconDao; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private final int tntDamageRadius; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression

    public BeaconExplosionListener(BeaconManager beaconManager, BeaconDao beaconDao, int tntRadius) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        this.beaconManager = beaconManager;
        this.beaconDao = beaconDao;
        this.tntDamageRadius = tntRadius;
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        if (!(event.getEntity() instanceof TNTPrimed)) {
            return;
        }

        Location explosionLoc = event.getLocation(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        for (Beacon beacon : beaconManager.getAllBeacons()) { //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            if (!beacon.getLocation().getWorld().equals(explosionLoc.getWorld())) continue; //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
            double distance = beacon.getLocation().distance(explosionLoc); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
            if (distance <= tntDamageRadius) {
                int newHp = beacon.getHealth() - 1; //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
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
