package org.flomik.FlomiksFactions.listener;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.flomik.FlomiksFactions.FlomiksFactions;
import org.flomik.FlomiksFactions.clan.Clan;
import org.flomik.FlomiksFactions.clan.managers.ClanManager;

import java.util.HashMap;
import java.util.Map;

import static org.bukkit.Bukkit.getServer;

public class ClanTNTListener implements Listener {
    private final ClanManager clanManager;
    private final FlomiksFactions plugin;

    private final Map<Clan, Long> lastLogoutTimes = new HashMap<>();
    private final Map<Clan, BukkitRunnable> clanTimers = new HashMap<>();

    public ClanTNTListener(ClanManager clanManager, FlomiksFactions plugin) {
        this.clanManager = clanManager;
        this.plugin = plugin;
    }

    private int getOnlineMembersCount(Clan clan) {
        int onlineCount = 0;
        for (String member : clan.getMembers()) {
            Player player = Bukkit.getPlayer(member);
            if (player != null && player.isOnline()) {
                onlineCount++;
            }
        }
        return onlineCount;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Clan clan = clanManager.getPlayerClan(player.getName());
        if (clan != null) {
            int onlineCount = getOnlineMembersCount(clan);
            Bukkit.getLogger().info("[FlomiksFactions] Player joined: " + player.getName() + ", Clan: " + clan.getName() + ", Online count: " + onlineCount);
            if (onlineCount > 0 && clanTimers.containsKey(clan)) {
                clanTimers.get(clan).cancel();
                clanTimers.remove(clan);
                Bukkit.getLogger().info("[FlomiksFactions] Cancelled TNT disable timer for clan: " + clan.getName());
                Bukkit.getLogger().info("[FlomiksFactions] TNT allowed for clan: " + clan.getName());
                setClanFlags(clan, StateFlag.State.ALLOW);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Clan clan = clanManager.getPlayerClan(player.getName());
        if (clan != null) {
            int onlineCount = getOnlineMembersCount(clan) - 1;
            Bukkit.getLogger().info("[FlomiksFactions] Player quit: " + player.getName() + ", Clan: " + clan.getName() + ", Online count: " + onlineCount);
            if (onlineCount == 0) {
                lastLogoutTimes.put(clan, System.currentTimeMillis());
                Bukkit.getLogger().info("[FlomiksFactions] No players online in clan: " + clan.getName() + ", scheduling TNT disable");
                BukkitRunnable task = new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (getOnlineMembersCount(clan) == 0) {
                            setClanFlags(clan, StateFlag.State.DENY);
                            Bukkit.getLogger().info("[FlomiksFactions] TNT denied for clan: " + clan.getName());
                        }
                    }
                };
                task.runTaskLater(plugin, 20L * 60 * 15);
                clanTimers.put(clan, task);
            }
        }
    }

    private void setClanFlags(Clan clan, StateFlag.State state) {
        WorldGuard wg = WorldGuard.getInstance();
        RegionContainer container = wg.getPlatform().getRegionContainer();

        for (World world : getServer().getWorlds()) {
            RegionManager regions = container.get(BukkitAdapter.adapt(world));

            if (regions != null) {
                for (Map.Entry<String, ProtectedRegion> entry : regions.getRegions().entrySet()) {
                    ProtectedRegion region = entry.getValue();

                    if (region.getId().startsWith("clan_" + clan.getName())) {
                        region.setFlag(Flags.TNT, state);
                        Bukkit.getLogger().info("[FlomiksFactions] Set TNT flag to " + state + " for region: " + region.getId());
                    }
                }
            }
        }
    }
}
