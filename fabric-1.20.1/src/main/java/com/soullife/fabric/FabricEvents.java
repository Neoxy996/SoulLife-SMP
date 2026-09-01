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

        // Block Place - prevent ghost from placing blocks
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (player instanceof ServerPlayer serverPlayer) {
                // Allow opening chests, doors, shulker, ender chest
                // Only prevent placing blocks via onBlockPlace
                if (CommonEvents.onBlockPlace(serverPlayer)) {
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
}

