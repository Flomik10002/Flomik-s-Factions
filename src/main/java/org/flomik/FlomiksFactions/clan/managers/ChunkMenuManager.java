package org.flomik.FlomiksFactions.clan.managers; //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression

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

public class ChunkMenuManager { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private static final String MENU_TITLE = "Карта чанков"; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private static final int MENU_SIZE = 45; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression

    private final FlomiksFactions plugin; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private final ClanManager clanManager; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression

    public ChunkMenuManager(FlomiksFactions plugin, ClanManager clanManager) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        this.plugin = plugin;
        this.clanManager = clanManager;
    }

    public void openChunkMenu(Player player) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        Inventory menu = Bukkit.createInventory(null, MENU_SIZE, MENU_TITLE); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        Chunk playerChunk = player.getLocation().getChunk(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        String facing = getFacingDirection(player.getLocation().getDirection()); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression

        for (int row = -2; row <= 2; row++) {
            for (int col = -4; col <= 4; col++) {
                Chunk chunk = getRelativeChunk(playerChunk, row, col, facing); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression

                String clanName = getChunkOwner(chunk, clanManager); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                ItemStack item; //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression

                if (row == 0 && col == 0) {
                    item = new ItemStack(Material.PLAYER_HEAD); //NOPMD - suppressed AvoidInstantiatingObjectsInLoops - TODO explain reason for suppression //NOPMD - suppressed AvoidInstantiatingObjectsInLoops - TODO explain reason for suppression //NOPMD - suppressed AvoidInstantiatingObjectsInLoops - TODO explain reason for suppression
                    SkullMeta meta = (SkullMeta) item.getItemMeta(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                    meta.setOwningPlayer(player);
                    meta.setDisplayName("§aВы здесь");

                    if (clanName != null) {
                        Clan clan = clanManager.getClan(clanName.toLowerCase()); //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression
                        if (clan != null) {
                            meta.setLore(Arrays.asList("§cТерритория клана: §e" + clanName, "§cЛидер: §e" + clan.getOwner()));
                        } else {
                            meta.setLore(Arrays.asList("§aЧанк без привата")); //NOPMD - suppressed AvoidDuplicateLiterals - TODO explain reason for suppression //NOPMD - suppressed AvoidDuplicateLiterals - TODO explain reason for suppression //NOPMD - suppressed AvoidDuplicateLiterals - TODO explain reason for suppression
                        }
                    } else {
                        meta.setLore(Arrays.asList("§aЧанк без привата"));
                    }
                    item.setItemMeta(meta);
                } else {
                    if (clanName != null) {
                        Clan clan = clanManager.getClan(clanName.toLowerCase()); //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression

                        if (clan != null) {
                            item = new ItemStack(Material.RED_CONCRETE); //NOPMD - suppressed AvoidInstantiatingObjectsInLoops - TODO explain reason for suppression //NOPMD - suppressed AvoidInstantiatingObjectsInLoops - TODO explain reason for suppression //NOPMD - suppressed AvoidInstantiatingObjectsInLoops - TODO explain reason for suppression
                            ItemMeta meta = item.getItemMeta(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                            meta.setDisplayName("§cЧанк (X: " + chunk.getX() + ", Z: " + chunk.getZ() + ")");
                            meta.setLore(Arrays.asList("§cТерритория клана: §e" + clanName, "§cЛидер: §e" + clan.getOwner()));
                            item.setItemMeta(meta);
                        } else {
                            item = new ItemStack(Material.GRASS_BLOCK); //NOPMD - suppressed AvoidInstantiatingObjectsInLoops - TODO explain reason for suppression //NOPMD - suppressed AvoidInstantiatingObjectsInLoops - TODO explain reason for suppression //NOPMD - suppressed AvoidInstantiatingObjectsInLoops - TODO explain reason for suppression
                            ItemMeta meta = item.getItemMeta(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                            meta.setDisplayName("§aЧанк (X: " + chunk.getX() + ", Z: " + chunk.getZ() + ")");
                            meta.setLore(Arrays.asList("§aЧанк без привата"));
                            item.setItemMeta(meta);
                        }
                    } else {
                        item = new ItemStack(Material.GRASS_BLOCK); //NOPMD - suppressed AvoidInstantiatingObjectsInLoops - TODO explain reason for suppression //NOPMD - suppressed AvoidInstantiatingObjectsInLoops - TODO explain reason for suppression //NOPMD - suppressed AvoidInstantiatingObjectsInLoops - TODO explain reason for suppression
                        ItemMeta meta = item.getItemMeta(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                        meta.setDisplayName("§aЧанк (X: " + chunk.getX() + ", Z: " + chunk.getZ() + ")");
                        meta.setLore(Arrays.asList("§aЧанк без привата"));
                        item.setItemMeta(meta);
                    }
                }
                int slot = getSlotForChunk(row, col); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                menu.setItem(slot, item);
            }
        }

        player.openInventory(menu);
    }

    private String getFacingDirection(Vector direction) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        double angle = Math.toDegrees(Math.atan2(direction.getX(), direction.getZ())); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression

        if (angle >= -45 && angle <= 45) {
            return "NORTH"; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
        } else if (angle >= 45 && angle <= 135) {
            return "WEST"; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
        } else if (angle >= -135 && angle <= -45) {
            return "EAST"; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
        } else {
            return "SOUTH";
        }
    }

    private Chunk getRelativeChunk(Chunk centerChunk, int rowOffset, int colOffset, String facing) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        int xOffset = 0;
        int zOffset = 0;

        switch (facing) { //NOPMD - suppressed NonExhaustiveSwitch - TODO explain reason for suppression //NOPMD - suppressed NonExhaustiveSwitch - TODO explain reason for suppression //NOPMD - suppressed NonExhaustiveSwitch - TODO explain reason for suppression
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

    private int getSlotForChunk(int rowOffset, int colOffset) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        // Вместо 2 + rowOffset → 2 - rowOffset
        int rowIndex = 2 - rowOffset; //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        int colIndex = 4 + colOffset; //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        return rowIndex * 9 + colIndex;
    }


    private static String getChunkOwner(Chunk chunk, ClanManager clanManager) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        String chunkId = getChunkId(chunk); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression

        for (Clan clan : clanManager.getAllClans()) { //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            if (clan.hasClaimedChunk(chunkId)) {
                return clan.getName(); //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
            }
        }
        return null;
    }

    private static String getChunkId(Chunk chunk) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        return chunk.getWorld().getName() + "_" + chunk.getX() + "_" + chunk.getZ();
    }
}