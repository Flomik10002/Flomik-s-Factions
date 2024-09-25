package org.flomik.flomiksFactions.worldEvents.shrine;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.flomik.flomiksFactions.FlomiksFactions;
import org.flomik.flomiksFactions.clan.Clan;
import org.flomik.flomiksFactions.clan.ClanManager;

import java.util.HashSet;
import java.util.Set;

public class ShrineCaptureManager {

    private final ShrineEvent shrineEvent;
    private final FlomiksFactions plugin;
    private final ClanManager clanManager; // Менеджер кланов для работы с кланами
    private BossBar captureBossBar;
    private int captureTime;
    private final Set<Player> playersInZone;
    private Clan capturingClan;

    public ShrineCaptureManager(ShrineEvent shrineEvent, FlomiksFactions plugin, ClanManager clanManager) {
        this.shrineEvent = shrineEvent;
        this.plugin = plugin;
        this.clanManager = clanManager;
        this.playersInZone = new HashSet<>();
        this.captureTime = 0;
        this.capturingClan = null; // Клан, который захватывает точку

        // Инициализируем боссбар
        captureBossBar = Bukkit.createBossBar("Захват Святилища", BarColor.YELLOW, BarStyle.SOLID);
    }

    public void startCaptureMechanism() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Location shrineLocation = shrineEvent.getActiveShrineLocation();
                if (shrineLocation == null) {
                    this.cancel();
                    return;
                }

                // Проверяем игроков на святилище
                checkPlayersOnShrine(shrineLocation);

                // Если никто не находится на точке, сбрасываем таймер
                if (playersInZone.isEmpty()) {
                    captureBossBar.setVisible(false);
                    captureTime = 0;
                    capturingClan = null; // Сбрасываем текущий клан
                } else {
                    // Если есть игроки из одного клана, обновляем таймер
                    if (capturingClan != null) {
                        captureBossBar.setVisible(true);
                        captureBossBar.setProgress(captureTime / 120.0); // 120 секунд = 2 минуты

                        // Если захват завершен, оповещаем и завершаем процесс
                        if (captureTime >= 120) {
                            Bukkit.broadcastMessage(ChatColor.GREEN + "Клан " + ChatColor.YELLOW + capturingClan.getName() + ChatColor.GREEN + " захватил Святилище Опыта!");
                            onShrineCaptureSuccess();
                            shrineEvent.deactivateShrine(); // Деактивируем точку
                            captureBossBar.setVisible(false); // Скрываем боссбар после захвата
                            this.cancel();
                        } else {
                            captureTime++;
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L); // Запускаем задачу каждую секунду
    }

    // Проверяем игроков на точке святилища
    private void checkPlayersOnShrine(Location shrineLocation) {
        Set<Player> newPlayersInZone = new HashSet<>();
        Clan currentClan = null;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (isPlayerOnShrine(player, shrineLocation)) {
                Clan playerClan = clanManager.getPlayerClan(player.getName());

                if (playerClan == null) {
                    continue; // Игнорируем игроков без клана
                }

                if (currentClan == null) {
                    currentClan = playerClan;
                } else if (!currentClan.getName().equals(playerClan.getName())) {
                    // Если на точке игроки из разных кланов, сбрасываем захват
                    sendEnemyOnShrineMessage(currentClan);
                    sendEnemyOnShrineMessage(playerClan);
                    captureTime = 0;
                    captureBossBar.setVisible(false);
                    capturingClan = null;
                    return;
                }

                newPlayersInZone.add(player);
                captureBossBar.addPlayer(player); // Добавляем игрока в боссбар
            } else {
                captureBossBar.removePlayer(player); // Убираем игрока из боссбара, если он не на точке
            }
        }

        if (!newPlayersInZone.isEmpty()) {
            if (capturingClan == null) {
                capturingClan = currentClan;

                // Добавляем всех участников клана в боссбар
                for (String member : capturingClan.getMembers()) {
                    Player clanMember = Bukkit.getPlayer(member);
                    if (clanMember != null && clanMember.isOnline()) {
                        captureBossBar.addPlayer(clanMember); // Добавляем всех участников клана
                    }
                }

                clanManager.sendClanMessage(capturingClan, ChatColor.GREEN + "Ваш клан начал захват Святилища Опыта. Не покидайте территорию в течение 2 минут.");
            }

            // Также добавляем игроков, находящихся в радиусе 16 блоков от точки
            addNearbyPlayersToBossBar(shrineLocation);
        }

        playersInZone.clear();
        playersInZone.addAll(newPlayersInZone);
    }

