package com.soullife.forge;

import com.soullife.manager.CommonEvents;
import com.soullife.manager.DeathManager;
import com.soullife.manager.GhostManager;
import com.soullife.manager.ItemUseManager;
import com.soullife.manager.SacrificeManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ForgeEvents {

    private static int tickCounter = 0;

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ItemStack offHand = player.getOffhandItem();
            ItemStack mainHand = player.getMainHandItem();
            boolean hasTotem = offHand.is(Items.TOTEM_OF_UNDYING) || mainHand.is(Items.TOTEM_OF_UNDYING);
            
            if (!hasTotem) {
                CommonEvents.dropOriginalArmor(player);
            }

            if (hasTotem) {
                return;
            }

            if (DeathManager.isGhost(player)) {
                ItemStack required = SacrificeManager.getRequiredItem(player);
                if (!SacrificeManager.hasItem(player, required)) {
                    return;
                }
            }

            CommonEvents.onPlayerDeath(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            loadPlayerData(player);
            CommonEvents.onPlayerRespawn(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            loadPlayerData(player);
            CommonEvents.onPlayerLogin(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            savePlayerData(player);
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getPlayer() instanceof ServerPlayer player)
            if (CommonEvents.onBlockBreak(player)) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (DeathManager.isGhost(player)) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            tickCounter++;
            
            if (tickCounter % 5 != 0) return;
            
            for (ServerPlayer serverPlayer : event.getServer().getPlayerList().getPlayers()) {
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
        }
    }

    @SubscribeEvent
    public static void onPlayerSave(PlayerEvent.SaveToFile event) {
        if (event.getEntity() instanceof ServerPlayer player)
            savePlayerData(player);
    }

    @SubscribeEvent
    public static void onUseItem(net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (!ItemUseManager.canUseItem(player, event.getItemStack())) {
                event.setCanceled(true);
            }
        }
    }

    private static void loadPlayerData(ServerPlayer player) {
        CompoundTag tag = player.getPersistentData();
        
        if (tag.contains("soullife_deaths")) {
            DeathManager.setDeaths(player, tag.getInt("soullife_deaths"));
            DeathManager.setGhost(player, tag.getBoolean("soullife_ghost"));
            DeathManager.setPermanentSpectator(player, tag.getBoolean("soullife_permanent"));
        }
    }

    private static void savePlayerData(ServerPlayer player) {
        CompoundTag tag = player.getPersistentData();
        
        tag.putInt("soullife_deaths", DeathManager.getDeathCount(player));
        tag.putBoolean("soullife_ghost", DeathManager.isGhost(player));
        tag.putBoolean("soullife_permanent", DeathManager.isPermanentSpectator(player));
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
