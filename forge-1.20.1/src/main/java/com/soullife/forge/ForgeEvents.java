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
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ForgeEvents {

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ItemStack offHand = player.getOffhandItem();
            ItemStack mainHand = player.getMainHandItem();
            boolean hasTotem = offHand.is(Items.TOTEM_OF_UNDYING) || mainHand.is(Items.TOTEM_OF_UNDYING);
            
            boolean isVoidDeath = event.getSource().getMsgId().equals("outOfWorld");
            
            if (!hasTotem) {
                CommonEvents.dropOriginalArmor(player);
            }

            if (hasTotem && !isVoidDeath) {
                return;
            }

            if (DeathManager.isGhost(player)) {
                ItemStack required = SacrificeManager.getRequiredItem(player);
                if (!SacrificeManager.hasItem(player, required)) {
                    return;
                }
            }

            CommonEvents.onPlayerDeath(player, event.getSource());
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            CommonEvents.onPlayerRespawn(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            DeathManager.savePlayerData(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            DeathManager.loadPlayerData(player);
            CommonEvents.onPlayerLogin(player);
        }
    }


    private static void removeGhostArmorEquipped(ServerPlayer player) {
        net.minecraft.world.entity.EquipmentSlot[] slots = {
            net.minecraft.world.entity.EquipmentSlot.HEAD,
            net.minecraft.world.entity.EquipmentSlot.CHEST,
            net.minecraft.world.entity.EquipmentSlot.LEGS,
            net.minecraft.world.entity.EquipmentSlot.FEET
        };
        
        for (net.minecraft.world.entity.EquipmentSlot slot : slots) {
            ItemStack armor = player.getItemBySlot(slot);
            if (!armor.isEmpty() && isGhostArmor(armor)) {
                player.setItemSlot(slot, ItemStack.EMPTY);
            }
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

    private static int tickCounter = 0;

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
                        removeGhostArmorEquipped(serverPlayer);
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
            DeathManager.savePlayerData(player);
    }

    @SubscribeEvent
    public static void onPlayerLoad(PlayerEvent.LoadFromFile event) {
        if (event.getEntity() instanceof ServerPlayer player)
            DeathManager.loadPlayerData(player);
    }

    @SubscribeEvent
    public static void onUseItem(net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (!ItemUseManager.canUseItem(player, event.getItemStack())) {
                event.setCanceled(true);
            }
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
