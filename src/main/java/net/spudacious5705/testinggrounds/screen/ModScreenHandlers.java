package net.spudacious5705.testinggrounds.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.spudacious5705.testinggrounds.TestingGrounds;

public class ModScreenHandlers {

    public static final ScreenHandlerType<StationScreenHandler> STATION_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier(TestingGrounds.MOD_ID, "station_crafting"),
                    new ExtendedScreenHandlerType<>(StationScreenHandler::new));

    public static final ScreenHandlerType<StorageScreenHandler> STORAGE_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier(TestingGrounds.MOD_ID, "storage_open"),
                    new ExtendedScreenHandlerType<>(StorageScreenHandler::new));

    public static final ScreenHandlerType<InscriberScreenHandler> INSCRIBER_SCREEN_HANDLER =
                    new ExtendedScreenHandlerType<>(InscriberScreenHandler::new);

    public  static  final ScreenHandlerType<ShopScreenHandler> SHOP_SCREEN_HANDLER =
                    new ExtendedScreenHandlerType<>(ShopScreenHandler::new);

    public static Identifier id(String path) {
        return new Identifier(TestingGrounds.MOD_ID, path);
    }
    public static void registerScreenHandlers() {
        TestingGrounds.LOGGER.info("Registering screen handlers for " + TestingGrounds.MOD_ID);

        Registry.register(Registries.SCREEN_HANDLER, id("inscriber_inscribing"), INSCRIBER_SCREEN_HANDLER);
        Registry.register(Registries.SCREEN_HANDLER, id("shop_gui"), SHOP_SCREEN_HANDLER);

        HandledScreens.register(STATION_SCREEN_HANDLER, StationScreen::new);
        HandledScreens.register(STORAGE_SCREEN_HANDLER, StorageScreen::new);
        HandledScreens.register(INSCRIBER_SCREEN_HANDLER, InscriberScreen::new);
        HandledScreens.register(SHOP_SCREEN_HANDLER, ShopScreen::new);

    }
}
