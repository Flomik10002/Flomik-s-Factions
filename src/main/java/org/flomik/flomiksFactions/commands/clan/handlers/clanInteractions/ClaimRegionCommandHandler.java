package org.flomik.flomiksFactions.commands.clan.handlers.clanInteractions;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.flomik.flomiksFactions.commands.clan.Clan;
import org.flomik.flomiksFactions.commands.clan.ClanManager;

public class ClaimRegionCommandHandler {

    private final ClanManager clanManager;
    private final UnclaimRegionCommandHandler unclaimRegionCommandHandler;

    public ClaimRegionCommandHandler(ClanManager clanManager, UnclaimRegionCommandHandler unclaimRegionCommandHandler) {
        this.clanManager = clanManager;
        this.unclaimRegionCommandHandler = unclaimRegionCommandHandler;
    }

    public boolean handleCommand(Player player) {
        Clan clan = clanManager.getPlayerClan(player.getName());

        if (clan == null || !isLeaderOrDeputy(player, clan)) {
            player.sendMessage(ChatColor.RED + "У вас нет прав для выполнения этой команды.");
            return true;
        }

        Chunk chunk = player.getLocation().getChunk();
        String chunkId = getChunkId(chunk);

        if (isChunkClaimed(chunkId, clan)) {
            player.sendMessage(ChatColor.YELLOW + "Этот чанк уже занят вашим кланом.");
            return true;
        }

        if (isEnoughStrength(clan)) {
            player.sendMessage(ChatColor.YELLOW + "У вашего клана недостаточно силы.");
            return true;
        }

        if (isChunkClaimedByAnotherClan(chunkId, clan)) {
            for (Clan oldClan : clanManager.clans.values()) {
                if (!oldClan.equals(clan) && oldClan.hasClaimedChunk(chunkId)) {
                    if (oldClan.getLands() > oldClan.getStrength())
                    {
                        oldClan.removeClaimedChunk(chunkId);
                        unclaimRegionCommandHandler.removeWorldGuardRegion(chunk, oldClan.getName());

                        addWorldGuardRegion(chunk, clan.getName(), player);
                        clan.addClaimedChunk(chunkId);

                        player.sendMessage(ChatColor.GREEN + "Вы успешно захватили чанк!");
                        return true;
                    }
                }
            }
        }

        // Проверка, не занят ли чанк другим кланом
        if (isChunkClaimedByAnotherClan(chunkId, clan)) {
            player.sendMessage(ChatColor.RED + "Этот чанк уже занят другим кланом.");
            return true;
        }

        // Добавляем регион WorldGuard
        addWorldGuardRegion(chunk, clan.getName(), player);

        // Добавляем чанк к клану
        clan.addClaimedChunk(chunkId);
        player.sendMessage(ChatColor.GREEN + "Чанк успешно занят вашим кланом!");
        return true;
    }

    private boolean isEnoughStrength(Clan clan) {
        if (clan.getStrength() <= clan.getLands()){
            return true;
        }
        return false;
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

    private void addWorldGuardRegion(Chunk chunk, String clanName, Player player) {
        Clan clan = clanManager.getPlayerClan(player.getName());
        WorldGuard wg = WorldGuard.getInstance();
        RegionContainer container = wg.getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(chunk.getWorld()));

        // Изменяем формат идентификатора региона, убираем двоеточие и используем подчеркивания
        String regionId = "clan_" + clanName + "_" + chunk.getWorld().getName() + "_" + chunk.getX() + "_" + chunk.getZ();

        BlockVector3 min = BlockVector3.at(chunk.getX() << 4, 0, chunk.getZ() << 4);
        BlockVector3 max = BlockVector3.at((chunk.getX() << 4) + 15, chunk.getWorld().getMaxHeight(), (chunk.getZ() << 4) + 15);
        ProtectedCuboidRegion region = new ProtectedCuboidRegion(regionId, min, max);

        // Устанавливаем флаги региона
        region.setFlag(Flags.INTERACT, StateFlag.State.ALLOW);
        region.setFlag(Flags.PVP, StateFlag.State.ALLOW);

        if (regions != null) {
            regions.addRegion(region);
            addMembers(clan, region);
        }
    }

    public void addMembers(Clan clan, ProtectedRegion region) {
        for (String member : clan.getMembers()) {
            String playerRole = clan.getRole(member);
            Player player = Bukkit.getPlayerExact(member);
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(member);

            // Убедимся, что игрок существует и онлайн
            if (player != null && (playerRole.equals("Лидер") || playerRole.equals("Заместитель"))) {
                // Проверка на наличие в текущих владельцах
                if (!region.getOwners().contains(player.getUniqueId())) {
                    // Получаем текущих владельцев
                    DefaultDomain owners = region.getOwners();

                    // Добавляем нового владельца
                    owners.addPlayer(player.getUniqueId());

                    // Устанавливаем обновленный список владельцев
                    region.setOwners(owners);
                }
            } if (player != null &&  playerRole.equals("Воин")) {
                // Проверка на наличие в текущих владельцах
                if (!region.getMembers().contains(player.getUniqueId())) {
                    // Получаем текущих владельцев
                    DefaultDomain members = region.getMembers();

                    // Добавляем нового владельца
                    members.addPlayer(player.getUniqueId());

                    // Устанавливаем обновленный список владельцев
                    region.setMembers(members);
                }
            } if (offlinePlayer != null && (playerRole.equals("Лидер") || playerRole.equals("Заместитель"))) {
                // Проверка на наличие в текущих владельцах
                if (!region.getOwners().contains(offlinePlayer.getUniqueId())) {
                    // Получаем текущих владельцев
                    DefaultDomain owners = region.getOwners();

                    // Добавляем нового владельца
                    owners.addPlayer(offlinePlayer.getUniqueId());

                    // Устанавливаем обновленный список владельцев
                    region.setOwners(owners);
                }
            } if (offlinePlayer != null && playerRole.equals("Воин")) {
                // Проверка на наличие в текущих владельцах
                if (!region.getMembers().contains(offlinePlayer.getUniqueId())) {
                    // Получаем текущих владельцев
                    DefaultDomain members = region.getMembers();

                    // Добавляем нового владельца
                    members.addPlayer(offlinePlayer.getUniqueId());

                    // Устанавливаем обновленный список владельцев
                    region.setMembers(members);
                }
            }
        }
    }
}
