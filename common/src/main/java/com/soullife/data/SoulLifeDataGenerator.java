package com.soullife.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "soullife", bus = Mod.EventBusSubscriber.Bus.MOD)
public class SoulLifeDataGenerator {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();

        // Register recipe provider
        generator.addProvider(event.includeServer(), new SoulLifeRecipeProvider(packOutput));
    }
}
