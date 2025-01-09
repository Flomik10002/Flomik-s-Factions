package org.flomik.FlomiksFactions.commands.handlers.playerInteractions;

import org.bukkit.entity.Player;
import org.flomik.FlomiksFactions.clan.managers.ChunkMenuManager;

public class MapHandler {

    private final ChunkMenuManager chunkMenuManager;

    public MapHandler(ChunkMenuManager chunkMenuManager) {
        this.chunkMenuManager = chunkMenuManager;
    }

    public boolean handleCommand(Player player) {
        chunkMenuManager.openChunkMenu(player);
        return true;
    }
}