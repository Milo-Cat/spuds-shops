package net.spudacious5705.shops.block.entity;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.spudacious5705.shops.SpudaciousShops;
import net.spudacious5705.shops.block.ModBlocks;
import net.spudacious5705.shops.block.custom.RugShopBlock;
import net.spudacious5705.shops.block.custom.ShelfShopBlock;

public class ModBlockEntities {

    public static final BlockEntityType<AngledShopEntity> ANGLED_SHOP_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, SpudaciousShops.id("shop_b_e"),
                    BlockEntityType.Builder.create(AngledShopEntity::new,
                            ModBlocks.SHOP_BLOCK_ANGLED_OAK,
                            ModBlocks.SHOP_BLOCK_ANGLED_BAMBOO,
                            ModBlocks.SHOP_BLOCK_ANGLED_BIRCH,
                            ModBlocks.SHOP_BLOCK_ANGLED_ACACIA,
                            ModBlocks.SHOP_BLOCK_ANGLED_CRIMSON,
                            ModBlocks.SHOP_BLOCK_ANGLED_CHERRY,
                            ModBlocks.SHOP_BLOCK_ANGLED_DARK_OAK,
                            ModBlocks.SHOP_BLOCK_ANGLED_MANGROVE,
                            ModBlocks.SHOP_BLOCK_ANGLED_SPRUCE,
                            ModBlocks.SHOP_BLOCK_ANGLED_WARPED,
                            ModBlocks.SHOP_BLOCK_ANGLED_JUNGLE
                    ).build());


    public static final BlockEntityType<WindowSillShopEntity> WINDOW_SHOP_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, SpudaciousShops.id("shop_b_e_window_sill"),
                    BlockEntityType.Builder.create(WindowSillShopEntity::new,
                            ModBlocks.SHOP_BLOCK_WINDOW_CALCITE,
                            ModBlocks.SHOP_BLOCK_WINDOW_ANDESITE
                    ).build());

    public static final BlockEntityType<HookShopEntity> HOOK_SHOP_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, SpudaciousShops.id("shop_b_e_hook"),
                    BlockEntityType.Builder.create(HookShopEntity::new,
                            ModBlocks.SHOP_BLOCK_HOOK
                    ).build());

    public static final BlockEntityType<RugShopEntity> RUG_SHOP_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, SpudaciousShops.id("shop_b_e_rug"),
                    BlockEntityType.Builder.create(RugShopEntity::new,
                            ModBlocks.ALL_RUG_SHOPS.toArray(new RugShopBlock[0])
                    ).build());

    public static final BlockEntityType<CrateShopEntity> CRATE_SHOP_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, SpudaciousShops.id("shop_b_e_crate"),
                    BlockEntityType.Builder.create(CrateShopEntity::new,
                            ModBlocks.SHOP_BLOCK_CRATE
                    ).build());

    public static final BlockEntityType<ShelfShopEntity> SHELF_SHOP_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, SpudaciousShops.id("shop_b_e_shelf"),
                    BlockEntityType.Builder.create(ShelfShopEntity::new,
                            ModBlocks.ALL_SHELF_SHOPS.toArray(new ShelfShopBlock[0])
                    ).build());


    public static void registerBlockEntities() {
        SpudaciousShops.LOGGER.info("Registering block entities for" + SpudaciousShops.MOD_ID);
        AbstractShopEntity.initialiseStaticMethods();
    }
}