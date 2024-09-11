package org.flomik.flomiksFactions.events;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MenuProtectionListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Карта чанков")) {
            // Если меню называется "Карта чанков", отменяем все клики
            event.setCancelled(true);
        }
    }
}