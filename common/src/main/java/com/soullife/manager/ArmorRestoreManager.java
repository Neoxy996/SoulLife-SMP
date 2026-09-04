package com.soullife.manager;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public class ArmorRestoreManager {

    public static void saveOriginalArmor(ServerPlayer player) {
        CompoundTag playerTag = player.serializeNBT();
        CompoundTag armorTag = new CompoundTag();
        
        // Save original armor to NBT
        ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack legs = player.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack feet = player.getItemBySlot(EquipmentSlot.FEET);
        
        if (!head.isEmpty()) armorTag.put("Head", head.save(new CompoundTag()));
        if (!chest.isEmpty()) armorTag.put("Chest", chest.save(new CompoundTag()));
        if (!legs.isEmpty()) armorTag.put("Legs", legs.save(new CompoundTag()));
        if (!feet.isEmpty()) armorTag.put("Feet", feet.save(new CompoundTag()));
        
        playerTag.put("SoulLife:OriginalArmor", armorTag);
    }

    public static void restoreOriginalArmor(ServerPlayer player) {
        CompoundTag playerTag = player.serializeNBT();
        
        if (playerTag.contains("SoulLife:OriginalArmor")) {
            CompoundTag armorTag = playerTag.getCompound("SoulLife:OriginalArmor");
            
            if (armorTag.contains("Head")) {
                ItemStack head = ItemStack.of(armorTag.getCompound("Head"));
                if (!head.isEmpty()) player.setItemSlot(EquipmentSlot.HEAD, head);
            }
            if (armorTag.contains("Chest")) {
                ItemStack chest = ItemStack.of(armorTag.getCompound("Chest"));
                if (!chest.isEmpty()) player.setItemSlot(EquipmentSlot.CHEST, chest);
            }
            if (armorTag.contains("Legs")) {
                ItemStack legs = ItemStack.of(armorTag.getCompound("Legs"));
                if (!legs.isEmpty()) player.setItemSlot(EquipmentSlot.LEGS, legs);
            }
            if (armorTag.contains("Feet")) {
                ItemStack feet = ItemStack.of(armorTag.getCompound("Feet"));
                if (!feet.isEmpty()) player.setItemSlot(EquipmentSlot.FEET, feet);
            }
        }
    }
}
