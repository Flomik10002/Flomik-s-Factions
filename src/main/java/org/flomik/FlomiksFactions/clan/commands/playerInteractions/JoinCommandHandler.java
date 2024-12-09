package org.flomik.FlomiksFactions.clan.commands.playerInteractions;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.flomik.FlomiksFactions.clan.Clan;
import org.flomik.FlomiksFactions.clan.ClanManager;

import java.util.Set;

public class JoinCommandHandler {

    private final ClanManager clanManager;

    public JoinCommandHandler(ClanManager clanManager) {
        this.clanManager = clanManager;
    }

    public boolean handleCommand(Player player, String[] args) {
        if (args.length > 1) {
            String clanName = args[1].trim().toLowerCase();
            if (clanName.isEmpty()) {
                player.sendMessage(ChatColor.RED + "Название клана не может быть пустым.");
                return true;
            }

            Clan invitedClan = clanManager.getClan(clanName);
            if (invitedClan == null) {
                player.sendMessage(ChatColor.RED + "Клан с таким названием не существует.");
                return true;
            }

            String invitedPlayer = player.getName();
            // Получаем приглашения игрока из БД
            Set<String> invites = clanManager.getInvitationDao().getInvitationsForPlayer(invitedPlayer);

            if (invites.contains(clanName)) {
                // Игрок имеет приглашение в этот клан
                try {
                    clanManager.joinClan(clanName, invitedPlayer);
                    // Удаляем приглашение из базы
                    clanManager.getInvitationDao().removeInvitation(invitedPlayer, clanName);

                    clanManager.sendClanMessage(invitedClan, ChatColor.YELLOW + player.getName() + ChatColor.GREEN + " присоединился к вашему клану " + ChatColor.YELLOW + invitedClan.getName() + ChatColor.GREEN + "!");
                } catch (IllegalArgumentException e) {
                    player.sendMessage(ChatColor.RED + e.getMessage());
                }
            } else {
                player.sendMessage(ChatColor.RED + "Вы не получили приглашение в этот клан.");
            }
        } else {
            TextComponent usageMessage = new TextComponent(ChatColor.YELLOW + "Использование: ");
            TextComponent clickCommand = new TextComponent(ChatColor.GOLD + "/clan join <название>");
            clickCommand.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/clan join "));
            usageMessage.addExtra(clickCommand);
            player.spigot().sendMessage(usageMessage);
        }
        return true;
    }
}
