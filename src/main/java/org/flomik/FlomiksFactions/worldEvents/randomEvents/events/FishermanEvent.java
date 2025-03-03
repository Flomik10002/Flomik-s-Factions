package org.flomik.FlomiksFactions.worldEvents.randomEvents.events; //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.flomik.FlomiksFactions.FlomiksFactions;

import java.util.List;

public class FishermanEvent extends RandomEvent implements Listener { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression

    private String EventName = "Рыболов"; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private String description = "Ловите рыбу! Тот, кто поймает больше всего рыбы за отведённое время, победит."; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private int remainingTime; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression

    public FishermanEvent(FlomiksFactions plugin) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        super(plugin, 601, List.of(1000, 750, 500));
        this.remainingTime = duration;
        plugin.getServer().getPluginManager().registerEvents(this, plugin); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
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
        if (!running) return; //NOPMD - suppressed ControlStatementBraces - TODO explain reason for suppression //NOPMD - suppressed ControlStatementBraces - TODO explain reason for suppression //NOPMD - suppressed ControlStatementBraces - TODO explain reason for suppression

        running = false;

        bossBar.removeAll();

        HandlerList.unregisterAll(this);
        announceResults(EventName, "выловил", "рыб");
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        if (!running || event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return; //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression

        Player player = event.getPlayer(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        scores.put(player, scores.getOrDefault(player, 0) + 1);
    }

    @Override
    public void onTick() {
        if (--remainingTime <= 0) { //NOPMD - suppressed AssignmentInOperand - TODO explain reason for suppression //NOPMD - suppressed AssignmentInOperand - TODO explain reason for suppression //NOPMD - suppressed AssignmentInOperand - TODO explain reason for suppression
            stop();
            return;
        }

        updateBossBarProgress(remainingTime, EventName);
    }
}
