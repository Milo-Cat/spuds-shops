package net.spudacious5705.shops.block.custom;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import net.spudacious5705.shops.block.ModBlockEntities;
import net.spudacious5705.shops.block.PostRegAssigner;
import net.spudacious5705.shops.block.VariantResources;
import net.spudacious5705.shops.block.entity.AbstractShopEntity;
import net.spudacious5705.shops.block.entity.ShelfShopEntity;
import net.spudacious5705.shops.properties.PermissionLevel;
import net.spudacious5705.shops.screen.ScreenSettingsGroup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Comparator;

public class ShelfShopBlock extends AbstractShopBlock{

    public static final VoxelShape CULLING_SHAPE = createCuboidShape(2, 0, 2, 14.0, 14.0, 14.0);

    public static final VoxelShape[] SHAPES_TOP = {
            createCuboidShape(0.5, 8.0, 8.0, 15.5, 14.0, 16.0),//NORTH
            createCuboidShape(8.0, 8.0, 0.5, 16.0, 14.0, 15.5),//WEST
            createCuboidShape(0.5, 8.0, 0.0, 15.5, 14.0, 8.0),//SOUTH
            createCuboidShape(0.0, 8.0, 0.5, 8.0, 14.0, 15.5)};//EAST

    public static final VoxelShape[] SHAPES_BOTTOM = {
            createCuboidShape(0.5, 0.0, 8.0, 15.5, 6.0, 16.0),//NORTH
            createCuboidShape(8.0, 0.0, 0.5, 16.0, 6.0, 15.5),//WEST
            createCuboidShape(0.5, 0.0, 0.0, 15.5, 6.0, 8.0),//SOUTH
            createCuboidShape(0.0, 0.0, 0.5, 8.0, 6.0, 15.5)};//EAST

    private static VoxelShape[] initShapes_double(){
        VoxelShape[] SHAPES = new VoxelShape[4];
        for(int i = 0; i<4; i++){
            SHAPES[i] = Shapes.or(SHAPES_TOP[i],SHAPES_BOTTOM[i]);
        }
        return SHAPES;
    }

    public static final VoxelShape[] SHAPES_DOUBLE = initShapes_double();

    public static final EnumProperty<SlabType> SHELVES_ENABLED = EnumProperty.create("type", SlabType.class);

    public Block SlabWoodType;
    public final VariantResources.wood_variant VARIANT;

