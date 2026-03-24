package net.spudacious5705.shops.screen.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.spudacious5705.shops.screen.owner_screen.ShopScreenHandlerOwner;
import net.spudacious5705.shops.screen.owner_screen.ShopScreenOwner;
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

    public void handleServerSide(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (
                    ctx.player() instanceof ServerPlayer player
                            &&
                            player.containerMenu instanceof ShopScreenHandlerOwner screenHandler
            ) {
                screenHandler.updateTabSelectionServerside(tab);

                PacketDistributor.sendToPlayer(player, new ShopTabSyncPkt(tab));

            }
        });
    }

    public void handleClientSide(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.screen instanceof ShopScreenOwner screen) {
                screen.getMenu().updateTabSelectionResponse(tab);
            }
        });
    }
}
