package net.spudacious5705.shops.screen.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.spudacious5705.shops.screen.ShopScreenHandlerOwner;
import net.spudacious5705.shops.screen.ToggleButtonID;

import java.util.function.Supplier;

public record ToggleSyncPkt(ToggleButtonID buttonID, boolean state) {
    public void encode(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf
                .writeEnum(buttonID)
                .writeBoolean(state);
    }

    public static ToggleSyncPkt decode(FriendlyByteBuf friendlyByteBuf) {
        return new ToggleSyncPkt(
                friendlyByteBuf.readEnum(ToggleButtonID.class),
                friendlyByteBuf.readBoolean()
        );
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null && player.containerMenu instanceof ShopScreenHandlerOwner screenHandler) {
                boolean response = screenHandler.toggleButtonServersideUpdate(buttonID,state);

                // Send response back to client
                NetworkHelper.CHANNEL.send(
                        PacketDistributor.PLAYER.with(() -> player),
                        new ToggleSyncResponsePkt(buttonID, response)
                );
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
