package org.flomik.FlomiksFactions.worldEvents.randomEvents.managers; //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class EventScheduler { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private final RandomEventManager eventManager; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private final JavaPlugin plugin; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private final Random random = new Random(); //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression

    private static final List<int[]> ACTIVE_PERIODS = Arrays.asList( //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
            new int[]{10, 16}, // 10:00 - 16:00
            new int[]{18, 24}  // 18:00 - 00:00
    );

    private static final int MIN_DELAY_MINUTES = 120; // 2 часа //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private static final int MAX_DELAY_MINUTES = 180; // 3 часа //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression

    public EventScheduler(JavaPlugin plugin, RandomEventManager eventManager) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        this.plugin = plugin;
        this.eventManager = eventManager;
    }

    public void start() { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        scheduleNextEvent();
    }

    private void scheduleNextEvent() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (isActivePeriod()) {
                    int randomDelayMinutes = random.nextInt(120); //NOPMD - suppressed LongVariable - TODO explain reason for suppression //NOPMD - suppressed LongVariable - TODO explain reason for suppression
                    LocalDateTime eventTime = LocalDateTime.now().plusMinutes(randomDelayMinutes); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression

                    Bukkit.getLogger().info("[FlomiksFactions] Следующий ивент начнется через " + randomDelayMinutes + " минут (" + eventTime + ")."); //NOPMD - suppressed GuardLogStatement - TODO explain reason for suppression //NOPMD - suppressed GuardLogStatement - TODO explain reason for suppression

                    scheduleEventAfterDelay(randomDelayMinutes * 60L);
                } else {
                    long initialDelay = calculateInitialDelay(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                    Bukkit.getLogger().info("[FlomiksFactions] Не в активном периоде. Ждем " + (initialDelay / 60) + " минут до следующего активного периода."); //NOPMD - suppressed GuardLogStatement - TODO explain reason for suppression //NOPMD - suppressed GuardLogStatement - TODO explain reason for suppression
                    scheduleNextEventWithDelay(initialDelay);
                }
            }
        }.runTaskLater(plugin, calculateInitialDelay());
    }

    private void scheduleEventAfterDelay(long delaySeconds) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        new BukkitRunnable() {
            @Override
            public void run() {
                String randomEvent = getRandomEvent(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                eventManager.startEvent(randomEvent);

                Bukkit.getLogger().info("[FlomiksFactions] Ивент " + randomEvent + " запущен!"); //NOPMD - suppressed GuardLogStatement - TODO explain reason for suppression //NOPMD - suppressed GuardLogStatement - TODO explain reason for suppression

                int nextDelayMinutes = MIN_DELAY_MINUTES + random.nextInt(MAX_DELAY_MINUTES - MIN_DELAY_MINUTES + 1); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                LocalDateTime nextEventTime = LocalDateTime.now().plusMinutes(nextDelayMinutes); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression

                Bukkit.getLogger().info("[FlomiksFactions] Следующий ивент будет запущен через " + nextDelayMinutes + " минут (" + nextEventTime + ")."); //NOPMD - suppressed GuardLogStatement - TODO explain reason for suppression //NOPMD - suppressed GuardLogStatement - TODO explain reason for suppression
                scheduleNextEventWithDelay(nextDelayMinutes * 60L);
            }
        }.runTaskLater(plugin, delaySeconds * 20L);
    }

    private void scheduleNextEventWithDelay(long delaySeconds) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        new BukkitRunnable() {
            @Override
            public void run() {
                scheduleNextEvent();
            }
        }.runTaskLater(plugin, delaySeconds * 20L);
    }

    private boolean isActivePeriod() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Europe/Moscow")); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        int currentHour = now.getHour(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression

        for (int[] period : ACTIVE_PERIODS) { //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            if (currentHour >= period[0] && currentHour < period[1]) {
                return true; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
            }
        }
        return false;
    }

    private long calculateInitialDelay() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Europe/Moscow")); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression

        if (isActivePeriod()) {
            return 0L; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
        }

        for (int[] period : ACTIVE_PERIODS) { //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            if (now.getHour() < period[0]) {
                LocalDateTime nextStart = now.withHour(period[0]).withMinute(0).withSecond(0); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                return java.time.Duration.between(now, nextStart).getSeconds(); //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
            }
        }

        LocalDateTime nextStart = now.plusDays(1).withHour(ACTIVE_PERIODS.get(0)[0]).withMinute(0).withSecond(0); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        return java.time.Duration.between(now, nextStart).getSeconds();
    }

    private String getRandomEvent() {
        List<String> events = Arrays.asList("fisherman", "diver", "runner", "castle"); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        return events.get(random.nextInt(events.size()));
    }
}
