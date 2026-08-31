package com.soullife.manager;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameType;
import net.minecraft.core.particles.ParticleTypes;

/**
 * SoulLife - GhostManager
 * Handles ghost state: armor, effects, sounds, particles.
 */
public class GhostManager {

    // ─── Apply Ghost State ────────────────────────────────────────────────────
    public static void applyGhostState(ServerPlayer player) {
        player.setGameMode(GameType.SURVIVAL);  // ✅ SURVIVAL, NOT SPECTATOR
        giveGhostArmor(player);
        applyGhostEffects(player);
        playWitherSound(player);
        DeathManager.setGhost(player, true);
    }

    // ─── Remove Ghost State ───────────────────────────────────────────────────
    public static void removeGhostState(ServerPlayer player) {
        player.setGameMode(GameType.SURVIVAL);
        removeGhostArmor(player);
        removeGhostEffects(player);
        playTotemEffect(player);
        DeathManager.setGhost(player, false);
    }

    // ─── Ghost Armor ──────────────────────────────────────────────────────────
    private static void giveGhostArmor(ServerPlayer player) {
        // Ghost head (player head with custom texture)
        ItemStack ghostHead = new ItemStack(Items.PLAYER_HEAD);
        
        // Add Binding Curse (can't remove)
        ghostHead.enchant(Enchantments.BINDING_CURSE, 1);
        
        // Custom name
        ghostHead.setHoverName(Component.literal("Ghost Head").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        
        // Apply ghost head NBT data for custom texture
        net.minecraft.nbt.CompoundTag tag = ghostHead.getOrCreateTag();
        net.minecraft.nbt.CompoundTag skullOwner = new net.minecraft.nbt.CompoundTag();
        skullOwner.putString("Name", "Ghost");
        tag.put("SkullOwner", skullOwner);
        
        player.setItemSlot(EquipmentSlot.HEAD, ghostHead);
    }

    private static void removeGhostArmor(ServerPlayer player) {
        player.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
        player.setItemSlot(EquipmentSlot.CHEST, ItemStack.EMPTY);
        player.setItemSlot(EquipmentSlot.LEGS, ItemStack.EMPTY);
        player.setItemSlot(EquipmentSlot.FEET, ItemStack.EMPTY);
    }

    // ─── Ghost Effects ────────────────────────────────────────────────────────
    public static void applyGhostEffects(ServerPlayer player) {
        player.addEffect(new MobEffectInstance(
            MobEffects.MOVEMENT_SPEED, 999999 * 20, 0, false, false, true));
        player.addEffect(new MobEffectInstance(
            MobEffects.GLOWING, 999999 * 20, 0, false, false, true));
    }

    private static void removeGhostEffects(ServerPlayer player) {
        player.removeEffect(MobEffects.MOVEMENT_SPEED);
        player.removeEffect(MobEffects.GLOWING);
    }

    // ─── Wither Sound ─────────────────────────────────────────────────────────
    public static void playWitherSound(ServerPlayer player) {
        player.level().playSound(
            player,
            player.blockPosition(),
            SoundEvents.WITHER_SPAWN,
            SoundSource.MASTER,
            1.0f, 1.0f
        );
    }

    // ─── Totem Effect ─────────────────────────────────────────────────────────
    public static void playTotemEffect(ServerPlayer player) {
        player.level().playSound(
            null,
            player.blockPosition(),
            SoundEvents.TOTEM_USE,
            SoundSource.PLAYERS,
            1.0f, 1.0f
        );

        if (player.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            serverLevel.sendParticles(
                ParticleTypes.TOTEM_OF_UNDYING,
                player.getX(), player.getY() + 1, player.getZ(),
                50, 0.5, 1.0, 0.5, 0.3
            );
        }
    }

    // ─── Refresh Effects ──────────────────────────────────────────────────────
    public static void refreshGhostEffects(ServerPlayer player) {
        if (DeathManager.isGhost(player)) {
            applyGhostEffects(player);
        }
    }

    // ─── Check if armor is ghost armor ────────────────────────────────────────
    public static boolean isGhostArmor(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return stack.hasCustomHoverName() &&
               stack.getHoverName().getString().contains("Ghost Armor");
    }
}
