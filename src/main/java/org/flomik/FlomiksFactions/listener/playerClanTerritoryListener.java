package org.flomik.FlomiksFactions.listener; //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.flomik.FlomiksFactions.FlomiksFactions;
import org.flomik.FlomiksFactions.clan.Clan;
import org.flomik.FlomiksFactions.clan.managers.ClanManager;

import java.util.HashMap;
import java.util.Map;

public class playerClanTerritoryListener implements Listener { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private final ClanManager clanManager; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private final FlomiksFactions plugin; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private final Map<Player, Boolean> playerInClanTerritory; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression

    public playerClanTerritoryListener(ClanManager clanManager, FlomiksFactions plugin) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        this.clanManager = clanManager;
        this.plugin = plugin;
        this.playerInClanTerritory = new HashMap<>();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        Player player = event.getPlayer(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        Chunk chunk = player.getLocation().getChunk(); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
        String chunkId = getChunkId(chunk); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        boolean isInClanTerritory = false;

        for (Clan clan : clanManager.clans.values()) { //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
            if (clan.getRegionNames().contains(chunkId)) { //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
                sendActionBar(player, ChatColor.YELLOW + "Вы находитесь на территории клана " + ChatColor.GOLD + clan.getName());
                isInClanTerritory = true;
                break;
            }
        }

        if (!isInClanTerritory) {
            if (playerInClanTerritory.getOrDefault(player, false)) { //NOPMD - suppressed CollapsibleIfStatements - TODO explain reason for suppression //NOPMD - suppressed CollapsibleIfStatements - TODO explain reason for suppression //NOPMD - suppressed CollapsibleIfStatements - TODO explain reason for suppression
                clearActionBar(player);
            }
        }

        playerInClanTerritory.put(player, isInClanTerritory);
    }

    private String getChunkId(Chunk chunk) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        return chunk.getWorld().getName() + "_" + chunk.getX() + "_" + chunk.getZ();
    }

    private void sendActionBar(Player player, String message) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        new BukkitRunnable() {
            @Override
            public void run() {
                player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                        net.md_5.bungee.api.chat.TextComponent.fromLegacyText(message));
            }
        }.runTask(plugin);
    }

    private void clearActionBar(Player player) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        new BukkitRunnable() {
            @Override
            public void run() {
                player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                        net.md_5.bungee.api.chat.TextComponent.fromLegacyText(""));
            }
        }.runTask(plugin);
    }
}
