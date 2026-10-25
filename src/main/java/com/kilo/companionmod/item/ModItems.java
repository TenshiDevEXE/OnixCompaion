package com.kilo.companionmod.item;

import com.kilo.companionmod.CompanionMod;
import com.kilo.companionmod.ModEntities;
import com.kilo.companionmod.entity.CompanionEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CompanionMod.MOD_ID);

    public static final RegistryObject<Item> COMPANION_SPAWN_EGG = ITEMS.register("companion_spawn_egg",
        () -> {
            try {
                return new SpawnEggItem(ModEntities.COMPANION.get(), 0xFFFFFF, 0xFFFFFF, new Item.Properties());
            } catch (Throwable t) {
                CompanionMod.LOGGER.warn("No se pudo crear el spawn egg del compañero: entity type no disponible", t);
                return new Item(new Item.Properties());
            }
        }
    );

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
