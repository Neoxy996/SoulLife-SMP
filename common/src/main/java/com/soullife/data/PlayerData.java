package com.soullife.data;

/**
 * SoulLife - PlayerData
 * Stores death count and ghost state per player.
 */
public class PlayerData {

    private int deathCount = 0;
    private boolean isGhost = false;
    private boolean permanentSpectator = false;

    public int getDeathCount() { return deathCount; }
    public void setDeathCount(int count) { this.deathCount = Math.max(0, count); }
    public void addDeaths(int amount) { this.deathCount += amount; }

    public boolean isGhost() { return isGhost; }
    public void setGhost(boolean ghost) { this.isGhost = ghost; }

    public boolean isPermanentSpectator() { return permanentSpectator; }
    public void setPermanentSpectator(boolean value) { this.permanentSpectator = value; }
}
