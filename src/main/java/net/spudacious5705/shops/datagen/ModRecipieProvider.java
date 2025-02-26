package net.spudacious5705.shops.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.recipe.book.RecipeCategory;
import net.spudacious5705.shops.block.ModBlocks;

import java.util.function.Consumer;

public class ModRecipieProvider extends FabricRecipeProvider {
    public ModRecipieProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> consumer) {
        makeShopRecipie(consumer,ModBlocks.SHOP_BLOCK_ACACIA, Blocks.ACACIA_WOOD);
        makeShopRecipie(consumer,ModBlocks.SHOP_BLOCK_BAMBOO, Blocks.BAMBOO_PLANKS);
        makeShopRecipie(consumer,ModBlocks.SHOP_BLOCK_BIRCH, Blocks.BIRCH_PLANKS);
        makeShopRecipie(consumer,ModBlocks.SHOP_BLOCK_CHERRY, Blocks.CHERRY_PLANKS);
        makeShopRecipie(consumer,ModBlocks.SHOP_BLOCK_CRIMSON, Blocks.CRIMSON_PLANKS);
        makeShopRecipie(consumer,ModBlocks.SHOP_BLOCK_DARK_OAK, Blocks.DARK_OAK_PLANKS);
        makeShopRecipie(consumer,ModBlocks.SHOP_BLOCK_MANGROVE, Blocks.MANGROVE_PLANKS);
        makeShopRecipie(consumer,ModBlocks.SHOP_BLOCK_OAK, Blocks.OAK_PLANKS);
        makeShopRecipie(consumer,ModBlocks.SHOP_BLOCK_SPRUCE, Blocks.SPRUCE_PLANKS);
        makeShopRecipie(consumer,ModBlocks.SHOP_BLOCK_WARPED, Blocks.WARPED_PLANKS);
        makeShopRecipie(consumer,ModBlocks.SHOP_BLOCK_JUNGLE, Blocks.JUNGLE_PLANKS);
    }

    private void makeShopRecipie(Consumer<RecipeJsonProvider> consumer, Block shop, Block wood){
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, shop)
                .pattern(" g ").pattern("pwp").pattern("ccc")
                .input('g', Blocks.GLASS)
                .input('c', Blocks.CHEST)
                .input('p', wood)
                .input('w', Blocks.WHITE_WOOL)
                .criterion(hasItem(Blocks.GLASS), conditionsFromItem(Blocks.GLASS))
                .criterion(hasItem(Blocks.CHEST),
                        conditionsFromItem(Blocks.CHEST))
                .criterion(hasItem(wood), conditionsFromItem(wood))
                .criterion(hasItem(Blocks.WHITE_WOOL),
                        conditionsFromItem(Blocks.WHITE_WOOL))
                .offerTo(consumer);

    }
}
