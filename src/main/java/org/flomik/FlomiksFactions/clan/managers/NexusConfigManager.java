package org.flomik.FlomiksFactions.clan.managers;

import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.flomik.FlomiksFactions.FlomiksFactions;
import org.flomik.FlomiksFactions.utils.Utils; // your custom class, if needed

import java.io.File;
import java.io.IOException;

public class NexusConfigManager {

    private static FileConfiguration customFile;
    private static File file;
    private static FlomiksFactions plugin;

    /**
     * Create or load 'nexus.yml' in the plugin's data folder
     */
    public static void setup(FlomiksFactions pl) {
        plugin = pl;
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        file = new File(dataFolder, "nexus.yml");

        // If it doesn't exist in the plugin jar's resources, remove 'false' from the saveResource call
        if (!file.exists()) {
            plugin.saveResource("nexus.yml", false);
        }

        customFile = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Reload from disk
     */

    public static void loadConfig() {
        customFile = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Save changes in memory to disk
     */
    public static void save() {
        try {
            customFile.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Couldn't save nexus.yml!");
            e.printStackTrace();
        }
    }

    /**
     * Get the FileConfiguration object for direct reads/writes
     */
    public static FileConfiguration get() {
        return customFile;
    }

    /**
     * Get a bar color from path, defaults to WHITE if missing or invalid
     */
    public static BarColor getColor(String path) {
        String colorName = customFile.getString(path, "WHITE");
        switch (colorName.toUpperCase()) {
            case "RED":
                return BarColor.RED;
            case "YELLOW":
                return BarColor.YELLOW;
            case "GREEN":
                return BarColor.GREEN;
            case "BLUE":
                return BarColor.BLUE;
            case "PINK":
                return BarColor.PINK;
            case "PURPLE":
                return BarColor.PURPLE;
            case "WHITE":
            default:
                return BarColor.WHITE;
        }
    }

    /**
     * Get a translated string (supports color codes)
     */
    public static String getString(String path) {
        String value = customFile.getString(path, "");
        // If you have hex color conversions (Utils.hex), use it; else remove that part
        value = ChatColor.translateAlternateColorCodes('&', Utils.hex(value));
        return value;
    }

    /**
     * Get an integer from the config
     */
    public static int getInt(String path) {
        return customFile.getInt(path, 0);
    }

    // ... You can add other get methods (getDouble, getBoolean, etc.) as needed
}
