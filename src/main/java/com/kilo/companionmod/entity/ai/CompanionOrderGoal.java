package com.kilo.companionmod.entity.ai;

import com.kilo.companionmod.config.ModConfig;
import com.kilo.companionmod.entity.CompanionEntity;
import com.kilo.companionmod.entity.CompanionOrder;
import com.kilo.companionmod.entity.CombatMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

public class CompanionOrderGoal extends Goal {
    private final CompanionEntity companion;
    private double homeX;
    private double homeY;
    private double homeZ;
    private int followCooldown;
    private int teleportCooldown;
    private int combatCooldown;
    private int pathFailCount;

    public CompanionOrderGoal(CompanionEntity companion) {
        this.companion = companion;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.TARGET));
        this.homeX = companion.getX();
        this.homeY = companion.getY();
        this.homeZ = companion.getZ();
    }

    @Override
    public boolean canUse() {
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return true;
    }

    @Override
    public void start() {
        this.homeX = this.companion.getX();
        this.homeY = this.companion.getY();
        this.homeZ = this.companion.getZ();
        this.followCooldown = 0;
        this.teleportCooldown = 0;
        this.combatCooldown = 0;
        this.pathFailCount = 0;
    }

    @Override
    public void tick() {
        if (this.companion.isSitting()) {
            this.companion.getNavigation().stop();
            return;
        }

        Level level = this.companion.level();
        if (level.isClientSide) return;

        if (this.followCooldown > 0) this.followCooldown--;
        if (this.teleportCooldown > 0) this.teleportCooldown--;
        if (this.combatCooldown > 0) this.combatCooldown--;

        CompanionOrder order = this.companion.getOrder();
        CombatMode combatMode = this.companion.getCombatMode();

        if (order == CompanionOrder.STAY) {
            handleStay();
        } else if (order == CompanionOrder.FOLLOW) {
            handleFollow(level);
        } else if (order == CompanionOrder.WANDER) {
            handleWander(level);
        }

        handleCombat(level, combatMode);
        handleTeleportSafety(level);
        handleItemPickup(level);
    }

    private void handleStay() {
        if (this.companion.isHomeSet()) {
            BlockPos home = this.companion.getHomePosition();
            double dx = this.companion.getX() - home.getX();
            double dz = this.companion.getZ() - home.getZ();
            double radius = this.companion.getHomeRadius();
            if (dx * dx + dz * dz > radius * radius) {
                this.moveTowards(home.getX(), home.getY(), home.getZ(), this.companion.getMovementSpeedMultiplier() * 0.6D);
            } else {
                this.companion.getNavigation().stop();
            }
        } else {
            double dx = this.companion.getX() - this.homeX;
            double dz = this.companion.getZ() - this.homeZ;
            if (dx * dx + dz * dz > 16.0D) {
                this.moveTowards(this.homeX, this.homeY, this.homeZ, 0.6D);
            } else {
                this.companion.getNavigation().stop();
            }
        }
    }

    private void handleFollow(Level level) {
        Player owner = this.findOwner(level);
        if (owner != null) {
            double dx = this.companion.getX() - owner.getX();
            double dz = this.companion.getZ() - owner.getZ();
            if (dx * dx + dz * dz > 4.0D && this.followCooldown <= 0) {
            double speed = 1.1D * this.companion.getMovementSpeedMultiplier();
            this.moveTowards(owner.getX(), owner.getY(), owner.getZ(), speed);
            this.followCooldown = 10;
            } else {
                this.companion.getNavigation().stop();
            }
        } else {
            this.companion.getNavigation().stop();
        }
    }

    private void handleWander(Level level) {
        if (this.companion.isHomeSet()) {
            BlockPos home = this.companion.getHomePosition();
            double dx = this.companion.getX() - home.getX();
            double dz = this.companion.getZ() - home.getZ();
            double radius = this.companion.getHomeRadius();
            if (dx * dx + dz * dz > radius * radius * 0.5D) {
                this.moveTowards(home.getX(), home.getY(), home.getZ(), 0.4D * this.companion.getMovementSpeedMultiplier());
            } else {
                this.companion.getNavigation().stop();
            }
        } else {
            this.companion.getNavigation().stop();
        }
    }

    private void handleCombat(Level level, CombatMode mode) {
        if (mode == CombatMode.PASSIVE || this.combatCooldown > 0) return;

        Player owner = this.findOwner(level);
        List<LivingEntity> hostiles = level.getEntitiesOfClass(LivingEntity.class,
            new AABB(this.companion.getX() - 8.0D, this.companion.getY() - 2.0D, this.companion.getZ() - 8.0D,
                     this.companion.getX() + 8.0D, this.companion.getY() + 2.0D, this.companion.getZ() + 8.0D),
            e -> e.isAlive() && e instanceof Mob && e != this.companion && !(e instanceof Player));

        LivingEntity target = null;
        if (!hostiles.isEmpty()) {
            target = hostiles.stream()
                .min(Comparator.comparingDouble(e -> e.distanceToSqr(this.companion)))
                .orElse(null);
        }

        if (target != null) {
            boolean shouldAttack = false;
            if (mode == CombatMode.AGGRESSIVE) {
                shouldAttack = true;
            } else             if (mode == CombatMode.DEFENSIVE && owner != null) {
                if (target.distanceToSqr(owner) < 25.0D) {
                    shouldAttack = true;
                }
            }

            if (shouldAttack) {
                this.companion.setTarget(target);
                this.companion.getLookControl().setLookAt(target.getX(), target.getEyeY(), target.getZ(), 10.0F, 10.0F);
                double dx = this.companion.getX() - target.getX();
                double dz = this.companion.getZ() - target.getZ();
                if (dx * dx + dz * dz > 2.25D && this.combatCooldown <= 0) {
                    this.moveTowards(target.getX(), target.getY(), target.getZ(), 1.2D * this.companion.getMovementSpeedMultiplier());
                    this.combatCooldown = 10;
                } else {
                    this.companion.getNavigation().stop();
                }
            }
        }
    }

    private void handleTeleportSafety(Level level) {
        if (this.teleportCooldown > 0) return;

        Player owner = this.findOwner(level);
        if (owner == null) return;

        double dx = this.companion.getX() - owner.getX();
        double dz = this.companion.getZ() - owner.getZ();
        double distSq = dx * dx + dz * dz;
        int teleportDist = ModConfig.COMMON.teleportDistance.get();

        if (distSq > teleportDist * teleportDist) {
            double tx = owner.getX() + (this.companion.getRandom().nextDouble() - 0.5) * 4.0D;
            double ty = owner.getY();
            double tz = owner.getZ() + (this.companion.getRandom().nextDouble() - 0.5) * 4.0D;
            this.companion.setPos(tx, ty, tz);
            this.companion.getNavigation().stop();
            this.teleportCooldown = 100;
            this.pathFailCount = 0;
        } else if (this.pathFailCount >= 3) {
            double tx = owner.getX() + (this.companion.getRandom().nextDouble() - 0.5) * 4.0D;
            double ty = owner.getY();
            double tz = owner.getZ() + (this.companion.getRandom().nextDouble() - 0.5) * 4.0D;
            this.companion.setPos(tx, ty, tz);
            this.companion.getNavigation().stop();
            this.teleportCooldown = 60;
            this.pathFailCount = 0;
        }
    }

    private Player findOwner(Level level) {
        return level.players().stream()
            .min(Comparator.comparingDouble(p -> p.distanceToSqr(this.companion)))
            .orElse(null);
    }

    private void moveTowards(double x, double y, double z, double speed) {
        this.companion.getNavigation().moveTo(x, y, z, speed);
        this.companion.getLookControl().setLookAt(x, y, z, 10.0F, 10.0F);
    }

    private void handleItemPickup(Level level) {
        int radius = ModConfig.COMMON.itemPickupRadius.get();
        if (radius <= 0) return;
        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class,
            new AABB(this.companion.getX() - radius, this.companion.getY() - 2.0D, this.companion.getZ() - radius,
                     this.companion.getX() + radius, this.companion.getY() + 2.0D, this.companion.getZ() + radius),
            ItemEntity::isAlive);
        if (items.isEmpty()) return;
        ItemEntity nearest = items.stream()
            .min(Comparator.comparingDouble(e -> e.distanceToSqr(this.companion)))
            .orElse(null);
        if (nearest != null) {
            double dx = this.companion.getX() - nearest.getX();
            double dz = this.companion.getZ() - nearest.getZ();
            if (dx * dx + dz * dz < 1.5D) {
                ItemStack stack = nearest.getItem();
                if (!stack.isEmpty()) {
                    if (this.companion.addItemToInventory(stack.copy())) {
                        nearest.discard();
                    }
                }
            } else {
                this.companion.getNavigation().moveTo(nearest.getX(), nearest.getY(), nearest.getZ(), 1.0D);
                this.companion.getLookControl().setLookAt(nearest.getX(), nearest.getY(), nearest.getZ(), 10.0F, 10.0F);
            }
        }
    }
}
