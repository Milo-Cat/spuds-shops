package net.spudacious5705.testinggrounds.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.util.Identifier;
import net.spudacious5705.testinggrounds.TestingGrounds;
import net.spudacious5705.testinggrounds.block.ModBlocks;
import net.spudacious5705.testinggrounds.item.ModItems;

public class ModModelProvider extends FabricModelProvider {

    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.DENSE_WOOD_BLOCK);//generates: block states .json, block model .json, item model .json files
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.SUSPICIOUS_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.PAINFULL_BLOCK);

    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ModItems.SUSPICIOUS_SUBSTANCE, Models.GENERATED);
        itemModelGenerator.register(ModItems.RUNIC_GOLD, Models.GENERATED);
    }
}
