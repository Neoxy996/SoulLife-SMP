package com.soullife.manager;

import com.soullife.util.MessageUtil;
import com.soullife.util.ScoreboardManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;

/**
 * SoulLife - CommonEvents
 * Shared event logic called by platform-specific event handlers.
 */
public class CommonEvents {

    /**
     * Called when a player dies.
     */
    public static void onPlayerDeath(ServerPlayer player) {
        // If already permanent spectator → do nothing
        if (DeathManager.isPermanentSpectator(player)) return;

        // If ghost and didn't pay item - don't count as death
        if (DeathManager.isGhost(player)) {
            ItemStack required = SacrificeManager.getRequiredItem(player);
            if (!SacrificeManager.hasItem(player, required)) {
                return;
            }
        }

        // Add death
        DeathManager.addDeaths(player, 1);
        int deaths = DeathManager.getDeathCount(player);

        // Update Tab
        ScoreboardManager.updateTabDisplay(player);

        // Death 20 → Still ghost (need dragon egg to revive)
        if (deaths >= 20) {
            GhostManager.applyGhostState(player);
            MessageUtil.sendDeathMessages(player, SacrificeManager.getRequiredItem(player));
            return;
        }

        // Normal death → Ghost mode
        GhostManager.applyGhostState(player);

        // Get required sacrifice item
        ItemStack required = SacrificeManager.getRequiredItem(player);

        // Send messages
        MessageUtil.sendDeathMessages(player, required);
    }

    /**
     * Called when player respawns (after ghost state).
     */
    public static void onPlayerRespawn(ServerPlayer player) {
        if (DeathManager.isPermanentSpectator(player)) {
            player.setGameMode(GameType.SPECTATOR);
            return;
        }

        if (DeathManager.isGhost(player)) {
            // Re-apply ghost state after respawn
            GhostManager.applyGhostState(player);
        }

        // Refresh Tab
        ScoreboardManager.updateTabDisplay(player);
    }

    /**
     * Called when player logs in.
     */
    public static void onPlayerLogin(ServerPlayer player) {
        // Restore ghost state
        if (DeathManager.isPermanentSpectator(player)) {
            player.setGameMode(GameType.SPECTATOR);
        } else if (DeathManager.isGhost(player)) {
            GhostManager.refreshGhostEffects(player);
            player.setGameMode(GameType.SURVIVAL);  // ✅ SURVIVAL, not SPECTATOR
        }

        // Update Tab
        ScoreboardManager.updateTabDisplay(player);
    }

    /**
     * Called on item pickup - check if it's the sacrifice item.
     */
    public static void onItemPickup(ServerPlayer player, ItemStack pickedUp) {
        if (!DeathManager.isGhost(player)) return;
        if (DeathManager.isPermanentSpectator(player)) return;

        ItemStack required = SacrificeManager.getRequiredItem(player);
        if (!required.isEmpty() && pickedUp.is(required.getItem())) {
            // Try sacrifice - if successful, return (item consumed)
            if (SacrificeManager.trySacrifice(player)) {
                return;
            }
        }
    }

    /**
     * Called when player tries to break a block (prevent in ghost mode).
     */
    public static boolean onBlockBreak(ServerPlayer player) {
        // Ghost can't break blocks
        return DeathManager.isGhost(player);
    }

    public static boolean onBlockPlace(ServerPlayer player) {
        // Ghost can't place blocks - return item to player
        if (DeathManager.isGhost(player)) {
            ItemStack heldItem = player.getMainHandItem();
            if (!heldItem.isEmpty()) {
                // Create single item to return
                ItemStack singleItem = heldItem.copy();
                singleItem.setCount(1);
                
                // Add item back to inventory
                if (!player.getInventory().add(singleItem)) {
                    // If inventory full, drop it
                    player.drop(singleItem, false);
                }
            }
            return true;
        }
        return false;
    }
}
