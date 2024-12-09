package org.flomik.flomiksFactions.worldEvents.randomEvents.events;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.flomik.flomiksFactions.FlomiksFactions;
import org.flomik.flomiksFactions.worldEvents.randomEvents.RandomEvent;

import java.util.List;

public class FishermanEvent extends RandomEvent implements Listener {

    private String EventName = "Рыболов";
    private String description = "Ловите рыбу! Тот, кто поймает больше всего рыбы за отведённое время, победит.";
    private int remainingTime;

    public FishermanEvent(FlomiksFactions plugin) {
        super(plugin, 601, List.of(1000, 750, 500));
        this.remainingTime = duration;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void start() {
        running = true;
        this.bossBar = Bukkit.createBossBar("Ивент: Рыболов", BarColor.GREEN, BarStyle.SEGMENTED_20);
        announceEventStart(EventName, description);

        Bukkit.getOnlinePlayers().forEach(bossBar::addPlayer);

        Bukkit.getScheduler().runTaskTimer(plugin, this::onTick, 0L, 20L);
    }

    @Override
    public void stop() {
        if (!running) return;

        running = false;

        bossBar.removeAll();

        HandlerList.unregisterAll(this);
        announceResults(EventName, "выловил", "рыб");
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if (!running || event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;

        Player player = event.getPlayer();
        scores.put(player, scores.getOrDefault(player, 0) + 1);
    }

    @Override
    public void onTick() {
        if (--remainingTime <= 0) {
            stop();
            return;
        }

        updateBossBarProgress(remainingTime, EventName);
    }
}
