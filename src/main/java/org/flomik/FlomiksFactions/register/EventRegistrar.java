package org.flomik.FlomiksFactions.register;

import org.bukkit.plugin.PluginManager;
import org.flomik.FlomiksFactions.FlomiksFactions;
import org.flomik.FlomiksFactions.clan.managers.NexusConfigManager;
import org.flomik.FlomiksFactions.listener.*;
import org.flomik.FlomiksFactions.listener.CastleInteractListener;

public class EventRegistrar {
    public static void registerEvents(FlomiksFactions plugin) {
        PluginManager pm = plugin.getServer().getPluginManager();

        PlayerDataListener playerJoinListener = new PlayerDataListener(plugin, plugin.getPlayerDataHandler(), plugin.getClanManager());
        pm.registerEvents(playerJoinListener, plugin);
        playerJoinListener.startPeriodicStatsUpdate(plugin);

        pm.registerEvents(new playerClanTerritoryListener(plugin.getClanManager(), plugin), plugin);
        pm.registerEvents(new PlayerDeathStrengthListener(plugin.getPlayerDataHandler()), plugin);
        pm.registerEvents(new ChatPrefixListener(plugin.getClanManager()), plugin);
        pm.registerEvents(new ClanTNTListener(plugin.getClanManager(), plugin), plugin);
        pm.registerEvents(new MenuProtectionListener(), plugin);
        pm.registerEvents(new BeaconExplosionListener(plugin.getBeaconManager(), plugin.getBeaconDao(), NexusConfigManager.getInt("tnt-radius")), plugin);
        pm.registerEvents(new NexusBlockListener(plugin.getClaimRegionHandler(), plugin.getClanManager()), plugin);
        pm.registerEvents(new CastleInteractListener(plugin.getCastleEvent(), plugin.getLootManager()), plugin);
    }
}
