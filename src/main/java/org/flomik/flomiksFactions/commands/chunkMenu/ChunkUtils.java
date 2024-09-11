package org.flomik.flomiksFactions.commands.chunkMenu;

import org.bukkit.Chunk;
import org.flomik.flomiksFactions.commands.clan.Clan;
import org.flomik.flomiksFactions.commands.clan.ClanManager;

public class ChunkUtils {

    // Метод для поиска клана, который владеет данным чанком
    public static String getChunkOwner(Chunk chunk, ClanManager clanManager) {
        String chunkId = getChunkId(chunk);

        // Проходим по всем кланам и ищем, кто владеет этим чанком
        for (Clan clan : clanManager.getAllClans()) {
            if (clan.hasClaimedChunk(chunkId)) {
                return clan.getName();  // Возвращаем имя клана, который владеет чанком
            }
        }

        return null;  // Если ни один клан не владеет чанком, возвращаем null
    }

    // Генерируем идентификатор чанка
    private static String getChunkId(Chunk chunk) {
        return chunk.getWorld().getName() + "_" + chunk.getX() + "_" + chunk.getZ();
    }
}
