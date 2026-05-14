package net.spudacious5705.shops.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.spudacious5705.shops.block.ModBlockTags;
import net.spudacious5705.shops.block.ModBlocks;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        ModBlocks.registerModBlocks();


        FabricTagBuilder pick = getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE);
        FabricTagBuilder axe = getOrCreateTagBuilder(BlockTags.AXE_MINEABLE);
        ModBlocks.getAllShops().forEach(shop ->{
            getOrCreateTagBuilder(shop.getPreferredTool()).add(shop);
        });


        addShops(getOrCreateTagBuilder(ModBlockTags.SPUDS_SHOPS));
    }

    private void addShops(FabricTagBuilder builder) {
        ModBlocks.getAllShops().forEach(builder::add);
    }
}
