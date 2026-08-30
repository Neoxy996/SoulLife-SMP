package com.soullife.manager;

import com.soullife.util.MessageUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;

/**
 * SoulLife - CommonEvents
 * Server-side event handlers shared across all loaders.
 */
public class CommonEvents {

    // ── Death Event ───────────────────────────────────────────────────────────
    public static void onPlayerDeath(ServerPlayer player) {
        // If ghost dies without paying → don't count
        if (GhostManager.isGhost(player)) {
            return;
        }

        int deaths = DeathManager.getDeathCount(player);
        deaths++;
        DeathManager.setDeaths(player, deaths);

        if (deaths >= 20) {
            // Death 20: Permanent spectator forever
            DeathManager.setPermanentSpectator(player, true);
            GhostManager.applyGhostState(player);
            player.setGameMode(GameType.SPECTATOR); // ONLY spectator on 20+
            MessageUtil.sendPermanentDeathMessage(player);
        } else {
            // Deaths 1-19: Become ghost (STAY IN SURVIVAL!)
            GhostManager.applyGhostState(player);
            player.setGameMode(GameType.SURVIVAL); // ✅ SURVIVAL NOT SPECTATOR
            
            ItemStack required = SacrificeManager.getRequiredItem(player);
            MessageUtil.sendDeathMessages(player, required);
        }
    }

    // ── Respawn Event ─────────────────────────────────────────────────────────
    public static void onPlayerRespawn(ServerPlayer player) {
        // Respawn just applies ghost state if needed
        if (GhostManager.isGhost(player)) {
            GhostManager.applyGhostState(player);
            player.setGameMode(GameType.SURVIVAL); // ✅ SURVIVAL
        }
    }

    // ── Login Event ───────────────────────────────────────────────────────────
    public static void onPlayerLogin(ServerPlayer player) {
        DeathManager.loadFromNBT(player, player.getPersistentData());
        
        if (DeathManager.isPermanentSpectator(player)) {
            // Only spectator if permanent
            player.setGameMode(GameType.SPECTATOR);
            GhostManager.applyGhostState(player);
        } else if (GhostManager.isGhost(player)) {
            // Regular ghost: SURVIVAL
            GhostManager.applyGhostState(player);
            player.setGameMode(GameType.SURVIVAL); // ✅ SURVIVAL
        }
    }

    // ── Item Pickup Event ─────────────────────────────────────────────────────
    public static void onItemPickup(ServerPlayer player, ItemStack pickedUp) {
        if (!GhostManager.isGhost(player)) return;
        if (pickedUp.isEmpty()) return;

        ItemStack required = SacrificeManager.getRequiredItem(player);
        
        // ✅ Check if this is the required sacrifice item
        if (ItemStack.isSameItemSameTags(pickedUp, required)) {
            // Remove from inventory
            pickedUp.shrink(1);
            
            // ✅ Revive player to SURVIVAL
            GhostManager.removeGhostState(player);
            player.setGameMode(GameType.SURVIVAL);
            MessageUtil.broadcastRevival(player);
        }
    }

    // ── Block Break Event ─────────────────────────────────────────────────────
    public static boolean onBlockBreak(ServerPlayer player) {
        // ✅ Ghosts can't break blocks
        if (GhostManager.isGhost(player)) {
            return true; // Cancel the break
        }
        return false;
    }

    // ── Block Place Event ─────────────────────────────────────────────────────
    public static boolean onBlockPlace(ServerPlayer player) {
        // ✅ Ghosts can't place blocks
        if (GhostManager.isGhost(player)) {
            return true; // Cancel the place
        }
        return false;
    }
}
