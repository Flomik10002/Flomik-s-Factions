
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
import org.bukkit.plugin.java.JavaPlugin;
import org.flomik.flomiksFactions.admin.commands.SetStrengthCommand;
import org.flomik.flomiksFactions.clan.Clan;
import org.flomik.flomiksFactions.donation.DonationCommand;
import org.flomik.flomiksFactions.donation.DonationManager;
import org.flomik.flomiksFactions.donation.effects.ParticleEffectHandler;
import org.flomik.flomiksFactions.donation.effects.commands.ParticleCommand;
import org.flomik.flomiksFactions.menu.MenuChecherPerm;
import org.flomik.flomiksFactions.worldEvents.shrine.commands.ShrineCommand;
import org.flomik.flomiksFactions.menu.chunkMenu.ChunkMenuCommand;
import org.flomik.flomiksFactions.chat.commands.ChatCommandHandler;
import org.flomik.flomiksFactions.clan.ClanCommand;
import org.flomik.flomiksFactions.clan.ClanManager;
import org.flomik.flomiksFactions.clan.commands.clanInteractions.ClanChunksCommand;
import org.flomik.flomiksFactions.listener.*;
import org.flomik.flomiksFactions.player.commands.PlayerCommand;
import org.flomik.flomiksFactions.player.PlayerDataHandler;
import org.flomik.flomiksFactions.menu.MenuManager;
import org.flomik.flomiksFactions.worldEvents.shrine.ShrineEvent;

import java.util.Map;

public final class FlomiksFactions extends JavaPlugin {

    public ClanManager clanManager;
    private MenuManager menuManager;
    private PlayerDataHandler playerDataHandler;
    private ShrineEvent shrineEvent;
    private DonationManager donationManager;
    private ParticleEffectHandler particleEffectHandler;

    @Override
    public void onEnable() {
        this.playerDataHandler = new PlayerDataHandler(this);
        this.donationManager = new DonationManager(this, playerDataHandler);
        this.clanManager = new ClanManager(this);
        this.menuManager = new MenuManager(this, clanManager);
        shrineEvent = new ShrineEvent(this);
        particleEffectHandler = new ParticleEffectHandler(this);

        PlayerJoinListener playerJoinListener = new PlayerJoinListener(playerDataHandler, clanManager);

        getServer().getPluginManager().registerEvents(playerJoinListener, this);
        getServer().getPluginManager().registerEvents(new OnPlayerMoveListener(clanManager, this), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(playerDataHandler), this);
        getServer().getPluginManager().registerEvents(new ChatPrefixListener(clanManager), this);
        getServer().getPluginManager().registerEvents(new ClanTNTListener(clanManager, this), this);
        getServer().getPluginManager().registerEvents(new MenuProtectionListener(), this);
        getServer().getPluginManager().registerEvents(new ParticleCommand(donationManager, particleEffectHandler, playerDataHandler), this); // Регистрация обработчика кликов для меню частиц
        getServer().getPluginManager().registerEvents(new DonationCommand(donationManager, playerDataHandler), this); // Регистрация обработчика кликов для меню частиц


        getCommand("player").setExecutor(new PlayerCommand(playerDataHandler, clanManager));
        getCommand("clan").setExecutor(new ClanCommand(clanManager, playerDataHandler, this, menuManager, shrineEvent));
        getCommand("clanchat").setExecutor(new ChatCommandHandler(clanManager));
        getCommand("chunkmap").setExecutor(new ChunkMenuCommand(menuManager));
        getCommand("setSt").setExecutor(new SetStrengthCommand(playerDataHandler));
        getCommand("clanchunks").setExecutor(new ClanChunksCommand(this, clanManager));
        getCommand("shrine").setExecutor(new ShrineCommand(shrineEvent));
        getCommand("donate").setExecutor(new DonationCommand(donationManager, playerDataHandler));
        getCommand("particles").setExecutor(new ParticleCommand(donationManager, particleEffectHandler, playerDataHandler));
        getCommand("clansettingsaccess").setExecutor(new MenuChecherPerm(clanManager));

        new StrengthTickTask(playerDataHandler).addStrength(this);
        new ClanPvPListener(this, clanManager);
        playerJoinListener.startPeriodicStatsUpdate(this);
        new Placeholders(playerDataHandler, clanManager).register();
    }

    @Override
    public void onDisable() {
        shrineEvent.saveShrinesToFile();
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
