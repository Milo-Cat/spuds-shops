package net.spudacious5705.shops.block.entity;


import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.spudacious5705.shops.block.ModBlockEntities;

public class WindowSillShopEntity extends AbstractShopEntity{

    public WindowSillShopEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WINDOW_SHOP_ENTITY.get(), pos, state, -0.3f);
    }


    @Override
    public int getTextureId() {
        return 0;
    }

}
