package org.flomik.flomiksFactions.menu.chunkMenu;

import org.bukkit.Chunk;
import org.flomik.flomiksFactions.clan.Clan;
import org.flomik.flomiksFactions.clan.ClanManager;

public class ChunkUtils {

    public static String getChunkOwner(Chunk chunk, ClanManager clanManager) {
        String chunkId = getChunkId(chunk);

        for (Clan clan : clanManager.getAllClans()) {
            if (clan.hasClaimedChunk(chunkId)) {
                return clan.getName();
            }
        }
        return null;
    }

    private static String getChunkId(Chunk chunk) {
        return chunk.getWorld().getName() + "_" + chunk.getX() + "_" + chunk.getZ();
    }
}
