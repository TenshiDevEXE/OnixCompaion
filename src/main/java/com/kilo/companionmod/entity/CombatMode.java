package com.kilo.companionmod.entity;

public enum CombatMode {
    PASSIVE("companionmod.combat.passive"),
    DEFENSIVE("companionmod.combat.defensive"),
    AGGRESSIVE("companionmod.combat.aggressive");

    private final String langKey;

    CombatMode(String langKey) {
        this.langKey = langKey;
    }

    public String getLangKey() {
        return langKey;
    }
}
