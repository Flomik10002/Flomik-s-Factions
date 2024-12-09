
package org.flomik.flomiksFactions;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.flomik.flomiksFactions.admin.commands.SetStrengthCommand;
import org.flomik.flomiksFactions.clan.Clan;
import org.flomik.flomiksFactions.databases.ClanDao;
import org.flomik.flomiksFactions.databases.DatabaseManager;
import org.flomik.flomiksFactions.databases.InvitationDao;
import org.flomik.flomiksFactions.databases.PlayerDatabaseManager;
import org.flomik.flomiksFactions.donation.DonationCommand;
import org.flomik.flomiksFactions.donation.DonationManager;
import org.flomik.flomiksFactions.donation.effects.ParticleEffectHandler;
import org.flomik.flomiksFactions.donation.effects.commands.ParticleCommand;
import org.flomik.flomiksFactions.menu.MenuCheckerPerm;
import org.flomik.flomiksFactions.worldEvents.randomEvents.EventScheduler;
import org.flomik.flomiksFactions.worldEvents.randomEvents.RandomEvent;
import org.flomik.flomiksFactions.worldEvents.randomEvents.RandomEventManager;
import org.flomik.flomiksFactions.worldEvents.randomEvents.commands.EventCommand;
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
    private RandomEventManager eventManager;
    private Economy economy;
    private EventScheduler eventScheduler;
    private DatabaseManager databaseManager;
    private ClanDao clanDao;
    private InvitationDao invitationDao;
    private PlayerDatabaseManager playerDatabaseManager;


    @Override
    public void onEnable() {
        this.databaseManager = new DatabaseManager();
        databaseManager.initDatabase(this);
        databaseManager.createTables();

        this.clanDao = new ClanDao(databaseManager);
        this.invitationDao = new InvitationDao(databaseManager);
        this.clanManager = new ClanManager(this, clanDao, invitationDao);

        this.playerDatabaseManager = new PlayerDatabaseManager();
        playerDatabaseManager.initDatabase(this);
        playerDatabaseManager.createTables();

        getLogger().info("Plugin enabled and database connected!");

        if (!setupEconomy()) {
            getLogger().severe("Vault не найден! Экономика не будет работать.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.playerDataHandler = new PlayerDataHandler(this);
        this.eventScheduler = new EventScheduler(this, eventManager);
        this.donationManager = new DonationManager(this, playerDataHandler);
        this.menuManager = new MenuManager(this, clanManager);
        this.shrineEvent = new ShrineEvent(this);
        this.eventManager = new RandomEventManager(this);
        this.particleEffectHandler = new ParticleEffectHandler(this);

        PlayerJoinListener playerJoinListener = new PlayerJoinListener(playerDataHandler, clanManager);

        getServer().getPluginManager().registerEvents(playerJoinListener, this);
        getServer().getPluginManager().registerEvents(new OnPlayerMoveListener(clanManager, this), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(playerDataHandler), this);
        getServer().getPluginManager().registerEvents(new ChatPrefixListener(clanManager), this);
        getServer().getPluginManager().registerEvents(new ClanTNTListener(clanManager, this), this);
        getServer().getPluginManager().registerEvents(new MenuProtectionListener(), this);
        getServer().getPluginManager().registerEvents(new ParticleCommand(donationManager, particleEffectHandler, playerDataHandler), this);
        getServer().getPluginManager().registerEvents(new DonationCommand(donationManager, playerDataHandler), this);

        getCommand("player").setExecutor(new PlayerCommand(playerDataHandler, clanManager));
        getCommand("clan").setExecutor(new ClanCommand(clanManager, playerDataHandler, this, menuManager, shrineEvent));
        getCommand("clanchat").setExecutor(new ChatCommandHandler(clanManager));
        getCommand("chunkmap").setExecutor(new ChunkMenuCommand(menuManager));
        getCommand("setSt").setExecutor(new SetStrengthCommand(playerDataHandler));
        getCommand("clanchunks").setExecutor(new ClanChunksCommand(this, clanManager));
        getCommand("shrine").setExecutor(new ShrineCommand(shrineEvent));
        getCommand("donate").setExecutor(new DonationCommand(donationManager, playerDataHandler));
        getCommand("particles").setExecutor(new ParticleCommand(donationManager, particleEffectHandler, playerDataHandler));
        getCommand("clansettingsaccess").setExecutor(new MenuCheckerPerm(clanManager));
        getCommand("event").setExecutor(new EventCommand(eventManager, this));

        new StrengthTickTask(playerDataHandler).addStrength(this);
        new ClanPvPListener(this, clanManager);
        playerJoinListener.startPeriodicStatsUpdate(this);
        new Placeholders(playerDataHandler, clanManager).register();

        eventScheduler.start();
    }

    @Override
    public void onDisable() {
        shrineEvent.saveShrinesToFile();

        if (databaseManager != null) {
            databaseManager.close();
        }
        getLogger().info("Plugin disabled and database connection closed!");

        denyTNTForAllClans();

        if (eventManager.isRunning()) {
            eventManager.stopEvent();
        }
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

                        if (region.getId().startsWith("clan_" + clan.getName())) {
                            region.setFlag(Flags.TNT, StateFlag.State.DENY);
                            Bukkit.getLogger().info("Set TNT flag to DENY for region: " + region.getId());
                        }
                    }
                }
            }
        }
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    public ClanManager getClanManager() {
        return clanManager;
    }

    public ClanDao getClanDao() {
        return clanDao;
    }

    public InvitationDao getInvitationDao() {
        return invitationDao;
    }

    public PlayerDatabaseManager getPlayerDatabaseManager() {
        return playerDatabaseManager;
    }

    public Economy getEconomy() {
        return economy;
    }

    public ShrineEvent getShrineEvent() {
        return shrineEvent;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (eventManager.isRunning()) {
            RandomEvent currentEvent = eventManager.getCurrentEvent();
            if (currentEvent.getBossBar() != null) {
                currentEvent.getBossBar().addPlayer(event.getPlayer());
            }
        }
    }

}
