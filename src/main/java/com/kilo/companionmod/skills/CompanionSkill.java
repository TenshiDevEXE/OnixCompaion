package com.kilo.companionmod.skills;

import com.kilo.companionmod.CompanionMod;
import net.minecraft.nbt.CompoundTag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CompanionSkill {
    private static final Logger LOGGER = LogManager.getLogger();
    private final CompanionSkillType type;
    private int level;
    private long experience;
    private long experienceToNextLevel;

    public CompanionSkill(CompanionSkillType type) {
        this.type = type;
        this.level = 1;
        this.experience = 0;
        this.experienceToNextLevel = calculateXpForLevel(2);
    }

    public CompanionSkillType getType() {
        return type;
    }

    public int getLevel() {
        return level;
    }

    public long getExperience() {
        return experience;
    }

    public long getExperienceToNextLevel() {
        return experienceToNextLevel;
    }

    public float getProgress() {
        if (experienceToNextLevel <= 0) return 1.0F;
        return Math.min(1.0F, (float) experience / (float) experienceToNextLevel);
    }

    public void addExperience(long amount) {
        if (amount <= 0) return;
        experience += amount;
        CompanionMod.LOGGER.debug("Companion skill {} gained {} XP (total: {})", type.displayName, amount, experience);

        while (experience >= experienceToNextLevel && level < 100) {
            experience -= experienceToNextLevel;
            level++;
            experienceToNextLevel = calculateXpForLevel(level + 1);
            CompanionMod.LOGGER.info("Companion skill {} leveled up to {}", type.displayName, level);
        }
    }

    public void setLevel(int level) {
        this.level = Math.max(1, Math.min(100, level));
        this.experienceToNextLevel = calculateXpForLevel(level + 1);
    }

    private long calculateXpForLevel(int targetLevel) {
        if (targetLevel <= 1) return 0;
        return (long) (100.0 * Math.pow(1.5, targetLevel - 2));
    }

    public CompoundTag saveToTag() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Type", type.name());
        tag.putInt("Level", level);
        tag.putLong("Experience", experience);
        return tag;
    }

    public static CompanionSkill loadFromTag(CompoundTag tag) {
        CompanionSkillType type = CompanionSkillType.valueOf(tag.getString("Type"));
        CompanionSkill skill = new CompanionSkill(type);
        skill.level = tag.getInt("Level");
        skill.experience = tag.getLong("Experience");
        skill.experienceToNextLevel = skill.calculateXpForLevel(skill.level + 1);
        return skill;
    }
}
