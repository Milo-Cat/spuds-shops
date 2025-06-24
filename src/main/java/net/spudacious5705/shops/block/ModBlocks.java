package net.spudacious5705.shops.block;

import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.spudacious5705.shops.SpudaciousShops;
import net.spudacious5705.shops.block.custom.*;
import net.spudacious5705.shops.item.custom.ShopItem;
import net.spudacious5705.shops.properties.Colour;

import java.util.ArrayList;
import java.util.List;

import static net.spudacious5705.shops.block.VariantResources.wood_variant.*;

public abstract class ModBlocks{


    private static final AbstractBlock.Settings settingsWood = shopSettings(AbstractShopBlock.Settings.copy(Blocks.OAK_PLANKS));

    private static final AbstractBlock.Settings settingsStone = shopSettings(AbstractBlock.Settings.copy(Blocks.STONE));

    public static final AbstractBlock.Settings settingsCarpet = shopSettings(AbstractBlock.Settings.copy(Blocks.RED_CARPET));

    private static AbstractBlock.Settings shopSettings(AbstractBlock.Settings settingsExample){
        return settingsExample
                .nonOpaque()
                .hardness(-1f)
                .resistance(Float.MAX_VALUE);
    }

    public static final List<AbstractShopBlock> ALL_SHOPS = new ArrayList<>();
    public static final List<AbstractShopBlock> BASIC_SHOPS = new ArrayList<>();

    public static final List<ShelfShopBlock> ALL_SHELF_SHOPS = new ArrayList<>(11);

    public static final List<WindowSillShopBlock> ALL_WINDOW_SHOPS = new ArrayList<WindowSillShopBlock>(10);


    public static final List<RugShopBlock> ALL_RUG_SHOPS = new ArrayList<>(11);

    //region ANGLED
    public static final AngledShopBlock SHOP_BLOCK_ANGLED_ACACIA = registerAngledShopBlock(ACACIA, Items.ACACIA_PLANKS);
    public static final AngledShopBlock SHOP_BLOCK_ANGLED_BAMBOO = registerAngledShopBlock(BAMBOO, Items.BAMBOO_PLANKS);
    public static final AngledShopBlock SHOP_BLOCK_ANGLED_BIRCH = registerAngledShopBlock(BIRCH, Items.BIRCH_PLANKS);
    public static final AngledShopBlock SHOP_BLOCK_ANGLED_CHERRY = registerAngledShopBlock(CHERRY, Items.CHERRY_PLANKS);
    public static final AngledShopBlock SHOP_BLOCK_ANGLED_CRIMSON = registerAngledShopBlock(CRIMSON, Items.CRIMSON_PLANKS);
    public static final AngledShopBlock SHOP_BLOCK_ANGLED_DARK_OAK = registerAngledShopBlock(DARK_OAK, Items.DARK_OAK_PLANKS);
    public static final AngledShopBlock SHOP_BLOCK_ANGLED_MANGROVE = registerAngledShopBlock(MANGROVE, Items.MANGROVE_PLANKS);
    public static final AngledShopBlock SHOP_BLOCK_ANGLED_OAK = registerAngledShopBlock(OAK, Items.OAK_PLANKS);
    public static final AngledShopBlock SHOP_BLOCK_ANGLED_SPRUCE = registerAngledShopBlock(SPRUCE, Items.SPRUCE_PLANKS);
    public static final AngledShopBlock SHOP_BLOCK_ANGLED_WARPED = registerAngledShopBlock(WARPED, Items.WARPED_PLANKS);
    public static final AngledShopBlock SHOP_BLOCK_ANGLED_JUNGLE = registerAngledShopBlock(JUNGLE, Items.JUNGLE_PLANKS);


    private static AngledShopBlock registerAngledShopBlock(VariantResources.wood_variant variant, Item woodType) {
        String name = "shop_"+variant.name;
        Identifier id = SpudaciousShops.id(name);

        AngledShopBlock block = new AngledShopBlock(settingsWood, woodType, variant);
        for(Colour colour : Colour.values()) {
            block.addDropItem(registerShopBlockItem(name, block, colour), colour);
        }

        return addToAllShops(
                Registry.register(
                        Registries.BLOCK,
                        id,
                        block
                )
        );
    }
    //endregion

