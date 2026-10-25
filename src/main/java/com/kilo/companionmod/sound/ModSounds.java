package com.kilo.companionmod.sound;

import com.kilo.companionmod.CompanionMod;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, CompanionMod.MOD_ID);

    public static final RegistryObject<SoundEvent> COMPANION_CLICK = SOUND_EVENTS.register("companion_click",
        () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionMod.MOD_ID, "companion_click"))
    );

    public static final RegistryObject<SoundEvent> COMPANION_ORDER = SOUND_EVENTS.register("companion_order",
        () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionMod.MOD_ID, "companion_order"))
    );

    public static final RegistryObject<SoundEvent> COMPANION_ERROR = SOUND_EVENTS.register("companion_error",
        () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionMod.MOD_ID, "companion_error"))
    );

    public static final RegistryObject<SoundEvent> COMPANION_DEATH = SOUND_EVENTS.register("companion_death",
        () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionMod.MOD_ID, "companion_death"))
    );

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
