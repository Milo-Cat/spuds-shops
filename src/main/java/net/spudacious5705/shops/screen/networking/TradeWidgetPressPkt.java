package net.spudacious5705.shops.screen.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.spudacious5705.shops.screen.ToggleButtonID;
import net.spudacious5705.shops.screen.owner_screen.ShopScreenHandlerOwner;

import java.util.function.Supplier;

public record TradeWidgetPressPkt(int slotId, int button) {
    public void encode(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf
                .writeInt(slotId)
                .writeInt(button);
    }

    public static TradeWidgetPressPkt decode(FriendlyByteBuf friendlyByteBuf) {
        return new TradeWidgetPressPkt(
                friendlyByteBuf.readInt(),
                friendlyByteBuf.readInt()
        );
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null && player.containerMenu instanceof ShopScreenHandlerOwner screenHandler) {
                screenHandler.tradeWindowPressServer(slotId,button);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
