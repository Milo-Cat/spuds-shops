package net.spudacious5705.shops.screen.networking;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;
import net.spudacious5705.shops.SpudaciousShops;

public record ShopScreenPayload(BlockPos pos) implements CustomPayload {
    public static final Id<ShopScreenPayload> ID = new Id<>(SpudaciousShops.id("shop_screen_payload"));

    public static final PacketCodec<RegistryByteBuf, ShopScreenPayload> PACKET_CODEC =
            PacketCodec.tuple(BlockPos.PACKET_CODEC, ShopScreenPayload::pos, ShopScreenPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}


