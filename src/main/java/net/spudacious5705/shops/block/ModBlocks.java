package net.spudacious5705.shops.block;


import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.spudacious5705.shops.SpudaciousShops;
import net.spudacious5705.shops.block.custom.*;
import net.spudacious5705.shops.item.custom.ShopItem;
import net.spudacious5705.shops.properties.Colour;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static net.spudacious5705.shops.SpudaciousShops.MOD_ID;
import static net.spudacious5705.shops.block.VariantResources.*;
import static net.spudacious5705.shops.block.VariantResources.wood_variant.*;

public class ModBlocks{

    public static final List<Runnable> postRegistryTasks = new ArrayList<>();

    public static final DeferredRegister<net.minecraft.world.level.block.Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);

    public static final DeferredRegister<net.minecraft.world.item.Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    private static final BlockBehaviour.Properties settingsChain = shopSettings(Blocks.CHAIN);

    private static final BlockBehaviour.Properties settingsWood = shopSettings(Blocks.OAK_PLANKS);

    private static final BlockBehaviour.Properties settingsStone = shopSettings(Blocks.STONE);

    public static final BlockBehaviour.Properties settingsCarpet = shopSettings(Blocks.RED_CARPET);

    private static BlockBehaviour.Properties shopSettings(Block example){
        return BlockBehaviour.Properties.copy(example)
                .noOcclusion()
                .strength(2f, Float.MAX_VALUE);
    }

    public static final List<RegistryObject<? extends AbstractShopBlock>> ALL_SHOPS = new ArrayList<>();
    public static final List<RegistryObject<? extends AbstractShopBlock>> BASIC_SHOPS = new ArrayList<>();

    public static final List<RegistryObject<ShelfShopBlock>> ALL_SHELF_SHOPS = new ArrayList<>(11);

    public static final List<RegistryObject<WindowSillShopBlock>> ALL_WINDOW_SHOPS = new ArrayList<>(10);


    public static final List<RegistryObject<RugShopBlock>> ALL_RUG_SHOPS = new ArrayList<>(11);

    //region ANGLED
    public static final RegistryObject<AngledShopBlock> SHOP_BLOCK_ANGLED_ACACIA = registerAngledShopBlock(ACACIA, new PostRegAssigner<>(() -> Items.ACACIA_PLANKS));
    public static final RegistryObject<AngledShopBlock> SHOP_BLOCK_ANGLED_BAMBOO = registerAngledShopBlock(BAMBOO, new PostRegAssigner<>(() -> Items.BAMBOO_PLANKS));
    public static final RegistryObject<AngledShopBlock> SHOP_BLOCK_ANGLED_BIRCH = registerAngledShopBlock(BIRCH, new PostRegAssigner<>(() -> Items.BIRCH_PLANKS));
    public static final RegistryObject<AngledShopBlock> SHOP_BLOCK_ANGLED_CHERRY = registerAngledShopBlock(CHERRY, new PostRegAssigner<>(() -> Items.CHERRY_PLANKS));
    public static final RegistryObject<AngledShopBlock> SHOP_BLOCK_ANGLED_CRIMSON = registerAngledShopBlock(CRIMSON, new PostRegAssigner<>(() -> Items.CRIMSON_PLANKS));
    public static final RegistryObject<AngledShopBlock> SHOP_BLOCK_ANGLED_DARK_OAK = registerAngledShopBlock(DARK_OAK, new PostRegAssigner<>(() -> Items.DARK_OAK_PLANKS));
    public static final RegistryObject<AngledShopBlock> SHOP_BLOCK_ANGLED_MANGROVE = registerAngledShopBlock(MANGROVE, new PostRegAssigner<>(() -> Items.MANGROVE_PLANKS));
    public static final RegistryObject<AngledShopBlock> SHOP_BLOCK_ANGLED_OAK = registerAngledShopBlock(OAK, new PostRegAssigner<>(() -> Items.OAK_PLANKS));
    public static final RegistryObject<AngledShopBlock> SHOP_BLOCK_ANGLED_SPRUCE = registerAngledShopBlock(SPRUCE, new PostRegAssigner<>(() -> Items.SPRUCE_PLANKS));
    public static final RegistryObject<AngledShopBlock> SHOP_BLOCK_ANGLED_WARPED = registerAngledShopBlock(WARPED, new PostRegAssigner<>(() -> Items.WARPED_PLANKS));
    public static final RegistryObject<AngledShopBlock> SHOP_BLOCK_ANGLED_JUNGLE = registerAngledShopBlock(JUNGLE, new PostRegAssigner<>(() -> Items.JUNGLE_PLANKS));


    private static RegistryObject<AngledShopBlock> registerAngledShopBlock(VariantResources.wood_variant variant, PostRegAssigner<Item> woodType) {
        String name = "shop_"+variant.name;

        RegistryObject<AngledShopBlock> block = BLOCKS.register(
                name,
                () -> new AngledShopBlock(settingsWood, woodType, variant)
        );
        VariantResources.putItem(ANGLED,woodType, block::get);


        for(Colour colour : Colour.values()) {

            RegistryObject<ShopItem> item = registerShopBlockItem(name, block, colour);

            postRegistryTasks.add(() ->
                    {
                        block.get().addDropItem(item.get(), colour);
                    }
            );
        }

        ITEMS.register(name,
                () -> new BlockItem(block.get(), new Item.Properties())
        );
                
        return addToAllShops(
                block
        );
    }
    //endregion

    //region WINDOW SILL
    public static final RegistryObject<WindowSillShopBlock> SHOP_BLOCK_WINDOW_CALCITE = registerWindowShopBlock("calcite", new PostRegAssigner<>(() -> Items.CALCITE));
    public static final RegistryObject<WindowSillShopBlock> SHOP_BLOCK_WINDOW_ANDESITE = registerWindowShopBlock("andesite", new PostRegAssigner<>(() -> Items.ANDESITE));
    //endregion

    public static final RegistryObject<HookShopBlock> SHOP_BLOCK_HOOK = registerBasic("hook_shop",()->new HookShopBlock(settingsChain));

    //region RUG
    public static final RegistryObject<RugShopBlock> SHOP_BLOCK_RUG_RED = registerRugLegacy();
    public static final RegistryObject<RugShopBlock> SHOP_BLOCK_RUG_WHITE = registerRug("white", new PostRegAssigner<>(() -> Items.WHITE_CARPET), new PostRegAssigner<>(() -> Items.WHITE_DYE));
    public static final RegistryObject<RugShopBlock> SHOP_BLOCK_RUG_ORANGE = registerRug("orange", new PostRegAssigner<>(() -> Items.ORANGE_CARPET), new PostRegAssigner<>(() -> Items.ORANGE_DYE));
    public static final RegistryObject<RugShopBlock> SHOP_BLOCK_RUG_MAGENTA = registerRug("magenta", new PostRegAssigner<>(() -> Items.MAGENTA_CARPET), new PostRegAssigner<>(() -> Items.MAGENTA_DYE));
    public static final RegistryObject<RugShopBlock> SHOP_BLOCK_RUG_LIGHT_BLUE = registerRug("light_blue", new PostRegAssigner<>(() -> Items.LIGHT_BLUE_CARPET), new PostRegAssigner<>(() -> Items.LIGHT_BLUE_DYE));
    public static final RegistryObject<RugShopBlock> SHOP_BLOCK_RUG_YELLOW = registerRug("yellow", new PostRegAssigner<>(() -> Items.YELLOW_CARPET), new PostRegAssigner<>(() -> Items.YELLOW_DYE));
    public static final RegistryObject<RugShopBlock> SHOP_BLOCK_RUG_LIME = registerRug("lime", new PostRegAssigner<>(() -> Items.LIME_CARPET), new PostRegAssigner<>(() -> Items.LIME_DYE));
    public static final RegistryObject<RugShopBlock> SHOP_BLOCK_RUG_PINK = registerRug("pink", new PostRegAssigner<>(() -> Items.PINK_CARPET), new PostRegAssigner<>(() -> Items.PINK_DYE));
    public static final RegistryObject<RugShopBlock> SHOP_BLOCK_RUG_GRAY = registerRug("gray", new PostRegAssigner<>(() -> Items.GRAY_CARPET), new PostRegAssigner<>(() -> Items.GRAY_DYE));
    public static final RegistryObject<RugShopBlock> SHOP_BLOCK_RUG_LIGHT_GRAY = registerRug("light_gray", new PostRegAssigner<>(() -> Items.LIGHT_GRAY_CARPET), new PostRegAssigner<>(() -> Items.LIGHT_GRAY_DYE));
    public static final RegistryObject<RugShopBlock> SHOP_BLOCK_RUG_CYAN = registerRug("cyan", new PostRegAssigner<>(() -> Items.CYAN_CARPET), new PostRegAssigner<>(() -> Items.CYAN_DYE));
    public static final RegistryObject<RugShopBlock> SHOP_BLOCK_RUG_PURPLE = registerRug("purple", new PostRegAssigner<>(() -> Items.PURPLE_CARPET), new PostRegAssigner<>(() -> Items.PURPLE_DYE));
    public static final RegistryObject<RugShopBlock> SHOP_BLOCK_RUG_BLUE = registerRug("blue", new PostRegAssigner<>(() -> Items.BLUE_CARPET), new PostRegAssigner<>(() -> Items.BLUE_DYE));
    public static final RegistryObject<RugShopBlock> SHOP_BLOCK_RUG_BROWN = registerRug("brown", new PostRegAssigner<>(() -> Items.BROWN_CARPET), new PostRegAssigner<>(() -> Items.BROWN_DYE));
    public static final RegistryObject<RugShopBlock> SHOP_BLOCK_RUG_GREEN = registerRug("green", new PostRegAssigner<>(() -> Items.GREEN_CARPET), new PostRegAssigner<>(() -> Items.GREEN_DYE));
    public static final RegistryObject<RugShopBlock> SHOP_BLOCK_RUG_BLACK = registerRug("black", new PostRegAssigner<>(() -> Items.BLACK_CARPET), new PostRegAssigner<>(() -> Items.BLACK_DYE));


    private static RegistryObject<RugShopBlock> registerRug(String colour, PostRegAssigner<Item> carpet, PostRegAssigner<Item> dye){

        RegistryObject<RugShopBlock> rug = registerBasic("rug_shop_"+colour,()->new RugShopBlock(carpet,colour));
        ALL_RUG_SHOPS.add(rug);

        VariantResources.putItem(RUGS_CARPET, carpet, rug);
        VariantResources.putItem(RUGS_DYE, dye, rug);

        return rug;
    }

    private static RegistryObject<RugShopBlock> registerRugLegacy(){
        String name = "rug_shop";

        RegistryObject<RugShopBlock> rug = registerBasic(name,()->new RugShopBlock(new PostRegAssigner<>(() -> Items.RED_CARPET), "red"));
        ALL_RUG_SHOPS.add(rug);

        VariantResources.putItem(RUGS_CARPET,new PostRegAssigner<>(() -> Items.RED_CARPET),rug::get);
        VariantResources.putItem(RUGS_DYE, new PostRegAssigner<>(() -> Items.RED_DYE),rug::get);

        return rug;
    }
    //endregion

    public static final RegistryObject<CrateShopBlock> SHOP_BLOCK_CRATE = registerBasic("crate_shop",()->new CrateShopBlock(settingsWood));

    //region SHELF
    public static final RegistryObject<ShelfShopBlock> SHOP_BLOCK_SHELF_ACACIA = registerShelf(ACACIA,new PostRegAssigner<>(() -> Blocks.ACACIA_SLAB));
    public static final RegistryObject<ShelfShopBlock> SHOP_BLOCK_SHELF_BAMBOO = registerShelf(BAMBOO,new PostRegAssigner<>(() -> Blocks.BAMBOO_SLAB));
    public static final RegistryObject<ShelfShopBlock> SHOP_BLOCK_SHELF_BIRCH = registerShelf(BIRCH,new PostRegAssigner<>(() -> Blocks.BIRCH_SLAB));
    public static final RegistryObject<ShelfShopBlock> SHOP_BLOCK_SHELF_CHERRY = registerShelf(CHERRY,new PostRegAssigner<>(() -> Blocks.CHERRY_SLAB));
    public static final RegistryObject<ShelfShopBlock> SHOP_BLOCK_SHELF_CRIMSON = registerShelf(CRIMSON,new PostRegAssigner<>(() -> Blocks.CRIMSON_SLAB));
    public static final RegistryObject<ShelfShopBlock> SHOP_BLOCK_SHELF_DARK_OAK = registerShelf(DARK_OAK,new PostRegAssigner<>(() -> Blocks.DARK_OAK_SLAB));
    public static final RegistryObject<ShelfShopBlock> SHOP_BLOCK_SHELF_MANGROVE = registerShelf(MANGROVE,new PostRegAssigner<>(() -> Blocks.MANGROVE_SLAB));
    public static final RegistryObject<ShelfShopBlock> SHOP_BLOCK_SHELF_OAK = registerShelf(OAK,new PostRegAssigner<>(() -> Blocks.OAK_SLAB));
    public static final RegistryObject<ShelfShopBlock> SHOP_BLOCK_SHELF_SPRUCE = registerShelf(SPRUCE,new PostRegAssigner<>(() -> Blocks.SPRUCE_SLAB));
    public static final RegistryObject<ShelfShopBlock> SHOP_BLOCK_SHELF_WARPED = registerShelf(WARPED,new PostRegAssigner<>(() -> Blocks.WARPED_SLAB));
    public static final RegistryObject<ShelfShopBlock> SHOP_BLOCK_SHELF_JUNGLE = registerShelf(JUNGLE,new PostRegAssigner<>(() -> Blocks.JUNGLE_SLAB));


    private static RegistryObject<ShelfShopBlock> registerShelf(VariantResources.wood_variant variant, PostRegAssigner<Block> slab){
        
        String name = "shelf_shop_"+variant.name;
        
        RegistryObject<ShelfShopBlock> shop = BLOCKS.register(name, () -> new ShelfShopBlock(settingsWood,slab,variant));

        
        ITEMS.register(name, () ->
                new BlockItem(shop.get(), new Item.Properties())//ERROR ON THIS LINE
        );

        addToAllShops(
                shop
        );

        ALL_SHELF_SHOPS.add(shop);
        VariantResources.putBlock(SHELF, slab, shop::get);
        return shop;
    }
    //endregion


    private static <S extends AbstractShopBlock> RegistryObject<S> registerBasic(String name, Supplier<S> shop){

        RegistryObject<S> block = addToBasicShops(
                addToAllShops(
                        BLOCKS.register(name, shop)
                )
        );
        ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));

        return block;
    }

    private static RegistryObject<WindowSillShopBlock> registerWindowShopBlock(String name, PostRegAssigner<Item> stoneType) {
        RegistryObject<WindowSillShopBlock> shop = registerBasic("shop_window_"+name,() -> new WindowSillShopBlock(settingsStone, stoneType));
        ALL_WINDOW_SHOPS.add(shop);
        VariantResources.putItem(WINDOW_SILL, stoneType,shop::get);
        return shop;
    }

    private static <S extends AbstractShopBlock> RegistryObject<S> addToAllShops(RegistryObject<S> register) {
        ALL_SHOPS.add(register);
        return register;
    }

    private static <S extends AbstractShopBlock> RegistryObject<S> addToBasicShops(RegistryObject<S> register) {
        BASIC_SHOPS.add(register);
        return register;
    }

    private static RegistryObject<ShopItem> registerShopBlockItem(String name, RegistryObject<AngledShopBlock> block, Colour colour) {
        name = name + "_" + colour.asString();
        return ITEMS.register(name, () -> new ShopItem(block.get(),new Item.Properties(), colour));

    }

    public static void registerModBlocks(IEventBus modEventBus) {
        SpudaciousShops.LOGGER.info("Registering mod blocks for " + MOD_ID);

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        //postRegistryTasks is called in SpudaciousShops.commonSetup()
    }

    public static List<RegistryObject<? extends AbstractShopBlock>> getAllShops(){
        return ALL_SHOPS;
    }

}
