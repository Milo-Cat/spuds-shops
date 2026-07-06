package net.spudacious5705.shops.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.ticks.TickPriority;
import net.spudacious5705.shops.block.entity.AbstractShopEntity;
import net.spudacious5705.shops.util.ShopMenuHelper;
import net.spudacious5705.shops.block.resources.VariantResources;
import net.spudacious5705.shops.permission.PermissionLevel;
import net.spudacious5705.shops.properties.ModProperties;
import net.spudacious5705.shops.screen.ScreenSettingsGroup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public abstract class AbstractShopBlock extends Block implements EntityBlock {

    public static final DirectionProperty FACING =
            DirectionProperty.create("facing", Direction.Plane.HORIZONTAL);

    public static final BooleanProperty BREAKABLE = ModProperties.BREAKABLE;
    private static final VoxelShape TEST_SHAPE = Block.box(0, 0, 0, 16, 10, 16);

    public AbstractShopBlock(BlockBehaviour.Properties properties) {
        super(properties.forceSolidOn());
        registerDefaultStateTemplate();
    }

    public static VoxelShape createCuboidShape(
            double x1, double y1, double z1,
            double x2, double y2, double z2
    ) {
        return Block.box(x1, y1, z1, x2, y2, z2);
    }

    protected static PermissionLevel userSignIn(Level world, BlockPos pos, Player player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof AbstractShopEntity shopEntity) {
            return shopEntity.userSignIn(player);
        }
        return PermissionLevel.CUSTOMER;
    }

    protected void registerDefaultStateTemplate() {
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(BREAKABLE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, BREAKABLE);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState()
                .setValue(FACING, ctx.getHorizontalDirection().getOpposite())
                .setValue(BREAKABLE, false);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void setPlacedBy(
            @NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState state,
            @Nullable LivingEntity placer, @NotNull ItemStack stack
    ) {
        if (placer != null) {
            if (placer instanceof Player player) {
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity instanceof AbstractShopEntity shopEntity) {
                    shopEntity.userSignIn(player);
                }
            }
        }
        super.setPlacedBy(world, pos, state, placer, stack);
    }

    @Override
    public abstract @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state);

    public TagKey<Block> getPreferredTool() {
        return BlockTags.MINEABLE_WITH_AXE;
    }

    @Override
    public void attack(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player) {
        if (!(level.getBlockEntity(pos) instanceof AbstractShopEntity shop)) return;

        if (shop.isUnbreakable(player)) {
            level.setBlock(pos, state.setValue(BREAKABLE, false), 3);
            if (level.isClientSide) {
                player.displayClientMessage(shop.cantBreakMessage(), true);
            }
            return;
        }

        level.setBlock(pos, state.setValue(BREAKABLE, true), 3);
        level.scheduleTick(pos, this, 140, TickPriority.EXTREMELY_HIGH);

        super.attack(state, level, pos, player);
    }

    @Override
    public float getDestroyProgress(
            BlockState state, @NotNull Player player,
            @NotNull BlockGetter level, @NotNull BlockPos pos
    ) {
        return state.getValue(BREAKABLE)
                ? super.getDestroyProgress(state, player, level, pos)
                : 0.0F;
    }

    @Override
    public void tick(
            BlockState pState, ServerLevel pLevel,
            @NotNull BlockPos pPos, @NotNull RandomSource pRandom
    ) {
        pLevel.setBlock(pPos, pState.setValue(BREAKABLE, false), 3);
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(
            ItemStack stack, @NotNull BlockState state,
            @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player,
            @NotNull InteractionHand hand, @NotNull BlockHitResult hit
    ) {
        if (stack.isEmpty()) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        PermissionLevel perm = userSignIn(level, pos, player);
        if (perm.canEditTrades()) {
            if (onUseWithItem(stack, state, level, pos, player)) return ItemInteractionResult.SUCCESS;
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(
            @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
            @NotNull Player player, @NotNull BlockHitResult hitResult
    ) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        BlockEntity be = level.getBlockEntity(pos);

        if (!(be instanceof AbstractShopEntity shop)) return InteractionResult.FAIL;


        if (player instanceof ServerPlayer serverPlayer) {
            ShopMenuHelper.openShopMenu(serverPlayer, shop, pos, false);
        }


        return InteractionResult.SUCCESS;
    }

    protected final <T extends Comparable<T>> @NotNull BlockState copyValues(
            @NotNull BlockState subject, @NotNull BlockState source,
            @NotNull Property<?>... properties
    ) {
        for (Property<?> prop : properties) {
            Property<T> p = ((Property<T>) prop);
            if (source.hasProperty(p)) {
                subject = subject.setValue(p, source.getValue(p));
            }
        }
        return subject;
    }

    protected boolean shouldOpenTop(BlockHitResult hit) {
        return false;
    }

    @Override
    public void onRemove(
            @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
            @NotNull BlockState newState, boolean isMoving
    ) {
        if (isStateReplacedValid(newState)) {
            return;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof AbstractShopEntity shopEntity) {
            if (!state.getValue(BREAKABLE)) {
                level.setBlock(pos, state, 3);
                return;
            }
            shopEntity.itemScatter(level, pos);
            level.updateNeighbourForOutputSignal(pos, this);
        }
        level.removeBlockEntity(pos);
        super.onRemove(state, level, pos, newState, isMoving);
    }

    public @NotNull BlockState rotate(
            @NotNull BlockState state, @NotNull Rotation direction
    ) {
        return state.setValue(FACING, direction.rotate(state.getValue(FACING)));
    }

    public @NotNull BlockState mirror(@NotNull BlockState pState, @NotNull Mirror pMirror) {
        return rotate(pState, pMirror.getRotation(pState.getValue(FACING)));
    }

    @Override
    public @NotNull VoxelShape getShape(
            @NotNull BlockState state, @NotNull BlockGetter pLevel,
            @NotNull BlockPos pPos, @NotNull CollisionContext pContext
    ) {
        return getGenericShape(state);
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(
            @NotNull BlockState state, @NotNull BlockGetter level,
            @NotNull BlockPos pos, @NotNull CollisionContext context
    ) {
        return getGenericShape(state);
    }

    @Override
    public @NotNull VoxelShape getOcclusionShape(
            @NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos
    ) {
        return getGenericShape(state);
    }

    protected VoxelShape getGenericShape(BlockState state) {
        return TEST_SHAPE;
    }


    protected abstract boolean isStateReplacedValid(BlockState newState);

    protected abstract boolean onUseWithItem(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player);


    @Override
    public abstract <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            @NotNull Level world, @NotNull BlockState state, @NotNull BlockEntityType<T> type
    );

    @Override
    public @NotNull BlockState playerWillDestroy(
            @NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState state,
            @NotNull Player player
    ) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof AbstractShopEntity shop) {
            if (shop.isUnbreakable(player)) {
                if (world.isClientSide()) {
                    player.displayClientMessage(shop.cantBreakMessage(), true);
                }
                return state;
            }
        }

        if (player.isCreative()) {
            world.setBlock(pos, state.setValue(BREAKABLE, true), 3);
        }

        return super.playerWillDestroy(world, pos, state, player);
    }


    public ScreenSettingsGroup getScreenSettings() {
        return ScreenSettingsGroup.createBasicWood(VariantResources.wood_variant.OAK);
    }
}



