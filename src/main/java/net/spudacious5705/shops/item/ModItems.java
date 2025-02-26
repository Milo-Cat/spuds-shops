package net.spudacious5705.shops.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.spudacious5705.shops.SpudaciousShops;

public class ModItems {

    public static final Item STOCK_WARNING = register(new Item(new FabricItemSettings()), "stock_warning");

    public static final Item PAYMENT_WARNING = register(new Item(new FabricItemSettings()), "payment_warning");

    private static Item register(Item item, String id) {
        return Registry.register(Registries.ITEM, new Identifier(SpudaciousShops.MOD_ID, id), item);
    }

    public static void registerModItems() {
        SpudaciousShops.LOGGER.info("Registering mod items for " + SpudaciousShops.MOD_ID);
    }
}
