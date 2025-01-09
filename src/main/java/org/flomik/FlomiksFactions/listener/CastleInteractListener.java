package org.flomik.FlomiksFactions.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.flomik.FlomiksFactions.worldEvents.castle.config.CastleConfigManager;
import org.flomik.FlomiksFactions.worldEvents.castle.events.CastleEvent;
import org.flomik.FlomiksFactions.worldEvents.castle.managers.CastleLootManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CastleInteractListener implements Listener {
    private final CastleEvent eventManager;
    private final CastleLootManager lootManager;

    public CastleInteractListener(CastleEvent eventManager, CastleLootManager lootManager) {
        this.eventManager = eventManager;
        this.lootManager = lootManager;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null || block.getType() != Material.PLAYER_HEAD) return;

        if (!eventManager.isEventActive()) return;

        block.setType(Material.AIR);
        Player player = event.getPlayer();

        ItemStack reward = getRandomLoot();
        if (reward != null) {
            player.getInventory().addItem(reward);
            player.sendMessage(CastleConfigManager.getString("messages.loot-recieved").replace("%item%", reward.getType().toString()));
        }
    }
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        String menuTitle = "Лут в замке";

        if (!event.getView().getTitle().equalsIgnoreCase(menuTitle)) {
            return;
        }

        Map<ItemStack, Double> newLootTable = new HashMap<>();

        for (ItemStack item : inventory.getContents()) {
            if (item != null) {
                newLootTable.put(item.clone(), 0.5);
            }
        }

        lootManager.updateLootTable(newLootTable);
    }

    private ItemStack getRandomLoot() {
        Random random = new Random();
        double totalWeight = lootManager.getLootTable().values().stream().mapToDouble(Double::doubleValue).sum();
        double roll = random.nextDouble() * totalWeight;

        double cumulativeWeight = 0.0;
        for (Map.Entry<ItemStack, Double> entry : lootManager.getLootTable().entrySet()) {
            cumulativeWeight += entry.getValue();
            if (roll <= cumulativeWeight) {
                return entry.getKey();
            }
        }
        return null;
    }
}
