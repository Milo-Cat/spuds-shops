package net.spudacious5705.shops.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.block.enums.SlabType;
import net.minecraft.data.client.*;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.spudacious5705.shops.SpudaciousShops;
import net.spudacious5705.shops.block.ModBlocks;
import net.spudacious5705.shops.block.custom.RugShopBlock;
import net.spudacious5705.shops.block.custom.ShelfShopBlock;
import net.spudacious5705.shops.block.custom.WindowSillShopBlock;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.minecraft.data.client.VariantSettings.Rotation.*;

public class blockstateProvider extends FabricModelProvider {
    public blockstateProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator generator) {
        ModBlocks.registerModBlocks();
        ModBlocks.ALL_SHELF_SHOPS.forEach(shelfShopBlock -> {generateShelfBlock(generator, shelfShopBlock, shelfShopBlock.getWoodName());});

        ModBlocks.ALL_RUG_SHOPS.forEach(rug -> {generateRugBlock(generator,rug);});

        ModBlocks.ALL_WINDOW_SHOPS.forEach( window -> {
            String variant = window.STONE_TYPE.toString();
            Identifier textureId = SpudaciousShops.id("block/window_sill/"+variant);
            TextureMap textures = new TextureMap()
                    .put(TextureKey.PARTICLE, textureId)
                    .put(TextureKey.TEXTURE,textureId);

            Identifier modelId =
            new Model(
                    Optional.of(SpudaciousShops.id("block/shop_window")),
                    Optional.of(""),
                    TextureKey.PARTICLE, TextureKey.TEXTURE)
                    .upload(
                            window,
                            textures,
                            generator.modelCollector);


            Function<VariantSettings.Rotation, BlockStateVariant> variantGen = rotation -> BlockStateVariant.create().put(VariantSettings.MODEL, modelId).put(VariantSettings.Y, rotation);


            BlockStateVariantMap map = BlockStateVariantMap.create(WindowSillShopBlock.FACING)
                    .register(Direction.NORTH,variantGen.apply(R0))
                    .register(Direction.EAST,variantGen.apply(R90))
                    .register(Direction.SOUTH,variantGen.apply(R180))
                    .register(Direction.WEST,variantGen.apply(R270));

            generator.blockStateCollector.accept(
                    VariantsBlockStateSupplier.create(window)
                            .coordinate(map));

        });
    }

    private void generateShelfBlock(BlockStateModelGenerator generator, ShelfShopBlock block, String woodType){

        Model model_double = makeModel("",woodType);
        Model model_top = makeModel("_top",woodType);
        Model model_bottom = makeModel("_bottom",woodType);

        TextureMap textures = new TextureMap()
                .put(TextureKey.PARTICLE, Identifier.of("minecraft","block/"+woodType+"_planks"))
                .put(TextureKey.ALL,SpudaciousShops.id("block/shelf/shelf_"+woodType));

        Identifier itop = model_top.upload(block, textures, generator.modelCollector);
        Identifier ibottom = model_bottom.upload(block, textures, generator.modelCollector);
        Identifier idouble = model_double.upload(block, textures, generator.modelCollector);

        Identifier fullBlockModel = ModelIds.getBlockModelId(block);

        BlockStateVariantMap.DoubleProperty<SlabType,Direction> map = BlockStateVariantMap.create(ShelfShopBlock.SHELVES_ENABLED, ShelfShopBlock.FACING);
        map = generateForDirection(map,Direction.NORTH, R0, ibottom, itop, idouble);
        map = generateForDirection(map,Direction.EAST, R90, ibottom, itop, idouble);
        map = generateForDirection(map,Direction.SOUTH, R180, ibottom, itop, idouble);
        map = generateForDirection(map,Direction.WEST, VariantSettings.Rotation.R270, ibottom, itop, idouble);

        generator.blockStateCollector.accept(
                        VariantsBlockStateSupplier.create(block)
                .coordinate(map));

    }

    private void generateRugBlock(BlockStateModelGenerator generator, RugShopBlock block){
        Identifier textureId = SpudaciousShops.id("block/rug/"+block.COLOUR);
        TextureMap textures = new TextureMap()
                .put(TextureKey.PARTICLE, textureId)
                .put(TextureKey.ALL,textureId);


        BlockStateVariant main = helper(makeRugModel("main").upload(block, textures, generator.modelCollector));
        BlockStateVariant east = helper(makeRugModel("east").upload(block, textures, generator.modelCollector));
        BlockStateVariant north = helper(makeRugModel("north").upload(block, textures, generator.modelCollector));
        BlockStateVariant south = helper(makeRugModel("south").upload(block, textures, generator.modelCollector));
        BlockStateVariant west = helper(makeRugModel("west").upload(block, textures, generator.modelCollector));

        generator.blockStateCollector.accept(MultipartBlockStateSupplier.create(block)
                .with(main)
                .with( When.create().set(RugShopBlock.CONNECTED_EAST,true),east)
                .with( When.create().set(RugShopBlock.CONNECTED_NORTH,true),north)
                .with( When.create().set(RugShopBlock.CONNECTED_SOUTH,true),south)
                .with( When.create().set(RugShopBlock.CONNECTED_WEST,true),west)
        );
    }

    private static BlockStateVariant helper(Identifier identifier){
        return BlockStateVariant.create().put(VariantSettings.MODEL,identifier);
    }

    private BlockStateVariantMap.DoubleProperty<SlabType,Direction> generateForDirection(BlockStateVariantMap.DoubleProperty<SlabType,Direction> map, Direction direction, VariantSettings.Rotation rotation, Identifier ibottom, Identifier itop, Identifier idouble){
        return map.register(SlabType.BOTTOM, direction, BlockStateVariant.create().put(VariantSettings.MODEL, ibottom).put(VariantSettings.Y, rotation))
                .register(SlabType.TOP, direction, BlockStateVariant.create().put(VariantSettings.MODEL, itop).put(VariantSettings.Y, rotation))
                .register(SlabType.DOUBLE, direction, BlockStateVariant.create().put(VariantSettings.MODEL, idouble).put(VariantSettings.Y, rotation));

    }


    private static final String header = "block/shelf/shelf_shop";

    //helper method for creating Models with variants
    private static Model makeModel(String parent, String variant) {
        String id = header+parent;
        return new Model(Optional.of(SpudaciousShops.id(id)), Optional.of(parent), TextureKey.PARTICLE, TextureKey.ALL);
    }

    private static Model makeRugModel(String variant){
        variant = "_"+variant;
        String id = "block/rug_shop/rug_shop"+variant;
        return new Model(Optional.of(SpudaciousShops.id(id)), Optional.of(variant), TextureKey.PARTICLE, TextureKey.ALL);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        ModBlocks.registerModBlocks();
        ModBlocks.ALL_RUG_SHOPS.forEach(rug ->{
            String path;
            if(Objects.equals(rug.COLOUR, "red")){
                path = "block/rug_shop_main";
            }else {
                path = "block/rug_shop_" + rug.COLOUR + "_main";
            }
            itemModelGenerator.register(
                    rug.asItem(),
                    new Model(Optional.of(SpudaciousShops.id(path)), Optional.of(rug.COLOUR))
            );
        });

        ModBlocks.ALL_WINDOW_SHOPS.forEach(sill ->{
            String name = sill.STONE_TYPE.toString();
            String path = "block/shop_window_" + name;
            itemModelGenerator.register(
                    sill.asItem(),
                    new Model(Optional.of(SpudaciousShops.id(path)), Optional.of(""))
            );
        });
    }
}
