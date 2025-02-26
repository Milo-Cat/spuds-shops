package net.spudacious5705.shops.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.registry.RegistryWrapper;
import net.spudacious5705.shops.block.ModBlocks;
import net.spudacious5705.shops.item.ModItems;

import java.util.concurrent.CompletableFuture;


public class ModLootTableProvider extends FabricBlockLootTableProvider {

    public ModLootTableProvider(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generate() {
        addDrop(ModBlocks.SHOP_BLOCK_ACACIA);
        addDrop(ModBlocks.SHOP_BLOCK_BAMBOO);
        addDrop(ModBlocks.SHOP_BLOCK_BIRCH);
        addDrop(ModBlocks.SHOP_BLOCK_CHERRY);
        addDrop(ModBlocks.SHOP_BLOCK_CRIMSON);
        addDrop(ModBlocks.SHOP_BLOCK_DARK_OAK);
        addDrop(ModBlocks.SHOP_BLOCK_MANGROVE);
        addDrop(ModBlocks.SHOP_BLOCK_OAK);
        addDrop(ModBlocks.SHOP_BLOCK_SPRUCE);
        addDrop(ModBlocks.SHOP_BLOCK_WARPED);
        addDrop(ModBlocks.SHOP_BLOCK_WARPED);

    }

}
