package org.flomik.FlomiksFactions.register;

import org.flomik.FlomiksFactions.FlomiksFactions;
import org.flomik.FlomiksFactions.admin.commands.SetStrengthCommand;
import org.flomik.FlomiksFactions.chat.commands.ChatCommandHandler;
import org.flomik.FlomiksFactions.clan.ClanCommand;
import org.flomik.FlomiksFactions.clan.commands.clanInteractions.ClanChunksCommand;
import org.flomik.FlomiksFactions.donation.DonationCommand;
import org.flomik.FlomiksFactions.donation.effects.commands.ParticleCommand;
import org.flomik.FlomiksFactions.menu.chunkMenu.ChunkMenuCommand;
import org.flomik.FlomiksFactions.player.commands.PlayerCommand;
import org.flomik.FlomiksFactions.worldEvents.castle.CastleCommand;
import org.flomik.FlomiksFactions.worldEvents.randomEvents.commands.EventCommand;
import org.flomik.FlomiksFactions.worldEvents.shrine.commands.ShrineCommand;

public class CommandRegistrar {
    public static void registerCommands(FlomiksFactions plugin) {
        plugin.getCommand("player").setExecutor(new PlayerCommand(plugin.getPlayerDataHandler(), plugin.getClanManager()));
        plugin.getCommand("clan").setExecutor(new ClanCommand(plugin.getClanManager(), plugin.getPlayerDataHandler(), plugin, plugin.getMenuManager(), plugin.getShrineEvent()));
        plugin.getCommand("clanchat").setExecutor(new ChatCommandHandler(plugin.getClanManager()));
        plugin.getCommand("chunkmap").setExecutor(new ChunkMenuCommand(plugin.getMenuManager()));
        plugin.getCommand("setSt").setExecutor(new SetStrengthCommand(plugin.getPlayerDataHandler()));
        plugin.getCommand("clanchunks").setExecutor(new ClanChunksCommand(plugin, plugin.getClanManager()));
        plugin.getCommand("shrine").setExecutor(new ShrineCommand(plugin.getShrineEvent()));
        plugin.getCommand("donate").setExecutor(new DonationCommand(plugin.getDonationManager(), plugin.getPlayerDataHandler()));
        plugin.getCommand("particles").setExecutor(new ParticleCommand(plugin.getDonationManager(), plugin.getParticleEffectHandler(), plugin.getPlayerDataHandler()));
        plugin.getCommand("event").setExecutor(new EventCommand(plugin.getEventManager(), plugin));
        plugin.getCommand("castle").setExecutor(new CastleCommand(plugin.getLootManager()));
    }
}
