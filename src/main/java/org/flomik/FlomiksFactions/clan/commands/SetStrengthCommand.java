package org.flomik.FlomiksFactions.clan.commands; //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression //NOPMD - suppressed PackageCase - TODO explain reason for suppression

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.flomik.FlomiksFactions.player.PlayerDataHandler;

public class SetStrengthCommand implements CommandExecutor { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression

    private final PlayerDataHandler playerDataHandler; //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression

    public SetStrengthCommand(PlayerDataHandler playerDataHandler) { //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression //NOPMD - suppressed CommentRequired - TODO explain reason for suppression
        this.playerDataHandler = playerDataHandler;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) { //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed MethodArgumentCouldBeFinal - TODO explain reason for suppression
        String playerName = args[0]; //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        if (playerName == null) {
            sender.sendMessage("/addSt <игрок> <число>");
            return true; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
        }if (args[1] == null) {
            sender.sendMessage("/addSt <игрок> <число>");
            return true; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
        }
        Player player = Bukkit.getPlayer(playerName); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression
        int strength = Integer.parseInt(args[1]); //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression //NOPMD - suppressed LocalVariableCouldBeFinal - TODO explain reason for suppression


        if (player == null) {
            sender.sendMessage("Игрок не найден");
            return true; //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression //NOPMD - suppressed OnlyOneReturn - TODO explain reason for suppression
        }

        playerDataHandler.setPlayerStrength(playerName, strength);

        return true;
    }
}