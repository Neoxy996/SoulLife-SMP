package com.soullife.fabric;

import com.soullife.manager.SoulLifeCommands;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

/**
 * SoulLife - Fabric 1.20.1 Main Class
 */
public class SoulLifeFabric implements ModInitializer {

    public static final String MOD_ID = "soullife";

    @Override
    public void onInitialize() {
        // Register Commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            SoulLifeCommands.register(dispatcher);
        });

        // Register Events
        FabricEvents.register();
    }
}
