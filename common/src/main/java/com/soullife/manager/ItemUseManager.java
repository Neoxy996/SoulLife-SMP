package com.soullife.manager;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ItemUseManager {

    public static boolean canUseItem(ServerPlayer player, ItemStack item) {
        // Ghost restricted items only
        if (DeathManager.isGhost(player)) {
            // Block bucket usage (water/lava)
            if (item.getItem() instanceof BucketItem) return false;
            
            // Block flint and steel (fire)
            if (item.getItem() instanceof FlintAndSteelItem) return false;
            
            // Block ender pearl
            if (item.is(Items.ENDER_PEARL)) return false;
            
            // Allow everything else: bow, food, chests, doors, etc
        }
        
        return true;
    }
}
