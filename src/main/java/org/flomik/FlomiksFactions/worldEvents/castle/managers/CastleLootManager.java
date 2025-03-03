package org.flomik.FlomiksFactions.worldEvents.castle.managers; //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.flomik.FlomiksFactions.FlomiksFactions;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CastleLootManager { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private final FlomiksFactions plugin; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private final Map<ItemStack, Double> lootTable = new HashMap<>(); //NOPMD - suppressed UseConcurrentHashMap - TODO explain reason for suppression //NOPMD - suppressed UseConcurrentHashMap - TODO explain reason for suppression //NOPMD - suppressed UseConcurrentHashMap - TODO explain reason for suppression
    private final File lootFile; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression

    public CastleLootManager(FlomiksFactions plugin) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        this.plugin = plugin;
        this.lootFile = new File(plugin.getDataFolder(), "loot.yml");
        loadLootTable(); //NOPMD - suppressed ConstructorCallsOverridableMethod - TODO explain reason for suppression //NOPMD - suppressed ConstructorCallsOverridableMethod - TODO explain reason for suppression //NOPMD - suppressed ConstructorCallsOverridableMethod - TODO explain reason for suppression
    }

    public void updateLootTable(Map<ItemStack, Double> newLootTable) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        lootTable.clear();
        lootTable.putAll(newLootTable);
        saveLootTable();
    }

    public void loadLootTable() { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        lootTable.clear();
        if (!lootFile.exists()) {
            plugin.saveResource("loot.yml", false);
        }

        FileConfiguration lootConfig = YamlConfiguration.loadConfiguration(lootFile); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        for (String key : lootConfig.getKeys(false)) { //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            String materialName = lootConfig.getString(key + ".material"); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            int amount = lootConfig.getInt(key + ".amount", 1); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            double chance = lootConfig.getDouble(key + ".chance", 0.0); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression

            if (materialName != null) {
                try {
                    Material material = Material.valueOf(materialName.toUpperCase()); //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression
                    ItemStack item = new ItemStack(material, amount); //NOPMD - suppressed AvoidInstantiatingObjectsInLoops - TODO explain reason for suppression //NOPMD - suppressed AvoidInstantiatingObjectsInLoops - TODO explain reason for suppression //NOPMD - suppressed AvoidInstantiatingObjectsInLoops - TODO explain reason for suppression
                    lootTable.put(item, chance);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid material: " + materialName + " in loot.yml"); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
                }
            } else {
                plugin.getLogger().warning("Material missing for loot item: " + key); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
            }
        }
    }

    public void saveLootTable() { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        FileConfiguration lootConfig = new YamlConfiguration(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        for (Map.Entry<ItemStack, Double> entry : lootTable.entrySet()) { //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            ItemStack item = entry.getKey(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            lootConfig.set(item.getType() + ".material", item.getType().toString());
            lootConfig.set(item.getType() + ".amount", item.getAmount());
            lootConfig.set(item.getType() + ".chance", entry.getValue());
        }

        try {
            lootConfig.save(lootFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save loot table!"); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
            e.printStackTrace(); //NOPMD - suppressed AvoidPrintStackTrace - TODO explain reason for suppression //NOPMD - suppressed AvoidPrintStackTrace - TODO explain reason for suppression //NOPMD - suppressed AvoidPrintStackTrace - TODO explain reason for suppression
        }
    }

    public Map<ItemStack, Double> getLootTable() {
        return lootTable;
    }
}