    public ShelfShopBlock(Properties properties, PostRegAssigner<Block> slabAssigner, VariantResources.wood_variant variant) {
        super(properties);
        slabAssigner.assignTo(o -> SlabWoodType = o);
        this.VARIANT = variant;
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, BREAKABLE, SHELVES_ENABLED);
    }

    public String getWoodName(){
        return VARIANT.name;
    }

    @Override
    public ScreenSettingsGroup getScreenSettings() {
        return ScreenSettingsGroup.createBasicWood(VARIANT);
    }

    protected void registerDefaultStateTemplate(){
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(BREAKABLE, false)
                .setValue(SHELVES_ENABLED, SlabType.DOUBLE)
        );
    }


    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockPos pos = ctx.getClickedPos();
        BlockState blockState = ctx.getLevel().getBlockState(pos);
        Level world = ctx.getLevel();

        // Scenario: adding a shelf to an existing block
        if (ctx.getItemInHand().is(blockState.getBlock().asItem())) {
            return blockState.setValue(SHELVES_ENABLED, SlabType.DOUBLE);
        }

        // Determine best horizontal wall to attach to
        Direction finalDirection = null;
        Player player = ctx.getPlayer();
        Direction[] horizontalDirections = {Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};

        if (player != null) {
            Vec3 lookVec = player.getLookAngle();
            Arrays.sort(horizontalDirections, Comparator.comparingDouble(dir -> -lookVec.dot(Vec3.atLowerCornerOf(dir.getNormal()))));
        }

        for (Direction direction : horizontalDirections) {
            BlockPos facingBlockPos = pos.relative(direction);
            if (world.getBlockState(facingBlockPos).isFaceSturdy(world, facingBlockPos, direction.getOpposite())) {
                finalDirection = direction.getOpposite(); // attach to this wall
                break;
            }
        }

        // Reject placement if no valid wall found
        if (finalDirection == null) return null;

        // Determine slab type based on click height
        SlabType type = (ctx.getClickLocation().y - pos.getY()) > 0.5 ? SlabType.TOP : SlabType.BOTTOM;

        return this.defaultBlockState()
                .setValue(FACING, finalDirection)
                .setValue(BREAKABLE, false)
                .setValue(SHELVES_ENABLED, type);
    }


    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ShelfShopEntity(pos,state);
    }

    @Override
    protected boolean onUseWithItem(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player) {
        Item item = stack.getItem();
        if(item != this.SlabWoodType.asItem()) {
            if(world.getBlockEntity(pos) instanceof ShelfShopEntity shopEntity) {
                if (VariantResources.SHELF.containsKey(item)) {
                    ShelfShopBlock newShelf = net.spudacious5705.shops.block.VariantResources.SHELF.get(item);


                    if (!player.isCreative()) {

                        int i = state.getValue(SHELVES_ENABLED) == SlabType.DOUBLE ? 2 : 1;

                        if (stack.getCount() < i) return false;
                        stack.shrink(i);

                        ItemStack dropStack = SlabWoodType.asItem().getDefaultInstance();

                        dropStack.setCount(i);

                        world.addFreshEntity(new ItemEntity(world, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, dropStack, 0f, 0.1f, 0f));
                    }


                    world.setBlockAndUpdate(pos, copyValues(newShelf.defaultBlockState(), state, FACING, SHELVES_ENABLED));
                    shopEntity.forceUpdateClient();
                    return true;


                }
            }
        }

        return false;
    }



    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if(ModBlockEntities.SHELF_SHOP_ENTITY.get() == type) {
            return level.isClientSide
                    ? (lvl, pos, st, be) -> ((ShelfShopEntity)be).renderTick()
                    : (lvl, pos, st, be) -> ((ShelfShopEntity)be).serverTick((ServerLevel) lvl, pos,  st);

        }
        return null;
    }

   @Override
    public @NotNull VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos){return CULLING_SHAPE;}

    @Override
    protected VoxelShape getGenericShape(BlockState state) {
        int select = switch (state.getValue(FACING)){
            case NORTH -> 0;
            case WEST -> 1;
            case SOUTH -> 2;
            default -> 3;
        };
        return switch(state.getValue(SHELVES_ENABLED)) {
            case TOP -> SHAPES_TOP[select];
            case BOTTOM -> SHAPES_BOTTOM[select];
            default -> SHAPES_DOUBLE[select];
        };
    }


    @Override
    public boolean canBeReplaced(BlockState state, @NotNull BlockPlaceContext context) {
        if(state.getValue(SHELVES_ENABLED) == SlabType.DOUBLE) return false;
        return context.getItemInHand().is(state.getBlock().asItem());
    }

    @Override
    public @NotNull InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;

        ItemStack stack = player.getItemInHand(hand);
        BlockEntity be = level.getBlockEntity(pos);

        if (!(be instanceof AbstractShopEntity shop)) return InteractionResult.FAIL;

        PermissionLevel perm = userSignIn(level, pos, player);

        if (!stack.isEmpty() && perm.canEditTrades()) {
            if (onUseWithItem(stack, state, level, pos, player)) return InteractionResult.SUCCESS;
        }


        if(player instanceof ServerPlayer serverPlayer) {
            NetworkHooks.openScreen(serverPlayer, shop.createScreenHandlerFactory(false), buf -> {
                buf.writeBlockPos(pos);
                buf.writeBoolean(hit.getLocation().y - pos.getY() > 0.5);
            });
        }


        return InteractionResult.SUCCESS;
    }


    @Override
    protected boolean isStateReplacedValid(BlockState newShopState) {
        return newShopState.getBlock() instanceof ShelfShopBlock;
    }

}
