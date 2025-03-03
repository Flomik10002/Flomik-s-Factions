package org.flomik.FlomiksFactions; //NOPMD - suppressed CouplingBetweenObjects - TODO explain reason for suppression //NOPMD - suppressed CouplingBetweenObjects - TODO explain reason for suppression //NOPMD - suppressed CouplingBetweenObjects - TODO explain reason for suppression

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.flomik.FlomiksFactions.clan.managers.*;
import org.flomik.FlomiksFactions.clan.commands.handlers.clanInteractions.ClaimRegionHandler;
import org.flomik.FlomiksFactions.clan.commands.handlers.clanInteractions.UnclaimRegionHandler;
import org.flomik.FlomiksFactions.database.*;
import org.flomik.FlomiksFactions.register.CommandRegistrar;
import org.flomik.FlomiksFactions.register.EventRegistrar;
import org.flomik.FlomiksFactions.utils.Placeholders;
import org.flomik.FlomiksFactions.worldEvents.castle.config.CastleConfigManager;
import org.flomik.FlomiksFactions.worldEvents.castle.events.CastleEvent;
import org.flomik.FlomiksFactions.worldEvents.castle.managers.CastleLootManager;
import org.flomik.FlomiksFactions.worldEvents.castle.managers.HeadsManager;
import org.flomik.FlomiksFactions.worldEvents.randomEvents.managers.EventScheduler;
import org.flomik.FlomiksFactions.worldEvents.randomEvents.managers.RandomEventManager;
import org.flomik.FlomiksFactions.listener.*;
import org.flomik.FlomiksFactions.player.PlayerDataHandler;
import org.flomik.FlomiksFactions.worldEvents.shrine.config.ShrineConfigManager;
import org.flomik.FlomiksFactions.worldEvents.shrine.event.ShrineEvent;

public final class FlomiksFactions extends JavaPlugin { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private ClanManager clanManager; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private BeaconDao beaconDao; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private ClaimRegionHandler claimRegionHandler; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private UnclaimRegionHandler unclaimRegionHandler; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private BeaconManager beaconManager; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private BeaconDatabaseManager beaconDatabaseManager; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private ChunkMenuManager chunkMenuManager; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private PlayerDataHandler playerDataHandler; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private ShrineEvent shrineEvent; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private CastleEvent castleEvent; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private RandomEventManager eventManager; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private Economy economy; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private EventScheduler eventScheduler; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private ClanDatabaseManager clanDatabaseManager; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private ClanDao clanDao; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private InvitationDao invitationDao; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private PlayerDatabaseManager playerDatabaseManager; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private CastleLootManager lootManager; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private HeadsManager headsManager; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    private ShrineConfigManager shrineConfigManager; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression

    @Override
    public void onEnable() {
        setupConfigs();
        setupDatabase();
        setupDependencies();
        registerCommandsAndEvents();

        startSchedulers();

        getLogger().info("Плагин успешно запущен!");
    }

    @Override
    public void onDisable() {
        saveData();
        closeDatabase();
        denyClanTNT();
        stopEvents();

        getLogger().info("Плагин успешно отключён.");
    }

    private void saveData() {
        if (shrineEvent != null) shrineEvent.saveShrinesToFile(); //NOPMD - suppressed ControlStatementBraces - TODO explain reason for suppression //NOPMD - suppressed ControlStatementBraces - TODO explain reason for suppression //NOPMD - suppressed ControlStatementBraces - TODO explain reason for suppression
    }

    private void closeDatabase() {
        if (clanDatabaseManager != null) clanDatabaseManager.close(); //NOPMD - suppressed ControlStatementBraces - TODO explain reason for suppression //NOPMD - suppressed ControlStatementBraces - TODO explain reason for suppression //NOPMD - suppressed ControlStatementBraces - TODO explain reason for suppression
    }

    private void denyClanTNT() {
        new TNTManager(clanManager).denyTNTForAllClans();
    }

    private void stopEvents() {
        if (eventManager.isRunning()) eventManager.stopEvent(); //NOPMD - suppressed ControlStatementBraces - TODO explain reason for suppression //NOPMD - suppressed ControlStatementBraces - TODO explain reason for suppression //NOPMD - suppressed ControlStatementBraces - TODO explain reason for suppression
    }

    private void setupConfigs() {
        CastleConfigManager.setup(this);
        CastleConfigManager.loadConfig();
        CastleConfigManager.get().options().copyDefaults(true);
        CastleConfigManager.save();

        ShrineConfigManager.setup(this);
        ShrineConfigManager.loadConfig();
        ShrineConfigManager.get().options().copyDefaults(true);
        ShrineConfigManager.save();

        NexusConfigManager.setup(this);
        NexusConfigManager.loadConfig();
        NexusConfigManager.get().options().copyDefaults(true);
        NexusConfigManager.save();
    }

