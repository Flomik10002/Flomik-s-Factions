package org.flomik.FlomiksFactions.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.flomik.FlomiksFactions.clan.nexus.Beacon;
import org.flomik.FlomiksFactions.clan.nexus.BeaconManager;

public class BeaconProtectionListener implements Listener {

    private final BeaconManager beaconManager;

    public BeaconProtectionListener(BeaconManager beaconManager) {
        this.beaconManager = beaconManager;
    }

    @EventHandler
    public void onBeaconBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.BEACON) return;

        // Проверяем: есть ли маяк с такой локацией
        Beacon beacon = beaconManager.getBeaconByLocation(block.getLocation());
        if (beacon != null) {
            event.setCancelled(true);
            Player player = event.getPlayer();
            player.sendMessage("§cМаяк нельзя ломать вручную. Он может быть удалён только при снятии привата клана.");
        }
    }
}
