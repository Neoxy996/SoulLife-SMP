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

    private static int tickCounter = 0;

    public static void register() {
        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, damageSource, damageAmount) -> {
            if (entity instanceof ServerPlayer player) {
                CommonEvents.onPlayerDeath(player);
            }
            return true;
        });

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            loadPlayerData(newPlayer);
            CommonEvents.onPlayerRespawn(newPlayer);
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.getPlayer();
            loadPlayerData(player);
            CommonEvents.onPlayerLogin(player);
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            ServerPlayer player = handler.getPlayer();
            savePlayerData(player);
        });

        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if (player instanceof ServerPlayer serverPlayer) {
                if (CommonEvents.onBlockBreak(serverPlayer)) {
                    return InteractionResult.FAIL;
                }
            }
            return InteractionResult.PASS;
        });

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

        ServerTickEvents.END_SERVER_TICK.register((server) -> {
            tickCounter++;
            
            if (tickCounter % 5 != 0) return;
            
            for (ServerPlayer serverPlayer : server.getPlayerList().getPlayers()) {
                if (DeathManager.isGhost(serverPlayer)) {
                    ItemStack required = SacrificeManager.getRequiredItem(serverPlayer);
                    if (!required.isEmpty() && SacrificeManager.hasItem(serverPlayer, required)) {
                        SacrificeManager.trySacrifice(serverPlayer);
                    }
                }
                
                for (int i = 0; i < 36; i++) {
                    ItemStack stack = serverPlayer.getInventory().getItem(i);
                    if (!stack.isEmpty() && isGhostArmor(stack)) {
                        serverPlayer.getInventory().removeItem(stack);
                    }
                }
            }
        });
    }

    private static void loadPlayerData(ServerPlayer player) {
        try {
            CompoundTag nbt = new CompoundTag();
            
            if (player.getEntityData().getCompound("PublicCustomData").contains("soullife_deaths")) {
                CompoundTag customData = player.getEntityData().getCompound("PublicCustomData");
                int deaths = customData.getInt("soullife_deaths");
                boolean isGhost = customData.getBoolean("soullife_ghost");
                boolean isPermanent = customData.getBoolean("soullife_permanent");
                
                DeathManager.setDeaths(player, deaths);
                DeathManager.setGhost(player, isGhost);
                DeathManager.setPermanentSpectator(player, isPermanent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void savePlayerData(ServerPlayer player) {
        try {
            CompoundTag customData = player.getEntityData().getCompound("PublicCustomData");
            
            customData.putInt("soullife_deaths", DeathManager.getDeathCount(player));
            customData.putBoolean("soullife_ghost", DeathManager.isGhost(player));
            customData.putBoolean("soullife_permanent", DeathManager.isPermanentSpectator(player));
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        if (block instanceof net.minecraft.world.level.block.BarrelBlock) return true;
        if (block instanceof net.minecraft.world.level.block.DoorBlock) return true;
        if (block instanceof net.minecraft.world.level.block.TrapDoorBlock) return true;
        if (block instanceof net.minecraft.world.level.block.FenceGateBlock) return true;
        if (block instanceof net.minecraft.world.level.block.LecternBlock) return true;
        if (block instanceof net.minecraft.world.level.block.AnvilBlock) return true;
        
        return false;
    }

    private static boolean isGhostArmor(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (!stack.hasCustomHoverName()) return false;
        String name = stack.getHoverName().getString();
        return name.contains("Ghost") || name.contains("Skeleton");
    }
}
