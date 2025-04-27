package org.flomik.FlomiksFactions.clan.commands.handlers.clanInteractions;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.flomik.FlomiksFactions.clan.Clan;
import org.flomik.FlomiksFactions.clan.nexus.BeaconManager;
import org.flomik.FlomiksFactions.clan.managers.ClanManager;
import org.flomik.FlomiksFactions.clan.notifications.ClanNotificationService;
import org.flomik.FlomiksFactions.database.BeaconDao;
import org.flomik.FlomiksFactions.utils.UsageUtil;

/**
 * Обработчик команды "/clan unclaim" (или "/clan unclaim all"),
 * а также логика снятия привата с чанка/всех чанков.
 */
public class UnclaimRegionHandler {

    private final ClanManager clanManager;
    private final ClanNotificationService clanNotificationService;
    private final BeaconDao beaconDao;
    private final BeaconManager beaconManager;

    public UnclaimRegionHandler(ClanManager clanManager,
                                ClanNotificationService clanNotificationService,
                                BeaconDao beaconDao,
                                BeaconManager beaconManager) {
        this.clanManager = clanManager;
        this.clanNotificationService = clanNotificationService;
        this.beaconDao = beaconDao;
        this.beaconManager = beaconManager;
    }

    /**
     * /clan unclaim [all]
     */
    public boolean handleCommand(Player player, String[] args) {
        Clan clan = clanManager.getPlayerClan(player.getName());
        if (!clanManager.isLeaderOrDeputy(clan, player)) {
            player.sendMessage(ChatColor.RED + "У вас нет прав для выполнения этой команды или вы не в клане.");
            return true;
        }

        // Если 2й арг "all", снимаем все приваты
        if (args.length > 1 && "all".equalsIgnoreCase(args[1])) {
            unclaimAllRegionsForClan(player, clan);
        } else if (args.length == 1) {
            // unclaim текущий чанк
            unclaimCurrentChunk(player, clan);
        } else {
            // лишний аргумент → подсказка
            UsageUtil.sendUsageMessage(player, "/clan unclaim [all]");
        }
        return true;
    }

    /**
     * Снимаем приват с текущего чанка (где стоит игрок).
     */
    private void unclaimCurrentChunk(Player player, Clan clan) {
        Chunk chunk = player.getLocation().getChunk();
        String chunkId = clanManager.getChunkId(chunk);

        if (!clan.hasClaimedChunk(chunkId)) {
            player.sendMessage(ChatColor.RED + "Этот чанк не принадлежит вашему клану.");
            return;
        }

        clanManager.removeWorldGuardRegion(chunk, clan.getName());

        // Удаляем маяк из БД/менеджера
        String regionId = "clan_" + clan.getName() + "_" + chunkId;
        beaconDao.deleteBeaconByRegionId(regionId);
        beaconManager.removeBeacon(regionId);

        // Если home был в этом чанке, убираем
        if (isHomeInChunk(clan, chunk)) {
            clan.removeHome();
            player.sendMessage(ChatColor.YELLOW + "Точка дома была удалена, так как находилась в этом привате.");
        }

        clan.removeClaimedChunk(chunkId);
        clanNotificationService.sendClanMessage(clan,
                ChatColor.GREEN + "Игрок " + ChatColor.YELLOW + player.getName()
                        + ChatColor.GREEN + " убрал приват с чанка!");
    }

    /**
     * Снимаем приват со всех чанков клана.
     */
    private void unclaimAllRegionsForClan(Player player, Clan clan) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();

        for (World world : player.getServer().getWorlds()) {
            RegionManager regions = container.get(BukkitAdapter.adapt(world));
            if (regions == null) continue;

            // Ищем регионы, начинающиеся на "clan_<Имя>"
            // Удаляем их
            regions.getRegions().entrySet().removeIf(entry -> {
                String regionId = entry.getKey();
                ProtectedRegion region = entry.getValue();
                if (regionId.startsWith("clan_" + clan.getName().toLowerCase())) {
                    // Удаляем маяк
                    beaconDao.deleteBeaconByRegionId(regionId);
                    beaconManager.removeBeacon(regionId);
                    return true; // удаляем из Map
                }
                return false; // не удаляем
            });
        }

        // Удаляем все чанки у клана
        clan.clearClaimedChunks();

        // Если Home в удалённом чанке, убираем
        if (clan.getHome() != null) {
            clan.removeHome();
            clanNotificationService.sendClanMessage(clan,
                    ChatColor.YELLOW + "Точка дома была удалена, так как находилась в одном из удалённых приватов.");
        }

        clanNotificationService.sendClanMessage(clan, ChatColor.GREEN + "Игрок " + ChatColor.YELLOW + player.getName()
                + ChatColor.GREEN + " убрал все приваты!");
    }

    private boolean isHomeInChunk(Clan clan, Chunk chunk) {
        Location homeLoc = clan.getHome();
        return homeLoc != null && homeLoc.getChunk().equals(chunk);
    }
}
