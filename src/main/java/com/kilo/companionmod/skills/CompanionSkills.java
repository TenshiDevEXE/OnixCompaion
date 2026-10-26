package com.kilo.companionmod.skills;

import com.kilo.companionmod.CompanionMod;
import net.minecraft.nbt.CompoundTag;
import java.util.EnumMap;
import java.util.Map;

public class CompanionSkills {
    private final Map<CompanionSkillType, CompanionSkill> skills = new EnumMap<>(CompanionSkillType.class);

    public CompanionSkills() {
        for (CompanionSkillType type : CompanionSkillType.values()) {
            skills.put(type, new CompanionSkill(type));
        }
    }

    public CompanionSkill getSkill(CompanionSkillType type) {
        return skills.get(type);
    }

    public int getLevel(CompanionSkillType type) {
        CompanionSkill skill = skills.get(type);
        return skill != null ? skill.getLevel() : 1;
    }

    public void addExperience(CompanionSkillType type, long amount) {
        CompanionSkill skill = skills.get(type);
        if (skill != null) {
            skill.addExperience(amount);
            CompanionMod.LOGGER.debug("Companion {} skill gained {} XP", type.displayName, amount);
        }
    }

    public float getProgress(CompanionSkillType type) {
        CompanionSkill skill = skills.get(type);
        return skill != null ? skill.getProgress() : 0.0F;
    }

    public int getTotalLevel() {
        return skills.values().stream().mapToInt(CompanionSkill::getLevel).sum();
    }

    public float getMovementSpeedBonus() {
        return 1.0F + (getLevel(CompanionSkillType.EXPLORATION) - 1) * 0.02F;
    }

    public float getCombatDamageBonus() {
        return 1.0F + (getLevel(CompanionSkillType.COMBAT) - 1) * 0.05F;
    }

    public float getMiningSpeedBonus() {
        return 1.0F + (getLevel(CompanionSkillType.MINING) - 1) * 0.03F;
    }

    public int getExtraMiningLoot() {
        return Math.max(0, (getLevel(CompanionSkillType.MINING) - 1) / 5);
    }

    public float getArmorBonus() {
        return (getLevel(CompanionSkillType.TANKING) - 1) * 0.5F;
    }

    public int getUnderwaterBreathTicks() {
        return 300 + (getLevel(CompanionSkillType.SURVIVAL) - 1) * 50;
    }

    public double getCollectionRangeBonus() {
        return 1.0D + (getLevel(CompanionSkillType.GATHERING) - 1) * 0.1D;
    }

    public int getCollectionSpeed() {
        return 1 + (getLevel(CompanionSkillType.GATHERING) - 1) / 3;
    }

    public CompoundTag saveToTag() {
        CompoundTag tag = new CompoundTag();
        for (Map.Entry<CompanionSkillType, CompanionSkill> entry : skills.entrySet()) {
            tag.put(entry.getKey().name(), entry.getValue().saveToTag());
        }
        return tag;
    }

    public void loadFromTag(CompoundTag tag) {
        for (String key : tag.getAllKeys()) {
            try {
                CompanionSkillType type = CompanionSkillType.valueOf(key);
                CompanionSkill skill = CompanionSkill.loadFromTag(tag.getCompound(key));
                skills.put(type, skill);
            } catch (IllegalArgumentException e) {
                CompanionMod.LOGGER.warn("Unknown skill type: {}", key);
            }
        }
    }
}
