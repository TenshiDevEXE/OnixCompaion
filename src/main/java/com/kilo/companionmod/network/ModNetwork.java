package com.kilo.companionmod.network;

import com.kilo.companionmod.CompanionMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

public class ModNetwork {
    public static final StreamCodec<RegistryFriendlyByteBuf, CompanionOrderPacket> COMPANION_ORDER_CODEC =
        new StreamCodec<RegistryFriendlyByteBuf, CompanionOrderPacket>() {
            @Override
            public void encode(RegistryFriendlyByteBuf buf, CompanionOrderPacket packet) {
                buf.writeInt(packet.getEntityId());
                buf.writeInt(packet.getOrder());
                buf.writeInt(packet.getCombatMode());
            }

            @Override
            public CompanionOrderPacket decode(RegistryFriendlyByteBuf buf) {
                return new CompanionOrderPacket(buf.readInt(), buf.readInt(), buf.readInt());
            }
        };

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(ModNetwork::onRegisterPayloadHandlers);
    }

    private static void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
        event.registrar(CompanionOrderPacket.ID.toString())
            .playBidirectional(
                CompanionOrderPacket.TYPE,
                COMPANION_ORDER_CODEC,
                (IPayloadHandler<CompanionOrderPacket>) CompanionOrderPacket::handle
            );
    }
}
