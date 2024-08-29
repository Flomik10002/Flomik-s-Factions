package org.flomik.flomiksFactions;

import org.bukkit.plugin.java.JavaPlugin;
import org.flomik.flomiksFactions.commands.clan.ClanCommand;
import org.flomik.flomiksFactions.commands.clan.ClanManager;
import org.flomik.flomiksFactions.commands.clan.ClanPvPListener;

public final class FlomiksFactions extends JavaPlugin {

    private ClanManager clanManager;

    @Override
    public void onEnable() {
        this.clanManager = new ClanManager(this);
        getCommand("clan").setExecutor(new ClanCommand(clanManager));
        new ClanPvPListener(this, clanManager);
    }

    @Override
    public void onDisable() {
        clanManager.saveAllClans();
    }

    public ClanManager getClanManager() {
        return clanManager;
    }
}
