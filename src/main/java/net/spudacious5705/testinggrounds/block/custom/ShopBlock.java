package net.spudacious5705.testinggrounds.block.custom;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.spudacious5705.testinggrounds.block.entity.InscriberBlockEntity;
import net.spudacious5705.testinggrounds.block.entity.ModBlockEntities;
import net.spudacious5705.testinggrounds.block.entity.ShopEntity;
import org.jetbrains.annotations.Nullable;

public class ShopBlock extends BlockWithEntity implements BlockEntityProvider {

    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;

    public static final VoxelShape CULLING_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);


    public static final VoxelShape BASE_NORTH = Block.createCuboidShape(0.0, 0.0, 2.0, 16.0, 6.0, 16.0);
    public static final VoxelShape BASE_EAST = Block.createCuboidShape(0.0, 0.0, 0.0, 14.0, 6.0, 16.0);
    public static final VoxelShape BASE_SOUTH = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 6.0, 14.0);
    public static final VoxelShape BASE_WEST = Block.createCuboidShape(2.0, 0.0, 0.0, 16.0, 6.0, 16.0);

    public static final VoxelShape NORTH_SHAPE = VoxelShapes.union(
            Block.createCuboidShape(1.0, 6.0, 3.0, 15.0, 9.0, 12.0),
            Block.createCuboidShape(1.0, 9.0, 8.0, 15.0, 11.5, 15.0),
            BASE_NORTH
    );
    public static final VoxelShape EAST_SHAPE = VoxelShapes.union(
            Block.createCuboidShape(4.0, 6.0, 1.0, 13.0, 9.0, 15.0),
            Block.createCuboidShape(1.0, 9, 1.0, 8.0, 11.5, 15.0),
            BASE_EAST
    );
    public static final VoxelShape SOUTH_SHAPE = VoxelShapes.union(
            Block.createCuboidShape(1.0, 6.0, 4.0, 15.0, 9.0, 13.0),
            Block.createCuboidShape(1.0, 9, 1.0, 15.0, 11.5, 8.0),
            BASE_SOUTH
    );
    public static final VoxelShape WEST_SHAPE = VoxelShapes.union(
            Block.createCuboidShape(3.0, 6.0, 1.0, 12.0, 9.0, 15),
            Block.createCuboidShape(8.0, 9.0, 1.0, 15, 11.5, 15),
            BASE_WEST
    );


    public ShopBlock(Settings settings) {
        super(settings);
        this.setDefaultState(
                this.stateManager.getDefaultState().with(FACING, Direction.NORTH)
        );
    }

    public Direction getFacing(BlockState state) {
        return state.get(FACING);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch ((Direction) state.get(FACING)) {
            case NORTH:
                return NORTH_SHAPE;
            case SOUTH:
                return SOUTH_SHAPE;
            case EAST:
                return EAST_SHAPE;
            case WEST:
                return WEST_SHAPE;
            default:
                return CULLING_SHAPE;
        }
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch ((Direction) state.get(FACING)) {
            case NORTH:
                return BASE_NORTH;
            case SOUTH:
                return BASE_SOUTH;
            case EAST:
                return BASE_EAST;
            case WEST:
                return BASE_WEST;
            default:
                return CULLING_SHAPE;
        }
    }

    @Override
    public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return CULLING_SHAPE;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (placer.isPlayer()) {
            PlayerEntity player = (PlayerEntity) placer;
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof ShopEntity) {
                ShopEntity shopEntity = (ShopEntity) blockEntity;

            }
        }
        super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ShopEntity(pos, state);
    }

    private void setOwner(World world, BlockPos pos, PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ShopEntity) {
            ShopEntity shopEntity = (ShopEntity) blockEntity;
            shopEntity.setOwnerID(player.getUuid());
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {

        if (world.isClient) return ActionResult.SUCCESS;

        setOwner(world, pos, player);

        NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);
        if (screenHandlerFactory != null) {
            player.openHandledScreen(screenHandlerFactory);
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof ShopEntity) {
                ((ShopEntity) blockEntity).clearFunctionalSlots();
                ItemScatterer.spawn(world, pos, (ShopEntity) blockEntity);
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.SHOP_ENTITY,
                (world1, pos, state1, blockEntity) -> blockEntity.tick(world1,pos,state1));
    }

}


