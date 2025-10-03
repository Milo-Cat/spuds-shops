package net.spudacious5705.shops.screenNetworking;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.spudacious5705.shops.SpudaciousShops;

public record ToggleSyncPayload(String tabID, boolean state) implements CustomPayload {
    public static final Id<ToggleSyncPayload> ID = new Id<>(SpudaciousShops.id("toggle_sync"));

    public static final PacketCodec<RegistryByteBuf, ToggleSyncPayload> PACKET_CODEC =
            PacketCodec.tuple(PacketCodecs.string(4), ToggleSyncPayload::tabID,
                    PacketCodecs.BOOL,  ToggleSyncPayload::state,
                    ToggleSyncPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    @Override
    public String tabID() {
        return tabID;
    }

    public enum ToggleButtonID {
        CreativeToggle("Crea"),
        ShopStyleToggle("Styl"),
        IgnoreNBTToggle("iNBT"),
        EffectsToggle("Efex");

        private final String serialised;

        ToggleButtonID(String s) {
            this.serialised = s;
        }

        public String getSerialised(){
            return serialised;
        }

        public static ToggleButtonID fromString(String s) {
            for (ToggleButtonID id : ToggleButtonID.values()) {
                if (id.getSerialised().equalsIgnoreCase(s)) {
                    return id;
                }
            }
            throw new IllegalArgumentException("Unknown ToggleButtonID: " + s);
        }
    }
}


