package com.kilo.companionmod.entity;

public enum CompanionOrder {
    FOLLOW("companionmod.order.follow"),
    STAY("companionmod.order.stay"),
    WANDER("companionmod.order.wander"),
    PATROL("companionmod.order.patrol"),
    GUARD("companionmod.order.guard"),
    GATHER("companionmod.order.gather"),
    MINE("companionmod.order.mine"),
    BUILD("companionmod.order.build");

    private final String langKey;

    CompanionOrder(String langKey) {
        this.langKey = langKey;
    }

    public String getLangKey() {
        return langKey;
    }
}
