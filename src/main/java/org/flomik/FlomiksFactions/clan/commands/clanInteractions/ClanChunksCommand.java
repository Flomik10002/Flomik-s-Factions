package org.flomik.FlomiksFactions.clan.commands.clanInteractions;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.flomik.FlomiksFactions.FlomiksFactions;
import org.flomik.FlomiksFactions.clan.Clan;
import org.flomik.FlomiksFactions.clan.ClanManager;

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


        Clan playerClan = clanManager.getPlayerClan(player.getName());
        if (playerClan == null) {
            player.sendMessage(ChatColor.RED + "Вы не состоите в клане.");
            return true;
        }


        List<String> claimedChunks = playerClan.getRegionNames();
        if (claimedChunks.isEmpty()) {
            player.sendMessage(ChatColor.RED + "Ваш клан не владеет никакими чанками.");
            return true;
        }


        player.sendMessage(ChatColor.GREEN + "Чанки, принадлежащие вашему клану " + ChatColor.YELLOW + playerClan.getName() + ChatColor.GREEN + ":");


        for (String chunkId : claimedChunks) {

            String[] parts = chunkId.split("_");
            if (parts.length < 3) continue;

            String worldName = parts[0];
            int chunkX = Integer.parseInt(parts[1]);
            int chunkZ = Integer.parseInt(parts[2]);


            player.sendMessage(ChatColor.GREEN + "Мир: " + ChatColor.YELLOW + worldName + ChatColor.GREEN + " Координаты чанка: X: " + ChatColor.YELLOW + chunkX + ChatColor.GREEN + " Z: " + ChatColor.YELLOW + chunkZ);
        }

        return true;
    }
}
