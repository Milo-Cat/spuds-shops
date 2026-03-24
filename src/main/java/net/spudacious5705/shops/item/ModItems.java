package net.spudacious5705.shops.item;


import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.spudacious5705.shops.SpudaciousShops;
import net.spudacious5705.shops.item.custom.ContractScroll;

import java.util.function.Supplier;

import static net.spudacious5705.shops.SpudaciousShops.MOD_ID;

public class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);

    public static final DeferredItem<Item> STOCK_WARNING = register(() -> new Item(new Item.Properties()), "stock_warning");

    public static final DeferredItem<Item> PAYMENT_WARNING = register(() -> new Item(new Item.Properties()), "payment_warning");

    public static final DeferredItem<ContractScroll> CONTRACT_SCROLL = register(() -> new ContractScroll(new Item.Properties()), "contract_scroll");

    private static <I extends Item> DeferredItem<I> register(Supplier<I> item, String name) {
        return ITEMS.register(name, (item));
    }

    public static void registerModItems(IEventBus modEventBus) {
        SpudaciousShops.LOGGER.info("Registering mod items for " + SpudaciousShops.MOD_ID);
        ITEMS.register(modEventBus);
    }
}
