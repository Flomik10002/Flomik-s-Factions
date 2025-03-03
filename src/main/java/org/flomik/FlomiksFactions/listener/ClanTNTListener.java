package org.flomik.FlomiksFactions.listener; //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression

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

public class ClanTNTListener implements Listener { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private final ClanManager clanManager; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private final FlomiksFactions plugin; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression

    private final Map<Clan, Long> lastLogoutTimes = new HashMap<>(); //NOPMD - suppressed UseConcurrentHashMap - TODO explain reason for suppression //NOPMD - suppressed UseConcurrentHashMap - TODO explain reason for suppression //NOPMD - suppressed UseConcurrentHashMap - TODO explain reason for suppression
    private final Map<Clan, BukkitRunnable> clanTimers = new HashMap<>(); //NOPMD - suppressed UseConcurrentHashMap - TODO explain reason for suppression //NOPMD - suppressed UseConcurrentHashMap - TODO explain reason for suppression //NOPMD - suppressed UseConcurrentHashMap - TODO explain reason for suppression

    public ClanTNTListener(ClanManager clanManager, FlomiksFactions plugin) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        this.clanManager = clanManager;
        this.plugin = plugin;
    }

    private int getOnlineMembersCount(Clan clan) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        int onlineCount = 0;
        for (String member : clan.getMembers()) { //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            Player player = Bukkit.getPlayer(member); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            if (player != null && player.isOnline()) {
                onlineCount++;
            }
        }
        return onlineCount;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        Player player = event.getPlayer(); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
        Clan clan = clanManager.getPlayerClan(player.getName()); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        if (clan != null) {
            int onlineCount = getOnlineMembersCount(clan); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            Bukkit.getLogger().info("[FlomiksFactions] Player joined: " + player.getName() + ", Clan: " + clan.getName() + ", Online count: " + onlineCount); //NOPMD - suppressed GuardLogStatement - TODO explain reason for suppression //NOPMD - suppressed GuardLogStatement - TODO explain reason for suppression //NOPMD - suppressed GuardLogStatement - TODO explain reason for suppression
            if (onlineCount > 0 && clanTimers.containsKey(clan)) {
                clanTimers.get(clan).cancel();
                clanTimers.remove(clan);
                Bukkit.getLogger().info("[FlomiksFactions] Cancelled TNT disable timer for clan: " + clan.getName()); //NOPMD - suppressed GuardLogStatement - TODO explain reason for suppression //NOPMD - suppressed GuardLogStatement - TODO explain reason for suppression //NOPMD - suppressed GuardLogStatement - TODO explain reason for suppression
                Bukkit.getLogger().info("[FlomiksFactions] TNT allowed for clan: " + clan.getName()); //NOPMD - suppressed GuardLogStatement - TODO explain reason for suppression //NOPMD - suppressed GuardLogStatement - TODO explain reason for suppression //NOPMD - suppressed GuardLogStatement - TODO explain reason for suppression
                setClanFlags(clan, StateFlag.State.ALLOW);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        Player player = event.getPlayer(); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
        Clan clan = clanManager.getPlayerClan(player.getName()); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        if (clan != null) {
            int onlineCount = getOnlineMembersCount(clan) - 1; //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            Bukkit.getLogger().info("[FlomiksFactions] Player quit: " + player.getName() + ", Clan: " + clan.getName() + ", Online count: " + onlineCount); //NOPMD - suppressed GuardLogStatement - TODO explain reason for suppression //NOPMD - suppressed GuardLogStatement - TODO explain reason for suppression //NOPMD - suppressed GuardLogStatement - TODO explain reason for suppression
            if (onlineCount == 0) {
                lastLogoutTimes.put(clan, System.currentTimeMillis());
                Bukkit.getLogger().info("[FlomiksFactions] No players online in clan: " + clan.getName() + ", scheduling TNT disable"); //NOPMD - suppressed GuardLogStatement - TODO explain reason for suppression //NOPMD - suppressed GuardLogStatement - TODO explain reason for suppression //NOPMD - suppressed GuardLogStatement - TODO explain reason for suppression
                BukkitRunnable task = new BukkitRunnable() { //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                    @Override
                    public void run() {
                        if (getOnlineMembersCount(clan) == 0) { //NOPMD - suppressed AvoidDeeplyNestedIfStmts - TODO explain reason for suppression //NOPMD - suppressed AvoidDeeplyNestedIfStmts - TODO explain reason for suppression //NOPMD - suppressed AvoidDeeplyNestedIfStmts - TODO explain reason for suppression
                            setClanFlags(clan, StateFlag.State.DENY);
                            Bukkit.getLogger().info("[FlomiksFactions] TNT denied for clan: " + clan.getName()); //NOPMD - suppressed GuardLogStatement - TODO explain reason for suppression //NOPMD - suppressed GuardLogStatement - TODO explain reason for suppression //NOPMD - suppressed GuardLogStatement - TODO explain reason for suppression
                        }
                    }
                };
                task.runTaskLater(plugin, 20L * 60 * 15);
                clanTimers.put(clan, task);
            }
        }
    }

    private void setClanFlags(Clan clan, StateFlag.State state) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        WorldGuard wg = WorldGuard.getInstance(); //NOPMD - suppressed ShortVariable - TODO explain reason for suppression //NOPMD - suppressed ShortVariable - TODO explain reason for suppression //NOPMD - suppressed ShortVariable - TODO explain reason for suppression
        RegionContainer container = wg.getPlatform().getRegionContainer(); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression

        for (World world : getServer().getWorlds()) { //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            RegionManager regions = container.get(BukkitAdapter.adapt(world)); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression

            if (regions != null) {
                for (Map.Entry<String, ProtectedRegion> entry : regions.getRegions().entrySet()) { //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                    ProtectedRegion region = entry.getValue(); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression

                    if (region.getId().startsWith("clan_" + clan.getName())) {
                        region.setFlag(Flags.TNT, state);
                        Bukkit.getLogger().info("[FlomiksFactions] Set TNT flag to " + state + " for region: " + region.getId()); //NOPMD - suppressed GuardLogStatement - TODO explain reason for suppression //NOPMD - suppressed GuardLogStatement - TODO explain reason for suppression //NOPMD - suppressed GuardLogStatement - TODO explain reason for suppression
                    }
                }
            }
        }
    }
}
