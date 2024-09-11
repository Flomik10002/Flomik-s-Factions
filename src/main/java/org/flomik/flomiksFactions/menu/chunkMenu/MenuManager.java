package org.flomik.flomiksFactions.menu.chunkMenu;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.flomik.flomiksFactions.FlomiksFactions;
import org.flomik.flomiksFactions.commands.clan.ClanManager;

import java.util.Arrays;

public class MenuManager {
    private final FlomiksFactions plugin;
    private final ClanManager clanManager;

    public MenuManager(FlomiksFactions plugin, ClanManager clanManager) {
        this.plugin = plugin;
        this.clanManager = clanManager;
    }

    public void openMenu(Player player) {
        // Создаем инвентарь 54 слота (6x9)
        Inventory menu = Bukkit.createInventory(null, 54, "Карта чанков");

        // Проходим по чанкам вокруг игрока
        for (int x = 0; x < 9; x++) {
            for (int z = 0; z < 6; z++) {
                Chunk chunk = player.getWorld().getChunkAt(x, z);

                // Проверяем, есть ли клан, который владеет чанком
                String clanName = ChunkUtils.getChunkOwner(chunk, clanManager);
                ItemStack item;

                if (clanName != null) {
                    // Чанк в привате клана
                    item = new ItemStack(Material.BARRIER);
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName("§cЧанк (X: " + chunk.getX() + ", Z: " + chunk.getZ() + ")");
                    meta.setLore(Arrays.asList("§cЭтот чанк под контролем клана §e" + clanName));
                    item.setItemMeta(meta);
                } else {
                    // Чанк свободен
                    item = new ItemStack(Material.GRASS_BLOCK);
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName("§aЧанк (X: " + chunk.getX() + ", Z: " + chunk.getZ() + ")");
                    meta.setLore(Arrays.asList("§aЧанк без привата"));
                    item.setItemMeta(meta);
                }

                // Добавляем предмет в меню
                menu.addItem(item);
            }
        }

        // Открываем меню для игрока
        player.openInventory(menu);
    }

    public FlomiksFactions getPlugin() {
        return plugin;
    }
}
