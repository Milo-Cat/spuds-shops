package net.spudacious5705.testinggrounds.block.entity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.spudacious5705.testinggrounds.TestingGrounds;
import net.spudacious5705.testinggrounds.block.ModBlocks;

public class ModBlockEntities {
    public static final BlockEntityType<StationBlockEntity> STATION_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE,new Identifier("station_b_e"),
            FabricBlockEntityTypeBuilder.create(StationBlockEntity::new,
                    ModBlocks.STATION).build());

    public static final BlockEntityType<StorageBlockEntity> STORAGE_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE,new Identifier("storage_b_e"),
                    FabricBlockEntityTypeBuilder.create(StorageBlockEntity::new,
                            ModBlocks.STORAGE_BLOCK).build());

    public static final BlockEntityType<InscriberBlockEntity> INSCRIBER_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(TestingGrounds.MOD_ID, "inscriber_b_e"),
                    FabricBlockEntityTypeBuilder.create(InscriberBlockEntity::new,
                            ModBlocks.INSCRIBER_BLOCK).build());

    public static final BlockEntityType<InscriberBlockEntity> MULTI_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(TestingGrounds.MOD_ID, "multiblock_e"),
                    FabricBlockEntityTypeBuilder.create(InscriberBlockEntity::new,
                            ModBlocks.MULTI_BLOCK).build());

    public static final BlockEntityType<ShopEntity> SHOP_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(TestingGrounds.MOD_ID, "shop_b_e"),
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
        TestingGrounds.LOGGER.info("Registering block entities for" + TestingGrounds.MOD_ID);
    }
}
