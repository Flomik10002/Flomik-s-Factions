package org.flomik.FlomiksFactions.clan.commands.handlers.clanInteractions;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.flomik.FlomiksFactions.clan.nexus.Beacon;
import org.flomik.FlomiksFactions.clan.Clan;
import org.flomik.FlomiksFactions.clan.nexus.BeaconManager;
import org.flomik.FlomiksFactions.clan.managers.ClanManager;
import org.flomik.FlomiksFactions.clan.notifications.ClanNotificationService;
import org.flomik.FlomiksFactions.database.BeaconDao;
import org.flomik.FlomiksFactions.utils.UsageUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Обработчик команды "/clan claim",
 * а также логика установки "маяка" и привата чанка.
 */
public class ClaimRegionHandler {

    private final ClanManager clanManager;
    private final ClanNotificationService clanNotificationService;
    private final BeaconDao beaconDao;
    private final BeaconManager beaconManager;

    public ClaimRegionHandler(ClanManager clanManager,
                              ClanNotificationService clanNotificationService,
                              BeaconDao beaconDao,
                              BeaconManager beaconManager)
    {
        this.clanManager = clanManager;
        this.clanNotificationService = clanNotificationService;
        this.beaconDao = beaconDao;
        this.beaconManager = beaconManager;
    }

    /**
     * Обрабатывает команду "/clan claim".
     */
    public boolean handleCommand(Player sender, String[] args) {
        // Если вообще нет аргументов или 1-й аргумент != claim → подсказка
        if (args.length < 1 || !args[0].equalsIgnoreCase("claim")) {
            UsageUtil.sendUsageMessage(sender, "/clan claim");
            return true;
        }

        Clan clan = clanManager.getPlayerClan(sender.getName());
        if (clan == null) {
            sender.sendMessage(ChatColor.RED + "Вы не состоите в клане.");
            return true;
        }
        if (!"Лидер".equalsIgnoreCase(clan.getRole(sender.getName()))) {
            sender.sendMessage(ChatColor.RED + "Только лидер клана может использовать эту команду.");
            return true;
        }

        // Выдаём "маяк-нексус"
        giveNexusBeacon(sender);
        return true;
    }

