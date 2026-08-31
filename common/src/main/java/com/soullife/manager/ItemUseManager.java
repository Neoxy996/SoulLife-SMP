package com.soullife.manager;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.EnderPearlItem;
import net.minecraft.world.item.ItemStack;

public class ItemUseManager {

    public static boolean canUseItem(ServerPlayer player, ItemStack item) {
        // Ghost restricted items
        if (DeathManager.isGhost(player)) {
            // Block bucket usage
            if (item.getItem() instanceof BucketItem) return false;
            
            // Block flint and steel (fire)
            if (item.getItem() instanceof FlintAndSteelItem) return false;
            
            // Block ender pearl
            if (item.getItem() instanceof EnderPearlItem) return false;
            
            // Allow everything else (bow, food, etc)
        }
        
        return true;
    }
}
