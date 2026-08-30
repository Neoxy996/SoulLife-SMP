package com.soullife.neoforge;

import com.soullife.manager.SoulLifeCommands;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/**
 * SoulLife - NeoForge 1.21.1
 */
@Mod(SoulLifeMod.MOD_ID)
public class SoulLifeMod {

    public static final String MOD_ID = "soullife";

    public SoulLifeMod(IEventBus modEventBus) {
        IEventBus forgeEventBus = NeoForge.EVENT_BUS;

        modEventBus.addListener(this::commonSetup);
        forgeEventBus.addListener(this::registerCommands);
        forgeEventBus.register(NeoForgeEvents.class);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    private void registerCommands(RegisterCommandsEvent event) {
        SoulLifeCommands.register(event.getDispatcher());
    }
}
