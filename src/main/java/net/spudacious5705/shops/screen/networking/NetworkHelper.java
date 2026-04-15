package net.spudacious5705.shops.screen.networking;


import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import static net.spudacious5705.shops.SpudaciousShops.MOD_ID;

@EventBusSubscriber(modid = MOD_ID)
public class NetworkHelper {

    private static final String PROTOCOL_VERSION = "1.0";

    @SubscribeEvent // on the mod event bus
    public static void register(final RegisterPayloadHandlersEvent event) {
        PayloadRegistrar reg = event.registrar(PROTOCOL_VERSION);
        reg.playBidirectional(
                ShopTabSyncPkt.TYPE,
                ShopTabSyncPkt.STREAM_CODEC,
                ShopTabSyncPkt::handleClientSide,
                ShopTabSyncPkt::handleServerSide
        );

        reg.playBidirectional(
                ToggleSyncPkt.TYPE,
                ToggleSyncPkt.STREAM_CODEC,
                ToggleSyncPkt::handleClientSide,
                ToggleSyncPkt::handleServerSide
        );

        reg.playToServer(
                ShopSelfDemotePkt.TYPE,
                ShopSelfDemotePkt.STREAM_CODEC,
                ShopSelfDemotePkt::handle
        );

    }


}
