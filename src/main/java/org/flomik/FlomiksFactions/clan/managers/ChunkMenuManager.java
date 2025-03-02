package org.flomik.FlomiksFactions.clan.managers;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.Vector;
import org.flomik.FlomiksFactions.FlomiksFactions;
import org.flomik.FlomiksFactions.clan.Clan;

import java.util.Arrays;

public class ChunkMenuManager {
    private static final String MENU_TITLE = "Карта чанков";
    private static final int MENU_SIZE = 45;

    private final FlomiksFactions plugin;
    private final ClanManager clanManager;

    public ChunkMenuManager(FlomiksFactions plugin, ClanManager clanManager) {
        this.plugin = plugin;
        this.clanManager = clanManager;
    }

    public void openChunkMenu(Player player) {
        Inventory menu = Bukkit.createInventory(null, MENU_SIZE, MENU_TITLE);
        Chunk playerChunk = player.getLocation().getChunk();
        String facing = getFacingDirection(player.getLocation().getDirection());

        for (int row = -2; row <= 2; row++) {
            for (int col = -4; col <= 4; col++) {
                Chunk chunk = getRelativeChunk(playerChunk, row, col, facing);

                String clanName = getChunkOwner(chunk, clanManager);
                ItemStack item;

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
                int slot = getSlotForChunk(row, col);
                menu.setItem(slot, item);
            }
        }

        player.openInventory(menu);
    }

    private String getFacingDirection(Vector direction) {
        double angle = Math.toDegrees(Math.atan2(direction.getX(), direction.getZ()));

        if (angle >= -45 && angle <= 45) {
            return "NORTH";
        } else if (angle >= 45 && angle <= 135) {
            return "WEST";
        } else if (angle >= -135 && angle <= -45) {
            return "EAST";
        } else {
            return "SOUTH";
        }
    }

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

    private int getSlotForChunk(int rowOffset, int colOffset) {
        // Вместо 2 + rowOffset → 2 - rowOffset
        int rowIndex = 2 - rowOffset;
        int colIndex = 4 + colOffset;
        return rowIndex * 9 + colIndex;
    }


    private static String getChunkOwner(Chunk chunk, ClanManager clanManager) {
        String chunkId = getChunkId(chunk);

        for (Clan clan : clanManager.getAllClans()) {
            if (clan.hasClaimedChunk(chunkId)) {
                return clan.getName();
            }
        }
        return null;
    }

    private static String getChunkId(Chunk chunk) {
        return chunk.getWorld().getName() + "_" + chunk.getX() + "_" + chunk.getZ();
    }
}