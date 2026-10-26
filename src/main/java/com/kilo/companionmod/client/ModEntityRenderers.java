package com.kilo.companionmod.client;

import com.kilo.companionmod.ModEntities;
import com.kilo.companionmod.client.renderer.CompanionRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

public class ModEntityRenderers {
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            EntityRenderers.register(ModEntities.COMPANION.get(), CompanionRenderer::new);
        });
    }
}
