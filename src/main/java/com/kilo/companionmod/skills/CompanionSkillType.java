package com.kilo.companionmod.skills;

public enum CompanionSkillType {
    COMBAT("Combate", 0xFF4444),
    MINING("Minería", 0xAAAAAA),
    SURVIVAL("Supervivencia", 0x44AA44),
    GATHERING("Recolección", 0x4444AA),
    BUILDING("Construcción", 0xAA44AA),
    ARCHERY("Arquería", 0xAA8844),
    TANKING("Tanque", 0x884444),
    HEALING("Sanación", 0x44AA88),
    EXPLORATION("Exploración", 0x8844AA),
    STEALTH("Sigilo", 0x448844);

    public final String displayName;
    public final int color;

    CompanionSkillType(String displayName, int color) {
        this.displayName = displayName;
        this.color = color;
    }
}
