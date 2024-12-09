package org.flomik.FlomiksFactions.donation;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.flomik.FlomiksFactions.player.PlayerDataHandler;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class DonationCommand implements CommandExecutor, Listener {

    private final DonationManager donationManager;
    private final PlayerDataHandler playerDataHandler;

    public DonationCommand(DonationManager donationManager, PlayerDataHandler playerDataHandler) {
        this.donationManager = donationManager;
        this.playerDataHandler = playerDataHandler;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = null;
        if ((sender instanceof Player)) {
            player = (Player) sender;
        }

        switch (args[0].toLowerCase()) {
            case "particles":
                openParticlePurchaseMenu(player);
                break;

            default:
                player.sendMessage(ChatColor.RED + "Неизвестная команда. Доступные команды: balance, add, buy");
                break;
        }

        return true;
    }

    private void openParticlePurchaseMenu(Player player) {

        Inventory particleMenu = Bukkit.createInventory(null, 18, ChatColor.GOLD + "§6Купить цвет частиц");
        Map<String, Color> availableColors = donationManager.getAvailableColors();
        Set<String> purchasedColors = playerDataHandler.getPurchasedColors(player.getName());
        List<String> purchasedSorted = purchasedColors.stream().sorted().toList();
        List<String> notPurchasedSorted = availableColors.keySet().stream()
                .filter(color -> !purchasedColors.contains(color))
                .sorted().toList();

        int slot = 0;

        for (String colorName : notPurchasedSorted) {
            ItemStack item = createConcreteBlock(colorName);
            particleMenu.setItem(slot++, item);
        }

        for (String colorName : purchasedSorted) {
            ItemStack item = createGlass(colorName);
            particleMenu.setItem(slot++, item);
        }

        player.openInventory(particleMenu);
    }

    private ItemStack createGlass(String colorName) {
        Material glassPaneMaterial = Material.valueOf(colorName.toUpperCase() + "_STAINED_GLASS");
        ItemStack glassPane = new ItemStack(glassPaneMaterial);

        ItemMeta meta = glassPane.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GREEN + "Куплен: " + colorName);
            glassPane.setItemMeta(meta);
        }
        return glassPane;
    }


    private ItemStack createConcreteBlock(String colorName) {
        Material concreteMaterial = Material.valueOf(colorName.toUpperCase() + "_CONCRETE");
        ItemStack concreteBlock = new ItemStack(concreteMaterial);

        ItemMeta meta = concreteBlock.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.RED + "Не куплен: " + colorName);
            concreteBlock.setItemMeta(meta);
        }
        return concreteBlock;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();


        if (inventory == null || !event.getView().getTitle().equals(ChatColor.GOLD + "§6Купить цвет частиц")) {
            return;
        }

        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || !clickedItem.hasItemMeta()) return;

        String colorName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName()).replace("Куплен: ", "").replace("Не куплен: ", "").toLowerCase();

        if (playerDataHandler.getPurchasedColors(player.getName()).contains(colorName)) {
            player.sendMessage(ChatColor.RED + "Этот цвет уже куплен.");
        } else {

            if (donationManager.purchaseEffect(player, 200, colorName)) {
                player.sendMessage(ChatColor.GREEN + "Вы успешно купили цвет " + colorName + "!");
                player.closeInventory();
            } else {
                player.sendMessage(ChatColor.RED + "У вас недостаточно дублонов для покупки.");
            }
        }
    }
}