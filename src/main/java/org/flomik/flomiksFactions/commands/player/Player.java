package org.flomik.flomiksFactions.commands.player;

public class Player {
    private int level;
    private int strength;
    private int maxPower;

    public Player(int level, int strength, int maxPower) {
        this.level = level;
        this.strength = strength;
        this.maxPower = maxPower;
    }

    // Геттеры и сеттеры для уровня, силы и максимальной силы
    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int getMaxPower() {
        return maxPower;
    }

    public void setMaxPower(int maxPower) {
        this.maxPower = maxPower;
    }
}