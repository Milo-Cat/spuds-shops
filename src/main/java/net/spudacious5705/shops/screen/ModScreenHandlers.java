package net.spudacious5705.shops.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.spudacious5705.shops.SpudaciousShops;
import net.spudacious5705.shops.screenNetworking.ShopScreenPayload;

import java.util.HashMap;
import java.util.Map;

public class ModScreenHandlers {

    public  static  final ScreenHandlerType<ShopScreenHandlerOwner> SHOP_SCREEN_HANDLER_OWNER =
            Registry.register(Registries.SCREEN_HANDLER, id("shop_gui_owner"),
                    new ExtendedScreenHandlerType<>(ShopScreenHandlerOwner::new, ShopScreenPayload.PACKET_CODEC));

    public  static  final ScreenHandlerType<ShopScreenHandlerCustomer> SHOP_SCREEN_HANDLER_CUSTOMER =
                        Registry.register(Registries.SCREEN_HANDLER, id("shop_gui_customer"),
                                new ExtendedScreenHandlerType<>(ShopScreenHandlerCustomer::new,ShopScreenPayload.PACKET_CODEC));



    public static Identifier id(String path) {
        return SpudaciousShops.id(path);
    }

    static final Map<Character, Identifier> CURRENCY_IMG_MAP = new HashMap<>() {{
        put('£', SpudaciousShops.id("textures/gui/currency_textures/gbp.png"));
        put('€', SpudaciousShops.id("textures/gui/currency_textures/eur.png"));
        put('x', SpudaciousShops.id("textures/gui/contract_slot.png"));
    }};


    public static void registerScreenHandlers() {
        SpudaciousShops.LOGGER.info("Registering screen handlers for " + SpudaciousShops.MOD_ID);
    }
}