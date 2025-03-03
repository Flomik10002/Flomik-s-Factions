package org.flomik.FlomiksFactions.worldEvents.randomEvents.events; //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.flomik.FlomiksFactions.FlomiksFactions;

import java.util.List;

public class DiverEvent extends RandomEvent { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression

    private String EventName = "Водолаз"; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private String description = "Погружайтесь под воду! Тот, кто проведёт больше времени под водой, станет победителем."; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private int remainingTime; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression

    public DiverEvent(FlomiksFactions plugin) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        super(plugin, 61, List.of(500, 300, 200));
        this.remainingTime = duration;
        this.bossBar = Bukkit.createBossBar("Ивент: Водолаз", BarColor.BLUE, BarStyle.SEGMENTED_20);
    }

    @Override
    public void start() {
        running = true;

        announceEventStart(EventName, description);

        Bukkit.getOnlinePlayers().forEach(bossBar::addPlayer);

        plugin.getServer().getScheduler().runTaskTimer(plugin, this::onTick, 0L, 20L); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
    }

    @Override
    public void stop() {
        if (!running) return; //NOPMD - suppressed ControlStatementBraces - TODO explain reason for suppression //NOPMD - suppressed ControlStatementBraces - TODO explain reason for suppression //NOPMD - suppressed ControlStatementBraces - TODO explain reason for suppression

        bossBar.removeAll();

        running = false;
        announceResults(EventName, "продержался", "секунд под водой");
    }

    @Override
    public void onTick() {
        if (--remainingTime <= 0) { //NOPMD - suppressed AssignmentInOperand - TODO explain reason for suppression //NOPMD - suppressed AssignmentInOperand - TODO explain reason for suppression //NOPMD - suppressed AssignmentInOperand - TODO explain reason for suppression
            stop();
            return;
        }

        updateBossBarProgress(remainingTime, EventName);

        for (Player player : plugin.getServer().getOnlinePlayers()) { //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
            if (isPlayerUnderwater(player)) {
                scores.put(player, scores.getOrDefault(player, 0) + 1);
            }
        }
    }

    private boolean isPlayerUnderwater(Player player) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        Material eyeBlock = player.getEyeLocation().getBlock().getType(); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
        return eyeBlock == Material.WATER || eyeBlock == Material.KELP || eyeBlock == Material.SEAGRASS
                || eyeBlock == Material.TALL_SEAGRASS || eyeBlock == Material.BUBBLE_COLUMN;
    }
}
