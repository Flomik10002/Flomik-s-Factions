package org.flomik.FlomiksFactions.worldEvents.shrine.managers; //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression

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

public class ShrineEventManager { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression

    private final ShrineEvent shrineEvent; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private final FlomiksFactions plugin; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private final ClanManager clanManager; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private BossBar captureBossBar; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private int captureTime; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private final Set<Player> playersInZone; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private Clan capturingClan; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression

    public ShrineEventManager(ShrineEvent shrineEvent, FlomiksFactions plugin, ClanManager clanManager) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        this.shrineEvent = shrineEvent;
        this.plugin = plugin;
        this.clanManager = clanManager;
        this.playersInZone = new HashSet<>();
        this.captureTime = 0;
        this.capturingClan = null; //NOPMD - suppressed NullAssignment - TODO explain reason for suppression //NOPMD - suppressed NullAssignment - TODO explain reason for suppression //NOPMD - suppressed NullAssignment - TODO explain reason for suppression

        captureBossBar = Bukkit.createBossBar(
                ChatColor.translateAlternateColorCodes('&',
                        plugin.getShrineConfigManager().get().getString("bossbar.title")), //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
                BarColor.YELLOW,
                BarStyle.SOLID
        );
    }

    public void startCaptureMechanism() { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        new BukkitRunnable() {
            @Override
            public void run() {
                Location shrineLocation = shrineEvent.getActiveShrineLocation(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                if (shrineLocation == null) {
                    this.cancel();
                    return;
                }

                checkPlayersOnShrine(shrineLocation);

                if (playersInZone.isEmpty()) {
                    captureBossBar.setVisible(false);
                    captureTime = 0;
                    capturingClan = null; //NOPMD - suppressed NullAssignment - TODO explain reason for suppression //NOPMD - suppressed NullAssignment - TODO explain reason for suppression //NOPMD - suppressed NullAssignment - TODO explain reason for suppression
                } else {

                    if (capturingClan != null) {
                        captureBossBar.setVisible(true);
                        int captureDuration = plugin.getShrineConfigManager().get().getInt("settings.capture-duration-seconds", 120); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
                        captureBossBar.setProgress(captureTime / (double) captureDuration);

                        if (captureTime >= captureDuration) {
                            String successMessage = plugin.getShrineConfigManager().get().getString("messages.capture-success"); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
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

    private void checkPlayersOnShrine(Location shrineLocation) { //NOPMD - suppressed CyclomaticComplexity - TODO explain reason for suppression //NOPMD - suppressed CyclomaticComplexity - TODO explain reason for suppression //NOPMD - suppressed CyclomaticComplexity - TODO explain reason for suppression
        Set<Player> newPlayersInZone = new HashSet<>(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        Clan currentClan = null;

        for (Player player : Bukkit.getOnlinePlayers()) { //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            if (isPlayerOnShrine(player, shrineLocation)) {
                Clan playerClan = clanManager.getPlayerClan(player.getName()); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression

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
                    capturingClan = null; //NOPMD - suppressed NullAssignment - TODO explain reason for suppression //NOPMD - suppressed NullAssignment - TODO explain reason for suppression //NOPMD - suppressed NullAssignment - TODO explain reason for suppression
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

                for (String member : capturingClan.getMembers()) { //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                    Player clanMember = Bukkit.getPlayer(member); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                    if (clanMember != null && clanMember.isOnline()) {
                        captureBossBar.addPlayer(clanMember);
                    }
                }

                clanManager.sendClanMessage(capturingClan, ChatColor.translateAlternateColorCodes('&',
                        plugin.getShrineConfigManager().get().getString("messages.capture-start"))); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
            }

            addNearbyPlayersToBossBar(shrineLocation);
        }

        playersInZone.clear();
        playersInZone.addAll(newPlayersInZone);
    }

    private void addNearbyPlayersToBossBar(Location shrineLocation) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        double radius = plugin.getShrineConfigManager().get().getDouble("settings.capture-radius", 16.0); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
        for (Player player : Bukkit.getOnlinePlayers()) { //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            if (player.getWorld().equals(shrineLocation.getWorld()) &&
                    player.getLocation().distance(shrineLocation) <= radius) {
                captureBossBar.addPlayer(player);
            } else {
                captureBossBar.removePlayer(player);
            }
        }
    }

    private void sendEnemyOnShrineMessage(Clan clan) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        for (String member : clan.getMembers()) { //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            Player clanMember = Bukkit.getPlayer(member); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            if (clanMember != null && clanMember.isOnline()) {
                sendActionBar(clanMember, ChatColor.translateAlternateColorCodes('&',
                        plugin.getShrineConfigManager().get().getString("messages.enemy-on-shrine"))); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
            }
        }
    }

    private boolean isPlayerOnShrine(Player player, Location shrineLocation) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        Location playerLocation = player.getLocation(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        return playerLocation.getBlockX() >= shrineLocation.getBlockX() - 1
                && playerLocation.getBlockX() <= shrineLocation.getBlockX() + 1
                && playerLocation.getBlockZ() >= shrineLocation.getBlockZ() - 1
                && playerLocation.getBlockZ() <= shrineLocation.getBlockZ() + 1
                && playerLocation.getBlockY() == shrineLocation.getBlockY();
    }

    private void rewardPlayersInZone() {
        if (capturingClan != null) {
            BukkitRunnable rewardTask = new BukkitRunnable() { //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                int cycles = 0; //NOPMD - suppressed RedundantFieldInitializer - TODO explain reason for suppression //NOPMD - suppressed RedundantFieldInitializer - TODO explain reason for suppression //NOPMD - suppressed RedundantFieldInitializer - TODO explain reason for suppression
                int maxCycles = plugin.getShrineConfigManager().get().getInt("settings.reward-cycles", 40); //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression

                @Override
                public void run() {
                    if (cycles >= maxCycles || capturingClan == null) {
                        this.cancel();
                        return;
                    }

                    for (String member : capturingClan.getMembers()) { //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                        Player clanMember = Bukkit.getPlayer(member); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                        if (clanMember != null && clanMember.isOnline()) {

                            int rewardExp = plugin.getShrineConfigManager().get().getInt("settings.reward-exp", 27); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
                            clanMember.giveExp(rewardExp);
                            clanMember.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    plugin.getShrineConfigManager().get().getString("messages.player-reward") //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
                                            .replace("{exp}", String.valueOf(rewardExp))));
                        }
                    }

                    cycles++;
                }
            };
            long rewardIntervalTicks = plugin.getShrineConfigManager().get().getInt("settings.reward-interval-ticks", 3600); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
            rewardTask.runTaskTimer(plugin, 0L, rewardIntervalTicks);
        }
    }

    private void onShrineCaptureSuccess() {
        if (capturingClan != null) {

            int clanXpReward = plugin.getShrineConfigManager().get().getInt("settings.reward-clan-xp", 1); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
            capturingClan.addClanXp(clanXpReward);
            clanManager.saveClan(capturingClan);

            String successMessage = plugin.getShrineConfigManager().get().getString("messages.capture-success-reward") //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
                    .replace("{clan_xp}", String.valueOf(clanXpReward));
            clanManager.sendClanMessage(capturingClan, ChatColor.translateAlternateColorCodes('&', successMessage));

            rewardPlayersInZone();
        }
    }

    private void sendActionBar(Player player, String message) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        new BukkitRunnable() {
            @Override
            public void run() {
                player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                        net.md_5.bungee.api.chat.TextComponent.fromLegacyText(message));
            }
        }.runTask(plugin);
    }
}
