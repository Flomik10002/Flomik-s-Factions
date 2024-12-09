package org.flomik.FlomiksFactions.worldEvents.randomEvents;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class EventScheduler {
    private final RandomEventManager eventManager;
    private final JavaPlugin plugin;
    private final Random random = new Random();

    private static final List<int[]> ACTIVE_PERIODS = Arrays.asList(
            new int[]{10, 16}, // 10:00 - 16:00
            new int[]{18, 24}  // 18:00 - 00:00
    );

    private static final int MIN_DELAY_MINUTES = 120; // 2 часа
    private static final int MAX_DELAY_MINUTES = 180; // 3 часа

    public EventScheduler(JavaPlugin plugin, RandomEventManager eventManager) {
        this.plugin = plugin;
        this.eventManager = eventManager;
    }

    public void start() {
        scheduleNextEvent();
    }

    private void scheduleNextEvent() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (isActivePeriod()) {
                    int randomDelayMinutes = random.nextInt(120);
                    LocalDateTime eventTime = LocalDateTime.now().plusMinutes(randomDelayMinutes);

                    Bukkit.getLogger().info("[FlomikFactions] Следующий ивент начнется через " + randomDelayMinutes + " минут (" + eventTime + ").");

                    scheduleEventAfterDelay(randomDelayMinutes * 60L);
                } else {
                    long initialDelay = calculateInitialDelay();
                    Bukkit.getLogger().info("[FlomikFactions] Не в активном периоде. Ждем " + (initialDelay / 60) + " минут до следующего активного периода.");
                    scheduleNextEventWithDelay(initialDelay);
                }
            }
        }.runTaskLater(plugin, calculateInitialDelay());
    }

    private void scheduleEventAfterDelay(long delaySeconds) {
        new BukkitRunnable() {
            @Override
            public void run() {
                String randomEvent = getRandomEvent();
                eventManager.startEvent(randomEvent);

                Bukkit.getLogger().info("[FlomikFactions] Ивент " + randomEvent + " запущен!");

                int nextDelayMinutes = MIN_DELAY_MINUTES + random.nextInt(MAX_DELAY_MINUTES - MIN_DELAY_MINUTES + 1);
                LocalDateTime nextEventTime = LocalDateTime.now().plusMinutes(nextDelayMinutes);

                Bukkit.getLogger().info("[FlomikFactions] Следующий ивент будет запущен через " + nextDelayMinutes + " минут (" + nextEventTime + ").");
                scheduleNextEventWithDelay(nextDelayMinutes * 60L);
            }
        }.runTaskLater(plugin, delaySeconds * 20L);
    }

    private void scheduleNextEventWithDelay(long delaySeconds) {
        new BukkitRunnable() {
            @Override
            public void run() {
                scheduleNextEvent();
            }
        }.runTaskLater(plugin, delaySeconds * 20L);
    }

    private boolean isActivePeriod() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Europe/Moscow"));
        int currentHour = now.getHour();

        for (int[] period : ACTIVE_PERIODS) {
            if (currentHour >= period[0] && currentHour < period[1]) {
                return true;
            }
        }
        return false;
    }

    private long calculateInitialDelay() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Europe/Moscow"));

        if (isActivePeriod()) {
            return 0L;
        }

        for (int[] period : ACTIVE_PERIODS) {
            if (now.getHour() < period[0]) {
                LocalDateTime nextStart = now.withHour(period[0]).withMinute(0).withSecond(0);
                return java.time.Duration.between(now, nextStart).getSeconds();
            }
        }

        LocalDateTime nextStart = now.plusDays(1).withHour(ACTIVE_PERIODS.get(0)[0]).withMinute(0).withSecond(0);
        return java.time.Duration.between(now, nextStart).getSeconds();
    }

    private String getRandomEvent() {
        List<String> events = Arrays.asList("fisherman", "diver", "runner", "castle");
        return events.get(random.nextInt(events.size()));
    }
}
