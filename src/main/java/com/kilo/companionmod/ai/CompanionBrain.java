package com.kilo.companionmod.ai;

import com.kilo.companionmod.CompanionMod;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.goal.Goal;

public class CompanionBrain {
    private CompanionOrderType currentOrder;
    private boolean isSitting;
    private boolean isAggressive;
    private int orderChangeCooldown;
    private final CompanionMemory memory;

    public CompanionBrain() {
        this.currentOrder = CompanionOrderType.FOLLOW;
        this.isSitting = false;
        this.isAggressive = false;
        this.orderChangeCooldown = 0;
        this.memory = new CompanionMemory();
    }

    public CompanionBrain(net.minecraft.world.entity.Entity entity) {
        this();
    }

    public CompanionOrderType getCurrentOrder() {
        return currentOrder;
    }

    public void setOrder(CompanionOrderType order) {
        this.currentOrder = order;
        this.orderChangeCooldown = 20;
        CompanionMod.LOGGER.info("Companion order changed to: {}", order.displayName);
    }

    public boolean isSitting() {
        return isSitting;
    }

    public void setSitting(boolean sitting) {
        this.isSitting = sitting;
        if (sitting) {
            this.currentOrder = CompanionOrderType.STAY;
        }
    }

    public boolean isAggressive() {
        return isAggressive;
    }

    public void setAggressive(boolean aggressive) {
        this.isAggressive = aggressive;
    }

    public int getOrderChangeCooldown() {
        return orderChangeCooldown;
    }

    public void tick() {
        if (orderChangeCooldown > 0) {
            orderChangeCooldown--;
        }
        memory.tick();
    }

    public CompanionMemory getMemory() {
        return memory;
    }

    public CompoundTag saveToTag() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Order", currentOrder.name());
        tag.putBoolean("Sitting", isSitting);
        tag.putBoolean("Aggressive", isAggressive);
        tag.putInt("OrderCooldown", orderChangeCooldown);
        tag.put("Memory", memory.saveToTag());
        return tag;
    }

    public void loadFromTag(CompoundTag tag) {
        try {
            this.currentOrder = CompanionOrderType.valueOf(tag.getString("Order"));
        } catch (IllegalArgumentException e) {
            this.currentOrder = CompanionOrderType.FOLLOW;
        }
        this.isSitting = tag.getBoolean("Sitting");
        this.isAggressive = tag.getBoolean("Aggressive");
        this.orderChangeCooldown = tag.getInt("OrderCooldown");
        this.memory.loadFromTag(tag.getCompound("Memory"));
    }
}
