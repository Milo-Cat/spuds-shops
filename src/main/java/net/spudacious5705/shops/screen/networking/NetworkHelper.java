package net.spudacious5705.shops.screen.networking;


import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.spudacious5705.shops.SpudaciousShops;

public class NetworkHelper {

    private static final String PROTOCOL_VERSION = "1.0";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            SpudaciousShops.getResource("main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    public static void register() {
        CHANNEL.registerMessage(packetId++, ShopTabSyncPkt.class,
                ShopTabSyncPkt::encode,
                ShopTabSyncPkt::decode,
                ShopTabSyncPkt::handle);

        CHANNEL.registerMessage(packetId++, ShopSelfDemotePkt.class,
                ShopSelfDemotePkt::encode,
                ShopSelfDemotePkt::decode,
                ShopSelfDemotePkt::handle);

        CHANNEL.registerMessage(packetId++, ToggleSyncPkt.class,
                ToggleSyncPkt::encode,
                ToggleSyncPkt::decode,
                ToggleSyncPkt::handle);

        CHANNEL.registerMessage(packetId++, ToggleSyncResponsePkt.class,
                ToggleSyncResponsePkt::encode,
                ToggleSyncResponsePkt::decode,
                ToggleSyncResponsePkt::handle);

        CHANNEL.registerMessage(packetId++, ShopTabSyncResponsePkt.class,
                ShopTabSyncResponsePkt::encode,
                ShopTabSyncResponsePkt::decode,
                ShopTabSyncResponsePkt::handle);
    }


}
