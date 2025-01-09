package org.flomik.FlomiksFactions.worldEvents.shrine.managers;

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
import org.flomik.FlomiksFactions.clan.managers.ClanManager;
import org.flomik.FlomiksFactions.worldEvents.shrine.event.ShrineEvent;

import java.util.HashSet;
import java.util.Set;

public class ShrineEventManager {

    private final ShrineEvent shrineEvent;
    private final FlomiksFactions plugin;
    private final ClanManager clanManager;
    private BossBar captureBossBar;
    private int captureTime;
    private final Set<Player> playersInZone;
    private Clan capturingClan;

    public ShrineEventManager(ShrineEvent shrineEvent, FlomiksFactions plugin, ClanManager clanManager) {
        this.shrineEvent = shrineEvent;
        this.plugin = plugin;
        this.clanManager = clanManager;
        this.playersInZone = new HashSet<>();
        this.captureTime = 0;
        this.capturingClan = null;

        captureBossBar = Bukkit.createBossBar(
                ChatColor.translateAlternateColorCodes('&',
                        plugin.getShrineConfigManager().get().getString("bossbar.title")),
                BarColor.YELLOW,
                BarStyle.SOLID
        );
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
                        int captureDuration = plugin.getShrineConfigManager().get().getInt("settings.capture-duration-seconds", 120);
                        captureBossBar.setProgress(captureTime / (double) captureDuration);

                        if (captureTime >= captureDuration) {
                            String successMessage = plugin.getShrineConfigManager().get().getString("messages.capture-success");
                            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', successMessage.replace("{clan}", capturingClan.getName())));
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

                clanManager.sendClanMessage(capturingClan, ChatColor.translateAlternateColorCodes('&',
                        plugin.getShrineConfigManager().get().getString("messages.capture-start")));
            }

            addNearbyPlayersToBossBar(shrineLocation);
        }

        playersInZone.clear();
        playersInZone.addAll(newPlayersInZone);
    }

    private void addNearbyPlayersToBossBar(Location shrineLocation) {
        double radius = plugin.getShrineConfigManager().get().getDouble("settings.capture-radius", 16.0);
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
                sendActionBar(clanMember, ChatColor.translateAlternateColorCodes('&',
                        plugin.getShrineConfigManager().get().getString("messages.enemy-on-shrine")));
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
                int maxCycles = plugin.getShrineConfigManager().get().getInt("settings.reward-cycles", 40);

                @Override
                public void run() {
                    if (cycles >= maxCycles || capturingClan == null) {
                        this.cancel();
                        return;
                    }

                    for (String member : capturingClan.getMembers()) {
                        Player clanMember = Bukkit.getPlayer(member);
                        if (clanMember != null && clanMember.isOnline()) {

                            int rewardExp = plugin.getShrineConfigManager().get().getInt("settings.reward-exp", 27);
                            clanMember.giveExp(rewardExp);
                            clanMember.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    plugin.getShrineConfigManager().get().getString("messages.player-reward")
                                            .replace("{exp}", String.valueOf(rewardExp))));
                        }
                    }

                    cycles++;
                }
            };
            long rewardIntervalTicks = plugin.getShrineConfigManager().get().getInt("settings.reward-interval-ticks", 3600);
            rewardTask.runTaskTimer(plugin, 0L, rewardIntervalTicks);
        }
    }

    private void onShrineCaptureSuccess() {
        if (capturingClan != null) {

            int clanXpReward = plugin.getShrineConfigManager().get().getInt("settings.reward-clan-xp", 1);
            capturingClan.addClanXp(clanXpReward);
            clanManager.saveClan(capturingClan);

            String successMessage = plugin.getShrineConfigManager().get().getString("messages.capture-success-reward")
                    .replace("{clan_xp}", String.valueOf(clanXpReward));
            clanManager.sendClanMessage(capturingClan, ChatColor.translateAlternateColorCodes('&', successMessage));

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
