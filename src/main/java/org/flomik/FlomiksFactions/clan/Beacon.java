package org.flomik.FlomiksFactions.clan;

import org.bukkit.Location;

public class Beacon {
    private final String clanName;
    private final Location location;
    private int health;
    private final String regionId;

    public Beacon(String clanName, Location location, int health, String regionId) {
        this.clanName = clanName;
        this.location = location;
        this.health = health;
        this.regionId = regionId;
    }

    public String getClanName() {
        return clanName;
    }

    public Location getLocation() {
        return location;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public String getRegionId() {
        return regionId;
    }
}
