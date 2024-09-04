package org.flomik.flomiksFactions.events;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.flomik.flomiksFactions.FlomiksFactions;
import org.flomik.flomiksFactions.commands.clan.Clan;
import org.flomik.flomiksFactions.commands.clan.ClanManager;

import java.util.HashMap;
import java.util.Map;

public class OnPlayerMoveListener implements Listener {
    private final ClanManager clanManager;
    private final FlomiksFactions plugin;
    private final Map<Player, Boolean> playerInClanTerritory;

    public OnPlayerMoveListener(ClanManager clanManager, FlomiksFactions plugin) {
        this.clanManager = clanManager;
        this.plugin = plugin;
        this.playerInClanTerritory = new HashMap<>();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Chunk chunk = player.getLocation().getChunk();
        String chunkId = getChunkId(chunk);
        boolean isInClanTerritory = false;

        for (Clan clan : clanManager.clans.values()) {
            if (clan.getRegionNames().contains(chunkId)) {
                sendActionBar(player, ChatColor.YELLOW + "Вы находитесь на территории клана " + ChatColor.GOLD + clan.getName());
                isInClanTerritory = true;
                break;
            }
        }

        // Если игрок покидает территорию клана
        if (!isInClanTerritory) {
            if (playerInClanTerritory.getOrDefault(player, false)) {
                clearActionBar(player);
            }
        }

        // Обновляем состояние игрока
        playerInClanTerritory.put(player, isInClanTerritory);
    }

    private String getChunkId(Chunk chunk) {
        return chunk.getWorld().getName() + "_" + chunk.getX() + "_" + chunk.getZ();
    }

    private void sendActionBar(Player player, String message) {
        new BukkitRunnable() {
            @Override
            public void run() {
                player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                        net.md_5.bungee.api.chat.TextComponent.fromLegacyText(message));
            }
        }.runTask(plugin);
    }

    private void clearActionBar(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                        net.md_5.bungee.api.chat.TextComponent.fromLegacyText(""));
            }
        }.runTask(plugin);
    }
}
