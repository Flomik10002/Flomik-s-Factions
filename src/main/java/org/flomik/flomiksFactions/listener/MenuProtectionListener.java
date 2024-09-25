package org.flomik.flomiksFactions.listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MenuProtectionListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();

        // Проверяем, является ли инвентарь меню "Карта чанков" или меню выбора частиц
        if (title.equals("Карта чанков")) {
            // Отменяем все клики, чтобы игроки не могли забирать предметы
            event.setCancelled(true);
        }
    }
}
//|| title.equals("§6Выберите цвет частиц")
//§