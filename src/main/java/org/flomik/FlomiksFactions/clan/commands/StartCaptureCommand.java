package org.flomik.FlomiksFactions.clan.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.flomik.FlomiksFactions.clan.Clan;
import org.flomik.FlomiksFactions.clan.managers.ClanManager;
import org.flomik.FlomiksFactions.clan.nexus.Beacon;
import org.flomik.FlomiksFactions.clan.nexus.BeaconCaptureManager;
import org.flomik.FlomiksFactions.clan.nexus.BeaconManager;

public class StartCaptureCommand implements CommandExecutor {

    private final BeaconCaptureManager captureManager;
    private final BeaconManager beaconManager;
    private final ClanManager clanManager;

    public StartCaptureCommand(BeaconCaptureManager captureManager, BeaconManager beaconManager, ClanManager clanManager) {
        this.captureManager = captureManager;
        this.beaconManager = beaconManager;
        this.clanManager = clanManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Использование: /startcapture <атакующий_клан> <цель_клан>");
            return true;
        }

        String attackingName = args[0];
        String defendingName = args[1];

        Clan attacking = clanManager.getClan(attackingName);
        Clan defending = clanManager.getClan(defendingName);

        if (attacking == null || defending == null) {
            sender.sendMessage(ChatColor.RED + "Один из кланов не существует.");
            return true;
        }

        // Ищем маяк защищающего клана
        Beacon beacon = beaconManager.getAllBeacons().stream()
                .filter(b -> b.getClanName().equalsIgnoreCase(defending.getName()))
                .findFirst().orElse(null);

        if (beacon == null) {
            sender.sendMessage(ChatColor.RED + "У клана " + defending.getName() + " нет маяка.");
            return true;
        }

        if (beacon.getHealth() > 0) {
            sender.sendMessage(ChatColor.RED + "Маяк еще не разрушен. Текущее HP: " + beacon.getHealth());
            return true;
        }

        // Проверяем наличие хотя бы одного атакующего игрока в чанке маяка
        Chunk chunk = beacon.getLocation().getChunk();
        boolean attackerInChunk = chunk.getWorld().getPlayers().stream()
                .filter(p -> p.getLocation().getChunk().equals(chunk))
                .anyMatch(p -> {
                    Clan c = clanManager.getPlayerClan(p.getName());
                    return c != null && c.equals(attacking);
                });

        if (!attackerInChunk) {
            sender.sendMessage(ChatColor.RED + "В чанке маяка нет ни одного игрока атакующего клана.");
            return true;
        }

        // Всё готово — начинаем захват
        captureManager.manualStartCapture(beacon, defending, attacking);
        sender.sendMessage(ChatColor.GREEN + "Захват маяка клана " + defending.getName() + " начат!");
        return true;
    }
}