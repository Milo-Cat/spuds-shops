package net.spudacious5705.shops.screen.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.spudacious5705.shops.screen.ToggleButtonID;
import org.jetbrains.annotations.NotNull;

import static net.spudacious5705.shops.SpudaciousShops.id;

public record ToggleSyncPkt(ToggleButtonID buttonID, boolean state) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ToggleSyncPkt> TYPE = new CustomPacketPayload.Type<>(id("togglable_sync"));

    static final StreamCodec<ByteBuf, ToggleButtonID> ENUM_CODEC = new StreamCodec<>() {
        public @NotNull ToggleButtonID decode(@NotNull ByteBuf buf) {
            return ToggleButtonID.values()[VarInt.read(buf)];
        }

        public void encode(@NotNull ByteBuf buf, ToggleButtonID button) {
            VarInt.write(buf, button.ordinal());
        }
    };

    public static final StreamCodec<ByteBuf, ToggleSyncPkt> STREAM_CODEC = StreamCodec.composite(
            ENUM_CODEC,
            ToggleSyncPkt::buttonID,
            ByteBufCodecs.BOOL,
            ToggleSyncPkt::state,
            ToggleSyncPkt::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
