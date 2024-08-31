package org.flomik.flomiksFactions.commands.clan.handlers.clanInteractions;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.flomik.flomiksFactions.commands.clan.Clan;
import org.flomik.flomiksFactions.commands.clan.ClanManager;

public class ClaimRegionCommandHandler {

    private final ClanManager clanManager;

    public ClaimRegionCommandHandler(ClanManager clanManager) {
        this.clanManager = clanManager;
    }

    public boolean handleCommand(Player player) {
        Clan clan = clanManager.getPlayerClan(player.getName());

        if (clan == null || !isLeaderOrDeputy(player, clan)) {
            player.sendMessage(ChatColor.RED + "У вас нет прав для выполнения этой команды.");
            return true;
        }

        Chunk chunk = player.getLocation().getChunk();
        String chunkId = getChunkId(chunk);

        // Проверка, не занят ли чанк другим кланом
        if (isChunkClaimedByAnotherClan(chunkId, clan)) {
            player.sendMessage(ChatColor.RED + "Этот чанк уже занят другим кланом.");
            return true;
        }

        if (isChunkClaimed(chunkId, clan)) {
            player.sendMessage(ChatColor.YELLOW + "Этот чанк уже занят вашим кланом.");
            return true;
        }

        // Добавляем регион WorldGuard
        addWorldGuardRegion(chunk, clan.getName());

        // Добавляем чанк к клану
        clan.addClaimedChunk(chunkId);
        player.sendMessage(ChatColor.GREEN + "Чанк успешно занят вашим кланом!");
        return true;
    }

    private boolean isLeaderOrDeputy(Player player, Clan clan) {
        String playerRole = clan.getRole(player.getName());
        return playerRole.equals("Лидер") || playerRole.equals("Заместитель");
    }

    private String getChunkId(Chunk chunk) {
        // Используем подчеркивания вместо двоеточий
        return chunk.getWorld().getName() + "_" + chunk.getX() + "_" + chunk.getZ();
    }

    private boolean isChunkClaimedByAnotherClan(String chunkId, Clan currentClan) {
        for (Clan clan : clanManager.clans.values()) {
            if (!clan.equals(currentClan) && clan.hasClaimedChunk(chunkId)) {
                return true;
            }
        }
        return false;
    }

    private boolean isChunkClaimed(String chunkId, Clan currentClan) {
        for (Clan clan : clanManager.clans.values()) {
            if (clan.equals(currentClan) && clan.hasClaimedChunk(chunkId)) {
                return true;
            }
        }
        return false;
    }

    private void addWorldGuardRegion(Chunk chunk, String clanName) {
        WorldGuard wg = WorldGuard.getInstance();
        RegionContainer container = wg.getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(chunk.getWorld()));

        // Изменяем формат идентификатора региона, убираем двоеточие и используем подчеркивания
        String regionId = "clan_" + clanName + "_" + chunk.getWorld().getName() + "_" + chunk.getX() + "_" + chunk.getZ();

        BlockVector3 min = BlockVector3.at(chunk.getX() << 4, 0, chunk.getZ() << 4);
        BlockVector3 max = BlockVector3.at((chunk.getX() << 4) + 15, chunk.getWorld().getMaxHeight(), (chunk.getZ() << 4) + 15);
        ProtectedCuboidRegion region = new ProtectedCuboidRegion(regionId, min, max);

        // Устанавливаем флаги региона
        region.setFlag(Flags.BUILD, StateFlag.State.ALLOW);

        if (regions != null) {
            regions.addRegion(region);
        }
    }
}
