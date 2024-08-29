package org.flomik.flomiksFactions;

import org.bukkit.plugin.java.JavaPlugin;
import org.flomik.flomiksFactions.commands.clan.ClanCommand;
import org.flomik.flomiksFactions.commands.clan.ClanManager;
import org.flomik.flomiksFactions.events.ClanPvPListener;
import org.flomik.flomiksFactions.commands.player.PlayerCommand;
import org.flomik.flomiksFactions.events.PlayerJoinListener;
import org.flomik.flomiksFactions.commands.player.PlayerDataHandler;

public final class FlomiksFactions extends JavaPlugin {

    private ClanManager clanManager;
    private PlayerDataHandler playerDataHandler;

    @Override
    public void onEnable() {
        this.clanManager = new ClanManager(this);
        this.playerDataHandler = new PlayerDataHandler(this);

        PlayerJoinListener playerJoinListener = new PlayerJoinListener(playerDataHandler);


        getServer().getPluginManager().registerEvents(playerJoinListener, this);

        getCommand("player").setExecutor(new PlayerCommand(playerDataHandler, clanManager));
        getCommand("clan").setExecutor(new ClanCommand(clanManager, playerDataHandler, this));
        new ClanPvPListener(this, clanManager);
        playerJoinListener.startPeriodicStatsUpdate(this);
    }

    @Override
    public void onDisable() {
        clanManager.saveAllClans();
    }
}
