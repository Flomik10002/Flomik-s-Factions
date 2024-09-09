package org.flomik.flomiksFactions.commands.clan.handlers.playerInteractions;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.flomik.flomiksFactions.commands.clan.Clan;
import org.flomik.flomiksFactions.commands.clan.ClanManager;

import java.util.Objects;

public class DemoteCommandHandler {

    private final ClanManager clanManager;

    public DemoteCommandHandler(ClanManager clanManager) {
        this.clanManager = clanManager;
    }

    public boolean handleCommand(Player player, String[] args) {
        if (args.length > 1) {
            String targetDemPlayerName = args[1];
            Clan clan = clanManager.getPlayerClan(player.getName());

            if (clan == null) {
                player.sendMessage(ChatColor.RED + "Не удалось найти ваш клан.");
                return false;
            }

            String playerRole = clan.getRole(player.getName());
            if (!playerRole.equals("Лидер") && !playerRole.equals("Заместитель")) {
                player.sendMessage(ChatColor.RED + "Только Лидер или Заместитель могут понижать ранг.");
                return false;
            }

            try {
                clan.demoteMember(player.getName(), targetDemPlayerName);
                clanManager.sendClanMessage(clan, ChatColor.GREEN + "Игрок " + ChatColor.YELLOW + targetDemPlayerName + ChatColor.GREEN + " понижен в должности.");

                String targetPlayerRole = clan.getRole(targetDemPlayerName);
                Player targetPlayer = Bukkit.getPlayerExact(targetDemPlayerName);
                if(Objects.equals(targetPlayerRole, "Воин")) {
                    this.clanManager.removePlayerFromClanRegions(targetPlayer, clan);
                    this.clanManager.addPlayerToClanRegionsAsMember(targetPlayer, clan);
                }
                if(Objects.equals(targetPlayerRole, "Рекрут")) {
                    this.clanManager.removePlayerFromClanRegions(targetPlayer, clan);
                }
            } catch (IllegalArgumentException e) {
                player.sendMessage(ChatColor.RED + e.getMessage());
            }
            clanManager.saveClan(clan);
            return true;
        } else {
            TextComponent usageMessage = new TextComponent(ChatColor.YELLOW + "Использование: ");
            TextComponent clickCommand = new TextComponent(ChatColor.GOLD + "/clan demote <игрок>");
            clickCommand.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/clan demote "));
            usageMessage.addExtra(clickCommand);
            player.spigot().sendMessage(usageMessage);
        }
        return true;
    }
}
