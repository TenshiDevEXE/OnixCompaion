package com.kilo.companionmod.entity;

import com.kilo.companionmod.CompanionMod;
import com.kilo.companionmod.ai.CompanionBrain;
import com.kilo.companionmod.config.ModConfig;
import com.kilo.companionmod.entity.ai.CompanionOrderGoal;
import com.kilo.companionmod.gui.CompanionScreen;
import com.kilo.companionmod.skills.CompanionSkills;
import com.kilo.companionmod.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

public class CompanionEntity extends PathfinderMob {
    private static final EntityDataAccessor<Integer> DATA_ORDER_ID =
        SynchedEntityData.defineId(CompanionEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_SITTING =
        SynchedEntityData.defineId(CompanionEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_COMBAT_MODE =
        SynchedEntityData.defineId(CompanionEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_LOYALTY =
        SynchedEntityData.defineId(CompanionEntity.class, EntityDataSerializers.INT);

    private BlockPos homePosition;
    private int homeRadius = 16;
    private int loyalty = 100;
    private int loyaltyCooldown = 0;
    private boolean configApplied = false;

    private final CompanionSkills skills = new CompanionSkills();
    private final CompanionBrain brain = new CompanionBrain(this);
    private final ItemStack[] inventory = new ItemStack[27];
    private final ItemStack[] equipment = new ItemStack[5];

    public CompanionEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        try {
            applyConfig();
        } catch (Exception ignored) {
        }
    }

    private void applyConfig() {
        if (this.configApplied || this.level() == null || this.level().isClientSide) return;
        try {
            this.configApplied = true;
            var healthAttr = this.getAttribute(Attributes.MAX_HEALTH);
            var speedAttr = this.getAttribute(Attributes.MOVEMENT_SPEED);
            var damageAttr = this.getAttribute(Attributes.ATTACK_DAMAGE);
            if (healthAttr != null) healthAttr.setBaseValue(ModConfig.COMMON.maxHealth.get());
            if (speedAttr != null) speedAttr.setBaseValue(ModConfig.COMMON.movementSpeed.get());
            if (damageAttr != null) damageAttr.setBaseValue(ModConfig.COMMON.attackDamage.get());
            this.setHealth(this.getMaxHealth());
        } catch (Exception ignored) {
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 20.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.35D)
            .add(Attributes.ATTACK_DAMAGE, 4.0D)
            .add(Attributes.ARMOR, 2.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new CompanionOrderGoal(this));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ORDER_ID, CompanionOrder.FOLLOW.ordinal());
        builder.define(DATA_SITTING, false);
        builder.define(DATA_COMBAT_MODE, CombatMode.DEFENSIVE.ordinal());
        builder.define(DATA_LOYALTY, 100);
    }

    public CompanionOrder getOrder() {
        return CompanionOrder.values()[this.entityData.get(DATA_ORDER_ID)];
    }

    public void setOrder(CompanionOrder order) {
        this.entityData.set(DATA_ORDER_ID, order.ordinal());
    }

    public boolean isSitting() {
        return this.entityData.get(DATA_SITTING);
    }

    public void setSitting(boolean sitting) {
        this.entityData.set(DATA_SITTING, sitting);
    }

    public CombatMode getCombatMode() {
        return CombatMode.values()[this.entityData.get(DATA_COMBAT_MODE)];
    }

    public void setCombatMode(CombatMode mode) {
        this.entityData.set(DATA_COMBAT_MODE, mode.ordinal());
    }

    public int getLoyalty() {
        return this.entityData.get(DATA_LOYALTY);
    }

    public void setLoyalty(int loyalty) {
        this.entityData.set(DATA_LOYALTY, Math.max(0, Math.min(100, loyalty)));
    }

    public void modifyLoyalty(int amount) {
        setLoyalty(getLoyalty() + amount);
    }

    @Nullable
    public String getOwnerName() {
        return this.ownerName;
    }

    public void setOwnerName(String name) {
        this.ownerName = name;
    }

    @Nullable
    private String ownerName;

    public BlockPos getHomePosition() {
        return this.homePosition;
    }

    public void setHomePosition(BlockPos pos) {
        this.homePosition = pos;
    }

    public int getHomeRadius() {
        return this.homeRadius;
    }

    public void setHomeRadius(int radius) {
        this.homeRadius = radius;
    }

    public boolean isHomeSet() {
        return this.homePosition != null;
    }

    public boolean isDaytime(Level level) {
        return level.isDay();
    }

    public double getMovementSpeedMultiplier() {
        double base = 1.0D;
        base *= this.skills.getMovementSpeedBonus();
        if (this.level() != null && !this.level().isDay()) {
            base *= 0.7D;
        }
        return base;
    }

    public double getAttackDamageBonus() {
        double base = 1.0D;
        CombatMode mode = getCombatMode();
        if (mode == CombatMode.AGGRESSIVE) base += 0.5D;
        if (mode == CombatMode.DEFENSIVE) base += 0.25D;
        base *= this.skills.getCombatDamageBonus();
        return base;
    }

    public double getMiningSpeedMultiplier() {
        return this.skills.getMiningSpeedBonus();
    }

    public int getExtraMiningLoot() {
        return this.skills.getExtraMiningLoot();
    }

    public double getArmorBonus() {
        return this.skills.getArmorBonus();
    }

    public int getUnderwaterBreathTicks() {
        return this.skills.getUnderwaterBreathTicks();
    }

    public double getCollectionRangeBonus() {
        return this.skills.getCollectionRangeBonus();
    }

    public int getCollectionSpeed() {
        return this.skills.getCollectionSpeed();
    }

    public boolean shouldObeyOrder() {
        return this.loyaltyCooldown <= 0 && this.getLoyalty() > 20;
    }

    public void setLoyaltyCooldown(int ticks) {
        this.loyaltyCooldown = ticks;
    }

    public void tickLoyalty() {
        if (this.loyaltyCooldown > 0) {
            this.loyaltyCooldown--;
        }
    }

    public CompanionSkills getSkills() {
        return this.skills;
    }

    public CompanionBrain getCompanionBrain() {
        return this.brain;
    }

    public ItemStack[] getInventory() {
        return this.inventory;
    }

    public ItemStack getInventorySlot(int slot) {
        if (slot >= 0 && slot < this.inventory.length) {
            return this.inventory[slot];
        }
        return ItemStack.EMPTY;
    }

    public boolean addItemToInventory(ItemStack stack) {
        if (stack.isEmpty()) return false;
        for (int i = 0; i < this.inventory.length; i++) {
            if (this.inventory[i] == null || this.inventory[i].isEmpty()) {
                this.inventory[i] = stack.copy();
                return true;
            }
        }
        return false;
    }

    public ItemStack getEquipmentSlot(EquipmentSlot slot) {
        int index = slotToIndex(slot);
        if (index >= 0 && index < this.equipment.length) {
            return this.equipment[index] == null ? ItemStack.EMPTY : this.equipment[index];
        }
        return ItemStack.EMPTY;
    }

    public ItemStack[] getEquipment() {
        return this.equipment;
    }

    public void setEquipmentSlot(EquipmentSlot slot, ItemStack stack) {
        int index = slotToIndex(slot);
        if (index >= 0 && index < this.equipment.length) {
            this.equipment[index] = stack == null ? ItemStack.EMPTY : stack;
        }
    }

    private int slotToIndex(EquipmentSlot slot) {
        switch (slot) {
            case HEAD: return 0;
            case CHEST: return 1;
            case LEGS: return 2;
            case FEET: return 3;
            case MAINHAND: return 4;
            default: return -1;
        }
    }

    public boolean heal(int amount) {
        if (amount <= 0) return false;
        float newHealth = this.getHealth() + amount;
        float maxHealth = (float) this.getMaxHealth();
        if (newHealth > maxHealth) newHealth = maxHealth;
        this.setHealth(newHealth);
        return true;
    }

    @Override
    public void tick() {
        try {
            super.tick();
            tickLoyalty();
            applyAttributeBonuses();
        } catch (Exception ignored) {
        }
    }

    private void applyAttributeBonuses() {
        try {
            double armorBonus = getArmorBonus();
            var armorAttr = this.getAttribute(Attributes.ARMOR);
            if (armorAttr != null) {
                armorAttr.setBaseValue(2.0D + armorBonus);
            }
        } catch (Exception ignored) {
        }
    }

    public InteractionResult onRightClick(Player player, InteractionHand hand) {
        try {
            ItemStack stack = player.getItemInHand(hand);
            boolean client = player.level().isClientSide;
            if (!client) {
                if (!stack.isEmpty() && isFood(stack)) {
                    int healAmount = getFoodHealAmount(stack, player);
                    if (healAmount > 0) {
                        stack.shrink(1);
                        heal(healAmount);
                        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), ModSounds.COMPANION_ORDER.get(), SoundSource.NEUTRAL, 1.0F, 1.0F);
                        player.displayClientMessage(Component.literal("Compañero curado " + healAmount + " PV"), true);
                        return InteractionResult.SUCCESS;
                    }
                }
                if (stack.isEmpty()) {
                    boolean sitting = !this.isSitting();
                    this.setSitting(sitting);
                    if (sitting) {
                        this.setOrder(CompanionOrder.STAY);
                    } else {
                        this.setOrder(CompanionOrder.FOLLOW);
                    }
                    this.level().playSound(null, this.getX(), this.getY(), this.getZ(), ModSounds.COMPANION_ORDER.get(), SoundSource.NEUTRAL, 1.0F, 1.0F);
                    this.level().addParticle(ParticleTypes.HEART, this.getX(), this.getY() + 1.0D, this.getZ(), 0.0D, 0.2D, 0.0D);
                    this.level().addParticle(ParticleTypes.HEART, this.getX() + 0.3D, this.getY() + 1.0D, this.getZ() - 0.3D, 0.0D, 0.2D, 0.0D);
                    player.displayClientMessage(Component.literal("Compañero " + (sitting ? "se queda quieto" : "te sigue")), true);
                    return InteractionResult.SUCCESS;
                }
            }
            if (client && stack.isEmpty()) {
                net.minecraft.client.Minecraft.getInstance().setScreen(
                    new CompanionScreen(Component.literal("Órdenes del Compañero"), this.getId()));
            }
        } catch (Exception ignored) {
        }
        return super.mobInteract(player, hand);
    }

    public boolean isFood(ItemStack stack) {
        return stack.getItem().getFoodProperties(stack, this) != null;
    }

    private int getFoodHealAmount(ItemStack stack, Player player) {
        var props = stack.getItem().getFoodProperties(stack, player);
        if (props == null) return 0;
        return props.nutrition() * 2;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Order", this.getOrder().ordinal());
        tag.putBoolean("Sitting", this.isSitting());
        tag.putInt("CombatMode", this.getCombatMode().ordinal());
        tag.putInt("Loyalty", this.getLoyalty());
        if (this.ownerName != null) {
            tag.putString("OwnerName", this.ownerName);
        }
        if (this.homePosition != null) {
            tag.putInt("HomeX", this.homePosition.getX());
            tag.putInt("HomeY", this.homePosition.getY());
            tag.putInt("HomeZ", this.homePosition.getZ());
            tag.putInt("HomeRadius", this.homeRadius);
        }
        tag.put("Skills", this.skills.saveToTag());
        tag.put("Brain", this.brain.saveToTag());

        CompoundTag invTag = new CompoundTag();
        for (int i = 0; i < this.inventory.length; i++) {
            if (this.inventory[i] != null && !this.inventory[i].isEmpty()) {
                CompoundTag slotTag = new CompoundTag();
                slotTag.putByte("Slot", (byte) i);
                this.inventory[i].save(this.level().registryAccess(), slotTag);
                invTag.put("Item" + i, slotTag);
            }
        }
        tag.put("Inventory", invTag);

        CompoundTag equipTag = new CompoundTag();
        for (int i = 0; i < this.equipment.length; i++) {
            if (this.equipment[i] != null && !this.equipment[i].isEmpty()) {
                CompoundTag slotTag = new CompoundTag();
                slotTag.putByte("Slot", (byte) i);
                this.equipment[i].save(this.level().registryAccess(), slotTag);
                equipTag.put("Item" + i, slotTag);
            }
        }
        tag.put("Equipment", equipTag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Order")) {
            this.setOrder(CompanionOrder.values()[tag.getInt("Order")]);
        }
        this.setSitting(tag.getBoolean("Sitting"));
        if (tag.contains("CombatMode")) {
            this.setCombatMode(CombatMode.values()[tag.getInt("CombatMode")]);
        }
        if (tag.contains("Loyalty")) {
            this.setLoyalty(tag.getInt("Loyalty"));
        }
        if (tag.contains("OwnerName")) {
            this.ownerName = tag.getString("OwnerName");
        }
        if (tag.contains("HomeX") && tag.contains("HomeY") && tag.contains("HomeZ")) {
            this.homePosition = new BlockPos(tag.getInt("HomeX"), tag.getInt("HomeY"), tag.getInt("HomeZ"));
            if (tag.contains("HomeRadius")) {
                this.homeRadius = tag.getInt("HomeRadius");
            }
        }
        if (tag.contains("Skills")) this.skills.loadFromTag(tag.getCompound("Skills"));
        if (tag.contains("Brain")) this.brain.loadFromTag(tag.getCompound("Brain"));

        if (tag.contains("Inventory")) {
            CompoundTag invTag = tag.getCompound("Inventory");
            for (String key : invTag.getAllKeys()) {
                if (key.startsWith("Item")) {
                    CompoundTag slotTag = invTag.getCompound(key);
                    byte slot = slotTag.getByte("Slot");
                    ItemStack stack = ItemStack.parse(this.level().registryAccess(), slotTag).orElse(ItemStack.EMPTY);
                    if (slot >= 0 && slot < this.inventory.length) {
                        this.inventory[slot] = stack;
                    }
                }
            }
        }

        if (tag.contains("Equipment")) {
            CompoundTag equipTag = tag.getCompound("Equipment");
            for (String key : equipTag.getAllKeys()) {
                if (key.startsWith("Item")) {
                    CompoundTag slotTag = equipTag.getCompound(key);
                    byte slot = slotTag.getByte("Slot");
                    ItemStack stack = ItemStack.parse(this.level().registryAccess(), slotTag).orElse(ItemStack.EMPTY);
                    if (slot >= 0 && slot < this.equipment.length) {
                        this.equipment[slot] = stack;
                    }
                }
            }
        }
    }
}
