package org.flomik.FlomiksFactions.clan.commands.handlers.playerInteractions; //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.flomik.FlomiksFactions.clan.Clan;
import org.flomik.FlomiksFactions.clan.managers.ClanManager;

import java.util.List;
import java.util.Set;

public class InviteHandler { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression

    private final ClanManager clanManager; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression

    public InviteHandler(ClanManager clanManager) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        this.clanManager = clanManager;
    }

    public boolean handleCommand(Player player, String[] args) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        if (args.length > 1) { //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression //NOPMD - suppressed AvoidLiteralsInIfCondition - TODO explain reason for suppression
            String playerName = args[1]; //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            Clan clan = clanManager.getPlayerClan(player.getName()); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            Clan playerClan = clanManager.getPlayerClan(playerName); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression

            if (clan == null) {
                player.sendMessage(ChatColor.RED + "Вы не состоите в клане.");
                return true; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
            }

            String playerRole = clan.getRole(player.getName()); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            if (!playerRole.equals("Лидер") && !playerRole.equals("Заместитель")) { //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression //NOPMD - suppressed LiteralsFirstInComparisons - TODO explain reason for suppression
                player.sendMessage(ChatColor.RED + "Только Лидер или Заместитель клана может отправлять и отзывать приглашения.");
                return true; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
            }

            if (playerClan != null) {
                player.sendMessage(ChatColor.RED + "Игрок уже участник другого клана!");
                return true; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
            }

            // Получаем текущие приглашения игрока из БД
            Set<String> invites = clanManager.getInvitationDao().getInvitationsForPlayer(playerName); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression

            if (invites.contains(clan.getName().toLowerCase())) { //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression
                // Приглашение уже есть, значит мы его отзываем
                clanManager.getInvitationDao().removeInvitation(playerName, clan.getName().toLowerCase()); //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression
                sendMessageToRole(clan, ChatColor.GREEN + "Приглашение в клан " + ChatColor.YELLOW + clan.getName() + ChatColor.GREEN + " было отменено для игрока " + ChatColor.YELLOW + playerName + ChatColor.GREEN + ".");

                Player invitedPlayer = player.getServer().getPlayer(playerName); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                if (invitedPlayer != null) {
                    invitedPlayer.sendMessage(ChatColor.RED + "Приглашение в клан " + ChatColor.YELLOW + clan.getName() + " было отменено.");
                } else {
                    player.sendMessage(ChatColor.RED + "Игрок " + playerName + " не в сети.");
                }
            } else {
                // Отправляем новое приглашение
                try {
                    clanManager.invitePlayer(clan.getName().toLowerCase(), playerName); //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression
                    sendMessageToRole(clan, ChatColor.GREEN + "Приглашение в клан " + ChatColor.YELLOW + clan.getName() + ChatColor.GREEN + " отправлено игроку " + ChatColor.YELLOW + playerName + ChatColor.GREEN + "!");
                    sendMessageToRole(clan, ChatColor.YELLOW + "Для отмены приглашения игроку " + ChatColor.GOLD + playerName + ChatColor.YELLOW + " повторите команду.");

                    Player invitedPlayer = player.getServer().getPlayer(playerName); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                    if (invitedPlayer != null) {
                        TextComponent message = new TextComponent(ChatColor.GREEN + "Вам пришло приглашение в клан " + ChatColor.YELLOW + clan.getName() + ChatColor.GREEN + " от игрока " + ChatColor.YELLOW + player.getName() + ChatColor.GREEN + ". Используйте "); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                        TextComponent joinCommand = new TextComponent(ChatColor.YELLOW + "/clan join " + clan.getName()); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
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
                            Set<String> currentInvites = clanManager.getInvitationDao().getInvitationsForPlayer(playerName); //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression //NOPMD - suppressed LawOfDemeter - TODO explain reason for suppression
                            if (currentInvites.contains(clan.getName().toLowerCase())) { //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression
                                // Удаляем приглашение по истечению времени
                                clanManager.getInvitationDao().removeInvitation(playerName, clan.getName().toLowerCase()); //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression //NOPMD - suppressed UseLocaleWithCaseConversions - TODO explain reason for suppression

                                sendMessageToRole(clan, ChatColor.RED + "Приглашение для игрока " + ChatColor.YELLOW + playerName + ChatColor.RED + " в клан " + ChatColor.YELLOW + clan.getName() + ChatColor.RED + " истекло.");
                                Player invitedPlayer = player.getServer().getPlayer(playerName); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
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
            TextComponent usageMessage = new TextComponent(ChatColor.YELLOW + "Использование: "); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            TextComponent clickCommand = new TextComponent(ChatColor.GOLD + "/clan invite <игрок>"); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            clickCommand.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/clan invite "));
            usageMessage.addExtra(clickCommand);
            player.spigot().sendMessage(usageMessage);
        }

        return true;
    }

    private void sendMessageToRole(Clan clan, String message) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        List<String> rolesToNotify = List.of("Лидер", "Заместитель"); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        for (String role : rolesToNotify) { //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            List<String> playersWithRole = clan.getPlayersWithRole(role); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
            for (String playerName : playersWithRole) { //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                Player player = Bukkit.getPlayer(playerName); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
                if (player != null) {
                    player.sendMessage(message);
                }
            }
        }
    }
}
