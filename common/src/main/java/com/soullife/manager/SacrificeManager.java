package com.soullife.manager;

import com.soullife.util.MessageUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/**
 * SoulLife - SacrificeManager
 * Handles the sacrifice system: checks item, revives player.
 */
public class SacrificeManager {

    /**
     * Try to sacrifice the required item and revive the player.
     * @return true if sacrifice was successful
     */
    public static boolean trySacrifice(ServerPlayer player) {
        if (!DeathManager.isGhost(player)) return false;
        if (DeathManager.isPermanentSpectator(player)) return false;

        int deaths = DeathManager.getDeathCount(player);
        if (deaths <= 0 || deaths > 20) return false;

        ItemStack required = DeathManager.getSacrificeItem(deaths - 1);
        if (required.isEmpty()) return false;

        // Check if player has the required item
        if (!hasItem(player, required)) return false;

        // Remove item from inventory
        removeItem(player, required);

        // Revive player
        GhostManager.removeGhostState(player);

        // Broadcast revival message
        MessageUtil.broadcastRevival(player);

        return true;
    }

    /**
     * Check if player has the required item in inventory.
     */
    public static boolean hasItem(ServerPlayer player, ItemStack required) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty() && stack.is(required.getItem())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Remove one of the required item from player inventory.
     */
    private static void removeItem(ServerPlayer player, ItemStack required) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty() && stack.is(required.getItem())) {
                stack.shrink(1);
                return;
            }
        }
    }

    /**
     * Get required item for the player's current death count.
     */
    public static ItemStack getRequiredItem(ServerPlayer player) {
        int deaths = DeathManager.getDeathCount(player);
        if (deaths <= 0 || deaths > 20) return ItemStack.EMPTY;
        return DeathManager.getSacrificeItem(deaths - 1);
    }
}
