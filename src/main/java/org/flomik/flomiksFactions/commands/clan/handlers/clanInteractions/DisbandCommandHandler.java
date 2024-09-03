package org.flomik.flomiksFactions.commands.clan.handlers.clanInteractions;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.flomik.flomiksFactions.commands.clan.Clan;
import org.flomik.flomiksFactions.commands.clan.ClanManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class DisbandCommandHandler  {

    private final ClanManager clanManager;
    private final ConcurrentHashMap<String, Long> pendingDisbands;

    public DisbandCommandHandler(ClanManager clanManager, ConcurrentHashMap<String, Long> pendingDisbands) {
        this.clanManager = clanManager;
        this.pendingDisbands = pendingDisbands;
    }

    public boolean handleCommand(Player player) {
        Clan clan = clanManager.getPlayerClan(player.getName());
        if (clan == null) {
            player.sendMessage(ChatColor.RED + "Вы не состоите в клане.");
            return true;
        }

        if (!clan.getOwner().equals(player.getName())) {
            player.sendMessage(ChatColor.RED + "Только Лидер клана может его распустить.");
            return true;
        }

        if (pendingDisbands.containsKey(player.getName())) {
            // Удаляем клан
            for (Clan otherClan : clanManager.getAllClans()) {
                if (otherClan.getAlliances().contains(clan.getName())) {
                    otherClan.removeAlliance(clan); // Удаляем альянс
                    clanManager.updateClan(otherClan); // Обновляем данные о клане
                }
            }
            clanManager.disbandClan(clan.getName());
            pendingDisbands.remove(player.getName());
            Bukkit.broadcastMessage(ChatColor.GREEN + "Клан " + ChatColor.YELLOW + clan.getName() + ChatColor.GREEN + " был успешно распущен.");

            com.sk89q.worldedit.entity.Player wgPlayer = BukkitAdapter.adapt(player);
            WorldGuard wg = WorldGuard.getInstance();
            RegionContainer container = wg.getPlatform().getRegionContainer();
            RegionManager regionManager = container.get(BukkitAdapter.adapt(player.getWorld()));

            if (regionManager == null) {
                return true;
            }

            List<String> regionsToRemove = new ArrayList<>();

            for (ProtectedRegion region : regionManager.getRegions().values()) {
                if (region.getOwners().contains(wgPlayer.getUniqueId())) {
                    regionsToRemove.add(region.getId());
                }
            }

            for (String regionId : regionsToRemove) {
                regionManager.removeRegion(regionId);
            }

        } else {
            // Запрашиваем подтверждение
            pendingDisbands.put(player.getName(), System.currentTimeMillis());
            player.sendMessage(ChatColor.YELLOW + "Вы действительно хотите распустить клан? Повторите команду в течении 15 секунд для подтверждения.");
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (pendingDisbands.containsKey(player.getName())) {
                        player.sendMessage(ChatColor.RED + "Время для подтверждения истекло.");
                        pendingDisbands.remove(player.getName());
                    }
                }
            }.runTaskLater(Bukkit.getPluginManager().getPlugin("FlomiksFactions"), 300L); // 300L = 15 секунд
        }
        return true;
    }
}
