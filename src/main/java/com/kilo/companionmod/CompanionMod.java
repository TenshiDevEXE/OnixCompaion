package com.kilo.companionmod;

import com.kilo.companionmod.client.ModEntityRenderers;
import com.kilo.companionmod.config.ModConfig;
import com.kilo.companionmod.entity.CompanionEntity;
import com.kilo.companionmod.event.CompanionEvents;
import com.kilo.companionmod.item.ModItems;
import com.kilo.companionmod.network.ModNetwork;
import com.kilo.companionmod.sound.ModSounds;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(CompanionMod.MOD_ID)
public class CompanionMod {
    public static final String MOD_ID = "onixcompanion";
    public static final Logger LOGGER = LogManager.getLogger();

    public CompanionMod(IEventBus modEventBus) {
        try {
            ModEntities.ENTITIES.register(modEventBus);
            ModItems.ITEMS.register(modEventBus);
            ModSounds.SOUND_EVENTS.register(modEventBus);
            ModConfig.register();
            ModNetwork.register(modEventBus);
            modEventBus.addListener(CompanionMod::onEntityAttributeCreation);
            modEventBus.addListener(ModEntityRenderers::onClientSetup);
            NeoForge.EVENT_BUS.addListener(CompanionEvents::onCompanionDeath);
        } catch (Throwable t) {
            LOGGER.error("Fallo crítico durante la inicialización de Companion Mod", t);
            throw t;
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
