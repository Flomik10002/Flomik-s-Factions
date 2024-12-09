package org.flomik.FlomiksFactions.donation.effects;

import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class ParticleEffectHandler {

    private final Plugin plugin;
    private final Map<Player, BukkitRunnable> particleTasks;
    private final Map<Player, Color> playerColors;

    public ParticleEffectHandler(Plugin plugin) {
        this.plugin = plugin;
        this.particleTasks = new HashMap<>();
        this.playerColors = new HashMap<>();
    }


    public void enableParticlesForPlayer(Player player, Color color) {
        if (particleTasks.containsKey(player)) {
            return;
        }

        playerColors.put(player, color);


        BukkitRunnable particleTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isOnline()) {
                    Particle.DustOptions dustOptions = new Particle.DustOptions(color, 1);
                    player.getWorld().spawnParticle(Particle.REDSTONE, player.getLocation().add(0, 0.1, 0), 5, dustOptions);
                } else {
                    this.cancel();
                }
            }
        };


        particleTask.runTaskTimer(plugin, 0L, 2L);
        particleTasks.put(player, particleTask);
    }


    public void disableParticlesForPlayer(Player player) {
        BukkitRunnable task = particleTasks.remove(player);
        if (task != null) {
            task.cancel();
        }
        playerColors.remove(player);
    }


    public boolean areParticlesEnabled(Player player) {
        return particleTasks.containsKey(player);
    }


    public Color getPlayerColor(Player player) {
        return playerColors.getOrDefault(player, Color.WHITE);
    }
}