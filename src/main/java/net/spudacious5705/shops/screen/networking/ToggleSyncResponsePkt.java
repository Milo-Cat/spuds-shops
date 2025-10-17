package net.spudacious5705.shops.screen.networking;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.spudacious5705.shops.screen.ShopScreenOwner;
import net.spudacious5705.shops.screen.ToggleButtonID;

import java.util.function.Supplier;

public record ToggleSyncResponsePkt(ToggleButtonID buttonID, boolean state) {
    public void encode(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf
                .writeEnum(buttonID)
                .writeBoolean(state);
    }

    public static ToggleSyncResponsePkt decode(FriendlyByteBuf friendlyByteBuf) {
        return new ToggleSyncResponsePkt(
                friendlyByteBuf.readEnum(ToggleButtonID.class),
                friendlyByteBuf.readBoolean()
        );
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.screen instanceof ShopScreenOwner screen) {
                screen.getMenu().updateToggleButtonFromPacket(buttonID, state);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
