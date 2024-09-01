package org.flomik.flomiksFactions.commands.clan.handlers.clanInteractions;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.flomik.flomiksFactions.commands.clan.ClanManager;

public class CreateCommandHandler {

    private final ClanManager clanManager;

    public CreateCommandHandler(ClanManager clanManager) {
        this.clanManager = clanManager;
    }

    public boolean handleCommand(Player player, String[] args) {
        if (args.length > 1) {
            String clanName = args[1].toLowerCase();

            final int MAX_NAME_LENGTH = 14;

            boolean wasTruncated = clanName.length() > MAX_NAME_LENGTH;
            if (wasTruncated) {
                clanName = clanName.substring(0, MAX_NAME_LENGTH);
            }

            try {
                clanManager.createClan(clanName, player.getName());
                player.sendMessage(ChatColor.GREEN + "Клан " + clanName + " успешно создан!");

                if (wasTruncated) {
                    player.sendMessage(ChatColor.YELLOW + "Название было слишком длинным и было обрезано до 14 символов.");
                }
            } catch (IllegalArgumentException e) {
                player.sendMessage(ChatColor.RED + e.getMessage());
            }
        } else {

            TextComponent usageMessage = new TextComponent(ChatColor.YELLOW + "Пожалуйста, укажите название клана. Использование: ");
            TextComponent inviteCommand = new TextComponent(ChatColor.GOLD + "/clan create <название>");
            inviteCommand.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/clan create "));
            usageMessage.addExtra(inviteCommand);
            player.spigot().sendMessage(usageMessage);
            }
        return true;
    }
}
