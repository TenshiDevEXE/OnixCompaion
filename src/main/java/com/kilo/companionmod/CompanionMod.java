package com.kilo.companionmod;

import com.kilo.companionmod.client.ModEntityRenderers;
import com.kilo.companionmod.config.ModConfig;
import com.kilo.companionmod.entity.CompanionEntity;
import com.kilo.companionmod.event.CompanionEvents;
import com.kilo.companionmod.item.ModItems;
import com.kilo.companionmod.network.ModNetwork;
import com.kilo.companionmod.sound.ModSounds;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(CompanionMod.MOD_ID)
public class CompanionMod {
    public static final String MOD_ID = "companionmod";
    public static final Logger LOGGER = LogManager.getLogger();

    public CompanionMod() {
        try {
            IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
            ModEntities.ENTITIES.register(modEventBus);
            ModItems.ITEMS.register(modEventBus);
            ModSounds.SOUND_EVENTS.register(modEventBus);
            ModConfig.register();
            ModNetwork.register();
            modEventBus.addListener(this::commonSetup);
            modEventBus.addListener(CompanionMod::onEntityAttributeCreation);
            MinecraftForge.EVENT_BUS.register(this);
            MinecraftForge.EVENT_BUS.register(new CompanionEvents());
        } catch (Throwable t) {
            LOGGER.error("Fallo crítico durante la inicialización de Companion Mod", t);
            throw t;
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        try {
            event.enqueueWork(() -> {
            });
        } catch (Throwable t) {
            LOGGER.error("Fallo en commonSetup de Companion Mod", t);
        }
    }

    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        try {
            event.put(ModEntities.COMPANION.get(), CompanionEntity.createAttributes().build());
        } catch (Throwable t) {
            LOGGER.error("Fallo registrando atributos de CompanionEntity", t);
        }
    }
}
