
package org.flomik.FlomiksFactions;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.flomik.FlomiksFactions.databases.ClanDao;
import org.flomik.FlomiksFactions.databases.ClanDatabaseManager;
import org.flomik.FlomiksFactions.databases.InvitationDao;
import org.flomik.FlomiksFactions.databases.PlayerDatabaseManager;
import org.flomik.FlomiksFactions.donation.DonationManager;
import org.flomik.FlomiksFactions.donation.effects.ParticleEffectHandler;
import org.flomik.FlomiksFactions.register.CommandRegistrar;
import org.flomik.FlomiksFactions.register.EventRegistrar;
import org.flomik.FlomiksFactions.worldEvents.castle.*;
import org.flomik.FlomiksFactions.worldEvents.randomEvents.EventScheduler;
import org.flomik.FlomiksFactions.worldEvents.randomEvents.RandomEventManager;
import org.flomik.FlomiksFactions.clan.ClanManager;
import org.flomik.FlomiksFactions.listener.*;
import org.flomik.FlomiksFactions.player.PlayerDataHandler;
import org.flomik.FlomiksFactions.menu.MenuManager;
import org.flomik.FlomiksFactions.worldEvents.shrine.ShrineEvent;
import org.flomik.FlomiksFactions.worldGuard.TNTManager;

public final class FlomiksFactions extends JavaPlugin {

    private ClanManager clanManager;
    private MenuManager menuManager;
    private PlayerDataHandler playerDataHandler;
    private ShrineEvent shrineEvent;
    private CastleEvent castleEvent;
    private DonationManager donationManager;
    private ParticleEffectHandler particleEffectHandler;
    private RandomEventManager eventManager;
    private Economy economy;
    private EventScheduler eventScheduler;
    private ClanDatabaseManager clanDatabaseManager;
    private ClanDao clanDao;
    private InvitationDao invitationDao;
    private PlayerDatabaseManager playerDatabaseManager;
    private CastleLootManager lootManager;
    private HeadsManager headsManager;

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
        if (shrineEvent != null) shrineEvent.saveShrinesToFile();
    }

    private void closeDatabase() {
        if (clanDatabaseManager != null) clanDatabaseManager.close();
    }

    private void denyClanTNT() {
        new TNTManager(clanManager).denyTNTForAllClans();
    }

    private void stopEvents() {
        if (eventManager.isRunning()) eventManager.stopEvent();
    }

    private void setupConfigs() {
        CastleConfigManager.setup(this);
        CastleConfigManager.loadConfig();
        CastleConfigManager.get().options().copyDefaults(true);
        CastleConfigManager.save();
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
        } catch (Exception e) {
            getLogger().severe("Ошибка при настройке базы данных: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    private void setupDependencies() {
        if (!setupEconomy()) {
            getLogger().severe("Vault не найден! Экономика не будет работать.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.clanManager = new ClanManager(this, clanDao, invitationDao);
        this.lootManager = new CastleLootManager(this);
        this.headsManager = new HeadsManager(this);
        this.playerDataHandler = new PlayerDataHandler(this);
        this.eventManager = new RandomEventManager(this);
        this.eventScheduler = new EventScheduler(this, eventManager);
        this.donationManager = new DonationManager(this, playerDataHandler);
        this.menuManager = new MenuManager(this, clanManager);
        this.shrineEvent = new ShrineEvent(this);
        this.castleEvent = new CastleEvent(this, headsManager);
        this.particleEffectHandler = new ParticleEffectHandler(this);
    }

    private void startSchedulers() {
        new StrengthTickTask(playerDataHandler).addStrength(this);
        new ClanPvPListener(this, clanManager);
        new Placeholders(this, playerDataHandler, clanManager).register();

        eventScheduler.start();
    }

    public boolean setupEconomy() {
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

    public MenuManager getMenuManager() {
        return menuManager;
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

    public DonationManager getDonationManager() {
        return donationManager;
    }

    public ParticleEffectHandler getParticleEffectHandler() {
        return particleEffectHandler;
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

    public InvitationDao getInvitationDao() {
        return invitationDao;
    }

    public PlayerDatabaseManager getPlayerDatabaseManager() {
        return playerDatabaseManager;
    }
}
