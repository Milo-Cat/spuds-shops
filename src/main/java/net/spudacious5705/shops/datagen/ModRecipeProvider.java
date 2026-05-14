package net.spudacious5705.shops.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.*;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.spudacious5705.shops.SpudaciousShops;
import net.spudacious5705.shops.block.ModBlocks;
import net.spudacious5705.shops.block.custom.AngledShopBlock;
import net.spudacious5705.shops.block.custom.ShelfShopBlock;
import net.spudacious5705.shops.item.ModItems;
import net.spudacious5705.shops.util.CushionResources;
import net.spudacious5705.shops.properties.Colour;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.spudacious5705.shops.block.ModBlocks.getAllShops;

public class ModRecipeProvider extends FabricRecipeProvider {


    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        CushionResources.initialise();

        for (Colour colour : Colour.values()) {

            for (Wood wood : Wood.values()) {
                makeAngledShopRecipe(exporter, wood.angledShopBlock.getColouredShopItem(colour), wood.block, CushionResources.COLOUR_MAP.get(colour).wool());
            }

        }

        List<ShelfShopBlock> shelfShops = getAllShops().stream()
                .filter(ShelfShopBlock.class::isInstance)
                .map(ShelfShopBlock.class::cast)
                .toList();

        for (ShelfShopBlock shelf : shelfShops) {
            makeShelfShopRecipe(exporter, shelf);
        }

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.SHOP_BLOCK_HOOK.asItem())
                .pattern(" i ")
                .pattern("ccc")
                .pattern(" h ")
                .input('i', Blocks.CHAIN)
                .input('c', Blocks.CHEST)
                .input('h', Blocks.TRIPWIRE_HOOK)
                .criterion(hasItem(Blocks.CHAIN), conditionsFromItem(Blocks.CHAIN))
                .criterion(hasItem(Blocks.CHEST), conditionsFromItem(Blocks.CHEST))
                .criterion(hasItem(Blocks.TRIPWIRE_HOOK), conditionsFromItem(Blocks.TRIPWIRE_HOOK))
                .offerTo(exporter);


        ModBlocks.ALL_RUG_SHOPS.forEach(rug -> {ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, rug.asItem())
                .pattern("   ")
                .pattern("grg")
                .pattern("ccc")
                .input('g', Items.GOLD_NUGGET)
                .input('c', Blocks.CHEST)
                .input('r', rug.CARPET)
                .criterion(hasItem(rug.CARPET), conditionsFromItem(rug.CARPET))
                .criterion(hasItem(Blocks.CHEST), conditionsFromItem(Blocks.CHEST))
                .criterion(hasItem(Items.GOLD_NUGGET), conditionsFromItem(Items.GOLD_NUGGET))
                .offerTo(exporter);
        });

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.SHOP_BLOCK_CRATE.asItem())
                .pattern("  b")
                .pattern(" b ")
                .pattern("b  ")
                .input('b', Blocks.BARREL)
                .criterion(hasItem(Blocks.BARREL), conditionsFromItem(Blocks.BARREL))
                .offerTo(exporter);

        ModBlocks.ALL_WINDOW_SHOPS.forEach(window -> {ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, window.asItem())
                .pattern("   ")
                .pattern(" r ")
                .pattern("ccc")
                .input('c', Blocks.CHEST)
                .input('r', window.STONE_TYPE)
                .criterion(hasItem(window.STONE_TYPE), conditionsFromItem(window.STONE_TYPE))
                .criterion(hasItem(Blocks.CHEST), conditionsFromItem(Blocks.CHEST))
                .criterion(hasItem(Items.GOLD_NUGGET), conditionsFromItem(Items.GOLD_NUGGET))
                .offerTo(exporter);
        });

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.CONTRACT_SCROLL)
                .input(Items.PAPER)
                .input(Items.FEATHER)
                .criterion(hasItem(Items.PAPER), conditionsFromItem(Items.PAPER))
                .criterion(hasItem(Items.FEATHER), conditionsFromItem(Items.FEATHER))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.CONTRACT_SCROLL)
                .input(ModItems.CONTRACT_SCROLL)
                .criterion(hasItem(ModItems.CONTRACT_SCROLL), conditionsFromItem(ModItems.CONTRACT_SCROLL))
                .offerTo(exporter, SpudaciousShops.id("contract_scroll_wipe"));

    }

    private void makeAngledShopRecipe(RecipeExporter exporter, Item shopItem, Block wood, Item wool){
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, shopItem)
                .pattern(" g ").pattern("pwp").pattern("ccc")
                .input('g', Blocks.GLASS)
                .input('c', Blocks.CHEST)
                .input('p', wood)
                .input('w', wool)
                .criterion(hasItem(Blocks.GLASS), conditionsFromItem(Blocks.GLASS))
                .criterion(hasItem(Blocks.CHEST),
                        conditionsFromItem(Blocks.CHEST))
                .criterion(hasItem(wood), conditionsFromItem(wood))
                .criterion(hasItem(wool),
                        conditionsFromItem(wool))
                .offerTo(exporter);

    }

    private void makeShelfShopRecipe(RecipeExporter exporter, ShelfShopBlock shopItem){
        Block slab = shopItem.SlabWoodType;

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, shopItem, 2)
                .pattern("ccc").pattern("sss").pattern("ccc")
                .input('c', Blocks.CHEST)
                .input('s', slab)
                .criterion(hasItem(Blocks.CHEST),conditionsFromItem(Blocks.CHEST))
                .criterion(hasItem(slab), conditionsFromItem(slab))
                .offerTo(exporter);

    }

    public enum Wood {
        ACACIA(ModBlocks.SHOP_BLOCK_ANGLED_ACACIA, Blocks.ACACIA_WOOD),
        BAMBOO(ModBlocks.SHOP_BLOCK_ANGLED_BAMBOO, Blocks.BAMBOO_PLANKS),
        BIRCH(ModBlocks.SHOP_BLOCK_ANGLED_BIRCH, Blocks.BIRCH_PLANKS),
        CHERRY(ModBlocks.SHOP_BLOCK_ANGLED_CHERRY, Blocks.CHERRY_PLANKS),
        CRIMSON(ModBlocks.SHOP_BLOCK_ANGLED_CRIMSON, Blocks.CRIMSON_PLANKS),
        DARK_OAK(ModBlocks.SHOP_BLOCK_ANGLED_DARK_OAK, Blocks.DARK_OAK_PLANKS),
        MANGROVE(ModBlocks.SHOP_BLOCK_ANGLED_MANGROVE, Blocks.MANGROVE_PLANKS),
        OAK(ModBlocks.SHOP_BLOCK_ANGLED_OAK, Blocks.OAK_PLANKS),
        SPRUCE(ModBlocks.SHOP_BLOCK_ANGLED_SPRUCE, Blocks.SPRUCE_PLANKS),
        WARPED(ModBlocks.SHOP_BLOCK_ANGLED_WARPED, Blocks.WARPED_PLANKS),
        JUNGLE(ModBlocks.SHOP_BLOCK_ANGLED_JUNGLE, Blocks.JUNGLE_PLANKS);

        private final AngledShopBlock angledShopBlock;
        private final Block block;

        // Constructor
        Wood(AngledShopBlock angledShopBlock, Block block) {
            this.angledShopBlock = angledShopBlock;
            this.block = block;
        }


        public Block getBlock() {
            return block;
        }
    }
}