    //region WINDOW SILL
    public static final WindowSillShopBlock SHOP_BLOCK_WINDOW_CALCITE = registerWindowShopBlock("calcite", Items.CALCITE);
    public static final WindowSillShopBlock SHOP_BLOCK_WINDOW_ANDESITE = registerWindowShopBlock("andesite", Items.ANDESITE);
    //endregion

    public static final HookShopBlock SHOP_BLOCK_HOOK = registerBasic("hook_shop",new HookShopBlock(shopSettings(AbstractBlock.Settings.copy(Blocks.CHAIN))));

    //region RUG
    public static final RugShopBlock SHOP_BLOCK_RUG_RED = registerRugLegacy();
    public static final RugShopBlock SHOP_BLOCK_RUG_WHITE = registerRug("white", Blocks.WHITE_CARPET, Items.WHITE_DYE);
    public static final RugShopBlock SHOP_BLOCK_RUG_ORANGE = registerRug("orange", Blocks.ORANGE_CARPET, Items.ORANGE_DYE);
    public static final RugShopBlock SHOP_BLOCK_RUG_MAGENTA = registerRug("magenta", Blocks.MAGENTA_CARPET, Items.MAGENTA_DYE);
    public static final RugShopBlock SHOP_BLOCK_RUG_LIGHT_BLUE = registerRug("light_blue", Blocks.LIGHT_BLUE_CARPET, Items.LIGHT_BLUE_DYE);
    public static final RugShopBlock SHOP_BLOCK_RUG_YELLOW = registerRug("yellow", Blocks.YELLOW_CARPET, Items.YELLOW_DYE);
    public static final RugShopBlock SHOP_BLOCK_RUG_LIME = registerRug("lime", Blocks.LIME_CARPET, Items.LIME_DYE);
    public static final RugShopBlock SHOP_BLOCK_RUG_PINK = registerRug("pink", Blocks.PINK_CARPET, Items.PINK_DYE);
    public static final RugShopBlock SHOP_BLOCK_RUG_GRAY = registerRug("gray", Blocks.GRAY_CARPET, Items.GRAY_DYE);
    public static final RugShopBlock SHOP_BLOCK_RUG_LIGHT_GRAY = registerRug("light_gray", Blocks.LIGHT_GRAY_CARPET, Items.LIGHT_GRAY_DYE);
    public static final RugShopBlock SHOP_BLOCK_RUG_CYAN = registerRug("cyan", Blocks.CYAN_CARPET, Items.CYAN_DYE);
    public static final RugShopBlock SHOP_BLOCK_RUG_PURPLE = registerRug("purple", Blocks.PURPLE_CARPET, Items.PURPLE_DYE);
    public static final RugShopBlock SHOP_BLOCK_RUG_BLUE = registerRug("blue", Blocks.BLUE_CARPET, Items.BLUE_DYE);
    public static final RugShopBlock SHOP_BLOCK_RUG_BROWN = registerRug("brown", Blocks.BROWN_CARPET, Items.BROWN_DYE);
    public static final RugShopBlock SHOP_BLOCK_RUG_GREEN = registerRug("green", Blocks.GREEN_CARPET, Items.GREEN_DYE);
    public static final RugShopBlock SHOP_BLOCK_RUG_BLACK = registerRug("black", Blocks.BLACK_CARPET, Items.BLACK_DYE);


    private static RugShopBlock registerRug(String colour, Block carpet, Item dye){

        RugShopBlock rug = registerBasic("rug_shop_"+colour,new RugShopBlock(carpet,colour));
        ALL_RUG_SHOPS.add(rug);

        net.spudacious5705.shops.block.VariantResources.RUGS_CARPET.put(carpet.asItem(),rug);
        net.spudacious5705.shops.block.VariantResources.RUGS_DYE.put(dye,rug);

        return rug;
    }

    private static RugShopBlock registerRugLegacy(){
        String name = "rug_shop";

        RugShopBlock rug = registerBasic(name,new RugShopBlock(Blocks.RED_CARPET, "red"));
        ALL_RUG_SHOPS.add(rug);

        net.spudacious5705.shops.block.VariantResources.RUGS_CARPET.put(Items.RED_CARPET,rug);
        net.spudacious5705.shops.block.VariantResources.RUGS_DYE.put(Items.RED_DYE,rug);

        return rug;
    }
    //endregion

