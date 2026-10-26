package com.kilo.companionmod.sound;

import com.kilo.companionmod.CompanionMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, CompanionMod.MOD_ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> COMPANION_CLICK = SOUND_EVENTS.register("companion_click",
        () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(CompanionMod.MOD_ID, "companion_click"))
    );

    public static final DeferredHolder<SoundEvent, SoundEvent> COMPANION_ORDER = SOUND_EVENTS.register("companion_order",
        () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(CompanionMod.MOD_ID, "companion_order"))
    );

    public static final DeferredHolder<SoundEvent, SoundEvent> COMPANION_ERROR = SOUND_EVENTS.register("companion_error",
        () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(CompanionMod.MOD_ID, "companion_error"))
    );

    public static final DeferredHolder<SoundEvent, SoundEvent> COMPANION_DEATH = SOUND_EVENTS.register("companion_death",
        () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(CompanionMod.MOD_ID, "companion_death"))
    );

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
