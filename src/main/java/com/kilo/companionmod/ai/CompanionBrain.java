package com.kilo.companionmod.ai;

import com.kilo.companionmod.config.ModConfig;
import com.kilo.companionmod.entity.CompanionEntity;
import com.kilo.companionmod.skills.CompanionSkills;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CompanionBrain {
    public enum Action {
        FOLLOW, STAY, WANDER, ATTACK, MINE, COLLECT, HEAL, FLEE, TELEPORT
    }

    public enum Category {
        COMBAT, MINING, SURVIVAL, COLLECTION
    }

    private final CompanionEntity companion;
    private final Map<Action, Double> weights = new HashMap<>();
    private final Map<Category, Integer> levels = new HashMap<>();
    private final Map<Category, Integer> xp = new HashMap<>();
    private final Deque<BlockPos> memory = new ArrayDeque<>();
    private final int maxMemory = 50;
    private final Random random = new Random();
    private double epsilon = 0.15D;
    private final double epsilonDecay = 0.995D;
    private final double epsilonMin = 0.05D;
    private final double emaAlpha = 0.3D;

    public CompanionBrain(CompanionEntity companion) {
        this.companion = companion;
        // Pesos iniciales
        weights.put(Action.FOLLOW, 1.0D);
        weights.put(Action.STAY, 0.5D);
        weights.put(Action.WANDER, 0.3D);
        weights.put(Action.ATTACK, 0.8D);
        weights.put(Action.MINE, 0.4D);
        weights.put(Action.COLLECT, 0.6D);
        weights.put(Action.HEAL, 0.7D);
        weights.put(Action.FLEE, 0.2D);
        weights.put(Action.TELEPORT, 0.1D);

        for (Category cat : Category.values()) {
            levels.put(cat, 1);
            xp.put(cat, 0);
        }
    }

    public Action chooseAction() {
        if (random.nextDouble() < epsilon) {
            // Explorar: acción aleatoria
            Action[] actions = Action.values();
            return actions[random.nextInt(actions.length)];
        }
        // Explotar: mejor acción según pesos
        Action best = null;
        double bestWeight = Double.NEGATIVE_INFINITY;
        for (Map.Entry<Action, Double> entry : weights.entrySet()) {
            if (entry.getValue() > bestWeight) {
                bestWeight = entry.getValue();
                best = entry.getKey();
            }
        }
        return best != null ? best : Action.FOLLOW;
    }

    public void reward(Action action, double amount) {
        double current = weights.getOrDefault(action, 0.0D);
        double updated = current + emaAlpha * (amount - current);
        weights.put(action, updated);
        // Decay epsilon
        epsilon = Math.max(epsilonMin, epsilon * epsilonDecay);
    }

    public void penalize(Action action, double amount) {
        reward(action, -amount);
    }

    public void addXP(Category category, int amount) {
        if (amount <= 0) return;
        int cap = ModConfig.COMMON.skillLevelCap.get();
        int currentLevel = levels.getOrDefault(category, 1);
        if (currentLevel >= cap) return;

        int newXP = xp.getOrDefault(category, 0) + amount;
        int xpNeeded = xpForNextLevel(currentLevel);

        while (newXP >= xpNeeded && currentLevel < cap) {
            newXP -= xpNeeded;
            currentLevel++;
            levels.put(category, currentLevel);
            xpNeeded = xpForNextLevel(currentLevel);
        }
        xp.put(category, newXP);

        // Sincronizar con CompanionSkills
        companion.getSkills().addXP(convertCategory(category), amount);
    }

    private com.kilo.companionmod.skills.CompanionSkills.Category convertCategory(Category cat) {
        return switch (cat) {
            case COMBAT -> com.kilo.companionmod.skills.CompanionSkills.Category.COMBAT;
            case MINING -> com.kilo.companionmod.skills.CompanionSkills.Category.MINING;
            case SURVIVAL -> com.kilo.companionmod.skills.CompanionSkills.Category.SURVIVAL;
            case COLLECTION -> com.kilo.companionmod.skills.CompanionSkills.Category.COLLECTION;
        };
    }

    private int xpForNextLevel(int currentLevel) {
        return (int) (100 * Math.pow(1.5, currentLevel - 1));
    }

    public int getLevel(Category category) {
        return levels.getOrDefault(category, 1);
    }

    public int getXP(Category category) {
        return xp.getOrDefault(category, 0);
    }

    public double getWeight(Action action) {
        return weights.getOrDefault(action, 0.0D);
    }

    public double getEpsilon() {
        return epsilon;
    }

    public void remember(BlockPos pos) {
        memory.remove(pos);
        memory.addFirst(pos);
        while (memory.size() > maxMemory) {
            memory.removeLast();
        }
    }

    public boolean remembers(BlockPos pos) {
        return memory.contains(pos);
    }

    public Deque<BlockPos> getMemory() {
        return memory;
    }

    public void readFromNBT(CompoundTag tag) {
        for (Action action : Action.values()) {
            String key = "brain_" + action.name();
            if (tag.contains(key)) {
                weights.put(action, tag.getDouble(key));
            }
        }
        for (Category cat : Category.values()) {
            String prefix = "brain_" + cat.name();
            if (tag.contains(prefix + "_level")) {
                levels.put(cat, tag.getInt(prefix + "_level"));
            }
            if (tag.contains(prefix + "_xp")) {
                xp.put(cat, tag.getInt(prefix + "_xp"));
            }
        }
        if (tag.contains("brain_epsilon")) {
            epsilon = tag.getDouble("brain_epsilon");
        }
        // Memoria
        if (tag.contains("brain_memory")) {
            CompoundTag memTag = tag.getCompound("brain_memory");
            for (String key : memTag.getAllKeys()) {
                if (key.startsWith("pos")) {
                    int x = memTag.getInt(key + "_x");
                    int y = memTag.getInt(key + "_y");
                    int z = memTag.getInt(key + "_z");
                    memory.add(new BlockPos(x, y, z));
                }
            }
        }
    }

    public void writeToNBT(CompoundTag tag) {
        for (Map.Entry<Action, Double> entry : weights.entrySet()) {
            tag.putDouble("brain_" + entry.getKey().name(), entry.getValue());
        }
        for (Map.Entry<Category, Integer> entry : levels.entrySet()) {
            tag.putInt("brain_" + entry.getKey().name() + "_level", entry.getValue());
        }
        for (Map.Entry<Category, Integer> entry : xp.entrySet()) {
            tag.putInt("brain_" + entry.getKey().name() + "_xp", entry.getValue());
        }
        tag.putDouble("brain_epsilon", epsilon);

        CompoundTag memTag = new CompoundTag();
        int i = 0;
        for (BlockPos pos : memory) {
            String key = "pos" + i;
            memTag.putInt(key + "_x", pos.getX());
            memTag.putInt(key + "_y", pos.getY());
            memTag.putInt(key + "_z", pos.getZ());
            i++;
        }
        tag.put("brain_memory", memTag);
    }
}
