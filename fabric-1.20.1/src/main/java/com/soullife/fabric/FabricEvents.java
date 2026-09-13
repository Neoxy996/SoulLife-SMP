package com.soullife.fabric;

import com.soullife.manager.CommonEvents;
import com.soullife.manager.DeathManager;
import com.soullife.manager.SacrificeManager;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

public class FabricEvents {

    private static int tickCounter = 0;
    private static final String DATA_DIR = "soullife-data";

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

        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
            loadPlayerData(newPlayer);
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
    }

    private static void loadPlayerData(ServerPlayer player) {
        try {
            File dataDir = new File(DATA_DIR);
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }
            
            File playerFile = new File(dataDir, player.getUUID() + ".txt");
            if (!playerFile.exists()) {
                return;
            }
            
            String content = new String(Files.readAllBytes(playerFile.toPath()));
            String[] parts = content.split("\\|");
            
            if (parts.length >= 3) {
                int deaths = Integer.parseInt(parts[0]);
                boolean isGhost = Boolean.parseBoolean(parts[1]);
                boolean isPermanent = Boolean.parseBoolean(parts[2]);
                
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
            File dataDir = new File(DATA_DIR);
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }
            
            File playerFile = new File(dataDir, player.getUUID() + ".txt");
            
            int deaths = DeathManager.getDeathCount(player);
            boolean isGhost = DeathManager.isGhost(player);
            boolean isPermanent = DeathManager.isPermanentSpectator(player);
            
            String data = deaths + "|" + isGhost + "|" + isPermanent;
            
            FileWriter writer = new FileWriter(playerFile);
            writer.write(data);
            writer.close();
        } catch (IOException e) {
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
