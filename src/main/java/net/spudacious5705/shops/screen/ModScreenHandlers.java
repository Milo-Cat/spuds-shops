package net.spudacious5705.shops.screen;



import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.spudacious5705.shops.SpudaciousShops;
import net.spudacious5705.shops.screen.owner_screen.ShopScreenHandlerOwner;
import net.spudacious5705.shops.screen.owner_screen.ShopScreenOwner;

import java.util.Map;

import static net.spudacious5705.shops.SpudaciousShops.getResource;

public class ModScreenHandlers {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, SpudaciousShops.MOD_ID);


    public static final RegistryObject<MenuType<ShopScreenHandlerOwner>> SHOP_SCREEN_HANDLER_OWNER =
            registerMenuType("shop_gui_owner", ShopScreenHandlerOwner::create);

    public static final RegistryObject<MenuType<ShopScreenHandlerCustomer>> SHOP_SCREEN_HANDLER_CUSTOMER =
            registerMenuType("shop_gui_customer",ShopScreenHandlerCustomer::create);

    private static <T extends AbstractContainerMenu>RegistryObject<MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }



    public static final Map<Character, ResourceLocation> CURRENCY_IMG_MAP = Map.of(
            '£', getResource("textures/gui/currency_textures/gbp.png"),
            '€', getResource("textures/gui/currency_textures/eur.png"),
            'x', getResource("textures/gui/contract_slot.png")
    );


    public static void registerScreenHandlers(IEventBus modEventBus) {//called by modMain

        SpudaciousShops.LOGGER.info("Registering screen handlers for " + SpudaciousShops.MOD_ID);
        MENUS.register(modEventBus);
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerScreens(){
        ScreenResources.init();
        MenuScreens.register(SHOP_SCREEN_HANDLER_OWNER.get(), ShopScreenOwner::new);
        MenuScreens.register(SHOP_SCREEN_HANDLER_CUSTOMER.get(), ShopScreenCustomer::new);

    }
}