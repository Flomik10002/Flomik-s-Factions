package org.flomik.FlomiksFactions.clan.managers;

import org.bukkit.*;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.flomik.FlomiksFactions.FlomiksFactions;
import org.flomik.FlomiksFactions.clan.Beacon;
import org.flomik.FlomiksFactions.clan.CaptureSession;
import org.flomik.FlomiksFactions.clan.Clan;
import org.flomik.FlomiksFactions.commands.handlers.clanInteractions.UnclaimRegionHandler;
import org.flomik.FlomiksFactions.database.BeaconDao;
import java.util.HashMap;
import java.util.Map;

public class BeaconCaptureManager {

    private final FlomiksFactions plugin;
    private final BeaconManager beaconManager;
    private final ClanManager clanManager;
    private final BeaconDao beaconDao;
    private final UnclaimRegionHandler unclaimHandler;
    int captureTime = NexusConfigManager.getInt("capture-time");
    BarColor barColor = NexusConfigManager.getColor("bossbar-color");

    private final Map<String, CaptureSession> activeCaptures = new HashMap<>();

    public BeaconCaptureManager(
            FlomiksFactions plugin,
            BeaconManager beaconManager,
            ClanManager clanManager,
            BeaconDao beaconDao,
            UnclaimRegionHandler unclaimHandler) {
        this.plugin = plugin;
        this.beaconManager = beaconManager;
        this.clanManager = clanManager;
        this.beaconDao = beaconDao;
        this.unclaimHandler = unclaimHandler;
    }

    // Called periodically
    public void checkForAutoCaptures() {
        for (Beacon beacon : beaconManager.getAllBeacons()) {
            // skip if not vulnerable
            if (beacon.getHealth() > 0) continue;
            if (activeCaptures.containsKey(beacon.getRegionId())) continue;

            Clan defendingClan = clanManager.getClan(beacon.getClanName());
            if (defendingClan == null) continue;

            Chunk chunk = beacon.getLocation().getChunk();
            Clan attackingClan = findAttackingClanInChunk(chunk, defendingClan);
            if (attackingClan != null) {
                startCapture(beacon, defendingClan, attackingClan);
            }
        }
    }

    private Clan findAttackingClanInChunk(Chunk chunk, Clan defendingClan) {
        for (org.bukkit.entity.Player p : chunk.getWorld().getPlayers()) {
            if (p.getLocation().getChunk().equals(chunk)) {
                Clan c = clanManager.getPlayerClan(p.getName());
                if (c != null && !c.equals(defendingClan)) {
                    return c;
                }
            }
        }
        return null;
    }

    private void startCapture(Beacon beacon, Clan defendingClan, Clan attackingClan) {
        // Create a BossBar
        BossBar bar = Bukkit.createBossBar(
                "Capturing " + beacon.getRegionId(),
                barColor,
                BarStyle.SOLID
        );

        for (String mem : defendingClan.getMembers()) {
            Player p = Bukkit.getPlayerExact(mem);
            if (p != null) bar.addPlayer(p);
        }
        for (String mem : attackingClan.getMembers()) {
            Player p = Bukkit.getPlayerExact(mem);
            if (p != null) bar.addPlayer(p);
        }

        // Create session
        CaptureSession session = new CaptureSession(beacon.getRegionId(), bar, defendingClan, attackingClan);
        activeCaptures.put(beacon.getRegionId(), session);

        // 3-minute countdown
        new BukkitRunnable() {
            int elapsed = 0;
            final int totalTicks = captureTime; // e.g. 180 for 3 min

            @Override
            public void run() {
                // If canceled externally
                if (!activeCaptures.containsKey(beacon.getRegionId())) {
                    bar.removeAll();
                    cancel();
                    return;
                }

                boolean attackerStillHere = attackerStillInChunk(beacon, attackingClan);
                if (!attackerStillHere) {
                    // Attack aborted
                    activeCaptures.remove(beacon.getRegionId());
                    bar.removeAll();
                    cancel();
                    return;
                }

                // Update bossbar
                double progress = (double) elapsed / (double) totalTicks;
                bar.setProgress(Math.min(1.0, progress));

                if (elapsed >= totalTicks) {
                    // Capture complete
                    finalizeCapture(beacon, defendingClan, attackingClan);
                    activeCaptures.remove(beacon.getRegionId());
                    bar.removeAll();
                    cancel();
                }
                elapsed++;
            }
        }.runTaskTimer(plugin, 20L, 20L); // 1-second intervals
    }

    private boolean attackerStillInChunk(Beacon beacon, Clan attackingClan) {
        Chunk chunk = beacon.getLocation().getChunk();
        for (Player p : chunk.getWorld().getPlayers()) {
            if (p.getLocation().getChunk().equals(chunk)) {
                Clan c = clanManager.getPlayerClan(p.getName());
                if (c != null && c.equals(attackingClan)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void finalizeCapture(Beacon beacon, Clan defendingClan, Clan attackingClan) {
        World w = beacon.getLocation().getWorld();
        String regionId = beacon.getRegionId();
        Chunk chunk = beacon.getLocation().getChunk();

        // 1) Remove old region + beacon
        unclaimHandler.removeRegionById(w, regionId);
        beaconManager.removeBeacon(regionId);
        beaconDao.deleteBeaconByRegionId(regionId);

        defendingClan.removeClaimedChunk(getChunkId(chunk));

        // 2) Claim chunk for attacker (optionally create a brand-new region or reuse same region name)
        attackingClan.addClaimedChunk(getChunkId(chunk));

        // Example: create a new regionId
        String newRegionId = "clan_" + attackingClan.getName() + "_" + w.getName() + "_" + chunk.getX() + "_" + chunk.getZ();
        // Then do your logic to add a new WG region with "addWorldGuardRegion(chunk, attackingClan.getName(), ...)"
        // or replicate your addWorldGuardRegion code.

        // 3) Optionally create a new Beacon for the new region
        Beacon newBeacon = new Beacon(attackingClan.getName(), beacon.getLocation(), 5, newRegionId);
        beaconManager.addBeacon(newBeacon);
        beaconDao.insertBeacon(attackingClan, beacon.getLocation(), newRegionId, 5);

        // 4) Announce success
        broadcastCaptureResult(defendingClan, attackingClan);
    }

    private String getChunkId(Chunk chunk) {
        return chunk.getWorld().getName() + "_" + chunk.getX() + "_" + chunk.getZ();
    }

    private void broadcastCaptureResult(Clan defendingClan, Clan attackingClan) {
        for (String mem : defendingClan.getMembers()) {
            Player p = Bukkit.getPlayerExact(mem);
            if (p != null) {
                p.sendMessage(ChatColor.RED + "Your beacon was captured by " + attackingClan.getName() + "!");
            }
        }
        for (String mem : attackingClan.getMembers()) {
            Player p = Bukkit.getPlayerExact(mem);
            if (p != null) {
                p.sendMessage(ChatColor.GREEN + "You have captured the territory from " + defendingClan.getName() + "!");
            }
        }
    }
}
