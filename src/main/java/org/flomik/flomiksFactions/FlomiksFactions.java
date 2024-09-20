
package org.flomik.flomiksFactions;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.flomik.flomiksFactions.commands.admin.SetStrengthCommand;
import org.flomik.flomiksFactions.commands.clan.Clan;
import org.flomik.flomiksFactions.commands.menu.chunkMenu.ChunkMenuCommand;
import org.flomik.flomiksFactions.commands.chat.ChatCommandHandler;
import org.flomik.flomiksFactions.commands.clan.ClanCommand;
import org.flomik.flomiksFactions.commands.clan.ClanManager;
import org.flomik.flomiksFactions.commands.clan.handlers.clanInteractions.ClanChunksCommand;
import org.flomik.flomiksFactions.events.*;
import org.flomik.flomiksFactions.commands.player.PlayerCommand;
import org.flomik.flomiksFactions.commands.player.PlayerDataHandler;
import org.flomik.flomiksFactions.commands.menu.MenuManager;

import java.util.Map;

public final class FlomiksFactions extends JavaPlugin {

    private ClanManager clanManager;
    private MenuManager menuManager;
    private PlayerDataHandler playerDataHandler;

    @Override
    public void onEnable() {
        this.clanManager = new ClanManager(this);
        this.playerDataHandler = new PlayerDataHandler(this);
        this.menuManager = new MenuManager(this, clanManager);

        PlayerJoinListener playerJoinListener = new PlayerJoinListener(playerDataHandler, clanManager);

        getServer().getPluginManager().registerEvents(playerJoinListener, this);
        getServer().getPluginManager().registerEvents(new OnPlayerMoveListener(clanManager, this), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(playerDataHandler), this);
        getServer().getPluginManager().registerEvents(new ChatPrefixListener(clanManager), this);
        getServer().getPluginManager().registerEvents(new ClanTNTListener(clanManager, this), this);
        getServer().getPluginManager().registerEvents(new MenuProtectionListener(), this);

        getCommand("player").setExecutor(new PlayerCommand(playerDataHandler, clanManager));
        getCommand("clan").setExecutor(new ClanCommand(clanManager, playerDataHandler, this, menuManager));
        getCommand("clanchat").setExecutor(new ChatCommandHandler(clanManager));
        getCommand("chunkmap").setExecutor(new ChunkMenuCommand(menuManager));
        getCommand("setSt").setExecutor(new SetStrengthCommand(playerDataHandler));
        getCommand("clanchunks").setExecutor(new ClanChunksCommand(this, clanManager));

        new StrengthTickTask(playerDataHandler).addStrength(this);
        new ClanPvPListener(this, clanManager);
        playerJoinListener.startPeriodicStatsUpdate(this);
    }

    @Override
    public void onDisable() {
        clanManager.saveAllClans();
        denyTNTForAllClans();
    }

    private void denyTNTForAllClans() {
        WorldGuard wg = WorldGuard.getInstance();
        RegionContainer container = wg.getPlatform().getRegionContainer();

        for (World world : getServer().getWorlds()) {
            RegionManager regions = container.get(BukkitAdapter.adapt(world));

            if (regions != null) {
                for (Clan clan : clanManager.getAllClans()) {
                    for (Map.Entry<String, ProtectedRegion> entry : regions.getRegions().entrySet()) {
                        ProtectedRegion region = entry.getValue();

                        // Проверяем, является ли регион частью клана
                        if (region.getId().startsWith("clan_" + clan.getName())) {
                            region.setFlag(Flags.TNT, StateFlag.State.DENY);
                            Bukkit.getLogger().info("Set TNT flag to DENY for region: " + region.getId());
                        }
                    }
                }
            }
        }
    }
}
