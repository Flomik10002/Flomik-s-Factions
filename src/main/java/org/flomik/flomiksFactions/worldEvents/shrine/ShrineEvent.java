package org.flomik.flomiksFactions.worldEvents.shrine;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.flomik.flomiksFactions.FlomiksFactions;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShrineEvent {

    private final FlomiksFactions plugin;
    private List<Location> shrineLocations;
    private Location activeShrineLocation;
    private File shrineFile;
    private FileConfiguration shrineConfig;

    public ShrineEvent(FlomiksFactions plugin) {
        this.plugin = plugin;
        this.shrineFile = new File(plugin.getDataFolder(), "shrines.yml");
        this.shrineConfig = YamlConfiguration.loadConfiguration(shrineFile);
        this.shrineLocations = new ArrayList<>();

        loadShrinesFromFile();
        startDailyEventScheduler(); // Запускаем ежедневный планировщик
    }

    // Загрузка точек святилищ из файла shrines.yml
    public void loadShrinesFromFile() {
        if (shrineConfig.contains("shrines")) {
            List<?> locations = shrineConfig.getList("shrines");
            for (Object obj : locations) {
                if (obj instanceof String) {
                    shrineLocations.add(stringToLocation((String) obj));
                }
            }
        }
    }

    // Сохранение точек святилищ в файл shrines.yml
    public void saveShrinesToFile() {
        List<String> locationStrings = new ArrayList<>();
        for (Location loc : shrineLocations) {
            locationStrings.add(locationToString(loc));
        }
        shrineConfig.set("shrines", locationStrings);

        try {
            shrineConfig.save(shrineFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Не удалось сохранить shrines.yml");
        }
    }

    // Преобразование Location в строку для записи в файл
    private String locationToString(Location loc) {
        return loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }

    // Преобразование строки в Location для загрузки из файла
    private Location stringToLocation(String locStr) {
        String[] parts = locStr.split(",");
        World world = Bukkit.getWorld(parts[0]);
        int x = Integer.parseInt(parts[1]);
        int y = Integer.parseInt(parts[2]);
        int z = Integer.parseInt(parts[3]);
        return new Location(world, x, y, z);
    }

    // Добавление новой точки святилища
    public void addShrineLocation(Player player) {
        Location loc = player.getLocation();
        shrineLocations.add(loc);
        saveShrinesToFile(); // Сразу сохраняем после добавления

        // Строим святилище из блоков золота 3x3
        buildShrine(loc);

        // Создаем регион через WorldGuard
        createWorldGuardRegion(player, loc);

        player.sendMessage("Точка Святилища Опыта добавлена в координатах: X=" + loc.getBlockX() + " Y=" + loc.getBlockY() + " Z=" + loc.getBlockZ());
    }

    // Создание WorldGuard региона для защиты всего чанка, где находится святилище
    private void createWorldGuardRegion(Player player, Location location) {
        World world = location.getWorld();
        Chunk chunk = location.getChunk(); // Получаем чанк по местоположению игрока или точки святилища

        String regionName = "shrine_" + chunk.getX() + "_" + chunk.getZ();
        WorldGuard wg = WorldGuard.getInstance();
        RegionContainer container = wg.getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(world));

        if (regions != null) {
            // Устанавливаем границы чанка, начиная с минимальной точки до максимальной
            BlockVector3 min = BlockVector3.at(chunk.getX() << 4, 0, chunk.getZ() << 4);  // Левый нижний угол чанка (X,Z координаты чанка и Y = 0)
            BlockVector3 max = BlockVector3.at((chunk.getX() << 4) + 15, world.getMaxHeight(), (chunk.getZ() << 4) + 15);  // Правый верхний угол чанка (X,Z = конец чанка, Y = максимальная высота мира)

            // Создаем регион для всего чанка
            ProtectedCuboidRegion region = new ProtectedCuboidRegion(regionName, min, max);

            try {
                // Устанавливаем флаги для защиты чанка
                region.setFlag(Flags.BLOCK_BREAK, StateFlag.State.DENY);   // Запрещаем ломать блоки
                region.setFlag(Flags.BLOCK_PLACE, StateFlag.State.DENY);   // Запрещаем ставить блоки
                region.setFlag(Flags.TNT, StateFlag.State.DENY);           // Запрещаем взрывы TNT
                region.setFlag(Flags.CREEPER_EXPLOSION, StateFlag.State.DENY); // Запрещаем взрывы криперов
                region.setFlag(Flags.MOB_SPAWNING, StateFlag.State.DENY);   // Запрещаем спавн мобов
                region.setFlag(Flags.PVP, StateFlag.State.ALLOW);          // Разрешаем PvP (можно убрать, если не нужно)

                // Добавляем регион в WorldGuard
                regions.addRegion(region);

                // Назначаем владельца региона
                DefaultDomain owners = region.getOwners();
                owners.addPlayer(player.getUniqueId());
                region.setOwners(owners);

                player.sendMessage("Регион чанка был успешно создан и защищен с помощью WorldGuard.");
            } catch (Exception e) {
                player.sendMessage("Не удалось создать регион: " + e.getMessage());
            }
        }
    }

    // Строим платформу святилища (3x3 блока из золота)
    private void buildShrine(Location location) {
        World world = location.getWorld();

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                Block block = world.getBlockAt(location.clone().add(dx, -1, dz));
                block.setType(Material.GOLD_BLOCK);
            }
        }
    }

    // Деактивация активного святилища после захвата
    public void deactivateShrine() {
        if (activeShrineLocation != null) {
            activeShrineLocation = null;
        }
    }

    // Завершение всех активных точек
    public void deleteAllSanctuaries() {
        // Получаем WorldGuard и менеджер регионов для каждого мира
        WorldGuard wg = WorldGuard.getInstance();
        for (World world : Bukkit.getWorlds()) {
            RegionContainer container = wg.getPlatform().getRegionContainer();
            RegionManager regions = container.get(BukkitAdapter.adapt(world));

            if (regions != null) {
                List<String> regionsToRemove = new ArrayList<>();

                // Ищем все регионы, которые относятся к шрайнам
                for (ProtectedRegion region : regions.getRegions().values()) {
                    if (region.getId().startsWith("shrine_")) {
                        regionsToRemove.add(region.getId());
                    }
                }

                // Удаляем найденные регионы
                for (String regionId : regionsToRemove) {
                    try {
                        regions.removeRegion(regionId);
                        Bukkit.broadcastMessage("Регион шрайна " + regionId + " был удален.");
                    } catch (Exception e) {
                        plugin.getLogger().severe("Не удалось удалить регион: " + regionId);
                    }
                }
            }
        }

        // Очищаем все активные точки шрайнов
        shrineLocations.clear();
        saveShrinesToFile(); // Сохраняем изменения в файл

        Bukkit.broadcastMessage("Все точки Святилищ Опыта и приваты шрайнов были удалены.");
    }


    // Возвращаем текущую активную точку святилища
    public Location getActiveShrineLocation() {
        return activeShrineLocation;
    }

    // Отмена активного ивента Святилище
    public void cancelShrineEvent() {
        if (activeShrineLocation == null) {
            Bukkit.broadcastMessage("Нет активного ивента Святилище Опыта для отмены.");
            return;
        }

        // Оповещаем игроков об отмене
        Bukkit.broadcastMessage("Ивент Святилище Опыта был отменен.");

        // Деактивируем текущую точку
        activeShrineLocation = null;
    }

    // Удаление точки, на которой стоит игрок
    public void removeShrineLocation(Player player) {
        Location playerLocation = player.getLocation();
        Location locationToRemove = null;

        // Ищем точку с такими же координатами, как у игрока
        for (Location loc : shrineLocations) {
            if (loc.getBlockX() == playerLocation.getBlockX()
                    && loc.getBlockY() == playerLocation.getBlockY()
                    && loc.getBlockZ() == playerLocation.getBlockZ()) {
                locationToRemove = loc;
                break;
            }
        }

        if (locationToRemove != null) {
            shrineLocations.remove(locationToRemove);
            saveShrinesToFile();
            player.sendMessage("Точка Святилища Опыта на координатах X=" + locationToRemove.getBlockX()
                    + " Y=" + locationToRemove.getBlockY() + " Z=" + locationToRemove.getBlockZ() + " была удалена.");
        } else {
            player.sendMessage("Точка на текущих координатах не найдена.");
        }
    }

    // Запуск ивента: выбираем случайную точку святилища
    public void startShrineEvent() {
        if (shrineLocations.isEmpty()) {
            Bukkit.broadcastMessage("Нет доступных точек Святилища Опыта для запуска.");
            return;
        }

        // Выбираем случайную активную точку
        Random random = new Random();
        activeShrineLocation = shrineLocations.get(random.nextInt(shrineLocations.size()));

        // Строим святилище из блоков золота, если их еще нет
        buildShrine(activeShrineLocation);

        // Оповещаем игроков о начале ивента
        Bukkit.broadcastMessage(ChatColor.GREEN + "Ивент Святилище Опыта начался! Координаты святилища: " +
                "X: " + ChatColor.YELLOW + activeShrineLocation.getBlockX() + ChatColor.GREEN + ", Y: " + ChatColor.YELLOW + activeShrineLocation.getBlockY() + ChatColor.GREEN + ", Z: " + ChatColor.YELLOW + activeShrineLocation.getBlockZ());

        // Начинаем захват святилища
        ShrineCaptureManager captureManager = new ShrineCaptureManager(this, plugin, plugin.clanManager);
        captureManager.startCaptureMechanism();
    }

    // Планировщик для запуска ивента в случайное время с 16:00 до 18:00 по МСК
    private void startDailyEventScheduler() {
        new BukkitRunnable() {
            @Override
            public void run() {
                // Используем московский часовой пояс
                ZoneId moscowZoneId = ZoneId.of("Europe/Moscow");
                LocalTime currentTime = LocalTime.now(moscowZoneId);
                LocalTime startTime = LocalTime.of(16, 0);
                LocalTime endTime = LocalTime.of(18, 0);

                // Проверка, что текущее время находится в промежутке 16:00 - 18:00 по МСК
                if (currentTime.isAfter(startTime) && currentTime.isBefore(endTime)) {
                    // Определяем случайное время в этом промежутке
                    Random random = new Random();
                    int randomMinutes = random.nextInt(120 + 1); // Случайное число минут между 0 и 120 (2 часа)

                    // Планируем запуск ивента через случайное количество минут
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            deactivateShrine();
                            startShrineEvent();
                        }
                    }.runTaskLater(plugin, randomMinutes * 60L); // Переводим минуты в тики
                    this.cancel(); // Останавливаем проверку на этот день после запуска планировщика
                }
            }
        }.runTaskTimer(plugin, 0L, 1200L); // Проверка каждую минуту (1200 тиков = 1 минута)
    }

    public List<Location> getShrineLocations() {
        return new ArrayList<>(shrineLocations); // Возвращаем копию списка точек святилищ
    }
}
