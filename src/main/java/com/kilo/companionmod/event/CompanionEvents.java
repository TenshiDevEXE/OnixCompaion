package com.kilo.companionmod.event;

import com.kilo.companionmod.ModEntities;
import com.kilo.companionmod.entity.CompanionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = "companionmod", bus = EventBusSubscriber.Bus.FORGE)
public class CompanionEvents {

    @SubscribeEvent
    public static void onCompanionDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof CompanionEntity)) return;

        CompanionEntity companion = (CompanionEntity) entity;

        // Droppear inventario
        try {
            for (ItemStack stack : companion.getInventory()) {
                if (stack != null && !stack.isEmpty()) {
                    entity.spawnAtLocation(stack);
                }
            }
        } catch (Exception ignored) {
        }

        // Droppear equipamiento
        try {
            for (ItemStack stack : companion.getEquipment()) {
                if (stack != null && !stack.isEmpty()) {
                    entity.spawnAtLocation(stack);
                }
            }
        } catch (Exception ignored) {
        }

        // Respawn en spawn point del mundo
        var level = entity.level();
        if (!(level instanceof ServerLevel serverLevel)) return;

        BlockPos spawnPos = serverLevel.getSharedSpawnPos();
        CompoundTag saveTag = new CompoundTag();
        companion.saveWithoutId(saveTag);

        respawnCompanion(serverLevel, spawnPos, saveTag);
    }

    private static void respawnCompanion(ServerLevel level, BlockPos spawnPos, CompoundTag tag) {
        try {
            EntityType<? extends CompanionEntity> type = ModEntities.COMPANION.get();
            CompanionEntity newCompanion = type.create(level);
            if (newCompanion == null) return;

            newCompanion.moveTo((double) spawnPos.getX(), (double) spawnPos.getY(), (double) spawnPos.getZ(), 0.0F, 0.0F);
            newCompanion.load(tag);
            level.addFreshEntity(newCompanion);
        } catch (Exception ignored) {
        }
    }
}
