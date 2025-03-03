package org.flomik.FlomiksFactions.worldEvents.randomEvents.events; //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.flomik.FlomiksFactions.FlomiksFactions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RunnerEvent extends RandomEvent { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression

    private final Map<Player, Location> lastLocations; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private String EventName = "Бегун"; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private String description = "Соревнуйтесь, кто пробежит больше всего блоков, не останавливаясь!"; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private int remainingTime; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression

    public RunnerEvent(FlomiksFactions plugin) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
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
        if (!running) return; //NOPMD - suppressed ControlStatementBraces - TODO explain reason for suppression //NOPMD - suppressed ControlStatementBraces - TODO explain reason for suppression //NOPMD - suppressed ControlStatementBraces - TODO explain reason for suppression

        bossBar.removeAll();
        running = false;
        announceResults(EventName, "пробежал", "блоков");
    }

    @Override
    public void onTick() {
        if (--remainingTime <= 0) { //NOPMD - suppressed AssignmentInOperand - TODO explain reason for suppression //NOPMD - suppressed AssignmentInOperand - TODO explain reason for suppression //NOPMD - suppressed AssignmentInOperand - TODO explain reason for suppression
            stop();
            return;
        }

        updateBossBarProgress(remainingTime, EventName);

        for (Player player : Bukkit.getOnlinePlayers()) { //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            if (!player.isSprinting()) continue; //NOPMD - suppressed ControlStatementBraces - TODO explain reason for suppression //NOPMD - suppressed ControlStatementBraces - TODO explain reason for suppression //NOPMD - suppressed ControlStatementBraces - TODO explain reason for suppression

            Location lastLoc = lastLocations.getOrDefault(player, player.getLocation()); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            double distance = lastLoc.distance(player.getLocation()); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression

            scores.put(player, scores.getOrDefault(player, 0) + (int) distance);
            lastLocations.put(player, player.getLocation());
        }
    }
}
