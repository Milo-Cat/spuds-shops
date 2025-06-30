package net.spudacious5705.shops.screenNetworking;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.spudacious5705.shops.SpudaciousShops;

public record SelfDemotePayload(int useless) implements CustomPayload {
    public static final Id<SelfDemotePayload> ID = new Id<>(SpudaciousShops.id("shop_self_demote"));

    public static final PacketCodec<RegistryByteBuf, SelfDemotePayload> PACKET_CODEC =
            PacketCodec.tuple(PacketCodecs.INTEGER, SelfDemotePayload::useless, SelfDemotePayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}


