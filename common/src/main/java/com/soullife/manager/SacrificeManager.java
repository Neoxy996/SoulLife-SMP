package com.soullife.manager;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class SacrificeManager {

    /**
     * Try to sacrifice the required item and revive the player.
     * @return true if sacrifice was successful
     */
    public static boolean trySacrifice(ServerPlayer player) {
        if (!DeathManager.isGhost(player)) return false;
        if (DeathManager.isPermanentSpectator(player)) return false;

        ItemStack required = getRequiredItem(player);
        if (required.isEmpty()) return false;

        // Check if player has the required item
        if (!hasItem(player, required)) return false;

        // Remove item from inventory
        removeItem(player, required);

        // Revive player
        GhostManager.removeGhostState(player);

        return true;
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
     * Get required item for the player's current death count.
     */
    public static ItemStack getRequiredItem(ServerPlayer player) {
        int deaths = DeathManager.getDeathCount(player);
        
        // Death 20+ only requires dragon egg
        if (deaths >= 20) {
            return new ItemStack(Items.DRAGON_EGG, 1);
        }
        
        // Deaths 1-19 require different items
        if (deaths > 0 && deaths < 20) {
            return DeathManager.getSacrificeItem(deaths - 1);
        }
        
        return ItemStack.EMPTY;
    }
}
