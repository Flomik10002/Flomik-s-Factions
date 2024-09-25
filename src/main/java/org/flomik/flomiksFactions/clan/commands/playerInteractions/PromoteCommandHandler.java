package org.flomik.flomiksFactions.clan.commands.playerInteractions;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.flomik.flomiksFactions.clan.Clan;
import org.flomik.flomiksFactions.clan.ClanManager;

import java.util.Objects;

public class PromoteCommandHandler {

    private final ClanManager clanManager;

    public PromoteCommandHandler(ClanManager clanManager) {
        this.clanManager = clanManager;
    }

    public boolean handleCommand(Player player, String[] args) {
        if (args.length > 1) {
            String targetPromPlayerName = args[1];
            Clan clan = clanManager.getPlayerClan(player.getName());

            if (clan == null) {
                player.sendMessage(ChatColor.RED + "Не удалось найти ваш клан.");
                return false;
            }

            String playerRole = clan.getRole(player.getName());
            if (!playerRole.equals("Лидер") && !playerRole.equals("Заместитель")) {
                player.sendMessage(ChatColor.RED + "Только Лидер или Заместитель могут повышать ранг.");
                return false;
            }

            try {
                clan.promoteMember(player.getName(), targetPromPlayerName);
                clanManager.sendClanMessage(clan, ChatColor.GREEN + "Игрок " + ChatColor.YELLOW + targetPromPlayerName + ChatColor.GREEN + " повышен в должности.");

                String targetPlayerRole = clan.getRole(targetPromPlayerName);
                Player targetPlayer = Bukkit.getPlayerExact(targetPromPlayerName);
                if(Objects.equals(targetPlayerRole, "Заместитель")) {
                    clanManager.removePlayerFromClanRegions(targetPlayer, clan);
                    clanManager.addPlayerToClanRegionsAsOwner(targetPlayer, clan);
                }
                if(Objects.equals(targetPlayerRole, "Воин")) {
                    clanManager.addPlayerToClanRegionsAsMember(targetPlayer, clan);
                }
            } catch (IllegalArgumentException e) {
                player.sendMessage(ChatColor.RED + e.getMessage());
            }
            clanManager.saveClan(clan);
            return true;
        } else {
            TextComponent usageMessage = new TextComponent(ChatColor.YELLOW + "Использование: ");
            TextComponent clickCommand = new TextComponent(ChatColor.GOLD + "/clan promote <игрок>");
            clickCommand.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/clan promote "));
            usageMessage.addExtra(clickCommand);
            player.spigot().sendMessage(usageMessage);
        }
        return true;
    }
}
