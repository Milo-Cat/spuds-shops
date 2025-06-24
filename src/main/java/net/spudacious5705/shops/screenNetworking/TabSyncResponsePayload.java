package net.spudacious5705.shops.screenNetworking;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.spudacious5705.shops.SpudaciousShops;

public record TabSyncResponsePayload(int screenID) implements CustomPayload {
    public static final Id<TabSyncResponsePayload> ID = new Id<>(SpudaciousShops.id("shop_tab_sync_response"));

    public static final PacketCodec<RegistryByteBuf, TabSyncResponsePayload> PACKET_CODEC =
            PacketCodec.tuple(PacketCodecs.INTEGER, TabSyncResponsePayload::screenID, TabSyncResponsePayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    @Override
    public int screenID() {
        return screenID;
    }
}


