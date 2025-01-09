package org.flomik.FlomiksFactions.worldEvents.castle.events;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.scheduler.BukkitRunnable;
import org.flomik.FlomiksFactions.FlomiksFactions;
import org.flomik.FlomiksFactions.worldEvents.castle.config.CastleConfigManager;
import org.flomik.FlomiksFactions.worldEvents.castle.managers.HeadsManager;

public class CastleEvent {
    private final FlomiksFactions plugin;
    private final HeadsManager headsManager;
    private final BossBar bossBar;
    private boolean eventActive = false;
    private int wave = 0;

    public CastleEvent(FlomiksFactions plugin, HeadsManager headsManager) {
        this.plugin = plugin;
        this.headsManager = headsManager;

        this.bossBar = Bukkit.createBossBar(
                CastleConfigManager.getString("bossbar.title"),
                CastleConfigManager.getColor("bossbar.color"),
                BarStyle.SEGMENTED_10
        );
        this.bossBar.setVisible(false);
    }

    public void start() {
        Bukkit.broadcastMessage("§a[Ивенты] §fИвент §6Замок §fначался! Участвуйте и получайте награды!");
        if (eventActive) return;

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
                    int countdown = CastleConfigManager.getInt("settings.wave-duration");

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

    public void spawnWave() {
        int headCount = (wave < 3) ? 15 : 5;
        headsManager.spawnRandomHeads(headCount);
        plugin.getLogger().info("Волна " + (wave + 1) + " заспавнилась с " + headCount + " сокровищницами");
    }

    public void endEvent() {
        eventActive = false;
        wave = 0;

        bossBar.removeAll();
        bossBar.setVisible(false);

        headsManager.despawnAllHeads();
        Bukkit.broadcastMessage("§a[Ивенты] §fИвент §6Замок §fзавершён!");
        plugin.getLogger().info("Ивент завершен");
    }

    public boolean isEventActive() {
        return eventActive;
    }
}
