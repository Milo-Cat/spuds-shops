package net.spudacious5705.shops.block.custom;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.spudacious5705.shops.block.ModBlockEntities;
import net.spudacious5705.shops.block.entity.HookShopEntity;
import org.jetbrains.annotations.Nullable;


public class HookShopBlock extends AbstractShopBlock{

    public static final VoxelShape SHAPE = createCuboidShape(5, -1.0, 5, 11.0, 16.0, 11.0);

    public HookShopBlock(BlockBehaviour.Properties settings) {
        super(settings);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HookShopEntity(pos,state);
    }


    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if(ModBlockEntities.HOOK_SHOP_ENTITY.get() == type) {
            return level.isClientSide
                    ? (lvl, pos, st, be) -> ((HookShopEntity)be).renderTick()
                    : (lvl, pos, st, be) -> ((HookShopEntity)be).serverTick((ServerLevel) lvl, pos,  st);

        }
        return null;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Level world = ctx.getLevel();

        BlockPos attachedPos = ctx.getClickedPos().relative(Direction.UP);
        BlockState attachedState = world.getBlockState(attachedPos);
        if(!attachedState.isFaceSturdy(world, attachedPos, Direction.DOWN))return null;

        return this.defaultBlockState()
                .setValue(FACING, ctx.getHorizontalDirection().getOpposite())
                .setValue(BREAKABLE, false);
    }



    @Override
    protected boolean isStateReplacedValid(BlockState newShopState) {
        return newShopState.getBlock() instanceof HookShopBlock;
    }

    @Override
    protected boolean onUseWithItem(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player) {
        return false;
    }

    @Override
    public VoxelShape getGenericShape(BlockState state) {
        return SHAPE;
    }

    @Override
    public TagKey<Block> getPreferredTool() {
        return BlockTags.MINEABLE_WITH_PICKAXE;
    }
}
