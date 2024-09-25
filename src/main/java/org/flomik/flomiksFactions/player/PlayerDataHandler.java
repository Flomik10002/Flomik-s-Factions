package org.flomik.flomiksFactions.player;

import org.bukkit.Color;
import org.bukkit.Statistic;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.flomik.flomiksFactions.FlomiksFactions;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PlayerDataHandler {
    private final FlomiksFactions plugin;
    private final File file;
    private final FileConfiguration playersConfig;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public PlayerDataHandler(FlomiksFactions plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "players.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        playersConfig = YamlConfiguration.loadConfiguration(file);
    }

    public Set<String> getPurchasedColors(String playerName) {
        List<String> colors = playersConfig.getStringList(playerName + ".purchasedColors");
        return new HashSet<>(colors);
    }

    // Добавление дублонов
    public void addDoubloons(Player player, int amount) {
        String playerName = player.getName();
        int currentBalance = getDoubloons(playerName);
        playersConfig.set(playerName + ".doubloons", currentBalance + amount);
        saveConfig();
    }

    // Получение дублонов игрока
    public int getDoubloons(String playerName) {
        return playersConfig.getInt(playerName + ".doubloons", 0);
    }

    // Установка купленного цвета частиц
    public void setPlayerEffectColor(String playerName, Color color) {
        playersConfig.addDefault(playerName + ".purchasedEffect", color.asRGB());
        saveConfig();
    }

    public void addPurchasedColor(String playerName, String colorName) {
        Set<String> purchasedColors = getPurchasedColors(playerName);
        purchasedColors.add(colorName);
        playersConfig.set(playerName + ".purchasedColors", new ArrayList<>(purchasedColors));
        saveConfig();
    }

    // Получение цвета эффекта игрока
    public Color getPlayerEffectColor(String playerName) {
        if (hasPurchasedParticles(playerName)) {
            int rgb = playersConfig.getInt(playerName + ".purchasedEffect");
            return Color.fromRGB(rgb);
        }
        return null; // Если частицы не куплены, возвращаем null
    }

    // Проверка, куплены ли частицы
    public boolean hasPurchasedParticles(String playerName) {
        return playersConfig.contains(playerName + ".purchasedEffect");
    }

    // Проверяет, есть ли дата первого захода для данного игрока
    public boolean hasFirstJoinDate(String playerName) {
        return playersConfig.contains(playerName + ".firstJoinDate");
    }

    // Устанавливает дату первого захода для данного игрока
    public void setFirstJoinDate(String playerName, LocalDate date) {
        playersConfig.set(playerName + ".firstJoinDate", date.format(DATE_FORMATTER));
        saveConfig();
    }

    public boolean hasPlayerData(String playerName) {
        // Проверяем наличие записи об игроке в конфигурации
        return playersConfig.contains(playerName);
    }

    public int getDeaths(Player player) {
        return player.getStatistic(Statistic.DEATHS);
    }

    public int getKills(Player player) {
        return player.getStatistic(Statistic.PLAYER_KILLS);
    }

    // Сохраняет уровень, силу и максимальную силу игрока
    public void savePlayerAttributes(String playerName, int level, int strength, int maxStrength) {
        playersConfig.set(playerName + ".level", level);
        playersConfig.set(playerName + ".strength", strength);
        playersConfig.set(playerName + ".maxStrength", maxStrength);
        saveConfig();
    }

    // Получает дату первого захода для данного игрока
    public String getFirstJoinDate(String playerName) {
        if (hasFirstJoinDate(playerName)) {
            return playersConfig.getString(playerName + ".firstJoinDate", "Неизвестно");
        }
        return "Неизвестно";
    }

    // Получает уровень игрока
    public int getPlayerLevel(String playerName) {
        return playersConfig.getInt(playerName + ".level", 1); // по умолчанию 1
    }

    public void setPlayerLevel(String playerName, int level) {
        playersConfig.set(playerName + ".level", level);
        saveConfig();
    }

    // Получает силу игрока
    public int getPlayerStrength(String playerName) {
        return playersConfig.getInt(playerName + ".strength", 0); // по умолчанию 10
    }

    public void setPlayerStrength(String playerName, int strength) {
        playersConfig.set(playerName + ".strength", strength);
        saveConfig();
    }

    public void addPlayerStrength(String playerName, int strength) {
        int oldStrength = playersConfig.getInt(playerName + ".strength", 0);
        strength = oldStrength + strength;
        playersConfig.set(playerName + ".strength", strength);
        saveConfig();
    }

    // Получает максимальную силу игрока
    public int getPlayerMaxStrength(String playerName) {
        return playersConfig.getInt(playerName + ".maxStrength", 10); // по умолчанию 10
    }

    // Сохраняет время игры (в тиках) для данного игрока
    public void setPlayTime(String playerName, int ticksPlayed) {
        playersConfig.set(playerName + ".playTime", ticksPlayed);
        saveConfig();
    }

    // Получает время игры (в тиках) для данного игрока
    public int getPlayTime(String playerName) {
        return playersConfig.getInt(playerName + ".playTime", 0); // по умолчанию 0
    }

    // Сохраняет изменения в конфигурации players.yml
    private void saveConfig() {
        try {
            playersConfig.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save player data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}