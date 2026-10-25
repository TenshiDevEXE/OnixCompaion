package com.kilo.companionmod.skills;

import com.kilo.companionmod.config.ModConfig;
import net.minecraft.nbt.CompoundTag;

import java.util.HashMap;
import java.util.Map;

public class CompanionSkills {
    public enum Category {
        COMBAT("combat"),
        MINING("mining"),
        SURVIVAL("survival"),
        COLLECTION("collection");

        private final String key;

        Category(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }

    public enum Skill {
        COMBAT_POWER(Category.COMBAT, "combat_power"),
        COMBAT_REFLEXES(Category.COMBAT, "combat_reflexes"),
        MINING_SPEED(Category.MINING, "mining_speed"),
        MINING_LOOT(Category.MINING, "mining_loot"),
        SURVIVAL_ARMOR(Category.SURVIVAL, "survival_armor"),
        SURVIVAL_BREATH(Category.SURVIVAL, "survival_breath"),
        COLLECTION_RANGE(Category.COLLECTION, "collection_range"),
        COLLECTION_SPEED(Category.COLLECTION, "collection_speed");

        private final Category category;
        private final String key;

        Skill(Category category, String key) {
            this.category = category;
            this.key = key;
        }

        public Category getCategory() {
            return category;
        }

        public String getKey() {
            return key;
        }
    }

    private final Map<Category, Integer> levels = new HashMap<>();
    private final Map<Category, Integer> xp = new HashMap<>();
    private final Map<Skill, Integer> skillLevels = new HashMap<>();

    public CompanionSkills() {
        for (Category cat : Category.values()) {
            levels.put(cat, 1);
            xp.put(cat, 0);
        }
        for (Skill skill : Skill.values()) {
            skillLevels.put(skill, 1);
        }
    }

    public int getLevel(Category category) {
        return levels.getOrDefault(category, 1);
    }

    public int getXP(Category category) {
        return xp.getOrDefault(category, 0);
    }

    public int getSkillLevel(Skill skill) {
        return skillLevels.getOrDefault(skill, 1);
    }

    public void addXP(Category category, int amount) {
        if (amount <= 0) return;
        int cap = ModConfig.COMMON.skillLevelCap.get();
        int currentLevel = getLevel(category);
        if (currentLevel >= cap) return;

        int newXP = getXP(category) + amount;
        int xpNeeded = xpForNextLevel(currentLevel);

        while (newXP >= xpNeeded && currentLevel < cap) {
            newXP -= xpNeeded;
            currentLevel++;
            levels.put(category, currentLevel);
            updateSkillLevels(category, currentLevel);
            xpNeeded = xpForNextLevel(currentLevel);
        }
        xp.put(category, newXP);
    }

    private void updateSkillLevels(Category category, int newLevel) {
        for (Skill skill : Skill.values()) {
            if (skill.getCategory() == category) {
                int targetLevel = 1 + (newLevel - 1) / 10;
                if (targetLevel > 5) targetLevel = 5;
                skillLevels.put(skill, targetLevel);
            }
        }
    }

    private int xpForNextLevel(int currentLevel) {
        return (int) (100 * Math.pow(1.5, currentLevel - 1));
    }

    public double getCombatDamageBonus() {
        int level = getSkillLevel(Skill.COMBAT_POWER);
        return 1.0D + (level - 1) * 0.15D;
    }

    public double getMovementSpeedBonus() {
        int level = getSkillLevel(Skill.COMBAT_REFLEXES);
        return 1.0D + (level - 1) * 0.05D;
    }

    public double getMiningSpeedBonus() {
        int level = getSkillLevel(Skill.MINING_SPEED);
        return 1.0D + (level - 1) * 0.2D;
    }

    public int getExtraMiningLoot() {
        int level = getSkillLevel(Skill.MINING_LOOT);
        return level - 1;
    }

    public double getArmorBonus() {
        int level = getSkillLevel(Skill.SURVIVAL_ARMOR);
        return (level - 1) * 2.0D;
    }

    public int getUnderwaterBreathTicks() {
        int level = getSkillLevel(Skill.SURVIVAL_BREATH);
        return 100 + (level - 1) * 100;
    }

    public double getCollectionRangeBonus() {
        int level = getSkillLevel(Skill.COLLECTION_RANGE);
        return 1.0D + (level - 1) * 0.5D;
    }

    public int getCollectionSpeed() {
        int level = getSkillLevel(Skill.COLLECTION_SPEED);
        return level;
    }

    public void readFromNBT(CompoundTag tag) {
        for (Category cat : Category.values()) {
            String prefix = cat.getKey();
            if (tag.contains(prefix + "Level")) {
                levels.put(cat, tag.getInt(prefix + "Level"));
            }
            if (tag.contains(prefix + "XP")) {
                xp.put(cat, tag.getInt(prefix + "XP"));
            }
        }
        for (Skill skill : Skill.values()) {
            String key = skill.getKey();
            if (tag.contains(key + "Level")) {
                skillLevels.put(skill, tag.getInt(key + "Level"));
            }
        }
    }

    public void writeToNBT(CompoundTag tag) {
        for (Category cat : Category.values()) {
            String prefix = cat.getKey();
            tag.putInt(prefix + "Level", levels.getOrDefault(cat, 1));
            tag.putInt(prefix + "XP", xp.getOrDefault(cat, 0));
        }
        for (Skill skill : Skill.values()) {
            tag.putInt(skill.getKey() + "Level", skillLevels.getOrDefault(skill, 1));
        }
    }
}
