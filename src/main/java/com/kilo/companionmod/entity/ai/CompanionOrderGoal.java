package com.kilo.companionmod.entity.ai;

import com.kilo.companionmod.config.ModConfig;
import com.kilo.companionmod.entity.CompanionEntity;
import com.kilo.companionmod.entity.CompanionOrder;
import com.kilo.companionmod.entity.CombatMode;
import com.kilo.companionmod.skills.CompanionSkillType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

public class CompanionOrderGoal extends Goal {
    private final CompanionEntity companion;
    private final Random random = new Random();
    private double homeX;
    private double homeY;
    private double homeZ;
    private int followCooldown;
    private int teleportCooldown;
    private int combatCooldown;
    private int pathFailCount;
    private int patrolIndex;
    private int gatherCooldown;
    private int mineCooldown;
    private int buildCooldown;
    private BlockPos[] patrolPoints;
    private BlockPos patrolCenter;
    private int patrolRadius;

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
        this.gatherCooldown = 0;
        this.mineCooldown = 0;
        this.buildCooldown = 0;
        this.patrolIndex = 0;
        this.patrolCenter = null;
        this.patrolPoints = null;
        this.patrolRadius = 8;
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
        if (this.gatherCooldown > 0) this.gatherCooldown--;
        if (this.mineCooldown > 0) this.mineCooldown--;
        if (this.buildCooldown > 0) this.buildCooldown--;

        CompanionOrder order = this.companion.getOrder();
        CombatMode combatMode = this.companion.getCombatMode();

        switch (order) {
            case STAY -> handleStay();
            case FOLLOW -> handleFollow(level);
            case WANDER -> handleWander(level);
            case PATROL -> handlePatrol(level);
            case GUARD -> handleGuard(level);
            case GATHER -> handleGather(level);
            case MINE -> handleMine(level);
            case BUILD -> handleBuild(level);
        }

