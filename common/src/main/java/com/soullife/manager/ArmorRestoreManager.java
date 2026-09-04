package com.soullife.manager;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

public class ArmorRestoreManager {

    public static void saveOriginalArmor(ServerPlayer player) {
        CompoundTag tag = player.getPersistentData();
        
        // Save original armor
        tag.put("SoulLife:OriginalHead", player.getItemBySlot(EquipmentSlot.HEAD).serializeNBT());
        tag.put("SoulLife:OriginalChest", player.getItemBySlot(EquipmentSlot.CHEST).serializeNBT());
        tag.put("SoulLife:OriginalLegs", player.getItemBySlot(EquipmentSlot.LEGS).serializeNBT());
        tag.put("SoulLife:OriginalFeet", player.getItemBySlot(EquipmentSlot.FEET).serializeNBT());
    }

    public static void restoreOriginalArmor(ServerPlayer player) {
        CompoundTag tag = player.getPersistentData();
        
        if (tag.contains("SoulLife:OriginalHead")) {
            ItemStack head = ItemStack.of(tag.getCompound("SoulLife:OriginalHead"));
            if (!head.isEmpty()) player.setItemSlot(EquipmentSlot.HEAD, head);
        }
        if (tag.contains("SoulLife:OriginalChest")) {
            ItemStack chest = ItemStack.of(tag.getCompound("SoulLife:OriginalChest"));
            if (!chest.isEmpty()) player.setItemSlot(EquipmentSlot.CHEST, chest);
        }
        if (tag.contains("SoulLife:OriginalLegs")) {
            ItemStack legs = ItemStack.of(tag.getCompound("SoulLife:OriginalLegs"));
            if (!legs.isEmpty()) player.setItemSlot(EquipmentSlot.LEGS, legs);
        }
        if (tag.contains("SoulLife:OriginalFeet")) {
            ItemStack feet = ItemStack.of(tag.getCompound("SoulLife:OriginalFeet"));
            if (!feet.isEmpty()) player.setItemSlot(EquipmentSlot.FEET, feet);
        }
    }
}
