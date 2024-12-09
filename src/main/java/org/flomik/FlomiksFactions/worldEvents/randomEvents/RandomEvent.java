package org.flomik.FlomiksFactions.worldEvents.randomEvents;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.flomik.FlomiksFactions.FlomiksFactions;

import java.util.*;

public abstract class RandomEvent {
    protected final FlomiksFactions plugin;
    protected final int duration;
    protected final List<Integer> rewards;
    protected final Map<Player, Integer> scores;
    protected BossBar bossBar;
    protected boolean running;
    private boolean finished;

    public RandomEvent(FlomiksFactions plugin, int duration, List<Integer> rewards) {
        this.plugin = plugin;
        this.duration = duration;
        this.rewards = rewards;
        this.scores = new HashMap<>();
        this.finished = false;
    }

    public abstract void start();
    public abstract void stop();
    public abstract void onTick();

    public boolean isRunning() {
        return running;
    }

    public BossBar getBossBar() {
        return bossBar;
    }

    protected void updateBossBarProgress(int remainingTime, String EventName) {
        double progress = (double) remainingTime / duration;
        bossBar.setProgress(Math.max(0, progress));
        bossBar.setTitle("§a[Ивент] §6" + EventName + ". Оставшееся время: " + remainingTime + " секунд");
    }

    protected void announceEventStart(String eventName, String description) {
        Bukkit.broadcastMessage("§a[Ивенты] §fИвент §6" + eventName + " §fначался! Участвуйте и побеждайте!\n§e" + description);
    }

    protected void announceResults(String eventName, String achievementAction, String achievementUnit) {
        if (finished) {
            plugin.getLogger().info("Результаты уже были объявлены.");
            return;
        }

        finished = true;

        List<Map.Entry<Player, Integer>> sortedScores = scores.entrySet().stream()
                .sorted(Map.Entry.<Player, Integer>comparingByValue().reversed())
                .toList();

        if (sortedScores.isEmpty()) {
            Bukkit.broadcastMessage("§a[Ивенты] §fИвент §6" + eventName + " §fзавершён, но никто не принял участие.");
            return;
        }

        Bukkit.broadcastMessage("§a[Ивенты] §fИвент §6" + eventName + " §fзавершён! Результаты:");

        for (int i = 0; i < rewards.size() && i < sortedScores.size(); i++) {
            Player player = sortedScores.get(i).getKey();
            int achievement = sortedScores.get(i).getValue();
            int reward = rewards.get(i);

            player.sendMessage(ChatColor.GOLD + "Вы заняли " + (i + 1) + " место и получили " + reward + " монет!");

            plugin.getEconomy().depositPlayer(player, reward);

            Bukkit.broadcastMessage("§6" + (i + 1) + " место: §e" + player.getName() + " §f— " + achievementAction + " " + achievement + " " + achievementUnit + " §fи получил §6" + reward + " золота!");
        }
    }
}
