package net.spudacious5705.testinggrounds.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.spudacious5705.testinggrounds.TestingGrounds;

public class ModItems {
    public static final Item SUSPICIOUS_SUBSTANCE = register(new Item(new FabricItemSettings()), "suspicious_substance");

    public static final Item RUNIC_GOLD = register(new Item(new FabricItemSettings()), "runic_gold");

    public static final Item STOCK_WARNING = register(new Item(new FabricItemSettings()), "stock_warning");

    public static final Item PAYMENT_WARNING = register(new Item(new FabricItemSettings()), "payment_warning");

    private static Item register(Item item, String id) {
        return Registry.register(Registries.ITEM, new Identifier(TestingGrounds.MOD_ID, id), item);
    }


    public static void initialize() {
        // Get the event for modifying entries in the ingredients group.
// And register an event handler that adds our suspicious item to the ingredients group.
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS)
                .register((itemGroup) -> itemGroup.add(ModItems.SUSPICIOUS_SUBSTANCE));
    }

    private static void addItemsToIngredientItemGroup(FabricItemGroupEntries entries) {
    }
    public static void registerModItems() {
        TestingGrounds.LOGGER.info("Registering Mod Items for " + TestingGrounds.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(ModItems::addItemsToIngredientItemGroup);
    }

}
