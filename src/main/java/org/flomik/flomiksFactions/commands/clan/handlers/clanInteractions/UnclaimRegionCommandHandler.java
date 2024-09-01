package org.flomik.flomiksFactions.commands.clan.handlers.clanInteractions;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.flomik.flomiksFactions.commands.clan.Clan;
import org.flomik.flomiksFactions.commands.clan.ClanManager;

public class UnclaimRegionCommandHandler {

    private final ClanManager clanManager;

    public UnclaimRegionCommandHandler(ClanManager clanManager) {
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

        // Проверка, занят ли чанк этим кланом
        if (!clan.hasClaimedChunk(chunkId)) {
            player.sendMessage(ChatColor.RED + "Этот чанк не принадлежит вашему клану.");
            return true;
        }

        // Удаляем регион WorldGuard
        removeWorldGuardRegion(chunk, clan.getName());

        // Убираем чанк из клана
        clan.removeClaimedChunk(chunkId);
        player.sendMessage(ChatColor.GREEN + "Чанк успешно распривачен.");
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

    public void removeWorldGuardRegion(Chunk chunk, String clanName) {
        WorldGuard wg = WorldGuard.getInstance();
        RegionContainer container = wg.getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(chunk.getWorld()));
        String regionId = "clan_" + clanName + "_" + getChunkId(chunk);
        regions.removeRegion(regionId);
    }
}
