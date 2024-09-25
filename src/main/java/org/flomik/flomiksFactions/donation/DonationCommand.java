package org.flomik.flomiksFactions.donation;

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
import org.flomik.flomiksFactions.player.PlayerDataHandler;

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
        if (!(sender instanceof Player)) {
            sender.sendMessage("Эту команду могут использовать только игроки.");
            return false;
        }

        Player player = (Player) sender;

        switch (args[0].toLowerCase()) {
            case "balance":
                // Проверка баланса дублонов
                int balance = playerDataHandler.getDoubloons(player.getName());
                player.sendMessage(ChatColor.GREEN + "Ваш баланс дублонов: " + ChatColor.GOLD + balance);
                break;

            case "add":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Использование: /donate add <количество>");
                    return false;
                }
                try {
                    int amount = Integer.parseInt(args[1]);
                    donationManager.addDoubloons(player, amount);
                    player.sendMessage(ChatColor.GREEN + "Вам добавлено " + amount + " дублонов.");
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "Введите корректное количество дублонов.");
                }
                break;

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
        // Создаем меню с 18 слотами
        Inventory particleMenu = Bukkit.createInventory(null, 18, ChatColor.GOLD + "§6Купить цвет частиц");

        // Получаем список всех доступных цветов
        Map<String, Color> availableColors = donationManager.getAvailableColors();

        // Разделяем на купленные и некупленные цвета
        Set<String> purchasedColors = playerDataHandler.getPurchasedColors(player.getName());

        // Сортируем купленные по алфавиту
        List<String> purchasedSorted = purchasedColors.stream().sorted().toList();

        // Сортируем некупленные по алфавиту
        List<String> notPurchasedSorted = availableColors.keySet().stream()
                .filter(color -> !purchasedColors.contains(color))
                .sorted().toList();

        // Добавляем в меню купленные цвета (стеклянные панели)
        int slot = 0;

        // Добавляем в меню некупленные цвета (бетонные блоки)
        for (String colorName : notPurchasedSorted) {
            ItemStack item = createConcreteBlock(colorName); // Некупленный цвет - бетонный блок
            particleMenu.setItem(slot++, item);
        }

        for (String colorName : purchasedSorted) {
            ItemStack item = createGlass(colorName); // Купленный цвет - стеклянная панель
            particleMenu.setItem(slot++, item);
        }

        // Открываем меню для игрока
        player.openInventory(particleMenu);
    }

    // Метод для создания стеклянной панели для купленного цвета
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

    // Метод для создания бетонного блока для некупленного цвета
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

        // Проверяем, что это наше меню
        if (inventory == null || !event.getView().getTitle().equals(ChatColor.GOLD + "§6Купить цвет частиц")) {
            return;
        }

        event.setCancelled(true); // Запрещаем забирать предметы из инвентаря

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || !clickedItem.hasItemMeta()) return;

        String colorName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName()).replace("Куплен: ", "").replace("Не куплен: ", "").toLowerCase();

        // Проверяем, куплен ли цвет
        if (playerDataHandler.getPurchasedColors(player.getName()).contains(colorName)) {
            player.sendMessage(ChatColor.RED + "Этот цвет уже куплен.");
        } else {
            // Попытка покупки
            if (donationManager.purchaseEffect(player, 200, colorName)) {
                player.sendMessage(ChatColor.GREEN + "Вы успешно купили цвет " + colorName + "!");
                player.closeInventory();
            } else {
                player.sendMessage(ChatColor.RED + "У вас недостаточно дублонов для покупки.");
            }
        }
    }
}