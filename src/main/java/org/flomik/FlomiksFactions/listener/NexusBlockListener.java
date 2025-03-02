package org.flomik.FlomiksFactions.listener;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.flomik.FlomiksFactions.clan.Clan;
import org.flomik.FlomiksFactions.clan.managers.ClanManager;
import org.flomik.FlomiksFactions.commands.handlers.clanInteractions.ClaimRegionHandler;

public class NexusBlockListener implements Listener {

    private final ClaimRegionHandler claimRegionHandler;
    private final ClanManager clanManager;

    public NexusBlockListener(ClaimRegionHandler claimRegionHandler, ClanManager clanManager) {
        this.claimRegionHandler = claimRegionHandler;
        this.clanManager = clanManager;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getItemInHand().getType() != Material.BEACON) {
            return;
        }

        ItemMeta meta = event.getItemInHand().getItemMeta();
        if (meta == null || !meta.hasCustomModelData() || meta.getCustomModelData() != 12345) {
            return;
        }

        Player player = event.getPlayer();
        Clan clan = clanManager.getPlayerClan(player.getName());
        if (clan == null || !clan.getRole(player.getName()).equalsIgnoreCase("Лидер") && !clan.getRole(player.getName()).equalsIgnoreCase("Заместитель")) {
            player.sendMessage(ChatColor.RED + "Только лидер или заместитель клана может устанавливать этот маяк.");
            event.setCancelled(true);
            return;
        }

        Block placedBlock = event.getBlockPlaced();
        boolean success = claimRegionHandler.claimChunkWithBeacon(player, placedBlock);
        if (success) {
            player.sendMessage(ChatColor.GREEN + "Чанк успешно заприватен Маяком-нексусом!");
        } else {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Не удалось заприватить чанк.");
        }
    }
}
