package net.spudacious5705.shops.item;

import net.spudacious5705.shops.SpudaciousShops;
import net.spudacious5705.shops.item.custom.ContractScroll;
import net.spudacious5705.shops.util.registry.DeferredItem;
import net.spudacious5705.shops.util.registry.DeferredRegister;

import java.util.function.Supplier;

import static net.spudacious5705.shops.SpudaciousShops.MOD_ID;

public class ModItems {

    public static final DeferredRegister.ItemRegister ITEMS = DeferredRegister.createItems(MOD_ID);

    public static final DeferredItem<net.minecraft.world.item.Item> STOCK_WARNING =
            register(() -> new net.minecraft.world.item.Item(new net.minecraft.world.item.Item.Properties()), "stock_warning");

    public static final DeferredItem<net.minecraft.world.item.Item> PAYMENT_WARNING =
            register(() -> new net.minecraft.world.item.Item(new net.minecraft.world.item.Item.Properties()), "payment_warning");

    public static final DeferredItem<ContractScroll> CONTRACT_SCROLL =
            register(() -> new ContractScroll(new net.minecraft.world.item.Item.Properties()), "contract_scroll");

    private static <I extends net.minecraft.world.item.Item> DeferredItem<I> register(Supplier<I> item, String name) {
        return ITEMS.register(name, item);
    }

    public static void registerModItems(Object ignoredEventBus) {
        SpudaciousShops.LOGGER.info("Registering mod items for " + SpudaciousShops.MOD_ID);
        ITEMS.register(null);
    }
}
