package com.soullife.fabric;

import com.soullife.manager.CommonEvents;
import com.soullife.manager.DeathManager;
import com.soullife.manager.ItemUseManager;
import com.soullife.manager.SacrificeManager;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;

public class FabricEvents {

    public static void register() {
        // Player Death
        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, damageSource, damageAmount) -> {
            if (entity instanceof ServerPlayer player) {
                CommonEvents.onPlayerDeath(player);
            }
            return true;
        });

        // Player Respawn
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            CommonEvents.onPlayerRespawn(newPlayer);
        });

        // Player Login
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.getPlayer();
            CommonEvents.onPlayerLogin(player);
        });

        // Player Logout
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            ServerPlayer player = handler.getPlayer();
            DeathManager.saveAllToNBT(player, new CompoundTag());
        });

        // Block Break - prevent ghost from breaking blocks
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if (player instanceof ServerPlayer serverPlayer) {
                if (CommonEvents.onBlockBreak(serverPlayer)) {
                    return InteractionResult.FAIL;
                }
            }
            return InteractionResult.PASS;
        });

        // Block Place - prevent ghost from placing blocks ONLY
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (player instanceof ServerPlayer serverPlayer) {
                if (DeathManager.isGhost(serverPlayer)) {
                    net.minecraft.world.level.block.Block block = world.getBlockState(hitResult.getBlockPos()).getBlock();
                    
                    if (isAllowedForGhost(block)) {
                        return InteractionResult.PASS;
                    }
                    
                    return InteractionResult.FAIL;
                }
            }
            return InteractionResult.PASS;
        });

        // Item Pickup & Use restriction - monitor inventory each tick
        ServerTickEvents.END_SERVER_TICK.register((server) -> {
            for (ServerPlayer serverPlayer : server.getPlayerList().getPlayers()) {
                if (DeathManager.isGhost(serverPlayer)) {
                    // Remove ghost armor from ground if picked up
                    net.minecraft.world.entity.EquipmentSlot[] slots = {
                        net.minecraft.world.entity.EquipmentSlot.HEAD,
                        net.minecraft.world.entity.EquipmentSlot.CHEST,
                        net.minecraft.world.entity.EquipmentSlot.LEGS,
                        net.minecraft.world.entity.EquipmentSlot.FEET
                    };
                    
                    for (net.minecraft.world.entity.EquipmentSlot slot : slots) {
                        ItemStack equipped = serverPlayer.getItemBySlot(slot);
                        if (equipped.isEmpty() || !isGhostArmor(equipped)) {
                            continue;
                        }
                        // If we find ghost armor not on server player, remove it from inventory
                        for (int i = 0; i < serverPlayer.getInventory().getContainerSize(); i++) {
                            ItemStack stack = serverPlayer.getInventory().getItem(i);
                            if (!stack.isEmpty() && isGhostArmor(stack)) {
                                serverPlayer.getInventory().removeItem(stack);
                            }
                        }
                    }
                    
                    // Check sacrifice items
                    for (ItemStack stack : serverPlayer.getInventory().items) {
                        if (!stack.isEmpty()) {
                            ItemStack required = SacrificeManager.getRequiredItem(serverPlayer);
                            if (!required.isEmpty() && stack.is(required.getItem())) {
                                SacrificeManager.trySacrifice(serverPlayer);
                            }
                        }
                    }
                }
            }
        });
    }

    private static boolean isAllowedForGhost(net.minecraft.world.level.block.Block block) {
        if (block instanceof net.minecraft.world.level.block.ChestBlock) return true;
        if (block instanceof net.minecraft.world.level.block.ShulkerBoxBlock) return true;
        if (block instanceof net.minecraft.world.level.block.EnderChestBlock) return true;
        if (block instanceof net.minecraft.world.level.block.CraftingTableBlock) return true;
        if (block instanceof net.minecraft.world.level.block.FurnaceBlock) return true;
        if (block instanceof net.minecraft.world.level.block.BlastFurnaceBlock) return true;
        if (block instanceof net.minecraft.world.level.block.SmokerBlock) return true;
        if (block instanceof net.minecraft.world.level.block.DispenserBlock) return true;
        if (block instanceof net.minecraft.world.level.block.DropperBlock) return true;
        if (block instanceof net.minecraft.world.level.block.HopperBlock) return true;
        if (block instanceof net.minecraft.world.level.block.DoorBlock) return true;
        if (block instanceof net.minecraft.world.level.block.TrapDoorBlock) return true;
        if (block instanceof net.minecraft.world.level.block.FenceGateBlock) return true;
        if (block instanceof net.minecraft.world.level.block.LecternBlock) return true;
        if (block instanceof net.minecraft.world.level.block.AnvilBlock) return true;
        
        return false;
    }
}

    private static boolean isGhostArmor(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (!stack.hasCustomHoverName()) return false;
        String name = stack.getHoverName().getString();
        return name.contains("Ghost") || name.contains("Skeleton");
    }
}
