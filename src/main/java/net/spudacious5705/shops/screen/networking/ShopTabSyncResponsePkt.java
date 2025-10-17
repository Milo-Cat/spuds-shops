package net.spudacious5705.shops.screen.networking;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.spudacious5705.shops.screen.ShopScreenOwner;

import java.util.function.Supplier;

public record ShopTabSyncResponsePkt(int tab) {

    public void encode(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeInt(tab);
    }

    public static ShopTabSyncResponsePkt decode(FriendlyByteBuf friendlyByteBuf) {
        return new ShopTabSyncResponsePkt(
                friendlyByteBuf.readInt()
        );
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.screen instanceof ShopScreenOwner screen) {
                screen.updateTabSelectionResponse(tab);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