    private void addNearbyPlayersToBossBar(Location shrineLocation) {
        double radius = 16.0; // Радиус 16 блоков
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getWorld().equals(shrineLocation.getWorld()) &&
                    player.getLocation().distance(shrineLocation) <= radius) {
                captureBossBar.addPlayer(player); // Добавляем игрока в боссбар
            } else {
                captureBossBar.removePlayer(player); // Убираем игрока из боссбара, если он вышел за пределы радиуса
            }
        }
    }

    // Вывод сообщения о том, что вражеский игрок зашел на точку
    private void sendEnemyOnShrineMessage(Clan clan) {
        for (String member : clan.getMembers()) {
            Player clanMember = Bukkit.getPlayer(member);
            if (clanMember != null && clanMember.isOnline()) {
                sendActionBar(clanMember, ChatColor.RED + "Враг зашел на точку захвата");
            }
        }
    }

    // Проверка, находится ли игрок на точке святилища
    private boolean isPlayerOnShrine(Player player, Location shrineLocation) {
        Location playerLocation = player.getLocation();
        return playerLocation.getBlockX() >= shrineLocation.getBlockX() - 1
                && playerLocation.getBlockX() <= shrineLocation.getBlockX() + 1
                && playerLocation.getBlockZ() >= shrineLocation.getBlockZ() - 1
                && playerLocation.getBlockZ() <= shrineLocation.getBlockZ() + 1
                && playerLocation.getBlockY() == shrineLocation.getBlockY();
    }

    // Награждаем всех участников клана каждые 3 минуты в течение 2 часов (240 тиков по 3 минуты)
    private void rewardPlayersInZone() {
        if (capturingClan != null) {
            BukkitRunnable rewardTask = new BukkitRunnable() {
                int cycles = 0;
                final int maxCycles = 40; // 40 циклов по 3 минуты = 120 минут (2 часа)

                @Override
                public void run() {
                    if (cycles >= maxCycles || capturingClan == null) {
                        // Завершаем задачу после 2 часов или если клан сброшен
                        this.cancel();
                        return;
                    }

                    // Проходим по всем членам клана
                    for (String member : capturingClan.getMembers()) {
                        Player clanMember = Bukkit.getPlayer(member);
                        if (clanMember != null && clanMember.isOnline()) {
                            // Выдаем 27 очков опыта
                            clanMember.giveExp(27);
                            clanMember.sendMessage(ChatColor.GREEN + "Вы получили 27 опыта за захват Святилища Опыта!");
                        }
                    }

                    // Увеличиваем количество циклов
                    cycles++;
                }
            };

            // Запускаем задачу с интервалом 3 минуты (3600 тиков)
            rewardTask.runTaskTimer(plugin, 0L, 3600L);
        }
    }

    // Вызываем, когда клан захватил святилище
    private void onShrineCaptureSuccess() {
        if (capturingClan != null) {
            // Увеличиваем опыт клана на 1 и сохраняем
            capturingClan.addClanXp(1);
            clanManager.saveClan(capturingClan); // Сохраняем изменения в клане

            clanManager.sendClanMessage(capturingClan, ChatColor.GREEN + "Ваш клан захватил Святилище Опыта и получил 1 очко опыта!");

            // Запускаем механизм награждения игроков опытом
            rewardPlayersInZone();
        }
    }

    private void sendActionBar(Player player, String message) {
        new BukkitRunnable() {
            @Override
            public void run() {
                player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                        net.md_5.bungee.api.chat.TextComponent.fromLegacyText(message));
            }
        }.runTask(plugin);
    }
}
