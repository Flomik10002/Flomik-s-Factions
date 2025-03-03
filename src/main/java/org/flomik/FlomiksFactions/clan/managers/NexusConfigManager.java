package org.flomik.FlomiksFactions.clan.managers; //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression

import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.flomik.FlomiksFactions.FlomiksFactions;
import org.flomik.FlomiksFactions.utils.Utils; // your custom class, if needed

import java.io.File;
import java.io.IOException;

public class NexusConfigManager { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression

    private static FileConfiguration customFile; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private static File file; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private static FlomiksFactions plugin; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression

    /**
     * Create or load 'nexus.yml' in the plugin's data folder
     */
    public static void setup(FlomiksFactions pl) { //NOPMD - suppressed ShortVariable - TODO explain reason for suppression //NOPMD - suppressed ShortVariable - TODO explain reason for suppression //NOPMD - suppressed ShortVariable - TODO explain reason for suppression
        plugin = pl;
        File dataFolder = plugin.getDataFolder(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        if (!dataFolder.exists()) { //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
            dataFolder.mkdirs(); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
        }
        file = new File(dataFolder, "nexus.yml");

        // If it doesn't exist in the plugin jar's resources, remove 'false' from the saveResource call //NOPMD - suppressed CommentSize - TODO explain reason for suppression //NOPMD - suppressed CommentSize - TODO explain reason for suppression //NOPMD - suppressed CommentSize - TODO explain reason for suppression
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
            plugin.getLogger().severe("Couldn't save nexus.yml!"); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
            e.printStackTrace(); //NOPMD - suppressed AvoidPrintStackTrace - TODO explain reason for suppression //NOPMD - suppressed AvoidPrintStackTrace - TODO explain reason for suppression //NOPMD - suppressed AvoidPrintStackTrace - TODO explain reason for suppression
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
    public static BarColor getColor(String path) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        String colorName = customFile.getString(path, "WHITE"); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        switch (colorName.toUpperCase()) { //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression
            case "RED":
                return BarColor.RED; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
            case "YELLOW":
                return BarColor.YELLOW; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
            case "GREEN":
                return BarColor.GREEN; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
            case "BLUE":
                return BarColor.BLUE; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
            case "PINK":
                return BarColor.PINK; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
            case "PURPLE":
                return BarColor.PURPLE; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
            case "WHITE":
            default:
                return BarColor.WHITE;
        }
    }

    /**
     * Get a translated string (supports color codes)
     */
    public static String getString(String path) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        String value = customFile.getString(path, "");
        // If you have hex color conversions (Utils.hex), use it; else remove that part
        value = ChatColor.translateAlternateColorCodes('&', Utils.hex(value));
        return value;
    }

    /**
     * Get an integer from the config
     */
    public static int getInt(String path) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        return customFile.getInt(path, 0);
    }

    // ... You can add other get methods (getDouble, getBoolean, etc.) as needed
}
