package net.spudacious5705.shops.block.custom;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.tick.TickPriority;
import net.spudacious5705.shops.block.VariantResources;
import net.spudacious5705.shops.block.entity.AbstractShopEntity;
import net.spudacious5705.shops.properties.ModProperties;
import net.spudacious5705.shops.properties.PermissionLevel;
import net.spudacious5705.shops.screen.ModScreenHandlers;
import net.spudacious5705.shops.screen.ScreenSettingsGroup;
import net.spudacious5705.shops.screenNetworking.ShopScreenPayload;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;


public abstract class AbstractShopBlock extends Block implements BlockEntityProvider{

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty BREAKABLE = ModProperties.BREAKABLE;


    protected final StateManager<Block, BlockState> shopStateManager;



    public AbstractShopBlock(Settings settings, StateManager.Factory<Block, BlockState> shopBlockStateFactory) {
        super(settings);
        StateManager.Builder<Block, BlockState> builder = new StateManager.Builder<>(this);
        builder.add(BREAKABLE);
        this.appendProperties(builder);

        this.shopStateManager = builder.build(Block::getDefaultState, shopBlockStateFactory);

        this.setDefaultState(
                defaultStateProperties(
                        (AbstractShopBlockState) this.shopStateManager.getDefaultState()
                        .with(BREAKABLE, false)
                )
        );
    }


    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    /**
     * made to add additional block state properties
     * use default (YourShopBlockState) state.with(PROPERTY, VALUE);
     * dont forget to include facing.(or call super)
    */
    protected AbstractShopBlockState defaultStateProperties(AbstractShopBlockState state){
        return (AbstractShopBlockState) state.with(FACING, Direction.SOUTH);
    }

    @Override
    public final StateManager<Block, BlockState> getStateManager() {
        return this.shopStateManager;
    }

    @Override
    protected final ImmutableMap<BlockState, VoxelShape> getShapesForStates(Function<BlockState, VoxelShape> stateToShape) {
        return this.shopStateManager.getStates().stream().collect(ImmutableMap.toImmutableMap(Function.identity(), stateToShape));
    }

