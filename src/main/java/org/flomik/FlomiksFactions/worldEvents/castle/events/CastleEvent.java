package org.flomik.FlomiksFactions.worldEvents.castle.events; //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression

import org.bukkit.Bukkit;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.scheduler.BukkitRunnable;
import org.flomik.FlomiksFactions.FlomiksFactions;
import org.flomik.FlomiksFactions.worldEvents.castle.config.CastleConfigManager;
import org.flomik.FlomiksFactions.worldEvents.castle.managers.HeadsManager;

public class CastleEvent { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private final FlomiksFactions plugin; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private final HeadsManager headsManager; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private final BossBar bossBar; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private boolean eventActive = false; //NOPMD - suppressed RedundantFieldInitializer - TODO explain reason for suppression //NOPMD - suppressed RedundantFieldInitializer - TODO explain reason for suppression //NOPMD - suppressed RedundantFieldInitializer - TODO explain reason for suppression
    private int wave = 0; //NOPMD - suppressed RedundantFieldInitializer - TODO explain reason for suppression //NOPMD - suppressed RedundantFieldInitializer - TODO explain reason for suppression //NOPMD - suppressed RedundantFieldInitializer - TODO explain reason for suppression

    public CastleEvent(FlomiksFactions plugin, HeadsManager headsManager) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        this.plugin = plugin;
        this.headsManager = headsManager;

        this.bossBar = Bukkit.createBossBar(
                CastleConfigManager.getString("bossbar.title"),
                CastleConfigManager.getColor("bossbar.color"),
                BarStyle.SEGMENTED_10
        );
        this.bossBar.setVisible(false);
    }

    public void start() { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        Bukkit.broadcastMessage("§a[Ивенты] §fИвент §6Замок §fначался! Участвуйте и получайте награды!");
        if (eventActive) return; //NOPMD - suppressed ControlStatementBraces - TODO explain reason for suppression //NOPMD - suppressed ControlStatementBraces - TODO explain reason for suppression //NOPMD - suppressed ControlStatementBraces - TODO explain reason for suppression

        eventActive = true;
        wave = 0;
        Bukkit.getOnlinePlayers().forEach(bossBar::addPlayer);
        bossBar.setVisible(true);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!eventActive || wave >= 4) {
                    endEvent();
                    cancel();
                    return;
                }

                spawnWave();
                wave++;

                new BukkitRunnable() {
                    int countdown = CastleConfigManager.getInt("settings.wave-duration"); //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression

                    @Override
                    public void run() {
                        if (!eventActive || countdown <= 0) {
                            bossBar.setProgress(0);
                            cancel();
                            return;
                        }

                        bossBar.setProgress(Math.max(0, Math.min(1, countdown / 90.0)));
                        bossBar.setTitle(CastleConfigManager.getString("bossbar.title").replace("%time%", String.valueOf(countdown)));
                        countdown--;
                    }
                }.runTaskTimer(plugin, 0, 20);
            }
        }.runTaskTimer(plugin, 0, 1800);
    }

    public void spawnWave() { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        int headCount = (wave < 3) ? 15 : 5; //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        headsManager.spawnRandomHeads(headCount);
        plugin.getLogger().info("Волна " + (wave + 1) + " заспавнилась с " + headCount + " сокровищницами"); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
    }

    public void endEvent() { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        eventActive = false;
        wave = 0;

        bossBar.removeAll();
        bossBar.setVisible(false);

        headsManager.despawnAllHeads();
        Bukkit.broadcastMessage("§a[Ивенты] §fИвент §6Замок §fзавершён!");
        plugin.getLogger().info("Ивент завершен"); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
    }

    public boolean isEventActive() {
        return eventActive;
    }
}
