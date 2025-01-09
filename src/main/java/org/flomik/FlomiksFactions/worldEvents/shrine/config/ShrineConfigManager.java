package org.flomik.FlomiksFactions.worldEvents.shrine.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.flomik.FlomiksFactions.FlomiksFactions;

import java.io.File;
import java.io.IOException;

public class ShrineConfigManager {
    private static FileConfiguration customFile;
    private static File file;
    private static FlomiksFactions plugin;

    public static void setup(FlomiksFactions pl) {
        plugin = pl;
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        file = new File(dataFolder, "shrines.yml");

        if (!file.exists()) {
            plugin.saveResource("shrines.yml", false);
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
            plugin.getLogger().severe("Couldn't save shrines.yml!");
            e.printStackTrace();
        }
    }
}
