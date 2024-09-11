
package org.flomik.flomiksFactions;

import org.bukkit.plugin.java.JavaPlugin;
import org.flomik.flomiksFactions.commands.MenuCommand;
import org.flomik.flomiksFactions.commands.chat.ChatCommandHandler;
import org.flomik.flomiksFactions.commands.clan.Clan;
import org.flomik.flomiksFactions.commands.clan.ClanCommand;
import org.flomik.flomiksFactions.commands.clan.ClanManager;
import org.flomik.flomiksFactions.events.*;
import org.flomik.flomiksFactions.commands.player.PlayerCommand;
import org.flomik.flomiksFactions.commands.player.PlayerDataHandler;
import org.flomik.flomiksFactions.menu.chunkMenu.MenuManager;

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


        getCommand("player").setExecutor(new PlayerCommand(playerDataHandler, clanManager));
        getCommand("clan").setExecutor(new ClanCommand(clanManager, playerDataHandler, this));
        getCommand("clanchat").setExecutor(new ChatCommandHandler(clanManager));
        getCommand("chunkmap").setExecutor(new MenuCommand(menuManager));

        new StrengthTickTask(playerDataHandler).addStrength(this);
        new ClanPvPListener(this, clanManager);
        playerJoinListener.startPeriodicStatsUpdate(this);
    }

    @Override
    public void onDisable() {
        clanManager.saveAllClans();
    }
}
