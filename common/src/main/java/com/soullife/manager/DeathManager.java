package com.soullife.manager;

import com.soullife.data.PlayerData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * SoulLife - DeathManager
 * Tracks player deaths and manages ghost/survival states.
 */
public class DeathManager {

    private static final Map<UUID, PlayerData> playerDataMap = new HashMap<>();

    // ─── Default sacrifice items (Death 1-20) ─────────────────────────────────
    public static final ItemStack[] DEFAULT_SACRIFICE_ITEMS = new ItemStack[]{
        new ItemStack(Items.IRON_INGOT),           // Death 1
        new ItemStack(Items.GOLD_INGOT),           // Death 2
        new ItemStack(Items.EMERALD),              // Death 3
        new ItemStack(Items.DIAMOND),              // Death 4
        new ItemStack(Items.GOLDEN_APPLE),         // Death 5
        new ItemStack(Items.IRON_BLOCK),           // Death 6
        new ItemStack(Items.GOLD_BLOCK),           // Death 7
        new ItemStack(Items.EMERALD_BLOCK),        // Death 8
        new ItemStack(Items.DIAMOND_BLOCK),        // Death 9
        new ItemStack(Items.END_CRYSTAL),          // Death 10
        new ItemStack(Items.NETHERITE_SCRAP),      // Death 11
        new ItemStack(Items.NETHERITE_INGOT),      // Death 12
        new ItemStack(Items.TOTEM_OF_UNDYING),     // Death 13
        new ItemStack(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), // Death 14
        new ItemStack(Items.WITHER_SKELETON_SKULL),// Death 15
        new ItemStack(Items.NETHER_STAR),          // Death 16
        new ItemStack(Items.ENCHANTED_GOLDEN_APPLE),// Death 17
        new ItemStack(Items.NETHERITE_BLOCK),      // Death 18
        new ItemStack(Items.BEACON),               // Death 19
        new ItemStack(Items.DRAGON_EGG),           // Death 20
    };

    // ─── Current sacrifice items (can be edited by admin) ─────────────────────
    private static final ItemStack[] currentSacrificeItems =
        DEFAULT_SACRIFICE_ITEMS.clone();

    // ─── Player Data ──────────────────────────────────────────────────────────
    public static PlayerData getPlayerData(UUID uuid) {
        return playerDataMap.computeIfAbsent(uuid, id -> new PlayerData());
    }

    public static PlayerData getPlayerData(ServerPlayer player) {
        return getPlayerData(player.getUUID());
    }

    // ─── Death Count ──────────────────────────────────────────────────────────
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

    // ─── Ghost State ─────────────────────────────────────────────────────────
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

    // ─── Sacrifice Items ─────────────────────────────────────────────────────
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

    // ─── NBT Save/Load ───────────────────────────────────────────────────────
    public static void saveAllToNBT(ServerPlayer player, CompoundTag tag) {
        saveToNBT(player, tag);
    }

    public static void loadAllFromNBT(ServerPlayer player, CompoundTag tag) {
        loadFromNBT(player, tag);
    }

    public static void saveToNBT(ServerPlayer player, CompoundTag tag) {
        PlayerData data = getPlayerData(player);
        tag.putInt("soullife_deaths", data.getDeathCount());
        tag.putBoolean("soullife_ghost", data.isGhost());
        tag.putBoolean("soullife_permanent", data.isPermanentSpectator());
    }

    public static void loadFromNBT(ServerPlayer player, CompoundTag tag) {
        if (tag.contains("soullife_deaths")) {
            PlayerData data = getPlayerData(player);
            data.setDeathCount(tag.getInt("soullife_deaths"));
            data.setGhost(tag.getBoolean("soullife_ghost"));
            data.setPermanentSpectator(tag.getBoolean("soullife_permanent"));
        }
    }
}
