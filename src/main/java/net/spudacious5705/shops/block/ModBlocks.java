package net.spudacious5705.shops.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.spudacious5705.shops.SpudaciousShops;
import net.spudacious5705.shops.block.custom.*;

public class ModBlocks {

    public static final Block SHOP_BLOCK_ACACIA = registerBlock("shop_acacia",
            new ShopBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).nonOpaque()));
    public static final Block SHOP_BLOCK_BAMBOO = registerBlock("shop_bamboo",
            new ShopBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).nonOpaque()));
    public static final Block SHOP_BLOCK_BIRCH = registerBlock("shop_birch",
            new ShopBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).nonOpaque()));
    public static final Block SHOP_BLOCK_CHERRY = registerBlock("shop_cherry",
            new ShopBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).nonOpaque()));
    public static final Block SHOP_BLOCK_CRIMSON = registerBlock("shop_crimson",
            new ShopBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).nonOpaque()));
    public static final Block SHOP_BLOCK_DARK_OAK = registerBlock("shop_dark_oak",
            new ShopBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).nonOpaque()));
    public static final Block SHOP_BLOCK_MANGROVE = registerBlock("shop_mangrove",
            new ShopBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).nonOpaque()));
    public static final Block SHOP_BLOCK_OAK = registerBlock("shop_oak",
            new ShopBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).nonOpaque()));
    public static final Block SHOP_BLOCK_SPRUCE = registerBlock("shop_spruce",
            new ShopBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).nonOpaque()));
    public static final Block SHOP_BLOCK_WARPED = registerBlock("shop_warped",
            new ShopBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).nonOpaque()));
    public static final Block SHOP_BLOCK_JUNGLE = registerBlock("shop_jungle",
            new ShopBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).nonOpaque()));

    /*public static final Block TROPHY = registerBlock("trophy",
            new Trophy(new Block(FabricBlockSettings.copyOf(Blocks.GOLD_BLOCK).nonOpaque()),FabricBlockSettings.copyOf(Blocks.GOLD_BLOCK).nonOpaque()));
*/
    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK,new Identifier(SpudaciousShops.MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, new Identifier(SpudaciousShops.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings()));
    }

    public static void registerModBlocks() {
        SpudaciousShops.LOGGER.info("Registering mod blocks for " + SpudaciousShops.MOD_ID);
    }
}
