package net.spudacious5705.shops.screen;



import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.spudacious5705.shops.SpudaciousShops;
import net.spudacious5705.shops.screen.owner_screen.ShopScreenHandlerOwner;
import net.spudacious5705.shops.screen.owner_screen.ShopScreenOwner;

import java.util.Map;
import java.util.function.Supplier;

import static net.spudacious5705.shops.SpudaciousShops.id;


public class ModScreenHandlers {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, SpudaciousShops.MOD_ID);


    public static final Supplier<MenuType<ShopScreenHandlerOwner>> SHOP_SCREEN_HANDLER_OWNER =
            MENUS.register("shop_gui_owner",
                    () -> IMenuTypeExtension.create(ShopScreenHandlerOwner::create));

    public static final Supplier<MenuType<ShopScreenHandlerCustomer>> SHOP_SCREEN_HANDLER_CUSTOMER =
            MENUS.register("shop_gui_customer",
                    () -> IMenuTypeExtension.create(ShopScreenHandlerCustomer::create));




    public static final Map<Character, ResourceLocation> CURRENCY_IMG_MAP = Map.of(
            '£', id("textures/gui/currency_textures/gbp.png"),
            '€', id("textures/gui/currency_textures/eur.png"),
            'x', id("textures/gui/contract_slot.png")
    );


    public static void registerScreenHandlers(IEventBus modEventBus) {//called by modMain
        ScreenResources.init();
        SpudaciousShops.LOGGER.info("Registering screen handlers for " + SpudaciousShops.MOD_ID);
        MENUS.register(modEventBus);
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(SHOP_SCREEN_HANDLER_OWNER.get(), ShopScreenOwner::new);
        event.register(SHOP_SCREEN_HANDLER_CUSTOMER.get(), ShopScreenCustomer::new);
    }
}