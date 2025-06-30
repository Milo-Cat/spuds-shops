package net.spudacious5705.shops.screenNetworking;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.spudacious5705.shops.SpudaciousShops;

public record TabSyncPayload(int screenID) implements CustomPayload {
    public static final Id<TabSyncPayload> ID = new Id<>(SpudaciousShops.id("shop_tab_sync"));

    public static final PacketCodec<RegistryByteBuf, TabSyncPayload> PACKET_CODEC =
            PacketCodec.tuple(PacketCodecs.INTEGER, TabSyncPayload::screenID, TabSyncPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    @Override
    public int screenID() {
        return screenID;
    }
}


