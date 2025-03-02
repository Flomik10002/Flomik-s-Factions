package org.flomik.FlomiksFactions.commands.handlers.clanInteractions;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.flomik.FlomiksFactions.clan.Clan;
import org.flomik.FlomiksFactions.clan.managers.ClanManager;

public class BankCommandHandler {
    private final ClanManager clanManager;
    private final Economy economy; // из Vault

    public BankCommandHandler(ClanManager clanManager, Economy economy) {
        this.clanManager = clanManager;
        this.economy = economy;
    }

    public boolean handleCommand(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(ChatColor.YELLOW + "Использование: /clan bank <deposit|withdraw> <сумма>");
            return true;
        }

        String subCommand = args[1].toLowerCase();
        String amountStr = args[2];
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException ex) {
            player.sendMessage(ChatColor.RED + "Некорректная сумма.");
            return true;
        }

        Clan clan = clanManager.getPlayerClan(player.getName());
        if (clan == null) {
            player.sendMessage(ChatColor.RED + "Вы не состоите в клане!");
            return true;
        }

        if (subCommand.equals("deposit")) {
            if (amount <= 0) {
                player.sendMessage(ChatColor.RED + "Сумма должна быть > 0.");
                return true;
            }
            if (!economy.has(player, amount)) {
                player.sendMessage(ChatColor.RED + "У вас нет столько денег!");
                return true;
            }
            economy.withdrawPlayer(player, amount);
            clan.deposit(amount);

            clanManager.saveClan(clan);
            player.sendMessage(ChatColor.GREEN + "Вы внесли " + amount + " в банк клана. Текущий баланс: " + clan.getBalance());

        } else if (subCommand.equals("withdraw")) {
            String role = clan.getRole(player.getName());
            if (!role.equals("Лидер") && !role.equals("Заместитель")) {
                player.sendMessage(ChatColor.RED + "Снимать деньги из банка может только Лидер или Заместитель!");
                return true;
            }

            try {
                clan.withdraw(amount);
                economy.depositPlayer(player, amount);
                clanManager.saveClan(clan);
                player.sendMessage(ChatColor.GREEN + "Вы сняли " + amount + " из банка клана. Остаток: " + clan.getBalance());
            } catch (IllegalArgumentException e) {
                player.sendMessage(ChatColor.RED + e.getMessage());
            }
        } else {
            player.sendMessage(ChatColor.RED + "Неизвестная команда. Используйте /clan bank deposit|withdraw <сумма>");
        }

        return true;
    }
}
