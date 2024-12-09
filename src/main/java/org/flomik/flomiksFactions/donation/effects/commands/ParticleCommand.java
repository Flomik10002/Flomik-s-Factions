package org.flomik.flomiksFactions.donation.effects.commands;

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
import org.flomik.flomiksFactions.donation.DonationManager;
import org.flomik.flomiksFactions.donation.effects.ParticleEffectHandler;
import org.flomik.flomiksFactions.player.PlayerDataHandler;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ParticleCommand implements CommandExecutor, Listener {

    private final DonationManager donationManager;
    private final ParticleEffectHandler particleEffectHandler;
    private final PlayerDataHandler playerDataHandler;

    public ParticleCommand(DonationManager donationManager, ParticleEffectHandler particleEffectHandler, PlayerDataHandler playerDataHandler) {
        this.donationManager = donationManager;
        this.particleEffectHandler = particleEffectHandler;
        this.playerDataHandler = playerDataHandler;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Эту команду могут использовать только игроки.");
            return false;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Использование: /particles <open/enable/disable>");
            return false;
        }

        switch (args[0].toLowerCase()) {
            case "enable":

                openParticleSelectionMenu(player);
                return true;

            case "disable":

                if (particleEffectHandler.areParticlesEnabled(player)) {
                    particleEffectHandler.disableParticlesForPlayer(player);
                    player.sendMessage(ChatColor.YELLOW + "Частицы отключены.");
                } else {
                    player.sendMessage(ChatColor.RED + "Частицы не активированы.");
                }
                break;

            default:
                player.sendMessage(ChatColor.RED + "Неизвестная команда.");
                break;
        }

        return true;
    }

    private void openParticleSelectionMenu(Player player) {

        Inventory particleMenu = Bukkit.createInventory(null, 18, ChatColor.GOLD + "§6Выберите цвет частиц");


        Map<String, Color> availableColors = donationManager.getAvailableColors();


        Set<String> purchasedColors = playerDataHandler.getPurchasedColors(player.getName());


        List<String> purchasedSorted = purchasedColors.stream().sorted().toList();


        List<String> notPurchasedSorted = availableColors.keySet().stream()
                .filter(color -> !purchasedColors.contains(color))
                .sorted().toList();


        int slot = 0;
        for (String colorName : purchasedSorted) {
            ItemStack item = createConcreteBlock(colorName);
            particleMenu.setItem(slot++, item);
        }


        for (String colorName : notPurchasedSorted) {
            ItemStack item = createGlass(colorName);
            particleMenu.setItem(slot++, item);
        }


        player.openInventory(particleMenu);
    }


    private ItemStack createConcreteBlock(String colorName) {
        Material concreteMaterial = Material.valueOf(colorName.toUpperCase() + "_CONCRETE");
        ItemStack concreteBlock = new ItemStack(concreteMaterial);

        ItemMeta meta = concreteBlock.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GREEN + "Куплен: " + colorName);
            concreteBlock.setItemMeta(meta);
        }
        return concreteBlock;
    }


    private ItemStack createGlass(String colorName) {
        Material glassPaneMaterial = Material.valueOf(colorName.toUpperCase() + "_STAINED_GLASS");
        ItemStack glassPane = new ItemStack(glassPaneMaterial);

        ItemMeta meta = glassPane.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.RED + "Не куплен: " + colorName);
            glassPane.setItemMeta(meta);
        }
        return glassPane;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();


        if (inventory == null || !event.getView().getTitle().equals(ChatColor.GOLD + "§6Выберите цвет частиц")) {
            return;
        }

        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || !clickedItem.hasItemMeta()) return;

        String colorName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName()).replace("Куплен: ", "").replace("Не куплен: ", "").toLowerCase();


        if (playerDataHandler.getPurchasedColors(player.getName()).contains(colorName)) {
            particleEffectHandler.disableParticlesForPlayer(player);
            particleEffectHandler.enableParticlesForPlayer(player, donationManager.getAvailableColors().get(colorName));
            player.sendMessage(ChatColor.GREEN + "Частицы цвета " + colorName + " активированы!");
            player.closeInventory();
        } else {
            player.sendMessage(ChatColor.RED + "Этот цвет вам недоступен.");
        }
    }
}