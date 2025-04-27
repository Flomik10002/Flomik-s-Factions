package org.flomik.FlomiksFactions.clan.nexus;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.flomik.FlomiksFactions.clan.Clan;
import org.flomik.FlomiksFactions.clan.commands.handlers.clanInteractions.ClaimRegionHandler;
import org.flomik.FlomiksFactions.clan.managers.ClanManager;

/**
 * Слушатель события установки (placement) блока.
 * Если блок — "маяк-нексус", то вызываем логику привата чанка.
 */
public class NexusBlockListener implements Listener {

    private final ClaimRegionHandler claimRegionHandler;
    private final ClanManager clanManager;

    /**
     * @param claimRegionHandler Содержит логику захвата чанка при установке маяка.
     * @param clanManager        Позволяет проверить, является ли игрок лидером/заместителем.
     */
    public NexusBlockListener(ClaimRegionHandler claimRegionHandler, ClanManager clanManager) {
        this.claimRegionHandler = claimRegionHandler;
        this.clanManager = clanManager;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        // Проверяем, является ли установленный блок маяком
        if (event.getItemInHand().getType() != Material.BEACON) {
            return;
        }
        // Проверяем, есть ли нужная modelData (12345)
        ItemMeta meta = event.getItemInHand().getItemMeta();
        if (meta == null || !meta.hasCustomModelData() || meta.getCustomModelData() != 12345) {
            return;
        }

        // Проверяем, что игрок — лидер или заместитель клана
        Player player = event.getPlayer();
        Clan clan = clanManager.getPlayerClan(player.getName());
        if (!clanManager.isLeaderOrDeputy(clan, player)) {
            player.sendMessage(ChatColor.RED + "Только лидер или заместитель клана может устанавливать этот маяк.");
            event.setCancelled(true);
            return;
        }

        // Пытаемся "захватить чанк" через ClaimRegionHandler
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
