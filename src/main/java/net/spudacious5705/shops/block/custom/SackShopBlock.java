package net.spudacious5705.shops.block.custom;


import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.spudacious5705.shops.block.ModBlockEntities;
import net.spudacious5705.shops.block.entity.RugShopEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.world.level.block.state.BlockBehaviour;


/**
 * Sack-style shop block with open/closed states.
 *
 * This block stores an open flag and updates collision bounds accordingly.
 */
public class SackShopBlock extends AbstractShopBlock {

    public static final VoxelShape BASE_SHAPE = createCuboidShape(0, 0, 0, 16, 14, 16);

    public static final VoxelShape CLOSED_SHAPE = Shapes.or(
            createCuboidShape(5.0, 14.0, 5.0, 11.0, 12.0, 11.0),
            BASE_SHAPE
    );

    public static final VoxelShape OPEN_SHAPE = Shapes.or(
            createCuboidShape(3.0, 14.0, 3.0, 17.0, 13.0, 13.0),
            BASE_SHAPE
    );

    public static final BooleanProperty OPEN = BooleanProperty.create("open");

    public SackShopBlock(BlockBehaviour.Properties properties) {
        super(properties);

    }

    @Override
    protected void registerDefaultStateTemplate() {
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(OPEN, false)
                .setValue(BREAKABLE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(
                OPEN,
                BREAKABLE
        );
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new RugShopEntity(pos, state);
    }

    @Override
    protected boolean onUseWithItem(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player) {
        return false;
    }


    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        if (ModBlockEntities.RUG_SHOP_ENTITY.get() == type) {
            return level.isClientSide()
                    ? (lvl, pos, st, be) -> ((RugShopEntity) be).renderTick()
                    : (lvl, pos, st, be) -> ((RugShopEntity) be).serverTick((ServerLevel) lvl, pos, st);

        }
        return null;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState()
                .setValue(BREAKABLE, false)
                .setValue(OPEN, false);
    }


    @Override
    public @NotNull BlockState rotate(BlockState state, @NotNull LevelAccessor level, @NotNull BlockPos pos, Rotation direction) {
        return state;
    }

    @Override
    public @NotNull BlockState mirror(@NotNull BlockState pState, Mirror pMirror) {
        return pState;
    }

    @Override
    protected boolean isStateReplacedValid(BlockState newShopState) {
        return newShopState.getBlock() instanceof SackShopBlock;
    }

    @Override
    protected VoxelShape getGenericShape(BlockState state) {
        return BASE_SHAPE;
    }

    @Override
    public @NotNull VoxelShape getOcclusionShape(@NotNull BlockState state) {
        return BASE_SHAPE;
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return state.getValue(OPEN) ? OPEN_SHAPE : CLOSED_SHAPE;
    }

    @Override
    public TagKey<Block> getPreferredTool() {
        return BlockTags.MINEABLE_WITH_HOE;
    }
}
