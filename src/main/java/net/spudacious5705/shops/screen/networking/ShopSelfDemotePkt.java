package net.spudacious5705.shops.screen.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.spudacious5705.shops.screen.ShopScreenHandlerOwner;

import java.util.function.Supplier;

public record ShopSelfDemotePkt() {
    public void encode(FriendlyByteBuf friendlyByteBuf) {
    }

    public static ShopSelfDemotePkt decode(FriendlyByteBuf friendlyByteBuf) {
        return new ShopSelfDemotePkt();
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null && player.containerMenu instanceof ShopScreenHandlerOwner screenHandler) {
                screenHandler.selfDemotePlayer(player);
                player.doCloseContainer();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
