package com.kilo.companionmod.item;

import com.kilo.companionmod.CompanionMod;
import com.kilo.companionmod.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CompanionMod.MOD_ID);

    public static final RegistryObject<Item> COMPANION_SPAWN_EGG = ITEMS.register("companion_spawn_egg",
        () -> new CompanionSpawnEggItem(ModEntities.COMPANION::get, 0xFFFFFF, 0xFFFFFF, new Item.Properties())
    );

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    public static class CompanionSpawnEggItem extends SpawnEggItem {
        private final Supplier<EntityType<?>> entityTypeSupplier;

        public CompanionSpawnEggItem(Supplier<EntityType<?>> entityTypeSupplier, int color1, int color2, Item.Properties properties) {
            super(null, color1, color2, properties);
            this.entityTypeSupplier = entityTypeSupplier;
        }

        @Override
        public EntityType<?> getType(CompoundTag tag) {
            EntityType<?> type = entityTypeSupplier.get();
            return type != null ? type : super.getType(tag);
        }
    }
}
