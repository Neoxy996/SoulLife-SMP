package com.soullife.manager;

import com.soullife.util.MessageUtil;
import com.soullife.util.ScoreboardManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;

public class CommonEvents {

   
    public static void onPlayerDeath(ServerPlayer player) {
       
        if (DeathManager.isPermanentSpectator(player)) return;

        
        ItemStack offHand = player.getOffhandItem();
        ItemStack mainHand = player.getMainHandItem();
        boolean hasTotem = offHand.is(Items.TOTEM_OF_UNDYING) || mainHand.is(Items.TOTEM_OF_UNDYING);
        
        if (!hasTotem) {
           
            dropOriginalArmor(player);
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

        
        DeathManager.addDeaths(player, 1);
        int deaths = DeathManager.getDeathCount(player);

        ScoreboardManager.updateTabDisplay(player);

        // Death 21+ → Permanent Spectator
        if (deaths >= 21) {
            DeathManager.setPermanentSpectator(player, true);
            player.setGameMode(GameType.SPECTATOR);
            MessageUtil.sendPermanentDeathMessage(player);
            return;
        }

        // Death 20 → Still ghost (need dragon egg to survive)
        if (deaths >= 20) {
            GhostManager.applyGhostState(player);
            MessageUtil.sendDeathMessages(player, new ItemStack(Items.DRAGON_EGG));
            return;
        }

        // Normal death → Ghost mode
        GhostManager.applyGhostState(player);

        // Get required sacrifice item
        ItemStack required = SacrificeManager.getRequiredItem(player);

        // Send messages
        MessageUtil.sendDeathMessages(player, required);
    }

    /**
     * Called when player respawns (after ghost state).
     */
    public static void onPlayerRespawn(ServerPlayer player) {
        if (DeathManager.isPermanentSpectator(player)) {
            player.setGameMode(GameType.SPECTATOR);
            return;
        }

        if (DeathManager.isGhost(player)) {
            // Re-apply ghost state after respawn
            GhostManager.applyGhostState(player);
        }

        // Refresh Tab
        ScoreboardManager.updateTabDisplay(player);
    }

    /**
     * Called when player logs in.
     */
    public static void onPlayerLogin(ServerPlayer player) {
        // Restore ghost state
        if (DeathManager.isPermanentSpectator(player)) {
            player.setGameMode(GameType.SPECTATOR);
        } else if (DeathManager.isGhost(player)) {
            GhostManager.refreshGhostEffects(player);
            player.setGameMode(GameType.SURVIVAL);  // ✅ SURVIVAL, not SPECTATOR
        }

        // Update Tab
        ScoreboardManager.updateTabDisplay(player);
    }

    /**
     * Called on item pickup - check if it's the sacrifice item.
     */
    public static void onItemPickup(ServerPlayer player, ItemStack pickedUp) {
        if (!DeathManager.isGhost(player)) return;
        if (DeathManager.isPermanentSpectator(player)) return;

        ItemStack required = SacrificeManager.getRequiredItem(player);
        if (!required.isEmpty() && pickedUp.is(required.getItem())) {
            // Try sacrifice - if successful, return (item consumed)
            if (SacrificeManager.trySacrifice(player)) {
                return;
            }
        }
    }

    /**
     * Called when player tries to break a block (prevent in ghost mode).
     */
    public static boolean onBlockBreak(ServerPlayer player) {
        // Ghost can't break blocks
        return DeathManager.isGhost(player);
    }

    public static boolean onBlockPlace(ServerPlayer player) {
        // Ghost can't place blocks at all (like adventure mode)
        // But CAN open containers (chests, shulker, crafting, doors, etc)
        if (DeathManager.isGhost(player)) {
            return true;  // Prevent PLACEMENT only
        }
        return false;
    }

    public static void dropOriginalArmor(ServerPlayer player) {
        // If already ghost - don't drop anything (already cleared)
        if (DeathManager.isGhost(player)) {
            return;
        }
        
        // Drop original armor only if NOT already a ghost
        net.minecraft.world.entity.EquipmentSlot[] slots = {
            net.minecraft.world.entity.EquipmentSlot.HEAD,
            net.minecraft.world.entity.EquipmentSlot.CHEST,
            net.minecraft.world.entity.EquipmentSlot.LEGS,
            net.minecraft.world.entity.EquipmentSlot.FEET
        };
        
        for (net.minecraft.world.entity.EquipmentSlot slot : slots) {
            ItemStack armor = player.getItemBySlot(slot);
            if (!armor.isEmpty() && !isGhostArmor(armor)) {
                player.drop(armor, true);
                // Clear the slot after dropping
                player.setItemSlot(slot, ItemStack.EMPTY);
            }
        }
    }

    private static boolean isGhostArmor(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (!stack.hasCustomHoverName()) return false;
        String name = stack.getHoverName().getString();
        return name.contains("Ghost") || name.contains("Skeleton");
    }
}
