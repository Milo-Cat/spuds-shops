package net.spudacious5705.shops.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.enums.SlabType;
import net.minecraft.item.Item;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.state.property.Properties;
import net.spudacious5705.shops.block.custom.AbstractShopBlock;
import net.spudacious5705.shops.block.custom.AngledShopBlock;
import net.spudacious5705.shops.block.custom.ShelfShopBlock;
import net.spudacious5705.shops.lootcondition.MatchingCushionColourCondition;
import net.spudacious5705.shops.properties.Colour;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.spudacious5705.shops.block.ModBlocks.*;


public class ModLootTableProvider extends FabricBlockLootTableProvider {


    public ModLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {


        registerModBlocks();

        //filter and keep only the angled shop blocks (the ones with the cushions).
        List<AngledShopBlock> allShops = getAllShops().stream()
                .filter(AngledShopBlock.class::isInstance)
                .map(AngledShopBlock.class::cast)
                .toList();

        for(AngledShopBlock shop : allShops){

            LootTable.Builder lootTable = LootTable.builder()
                    .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1)));

            for(Colour colour: Colour.values()){

                Item itemDrop = shop.getColouredShopItem(colour);

                lootTable.pool(
                        LootPool.builder()
                                .rolls(ConstantLootNumberProvider.create(1))
                                .with(ItemEntry.builder(itemDrop))
                                .conditionally(new MatchingCushionColourCondition(colour.asString()))
                );
            }

            addDrop(shop,lootTable);
        }


        List<ShelfShopBlock> shelfShops = getAllShops().stream()
                .filter(ShelfShopBlock.class::isInstance)
                .map(ShelfShopBlock.class::cast)
                .toList();

        for (ShelfShopBlock shop : shelfShops) {
            LootTable.Builder lootTable = LootTable.builder();

            // Base pool: Always drops one item
            LootPool.Builder basePool = LootPool.builder()
                    .rolls(ConstantLootNumberProvider.create(1))
                    .with(ItemEntry.builder(shop));
            lootTable.pool(basePool);

            // Extra pool: Adds an extra drop if the slab is double
            LootPool.Builder extraPool = LootPool.builder()
                    .rolls(ConstantLootNumberProvider.create(1))
                    .conditionally(BlockStatePropertyLootCondition.builder(shop)
                            .properties(StatePredicate.Builder.create()
                                    .exactMatch(Properties.SLAB_TYPE, SlabType.DOUBLE)))
                    .with(ItemEntry.builder(shop));
            lootTable.pool(extraPool);

            addDrop(shop, lootTable);
        }
        

        for(AbstractShopBlock shop : BASIC_SHOPS) {
            addDrop(shop, LootTable.builder().pool(
                    LootPool.builder()
                            .rolls(ConstantLootNumberProvider.create(1))
                            .with(ItemEntry.builder(shop))));

        }

    }

}
