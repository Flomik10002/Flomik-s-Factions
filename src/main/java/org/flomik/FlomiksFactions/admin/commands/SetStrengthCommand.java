package org.flomik.FlomiksFactions.admin.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.flomik.FlomiksFactions.player.PlayerDataHandler;

public class SetStrengthCommand implements CommandExecutor {

    private final PlayerDataHandler playerDataHandler;

    public SetStrengthCommand(PlayerDataHandler playerDataHandler) {
        this.playerDataHandler = playerDataHandler;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String playerName = args[0];
        if (playerName == null) {
            sender.sendMessage("/addSt <игрок> <число>");
            return true;
        }if (args[1] == null) {
            sender.sendMessage("/addSt <игрок> <число>");
            return true;
        }
        Player player = Bukkit.getPlayer(playerName);
        int strength = Integer.parseInt(args[1]);


        if (player == null) {
            sender.sendMessage("Игрок не найден");
            return true;
        }

        playerDataHandler.setPlayerStrength(playerName, strength);

        return true;
    }
}