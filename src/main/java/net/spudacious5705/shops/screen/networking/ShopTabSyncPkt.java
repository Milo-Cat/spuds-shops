package net.spudacious5705.shops.screen.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.spudacious5705.shops.screen.ShopScreenHandlerOwner;

import java.util.function.Supplier;

public record ShopTabSyncPkt(int tab) {

    public void encode(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeInt(tab);
    }

    public static ShopTabSyncPkt decode(FriendlyByteBuf friendlyByteBuf) {
        return new ShopTabSyncPkt(
                friendlyByteBuf.readInt()
        );
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null && player.containerMenu instanceof ShopScreenHandlerOwner screenHandler) {
                screenHandler.updateTabSelectionServerside(tab);

                // Send response back to client
                NetworkHelper.CHANNEL.send(
                        PacketDistributor.PLAYER.with(() -> player),
                        new ShopTabSyncResponsePkt(tab)
                );
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
