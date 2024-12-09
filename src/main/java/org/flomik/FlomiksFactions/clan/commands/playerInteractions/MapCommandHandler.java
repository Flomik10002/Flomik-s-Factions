package org.flomik.FlomiksFactions.clan.commands.playerInteractions;

import org.bukkit.entity.Player;
import org.flomik.FlomiksFactions.menu.MenuManager;

public class MapCommandHandler {

    private final MenuManager menuManager;

    public MapCommandHandler(MenuManager menuManager) {
        this.menuManager = menuManager;
    }

    public boolean handleCommand(Player player) {
        menuManager.openChunkMenu(player);
        return true;
    }
}