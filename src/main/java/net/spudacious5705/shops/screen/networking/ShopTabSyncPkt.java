package net.spudacious5705.shops.screen.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import static net.spudacious5705.shops.SpudaciousShops.id;

public record ShopTabSyncPkt(int tab) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ShopTabSyncPkt> TYPE = new CustomPacketPayload.Type<>(id("shop_tab_sync"));

    public static final StreamCodec<ByteBuf, ShopTabSyncPkt> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            ShopTabSyncPkt::tab,
            ShopTabSyncPkt::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
