package org.flomik.FlomiksFactions.register; //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression

import org.bukkit.plugin.PluginManager;
import org.flomik.FlomiksFactions.FlomiksFactions;
import org.flomik.FlomiksFactions.clan.nexus.NexusBlockListener;
import org.flomik.FlomiksFactions.clan.nexus.NexusConfigManager;
import org.flomik.FlomiksFactions.listener.*;
import org.flomik.FlomiksFactions.listener.CastleInteractListener;

public class EventRegistrar { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    public static void registerEvents(FlomiksFactions plugin) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        PluginManager pm = plugin.getServer().getPluginManager(); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression

        PlayerDataListener playerJoinListener = new PlayerDataListener(plugin, plugin.getPlayerDataHandler(), plugin.getClanManager()); //NOPMD - suppressed LongVariable - TODO explain reason for suppression //NOPMD - suppressed LongVariable - TODO explain reason for suppression //NOPMD - suppressed LongVariable - TODO explain reason for suppression
        pm.registerEvents(playerJoinListener, plugin);
        playerJoinListener.startPeriodicStatsUpdate(plugin);

        pm.registerEvents(new PlayerClanTerritoryListener(plugin.getClanManager()), plugin);
        pm.registerEvents(new PlayerDeathStrengthListener(plugin.getPlayerDataHandler()), plugin);
        pm.registerEvents(new ChatPrefixListener(plugin.getClanManager()), plugin);
        pm.registerEvents(new ClanTNTListener(plugin.getClanManager(), plugin), plugin);
        pm.registerEvents(new MenuProtectionListener(), plugin);
        pm.registerEvents(new BeaconExplosionListener(plugin.getBeaconManager(), plugin.getBeaconDao(), NexusConfigManager.getInt("tnt-radius")), plugin);
        pm.registerEvents(new NexusBlockListener(plugin.getClaimRegionHandler(), plugin.getClanManager()), plugin);
        pm.registerEvents(new CastleInteractListener(plugin.getCastleEvent(), plugin.getLootManager()), plugin);
        pm.registerEvents(new BeaconProtectionListener(plugin.getBeaconManager()), plugin);
    }
}
