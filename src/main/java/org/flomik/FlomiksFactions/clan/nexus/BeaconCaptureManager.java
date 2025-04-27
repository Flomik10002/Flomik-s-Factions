package org.flomik.FlomiksFactions.clan.nexus;

import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.flomik.FlomiksFactions.FlomiksFactions;
import org.flomik.FlomiksFactions.clan.Clan;
import org.flomik.FlomiksFactions.clan.managers.ClanManager;
import org.flomik.FlomiksFactions.clan.commands.handlers.clanInteractions.UnclaimRegionHandler;
import org.flomik.FlomiksFactions.database.BeaconDao;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class BeaconCaptureManager {
    private final FlomiksFactions plugin;
    private final BeaconManager beaconManager;
    private final ClanManager clanManager;
    private final BeaconDao beaconDao;
    private final int captureTime = NexusConfigManager.getInt("capture-time");
    private final BarColor barColor = NexusConfigManager.getColor("bossbar-color");
    private final Map<String, CaptureSession> activeCaptures = new ConcurrentHashMap<>();
    private final Map<String, BukkitTask> activeTasks = new ConcurrentHashMap<>();

    public BeaconCaptureManager(FlomiksFactions plugin, BeaconManager beaconManager,
                                ClanManager clanManager, BeaconDao beaconDao) {
        this.plugin = plugin;
        this.beaconManager = beaconManager;
        this.clanManager = clanManager;
        this.beaconDao = beaconDao;
    }

    public void checkForAutoCaptures() {
        beaconManager.getAllBeacons().forEach(beacon -> {
            if (beacon.getHealth() > 0 || activeCaptures.containsKey(beacon.getRegionId())) return;

            Optional.ofNullable(clanManager.getClan(beacon.getClanName())).ifPresent(defendingClan -> {
                Optional.ofNullable(findAttackingClanInChunk(beacon.getLocation().getChunk(), defendingClan))
                        .ifPresent(attackingClan -> startCapture(beacon, defendingClan, attackingClan));
            });
        });
    }

    private Clan findAttackingClanInChunk(Chunk chunk, Clan defendingClan) {
        return chunk.getWorld().getPlayers().stream()
                .filter(player -> player.getLocation().getChunk().equals(chunk))
                .map(player -> clanManager.getPlayerClan(player.getName()))
                .filter(clan -> clan != null && !clan.equals(defendingClan))
                .findFirst().orElse(null);
    }

    private void startCapture(Beacon beacon, Clan defendingClan, Clan attackingClan) {
        BossBar bar = Bukkit.createBossBar("Захват маяка клана " + defendingClan.getName(), barColor, BarStyle.SOLID);
        defendingClan.getMembers().forEach(name -> {
            Player p = Bukkit.getPlayerExact(name);
            if (p != null) bar.addPlayer(p);
        });
        attackingClan.getMembers().forEach(name -> {
            Player p = Bukkit.getPlayerExact(name);
            if (p != null) bar.addPlayer(p);
        });

        CaptureSession session = new CaptureSession(beacon.getRegionId(), bar, defendingClan, attackingClan);
        activeCaptures.put(beacon.getRegionId(), session);

        Bukkit.broadcastMessage(ChatColor.YELLOW + "Лидер или заместитель клана " + attackingClan.getName()
                + " начал захват маяка клана " + defendingClan.getName() + ". Удерживайте позицию!");

        BukkitTask task = new BukkitRunnable() {
            int captureProgress = 0;

            @Override
            public void run() {
                if (!activeCaptures.containsKey(beacon.getRegionId())) {
                    cancel();
                    return;
                }

                if (attackerStillInChunk(beacon, attackingClan)) {
                    captureProgress++;
                } else {
                    captureProgress = 0;
                }

                double progress = Math.min(1.0, (double) captureProgress / captureTime);
                bar.setProgress(progress);

                if (captureProgress >= captureTime) {
                    finalizeCapture(beacon, defendingClan, attackingClan);
                    stopCapture(beacon);
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 20L, 20L); // every second

        activeTasks.put(beacon.getRegionId(), task);
    }

    private boolean attackerStillInChunk(Beacon beacon, Clan attackingClan) {
        return beacon.getLocation().getChunk().getWorld().getPlayers().stream()
                .filter(player -> player.getLocation().getChunk().equals(beacon.getLocation().getChunk()))
                .map(player -> clanManager.getPlayerClan(player.getName()))
                .anyMatch(attackingClan::equals);
    }

    public void manualStartCapture(Beacon beacon, Clan defendingClan, Clan attackingClan) {
        if (activeCaptures.containsKey(beacon.getRegionId())) return;
        if (beacon.getHealth() > 0) return;
        startCapture(beacon, defendingClan, attackingClan);
        Player starter = Bukkit.getPlayerExact(attackingClan.getOwner());
        if (starter != null) {
            starter.sendMessage(ChatColor.GREEN + "Вы начали захват маяка клана \"" + defendingClan.getName()
                    + "\". Не отходите от маяка в течение 3 минут, иначе прогресс сбросится.");
        }
    }

    private void finalizeCapture(Beacon beacon, Clan defendingClan, Clan attackingClan) {
        World world = beacon.getLocation().getWorld();
        String regionId = beacon.getRegionId();
        Chunk chunk = beacon.getLocation().getChunk();

        clanManager.removeRegionById(world, regionId);
        beaconManager.removeBeacon(regionId);
        beaconDao.deleteBeaconByRegionId(regionId);

        defendingClan.removeClaimedChunk(getChunkId(chunk));
        attackingClan.addClaimedChunk(getChunkId(chunk));

        String newRegionId = "clan_" + attackingClan.getName() + "_" + world.getName() + "_" + chunk.getX() + "_" + chunk.getZ();
        Beacon newBeacon = new Beacon(attackingClan.getName(), beacon.getLocation(), 5, newRegionId);
        beaconManager.addBeacon(newBeacon);
        beaconDao.insertBeacon(attackingClan, beacon.getLocation(), newRegionId, 5);

        broadcastCaptureResult(defendingClan, attackingClan);
    }

    private void stopCapture(Beacon beacon) {
        activeCaptures.remove(beacon.getRegionId());
        Optional.ofNullable(activeTasks.remove(beacon.getRegionId())).ifPresent(BukkitTask::cancel);
    }

    private void broadcastCaptureResult(Clan defendingClan, Clan attackingClan) {
        defendingClan.getMembers().stream()
                .map(Bukkit::getPlayerExact)
                .filter(player -> player != null)
                .forEach(player -> player.sendMessage(ChatColor.RED + "Your beacon was captured by " + attackingClan.getName() + "!"));

        attackingClan.getMembers().stream()
                .map(Bukkit::getPlayerExact)
                .filter(player -> player != null)
                .forEach(player -> player.sendMessage(ChatColor.GREEN + "You have captured the territory from " + defendingClan.getName() + "!"));
    }

    private String getChunkId(Chunk chunk) {
        return chunk.getWorld().getName() + "_" + chunk.getX() + "_" + chunk.getZ();
    }
}
