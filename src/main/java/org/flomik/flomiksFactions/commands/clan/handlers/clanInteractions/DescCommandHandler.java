package org.flomik.flomiksFactions.commands.clan.handlers.clanInteractions;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.flomik.flomiksFactions.commands.clan.Clan;
import org.flomik.flomiksFactions.commands.clan.ClanManager;

import java.util.Arrays;

public class DescCommandHandler {

    private final ClanManager clanManager;

    public DescCommandHandler(ClanManager clanManager) {
        this.clanManager = clanManager;
    }

    public boolean handleCommand(Player player, String[] args) {
        if (args.length > 1) {
            String newDescription = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

            final int MAX_DESCRIPTION_LENGTH = 48;

            boolean wasTruncated = newDescription.length() > MAX_DESCRIPTION_LENGTH;
            if (wasTruncated) {
                newDescription = newDescription.substring(0, MAX_DESCRIPTION_LENGTH);
            }

            Clan playerClan = clanManager.getPlayerClan(player.getName());
            if (playerClan == null) {
                player.sendMessage(ChatColor.RED + "Вы не состоите в клане.");
                return true;
            }

            // Проверка, что игрок является владельцем клана
            String playerRole = playerClan.getRole(player.getName());
            if (!playerRole.equals("Лидер")) {
                player.sendMessage(ChatColor.RED + "Только Лидер клана может переименовать клан.");
                return true;
            }

            // Переименовываем клан
            playerClan.setDescription(newDescription);
            clanManager.updateClan(playerClan);

            // Опционально, можно уведомить всех членов клана о смене имени
            for (String memberName : playerClan.getMembers()) {
                Player member = player.getServer().getPlayer(memberName);
                if (member != null) {
                    member.sendMessage(ChatColor.GREEN + "Описание клана обновлено.");
                }
            }

            if (wasTruncated) {
                player.sendMessage(ChatColor.YELLOW + "Описание было слишком длинным и было обрезано до 48 символов.");
            }

            return false;
        } else {
            player.sendMessage(ChatColor.YELLOW + "Использование: " + ChatColor.GOLD + "/clan description <описание>");
        }
        return true;
    }
}
