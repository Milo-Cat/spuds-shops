package net.spudacious5705.testinggrounds.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.spudacious5705.testinggrounds.block.ModBlocks;
import net.spudacious5705.testinggrounds.item.ModItems;

import java.util.concurrent.CompletableFuture;


public class ModLootTableProvider extends FabricBlockLootTableProvider {


    private final CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup;

    public ModLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> reg) {
        super(dataOutput);
        this.registryLookup = reg;
    }

    @Override
    public void generate() {
        addDrop(ModBlocks.PAINFULL_BLOCK);
        addDrop(ModBlocks.STORAGE_BLOCK);
        addDrop(ModBlocks.DENSE_WOOD_BLOCK);
        addDrop(ModBlocks.STATION);
        //addDrop(ModBlocks.TROPHY);
        addDrop(ModBlocks.SUSPICIOUS_BLOCK, oreDrops(ModBlocks.SUSPICIOUS_BLOCK, ModItems.SUSPICIOUS_SUBSTANCE));

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


    }

}
