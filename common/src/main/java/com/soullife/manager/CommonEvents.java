package com.soullife.manager;

import com.soullife.util.MessageUtil;
import com.soullife.util.ScoreboardManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.network.chat.Component;

public class CommonEvents {

    public static void onPlayerDeath(ServerPlayer player) {
        if (DeathManager.isPermanentSpectator(player)) return;

        ItemStack offHand = player.getOffhandItem();
        ItemStack mainHand = player.getMainHandItem();
        boolean hasTotem = offHand.is(Items.TOTEM_OF_UNDYING) || mainHand.is(Items.TOTEM_OF_UNDYING);
        
        boolean isVoidDeath = isVoidDamage(player);
        
        if (!hasTotem) {
            dropOriginalArmor(player);
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

        DeathManager.addDeaths(player, 1);
        int deaths = DeathManager.getDeathCount(player);

        if (deaths >= 21) {
            DeathManager.setPermanentSpectator(player, true);
            player.setGameMode(GameType.SPECTATOR);
            MessageUtil.sendPermanentDeathMessage(player);
            return;
        }

        if (deaths >= 20) {
            GhostManager.applyGhostState(player);
            ScoreboardManager.updateTabDisplay(player);
            MessageUtil.sendDeathMessages(player, new ItemStack(Items.DRAGON_EGG));
            return;
        }

        GhostManager.applyGhostState(player);
        ScoreboardManager.updateTabDisplay(player);

        ItemStack required = SacrificeManager.getRequiredItem(player);
        MessageUtil.sendDeathMessages(player, required);
    }

    private static boolean isVoidDamage(ServerPlayer player) {
        return player.getLastDeathMessage() != null && 
               (player.getLastDeathMessage().getString().contains("Fell out of the world") ||
                player.getLastDeathMessage().getString().contains("didn't want to live in the same world"));
    }

    public static void onPlayerRespawn(ServerPlayer player) {
        if (DeathManager.isPermanentSpectator(player)) {
            player.setGameMode(GameType.SPECTATOR);
            return;
        }

        if (DeathManager.isGhost(player)) {
            GhostManager.applyGhostState(player);
        }

        ScoreboardManager.updateTabDisplay(player);
    }

    public static void onPlayerLogin(ServerPlayer player) {
        if (DeathManager.isPermanentSpectator(player)) {
            player.setGameMode(GameType.SPECTATOR);
        } else if (DeathManager.isGhost(player)) {
            GhostManager.refreshGhostEffects(player);
            player.setGameMode(GameType.SURVIVAL);
        }

        ScoreboardManager.updateTabDisplay(player);
    }

    public static void onItemPickup(ServerPlayer player, ItemStack pickedUp) {
        if (!DeathManager.isGhost(player)) return;
        if (DeathManager.isPermanentSpectator(player)) return;

        ItemStack required = SacrificeManager.getRequiredItem(player);
        if (!required.isEmpty() && pickedUp.is(required.getItem())) {
            if (SacrificeManager.trySacrifice(player)) {
                return;
            }
        }
    }

    public static boolean onBlockBreak(ServerPlayer player) {
        return DeathManager.isGhost(player);
    }

    public static boolean onBlockPlace(ServerPlayer player) {
        if (DeathManager.isGhost(player)) {
            return true;
        }
        return false;
    }

    public static void dropOriginalArmor(ServerPlayer player) {
        if (DeathManager.isGhost(player)) {
            return;
        }
        
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
