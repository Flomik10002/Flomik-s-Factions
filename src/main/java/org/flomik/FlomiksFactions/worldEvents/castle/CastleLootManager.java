package org.flomik.FlomiksFactions.worldEvents.castle;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.flomik.FlomiksFactions.FlomiksFactions;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CastleLootManager {
    private final FlomiksFactions plugin;
    private final Map<ItemStack, Double> lootTable = new HashMap<>();
    private final File lootFile;

    public CastleLootManager(FlomiksFactions plugin) {
        this.plugin = plugin;
        this.lootFile = new File(plugin.getDataFolder(), "loot.yml");
        loadLootTable();
    }

    public void updateLootTable(Map<ItemStack, Double> newLootTable) {
        lootTable.clear();
        lootTable.putAll(newLootTable);
        saveLootTable();
    }

    public void loadLootTable() {
        lootTable.clear();
        if (!lootFile.exists()) {
            plugin.saveResource("loot.yml", false);
        }

        FileConfiguration lootConfig = YamlConfiguration.loadConfiguration(lootFile);
        for (String key : lootConfig.getKeys(false)) {
            String materialName = lootConfig.getString(key + ".material");
            int amount = lootConfig.getInt(key + ".amount", 1);
            double chance = lootConfig.getDouble(key + ".chance", 0.0);

            if (materialName != null) {
                try {
                    Material material = Material.valueOf(materialName.toUpperCase());
                    ItemStack item = new ItemStack(material, amount);
                    lootTable.put(item, chance);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid material: " + materialName + " in loot.yml");
                }
            } else {
                plugin.getLogger().warning("Material missing for loot item: " + key);
            }
        }
    }

    public void saveLootTable() {
        FileConfiguration lootConfig = new YamlConfiguration();
        for (Map.Entry<ItemStack, Double> entry : lootTable.entrySet()) {
            ItemStack item = entry.getKey();
            lootConfig.set(item.getType() + ".material", item.getType().toString());
            lootConfig.set(item.getType() + ".amount", item.getAmount());
            lootConfig.set(item.getType() + ".chance", entry.getValue());
        }

        try {
            lootConfig.save(lootFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save loot table!");
            e.printStackTrace();
        }
    }

    public Map<ItemStack, Double> getLootTable() {
        return lootTable;
    }
}
