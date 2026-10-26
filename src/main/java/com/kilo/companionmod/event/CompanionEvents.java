package com.kilo.companionmod.event;

import com.kilo.companionmod.entity.CompanionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

public class CompanionEvents {

    @SubscribeEvent
    public static void onCompanionDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof CompanionEntity)) return;

        CompanionEntity companion = (CompanionEntity) entity;

        try {
            for (ItemStack stack : companion.getInventory()) {
                if (stack != null && !stack.isEmpty()) {
                    entity.spawnAtLocation(stack);
                }
            }
        } catch (Exception ignored) {
        }

        try {
            for (ItemStack stack : companion.getEquipment()) {
                if (stack != null && !stack.isEmpty()) {
                    entity.spawnAtLocation(stack);
                }
            }
        } catch (Exception ignored) {
        }

        var level = entity.level();
        if (!(level instanceof ServerLevel serverLevel)) return;

        BlockPos spawnPos = serverLevel.getSharedSpawnPos();
        CompoundTag saveTag = new CompoundTag();
        companion.saveWithoutId(saveTag);

        respawnCompanion(serverLevel, spawnPos, saveTag);
    }

    private static void respawnCompanion(ServerLevel level, BlockPos spawnPos, CompoundTag tag) {
        try {
            var type = com.kilo.companionmod.ModEntities.COMPANION.get();
            CompanionEntity newCompanion = type.create(level);
            if (newCompanion == null) return;

            newCompanion.moveTo((double) spawnPos.getX(), (double) spawnPos.getY(), (double) spawnPos.getZ(), 0.0F, 0.0F);
            newCompanion.load(tag);
            level.addFreshEntity(newCompanion);
        } catch (Exception ignored) {
        }
    }
}
