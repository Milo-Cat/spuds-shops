package net.spudacious5705.shops.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.spudacious5705.shops.SpudaciousShops;
import net.spudacious5705.shops.network.BlockPosPayload;

public class ModScreenHandlers {

    public  static  final ScreenHandlerType<ShopScreenHandlerOwner> SHOP_SCREEN_HANDLER_OWNER =
                    new ExtendedScreenHandlerType<>(ShopScreenHandlerOwner::new, BlockPosPayload.PACKET_CODEC);

    public  static  final ScreenHandlerType<ShopScreenHandlerCustomer> SHOP_SCREEN_HANDLER_CUSTOMER =
                    new ExtendedScreenHandlerType<>(ShopScreenHandlerCustomer::new, BlockPosPayload.PACKET_CODEC);


    public static Identifier id(String path) {
        return Identifier.of(SpudaciousShops.MOD_ID, path);
    }
    public static void registerScreenHandlers() {
        SpudaciousShops.LOGGER.info("Registering screen handlers for " + SpudaciousShops.MOD_ID);

        Registry.register(Registries.SCREEN_HANDLER, id("shop_gui_customer"), SHOP_SCREEN_HANDLER_CUSTOMER);

        Registry.register(Registries.SCREEN_HANDLER, id("shop_gui_owner"), SHOP_SCREEN_HANDLER_OWNER);

    }
}
