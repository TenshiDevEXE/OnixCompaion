package com.kilo.companionmod.network;

import com.kilo.companionmod.CompanionMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetwork {
    private static SimpleChannel INSTANCE;

    public static void register() {
        INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(CompanionMod.MOD_ID, "network"),
            () -> "1.0",
            s -> true,
            s -> true
        );
    }

    public static SimpleChannel get() {
        return INSTANCE;
    }
}
