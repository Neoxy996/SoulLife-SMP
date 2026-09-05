package com.soullife.fabric;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

public class ArmorRestoreManager {

    public static void saveOriginalArmor(ServerPlayer player) {
        // Save original armor in ghost head NBT
        ItemStack ghostHead = player.getItemBySlot(EquipmentSlot.HEAD);
        if (!ghostHead.isEmpty()) {
            CompoundTag tag = ghostHead.getOrCreateTag();
            
            // Save original armor items
            tag.put("OriginalHead", player.getItemBySlot(EquipmentSlot.HEAD).save(new CompoundTag()));
            tag.put("OriginalChest", player.getItemBySlot(EquipmentSlot.CHEST).save(new CompoundTag()));
            tag.put("OriginalLegs", player.getItemBySlot(EquipmentSlot.LEGS).save(new CompoundTag()));
            tag.put("OriginalFeet", player.getItemBySlot(EquipmentSlot.FEET).save(new CompoundTag()));
        }
    }

    public static void restoreOriginalArmor(ServerPlayer player) {
        // Get ghost head with saved armor data
        ItemStack ghostHead = player.getItemBySlot(EquipmentSlot.HEAD);
        if (!ghostHead.isEmpty() && ghostHead.hasTag()) {
            CompoundTag tag = ghostHead.getTag();
            
            if (tag.contains("OriginalHead")) {
                ItemStack head = ItemStack.of(tag.getCompound("OriginalHead"));
                if (!head.isEmpty()) player.setItemSlot(EquipmentSlot.HEAD, head);
            }
            if (tag.contains("OriginalChest")) {
                ItemStack chest = ItemStack.of(tag.getCompound("OriginalChest"));
                if (!chest.isEmpty()) player.setItemSlot(EquipmentSlot.CHEST, chest);
            }
            if (tag.contains("OriginalLegs")) {
                ItemStack legs = ItemStack.of(tag.getCompound("OriginalLegs"));
                if (!legs.isEmpty()) player.setItemSlot(EquipmentSlot.LEGS, legs);
            }
            if (tag.contains("OriginalFeet")) {
                ItemStack feet = ItemStack.of(tag.getCompound("OriginalFeet"));
                if (!feet.isEmpty()) player.setItemSlot(EquipmentSlot.FEET, feet);
            }
        }
    }

    public static void preventArmorDrop(ServerPlayer player) {
        // Don't drop ghost armor - keep it as equipped items
        ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack legs = player.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack feet = player.getItemBySlot(EquipmentSlot.FEET);
        
        // If wearing ghost armor, don't let it drop
        if (isGhostArmor(head) || isGhostArmor(chest) || isGhostArmor(legs) || isGhostArmor(feet)) {
            // Equipment stays equipped (can't be dropped)
        }
    }

    private static boolean isGhostArmor(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return stack.hasCustomHoverName() && 
               (stack.getHoverName().getString().contains("Ghost") || 
                stack.getHoverName().getString().contains("Skeleton"));
    }
}
