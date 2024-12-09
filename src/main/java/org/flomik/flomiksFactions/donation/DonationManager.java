package org.flomik.flomiksFactions.donation;

import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.flomik.flomiksFactions.FlomiksFactions;
import org.flomik.flomiksFactions.player.PlayerDataHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DonationManager {

    private final FlomiksFactions plugin;
    private final PlayerDataHandler playerDataHandler;
    private final Map<String, Color> availableColors;

    public DonationManager(FlomiksFactions plugin, PlayerDataHandler playerDataHandler) {
        this.plugin = plugin;
        this.playerDataHandler = playerDataHandler;
        this.availableColors = new HashMap<>();
        initializeAvailableColors();
    }

    private void initializeAvailableColors() {
        availableColors.put("red", Color.RED);
        availableColors.put("blue", Color.BLUE);
        availableColors.put("lime", Color.LIME);
        availableColors.put("yellow", Color.YELLOW);
        availableColors.put("purple", Color.PURPLE);
        availableColors.put("cyan", Color.AQUA);
        availableColors.put("white", Color.WHITE);
        availableColors.put("black", Color.BLACK);
        availableColors.put("pink", Color.FUCHSIA);
        availableColors.put("orange", Color.ORANGE);
    }

    public void addDoubloons(Player player, int amount) {
        playerDataHandler.addDoubloons(player, amount);
    }

    public boolean canAfford(Player player, int price) {
        return playerDataHandler.getDoubloons(player.getName()) >= price;
    }

    public boolean hasPurchasedColor(Player player, String colorName) {
        Set<String> purchasedColors = playerDataHandler.getPurchasedColors(player.getName());
        return purchasedColors != null && purchasedColors.contains(colorName.toLowerCase());
    }

    public boolean purchaseEffect(Player player, int price, String colorName) {
        Color color = availableColors.get(colorName.toLowerCase());
        if (color == null) {
            player.sendMessage("Выбранный цвет недоступен.");
            return false;
        }

        if (hasPurchasedColor(player, colorName)) {
            player.sendMessage("Этот цвет уже был куплен.");
            return false;
        }

        if (!canAfford(player, price)) {
            player.sendMessage("У вас недостаточно дублонов для покупки.");
            return false;
        }

        playerDataHandler.addDoubloons(player, -price);
        playerDataHandler.addPurchasedColor(player.getName(), colorName.toLowerCase());
        playerDataHandler.setPlayerEffectColor(player.getName(), color);

        player.sendMessage("Вы купили эффект с цветом: " + colorName + "!");
        return true;
    }

    public Map<String, Color> getAvailableColors() {
        return availableColors;
    }
}