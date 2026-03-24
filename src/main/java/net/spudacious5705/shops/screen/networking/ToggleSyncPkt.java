package net.spudacious5705.shops.screen.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.VarInt;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.spudacious5705.shops.screen.ToggleButtonID;
import net.spudacious5705.shops.screen.owner_screen.ShopScreenHandlerOwner;
import net.spudacious5705.shops.screen.owner_screen.ShopScreenOwner;
import org.jetbrains.annotations.NotNull;

import static net.spudacious5705.shops.SpudaciousShops.id;

public record ToggleSyncPkt(ToggleButtonID buttonID, boolean state) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ToggleSyncPkt> TYPE = new CustomPacketPayload.Type<>(id("togglable_sync"));
    static StreamCodec<ByteBuf, ToggleButtonID> ENUM_CODEC = new StreamCodec<>() {
        public @NotNull ToggleButtonID decode(@NotNull ByteBuf buf) {
            return (ToggleButtonID.values())[VarInt.read(buf)];
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

    public void handleServerSide(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (
                    ctx.player() instanceof ServerPlayer player
                            &&
                            player.containerMenu instanceof ShopScreenHandlerOwner screenHandler
            ) {
                boolean response = screenHandler.toggleButtonServersideUpdate(buttonID, state);

                // Send response back to client
                PacketDistributor.sendToPlayer(player,
                        new ToggleSyncPkt(buttonID, response)
                );
            }
        });
    }

    public void handleClientSide(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.screen instanceof ShopScreenOwner screen) {
                screen.getMenu().updateToggleButtonFromPacket(buttonID, state);
            }
        });
    }
}
