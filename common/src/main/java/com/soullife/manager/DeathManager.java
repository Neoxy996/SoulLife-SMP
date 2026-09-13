package com.soullife.manager;

import com.soullife.data.PlayerData;
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

    private static final ItemStack[] currentSacrificeItems = DEFAULT_SACRIFICE_ITEMS.clone();

    public static PlayerData getPlayerData(UUID uuid) {
        return playerDataMap.computeIfAbsent(uuid, id -> new PlayerData());
    }

    public static PlayerData getPlayerData(net.minecraft.server.level.ServerPlayer player) {
        return getPlayerData(player.getUUID());
    }

    public static int getDeathCount(net.minecraft.server.level.ServerPlayer player) {
        return getPlayerData(player).getDeathCount();
    }

    public static void addDeaths(net.minecraft.server.level.ServerPlayer player, int amount) {
        getPlayerData(player).addDeaths(amount);
    }

    public static void setDeaths(net.minecraft.server.level.ServerPlayer player, int amount) {
        getPlayerData(player).setDeathCount(amount);
    }

    public static void removeDeaths(net.minecraft.server.level.ServerPlayer player, int amount) {
        int current = getDeathCount(player);
        int newCount = Math.max(0, current - amount);
        getPlayerData(player).setDeathCount(newCount);
    }

    public static boolean isGhost(net.minecraft.server.level.ServerPlayer player) {
        return getPlayerData(player).isGhost();
    }

    public static void setGhost(net.minecraft.server.level.ServerPlayer player, boolean ghost) {
        getPlayerData(player).setGhost(ghost);
    }

    public static boolean isPermanentSpectator(net.minecraft.server.level.ServerPlayer player) {
        return getPlayerData(player).isPermanentSpectator();
    }

    public static void setPermanentSpectator(net.minecraft.server.level.ServerPlayer player, boolean value) {
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
}
