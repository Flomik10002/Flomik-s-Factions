package org.flomik.FlomiksFactions.worldEvents.shrine;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.flomik.FlomiksFactions.FlomiksFactions;
import org.flomik.FlomiksFactions.clan.Clan;
import org.flomik.FlomiksFactions.clan.ClanManager;

import java.util.HashSet;
import java.util.Set;

public class ShrineCaptureManager {

    private final ShrineEvent shrineEvent;
    private final FlomiksFactions plugin;
    private final ClanManager clanManager;
    private BossBar captureBossBar;
    private int captureTime;
    private final Set<Player> playersInZone;
    private Clan capturingClan;

    public ShrineCaptureManager(ShrineEvent shrineEvent, FlomiksFactions plugin, ClanManager clanManager) {
        this.shrineEvent = shrineEvent;
        this.plugin = plugin;
        this.clanManager = clanManager;
        this.playersInZone = new HashSet<>();
        this.captureTime = 0;
        this.capturingClan = null;

        captureBossBar = Bukkit.createBossBar("§a[Ивент] §6Захват Святилища", BarColor.YELLOW, BarStyle.SOLID);
    }

    public void startCaptureMechanism() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Location shrineLocation = shrineEvent.getActiveShrineLocation();
                if (shrineLocation == null) {
                    this.cancel();
                    return;
                }


                checkPlayersOnShrine(shrineLocation);


                if (playersInZone.isEmpty()) {
                    captureBossBar.setVisible(false);
                    captureTime = 0;
                    capturingClan = null;
                } else {

                    if (capturingClan != null) {
                        captureBossBar.setVisible(true);
                        captureBossBar.setProgress(captureTime / 120.0);


                        if (captureTime >= 120) {
                            Bukkit.broadcastMessage(ChatColor.GREEN + "Клан " + ChatColor.YELLOW + capturingClan.getName() + ChatColor.GREEN + " захватил Святилище Опыта!");
                            onShrineCaptureSuccess();
                            shrineEvent.deactivateShrine();
                            captureBossBar.setVisible(false);
                            this.cancel();
                        } else {
                            captureTime++;
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }


    private void checkPlayersOnShrine(Location shrineLocation) {
        Set<Player> newPlayersInZone = new HashSet<>();
        Clan currentClan = null;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (isPlayerOnShrine(player, shrineLocation)) {
                Clan playerClan = clanManager.getPlayerClan(player.getName());

                if (playerClan == null) {
                    continue;
                }

                if (currentClan == null) {
                    currentClan = playerClan;
                } else if (!currentClan.getName().equals(playerClan.getName())) {

                    sendEnemyOnShrineMessage(currentClan);
                    sendEnemyOnShrineMessage(playerClan);
                    captureTime = 0;
                    captureBossBar.setVisible(false);
                    capturingClan = null;
                    return;
                }

                newPlayersInZone.add(player);
                captureBossBar.addPlayer(player);
            } else {
                captureBossBar.removePlayer(player);
            }
        }

        if (!newPlayersInZone.isEmpty()) {
            if (capturingClan == null) {
                capturingClan = currentClan;


                for (String member : capturingClan.getMembers()) {
                    Player clanMember = Bukkit.getPlayer(member);
                    if (clanMember != null && clanMember.isOnline()) {
                        captureBossBar.addPlayer(clanMember);
                    }
                }

                clanManager.sendClanMessage(capturingClan, ChatColor.GREEN + "Ваш клан начал захват Святилища Опыта. Не покидайте территорию в течение 2 минут.");
            }


            addNearbyPlayersToBossBar(shrineLocation);
        }

        playersInZone.clear();
        playersInZone.addAll(newPlayersInZone);
    }

    private void addNearbyPlayersToBossBar(Location shrineLocation) {
        double radius = 16.0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getWorld().equals(shrineLocation.getWorld()) &&
                    player.getLocation().distance(shrineLocation) <= radius) {
                captureBossBar.addPlayer(player);
            } else {
                captureBossBar.removePlayer(player);
            }
        }
    }


    private void sendEnemyOnShrineMessage(Clan clan) {
        for (String member : clan.getMembers()) {
            Player clanMember = Bukkit.getPlayer(member);
            if (clanMember != null && clanMember.isOnline()) {
                sendActionBar(clanMember, ChatColor.RED + "Враг зашел на точку захвата");
            }
        }
    }


    private boolean isPlayerOnShrine(Player player, Location shrineLocation) {
        Location playerLocation = player.getLocation();
        return playerLocation.getBlockX() >= shrineLocation.getBlockX() - 1
                && playerLocation.getBlockX() <= shrineLocation.getBlockX() + 1
                && playerLocation.getBlockZ() >= shrineLocation.getBlockZ() - 1
                && playerLocation.getBlockZ() <= shrineLocation.getBlockZ() + 1
                && playerLocation.getBlockY() == shrineLocation.getBlockY();
    }


    private void rewardPlayersInZone() {
        if (capturingClan != null) {
            BukkitRunnable rewardTask = new BukkitRunnable() {
                int cycles = 0;
                final int maxCycles = 40;

                @Override
                public void run() {
                    if (cycles >= maxCycles || capturingClan == null) {

                        this.cancel();
                        return;
                    }


                    for (String member : capturingClan.getMembers()) {
                        Player clanMember = Bukkit.getPlayer(member);
                        if (clanMember != null && clanMember.isOnline()) {

                            clanMember.giveExp(27);
                            clanMember.sendMessage(ChatColor.GREEN + "Вы получили 27 опыта за захват Святилища Опыта!");
                        }
                    }


                    cycles++;
                }
            };


            rewardTask.runTaskTimer(plugin, 0L, 3600L);
        }
    }


    private void onShrineCaptureSuccess() {
        if (capturingClan != null) {

            capturingClan.addClanXp(1);
            clanManager.saveClan(capturingClan);

            clanManager.sendClanMessage(capturingClan, ChatColor.GREEN + "Ваш клан захватил Святилище Опыта и получил 1 очко опыта!");


            rewardPlayersInZone();
        }
    }

    private void sendActionBar(Player player, String message) {
        new BukkitRunnable() {
            @Override
            public void run() {
                player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                        net.md_5.bungee.api.chat.TextComponent.fromLegacyText(message));
            }
        }.runTask(plugin);
    }
}
