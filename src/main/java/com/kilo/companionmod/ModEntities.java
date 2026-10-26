package com.kilo.companionmod;

import com.kilo.companionmod.entity.CompanionEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, CompanionMod.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<CompanionEntity>> COMPANION = ENTITIES.register("companion",
        () -> EntityType.Builder.of(CompanionEntity::new, MobCategory.CREATURE)
            .sized(0.6F, 1.8F)
            .build(CompanionMod.MOD_ID + ":companion")
    );

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
}
