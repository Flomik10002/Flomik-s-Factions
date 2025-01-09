package org.flomik.FlomiksFactions.claim;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.flomik.FlomiksFactions.clan.Clan;
import org.flomik.FlomiksFactions.clan.managers.ClanManager;

public class BeaconPlaceListener implements Listener {

    private final ClanManager clanManager;
    private final ClaimService claimService;

    public BeaconPlaceListener(ClanManager clanManager, ClaimService claimService) {
        this.clanManager = clanManager;
        this.claimService = claimService;
    }

    @EventHandler
    public void onBeaconPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.BEACON) {
            return;
        }

        Player player = event.getPlayer();
        Clan clan = clanManager.getPlayerClan(player.getName());
        if (clan == null) {
            player.sendMessage(ChatColor.RED + "Вы не состоите в клане, маяк не будет установлен.");
            event.setCancelled(true);
            return;
        }

        String role = clan.getRole(player.getName());
        if (!role.equals("Лидер")) {
            player.sendMessage(ChatColor.RED + "Только лидер клана может устанавливать маяк.");
            event.setCancelled(true);
            return;
        }

        Chunk chunk = block.getChunk();
        boolean success = claimService.claimChunkWithBeacon(player, clan, chunk);

        if (!success) {
            event.setCancelled(true);
        }
    }
}
