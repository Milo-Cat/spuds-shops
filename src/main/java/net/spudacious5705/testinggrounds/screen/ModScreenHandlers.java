package net.spudacious5705.testinggrounds.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.spudacious5705.testinggrounds.TestingGrounds;
import net.spudacious5705.testinggrounds.block.entity.ModBlockEntities;
import net.spudacious5705.testinggrounds.block.entity.renderer.ShopBlockEntityRenderer;

public class ModScreenHandlers {

    public static final ScreenHandlerType<StationScreenHandler> STATION_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier(TestingGrounds.MOD_ID, "station_crafting"),
                    new ExtendedScreenHandlerType<>(StationScreenHandler::new));

    public static final ScreenHandlerType<StorageScreenHandler> STORAGE_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier(TestingGrounds.MOD_ID, "storage_open"),
                    new ExtendedScreenHandlerType<>(StorageScreenHandler::new));

    public static final ScreenHandlerType<InscriberScreenHandler> INSCRIBER_SCREEN_HANDLER =
                    new ExtendedScreenHandlerType<>(InscriberScreenHandler::new);

    public  static  final ScreenHandlerType<ShopScreenHandlerOwner> SHOP_SCREEN_HANDLER_OWNER =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier(TestingGrounds.MOD_ID, "shop_gui_owner"),
                    new ExtendedScreenHandlerType<>(ShopScreenHandlerOwner::new));

    public  static  final ScreenHandlerType<ShopScreenHandlerCustomer> SHOP_SCREEN_HANDLER_CUSTOMER =
                    new ExtendedScreenHandlerType<>(ShopScreenHandlerCustomer::new);


    public static Identifier id(String path) {
        return new Identifier(TestingGrounds.MOD_ID, path);
    }
    public static void registerScreenHandlers() {
        TestingGrounds.LOGGER.info("Registering screen handlers for " + TestingGrounds.MOD_ID);

        Registry.register(Registries.SCREEN_HANDLER, id("inscriber_inscribing"), INSCRIBER_SCREEN_HANDLER);

        Registry.register(Registries.SCREEN_HANDLER, id("shop_gui_customer"), SHOP_SCREEN_HANDLER_CUSTOMER);

        HandledScreens.register(STATION_SCREEN_HANDLER, StationScreen::new);
        HandledScreens.register(STORAGE_SCREEN_HANDLER, StorageScreen::new);
        HandledScreens.register(INSCRIBER_SCREEN_HANDLER, InscriberScreen::new);
        HandledScreens.register(SHOP_SCREEN_HANDLER_OWNER, ShopScreenOwner::new);
        HandledScreens.register(SHOP_SCREEN_HANDLER_CUSTOMER, ShopScreenCustomer::new);

        BlockEntityRendererFactories.register(ModBlockEntities.SHOP_ENTITY, ShopBlockEntityRenderer::new);
    }
}
