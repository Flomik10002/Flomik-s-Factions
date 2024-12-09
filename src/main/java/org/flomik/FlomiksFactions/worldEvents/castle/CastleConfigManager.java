package org.flomik.FlomiksFactions.worldEvents.castle;

import org.bukkit.boss.BarColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.ChatColor;
import org.flomik.FlomiksFactions.FlomiksFactions;
import org.flomik.FlomiksFactions.utils.Utils;

import java.io.File;
import java.io.IOException;

public class CastleConfigManager {
    private static FileConfiguration customFile;
    private static File file;
    private static FlomiksFactions plugin;

    public static void setup(FlomiksFactions pl) {
        plugin = pl;
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        file = new File(dataFolder, "castle.yml");

        if (!file.exists()) {
            plugin.saveResource("castle.yml", false);
        }

        customFile = YamlConfiguration.loadConfiguration(file);
    }

    public static void loadConfig() {
        customFile = YamlConfiguration.loadConfiguration(file);
    }

    public static FileConfiguration get() {
        return customFile;
    }

    public static void save() {
        try {
            customFile.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Couldn't save castle.yml!");
            e.printStackTrace();
        }
    }

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

    public static void reload() {
        loadConfig();
    }

    public static String getString(String path) {
        String value = customFile.getString(path, "");
        return ChatColor.translateAlternateColorCodes('&', Utils.hex(value));
    }

    public static int getInt(String path) {
        return customFile.getInt(path, 0);
    }
}
