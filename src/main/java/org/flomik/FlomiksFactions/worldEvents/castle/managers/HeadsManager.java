package org.flomik.FlomiksFactions.worldEvents.castle.managers;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.flomik.FlomiksFactions.FlomiksFactions;
import org.flomik.FlomiksFactions.worldEvents.castle.config.CastleConfigManager;

import java.lang.reflect.Field;
import java.util.*;

public class HeadsManager {
    private final Set<Location> spawnedHeads = new HashSet<>();
    private final FlomiksFactions plugin;
    private final List<Location> availableLocations;

    public HeadsManager(FlomiksFactions plugin) {
        this.plugin = plugin;
        this.availableLocations = loadHeadLocations();
    }

    public List<Location> loadHeadLocations() {
        List<Location> locations = new ArrayList<>();
        for (String locStr : CastleConfigManager.get().getStringList("head-locations")) {
            String[] parts = locStr.split(",");
            try {
                double x = Double.parseDouble(parts[0]);
                double y = Double.parseDouble(parts[1]);
                double z = Double.parseDouble(parts[2]);
                locations.add(new Location(Bukkit.getWorld("world"), x, y, z));
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                plugin.getLogger().warning("Неверный формат локации: " + locStr);
            }
        }
        return locations;
    }

    public void spawnRandomHeads(int count) {
        despawnAllHeads();
        Random random = new Random();
        Set<Location> selectedLocations = new HashSet<>();
        List<Location> remainingLocations = new ArrayList<>(availableLocations);
        remainingLocations.removeAll(spawnedHeads);

        for (int i = 0; i < count && !remainingLocations.isEmpty(); i++) {
            int index = random.nextInt(remainingLocations.size());
            Location location = remainingLocations.get(index);

            remainingLocations.remove(index);
            selectedLocations.add(location);
            spawnHead(location);
        }
        spawnedHeads.addAll(selectedLocations);
    }

    public void spawnHead(Location location) {
        Block block = location.getBlock();
        block.setType(org.bukkit.Material.PLAYER_HEAD);

        if (block.getState() instanceof Skull skull) {
            String textureBase64 = CastleConfigManager.get().getString("head-texture");

            if (textureBase64 == null || textureBase64.isEmpty()) {
                plugin.getLogger().warning("текстуры нет в конфиге");
                return;
            }
            GameProfile profile = new GameProfile(UUID.randomUUID(), null);
            profile.getProperties().put("textures", new Property("textures", textureBase64));
            try {
                Field profileField = skull.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(skull, profile);

                skull.update(true);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public void despawnAllHeads() {
        for (Location loc : spawnedHeads) {
            if (loc.getBlock().getType() == org.bukkit.Material.PLAYER_HEAD) {
                loc.getBlock().setType(org.bukkit.Material.AIR);
            }
        }
        spawnedHeads.clear();
    }
}
