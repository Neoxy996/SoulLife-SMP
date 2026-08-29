package com.soullife.fabric;

import com.soullife.manager.CommonEvents;
import com.soullife.manager.DeathManager;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerPlayer;

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
            DeathManager.loadAllFromNBT(player, player.getPersistentData());
            CommonEvents.onPlayerLogin(player);
        });

        // Logout (save)
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            ServerPlayer player = handler.getPlayer();
            DeathManager.saveAllToNBT(player, player.getPersistentData());
        });
    }
}
