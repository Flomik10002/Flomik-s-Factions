package org.flomik.flomiksFactions.clan.commands.playerInteractions;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.flomik.flomiksFactions.clan.Clan;
import org.flomik.flomiksFactions.clan.ClanManager;

import java.util.List;
import java.util.Set;

public class InviteCommandHandler {

    private final ClanManager clanManager;

    public InviteCommandHandler(ClanManager clanManager) {
        this.clanManager = clanManager;
    }

    public boolean handleCommand(Player player, String[] args) {
        if (args.length > 1) {
            String playerName = args[1];
            Clan clan = clanManager.getPlayerClan(player.getName());
            Clan playerClan = clanManager.getPlayerClan(playerName);

            if (clan == null) {
                player.sendMessage(ChatColor.RED + "Вы не состоите в клане.");
                return true;
            }

            String playerRole = clan.getRole(player.getName());
            if (!playerRole.equals("Лидер") && !playerRole.equals("Заместитель")) {
                player.sendMessage(ChatColor.RED + "Только Лидер или Заместитель клана может отправлять и отзывать приглашения.");
                return true;
            }

            if (playerClan != null) {
                player.sendMessage(ChatColor.RED + "Игрок уже участник другого клана!");
                return true;
            }

            // Получаем текущие приглашения игрока из БД
            Set<String> invites = clanManager.getInvitationDao().getInvitationsForPlayer(playerName);

            if (invites.contains(clan.getName().toLowerCase())) {
                // Приглашение уже есть, значит мы его отзываем
                clanManager.getInvitationDao().removeInvitation(playerName, clan.getName().toLowerCase());
                sendMessageToRole(clan, ChatColor.GREEN + "Приглашение в клан " + ChatColor.YELLOW + clan.getName() + ChatColor.GREEN + " было отменено для игрока " + ChatColor.YELLOW + playerName + ChatColor.GREEN + ".");

                Player invitedPlayer = player.getServer().getPlayer(playerName);
                if (invitedPlayer != null) {
                    invitedPlayer.sendMessage(ChatColor.RED + "Приглашение в клан " + ChatColor.YELLOW + clan.getName() + " было отменено.");
                } else {
                    player.sendMessage(ChatColor.RED + "Игрок " + playerName + " не в сети.");
                }
            } else {
                // Отправляем новое приглашение
                try {
                    clanManager.invitePlayer(clan.getName().toLowerCase(), playerName);
                    sendMessageToRole(clan, ChatColor.GREEN + "Приглашение в клан " + ChatColor.YELLOW + clan.getName() + ChatColor.GREEN + " отправлено игроку " + ChatColor.YELLOW + playerName + ChatColor.GREEN + "!");
                    sendMessageToRole(clan, ChatColor.YELLOW + "Для отмены приглашения игроку " + ChatColor.GOLD + playerName + ChatColor.YELLOW + " повторите команду.");

                    Player invitedPlayer = player.getServer().getPlayer(playerName);
                    if (invitedPlayer != null) {
                        TextComponent message = new TextComponent(ChatColor.GREEN + "Вам пришло приглашение в клан " + ChatColor.YELLOW + clan.getName() + ChatColor.GREEN + " от игрока " + ChatColor.YELLOW + player.getName() + ChatColor.GREEN + ". Используйте ");
                        TextComponent joinCommand = new TextComponent(ChatColor.YELLOW + "/clan join " + clan.getName());
                        joinCommand.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/clan join " + clan.getName()));
                        message.addExtra(joinCommand);
                        message.addExtra(new TextComponent(ChatColor.GREEN + " для принятия приглашения."));
                        invitedPlayer.spigot().sendMessage(message);
                    } else {
                        player.sendMessage(ChatColor.RED + "Игрок " + playerName + " не в сети, но приглашение будет доступно.");
                    }

                    // Таймер для истечения приглашения
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Set<String> currentInvites = clanManager.getInvitationDao().getInvitationsForPlayer(playerName);
                            if (currentInvites.contains(clan.getName().toLowerCase())) {
                                // Удаляем приглашение по истечению времени
                                clanManager.getInvitationDao().removeInvitation(playerName, clan.getName().toLowerCase());

                                sendMessageToRole(clan, ChatColor.RED + "Приглашение для игрока " + ChatColor.YELLOW + playerName + ChatColor.RED + " в клан " + ChatColor.YELLOW + clan.getName() + ChatColor.RED + " истекло.");
                                Player invitedPlayer = player.getServer().getPlayer(playerName);
                                if (invitedPlayer != null) {
                                    invitedPlayer.sendMessage(ChatColor.RED + "Ваше приглашение в клан " + ChatColor.YELLOW + clan.getName() + ChatColor.RED + " истекло.");
                                }
                            }
                        }
                    }.runTaskLater(Bukkit.getPluginManager().getPlugin("FlomiksFactions"), 5 * 60 * 20);
                } catch (IllegalArgumentException e) {
                    player.sendMessage(ChatColor.RED + e.getMessage());
                }
            }
        } else {
            TextComponent usageMessage = new TextComponent(ChatColor.YELLOW + "Использование: ");
            TextComponent clickCommand = new TextComponent(ChatColor.GOLD + "/clan invite <игрок>");
            clickCommand.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/clan invite "));
            usageMessage.addExtra(clickCommand);
            player.spigot().sendMessage(usageMessage);
        }

        return true;
    }

    private void sendMessageToRole(Clan clan, String message) {
        List<String> rolesToNotify = List.of("Лидер", "Заместитель");
        for (String role : rolesToNotify) {
            List<String> playersWithRole = clan.getPlayersWithRole(role);
            for (String playerName : playersWithRole) {
                Player player = Bukkit.getPlayer(playerName);
                if (player != null) {
                    player.sendMessage(message);
                }
            }
        }
    }
}
