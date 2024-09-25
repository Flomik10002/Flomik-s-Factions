package org.flomik.flomiksFactions.clan.commands.clanInteractions;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.flomik.flomiksFactions.FlomiksFactions;
import org.flomik.flomiksFactions.clan.Clan;
import org.flomik.flomiksFactions.clan.ClanManager;

import java.util.List;

public class ClanChunksCommand implements CommandExecutor {

    private final FlomiksFactions plugin;
    private final ClanManager clanManager;

    public ClanChunksCommand(FlomiksFactions plugin, ClanManager clanManager) {
        this.plugin = plugin;
        this.clanManager = clanManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player player = (Player) sender;

        // Получаем клан игрока
        Clan playerClan = clanManager.getPlayerClan(player.getName());
        if (playerClan == null) {
            player.sendMessage(ChatColor.RED + "Вы не состоите в клане.");
            return true;
        }

        // Получаем список всех чанков клана
        List<String> claimedChunks = playerClan.getRegionNames();
        if (claimedChunks.isEmpty()) {
            player.sendMessage(ChatColor.RED + "Ваш клан не владеет никакими чанками.");
            return true;
        }

        // Выводим заголовок в чат
        player.sendMessage(ChatColor.GREEN + "Чанки, принадлежащие вашему клану " + ChatColor.YELLOW + playerClan.getName() + ChatColor.GREEN + ":");

        // Проходим по каждому чанку и выводим информацию в чат
        for (String chunkId : claimedChunks) {
            // Пример: world_12_34
            String[] parts = chunkId.split("_");
            if (parts.length < 3) continue;

            String worldName = parts[0];
            int chunkX = Integer.parseInt(parts[1]);
            int chunkZ = Integer.parseInt(parts[2]);

            // Формируем сообщение для каждого чанка
            player.sendMessage(ChatColor.GREEN + "Мир: " + ChatColor.YELLOW + worldName + ChatColor.GREEN + " Координаты чанка: X: " + ChatColor.YELLOW + chunkX + ChatColor.GREEN + " Z: " + ChatColor.YELLOW + chunkZ);
        }

        return true;
    }
}