        handleCombat(level, combatMode);
        handleTeleportSafety(level);
        handleItemPickup(level);
    }

    private void handleStay() {
        BlockPos target = this.companion.isHomeSet() ? this.companion.getHomePosition() : new BlockPos((int) homeX, (int) homeY, (int) homeZ);
        double radius = this.companion.isHomeSet() ? this.companion.getHomeRadius() : 16.0D;
        double dx = this.companion.getX() - target.getX();
        double dz = this.companion.getZ() - target.getZ();
        if (dx * dx + dz * dz > radius * radius) {
            this.moveTowards(target.getX(), target.getY(), target.getZ(), this.companion.getMovementSpeedMultiplier() * 0.6D);
        } else {
            this.companion.getNavigation().stop();
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
        if (this.companion.getNavigation().isDone()) {
            BlockPos target = this.companion.isHomeSet() ? this.companion.getHomePosition() : new BlockPos((int) homeX, (int) homeY, (int) homeZ);
            double radius = this.companion.isHomeSet() ? this.companion.getHomeRadius() : 16.0D;
            Random random = this.random;
            double angle = random.nextDouble() * 2 * Math.PI;
            double dist = random.nextDouble() * radius * 0.5D;
            int tx = (int) (target.getX() + Math.cos(angle) * dist);
            int ty = target.getY();
            int tz = (int) (target.getZ() + Math.sin(angle) * dist);
            this.moveTowards(tx, ty, tz, 0.4D * this.companion.getMovementSpeedMultiplier());
        }
    }

    private void handlePatrol(Level level) {
        if (this.patrolPoints == null || this.patrolPoints.length == 0) {
            this.initializePatrol(level);
        }
        if (this.patrolPoints == null || this.patrolPoints.length == 0) {
            this.companion.getNavigation().stop();
            return;
        }

        BlockPos target = this.patrolPoints[this.patrolIndex];
        double dx = this.companion.getX() - target.getX();
        double dz = this.companion.getZ() - target.getZ();
        if (dx * dx + dz * dz < 2.0D) {
            this.patrolIndex = (this.patrolIndex + 1) % this.patrolPoints.length;
            target = this.patrolPoints[this.patrolIndex];
        }
        this.moveTowards(target.getX(), target.getY(), target.getZ(), 0.6D * this.companion.getMovementSpeedMultiplier());
    }

    private void initializePatrol(Level level) {
        BlockPos center = this.companion.isHomeSet() ? this.companion.getHomePosition() : new BlockPos((int) this.companion.getX(), (int) this.companion.getY(), (int) this.companion.getZ());
        this.patrolCenter = center;
        this.patrolPoints = new BlockPos[4];
        Random random = this.random;
        for (int i = 0; i < 4; i++) {
            double angle = i * Math.PI / 2;
            int tx = (int) (center.getX() + Math.cos(angle) * patrolRadius);
            int ty = center.getY();
            int tz = (int) (center.getZ() + Math.sin(angle) * patrolRadius);
            this.patrolPoints[i] = new BlockPos(tx, ty, tz);
        }
    }

    private void handleGuard(Level level) {
        Player owner = this.findOwner(level);
        if (owner == null) {
            this.companion.getNavigation().stop();
            return;
        }

        double dx = this.companion.getX() - owner.getX();
        double dz = this.companion.getZ() - owner.getZ();
        if (dx * dx + dz * dz > 9.0D) {
            this.moveTowards(owner.getX(), owner.getY(), owner.getZ(), 0.8D * this.companion.getMovementSpeedMultiplier());
        } else {
            this.companion.getNavigation().stop();
        }
    }

    private void handleGather(Level level) {
        if (this.gatherCooldown > 0) return;
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
            double distSq = this.companion.distanceToSqr(nearest);
            if (distSq < 1.5D) {
                ItemStack stack = nearest.getItem();
                if (!stack.isEmpty()) {
                    if (this.companion.addItemToInventory(stack.copy())) {
                        nearest.discard();
                    }
                }
                this.gatherCooldown = 10;
            } else {
                this.moveTowards(nearest.getX(), nearest.getY(), nearest.getZ(), 1.0D);
                this.gatherCooldown = 5;
            }
        }
    }

    private void handleMine(Level level) {
        if (this.mineCooldown > 0) return;
        Player owner = this.findOwner(level);
        if (owner == null) return;

        BlockPos target = this.findMineableBlock(level, owner);
        if (target != null) {
            double distSq = this.companion.distanceToSqr(target.getX(), target.getY(), target.getZ());
            if (distSq < 2.0D) {
                level.destroyBlock(target, true, this.companion);
                this.companion.getSkills().addExperience(CompanionSkillType.MINING, 10);
                this.mineCooldown = 20;
            } else {
                this.moveTowards(target.getX(), target.getY(), target.getZ(), 0.8D);
            }
        } else {
            this.companion.getNavigation().stop();
        }
    }

    private BlockPos findMineableBlock(Level level, Player owner) {
        BlockPos ownerPos = owner.blockPosition();
        int range = 6;
        for (int x = -range; x <= range; x++) {
            for (int y = -3; y <= 3; y++) {
                for (int z = -range; z <= range; z++) {
                    BlockPos pos = ownerPos.offset(x, y, z);
                    Block block = level.getBlockState(pos).getBlock();
                    if (block == Blocks.STONE || block == Blocks.COBBLESTONE || block == Blocks.DEEPSLATE ||
                        block == Blocks.COAL_ORE || block == Blocks.IRON_ORE || block == Blocks.GOLD_ORE ||
                        block == Blocks.DIAMOND_ORE || block == Blocks.EMERALD_ORE) {
                        return pos;
                    }
                }
            }
        }
        return null;
    }

    private void handleBuild(Level level) {
        if (this.buildCooldown > 0) return;
        Player owner = this.findOwner(level);
        if (owner == null) return;

        ItemStack blockToPlace = this.companion.getEquipmentSlot(net.minecraft.world.entity.EquipmentSlot.MAINHAND);
        if (blockToPlace.isEmpty() || !(blockToPlace.getItem() instanceof BlockItem)) return;

        BlockPos target = this.findBuildPosition(level, owner);
        if (target != null) {
            double distSq = this.companion.distanceToSqr(target.getX(), target.getY(), target.getZ());
            if (distSq < 2.0D) {
                level.setBlock(target, ((BlockItem) blockToPlace.getItem()).getBlock().defaultBlockState(), 3);
                blockToPlace.shrink(1);
                this.companion.getSkills().addExperience(CompanionSkillType.BUILDING, 10);
                this.buildCooldown = 20;
            } else {
                this.moveTowards(target.getX(), target.getY(), target.getZ(), 0.8D);
            }
        } else {
            this.companion.getNavigation().stop();
        }
    }

    private BlockPos findBuildPosition(Level level, Player owner) {
        BlockPos ownerPos = owner.blockPosition();
        int range = 4;
        for (int x = -range; x <= range; x++) {
            for (int y = 0; y <= 2; y++) {
                for (int z = -range; z <= range; z++) {
                    BlockPos pos = ownerPos.offset(x, y, z);
                    if (level.isEmptyBlock(pos) && !level.isEmptyBlock(pos.below())) {
                        return pos;
                    }
                }
            }
        }
        return null;
    }

    private void handleCombat(Level level, CombatMode mode) {
        if (mode == CombatMode.PASSIVE || this.combatCooldown > 0) return;

        Player owner = this.findOwner(level);
        List<LivingEntity> hostiles = level.getEntitiesOfClass(LivingEntity.class,
            new AABB(this.companion.getX() - 10.0D, this.companion.getY() - 2.0D, this.companion.getZ() - 10.0D,
                     this.companion.getX() + 10.0D, this.companion.getY() + 2.0D, this.companion.getZ() + 10.0D),
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
            } else if (mode == CombatMode.DEFENSIVE && owner != null) {
                if (target.distanceToSqr(owner) < 16.0D) {
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
            double tx = owner.getX() + (this.random.nextDouble() - 0.5) * 4.0D;
            double ty = owner.getY();
            double tz = owner.getZ() + (this.random.nextDouble() - 0.5) * 4.0D;
            this.companion.setPos(tx, ty, tz);
            this.companion.getNavigation().stop();
            this.teleportCooldown = 100;
            this.pathFailCount = 0;
        } else if (this.pathFailCount >= 3) {
            double tx = owner.getX() + (this.random.nextDouble() - 0.5) * 4.0D;
            double ty = owner.getY();
            double tz = owner.getZ() + (this.random.nextDouble() - 0.5) * 4.0D;
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
