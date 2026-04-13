package net.spudacious5705.shops.item;


import net.minecraft.world.item.BlockItem;
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

    public static final DeferredItem<Item> STOCK_WARNING = registerBasic("stock_warning");

    public static final DeferredItem<Item> PAYMENT_WARNING = registerBasic("payment_warning");

    public static final DeferredItem<Item> CONTRACT_SCROLL = ITEMS.registerItem("contract_scroll",
            ContractScroll::new,
            () -> new Item.Properties().stacksTo(1));

    private static <I extends Item> DeferredItem<Item> registerBasic(String name) {
        return ITEMS.registerItem(name, Item::new, Item.Properties::new);
    }

    public static void registerModItems(IEventBus modEventBus) {
        SpudaciousShops.LOGGER.info("Registering mod items for " + SpudaciousShops.MOD_ID);
        ITEMS.register(modEventBus);
    }
}
