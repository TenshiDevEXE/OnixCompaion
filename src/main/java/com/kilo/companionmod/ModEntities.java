package com.kilo.companionmod;

import com.kilo.companionmod.entity.CompanionEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, CompanionMod.MOD_ID);

    public static final RegistryObject<EntityType<CompanionEntity>> COMPANION = ENTITIES.register("companion",
        () -> EntityType.Builder.of(CompanionEntity::new, MobCategory.CREATURE)
            .sized(0.6F, 1.8F)
            .build(CompanionMod.MOD_ID + ":companion")
    );

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
}
