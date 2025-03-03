package net.spudacious5705.shops.block.entity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.spudacious5705.shops.SpudaciousShops;
import net.spudacious5705.shops.block.ModBlocks;

public class ModBlockEntities {

    public static final BlockEntityType<ShopEntity> SHOP_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(SpudaciousShops.MOD_ID, "shop_b_e"),
                    FabricBlockEntityTypeBuilder.create(ShopEntity::new,
                            ModBlocks.SHOP_BLOCK_OAK,
                            ModBlocks.SHOP_BLOCK_BAMBOO,
                            ModBlocks.SHOP_BLOCK_BIRCH,
                            ModBlocks.SHOP_BLOCK_ACACIA,
                            ModBlocks.SHOP_BLOCK_CRIMSON,
                            ModBlocks.SHOP_BLOCK_CHERRY,
                            ModBlocks.SHOP_BLOCK_DARK_OAK,
                            ModBlocks.SHOP_BLOCK_MANGROVE,
                            ModBlocks.SHOP_BLOCK_SPRUCE,
                            ModBlocks.SHOP_BLOCK_WARPED
                            ).build());


    public static void registerBlockEntities() {
        SpudaciousShops.LOGGER.info("Registering block entities for" + SpudaciousShops.MOD_ID);
    }
}
