package com.kilo.companionmod.entity;

public enum CompanionOrder {
    FOLLOW("companionmod.order.follow"),
    STAY("companionmod.order.stay"),
    WANDER("companionmod.order.wander");

    private final String langKey;

    CompanionOrder(String langKey) {
        this.langKey = langKey;
    }

    public String getLangKey() {
        return langKey;
    }
}