    private void setupDatabase() {
        try {
            this.clanDatabaseManager = new ClanDatabaseManager();
            clanDatabaseManager.initDatabase(this);
            clanDatabaseManager.createTables();

            this.clanDao = new ClanDao(clanDatabaseManager);
            this.invitationDao = new InvitationDao(clanDatabaseManager);

            this.playerDatabaseManager = new PlayerDatabaseManager();
            playerDatabaseManager.initDatabase(this);
            playerDatabaseManager.createTables();

            this.beaconDatabaseManager = new BeaconDatabaseManager();
            beaconDatabaseManager.initDatabase(this);
            beaconDatabaseManager.createTables();

            this.beaconDao = new BeaconDao(clanDatabaseManager);

        } catch (Exception e) { //NOPMD - suppressed AvoidCatchingGenericException - TODO explain reason for suppression //NOPMD - suppressed AvoidCatchingGenericException - TODO explain reason for suppression //NOPMD - suppressed AvoidCatchingGenericException - TODO explain reason for suppression
            getLogger().severe("Ошибка при настройке базы данных: " + e.getMessage()); //NOPMD - suppressed GuardLogStatement - TODO explain reason for suppression //NOPMD - suppressed GuardLogStatement - TODO explain reason for suppression //NOPMD - suppressed GuardLogStatement - TODO explain reason for suppression
            getServer().getPluginManager().disablePlugin(this); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
        }
    }

    private void setupDependencies() {
        if (!setupEconomy()) {
            getLogger().severe("Vault не найден! Экономика не будет работать.");
            getServer().getPluginManager().disablePlugin(this); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
            return;
        }

        this.clanManager = new ClanManager(this, clanDao, invitationDao);
        this.beaconManager = new BeaconManager();
        this.lootManager = new CastleLootManager(this);
        this.headsManager = new HeadsManager(this);
        this.playerDataHandler = new PlayerDataHandler(this);
        this.eventManager = new RandomEventManager(this);
        this.eventScheduler = new EventScheduler(this, eventManager);
        this.chunkMenuManager = new ChunkMenuManager(this, clanManager);
        this.shrineEvent = new ShrineEvent(this, shrineConfigManager);
        this.castleEvent = new CastleEvent(this, headsManager);
        this.unclaimRegionHandler = new UnclaimRegionHandler(clanManager, beaconDao,
                beaconManager);
        this.claimRegionHandler = new ClaimRegionHandler(
                clanManager,
                unclaimRegionHandler,
                shrineEvent,
                beaconDao,
                beaconManager
        );

    }

    private void startSchedulers() {
        new StrengthTickListener(playerDataHandler).addStrength(this);
        new ClanPvPListener(this, clanManager);
        new Placeholders(this, playerDataHandler, clanManager).register();

        eventScheduler.start();
    }

    private void startZeroHpCheck() { //NOPMD - suppressed UnusedPrivateMethod - TODO explain reason for suppression //NOPMD - suppressed UnusedPrivateMethod - TODO explain reason for suppression //NOPMD - suppressed UnusedPrivateMethod - TODO explain reason for suppression
        // how often we check, in ticks (default 100 = every 5 seconds)
        int checkInterval = NexusConfigManager.getInt("zero-hp-check-interval");
        if (checkInterval < 1) { //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression
            checkInterval = 100; // fallback
        }

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            beaconManager.getAllBeacons().forEach(beacon -> {
                if (beacon.getHealth() <= 0) {
                    // Here is where you handle "zero HP" logic.
                    // For example, you might mark this beacon as "vulnerable" or auto-start a capture event. //NOPMD - suppressed CommentSize - TODO explain reason for suppression //NOPMD - suppressed CommentSize - TODO explain reason for suppression //NOPMD - suppressed CommentSize - TODO explain reason for suppression

                    // Simple example: log it
                    getLogger().info("Beacon " + beacon.getRegionId() //NOPMD - suppressed GuardLogStatement - TODO explain reason for suppression //NOPMD - suppressed GuardLogStatement - TODO explain reason for suppression //NOPMD - suppressed GuardLogStatement - TODO explain reason for suppression
                            + " (clan " + beacon.getClanName()
                            + ") is at 0 HP and can be captured!");
                }
            });
        }, 0L, checkInterval);
    }


    public boolean setupEconomy() { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        if (getServer().getPluginManager().getPlugin("Vault") == null) { //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
            return false; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
        if (rsp == null) {
            return false; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    private void registerCommandsAndEvents() {
        CommandRegistrar.registerCommands(this);
        EventRegistrar.registerEvents(this);
    }

    public Economy getEconomy() {
        return economy;
    }

    public ShrineEvent getShrineEvent() {
        return shrineEvent;
    }

    public ClanManager getClanManager() {
        return clanManager;
    }

    public BeaconManager getBeaconManager () {
        return beaconManager;
    }

    public BeaconDao getBeaconDao () {
        return beaconDao;
    }

    public ChunkMenuManager getMenuManager() { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        return chunkMenuManager;
    }

    public PlayerDataHandler getPlayerDataHandler() {
        return playerDataHandler;
    }

    public CastleEvent getCastleEvent() {
        return castleEvent;
    }

    public CastleLootManager getLootManager() {
        return lootManager;
    }

    public RandomEventManager getEventManager() {
        return eventManager;
    }

    public ClanDatabaseManager getDatabaseManager() { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        return clanDatabaseManager;
    }

    public ClanDao getClanDao() {
        return clanDao;
    }

    public ClaimRegionHandler getClaimRegionHandler() {
        return claimRegionHandler;
    }

    public InvitationDao getInvitationDao() {
        return invitationDao;
    }

    public PlayerDatabaseManager getPlayerDatabaseManager() {
        return playerDatabaseManager;
    }

    public ShrineConfigManager getShrineConfigManager() {
        return shrineConfigManager;
    }
}
