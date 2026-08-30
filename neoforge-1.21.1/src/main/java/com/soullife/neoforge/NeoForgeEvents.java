package com.soullife.neoforge;

import com.soullife.manager.CommonEvents;
import com.soullife.manager.DeathManager;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

@Mod.EventBusSubscriber(modid = "soullife", bus = Mod.EventBusSubscriber.Bus.GAME)
public class NeoForgeEvents {

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player)
            CommonEvents.onPlayerDeath(player);
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player)
            CommonEvents.onPlayerRespawn(player);
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player)
            CommonEvents.onPlayerLogin(player);
    }

    @SubscribeEvent
    public static void onItemPickup(ItemEntityPickupEvent.Pre event) {
        if (event.getPlayer() instanceof ServerPlayer player)
            CommonEvents.onItemPickup(player, event.getItemEntity().getItem().getItem());
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getPlayer() instanceof ServerPlayer player)
            if (CommonEvents.onBlockBreak(player)) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() instanceof ServerPlayer player)
            if (CommonEvents.onBlockPlace(player)) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onPlayerSave(PlayerEvent.SaveToFile event) {
        if (event.getEntity() instanceof ServerPlayer player)
            DeathManager.saveAllToNBT(player, player.getPersistentData());
    }

    @SubscribeEvent
    public static void onPlayerLoad(PlayerEvent.LoadFromFile event) {
        if (event.getEntity() instanceof ServerPlayer player)
            DeathManager.loadAllFromNBT(player, player.getPersistentData());
    }
}
