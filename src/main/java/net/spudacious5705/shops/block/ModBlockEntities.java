package net.spudacious5705.shops.block;


import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.spudacious5705.shops.SpudaciousShops;
import net.spudacious5705.shops.block.custom.RugShopBlock;
import net.spudacious5705.shops.block.custom.ShelfShopBlock;
import net.spudacious5705.shops.block.entity.*;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, SpudaciousShops.MOD_ID);


    public static final RegistryObject<BlockEntityType<AngledShopEntity>> ANGLED_SHOP_ENTITY =
            BLOCK_ENTITIES.register("shop_b_e",
                    () -> BlockEntityType.Builder.of(AngledShopEntity::new,
                            ModBlocks.SHOP_BLOCK_ANGLED_OAK.get(),
                            ModBlocks.SHOP_BLOCK_ANGLED_BAMBOO.get(),
                            ModBlocks.SHOP_BLOCK_ANGLED_BIRCH.get(),
                            ModBlocks.SHOP_BLOCK_ANGLED_ACACIA.get(),
                            ModBlocks.SHOP_BLOCK_ANGLED_CRIMSON.get(),
                            ModBlocks.SHOP_BLOCK_ANGLED_CHERRY.get(),
                            ModBlocks.SHOP_BLOCK_ANGLED_DARK_OAK.get(),
                            ModBlocks.SHOP_BLOCK_ANGLED_MANGROVE.get(),
                            ModBlocks.SHOP_BLOCK_ANGLED_SPRUCE.get(),
                            ModBlocks.SHOP_BLOCK_ANGLED_WARPED.get(),
                            ModBlocks.SHOP_BLOCK_ANGLED_JUNGLE.get()
                    ).build(null)
            );

    public static final RegistryObject<BlockEntityType<WindowSillShopEntity>> WINDOW_SHOP_ENTITY =
            BLOCK_ENTITIES.register("shop_b_e_window_sill",
                    () -> BlockEntityType.Builder.of(WindowSillShopEntity::new,
                            ModBlocks.SHOP_BLOCK_WINDOW_CALCITE.get(),
                            ModBlocks.SHOP_BLOCK_WINDOW_ANDESITE.get()
                    ).build(null)
            );


    public static final RegistryObject<BlockEntityType<HookShopEntity>> HOOK_SHOP_ENTITY =
            BLOCK_ENTITIES.register("shop_b_e_hook",
                    () -> BlockEntityType.Builder.of(HookShopEntity::new,
                            ModBlocks.SHOP_BLOCK_HOOK.get()
                    ).build(null)
            );

    public static final RegistryObject<BlockEntityType<RugShopEntity>> RUG_SHOP_ENTITY =
            BLOCK_ENTITIES.register("shop_b_e_rug",
                    () -> BlockEntityType.Builder.of(RugShopEntity::new,
                            ModBlocks.ALL_RUG_SHOPS.stream().map(RegistryObject::get).toArray(RugShopBlock[]::new)
                    ).build(null)
            );

    public static final RegistryObject<BlockEntityType<CrateShopEntity>> CRATE_SHOP_ENTITY =
            BLOCK_ENTITIES.register("shop_b_e_crate",
                    () -> BlockEntityType.Builder.of(CrateShopEntity::new,
                            ModBlocks.SHOP_BLOCK_CRATE.get()
                    ).build(null)
            );


    public static final RegistryObject<BlockEntityType<ShelfShopEntity>> SHELF_SHOP_ENTITY =
            BLOCK_ENTITIES.register("shop_b_e_shelf",
                    () -> BlockEntityType.Builder.of(ShelfShopEntity::new,
                            ModBlocks.ALL_SHELF_SHOPS.stream().map(RegistryObject::get).toArray(ShelfShopBlock[]::new)
                    ).build(null)
            );



    public static void registerBlockEntities(IEventBus modEventBus) {
        SpudaciousShops.LOGGER.info("Registering block entities for" + SpudaciousShops.MOD_ID);
        BLOCK_ENTITIES.register(modEventBus);
        AbstractShopEntity.initialiseStaticMethods();
    }
}