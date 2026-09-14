package com.soullife.manager;

import com.soullife.data.PlayerData;
import com.soullife.data.PlayerDataNBT;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DeathManager {

    private static final Map<UUID, PlayerData> playerDataMap = new HashMap<>();

    public static final ItemStack[] DEFAULT_SACRIFICE_ITEMS = new ItemStack[]{
        new ItemStack(Items.IRON_INGOT),
        new ItemStack(Items.GOLD_INGOT),
        new ItemStack(Items.EMERALD),
        new ItemStack(Items.DIAMOND),
        new ItemStack(Items.GOLDEN_APPLE),
        new ItemStack(Items.IRON_BLOCK),
        new ItemStack(Items.GOLD_BLOCK),
        new ItemStack(Items.EMERALD_BLOCK),
        new ItemStack(Items.DIAMOND_BLOCK),
        new ItemStack(Items.END_CRYSTAL),
        new ItemStack(Items.NETHERITE_SCRAP),
        new ItemStack(Items.NETHERITE_INGOT),
        new ItemStack(Items.TOTEM_OF_UNDYING),
        new ItemStack(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
        new ItemStack(Items.WITHER_SKELETON_SKULL),
        new ItemStack(Items.NETHER_STAR),
        new ItemStack(Items.ENCHANTED_GOLDEN_APPLE),
        new ItemStack(Items.NETHERITE_BLOCK),
        new ItemStack(Items.BEACON),
        new ItemStack(Items.DRAGON_EGG),
    };

    private static final ItemStack[] currentSacrificeItems =
        DEFAULT_SACRIFICE_ITEMS.clone();

    public static PlayerData getPlayerData(UUID uuid) {
        return playerDataMap.computeIfAbsent(uuid, id -> new PlayerData());
    }

    public static PlayerData getPlayerData(ServerPlayer player) {
        return getPlayerData(player.getUUID());
    }

    public static int getDeathCount(ServerPlayer player) {
        return getPlayerData(player).getDeathCount();
    }

    public static void addDeaths(ServerPlayer player, int amount) {
        getPlayerData(player).addDeaths(amount);
    }

    public static void setDeaths(ServerPlayer player, int amount) {
        getPlayerData(player).setDeathCount(amount);
    }

    public static void removeDeaths(ServerPlayer player, int amount) {
        int current = getDeathCount(player);
        int newCount = Math.max(0, current - amount);
        getPlayerData(player).setDeathCount(newCount);
    }

    public static boolean isGhost(ServerPlayer player) {
        return getPlayerData(player).isGhost();
    }

    public static void setGhost(ServerPlayer player, boolean ghost) {
        getPlayerData(player).setGhost(ghost);
    }

    public static boolean isPermanentSpectator(ServerPlayer player) {
        return getPlayerData(player).isPermanentSpectator();
    }

    public static void setPermanentSpectator(ServerPlayer player, boolean value) {
        getPlayerData(player).setPermanentSpectator(value);
    }
    public static ItemStack getSacrificeItem(int deathIndex) {
        if (deathIndex < 0 || deathIndex >= 20) return ItemStack.EMPTY;
        return currentSacrificeItems[deathIndex].copy();
    }

    public static void setSacrificeItem(int deathIndex, ItemStack item) {
        if (deathIndex < 0 || deathIndex >= 20) return;
        currentSacrificeItems[deathIndex] = item.copy();
    }

    public static void resetSacrificeItems() {
        for (int i = 0; i < 20; i++) {
            currentSacrificeItems[i] = DEFAULT_SACRIFICE_ITEMS[i].copy();
        }
    }

    public static ItemStack[] getCurrentSacrificeItems() {
        return currentSacrificeItems;
    }

    public static void saveAllToNBT(ServerPlayer player, CompoundTag tag) {
        saveToNBT(player, tag);
    }

    public static void loadAllFromNBT(ServerPlayer player, CompoundTag tag) {
        loadFromNBT(player, tag);
    }

    public static void saveToNBT(ServerPlayer player, CompoundTag tag) {
        PlayerDataNBT.writeToTag(tag, getPlayerData(player));
    }

    public static void loadFromNBT(ServerPlayer player, CompoundTag tag) {
        PlayerDataNBT.readFromTag(tag, getPlayerData(player));
    }
}
