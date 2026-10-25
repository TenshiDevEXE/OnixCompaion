package com.kilo.companionmod;

import com.kilo.companionmod.config.ModConfig;
import com.kilo.companionmod.entity.CompanionEntity;
import com.kilo.companionmod.event.CompanionEvents;
import com.kilo.companionmod.item.ModItems;
import com.kilo.companionmod.network.ModNetwork;
import com.kilo.companionmod.sound.ModSounds;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(CompanionMod.MOD_ID)
public class CompanionMod {
    public static final String MOD_ID = "companionmod";
    public static final Logger LOGGER = LogManager.getLogger();

    public CompanionMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModEntities.ENTITIES.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModSounds.SOUND_EVENTS.register(modEventBus);
        ModConfig.register();
        ModNetwork.register();
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new CompanionEvents());
    }
}
