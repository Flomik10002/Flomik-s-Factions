package org.flomik.flomiksFactions.commands.clan.handlers.clanInteractions;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.flomik.flomiksFactions.commands.clan.Clan;
import org.flomik.flomiksFactions.commands.clan.ClanManager;

import java.util.Map;

public class UnclaimRegionCommandHandler {

    private final ClanManager clanManager;

    public UnclaimRegionCommandHandler(ClanManager clanManager) {
        this.clanManager = clanManager;
    }

    // Основной метод обработки команды
    public boolean handleCommand(Player player, String[] args) {
        Clan clan = clanManager.getPlayerClan(player.getName());

        if (clan == null || !isLeaderOrDeputy(player, clan)) {
            player.sendMessage(ChatColor.RED + "У вас нет прав для выполнения этой команды.");
            return true;
        }

        if (args.length > 1 && args[1].equalsIgnoreCase("all")) {
            // Если аргумент "all" указан, снимаем все приваты
            unclaimAllRegionsForClan(player, clan);
        } else {
            // В противном случае, снимаем приват только с текущего чанка
            unclaimCurrentChunk(player, clan);
        }

        return true;
    }

    // Снятие привата с текущего чанка
    private void unclaimCurrentChunk(Player player, Clan clan) {
        Chunk chunk = player.getLocation().getChunk();
        String chunkId = getChunkId(chunk);

        // Проверка, принадлежит ли чанк клану
        if (!clan.hasClaimedChunk(chunkId)) {
            player.sendMessage(ChatColor.RED + "Этот чанк не принадлежит вашему клану.");
            return;
        }

        // Удаляем регион WorldGuard
        removeWorldGuardRegion(chunk, clan.getName());

        // Проверка, находится ли точка дома в удаляемом регионе
        if (isHomeInChunk(clan, chunk)) {
            clan.removeHome();
            player.sendMessage(ChatColor.YELLOW + "Точка дома была удалена, так как она находилась в этом привате.");
        }

        // Убираем чанк из списка клана
        clan.removeClaimedChunk(chunkId);
        clanManager.sendClanMessage(clan, ChatColor.GREEN + "Игрок " + ChatColor.YELLOW + player.getName() + ChatColor.GREEN + " убрал приват с чанка!");
    }

    // Снятие привата со всех чанков клана
    private void unclaimAllRegionsForClan(Player player, Clan clan) {
        WorldGuard wg = WorldGuard.getInstance();
        RegionContainer container = wg.getPlatform().getRegionContainer();

        for (World world : player.getServer().getWorlds()) {
            RegionManager regions = container.get(BukkitAdapter.adapt(world));

            if (regions != null) {
                for (Map.Entry<String, ProtectedRegion> entry : regions.getRegions().entrySet()) {
                    ProtectedRegion region = entry.getValue();

                    // Проверяем, является ли регион частью клана и если игрок - лидер
                    if (region.getId().startsWith("clan_" + clan.getName()) && isLeaderOrDeputy(player, clan)) {
                        regions.removeRegion(region.getId());

                        // Проверяем, находится ли точка дома в одном из удаляемых приватов
                        Location homeLocation = clan.getHome();
                        if (homeLocation != null && isHomeInRegion(region, homeLocation)) {
                            clan.removeHome();
                            clanManager.sendClanMessage(clan,ChatColor.YELLOW + "Точка дома была удалена, так как она находилась в одном из удалённых приватов.");
                        }
                    }
                }
                clan.clearClaimedChunks();
            }
        }
        clanManager.sendClanMessage(clan, ChatColor.GREEN + "Игрок " + ChatColor.YELLOW + player.getName() + ChatColor.GREEN + " убрал все приваты!");
    }

    // Проверка роли игрока
    private boolean isLeaderOrDeputy(Player player, Clan clan) {
        String playerRole = clan.getRole(player.getName());
        return playerRole.equals("Лидер") || playerRole.equals("Заместитель");
    }

    // Формирование идентификатора чанка
    private String getChunkId(Chunk chunk) {
        return chunk.getWorld().getName() + "_" + chunk.getX() + "_" + chunk.getZ();
    }

    // Удаление региона WorldGuard
    public void removeWorldGuardRegion(Chunk chunk, String clanName) {
        WorldGuard wg = WorldGuard.getInstance();
        RegionContainer container = wg.getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(chunk.getWorld()));
        String regionId = "clan_" + clanName + "_" + getChunkId(chunk);
        if (regions != null) {
            regions.removeRegion(regionId);
        }
    }

    // Проверка, находится ли точка дома в текущем чанке
    private boolean isHomeInChunk(Clan clan, Chunk chunk) {
        Location homeLocation = clan.getHome();
        if (homeLocation == null) {
            return false; // Точка дома не установлена
        }

        return homeLocation.getChunk().equals(chunk);
    }

    // Проверка, находится ли точка дома в удаляемом регионе
    private boolean isHomeInRegion(ProtectedRegion region, Location homeLocation) {
        String regionId = region.getId();
        String homeChunkId = getChunkId(homeLocation.getChunk());
        return regionId.contains(homeChunkId);
    }
}
