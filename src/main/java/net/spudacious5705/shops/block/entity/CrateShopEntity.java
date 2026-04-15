package net.spudacious5705.shops.block.entity;


import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.spudacious5705.shops.block.ModBlockEntities;

/**
 * Block entity for the crate shop variant.
 *
 * <p>This implementation only sets up the crate shop entity with its model offset.
 */
public class CrateShopEntity extends AbstractShopEntity {

    public CrateShopEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CRATE_SHOP_ENTITY.get(), pos, state, 0f);
    }

}
