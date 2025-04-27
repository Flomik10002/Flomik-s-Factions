package org.flomik.FlomiksFactions;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.flomik.FlomiksFactions.clan.managers.*;
import org.flomik.FlomiksFactions.clan.commands.handlers.clanInteractions.ClaimRegionHandler;
import org.flomik.FlomiksFactions.clan.commands.handlers.clanInteractions.UnclaimRegionHandler;
import org.flomik.FlomiksFactions.clan.nexus.Beacon;
import org.flomik.FlomiksFactions.clan.nexus.BeaconCaptureManager;
import org.flomik.FlomiksFactions.clan.nexus.BeaconManager;
import org.flomik.FlomiksFactions.clan.nexus.NexusConfigManager;
import org.flomik.FlomiksFactions.clan.notifications.ClanNotificationService;
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

import java.util.List;

public final class FlomiksFactions extends JavaPlugin {

    // ------------------------------------------------------------------------
    //  ССЫЛКИ НА МЕНЕДЖЕРЫ/DAO/ETC
    // ------------------------------------------------------------------------
    private ClanManager clanManager;
    private ClanNotificationService clanNotificationService;
    private BeaconManager beaconManager;
    private BeaconDao beaconDao;
    private ChunkMenuManager chunkMenuManager;
    private PlayerDataHandler playerDataHandler;
    private ShrineEvent shrineEvent;
    private CastleEvent castleEvent;
    private RandomEventManager eventManager;
    private EventScheduler eventScheduler;
    private ClanDatabaseManager clanDatabaseManager;
    private ClanDao clanDao;
    private InvitationDao invitationDao;
    private PlayerDatabaseManager playerDatabaseManager;
    private CastleLootManager lootManager;
    private HeadsManager headsManager;
    private ShrineConfigManager shrineConfigManager; // если используете
    private UnclaimRegionHandler unclaimRegionHandler;
    private ClaimRegionHandler claimRegionHandler;
    private BeaconCaptureManager captureManager;

    // ------------------------------------------------------------------------
    //  ДРУГИЕ ПОЛЯ
    // ------------------------------------------------------------------------
    private Economy economy;

    // ------------------------------------------------------------------------
    //  ЖИЗНЕННЫЙ ЦИКЛ
    // ------------------------------------------------------------------------

    @Override
    public void onEnable() {
        initializeConfigs();
        initializeDatabase();
        initializeDependencies();
        registerCommandsAndEvents();
        startSchedulers();

        getLogger().info("Плагин FlomiksFactions успешно запущен!");
    }

    @Override
    public void onDisable() {
        saveData();
        closeDatabase();
        denyClanTNT();
        stopEvents();

        getLogger().info("Плагин FlomiksFactions успешно отключён.");
    }

    // ------------------------------------------------------------------------
    //  ИНИЦИАЛИЗАЦИЯ
    // ------------------------------------------------------------------------

    private void initializeConfigs() {
        // Castle
        CastleConfigManager.setup(this);
        CastleConfigManager.loadConfig();
        CastleConfigManager.get().options().copyDefaults(true);
        CastleConfigManager.save();

        // Shrine
        ShrineConfigManager.setup(this);
        ShrineConfigManager.loadConfig();
        ShrineConfigManager.get().options().copyDefaults(true);
        ShrineConfigManager.save();

        // Nexus
        NexusConfigManager.setup(this);
        NexusConfigManager.loadConfig();
        NexusConfigManager.get().options().copyDefaults(true);
        NexusConfigManager.save();
    }

    private void initializeDatabase() {
        try {
            // ClanDB
            clanDatabaseManager = new ClanDatabaseManager();
            clanDatabaseManager.initDatabase(this);
            clanDatabaseManager.createTables();
            clanDao = new ClanDao(clanDatabaseManager);
            invitationDao = new InvitationDao(clanDatabaseManager);

            playerDatabaseManager = new PlayerDatabaseManager();
            playerDatabaseManager.initDatabase(this);
            playerDatabaseManager.createTables();

            beaconDao = new BeaconDao(clanDatabaseManager);

            beaconDao.getAllBeacons().forEach(beaconManager::addBeacon);

        } catch (Exception e) {
            getLogger().severe("Ошибка при настройке базы данных: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    private void initializeDependencies() {
        // Vault
        if (!setupEconomy()) {
            getLogger().severe("Vault не найден! Экономика не будет работать.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Создаём менеджеры
        clanManager = new ClanManager(this, clanDao, invitationDao);
        clanNotificationService = new ClanNotificationService(clanManager);

        beaconManager = new BeaconManager();
        captureManager = new BeaconCaptureManager(this, beaconManager, clanManager, beaconDao);
        lootManager = new CastleLootManager(this);
        headsManager = new HeadsManager(this);
        playerDataHandler = new PlayerDataHandler(this);
        eventManager = new RandomEventManager(this);
        eventScheduler = new EventScheduler(this, eventManager);
        chunkMenuManager = new ChunkMenuManager(this, clanManager);

        // Shrine
        shrineConfigManager = new ShrineConfigManager(); // если нужно
        shrineEvent = new ShrineEvent(this, shrineConfigManager);

        // Castle
        castleEvent = new CastleEvent(this, headsManager);

        // Создаём хендлеры claim/unclaim
        unclaimRegionHandler = new UnclaimRegionHandler(clanManager, clanNotificationService, beaconDao, beaconManager);
        claimRegionHandler = new ClaimRegionHandler(clanManager, clanNotificationService, beaconDao, beaconManager);
    }

    // ------------------------------------------------------------------------
    //  REGISTRATION (COMMANDS / EVENTS)
    // ------------------------------------------------------------------------
    private void registerCommandsAndEvents() {
        CommandRegistrar.registerCommands(this);
        EventRegistrar.registerEvents(this);
    }

    // ------------------------------------------------------------------------
    //  START BACKGROUND TASKS
    // ------------------------------------------------------------------------
    private void startSchedulers() {
        new StrengthTickListener(playerDataHandler).addStrength(this);
        new ClanPvPListener(this, clanManager);

        // PlaceholderAPI
        new Placeholders(this, playerDataHandler, clanManager).register();

        eventScheduler.start();

        // Если нужен check beacons HP
        // startZeroHpCheck();
    }

    // ------------------------------------------------------------------------
    //  ОТКЛЮЧЕНИЕ / СОХРАНЕНИЕ
    // ------------------------------------------------------------------------
    private void saveData() {
        if (shrineEvent != null) {
            shrineEvent.saveShrinesToFile();
        }
    }

    private void closeDatabase() {
        if (clanDatabaseManager != null) {
            clanDatabaseManager.close();
        }
    }

    private void denyClanTNT() {
        new TNTManager(clanManager).denyTNTForAllClans();
    }

    private void stopEvents() {
        if (eventManager != null && eventManager.isRunning()) {
            eventManager.stopEvent();
        }
    }

    // ------------------------------------------------------------------------
    //  ECONOMY (VAULT)
    // ------------------------------------------------------------------------
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

    // ------------------------------------------------------------------------
    //  GETTERS (если нужны другим классам)
    // ------------------------------------------------------------------------
    public Economy getEconomy() {
        return economy;
    }

    public ClanNotificationService getClanNotificationService() {
        return clanNotificationService;
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

    public ChunkMenuManager getMenuManager() {
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

    public ClanDatabaseManager getDatabaseManager() {
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

    public BeaconCaptureManager getCaptureManager() {
        return captureManager;
    }
}
