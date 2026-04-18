package net.spudacious5705.shops.item;


import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.spudacious5705.shops.SpudaciousShops;
import net.spudacious5705.shops.item.custom.ContractScroll;

import static net.spudacious5705.shops.SpudaciousShops.MOD_ID;

public class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);

    public static final DeferredItem<Item> STOCK_WARNING = ITEMS.registerSimpleItem("stock_warning");

    public static final DeferredItem<Item> PAYMENT_WARNING = ITEMS.registerSimpleItem("payment_warning");

    public static final DeferredItem<ContractScroll> CONTRACT_SCROLL = ITEMS.registerItem("contract_scroll", ContractScroll::new);

    public static void registerModItems(IEventBus modEventBus) {
        SpudaciousShops.LOGGER.info("Registering mod items for " + SpudaciousShops.MOD_ID);
        ITEMS.register(modEventBus);
    }
}
