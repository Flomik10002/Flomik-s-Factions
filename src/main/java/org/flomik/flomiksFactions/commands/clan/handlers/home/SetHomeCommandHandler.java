package org.flomik.flomiksFactions.commands.clan.handlers.home;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.WorldGuard;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.flomik.flomiksFactions.commands.clan.Clan;
import org.flomik.flomiksFactions.commands.clan.ClanManager;

public class SetHomeCommandHandler {
    private final ClanManager clanManager;

    public SetHomeCommandHandler(ClanManager clanManager) {
        this.clanManager = clanManager;
    }

    public boolean handleCommand(Player player) {
        Clan clan = clanManager.getPlayerClan(player.getName());
        if (clan == null) {
            player.sendMessage(ChatColor.RED + "Вы не состоите в клане.");
            return false;
        }

        // Проверка роли игрока
        String playerRole = clan.getRole(player.getName());
        if (!playerRole.equals("Лидер") && !playerRole.equals("Заместитель")) {
            player.sendMessage(ChatColor.RED + "Только Лидер или Заместитель может установить точку дома.");
            return false;
        }

        // Получение местоположения игрока
        Location playerLocation = player.getLocation();

        // Проверка привата через WorldGuard
        if (!isInPlayerRegion(player, playerLocation)) {
            player.sendMessage(ChatColor.RED + "Вы не находитесь в своем привате или не являетесь владельцем.");
            return false;
        }

        // Установка дома клана
        clan.setHome(playerLocation);
        clanManager.saveClan(clan);
        player.sendMessage(ChatColor.GREEN + "Точка дома клана установлена.");
        return true;
    }

    private boolean isInPlayerRegion(Player player, Location location) {
        WorldGuard wg = WorldGuard.getInstance();
        RegionContainer container = wg.getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(location.getWorld()));

        if (regions == null) {
            return false; // Если нет регионов на этом мире
        }

        ApplicableRegionSet regionSet = regions.getApplicableRegions(BukkitAdapter.asBlockVector(location));
        for (ProtectedRegion region : regionSet) {
            if (region.isOwner(WorldGuardPlugin.inst().wrapPlayer(player)) || region.isMember(WorldGuardPlugin.inst().wrapPlayer(player))) {
                return true; // Если игрок владелец или участник привата
            }
        }

        return false; // Игрок не владелец и не участник
    }
}
