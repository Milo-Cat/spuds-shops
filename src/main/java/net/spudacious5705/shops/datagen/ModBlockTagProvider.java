package net.spudacious5705.shops.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.spudacious5705.shops.block.ModBlocks;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(BlockTags.AXE_MINEABLE)
                .add(ModBlocks.SHOP_BLOCK_ACACIA)
                .add(ModBlocks.SHOP_BLOCK_BAMBOO)
                .add(ModBlocks.SHOP_BLOCK_BIRCH)
                .add(ModBlocks.SHOP_BLOCK_CHERRY)
                .add(ModBlocks.SHOP_BLOCK_CRIMSON)
                .add(ModBlocks.SHOP_BLOCK_DARK_OAK)
                .add(ModBlocks.SHOP_BLOCK_MANGROVE)
                .add(ModBlocks.SHOP_BLOCK_OAK)
                .add(ModBlocks.SHOP_BLOCK_SPRUCE)
                .add(ModBlocks.SHOP_BLOCK_WARPED);

    }
}
