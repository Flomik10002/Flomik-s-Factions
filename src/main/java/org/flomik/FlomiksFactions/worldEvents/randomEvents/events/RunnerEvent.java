package org.flomik.FlomiksFactions.worldEvents.randomEvents.events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.flomik.FlomiksFactions.FlomiksFactions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RunnerEvent extends RandomEvent {

    private final Map<Player, Location> lastLocations;
    private String EventName = "Бегун";
    private String description = "Соревнуйтесь, кто пробежит больше всего блоков, не останавливаясь!";
    private int remainingTime;

    public RunnerEvent(FlomiksFactions plugin) {
        super(plugin, 481, List.of(1500, 1000, 750));
        lastLocations = new HashMap<>();
        this.remainingTime = duration;
    }

    @Override
    public void start() {
        running = true;
        this.bossBar = Bukkit.createBossBar("Ивент: Бегун", BarColor.RED, BarStyle.SEGMENTED_20);

        announceEventStart(EventName, description);

        Bukkit.getOnlinePlayers().forEach(bossBar::addPlayer);
        Bukkit.getScheduler().runTaskTimer(plugin, this::onTick, 0L, 20L);
    }

    @Override
    public void stop() {
        if (!running) return;

        bossBar.removeAll();
        running = false;
        announceResults(EventName, "пробежал", "блоков");
    }

    @Override
    public void onTick() {
        if (--remainingTime <= 0) {
            stop();
            return;
        }

        updateBossBarProgress(remainingTime, EventName);

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.isSprinting()) continue;

            Location lastLoc = lastLocations.getOrDefault(player, player.getLocation());
            double distance = lastLoc.distance(player.getLocation());

            scores.put(player, scores.getOrDefault(player, 0) + (int) distance);
            lastLocations.put(player, player.getLocation());
        }
    }
}
