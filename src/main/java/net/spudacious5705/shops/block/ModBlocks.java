package net.spudacious5705.shops.block;


import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.spudacious5705.shops.SpudaciousShops;
import net.spudacious5705.shops.block.custom.*;
import net.spudacious5705.shops.block.resources.VariantResources;
import net.spudacious5705.shops.item.custom.ShopItem;
import net.spudacious5705.shops.properties.Colour;
import net.spudacious5705.shops.util.PostRegAssigner;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static net.spudacious5705.shops.SpudaciousShops.MOD_ID;
import static net.spudacious5705.shops.block.resources.VariantResources.*;
import static net.spudacious5705.shops.block.resources.VariantResources.wood_variant.*;

public class ModBlocks{

    public static final List<Runnable> postRegistryTasks = new ArrayList<>();

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MOD_ID);

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);

    private static final BlockBehaviour.Properties settingsChain = shopSettings(Blocks.CHAIN);

    private static final BlockBehaviour.Properties settingsWood = shopSettings(Blocks.OAK_PLANKS);

    private static final BlockBehaviour.Properties settingsStone = shopSettings(Blocks.STONE);

    public static final BlockBehaviour.Properties settingsCarpet = shopSettings(Blocks.RED_CARPET);

    private static BlockBehaviour.Properties shopSettings(Block example){
        return BlockBehaviour.Properties.ofFullCopy(example)
                .noOcclusion()
                .strength(2f, Float.MAX_VALUE);
    }

    public static final List<DeferredBlock<? extends AbstractShopBlock>> ALL_SHOPS = new ArrayList<>();
    public static final List<DeferredBlock<? extends AbstractShopBlock>> BASIC_SHOPS = new ArrayList<>();

    public static final List<DeferredBlock<AngledShopBlock>> ALL_ORIGINAL_SHOPS = new ArrayList<>(11);

    public static final List<DeferredBlock<ShelfShopBlock>> ALL_SHELF_SHOPS = new ArrayList<>(11);

    public static final List<DeferredBlock<WindowSillShopBlock>> ALL_WINDOW_SHOPS = new ArrayList<>(2);


    public static final List<DeferredBlock<RugShopBlock>> ALL_RUG_SHOPS = new ArrayList<>(11);

    //region ANGLED
    public static final DeferredBlock<AngledShopBlock> SHOP_BLOCK_ANGLED_OAK = registerAngledShopBlock(OAK, new PostRegAssigner<>(() -> Items.OAK_PLANKS));

    private static void registerOriginal() {
        registerAngledShopBlock(ACACIA, new PostRegAssigner<>(() -> Items.ACACIA_PLANKS));
        registerAngledShopBlock(BAMBOO, new PostRegAssigner<>(() -> Items.BAMBOO_PLANKS));
        registerAngledShopBlock(BIRCH, new PostRegAssigner<>(() -> Items.BIRCH_PLANKS));
        registerAngledShopBlock(CHERRY, new PostRegAssigner<>(() -> Items.CHERRY_PLANKS));
        registerAngledShopBlock(CRIMSON, new PostRegAssigner<>(() -> Items.CRIMSON_PLANKS));
        registerAngledShopBlock(DARK_OAK, new PostRegAssigner<>(() -> Items.DARK_OAK_PLANKS));
        registerAngledShopBlock(MANGROVE, new PostRegAssigner<>(() -> Items.MANGROVE_PLANKS));
        registerAngledShopBlock(SPRUCE, new PostRegAssigner<>(() -> Items.SPRUCE_PLANKS));
        registerAngledShopBlock(WARPED, new PostRegAssigner<>(() -> Items.WARPED_PLANKS));
        registerAngledShopBlock(JUNGLE, new PostRegAssigner<>(() -> Items.JUNGLE_PLANKS));
    }

    private static DeferredBlock<AngledShopBlock> registerAngledShopBlock(wood_variant variant, PostRegAssigner<Item> woodType) {
        String name = "shop_"+variant.name;

        DeferredBlock<AngledShopBlock> block = BLOCKS.register(
                name,
                () -> new AngledShopBlock(settingsWood, woodType, variant)
        );

        ALL_ORIGINAL_SHOPS.add(block);

        VariantResources.putItem(ANGLED,woodType, block);


        for(Colour colour : Colour.values()) {

            DeferredItem<ShopItem> item = registerShopBlockItem(name, block, colour);

            postRegistryTasks.add(() ->
                    block.get().addDropItem(item.get(), colour)
            );
        }

        ITEMS.register(name,
                () -> new BlockItem(block.get(), new Item.Properties())
        );

        addToAllShops(
                block
        );
        return block;
    }
    //endregion

    //region WINDOW SILL
    private static void registerWindowSill() {
        registerWindowShopBlock("calcite", new PostRegAssigner<>(() -> Items.CALCITE));
        registerWindowShopBlock("andesite", new PostRegAssigner<>(() -> Items.ANDESITE));
    }
    //endregion

    public static final DeferredBlock<HookShopBlock> SHOP_BLOCK_HOOK = registerBasic("hook_shop",()->new HookShopBlock(settingsChain));

    //region RUG
    public static void registerRugs() {
        registerRugLegacy();
        registerRug("white", new PostRegAssigner<>(() -> Items.WHITE_CARPET), new PostRegAssigner<>(() -> Items.WHITE_DYE));
        registerRug("orange", new PostRegAssigner<>(() -> Items.ORANGE_CARPET), new PostRegAssigner<>(() -> Items.ORANGE_DYE));
        registerRug("magenta", new PostRegAssigner<>(() -> Items.MAGENTA_CARPET), new PostRegAssigner<>(() -> Items.MAGENTA_DYE));
        registerRug("light_blue", new PostRegAssigner<>(() -> Items.LIGHT_BLUE_CARPET), new PostRegAssigner<>(() -> Items.LIGHT_BLUE_DYE));
        registerRug("yellow", new PostRegAssigner<>(() -> Items.YELLOW_CARPET), new PostRegAssigner<>(() -> Items.YELLOW_DYE));
        registerRug("lime", new PostRegAssigner<>(() -> Items.LIME_CARPET), new PostRegAssigner<>(() -> Items.LIME_DYE));
        registerRug("pink", new PostRegAssigner<>(() -> Items.PINK_CARPET), new PostRegAssigner<>(() -> Items.PINK_DYE));
        registerRug("gray", new PostRegAssigner<>(() -> Items.GRAY_CARPET), new PostRegAssigner<>(() -> Items.GRAY_DYE));
        registerRug("light_gray", new PostRegAssigner<>(() -> Items.LIGHT_GRAY_CARPET), new PostRegAssigner<>(() -> Items.LIGHT_GRAY_DYE));
        registerRug("cyan", new PostRegAssigner<>(() -> Items.CYAN_CARPET), new PostRegAssigner<>(() -> Items.CYAN_DYE));
        registerRug("purple", new PostRegAssigner<>(() -> Items.PURPLE_CARPET), new PostRegAssigner<>(() -> Items.PURPLE_DYE));
        registerRug("blue", new PostRegAssigner<>(() -> Items.BLUE_CARPET), new PostRegAssigner<>(() -> Items.BLUE_DYE));
        registerRug("brown", new PostRegAssigner<>(() -> Items.BROWN_CARPET), new PostRegAssigner<>(() -> Items.BROWN_DYE));
        registerRug("green", new PostRegAssigner<>(() -> Items.GREEN_CARPET), new PostRegAssigner<>(() -> Items.GREEN_DYE));
        registerRug("black", new PostRegAssigner<>(() -> Items.BLACK_CARPET), new PostRegAssigner<>(() -> Items.BLACK_DYE));
    }


    private static void registerRug(String colour, PostRegAssigner<Item> carpet, PostRegAssigner<Item> dye){

        DeferredBlock<RugShopBlock> rug = registerBasic("rug_shop_"+colour,()->new RugShopBlock(carpet,colour));
        ALL_RUG_SHOPS.add(rug);

        VariantResources.putItem(RUGS_CARPET, carpet, rug);
        VariantResources.putItem(RUGS_DYE, dye, rug);

    }

    private static void registerRugLegacy(){
        String name = "rug_shop";

        DeferredBlock<RugShopBlock> rug = registerBasic(name,()->new RugShopBlock(new PostRegAssigner<>(() -> Items.RED_CARPET), "red"));
        ALL_RUG_SHOPS.add(rug);

        VariantResources.putItem(RUGS_CARPET,new PostRegAssigner<>(() -> Items.RED_CARPET), rug);
        VariantResources.putItem(RUGS_DYE, new PostRegAssigner<>(() -> Items.RED_DYE), rug);

    }
    //endregion

    public static final DeferredBlock<CrateShopBlock> SHOP_BLOCK_CRATE = registerBasic("crate_shop",()->new CrateShopBlock(settingsWood));

    //region SHELF
    private static void registerShelf() {
        registerShelf(ACACIA, new PostRegAssigner<>(() -> Blocks.ACACIA_SLAB));
        registerShelf(BAMBOO, new PostRegAssigner<>(() -> Blocks.BAMBOO_SLAB));
        registerShelf(BIRCH, new PostRegAssigner<>(() -> Blocks.BIRCH_SLAB));
        registerShelf(CHERRY, new PostRegAssigner<>(() -> Blocks.CHERRY_SLAB));
        registerShelf(CRIMSON, new PostRegAssigner<>(() -> Blocks.CRIMSON_SLAB));
        registerShelf(DARK_OAK, new PostRegAssigner<>(() -> Blocks.DARK_OAK_SLAB));
        registerShelf(MANGROVE, new PostRegAssigner<>(() -> Blocks.MANGROVE_SLAB));
        registerShelf(OAK, new PostRegAssigner<>(() -> Blocks.OAK_SLAB));
        registerShelf(SPRUCE, new PostRegAssigner<>(() -> Blocks.SPRUCE_SLAB));
        registerShelf(WARPED, new PostRegAssigner<>(() -> Blocks.WARPED_SLAB));
        registerShelf(JUNGLE, new PostRegAssigner<>(() -> Blocks.JUNGLE_SLAB));
    }

    private static void registerShelf(wood_variant variant, PostRegAssigner<Block> slab){
        
        String name = "shelf_shop_"+variant.name;
        
        DeferredBlock<ShelfShopBlock> shop = BLOCKS.register(name, () -> new ShelfShopBlock(settingsWood,slab,variant));

        
        ITEMS.register(name, () ->
                new BlockItem(shop.get(), new Item.Properties())//ERROR ON THIS LINE
        );

        addToAllShops(
                shop
        );

        ALL_SHELF_SHOPS.add(shop);
        VariantResources.putBlock(SHELF, slab, shop);
    }
    //endregion


    private static <S extends AbstractShopBlock> DeferredBlock<S> registerBasic(String name, Supplier<S> shop){

        DeferredBlock<S> block = addToBasicShops(
                addToAllShops(
                        BLOCKS.register(name, shop)
                )
        );
        ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));

        return block;
    }

    private static void registerWindowShopBlock(String name, PostRegAssigner<Item> stoneType) {
        DeferredBlock<WindowSillShopBlock> shop = registerBasic("shop_window_"+name,() -> new WindowSillShopBlock(settingsStone, stoneType));
        ALL_WINDOW_SHOPS.add(shop);
        VariantResources.putItem(WINDOW_SILL, stoneType, shop);
    }

    private static <S extends AbstractShopBlock> DeferredBlock<S> addToAllShops(DeferredBlock<S> register) {
        ALL_SHOPS.add(register);
        return register;
    }

    private static <S extends AbstractShopBlock> DeferredBlock<S> addToBasicShops(DeferredBlock<S> register) {
        BASIC_SHOPS.add(register);
        return register;
    }

    private static DeferredItem<ShopItem> registerShopBlockItem(String name, DeferredBlock<AngledShopBlock> block, Colour colour) {
        name = name + "_" + colour.asString();
        return ITEMS.register(name, () -> new ShopItem(block.get(),new Item.Properties(), colour));

    }

    public static void registerModBlocks(IEventBus modEventBus) {
        SpudaciousShops.LOGGER.info("Registering mod blocks for " + MOD_ID);
        registerOriginal();
        registerWindowSill();
        registerRugs();
        registerShelf();
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        //postRegistryTasks is called in SpudaciousShops.commonSetup()
    }

    public static List<DeferredBlock<? extends AbstractShopBlock>> getAllShops(){
        return ALL_SHOPS;
    }

}
