package com.kilo.companionmod.config;

import com.kilo.companionmod.CompanionMod;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ModConfig {
    public static final ModConfigSpec SPEC;
    public static final Common COMMON;

    static {
        Pair<Common, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Common::new);
        COMMON = specPair.getLeft();
        SPEC = specPair.getRight();
    }

    public static class Common {
        public final ModConfigSpec.IntValue followDistance;
        public final ModConfigSpec.IntValue maxHealth;
        public final ModConfigSpec.DoubleValue movementSpeed;
        public final ModConfigSpec.IntValue attackDamage;
        public final ModConfigSpec.IntValue teleportDistance;
        public final ModConfigSpec.IntValue pathFailThreshold;
        public final ModConfigSpec.IntValue loyaltyDecayThreshold;
        public final ModConfigSpec.BooleanValue soundsEnabled;
        public final ModConfigSpec.IntValue itemPickupRadius;
        public final ModConfigSpec.IntValue skillLevelCap;
        public final ModConfigSpec.IntValue respawnDelaySeconds;

        Common(ModConfigSpec.Builder builder) {
            builder.push("companion");
            followDistance = builder.defineInRange("followDistance", 64, 0, 1024);
            maxHealth = builder.defineInRange("maxHealth", 20, 1, 500);
            movementSpeed = builder.defineInRange("movementSpeed", 0.35D, 0.1D, 2.0D);
            attackDamage = builder.defineInRange("attackDamage", 4, 0, 50);
            teleportDistance = builder.defineInRange("teleportDistance", 64, 0, 1024);
            pathFailThreshold = builder.defineInRange("pathFailThreshold", 3, 0, 20);
            loyaltyDecayThreshold = builder.defineInRange("loyaltyDecayThreshold", 100, 0, 100);
            soundsEnabled = builder.define("soundsEnabled", true);
            itemPickupRadius = builder.defineInRange("itemPickupRadius", 8, 0, 64);
            skillLevelCap = builder.defineInRange("skillLevelCap", 50, 1, 200);
            respawnDelaySeconds = builder.defineInRange("respawnDelaySeconds", 30, 0, 300);
            builder.pop();
        }
    }

    public static void register() {
    }
}