package net.spudacious5705.shops.item;


import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.spudacious5705.shops.SpudaciousShops;
import net.spudacious5705.shops.item.custom.ContractScroll;

public class ModItems {

    public static final Item STOCK_WARNING = register(new Item(new Item.Settings()), "stock_warning");

    public static final Item PAYMENT_WARNING = register(new Item(new Item.Settings()), "payment_warning");

    public static final ContractScroll CONTRACT_SCROLL = register(new ContractScroll(new Item.Settings()), "contract_scroll");

    private static <I extends Item> I register(I item, String id) {
        return Registry.register(Registries.ITEM, Identifier.of(SpudaciousShops.MOD_ID, id), item);
    }
    public static void registerModItems() {
        SpudaciousShops.LOGGER.info("Registering mod items for " + SpudaciousShops.MOD_ID);
    }
}
