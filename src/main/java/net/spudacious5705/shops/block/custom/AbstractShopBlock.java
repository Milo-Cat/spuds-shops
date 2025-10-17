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
import net.minecraftforge.network.NetworkHooks;
import net.spudacious5705.shops.block.VariantResources;
import net.spudacious5705.shops.block.entity.AbstractShopEntity;
import net.spudacious5705.shops.properties.ModProperties;
import net.spudacious5705.shops.properties.PermissionLevel;
import net.spudacious5705.shops.screen.ScreenSettingsGroup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public abstract class AbstractShopBlock extends Block implements EntityBlock {

    public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.Plane.HORIZONTAL);
    public static final BooleanProperty BREAKABLE = ModProperties.BREAKABLE;




    public AbstractShopBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultStateTemplate();
    }

    protected void registerDefaultStateTemplate(){
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
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    public static VoxelShape createCuboidShape(double x1, double y1, double z1, double x2, double y2, double z2){
        return Block.box(x1 , y1 , z1 , x2 , y2 , z2 );
    }


    @Override
    public void setPlacedBy(@NotNull Level world, @NotNull BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
      if(placer != null) {
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
    public abstract @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state);

    protected static PermissionLevel userSignIn(Level world, BlockPos pos, Player player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof AbstractShopEntity shopEntity) {
            return shopEntity.userSignIn(player);
        }
        return PermissionLevel.CUSTOMER;
    }

    public TagKey<Block> getPreferredTool() {
        return BlockTags.MINEABLE_WITH_AXE;
    }

    @Override
    public void attack(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player) {
        if (!(level.getBlockEntity(pos) instanceof AbstractShopEntity shop)) return;

        if (!shop.canBreak(player)) {
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
    public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        return state.getValue(BREAKABLE)
                ? super.getDestroyProgress(state, player, level, pos)
                : 0.0F;
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        pLevel.setBlock(pPos, pState.setValue(BREAKABLE, false), 3);
    }

    @Override
    public @NotNull InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
      if (level.isClientSide()) return InteractionResult.SUCCESS;

        ItemStack stack = player.getItemInHand(hand);
        BlockEntity be = level.getBlockEntity(pos);

        if (!(be instanceof AbstractShopEntity shop)) return InteractionResult.FAIL;

        PermissionLevel perm = userSignIn(level, pos, player);

        if (!stack.isEmpty() && perm.canEditTrades()) {
            if (onUseWithItem(stack, state, level, pos, player)) return InteractionResult.SUCCESS;
        }


        if(player instanceof ServerPlayer serverPlayer) {
            NetworkHooks.openScreen(serverPlayer, shop.createScreenHandlerFactory(false), buf -> {
                buf.writeBlockPos(pos);//friendlyByteBuff formation here
                buf.writeBoolean(false);//ignore this false value. its meant to be there
            });
        }



        return InteractionResult.SUCCESS;
    }

    @SafeVarargs
    protected final <T extends Comparable<T>> @NotNull BlockState copyValues(@NotNull BlockState subject, @NotNull BlockState source, @NotNull Property<?>... properties){
        for (Property<?> prop : properties) {
            Property<T> p = ((Property<T>) prop);
            if (source.hasProperty(p)) {
                subject = subject.setValue(p, source.getValue(p));
            }
        }
        return subject;
    }

    protected boolean shouldOpenTop(BlockHitResult hit){
        return false;
    }


    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (isStateReplacedValid(newState)){
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

    @Override
    public BlockState rotate(BlockState state, LevelAccessor level, BlockPos pos, Rotation direction) {
        return state.setValue(FACING, direction.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return rotate(pState, pMirror.getRotation(pState.getValue(FACING)));
    }


    @Override
    public VoxelShape getShape(BlockState state, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext){return getGenericShape(state);}
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context){return getGenericShape(state);}
    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos){return getGenericShape(state);}

    private static final VoxelShape TEST_SHAPE = Block.box(0, 0, 0, 16, 10, 16);

    protected VoxelShape getGenericShape(BlockState state){return TEST_SHAPE;}


    protected boolean unbreakable(BlockState state) {
        return !state.getValue(BREAKABLE);
    }

    protected void makeBreakable(ServerLevel level, BlockPos pos, BlockState state) {
        level.setBlock(pos, state.setValue(BREAKABLE, true), 3);
    }

    protected void makeUnbreakable(ServerLevel level, BlockPos pos, BlockState state) {
        level.setBlock(pos, state.setValue(BREAKABLE, false), 3);
    }


    protected abstract boolean isStateReplacedValid(BlockState newState);

    protected abstract boolean onUseWithItem(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player);


    @Override
    public abstract<T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type);

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level world, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof AbstractShopEntity shop) {
            if (!shop.canBreak(player)) {
                if (world.isClientSide) {
                    player.displayClientMessage(shop.cantBreakMessage(), true);
                }
                return false; // cancel destruction
            }
        }

        if (player.isCreative()) {
            world.setBlock(pos, state.setValue(BREAKABLE, true), 3); // allow creative break
        }

        return super.onDestroyedByPlayer(state, world, pos, player, willHarvest, fluid);
    }


    public ScreenSettingsGroup getScreenSettings(){
        return ScreenSettingsGroup.createBasicWood(VariantResources.wood_variant.OAK);
    }
}



