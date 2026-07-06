package net.spudacious5705.shops.screen.networking;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.spudacious5705.shops.screen.owner_screen.ShopScreenHandlerOwner;
import net.spudacious5705.shops.screen.owner_screen.ShopScreenOwner;

public class NetworkHelper {

    public static void initialise() {
        PayloadTypeRegistry.playC2S().register(ShopTabSyncPkt.TYPE, ShopTabSyncPkt.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(ShopTabSyncPkt.TYPE, ShopTabSyncPkt.STREAM_CODEC);

        PayloadTypeRegistry.playC2S().register(ToggleSyncPkt.TYPE, ToggleSyncPkt.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(ToggleSyncPkt.TYPE, ToggleSyncPkt.STREAM_CODEC);

        PayloadTypeRegistry.playC2S().register(ShopSelfDemotePkt.TYPE, ShopSelfDemotePkt.STREAM_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(ShopTabSyncPkt.TYPE, (payload, context) ->
                context.server().execute(() -> {
                    ServerPlayer player = context.player();
                    if (player.containerMenu instanceof ShopScreenHandlerOwner screenHandler) {
                        screenHandler.updateTabSelectionServerside(payload.tab());
                        ServerPlayNetworking.send(player, new ShopTabSyncPkt(payload.tab()));
                    }
                }));

        ServerPlayNetworking.registerGlobalReceiver(ToggleSyncPkt.TYPE, (payload, context) ->
                context.server().execute(() -> {
                    ServerPlayer player = context.player();
                    if (player.containerMenu instanceof ShopScreenHandlerOwner screenHandler) {
                        boolean response = screenHandler.toggleButtonServersideUpdate(payload.buttonID(), payload.state());
                        ServerPlayNetworking.send(player, new ToggleSyncPkt(payload.buttonID(), response));
                    }
                }));

        ServerPlayNetworking.registerGlobalReceiver(ShopSelfDemotePkt.TYPE, (payload, context) ->
                context.server().execute(() -> {
                    ServerPlayer player = context.player();
                    if (player.containerMenu instanceof ShopScreenHandlerOwner screenHandler) {
                        screenHandler.selfDemotePlayer(player);
                        player.closeContainer();
                    }
                }));
    }

    @Environment(EnvType.CLIENT)
    public static void initialiseClient() {
        ClientPlayNetworking.registerGlobalReceiver(ShopTabSyncPkt.TYPE, (payload, context) ->
                context.client().execute(() -> {
                    if (Minecraft.getInstance().screen instanceof ShopScreenOwner screen) {
                        screen.getMenu().updateTabSelectionResponse(payload.tab());
                    }
                }));

        ClientPlayNetworking.registerGlobalReceiver(ToggleSyncPkt.TYPE, (payload, context) ->
                context.client().execute(() -> {
                    if (Minecraft.getInstance().screen instanceof ShopScreenOwner screen) {
                        screen.getMenu().updateToggleButtonFromPacket(payload.buttonID(), payload.state());
                    }
                }));
    }
}
