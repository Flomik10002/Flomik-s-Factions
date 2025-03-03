package org.flomik.FlomiksFactions.register; //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression

import org.flomik.FlomiksFactions.FlomiksFactions;
import org.flomik.FlomiksFactions.clan.commands.SetStrengthCommand;
import org.flomik.FlomiksFactions.clan.commands.ChatHandler;
import org.flomik.FlomiksFactions.clan.commands.ClanCommand;
import org.flomik.FlomiksFactions.clan.commands.ClanChunksCommand;
import org.flomik.FlomiksFactions.clan.commands.ChunkMenuCommand;
import org.flomik.FlomiksFactions.clan.commands.PlayerCommand;
import org.flomik.FlomiksFactions.clan.commands.CastleCommand;
import org.flomik.FlomiksFactions.clan.commands.EventCommand;
import org.flomik.FlomiksFactions.clan.commands.ShrineCommand;

public class CommandRegistrar { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
    public static void registerCommands(FlomiksFactions plugin) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
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
