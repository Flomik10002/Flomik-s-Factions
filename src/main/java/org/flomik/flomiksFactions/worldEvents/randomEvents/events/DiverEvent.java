package org.flomik.flomiksFactions.worldEvents.randomEvents.events;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.flomik.flomiksFactions.FlomiksFactions;
import org.flomik.flomiksFactions.worldEvents.randomEvents.RandomEvent;

import java.util.List;

public class DiverEvent extends RandomEvent {

    private String EventName = "Водолаз";
    private String description = "Погружайтесь под воду! Тот, кто проведёт больше времени под водой, станет победителем.";
    private int remainingTime;

    public DiverEvent(FlomiksFactions plugin) {
        super(plugin, 61, List.of(500, 300, 200));
        this.remainingTime = duration;
        this.bossBar = Bukkit.createBossBar("Ивент: Водолаз", BarColor.BLUE, BarStyle.SEGMENTED_20);
    }

    @Override
    public void start() {
        running = true;

        announceEventStart(EventName, description);

        Bukkit.getOnlinePlayers().forEach(bossBar::addPlayer);

        plugin.getServer().getScheduler().runTaskTimer(plugin, this::onTick, 0L, 20L);
    }

    @Override
    public void stop() {
        if (!running) return;

        bossBar.removeAll();

        running = false;
        announceResults(EventName, "продержался", "секунд под водой");
    }

    @Override
    public void onTick() {
        if (--remainingTime <= 0) {
            stop();
            return;
        }

        updateBossBarProgress(remainingTime, EventName);

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (isPlayerUnderwater(player)) {
                scores.put(player, scores.getOrDefault(player, 0) + 1);
            }
        }
    }

    private boolean isPlayerUnderwater(Player player) {
        Material eyeBlock = player.getEyeLocation().getBlock().getType();
        return eyeBlock == Material.WATER || eyeBlock == Material.KELP || eyeBlock == Material.SEAGRASS
                || eyeBlock == Material.TALL_SEAGRASS || eyeBlock == Material.BUBBLE_COLUMN;
    }
}
