package com.kilo.companionmod.network;

import com.kilo.companionmod.CompanionMod;
import com.kilo.companionmod.entity.CombatMode;
import com.kilo.companionmod.entity.CompanionEntity;
import com.kilo.companionmod.entity.CompanionOrder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class CompanionOrderPacket implements CustomPacketPayload {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(CompanionMod.MOD_ID, "order");
    public static final Type<CompanionOrderPacket> TYPE = new Type<>(ID);

    private final int entityId;
    private final int order;
    private final int combatMode;

    public CompanionOrderPacket(int entityId, int order, int combatMode) {
        this.entityId = entityId;
        this.order = order;
        this.combatMode = combatMode;
    }

    @Override
    public Type<CompanionOrderPacket> type() {
        return TYPE;
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeInt(order);
        buf.writeInt(combatMode);
    }

    public static CompanionOrderPacket decode(RegistryFriendlyByteBuf buf) {
        return new CompanionOrderPacket(buf.readInt(), buf.readInt(), buf.readInt());
    }

    public static void handle(CompanionOrderPacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            var player = ctx.player();
            if (!(player instanceof ServerPlayer serverPlayer)) return;
            Entity entity = serverPlayer.level().getEntity(msg.entityId);
            if (!(entity instanceof CompanionEntity companion)) return;

            if (msg.order >= 0 && msg.order < CompanionOrder.values().length) {
                companion.setOrder(CompanionOrder.values()[msg.order]);
            }
            if (msg.combatMode >= 0 && msg.combatMode < CombatMode.values().length) {
                companion.setCombatMode(CombatMode.values()[msg.combatMode]);
            }
        });
    }

    public int getEntityId() { return entityId; }
    public int getOrder() { return order; }
    public int getCombatMode() { return combatMode; }
}
