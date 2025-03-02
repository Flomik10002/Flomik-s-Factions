package org.flomik.FlomiksFactions.clan.managers;

import org.bukkit.Location;
import org.flomik.FlomiksFactions.clan.Beacon;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BeaconManager {
    private final Map<String, Beacon> beacons = new HashMap<>();

    public void addBeacon(Beacon beacon) {
        beacons.put(beacon.getRegionId(), beacon);
    }

    public Beacon getBeaconByLocation(Location loc) {
        for (Beacon beacon : beacons.values()) {
            if (beacon.getLocation().distance(loc) < 1) {
                return beacon;
            }
        }
        return null;
    }

    public Collection<Beacon> getAllBeacons() {
        return beacons.values();
    }

    public Beacon getBeaconByRegionId(String regionId) {
        return beacons.get(regionId);
    }

    public void removeBeacon(String regionId) {
        beacons.remove(regionId);
    }
}
