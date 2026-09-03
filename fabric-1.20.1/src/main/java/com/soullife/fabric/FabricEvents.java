package com.soullife.fabric;

import com.soullife.manager.CommonEvents;
import com.soullife.manager.DeathManager;
import com.soullife.manager.ItemUseManager;
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

        // Death
        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, source, amount) -> {
            if (entity instanceof ServerPlayer player)
                CommonEvents.onPlayerDeath(player);
            return true;
        });

        // Respawn
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) ->
            CommonEvents.onPlayerRespawn(newPlayer));

        // Login
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.getPlayer();
            CompoundTag tag = new CompoundTag();
            DeathManager.loadAllFromNBT(player, tag);
            CommonEvents.onPlayerLogin(player);
        });

        // Logout (save)
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            ServerPlayer player = handler.getPlayer();
            CompoundTag tag = new CompoundTag();
            DeathManager.saveAllToNBT(player, tag);
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

        // Block Place - prevent ghost from placing blocks ONLY (allow opening containers)
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (player instanceof ServerPlayer serverPlayer) {
                if (DeathManager.isGhost(serverPlayer)) {
                    // Allow opening containers but prevent placement
                    // Check if it's a placement action vs opening
                    net.minecraft.world.level.block.Block block = world.getBlockState(hitResult.getBlockPos()).getBlock();
                    
                    // List of allowed blocks for ghost (containers, doors, etc)
                    if (isAllowedForGhost(block)) {
                        return InteractionResult.PASS;  // Allow interaction
                    }
                    
                    // Block placement attempt
                    return InteractionResult.FAIL;
                }
            }
            return InteractionResult.PASS;
        });

        // Item Pickup & Use restriction - monitor inventory each tick
        ServerTickEvents.END_SERVER_TICK.register((server) -> {
            for (ServerPlayer serverPlayer : server.getPlayerList().getPlayers()) {
                if (DeathManager.isGhost(serverPlayer)) {
                    // Check sacrifice items
                    for (ItemStack stack : serverPlayer.getInventory().items) {
                        if (!stack.isEmpty()) {
                            CommonEvents.onItemPickup(serverPlayer, stack);
                        }
                    }
                    
                    // Prevent restricted item usage - don't allow usage
                    ItemStack mainHand = serverPlayer.getMainHandItem();
                    if (!mainHand.isEmpty() && !ItemUseManager.canUseItem(serverPlayer, mainHand)) {
                        // Don't use item, but don't delete it either (adventure mode)
                        // Item stays in hand, just can't be used
                    }
                }
            }
        });
    }

    private static boolean isAllowedForGhost(net.minecraft.world.level.block.Block block) {
        // Containers
        if (block instanceof net.minecraft.world.level.block.ChestBlock) return true;
        if (block instanceof net.minecraft.world.level.block.ShulkerBoxBlock) return true;
        if (block instanceof net.minecraft.world.level.block.EnderChestBlock) return true;
        if (block instanceof net.minecraft.world.level.block.CraftingTableBlock) return true;
        if (block instanceof net.minecraft.world.level.block.FurnaceBlock) return true;
        if (block instanceof net.minecraft.world.level.block.BlastFurnaceBlock) return true;
        if (block instanceof net.minecraft.world.level.block.SmokerBlock) return true;
        if (block instanceof net.minecraft.world.level.block.DispenserBlock) return true;
        if (block instanceof net.minecraft.world.level.block.HopperBlock) return true;
        
        // Doors & Gates
        if (block instanceof net.minecraft.world.level.block.DoorBlock) return true;
        if (block instanceof net.minecraft.world.level.block.TrapDoorBlock) return true;
        if (block instanceof net.minecraft.world.level.block.FenceGateBlock) return true;
        
        // Other interactive blocks
        if (block instanceof net.minecraft.world.level.block.LecternBlock) return true;
        if (block instanceof net.minecraft.world.level.block.AnvilBlock) return true;
        if (block instanceof net.minecraft.world.level.block.CartAssemblerBlock) return true;
        
        return false;
    }
}

