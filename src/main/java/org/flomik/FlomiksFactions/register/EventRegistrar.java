package org.flomik.FlomiksFactions.register;

import org.bukkit.plugin.PluginManager;
import org.flomik.FlomiksFactions.FlomiksFactions;
import org.flomik.FlomiksFactions.donation.DonationCommand;
import org.flomik.FlomiksFactions.donation.effects.commands.ParticleCommand;
import org.flomik.FlomiksFactions.listener.*;
import org.flomik.FlomiksFactions.worldEvents.castle.CastleInteractListener;

public class EventRegistrar {
    public static void registerEvents(FlomiksFactions plugin) {
        PluginManager pm = plugin.getServer().getPluginManager();

        PlayerJoinListener playerJoinListener = new PlayerJoinListener(plugin, plugin.getPlayerDataHandler(), plugin.getClanManager());
        pm.registerEvents(playerJoinListener, plugin);
        playerJoinListener.startPeriodicStatsUpdate(plugin);

        pm.registerEvents(new OnPlayerMoveListener(plugin.getClanManager(), plugin), plugin);
        pm.registerEvents(new PlayerDeathListener(plugin.getPlayerDataHandler()), plugin);
        pm.registerEvents(new ChatPrefixListener(plugin.getClanManager()), plugin);
        pm.registerEvents(new ClanTNTListener(plugin.getClanManager(), plugin), plugin);
        pm.registerEvents(new MenuProtectionListener(), plugin);
        pm.registerEvents(new ParticleCommand(plugin.getDonationManager(), plugin.getParticleEffectHandler(), plugin.getPlayerDataHandler()), plugin);
        pm.registerEvents(new DonationCommand(plugin.getDonationManager(), plugin.getPlayerDataHandler()), plugin);
        pm.registerEvents(new CastleInteractListener(plugin.getCastleEvent(), plugin.getLootManager()), plugin);
    }
}