    @Override
    public final BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if(placer != null) {
            if (placer instanceof PlayerEntity player) {
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity instanceof AbstractShopEntity shopEntity) {
                    shopEntity.userSignIn(player);
                }
            }
        }
        super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getPlacementState(ctx, this.getDefaultState()
                .with(FACING, ctx.getHorizontalPlayerFacing().getOpposite())
                .with(BREAKABLE, false));
    }


    protected BlockState getPlacementState(ItemPlacementContext ctx, BlockState state){
        return state;
    }

    @Nullable
    @Override
    public abstract BlockEntity createBlockEntity(BlockPos pos, BlockState state);

    protected static PermissionLevel userSignIn(World world, BlockPos pos, PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof AbstractShopEntity shopEntity) {
            return shopEntity.userSignIn(player);
        }
        return PermissionLevel.CUSTOMER;
    }

    public TagKey<Block> getPreferredTool() {
        return BlockTags.AXE_MINEABLE;
    }

    public abstract static class AbstractShopBlockState extends BlockState {

        public AbstractShopBlockState(Block block, Reference2ObjectArrayMap<Property<?>, Comparable<?>> reference2ObjectArrayMap, MapCodec<BlockState> mapCodec) {
            super(block, reference2ObjectArrayMap, mapCodec);
        }

        @Override
        public void onBlockBreakStart(World world, BlockPos pos, PlayerEntity player) {
            if(this.getBlock() instanceof AbstractShopBlock) {
                AbstractShopEntity shop = (AbstractShopEntity) world.getBlockEntity(pos);
                if(shop != null){
                if (shop.isUnbreakable(player)) {
                    world.setBlockState(pos, this.withIfExists(BREAKABLE, false));
                    if (world.isClient()) {
                        player.sendMessage(shop.cantBreakMessage(), true);
                    }
                    return;
                }
                }
                world.setBlockState(pos, this.withIfExists(BREAKABLE, true));
                world.scheduleBlockTick(pos, this.owner, 140, TickPriority.EXTREMELY_HIGH);
            }
        }

        @Override
        public final float getHardness(BlockView world, BlockPos pos) {
            if(this.get(BREAKABLE))return 2.0f;
            return -1f;
        }

        @Override
        public final void scheduledTick(ServerWorld world, BlockPos pos, Random random) {
            world.setBlockState(pos,this.withIfExists(BREAKABLE,false));
        }


        @Override
        public ActionResult onUse(World world, PlayerEntity player, BlockHitResult hit) {

            if (world.isClient) return ActionResult.SUCCESS;

            BlockPos pos = hit.getBlockPos();

            ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);

            BlockEntity be = world.getBlockEntity(pos);

            if(!(be instanceof AbstractShopEntity)) return ActionResult.FAIL;

            PermissionLevel perm = userSignIn(world, pos, player);

            if(!stack.isEmpty() && perm.canEditTrades()){
                if(((AbstractShopBlock)getBlock()).onUseWithItem(stack,this.asBlockState(),world,pos,player)) return ActionResult.SUCCESS;
            }

            if(world.getBlockEntity(pos) instanceof AbstractShopEntity shop){
                ExtendedScreenHandlerFactory<ShopScreenPayload> screenHandlerFactory = shop.createScreenHandlerFactory(false);
            if (screenHandlerFactory != null) {
                player.openHandledScreen(screenHandlerFactory);
            }}

            return ActionResult.SUCCESS;
        }



        @Override
        public abstract VoxelShape getCullingShape(BlockView world, BlockPos pos);

        @Override
        public abstract VoxelShape getOutlineShape(BlockView world, BlockPos pos, ShapeContext context);

        @Override
        public abstract VoxelShape getCollisionShape(BlockView world, BlockPos pos);

        @Override
        public BlockState rotate(BlockRotation rotation) {
            return this.with(FACING, rotation.rotate(this.get(FACING)));
        }

        @Override
        public BlockState mirror(BlockMirror mirror) {
            return this.rotate(mirror.getRotation(this.get(FACING)));
        }

        @Override
        public void onBlockAdded(World world, BlockPos pos, BlockState state, boolean notify) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity != null) {
                if (blockEntity instanceof AbstractShopEntity shopEntity) {
                    shopEntity.markDirty();
                }
            }
            super.onBlockAdded(world, pos, state, notify);
        }

        @Override
        public final void onStateReplaced(World world, BlockPos pos, BlockState newState, boolean moved) {
            if(isStateReplacedValid(newState)){
                return;
            }
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity != null) {
                if (blockEntity instanceof AbstractShopEntity shopEntity) {
                    //final defence
                    if(this.unbreakable()){
                        world.setBlockState(pos,this);
                        return;
                    }
                    shopEntity.itemScatter(world,pos);
                    world.updateComparators(pos, this.getBlock());
                }
            }
            world.removeBlockEntity(pos);
        }

        protected abstract boolean isStateReplacedValid(BlockState newState);

        public final boolean unbreakable() {
            return !this.get(BREAKABLE);
        }

        public final void makeBreakable(ServerWorld world, BlockPos pos) {
            world.setBlockState(pos,this.withIfExists(BREAKABLE,true));
        }

        public final void makeUnbreakable(ServerWorld world, BlockPos pos) {
            world.setBlockState(pos,this.withIfExists(BREAKABLE,false));
        }
    }//end of ShopBlockState

    protected abstract boolean onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player);

    @Override
    public abstract<T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type);

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockEntity be = world.getBlockEntity(pos);
        if(be instanceof AbstractShopEntity shop){
            if(shop.isUnbreakable(player)){
                if(world.isClient()) {
                    player.sendMessage(shop.cantBreakMessage(), true);
                }
                return state;
            }
        }
        if(player.isCreative()){
            world.setBlockState(pos, state.with(BREAKABLE,true));
            //allow creative players to break
        }
        this.spawnBreakParticles(world, player, pos, state);
        world.emitGameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Emitter.of(player, state));
        return state;
    }

    public ScreenSettingsGroup getScreenSettings(){
        return ScreenSettingsGroup.createBasicWood(VariantResources.wood_variant.OAK);
    }
}



