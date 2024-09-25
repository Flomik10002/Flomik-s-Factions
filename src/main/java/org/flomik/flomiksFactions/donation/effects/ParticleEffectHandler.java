package org.flomik.flomiksFactions.donation.effects;

import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class ParticleEffectHandler {

    private final Plugin plugin;
    private final Map<Player, BukkitRunnable> particleTasks; // Для отслеживания активных задач
    private final Map<Player, Color> playerColors; // Для отслеживания цвета частиц игрока

    public ParticleEffectHandler(Plugin plugin) {
        this.plugin = plugin;
        this.particleTasks = new HashMap<>();
        this.playerColors = new HashMap<>();
    }

    // Метод для включения частиц с предопределенным цветом для игрока
    public void enableParticlesForPlayer(Player player, Color color) {
        if (particleTasks.containsKey(player)) {
            return; // Если задача уже запущена для игрока, ничего не делаем
        }

        playerColors.put(player, color); // Сохраняем выбранный цвет

        // Создаем и запускаем задачу
        BukkitRunnable particleTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isOnline()) {
                    Particle.DustOptions dustOptions = new Particle.DustOptions(color, 1);
                    player.getWorld().spawnParticle(Particle.REDSTONE, player.getLocation().add(0, 0.1, 0), 5, dustOptions);
                } else {
                    this.cancel(); // Останавливаем задачу, если игрок оффлайн
                }
            }
        };

        // Запускаем задачу с повторением каждые 2 тика (быстрое обновление)
        particleTask.runTaskTimer(plugin, 0L, 2L);
        particleTasks.put(player, particleTask); // Сохраняем задачу для этого игрока
    }

    // Метод для отключения частиц для игрока
    public void disableParticlesForPlayer(Player player) {
        BukkitRunnable task = particleTasks.remove(player); // Убираем задачу из карты и получаем её
        if (task != null) {
            task.cancel(); // Останавливаем задачу, если она существует
        }
        playerColors.remove(player); // Убираем цвет игрока
    }

    // Проверка, активированы ли частицы у игрока
    public boolean areParticlesEnabled(Player player) {
        return particleTasks.containsKey(player);
    }

    // Получение текущего цвета игрока
    public Color getPlayerColor(Player player) {
        return playerColors.getOrDefault(player, Color.WHITE); // Возвращаем WHITE, если цвета нет
    }
}