package org.flomik.FlomiksFactions.worldEvents.randomEvents;

import org.flomik.FlomiksFactions.FlomiksFactions;
import org.flomik.FlomiksFactions.worldEvents.castle.CastleEvent;
import org.flomik.FlomiksFactions.worldEvents.randomEvents.events.DiverEvent;
import org.flomik.FlomiksFactions.worldEvents.randomEvents.events.FishermanEvent;
import org.flomik.FlomiksFactions.worldEvents.randomEvents.events.RunnerEvent;

public class RandomEventManager {
    private final FlomiksFactions plugin;
    private RandomEvent currentEvent;

    public RandomEventManager(FlomiksFactions plugin) {
        this.plugin = plugin;
    }

    public boolean isRunning() {
        return currentEvent != null && currentEvent.isRunning();
    }

    public void startEvent(String eventType) {
        if (isRunning()) {
            plugin.getLogger().info("Ивент уже запущен.");
            return;
        }

        switch (eventType.toLowerCase()) {
            case "fisherman" -> currentEvent = new FishermanEvent(plugin);
            case "diver" -> currentEvent = new DiverEvent(plugin);
            case "runner" -> currentEvent = new RunnerEvent(plugin);
            case "castle" -> {
                CastleEvent castleEvent = plugin.getCastleEvent();
                if (castleEvent != null) {
                    castleEvent.start();
                } else {
                    plugin.getLogger().info("Не удалось найти CastleEvent!");
                }
                return;
            }
            default -> {
                plugin.getLogger().info("Неизвестный тип ивента: " + eventType);
                return;
            }
        }

        currentEvent.start();
        plugin.getLogger().info(eventType + " ивент запущен!");
    }

    public RandomEvent getCurrentEvent() {
        return currentEvent;
    }

    public void stopEvent() {
        if (currentEvent != null) {
            currentEvent.stop();
            currentEvent = null;
            plugin.getLogger().info("Ивент остановлен.");
        }
    }
}
