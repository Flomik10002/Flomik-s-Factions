package org.flomik.flomiksFactions.commands.menu;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.Vector;
import org.flomik.flomiksFactions.FlomiksFactions;
import org.flomik.flomiksFactions.commands.clan.Clan;
import org.flomik.flomiksFactions.commands.clan.ClanManager;
import org.flomik.flomiksFactions.commands.menu.chunkMenu.ChunkUtils;

import java.util.Arrays;

public class MenuManager {
    private final FlomiksFactions plugin;
    private final ClanManager clanManager;

    public MenuManager(FlomiksFactions plugin, ClanManager clanManager) {
        this.plugin = plugin;
        this.clanManager = clanManager;
    }

    public void openChunkMenu(Player player) {
        // Создаем инвентарь 45 слотов (9x5)
        Inventory menu = Bukkit.createInventory(null, 45, "Карта чанков");

        // Получаем текущий чанк игрока
        Chunk playerChunk = player.getLocation().getChunk();

        // Определяем направление взгляда игрока
        String facing = getFacingDirection(player.getLocation().getDirection());

        // Проходим по чанкам в радиусе 9x5 с коррекцией по направлению взгляда
        for (int row = -2; row <= 2; row++) {
            for (int col = -4; col <= 4; col++) {
                // Рассчитываем позицию чанка в зависимости от направления
                Chunk chunk = getRelativeChunk(playerChunk, row, col, facing);

                // Проверяем, есть ли клан, который владеет чанком
                String clanName = ChunkUtils.getChunkOwner(chunk, clanManager);
                ItemStack item;

                // Если это центральный чанк, где находится игрок
                if (row == 0 && col == 0) {
                    item = new ItemStack(Material.PLAYER_HEAD);
                    SkullMeta meta = (SkullMeta) item.getItemMeta();
                    meta.setOwningPlayer(player);
                    meta.setDisplayName("§aВы здесь");

                    if (clanName != null) {
                        Clan clan = clanManager.getClan(clanName.toLowerCase());
                        if (clan != null) {
                            meta.setLore(Arrays.asList("§cТерритория клана: §e" + clanName, "§cЛидер: §e" + clan.getOwner()));
                        } else {
                            meta.setLore(Arrays.asList("§aЧанк без привата"));
                        }
                    } else {
                        meta.setLore(Arrays.asList("§aЧанк без привата"));
                    }
                    item.setItemMeta(meta);
                } else {
                    if (clanName != null) {
                        Clan clan = clanManager.getClan(clanName.toLowerCase());
                        // Обычные чанки вокруг игрока
                        if (clan != null) {
                            item = new ItemStack(Material.RED_CONCRETE);
                            ItemMeta meta = item.getItemMeta();
                            meta.setDisplayName("§cЧанк (X: " + chunk.getX() + ", Z: " + chunk.getZ() + ")");
                            meta.setLore(Arrays.asList("§cТерритория клана: §e" + clanName, "§cЛидер: §e" + clan.getOwner()));
                            item.setItemMeta(meta);
                        } else {
                            item = new ItemStack(Material.GRASS_BLOCK);
                            ItemMeta meta = item.getItemMeta();
                            meta.setDisplayName("§aЧанк (X: " + chunk.getX() + ", Z: " + chunk.getZ() + ")");
                            meta.setLore(Arrays.asList("§aЧанк без привата"));
                            item.setItemMeta(meta);
                        }
                    } else {
                        item = new ItemStack(Material.GRASS_BLOCK);
                        ItemMeta meta = item.getItemMeta();
                        meta.setDisplayName("§aЧанк (X: " + chunk.getX() + ", Z: " + chunk.getZ() + ")");
                        meta.setLore(Arrays.asList("§aЧанк без привата"));
                        item.setItemMeta(meta);
                    }
                }

                // Рассчитываем слот в инвентаре
                int slot = getSlotForChunk(row, col);
                menu.setItem(slot, item);
            }
        }

        // Открываем меню для игрока
        player.openInventory(menu);
    }

    // Метод для определения направления взгляда игрока
    private String getFacingDirection(Vector direction) {
        double angle = Math.toDegrees(Math.atan2(direction.getX(), direction.getZ()));

        if (angle >= -45 && angle <= 45) {
            return "SOUTH";
        } else if (angle >= 45 && angle <= 135) {
            return "WEST";
        } else if (angle >= -135 && angle <= -45) {
            return "EAST";
        } else {
            return "NORTH";
        }
    }

    // Метод для получения чанка относительно центрального чанка в зависимости от направления
    private Chunk getRelativeChunk(Chunk centerChunk, int rowOffset, int colOffset, String facing) {
        int xOffset = 0;
        int zOffset = 0;

        switch (facing) {
            case "NORTH":
                xOffset = colOffset;
                zOffset = -rowOffset;
                break;
            case "SOUTH":
                xOffset = -colOffset;
                zOffset = rowOffset;
                break;
            case "EAST":
                xOffset = rowOffset;
                zOffset = colOffset;
                break;
            case "WEST":
                xOffset = -rowOffset;
                zOffset = -colOffset;
                break;
        }

        return centerChunk.getWorld().getChunkAt(centerChunk.getX() + xOffset, centerChunk.getZ() + zOffset);
    }

    // Метод для вычисления слота в инвентаре 9x5
    private int getSlotForChunk(int rowOffset, int colOffset) {
        // Сначала рассчитываем индекс строки (по оси Z)
        int rowIndex = 2 + rowOffset; // Средняя строка - 2, диапазон: [-2, 2] -> [0, 4]

        // Затем рассчитываем индекс столбца (по оси X)
        int colIndex = 4 + colOffset; // Средний столбец - 4, диапазон: [-4, 4] -> [0, 8]

        // Общий индекс слота
        return rowIndex * 9 + colIndex; // 9 слотов в строке
    }
}