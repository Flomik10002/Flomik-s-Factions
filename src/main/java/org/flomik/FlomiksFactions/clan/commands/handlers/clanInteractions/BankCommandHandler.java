package org.flomik.FlomiksFactions.clan.commands.handlers.clanInteractions;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.flomik.FlomiksFactions.clan.Clan;
import org.flomik.FlomiksFactions.clan.managers.ClanManager;
import org.flomik.FlomiksFactions.utils.UsageUtil;

/**
 * Команда /clan bank <deposit|withdraw> <сумма>
 */
public class BankCommandHandler {

    private final ClanManager clanManager;
    private final Economy economy; // из Vault

    public BankCommandHandler(ClanManager clanManager, Economy economy) {
        this.clanManager = clanManager;
        this.economy = economy;
    }

    /**
     * /clan bank <deposit|withdraw> <сумма>
     */
    public boolean handleCommand(Player player, String[] args) {
        // Если нет нужного количества аргументов => подсказка
        if (args.length < 3) {
            UsageUtil.sendUsageMessage(player, "/clan bank <deposit|withdraw> <сумма>");
            return true;
        }

        // Второй аргумент: "deposit" или "withdraw"
        String subCommand = args[1].toLowerCase();
        // Третий аргумент: сумма
        String amountStr = args[2];

        // Парсим сумму
        double amount = parseAmount(player, amountStr);
        if (amount < 0) {
            // parseAmount вернул -1, если число некорректно
            return true;
        }

        // Проверяем, что игрок в клане
        Clan clan = clanManager.getPlayerClan(player.getName());
        if (clan == null) {
            player.sendMessage(ChatColor.RED + "Вы не состоите в клане!");
            return true;
        }

        switch (subCommand) {
            case "deposit":
                handleDeposit(player, clan, amount);
                break;
            case "withdraw":
                handleWithdraw(player, clan, amount);
                break;
            default:
                player.sendMessage(ChatColor.RED + "Неизвестная команда!");
                UsageUtil.sendUsageMessage(player, "/clan bank <deposit|withdraw> <сумма>");
                break;
        }
        return true;
    }

    /**
     * Обработка пополнения банка.
     */
    private void handleDeposit(Player player, Clan clan, double amount) {
        if (amount <= 0) {
            player.sendMessage(ChatColor.RED + "Сумма должна быть > 0.");
            return;
        }
        if (!economy.has(player, amount)) {
            player.sendMessage(ChatColor.RED + "У вас нет столько денег!");
            return;
        }

        // Списываем у игрока, добавляем в банк клана
        economy.withdrawPlayer(player, amount);
        clan.deposit(amount);
        clanManager.saveClan(clan);

        player.sendMessage(ChatColor.GREEN + "Вы внесли " + amount
                + " в банк клана. Текущий баланс: " + clan.getBalance());
    }

    /**
     * Обработка снятия денег из банка.
     */
    private void handleWithdraw(Player player, Clan clan, double amount) {
        // Проверяем, что игрок - лидер или заместитель
        if (!clanManager.isLeaderOrDeputy(clan, player)) {
            player.sendMessage(ChatColor.RED + "Снимать деньги из банка может только Лидер или Заместитель!");
            return;
        }
        if (amount <= 0) {
            player.sendMessage(ChatColor.RED + "Сумма должна быть > 0.");
            return;
        }

        try {
            clan.withdraw(amount);
            economy.depositPlayer(player, amount);
            clanManager.saveClan(clan);

            player.sendMessage(ChatColor.GREEN + "Вы сняли " + amount
                    + " из банка клана. Остаток: " + clan.getBalance());
        } catch (IllegalArgumentException e) {
            player.sendMessage(ChatColor.RED + e.getMessage());
        }
    }

    /**
     * Парсит сумму. Если ошибка - выводит сообщение
     * и возвращает -1.
     */
    private double parseAmount(Player player, String amountStr) {
        try {
            return Double.parseDouble(amountStr);
        } catch (NumberFormatException ex) {
            player.sendMessage(ChatColor.RED + "Некорректная сумма: " + amountStr);
            return -1;
        }
    }
}
