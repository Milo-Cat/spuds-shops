package net.spudacious5705.testinggrounds.block;

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
import net.spudacious5705.testinggrounds.TestingGrounds;
import net.spudacious5705.testinggrounds.block.custom.*;

public class ModBlocks {

    public static final Block SUSPICIOUS_BLOCK = registerBlock("suspicious_block",
            new Block(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK)));

    public static final Block PAINFULL_BLOCK = registerBlock("painfull_block",
            new PotatBlock(AbstractBlock.Settings.create().strength(1f)));

    public static final Block STATION = registerBlock("station",
            new StationBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).nonOpaque()));

    public static final Block STORAGE_BLOCK = registerBlock("storage_block",
            new StationBlock(FabricBlockSettings.copyOf(Blocks.CHAIN).nonOpaque()));

    public static final Block MAGIC_BLOCK = registerBlock("magic_block",
            new MagicBlock(FabricBlockSettings.copyOf(Blocks.NETHERRACK).nonOpaque()));

    public static final Block INSCRIBER_BLOCK = registerBlock("inscriber_block",
            new InscriberBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK)));

    public static final Block MULTI_BLOCK = registerBlock("multi_block",
            new MultiBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK)));

    public static final Block SHOP_BLOCK = registerBlock("shop_block",
            new ShopBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK)));

    public static final Block DENSE_WOOD_BLOCK = registerBlock("dense_wood_block",
            new Block(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.DRIPSTONE_BLOCK)));

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK,new Identifier(TestingGrounds.MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, new Identifier(TestingGrounds.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings()));
    }

    public static void initialise() {

    }
    public static void registerModBlocks() {
        TestingGrounds.LOGGER.info("Registering ModBlocks for " + TestingGrounds.MOD_ID);
    }
}
