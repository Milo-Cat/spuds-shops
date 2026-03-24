package net.spudacious5705.shops.block;


import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.spudacious5705.shops.SpudaciousShops;
import net.spudacious5705.shops.block.custom.AngledShopBlock;
import net.spudacious5705.shops.block.custom.RugShopBlock;
import net.spudacious5705.shops.block.custom.ShelfShopBlock;
import net.spudacious5705.shops.block.custom.WindowSillShopBlock;
import net.spudacious5705.shops.block.entity.*;

import java.util.function.Supplier;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, SpudaciousShops.MOD_ID);

    public static void registerBlockEntities(IEventBus modEventBus) {
        SpudaciousShops.LOGGER.info("Registering block entities for" + SpudaciousShops.MOD_ID);
        BLOCK_ENTITIES.register(modEventBus);
    }    public static final Supplier<BlockEntityType<AngledShopEntity>> ANGLED_SHOP_ENTITY =
            BLOCK_ENTITIES.register("shop_b_e",
                    () -> BlockEntityType.Builder.of(AngledShopEntity::new,
                            ModBlocks.ALL_ORIGINAL_SHOPS.stream().map(Supplier::get).toArray(AngledShopBlock[]::new)
                    ).build(null)
            );

    public static final Supplier<BlockEntityType<WindowSillShopEntity>> WINDOW_SHOP_ENTITY =
            BLOCK_ENTITIES.register("shop_b_e_window_sill",
                    () -> BlockEntityType.Builder.of(WindowSillShopEntity::new,
                            ModBlocks.ALL_WINDOW_SHOPS.stream().map(Supplier::get).toArray(WindowSillShopBlock[]::new)
                    ).build(null)
            );


    public static final Supplier<BlockEntityType<HookShopEntity>> HOOK_SHOP_ENTITY =
            BLOCK_ENTITIES.register("shop_b_e_hook",
                    () -> BlockEntityType.Builder.of(HookShopEntity::new,
                            ModBlocks.SHOP_BLOCK_HOOK.get()
                    ).build(null)
            );

    public static final Supplier<BlockEntityType<RugShopEntity>> RUG_SHOP_ENTITY =
            BLOCK_ENTITIES.register("shop_b_e_rug",
                    () -> BlockEntityType.Builder.of(RugShopEntity::new,
                            ModBlocks.ALL_RUG_SHOPS.stream().map(Supplier::get).toArray(RugShopBlock[]::new)
                    ).build(null)
            );

    public static final Supplier<BlockEntityType<CrateShopEntity>> CRATE_SHOP_ENTITY =
            BLOCK_ENTITIES.register("shop_b_e_crate",
                    () -> BlockEntityType.Builder.of(CrateShopEntity::new,
                            ModBlocks.SHOP_BLOCK_CRATE.get()
                    ).build(null)
            );


    public static final Supplier<BlockEntityType<ShelfShopEntity>> SHELF_SHOP_ENTITY =
            BLOCK_ENTITIES.register("shop_b_e_shelf",
                    () -> BlockEntityType.Builder.of(ShelfShopEntity::new,
                            ModBlocks.ALL_SHELF_SHOPS.stream().map(Supplier::get).toArray(ShelfShopBlock[]::new)
                    ).build(null)
            );



}