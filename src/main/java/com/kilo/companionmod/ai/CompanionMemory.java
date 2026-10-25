package com.kilo.companionmod.ai;

import com.kilo.companionmod.CompanionMod;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.biome.Biome;
import java.util.HashMap;
import java.util.Map;

public class CompanionMemory {
    private final Map<String, Integer> zoneVisitCount = new HashMap<>();
    private final Map<String, Long> zoneLastVisit = new HashMap<>();
    private final Map<String, Integer> biomePreference = new HashMap<>();
    private BlockPos homePosition;
    private int dangerZoneRadius;
    private long lastDangerTime;
    private int totalTicksAlive;
    private int ticksInCombat;
    private int ticksTravelled;

    public CompanionMemory() {
        this.homePosition = null;
        this.dangerZoneRadius = 16;
        this.lastDangerTime = 0;
        this.totalTicksAlive = 0;
        this.ticksInCombat = 0;
        this.ticksTravelled = 0;
    }

    public void recordZoneVisit(BlockPos pos) {
        String zoneKey = zoneKey(pos);
        zoneVisitCount.merge(zoneKey, 1, Integer::sum);
        zoneLastVisit.put(zoneKey, System.currentTimeMillis());
    }

    public int getZoneVisitCount(BlockPos pos) {
        return zoneVisitCount.getOrDefault(zoneKey(pos), 0);
    }

    public void recordBiomePreference(Biome biome, int value) {
        String biomeName = biome.toString();
        biomePreference.merge(biomeName, value, Integer::sum);
    }

    public int getBiomePreference(Biome biome) {
        return biomePreference.getOrDefault(biome.toString(), 0);
    }

    public void setHomePosition(BlockPos pos) {
        this.homePosition = pos;
    }

    public BlockPos getHomePosition() {
        return homePosition;
    }

    public boolean hasHomePosition() {
        return homePosition != null;
    }

    public void recordDanger() {
        this.lastDangerTime = System.currentTimeMillis();
    }

    public boolean isRecentlyDangerous(int seconds) {
        return System.currentTimeMillis() - lastDangerTime < seconds * 1000L;
    }

    public int getDangerZoneRadius() {
        return dangerZoneRadius;
    }

    public void setDangerZoneRadius(int radius) {
        this.dangerZoneRadius = radius;
    }

    public void tick() {
        totalTicksAlive++;
    }

    public void recordCombatTick() {
        ticksInCombat++;
    }

    public void recordTravelTick() {
        ticksTravelled++;
    }

    public int getTotalTicksAlive() {
        return totalTicksAlive;
    }

    public int getTicksInCombat() {
        return ticksInCombat;
    }

    public int getTicksTravelled() {
        return ticksTravelled;
    }

    private String zoneKey(BlockPos pos) {
        int zoneX = pos.getX() / 16;
        int zoneZ = pos.getZ() / 16;
        return zoneX + "," + zoneZ;
    }

    public CompoundTag saveToTag() {
        CompoundTag tag = new CompoundTag();
        CompoundTag zonesTag = new CompoundTag();
        for (Map.Entry<String, Integer> entry : zoneVisitCount.entrySet()) {
            zonesTag.putInt(entry.getKey(), entry.getValue());
        }
        tag.put("ZoneVisits", zonesTag);

        CompoundTag biomesTag = new CompoundTag();
        for (Map.Entry<String, Integer> entry : biomePreference.entrySet()) {
            biomesTag.putInt(entry.getKey(), entry.getValue());
        }
        tag.put("BiomePreferences", biomesTag);

        if (homePosition != null) {
            CompoundTag homeTag = new CompoundTag();
            homeTag.putInt("X", homePosition.getX());
            homeTag.putInt("Y", homePosition.getY());
            homeTag.putInt("Z", homePosition.getZ());
            tag.put("Home", homeTag);
        }

        tag.putInt("DangerRadius", dangerZoneRadius);
        tag.putLong("LastDanger", lastDangerTime);
        tag.putInt("TotalTicks", totalTicksAlive);
        tag.putInt("CombatTicks", ticksInCombat);
        tag.putInt("TravelTicks", ticksTravelled);
        return tag;
    }

    public void loadFromTag(CompoundTag tag) {
        if (tag.contains("ZoneVisits")) {
            CompoundTag zonesTag = tag.getCompound("ZoneVisits");
            for (String key : zonesTag.getAllKeys()) {
                zoneVisitCount.put(key, zonesTag.getInt(key));
            }
        }
        if (tag.contains("BiomePreferences")) {
            CompoundTag biomesTag = tag.getCompound("BiomePreferences");
            for (String key : biomesTag.getAllKeys()) {
                biomePreference.put(key, biomesTag.getInt(key));
            }
        }
        if (tag.contains("Home")) {
            CompoundTag homeTag = tag.getCompound("Home");
            homePosition = new BlockPos(homeTag.getInt("X"), homeTag.getInt("Y"), homeTag.getInt("Z"));
        }
        dangerZoneRadius = tag.getInt("DangerRadius");
        lastDangerTime = tag.getLong("LastDanger");
        totalTicksAlive = tag.getInt("TotalTicks");
        ticksInCombat = tag.getInt("CombatTicks");
        ticksTravelled = tag.getInt("TravelTicks");
    }
}