    public static final CrateShopBlock SHOP_BLOCK_CRATE = registerBasic("crate_shop",new CrateShopBlock(settingsWood));

    //region SHELF
    public static final ShelfShopBlock SHOP_BLOCK_SHELF_ACACIA = registerShelf(ACACIA,Blocks.ACACIA_SLAB);
    public static final ShelfShopBlock SHOP_BLOCK_SHELF_BAMBOO = registerShelf(BAMBOO,Blocks.BAMBOO_SLAB);
    public static final ShelfShopBlock SHOP_BLOCK_SHELF_BIRCH = registerShelf(BIRCH,Blocks.BIRCH_SLAB);
    public static final ShelfShopBlock SHOP_BLOCK_SHELF_CHERRY = registerShelf(CHERRY,Blocks.CHERRY_SLAB);
    public static final ShelfShopBlock SHOP_BLOCK_SHELF_CRIMSON = registerShelf(CRIMSON,Blocks.CRIMSON_SLAB);
    public static final ShelfShopBlock SHOP_BLOCK_SHELF_DARK_OAK = registerShelf(DARK_OAK,Blocks.DARK_OAK_SLAB);
    public static final ShelfShopBlock SHOP_BLOCK_SHELF_MANGROVE = registerShelf(MANGROVE,Blocks.MANGROVE_SLAB);
    public static final ShelfShopBlock SHOP_BLOCK_SHELF_OAK = registerShelf(OAK,Blocks.OAK_SLAB);
    public static final ShelfShopBlock SHOP_BLOCK_SHELF_SPRUCE = registerShelf(SPRUCE,Blocks.SPRUCE_SLAB);
    public static final ShelfShopBlock SHOP_BLOCK_SHELF_WARPED = registerShelf(WARPED,Blocks.WARPED_SLAB);
    public static final ShelfShopBlock SHOP_BLOCK_SHELF_JUNGLE = registerShelf(JUNGLE,Blocks.JUNGLE_SLAB);


    private static ShelfShopBlock registerShelf(VariantResources.wood_variant variant, Block slab){

        ShelfShopBlock shop = new ShelfShopBlock(settingsWood,slab,variant);

        String name = "shelf_shop_"+variant.name;

        Identifier id = SpudaciousShops.id(name);

        Registry.register(
                Registries.ITEM,
                id,
                new BlockItem(shop, new Item.Settings())
        );

        shop = addToAllShops(
                Registry.register(
                        Registries.BLOCK,
                        id,
                        shop
                )
        );

        ALL_SHELF_SHOPS.add(shop);
        net.spudacious5705.shops.block.VariantResources.SHELF.put(slab.asItem(),shop);
        return shop;
    }
    //endregion


    private static <S extends AbstractShopBlock> S registerBasic(String name, S shop){
        Identifier id = SpudaciousShops.id(name);
        Registry.register(
                Registries.ITEM,
                id,
                new BlockItem(shop, new Item.Settings())
        );

        return addToBasicShops(
                addToAllShops(
                Registry.register(
                        Registries.BLOCK,
                        id,
                        shop
                )
                )
        );
    }

    private static WindowSillShopBlock registerWindowShopBlock(String name, Item stoneType) {
        WindowSillShopBlock shop = registerBasic("shop_window_"+name,new WindowSillShopBlock(settingsStone, stoneType));
        ALL_WINDOW_SHOPS.add(shop);
        net.spudacious5705.shops.block.VariantResources.WINDOW_SILL.put(stoneType,shop);
        return shop;
    }

    private static <S extends AbstractShopBlock> S addToAllShops(S register) {
        ALL_SHOPS.add(register);
        return register;
    }

    private static <S extends AbstractShopBlock> S addToBasicShops(S register) {
        BASIC_SHOPS.add(register);
        return register;
    }

    private static ShopItem registerShopBlockItem(String name, AngledShopBlock block, Colour colour) {
        Identifier id = SpudaciousShops.id(name + "_" + colour.asString());
        return Registry.register(Registries.ITEM, id,
                new ShopItem(block, new Item.Settings(), colour));

    }

    public static void registerModBlocks() {
        SpudaciousShops.LOGGER.info("Registering mod blocks for " + SpudaciousShops.MOD_ID);
    }

    public static List<AbstractShopBlock> getAllShops(){
        return ALL_SHOPS;
    }
}
