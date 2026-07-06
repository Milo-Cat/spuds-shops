package net.spudacious5705.shops.screen;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ShopOpenData(BlockPos pos, boolean openTop) {
    public static final StreamCodec<ByteBuf, ShopOpenData> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            ShopOpenData::pos,
            ByteBufCodecs.BOOL,
            ShopOpenData::openTop,
            ShopOpenData::new
    );
}
