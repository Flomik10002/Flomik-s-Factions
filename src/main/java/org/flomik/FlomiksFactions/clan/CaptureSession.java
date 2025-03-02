package org.flomik.FlomiksFactions.clan;

import org.bukkit.boss.BossBar;

public class CaptureSession {
    private final String regionId;
    private final BossBar bossBar;
    private final Clan defendingClan;
    private final Clan attackingClan;

    public CaptureSession(String regionId, BossBar bossBar, Clan defendingClan, Clan attackingClan) {
        this.regionId = regionId;
        this.bossBar = bossBar;
        this.defendingClan = defendingClan;
        this.attackingClan = attackingClan;
    }

    public String getRegionId() {
        return regionId;
    }

    public BossBar getBossBar() {
        return bossBar;
    }

    public Clan getDefendingClan() {
        return defendingClan;
    }

    public Clan getAttackingClan() {
        return attackingClan;
    }
}
