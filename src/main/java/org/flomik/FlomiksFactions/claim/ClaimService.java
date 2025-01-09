package org.flomik.FlomiksFactions.claim;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.flomik.FlomiksFactions.clan.Clan;
import org.flomik.FlomiksFactions.clan.managers.ClanManager;

import java.util.Map;

public class ClaimService {

    private final ClanManager clanManager;

    public ClaimService(ClanManager clanManager) {
        this.clanManager = clanManager;
    }

    public boolean claimChunkWithBeacon(Player player, Clan clan, Chunk chunk) {
        // Пример проверки силы
        if (!hasEnoughStrength(clan)) {
            player.sendMessage(ChatColor.RED + "У вашего клана недостаточно силы!");
            return false;
        }

        String chunkId = getChunkId(chunk);

        // Проверяем, не занят ли чанк другим кланом
        for (Clan otherClan : clanManager.clans.values()) {
            if (!otherClan.equals(clan) && otherClan.hasClaimedChunk(chunkId)) {
                player.sendMessage(ChatColor.RED + "Этот чанк уже занят другим кланом: " + otherClan.getName());
                return false;
            }
        }

        // Добавляем запись о занятости чанка
        clan.addClaimedChunk(chunkId);
        addWorldGuardRegion(chunk, clan);
        clanManager.sendClanMessage(clan, ChatColor.GREEN + player.getName() + " установил маяк и заприватил этот чанк!");

        // TODO: Здесь можно инициализировать объект "Mayaк" (Nexus) со здоровьем = 5,
        // и сохранить в каком-нибудь map<chunkId, маяк> для дальнейшей логики взрывов TNT.

        return true;
    }

    private boolean hasEnoughStrength(Clan clan) {
        return clan.getStrength() > clan.getLands(); // или иная логика
    }

    private String getChunkId(Chunk chunk) {
        return chunk.getWorld().getName() + "_" + chunk.getX() + "_" + chunk.getZ();
    }

    private void addWorldGuardRegion(Chunk chunk, Clan clan) {
        WorldGuard wg = WorldGuard.getInstance();
        RegionManager regions = wg.getPlatform().getRegionContainer().get(BukkitAdapter.adapt(chunk.getWorld()));
        if (regions == null) return;

        String regionId = "clan_" + clan.getName() + "_" + chunk.getWorld().getName()
                + "_" + chunk.getX() + "_" + chunk.getZ();

        int minX = chunk.getX() << 4;
        int minZ = chunk.getZ() << 4;
        int maxX = minX + 15;
        int maxZ = minZ + 15;
        int maxY = chunk.getWorld().getMaxHeight();

        ProtectedCuboidRegion region = new ProtectedCuboidRegion(
                regionId,
                BlockVector3.at(minX, 0, minZ),
                BlockVector3.at(maxX, maxY, maxZ)
        );

        // Флаги
        region.setFlag(Flags.INTERACT, StateFlag.State.ALLOW);
        region.setFlag(Flags.PVP, StateFlag.State.ALLOW);
        region.setFlag(Flags.TNT, StateFlag.State.ALLOW);
        region.setFlag(Flags.CREEPER_EXPLOSION, StateFlag.State.DENY);
        region.setFlag(Flags.MOB_SPAWNING, StateFlag.State.DENY);

        regions.addRegion(region);
        addClanMembersAsOwners(clan, region);
    }

    private void addClanMembersAsOwners(Clan clan, ProtectedCuboidRegion region) {
        for (String memberName : clan.getMembers()) {
            OfflinePlayer offline = Bukkit.getOfflinePlayer(memberName);
            String role = clan.getRole(memberName);
            if (role.equals("Лидер") || role.equals("Заместитель")) {
                if (!region.getOwners().contains(offline.getUniqueId())) {
                    DefaultDomain owners = region.getOwners();
                    owners.addPlayer(offline.getUniqueId());
                    region.setOwners(owners);
                }
            } else {
                // Для "Воинов" (или других ролей) можно добавить их в Members
                if (!region.getMembers().contains(offline.getUniqueId())) {
                    DefaultDomain members = region.getMembers();
                    members.addPlayer(offline.getUniqueId());
                    region.setMembers(members);
                }
            }
        }
    }
}
