package net.spudacious5705.shops.item;


import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.spudacious5705.shops.SpudaciousShops;
import net.spudacious5705.shops.block.ModBlocks;


public final class ModItemGroups {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SpudaciousShops.MOD_ID);

    public static final RegistryObject<CreativeModeTab> SHOPS_TAB = CREATIVE_MODE_TABS.register("shops_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModBlocks.SHOP_BLOCK_ANGLED_OAK.get().getDefaultColouredShopItem()))
                    .title(Component.translatable("itemGroup.spudaciousshops.shop_item_group"))
                    .displayItems(
                            (params, entries) -> {
                                entries.accept(ModItems.CONTRACT_SCROLL.get());
                                ModBlocks.ALL_SHOPS.forEach(shop -> entries.accept(shop.get()));
                    }
                    ).build());
    

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}

