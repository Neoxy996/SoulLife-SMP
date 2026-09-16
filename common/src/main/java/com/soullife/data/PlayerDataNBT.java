package com.soullife.data;

import net.blay09.mods.balm.api.Balm;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import com.soullife.manager.LanguageManager;

public class PlayerDataNBT {
    
    private static final String SOULLIFE_TAG = "SoulLife";
    private static final String DEATHS_KEY = "Deaths";
    private static final String GHOST_KEY = "IsGhost";
    private static final String PERMANENT_KEY = "IsPermanentSpectator";
    private static final String LANGUAGE_KEY = "Language";
    private static final String SACRIFICE_ITEMS_KEY = "SacrificeItems";
    private static final String WORLD_KEY = "World";
    
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

    public static void saveToPersistent(ServerPlayer player, PlayerData data) {
        CompoundTag playerData = Balm.getHooks().getPersistentData(player);
        String worldKey = getWorldKey(player);
        
        CompoundTag worldData = playerData.getCompound(worldKey);
        CompoundTag soulLifeTag = new CompoundTag();
        writeToTag(soulLifeTag, data);
        worldData.put(SOULLIFE_TAG, soulLifeTag);
        playerData.put(worldKey, worldData);
        
        saveLanguageToPersistent(player, playerData, worldKey);
        saveSacrificeItemsToPersistent(player, playerData, worldKey);
    }

    public static PlayerData loadFromPersistent(ServerPlayer player) {
        CompoundTag playerData = Balm.getHooks().getPersistentData(player);
        String worldKey = getWorldKey(player);
        
        CompoundTag worldData = playerData.getCompound(worldKey);
        CompoundTag soulLifeTag = worldData.getCompound(SOULLIFE_TAG);
        
        loadLanguageFromPersistent(player, playerData, worldKey);
        loadSacrificeItemsFromPersistent(player, playerData, worldKey);
        
        return deserialize(soulLifeTag);
    }
    
    private static String getWorldKey(ServerPlayer player) {
        try {
            String levelName = player.level().getServer().getWorldData().getLevelName();
            return "SoulLife_" + levelName;
        } catch (Exception e) {
            return "SoulLife_world";
        }
    }
    
    private static void saveLanguageToPersistent(ServerPlayer player, CompoundTag playerData, String worldKey) {
        String language = LanguageManager.getLanguage(player);
        CompoundTag worldData = playerData.getCompound(worldKey);
        CompoundTag soulLifeTag = worldData.getCompound(SOULLIFE_TAG);
        soulLifeTag.putString(LANGUAGE_KEY, language);
    }
    
    private static void loadLanguageFromPersistent(ServerPlayer player, CompoundTag playerData, String worldKey) {
        CompoundTag worldData = playerData.getCompound(worldKey);
        CompoundTag soulLifeTag = worldData.getCompound(SOULLIFE_TAG);
        if (soulLifeTag.contains(LANGUAGE_KEY, Tag.TAG_STRING)) {
            String language = soulLifeTag.getString(LANGUAGE_KEY);
            LanguageManager.setLanguage(player, language);
        }
    }
    
    private static void saveSacrificeItemsToPersistent(ServerPlayer player, CompoundTag playerData, String worldKey) {
        CompoundTag worldData = playerData.getCompound(worldKey);
        CompoundTag soulLifeTag = worldData.getCompound(SOULLIFE_TAG);
        ListTag itemsList = new ListTag();
        
        ItemStack[] items = com.soullife.manager.DeathManager.getCurrentSacrificeItems();
        for (ItemStack item : items) {
            CompoundTag itemTag = new CompoundTag();
            item.save(itemTag);
            itemsList.add(itemTag);
        }
        
        soulLifeTag.put(SACRIFICE_ITEMS_KEY, itemsList);
    }
    
    private static void loadSacrificeItemsFromPersistent(ServerPlayer player, CompoundTag playerData, String worldKey) {
        CompoundTag worldData = playerData.getCompound(worldKey);
        CompoundTag soulLifeTag = worldData.getCompound(SOULLIFE_TAG);
        if (soulLifeTag.contains(SACRIFICE_ITEMS_KEY, Tag.TAG_LIST)) {
            ListTag itemsList = soulLifeTag.getList(SACRIFICE_ITEMS_KEY, Tag.TAG_COMPOUND);
            
            for (int i = 0; i < Math.min(itemsList.size(), 20); i++) {
                CompoundTag itemTag = itemsList.getCompound(i);
                ItemStack item = ItemStack.of(itemTag);
                com.soullife.manager.DeathManager.setSacrificeItem(i, item);
            }
        }
    }
}
