package com.soullife.forge;

import com.soullife.manager.SoulLifeCommands;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * SoulLife - Forge 1.20.1 Main Class
 */
@Mod(SoulLifeForge.MOD_ID)
public class SoulLifeForge {

    public static final String MOD_ID = "soullife";

    public SoulLifeForge() {
        MinecraftForge.EVENT_BUS.register(ForgeEvents.class);
        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
    }

    private void registerCommands(RegisterCommandsEvent event) {
        SoulLifeCommands.register(event.getDispatcher());
    }
}
