package net.spudacious5705.shops.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.spudacious5705.shops.SpudaciousShops;
import net.spudacious5705.shops.block.ModBlocks;
import net.spudacious5705.shops.util.registry.DeferredRegister;

import java.util.function.Supplier;

public final class ModItemGroups {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.createCreativeTabs(SpudaciousShops.MOD_ID);

    public static final Supplier<CreativeModeTab> SHOPS_TAB = CREATIVE_MODE_TABS.register("shops_tab",
            () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0).icon(() -> new ItemStack(ModBlocks.SHOP_BLOCK_ANGLED_OAK.get().getDefaultColouredShopItem()))
                    .title(Component.translatable("itemGroup.spudaciousshops.shop_item_group"))
                    .displayItems(
                            (params, entries) -> {
                                entries.accept(ModItems.CONTRACT_SCROLL.get());
                                ModBlocks.ALL_SHOPS.forEach(shop -> entries.accept(shop.get()));
                            }
                    ).build());

    public static void register(Object eventBus) {
        CREATIVE_MODE_TABS.register(null);
    }
}
