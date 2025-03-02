package org.flomik.FlomiksFactions.register;

import org.flomik.FlomiksFactions.FlomiksFactions;
import org.flomik.FlomiksFactions.commands.SetStrengthCommand;
import org.flomik.FlomiksFactions.commands.ChatHandler;
import org.flomik.FlomiksFactions.commands.ClanCommand;
import org.flomik.FlomiksFactions.commands.ClanChunksCommand;
import org.flomik.FlomiksFactions.commands.ChunkMenuCommand;
import org.flomik.FlomiksFactions.commands.PlayerCommand;
import org.flomik.FlomiksFactions.commands.CastleCommand;
import org.flomik.FlomiksFactions.commands.EventCommand;
import org.flomik.FlomiksFactions.commands.ShrineCommand;

public class CommandRegistrar {
    public static void registerCommands(FlomiksFactions plugin) {
        plugin.getCommand("player").setExecutor(new PlayerCommand(plugin.getPlayerDataHandler(), plugin.getClanManager()));
        plugin.getCommand("clan").setExecutor(new ClanCommand(plugin.getClanManager(), plugin.getPlayerDataHandler(), plugin, plugin.getMenuManager(), plugin.getShrineEvent(), plugin.getBeaconDao(), plugin.getBeaconManager()));
        plugin.getCommand("clanchat").setExecutor(new ChatHandler(plugin.getClanManager()));
        plugin.getCommand("chunkmap").setExecutor(new ChunkMenuCommand(plugin.getMenuManager()));
        plugin.getCommand("setSt").setExecutor(new SetStrengthCommand(plugin.getPlayerDataHandler()));
        plugin.getCommand("clanchunks").setExecutor(new ClanChunksCommand(plugin, plugin.getClanManager()));
        plugin.getCommand("shrine").setExecutor(new ShrineCommand(plugin.getShrineEvent()));
        plugin.getCommand("event").setExecutor(new EventCommand(plugin.getEventManager(), plugin));
        plugin.getCommand("castle").setExecutor(new CastleCommand(plugin.getLootManager()));
    }
}
