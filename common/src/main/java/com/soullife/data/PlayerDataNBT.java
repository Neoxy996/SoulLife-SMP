package com.soullife.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public class PlayerDataNBT {
    
    private static final String DEATHS_KEY = "sl_deaths";
    private static final String GHOST_KEY = "sl_ghost";
    private static final String PERMANENT_KEY = "sl_permanent";
    
    public static void writeToTag(CompoundTag tag, PlayerData data) {
        tag.putInt(DEATHS_KEY, data.getDeathCount());
        tag.putBoolean(GHOST_KEY, data.isGhost());
        tag.putBoolean(PERMANENT_KEY, data.isPermanentSpectator());
    }
    
    public static void readFromTag(CompoundTag tag, PlayerData data) {
        if (tag.contains(DEATHS_KEY, Tag.TAG_INT)) {
            data.setDeathCount(tag.getInt(DEATHS_KEY));
        }
        if (tag.contains(GHOST_KEY, Tag.TAG_BYTE)) {
            data.setGhost(tag.getBoolean(GHOST_KEY));
        }
        if (tag.contains(PERMANENT_KEY, Tag.TAG_BYTE)) {
            data.setPermanentSpectator(tag.getBoolean(PERMANENT_KEY));
        }
    }
    
    public static CompoundTag serialize(PlayerData data) {
        CompoundTag tag = new CompoundTag();
        writeToTag(tag, data);
        return tag;
    }
    
    public static PlayerData deserialize(CompoundTag tag) {
        PlayerData data = new PlayerData();
        readFromTag(tag, data);
        return data;
    }
}
