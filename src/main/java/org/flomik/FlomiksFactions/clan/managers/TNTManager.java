package org.flomik.FlomiksFactions.clan.managers;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.flomik.FlomiksFactions.clan.Clan;

import java.util.Map;

public class TNTManager {
    private final ClanManager clanManager;

    public TNTManager(ClanManager clanManager) {
        this.clanManager = clanManager;
    }

    public void denyTNTForAllClans() {
        WorldGuard wg = WorldGuard.getInstance();
        RegionContainer container = wg.getPlatform().getRegionContainer();

        for (World world : Bukkit.getServer().getWorlds()) {
            RegionManager regions = container.get(BukkitAdapter.adapt(world));

            if (regions != null) {
                for (Clan clan : clanManager.getAllClans()) {
                    for (Map.Entry<String, ProtectedRegion> entry : regions.getRegions().entrySet()) {
                        ProtectedRegion region = entry.getValue();

                        if (region.getId().startsWith("clan_" + clan.getName())) {
                            region.setFlag(Flags.TNT, StateFlag.State.DENY);
                        }
                    }
                }
            }
        }
    }
}
