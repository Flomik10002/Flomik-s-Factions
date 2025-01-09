package org.flomik.FlomiksFactions.commands.handlers.home;

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
import org.flomik.FlomiksFactions.clan.Clan;
import org.flomik.FlomiksFactions.clan.managers.ClanManager;

public class SetHomeHandler {
    private final ClanManager clanManager;

    public SetHomeHandler(ClanManager clanManager) {
        this.clanManager = clanManager;
    }

    public boolean handleCommand(Player player) {
        Clan clan = clanManager.getPlayerClan(player.getName());
        if (clan == null) {
            player.sendMessage(ChatColor.RED + "Вы не состоите в клане.");
            return false;
        }


        String playerRole = clan.getRole(player.getName());
        if (!playerRole.equals("Лидер") && !playerRole.equals("Заместитель")) {
            player.sendMessage(ChatColor.RED + "Только Лидер или Заместитель может установить точку дома.");
            return false;
        }


        Location playerLocation = player.getLocation();


        if (!isInPlayerRegion(player, playerLocation)) {
            player.sendMessage(ChatColor.RED + "Вы не находитесь в своем привате или не являетесь владельцем.");
            return false;
        }


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
            return false;
        }

        ApplicableRegionSet regionSet = regions.getApplicableRegions(BukkitAdapter.asBlockVector(location));
        for (ProtectedRegion region : regionSet) {
            if (region.isOwner(WorldGuardPlugin.inst().wrapPlayer(player)) || region.isMember(WorldGuardPlugin.inst().wrapPlayer(player))) {
                return true;
            }
        }

        return false;
    }
}
