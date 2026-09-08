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
            // Block flint and steel (fire) - ONLY this
            if (item.getItem() instanceof FlintAndSteelItem) return false;
            
            // Block ender pearl - ONLY this
            if (item.is(Items.ENDER_PEARL)) return false;
            
            // Allow EVERYTHING else: buckets, bow, food, chests, shulker, ender chest, etc
        }
        
        return true;
    }
}
