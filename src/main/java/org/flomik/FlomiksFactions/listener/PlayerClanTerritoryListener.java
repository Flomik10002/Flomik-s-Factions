package org.flomik.FlomiksFactions.listener;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.flomik.FlomiksFactions.clan.Clan;
import org.flomik.FlomiksFactions.clan.managers.ClanManager;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerClanTerritoryListener implements Listener {
    private final ClanManager clanManager;
    private final Map<Player, Boolean> playerInClanTerritory = new ConcurrentHashMap<>();

    public PlayerClanTerritoryListener(ClanManager clanManager) {
        this.clanManager = clanManager;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Chunk chunk = player.getLocation().getChunk();
        String chunkId = getChunkId(chunk);

        Optional<Clan> clanOptional = Optional.ofNullable(clanManager.getClanByChunk(chunk));
        boolean isInClanTerritory = clanOptional.isPresent();

        clanOptional.ifPresentOrElse(
                clan -> sendActionBar(player, ChatColor.YELLOW + "Вы находитесь на территории клана " + ChatColor.GOLD + clan.getName()),
                () -> {
                    if (playerInClanTerritory.getOrDefault(player, false)) {
                        sendActionBar(player, "");
                    }
                }
        );

        playerInClanTerritory.put(player, isInClanTerritory);
    }

    private String getChunkId(Chunk chunk) {
        return chunk.getWorld().getName() + "_" + chunk.getX() + "_" + chunk.getZ();
    }

    private void sendActionBar(Player player, String message) {
        player.spigot().sendMessage(
                net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                net.md_5.bungee.api.chat.TextComponent.fromLegacyText(message)
        );
    }
}
