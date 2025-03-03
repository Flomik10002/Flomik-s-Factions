package org.flomik.FlomiksFactions.worldEvents.randomEvents.managers; //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression

import org.flomik.FlomiksFactions.FlomiksFactions;
import org.flomik.FlomiksFactions.worldEvents.castle.events.CastleEvent;
import org.flomik.FlomiksFactions.worldEvents.randomEvents.events.DiverEvent;
import org.flomik.FlomiksFactions.worldEvents.randomEvents.events.FishermanEvent;
import org.flomik.FlomiksFactions.worldEvents.randomEvents.events.RandomEvent;
import org.flomik.FlomiksFactions.worldEvents.randomEvents.events.RunnerEvent;

public class RandomEventManager { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private final FlomiksFactions plugin; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private RandomEvent currentEvent; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression

    public RandomEventManager(FlomiksFactions plugin) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        this.plugin = plugin;
    }

    public boolean isRunning() { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        return currentEvent != null && currentEvent.isRunning();
    }

    public void startEvent(String eventType) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        if (isRunning()) {
            plugin.getLogger().info("Ивент уже запущен."); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
            return; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
        }

        switch (eventType.toLowerCase()) { //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression
            case "fisherman" -> currentEvent = new FishermanEvent(plugin);
            case "diver" -> currentEvent = new DiverEvent(plugin);
            case "runner" -> currentEvent = new RunnerEvent(plugin);
            case "castle" -> {
                CastleEvent castleEvent = plugin.getCastleEvent(); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
                if (castleEvent != null) {
                    castleEvent.start();
                } else {
                    plugin.getLogger().info("Не удалось найти CastleEvent!"); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
                }
                return; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
            }
            default -> {
                plugin.getLogger().info("Неизвестный тип ивента: " + eventType); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
                return;
            }
        }

        currentEvent.start();
        plugin.getLogger().info(eventType + " ивент запущен!"); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
    }

    public RandomEvent getCurrentEvent() {
        return currentEvent;
    }

    public void stopEvent() { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        if (currentEvent != null) {
            currentEvent.stop();
            currentEvent = null; //NOPMD - suppressed NullAssignment - TODO explain reason for suppression //NOPMD - suppressed NullAssignment - TODO explain reason for suppression //NOPMD - suppressed NullAssignment - TODO explain reason for suppression
            plugin.getLogger().info("Ивент остановлен."); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
        }
    }
}
