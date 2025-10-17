package net.spudacious5705.shops.screen;



import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.spudacious5705.shops.SpudaciousShops;

import java.util.Map;

import static net.spudacious5705.shops.SpudaciousShops.getResource;

public class ModScreenHandlers {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, SpudaciousShops.MOD_ID);


    public static final RegistryObject<MenuType<ShopScreenHandlerOwner>> SHOP_SCREEN_HANDLER_OWNER =
            registerMenuType("shop_gui_owner", ShopScreenHandlerOwner::new);

    public static final RegistryObject<MenuType<ShopScreenHandlerCustomer>> SHOP_SCREEN_HANDLER_CUSTOMER =
            registerMenuType("shop_gui_customer",ShopScreenHandlerCustomer::new);

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
}