    /**
     * Выдаёт игроку маяк-нексус.
     */
    private void giveNexusBeacon(Player player) {
        ItemStack nexusBeacon = new ItemStack(Material.BEACON, 1);
        ItemMeta meta = nexusBeacon.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GREEN + "Нексус");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Приватит чанк клана");
            lore.add(ChatColor.GRAY + "Имеет 5 'hp' против взрывов");
            meta.setLore(lore);
            meta.setCustomModelData(12345);
            nexusBeacon.setItemMeta(meta);
        }
        player.getInventory().addItem(nexusBeacon);
        player.sendMessage(ChatColor.GREEN + "Вы получили Маяк-нексус!");
    }

    /**
     * Логика привата чанка при установке маяка.
     */
    public boolean claimChunkWithBeacon(Player player, Block beaconBlock) {
        Clan clan = clanManager.getPlayerClan(player.getName());
        if (clan == null) {
            player.sendMessage(ChatColor.RED + "Вы не состоите в клане.");
            return false;
        }

        Chunk chunk = beaconBlock.getChunk();
        String chunkId = clanManager.getChunkId(chunk);

        // Запрещаем захватывать святилища
        if (isShrineChunk(chunk)) {
            player.sendMessage(ChatColor.RED + "Этот чанк является точкой Святилища и не может быть приватизирован.");
            return false;
        }
        // Проверяем, не владеем ли уже
        if (clan.hasClaimedChunk(chunkId)) {
            player.sendMessage(ChatColor.YELLOW + "Этот чанк уже принадлежит вашему клану.");
            return false;
        }
        // Достаточно ли силы
        if (clan.getStrength() <= clan.getLands()) {
            player.sendMessage(ChatColor.YELLOW + "У вашего клана недостаточно силы для привата.");
            return false;
        }

        // Проверяем, не принадлежит ли другому клану
        if (clanManager.isClaimedByAnotherClan(chunkId, clan)) {
            // Попытка "захвата"
            return tryOverclaim(player, chunk, chunkId, clan);
        }

        // Иначе просто приватим
        addWorldGuardRegion(chunk, clan, beaconBlock);
        return true;
    }

    /**
     * Пытается "отжать" чанк у другого клана,
     * если у того не хватает силы удержать.
     */
    private boolean tryOverclaim(Player player, Chunk chunk, String chunkId, Clan newOwner) {
        for (Clan oldClan : clanManager.getAllClans()) {
            if (!oldClan.equals(newOwner) && oldClan.hasClaimedChunk(chunkId)) {
                // Если у старого клана lands > strength → можно отжать
                if (oldClan.getLands() > oldClan.getStrength()) {
                    oldClan.removeClaimedChunk(chunkId);
                    clanManager.removeWorldGuardRegion(chunk, oldClan.getName());

                    addWorldGuardRegion(chunk, newOwner, chunk.getBlock(0, 0, 0)); // beaconBlock условно
                    newOwner.addClaimedChunk(chunkId);

                    clanNotificationService.sendClanMessage(newOwner,
                            ChatColor.GREEN + "Игрок " + ChatColor.YELLOW + player.getName() + ChatColor.GREEN
                                    + " захватил территорию клана " + ChatColor.YELLOW + oldClan.getName() + ChatColor.GREEN + "!");
                    clanNotificationService.sendClanMessage(oldClan,
                            ChatColor.RED + "Клан " + ChatColor.GOLD + newOwner.getName()
                                    + ChatColor.RED + " захватил один чанк вашей территории!");
                    return true;
                } else {
                    player.sendMessage(ChatColor.RED + "Этот чанк уже занят кланом " + oldClan.getName() + ".");
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * Создаёт WorldGuard-регион для клана + записывает маяк в БД.
     */
    private void addWorldGuardRegion(Chunk chunk, Clan clan, Block beaconBlock) {
        String regionId = "clan_" + clan.getName() + "_" + clanManager.getChunkId(chunk);

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(chunk.getWorld()));
        if (regions == null) return;

        // Создаём регион
        BlockVector3 min = BlockVector3.at(chunk.getX() << 4, 0, chunk.getZ() << 4);
        BlockVector3 max = BlockVector3.at((chunk.getX() << 4) + 15,
                chunk.getWorld().getMaxHeight(),
                (chunk.getZ() << 4) + 15);
        ProtectedCuboidRegion region = new ProtectedCuboidRegion(regionId, min, max);

        region.setFlag(Flags.INTERACT, StateFlag.State.ALLOW);
        region.setFlag(Flags.PVP, StateFlag.State.ALLOW);
        region.setFlag(Flags.TNT, StateFlag.State.ALLOW);
        region.setFlag(Flags.CREEPER_EXPLOSION, StateFlag.State.DENY);
        region.setFlag(Flags.MOB_SPAWNING, StateFlag.State.DENY);

        regions.addRegion(region);
        addMembersToRegion(clan, region);

        // Добавляем чанк к клану
        clan.addClaimedChunk(clanManager.getChunkId(chunk));

        // Создаём маяк
        beaconDao.insertBeacon(clan, beaconBlock.getLocation(), regionId, 5);
        Beacon beacon = new Beacon(clan.getName(), beaconBlock.getLocation(), 5, regionId);
        beaconManager.addBeacon(beacon);
    }

    /**
     * Добавляет всех участников клана в регион (лидеры/заместители → owner, воины → member).
     */
    private void addMembersToRegion(Clan clan, ProtectedRegion region) {
        for (String memberName : clan.getMembers()) {
            OfflinePlayer offline = Bukkit.getOfflinePlayer(memberName);
            String role = clan.getRole(memberName);

            if ("Лидер".equals(role) || "Заместитель".equals(role)) {
                if (!region.getOwners().contains(offline.getUniqueId())) {
                    DefaultDomain owners = region.getOwners();
                    owners.addPlayer(offline.getUniqueId());
                    region.setOwners(owners);
                }
            } else if ("Воин".equals(role)) {
                if (!region.getMembers().contains(offline.getUniqueId())) {
                    DefaultDomain members = region.getMembers();
                    members.addPlayer(offline.getUniqueId());
                    region.setMembers(members);
                }
            }
        }
    }

    /**
     * Проверяем, не пересекается ли чанк со "Shrine" рег. (пример).
     */
    private boolean isShrineChunk(Chunk chunk) {
        // Текущая проверка: наличие WorldGuard-регионов, начинающихся с "shrine_"
        // если пересекаются с текущим чанк
        World world = chunk.getWorld();
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(world));
        if (regions == null) return false;

        for (ProtectedRegion region : regions.getRegions().values()) {
            if (region.getId().startsWith("shrine_")) {
                // Проверяем пересечение с чанк координатами
                int chunkMinX = chunk.getX() << 4;
                int chunkMinZ = chunk.getZ() << 4;
                int chunkMaxX = chunkMinX + 15;
                int chunkMaxZ = chunkMinZ + 15;

                BlockVector3 min = region.getMinimumPoint();
                BlockVector3 max = region.getMaximumPoint();

                boolean intersect = (min.getX() <= chunkMaxX && max.getX() >= chunkMinX)
                        && (min.getZ() <= chunkMaxZ && max.getZ() >= chunkMinZ);

                if (intersect) {
                    return true;
                }
            }
        }
        return false;
    }

}