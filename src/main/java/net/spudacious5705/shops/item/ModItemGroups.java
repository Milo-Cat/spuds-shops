package net.spudacious5705.shops.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.spudacious5705.shops.SpudaciousShops;
import net.spudacious5705.shops.block.ModBlocks;


public final class ModItemGroups {
    public static final ItemGroup SHOP_ITEM_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModBlocks.SHOP_BLOCK_OAK))
            .entries((displayContext, entries) -> {

                        entries.add(ModBlocks.SHOP_BLOCK_ACACIA);
                        entries.add(ModBlocks.SHOP_BLOCK_BAMBOO);
                        entries.add(ModBlocks.SHOP_BLOCK_BIRCH);
                        entries.add(ModBlocks.SHOP_BLOCK_CHERRY);
                        entries.add(ModBlocks.SHOP_BLOCK_CRIMSON);
                        entries.add(ModBlocks.SHOP_BLOCK_OAK);
                        entries.add(ModBlocks.SHOP_BLOCK_MANGROVE);
                        entries.add(ModBlocks.SHOP_BLOCK_DARK_OAK);
                        entries.add(ModBlocks.SHOP_BLOCK_SPRUCE);
                        entries.add(ModBlocks.SHOP_BLOCK_WARPED);
                        entries.add(ModBlocks.SHOP_BLOCK_JUNGLE);

                    })
            .displayName(Text.translatable("itemGroup.spudaciousshops.shop_item_group"))
            .build();

    public static void initialise() {
        Registry.register(Registries.ITEM_GROUP,Identifier.of(SpudaciousShops.MOD_ID, "shop_item_group"), SHOP_ITEM_GROUP);
    }
}
