package com.kilo.companionmod.config;

import com.kilo.companionmod.CompanionMod;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;

public class ModConfig {
    public static final ForgeConfigSpec SPEC;
    public static final Common COMMON;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        COMMON = new Common(builder);
        SPEC = builder.build();
    }

    public static class Common {
        public final ForgeConfigSpec.IntValue followDistance;
        public final ForgeConfigSpec.IntValue maxHealth;
        public final ForgeConfigSpec.DoubleValue movementSpeed;
        public final ForgeConfigSpec.IntValue attackDamage;
        public final ForgeConfigSpec.IntValue teleportDistance;
        public final ForgeConfigSpec.IntValue pathFailThreshold;
        public final ForgeConfigSpec.IntValue loyaltyDecayThreshold;
        public final ForgeConfigSpec.BooleanValue soundsEnabled;
        public final ForgeConfigSpec.IntValue itemPickupRadius;
        public final ForgeConfigSpec.IntValue skillLevelCap;
        public final ForgeConfigSpec.IntValue respawnDelaySeconds;

        Common(ForgeConfigSpec.Builder builder) {
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
        ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON, SPEC, "companionmod-common.toml");
    }
}
