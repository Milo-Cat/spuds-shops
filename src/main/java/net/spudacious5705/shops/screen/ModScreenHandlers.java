package net.spudacious5705.shops.screen;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.spudacious5705.shops.SpudaciousShops;
import net.spudacious5705.shops.screen.owner_screen.ShopScreenHandlerOwner;
import net.spudacious5705.shops.util.MenuTypeHelper;
import net.spudacious5705.shops.util.registry.DeferredRegister;

import java.util.Map;
import java.util.function.Supplier;

import static net.spudacious5705.shops.SpudaciousShops.id;

public class ModScreenHandlers {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.createMenuTypes(SpudaciousShops.MOD_ID);

    public static final Supplier<MenuType<ShopScreenHandlerOwner>> SHOP_SCREEN_HANDLER_OWNER =
            MENUS.register("shop_gui_owner",
                    () -> MenuTypeHelper.create(ShopScreenHandlerOwner::create));

    public static final Supplier<MenuType<ShopScreenHandlerCustomer>> SHOP_SCREEN_HANDLER_CUSTOMER =
            MENUS.register("shop_gui_customer",
                    () -> MenuTypeHelper.create(ShopScreenHandlerCustomer::create));

    public static final Map<Character, ResourceLocation> CURRENCY_IMG_MAP = Map.of(
            '£', id("textures/gui/currency_textures/gbp.png"),
            '€', id("textures/gui/currency_textures/eur.png"),
            'x', id("textures/gui/contract_slot.png")
    );

    public static void registerScreenHandlers(Object modEventBus) {
        SpudaciousShops.LOGGER.info("Registering screen handlers for " + SpudaciousShops.MOD_ID);
        MENUS.register(null);
    }
}
