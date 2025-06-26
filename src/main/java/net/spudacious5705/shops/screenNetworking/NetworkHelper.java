package net.spudacious5705.shops.screenNetworking;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.spudacious5705.shops.screen.ShopScreenHandlerOwner;

public class NetworkHelper {

    public static void initialise(){

        PayloadTypeRegistry.playC2S().register(TabSyncPayload.ID,TabSyncPayload.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(TabSyncPayload.ID,TabSyncPayload.PACKET_CODEC);

        PayloadTypeRegistry.playC2S().register(SelfDemotePayload.ID,SelfDemotePayload.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(ShopScreenPayload.ID,ShopScreenPayload.PACKET_CODEC);


        ServerPlayNetworking.registerGlobalReceiver(TabSyncPayload.ID, (payload, context) -> {
            if(context.player().currentScreenHandler instanceof ShopScreenHandlerOwner screenHandler){
                int tab = payload.screenID();
                screenHandler.updateTabSelectionServerside(tab); // Sync the tab on the server

                //update client
                PacketByteBuf responseBuf = PacketByteBufs.create();
                responseBuf.writeInt(tab);
                ServerPlayNetworking.send(context.player(), new TabSyncPayload(tab));
            }
        });
        ClientPlayNetworking.registerGlobalReceiver(TabSyncPayload.ID, (payload, context) -> {
            if (context.player().currentScreenHandler instanceof ShopScreenHandlerOwner screenHandler) {
                screenHandler.updateTabSelectionResponse(payload.screenID()); // Update UI on client
            }
        });

        ServerPlayNetworking.registerGlobalReceiver(SelfDemotePayload.ID, (payload, context) -> {
            ServerPlayerEntity player = context.player();
            if(player.currentScreenHandler instanceof ShopScreenHandlerOwner screenHandler){
                screenHandler.selfDemotePlayer(player);
                player.closeHandledScreen();
            }
        });


    }

    /*@Environment(EnvType.CLIENT)
    public static void initialiseCLIENT(){


        ClientPlayNetworking.registerGlobalReceiver(TabSyncResponsePayload.ID,
                (payload, context) -> {
                    if (context.player().currentScreenHandler instanceof ShopScreenHandlerOwner screenHandler) {
                        screenHandler.updateTabSelectionResponse(payload.screenID()); // Update UI on client
                    }
                });
    }*/


}
