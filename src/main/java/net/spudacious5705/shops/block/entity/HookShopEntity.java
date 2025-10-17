package net.spudacious5705.shops.block.entity;


import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.spudacious5705.shops.block.ModBlockEntities;

public class HookShopEntity extends AbstractShopEntity{

    public HookShopEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HOOK_SHOP_ENTITY.get(), pos, state, -2.1f);
    }

    @Override
    public int getTextureId() {
        return 0;
    }


}
