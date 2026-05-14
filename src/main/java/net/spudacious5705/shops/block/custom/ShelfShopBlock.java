package net.spudacious5705.shops.block.custom;

import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.spudacious5705.shops.block.VariantResources;
import net.spudacious5705.shops.block.entity.AbstractShopEntity;
import net.spudacious5705.shops.block.entity.ShelfShopEntity;
import net.spudacious5705.shops.properties.PermissionLevel;
import net.spudacious5705.shops.screen.ScreenSettingsGroup;
import net.spudacious5705.shops.screenNetworking.ShopScreenPayload;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class ShelfShopBlock extends AbstractShopBlock{

    public static final VoxelShape CULLING_SHAPE = Block.createCuboidShape(2, 0, 2, 14.0, 14.0, 14.0);

    public static final VoxelShape[] SHAPES_TOP = {
            Block.createCuboidShape(0.5, 8.0, 8.0, 15.5, 14.0, 16.0),//NORTH
            Block.createCuboidShape(8.0, 8.0, 0.5, 16.0, 14.0, 15.5),//WEST
            Block.createCuboidShape(0.5, 8.0, 0.0, 15.5, 14.0, 8.0),//SOUTH
            Block.createCuboidShape(0.0, 8.0, 0.5, 8.0, 14.0, 15.5)};//EAST

    public static final VoxelShape[] SHAPES_BOTTOM = {
            Block.createCuboidShape(0.5, 0.0, 8.0, 15.5, 6.0, 16.0),//NORTH
            Block.createCuboidShape(8.0, 0.0, 0.5, 16.0, 6.0, 15.5),//WEST
            Block.createCuboidShape(0.5, 0.0, 0.0, 15.5, 6.0, 8.0),//SOUTH
            Block.createCuboidShape(0.0, 0.0, 0.5, 8.0, 6.0, 15.5)};//EAST

    private static VoxelShape[] initShapes_double(){
        VoxelShape[] SHAPES = new VoxelShape[4];
        for(int i = 0; i<4; i++){
            SHAPES[i] = VoxelShapes.union(SHAPES_TOP[i],SHAPES_BOTTOM[i]);
        }
        return SHAPES;
    }

    public static final VoxelShape[] SHAPES_DOUBLE = initShapes_double();

    public static final EnumProperty<SlabType> SHELVES_ENABLED = Properties.SLAB_TYPE;

    public final Block SlabWoodType;
    public final VariantResources.wood_variant VARIANT;

    public ShelfShopBlock(Settings settings, Block slab, VariantResources.wood_variant variant) {
        super(settings, ShelfShopState::new);
        this.SlabWoodType = slab;
        this.VARIANT = variant;
    }

    public String getWoodName(){
        return VARIANT.name;
    }

    @Override
    public ScreenSettingsGroup getScreenSettings() {
        return ScreenSettingsGroup.createBasicWood(VARIANT);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING)
                .add(SHELVES_ENABLED);
    }

    @Override
    protected AbstractShopBlockState defaultStateProperties(AbstractShopBlockState state){
        return (AbstractShopBlockState) state.with(FACING, Direction.NORTH)
                .with(SHELVES_ENABLED,  SlabType.DOUBLE);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos blockPos = ctx.getBlockPos();
        BlockState blockState = ctx.getWorld().getBlockState(blockPos);

        if (blockState.isOf(this)) {
            return blockState.with(SHELVES_ENABLED, SlabType.DOUBLE);
        }

        Direction finalDirection = ctx.getHorizontalPlayerFacing().getOpposite();
        PlayerEntity player = ctx.getPlayer();
        if(player != null){
            World world = ctx.getWorld();

            for(Direction direction : Direction.getEntityFacingOrder(player)){
                if(direction.getHorizontal() != -1) {
                    BlockPos facingBlockPos = blockPos.offset(direction);
                    if(world.getBlockState(facingBlockPos).isSideSolid(world, facingBlockPos, direction.getOpposite(), SideShapeType.FULL)){
                        finalDirection = direction.getOpposite();
                        break;
                    }
                }
            }
        }

        SlabType type = ((ctx.getHitPos().y - ctx.getBlockPos().getY()) > 0.5) ? SlabType.TOP : SlabType.BOTTOM;
        return getPlacementState(ctx, this.getDefaultState()
                .with(FACING, finalDirection)
                .with(BREAKABLE, false)
                .with(SHELVES_ENABLED, type));
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ShelfShopEntity(pos,state);
    }

    @Override
    protected boolean onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player) {
        Item item = stack.getItem();
        if(item != this.SlabWoodType.asItem()) {
            if (VariantResources.SHELF.containsKey(item)) {
                ShelfShopBlock newShelf = net.spudacious5705.shops.block.VariantResources.SHELF.get(item);

                if (newShelf.getDefaultState() instanceof ShelfShopState defaultShopState) {
                    if(!player.isCreative()) {

                        int i = state.get(SHELVES_ENABLED) == SlabType.DOUBLE ? 2 : 1;

                        stack.decrement(i);

                        ItemStack dropStack = SlabWoodType.asItem().getDefaultStack();

                        dropStack.setCount(i);

                        world.spawnEntity(new ItemEntity(world, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, dropStack, 0f, 0.1f, 0f));
                    }

                    BlockState newBlockState = importProperties(defaultShopState, state);
                    world.setBlockState(pos, newBlockState);

                    return true;
                }

            }
        }

        return false;
    }

    private static BlockState importProperties(BlockState defaultState, BlockState originalState) {
        return defaultState
                .with(FACING, originalState.get(FACING))
                .with(BREAKABLE, originalState.get(BREAKABLE))
                .with(SHELVES_ENABLED, originalState.get(SHELVES_ENABLED));
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return
                world.isClient() ?
                        (world1, pos, state1, blockEntity) -> {
                            if (blockEntity instanceof ShelfShopEntity be) {
                                be.renderTick();
                            }
                        }
                        :
                        (world1, pos, shopState, blockEntity) -> {
                            if(blockEntity instanceof ShelfShopEntity be){
                                be.serverTick((ServerWorld) world1, pos, (ShelfShopState) shopState);
                            }
                        };
    }

    public static class ShelfShopState extends AbstractShopBlockState {


        public ShelfShopState(Block block, Reference2ObjectArrayMap<Property<?>, Comparable<?>> reference2ObjectArrayMap, MapCodec<BlockState> mapCodec) {
            super(block, reference2ObjectArrayMap, mapCodec);
        }

        @Override
        public VoxelShape getCullingShape(BlockView world, BlockPos pos) {
            return CULLING_SHAPE;
        }

        @Override
        public VoxelShape getOutlineShape(BlockView world, BlockPos pos, ShapeContext context) {
            return getShape();
        }

        @Override
        public VoxelShape getCollisionShape(BlockView world, BlockPos pos) {
            return getShape();
        }

        private VoxelShape getShape(){
            int select = switch (this.get(FACING)){
                case NORTH -> 0;
                case WEST -> 1;
                case SOUTH -> 2;
                default -> 3;
            };
            return switch(this.get(SHELVES_ENABLED)) {
                case DOUBLE -> SHAPES_DOUBLE[select];
                case TOP -> SHAPES_TOP[select];
                case BOTTOM -> SHAPES_BOTTOM[select];
            };
        }

        @Override
        protected boolean isStateReplacedValid(BlockState newShopState) {
            return newShopState instanceof ShelfShopState;
        }

        @Override
        public boolean canReplace(ItemPlacementContext context) {
            if(this.get(SHELVES_ENABLED) == SlabType.DOUBLE) return false;
            return context.getStack().isOf(this.getBlock().asItem());
        }

        @Override
        public boolean canPlaceAt(WorldView world, BlockPos pos) {
            Predicate<Direction> predicate = direction -> world.getBlockState(pos.offset(direction)).isSideSolid(world,pos.offset(direction),direction.getOpposite(), SideShapeType.FULL);
            return predicate.test(Direction.NORTH)||predicate.test(Direction.SOUTH)||predicate.test(Direction.EAST)||predicate.test(Direction.WEST);
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
                if(((ShelfShopBlock)getBlock()).onUseWithItem(stack,this.asBlockState(),world,pos,player)) return ActionResult.SUCCESS;
            }

            if(world.getBlockEntity(pos) instanceof ShelfShopEntity shop){

                ExtendedScreenHandlerFactory<ShopScreenPayload> screenHandlerFactory =
                        shop.createScreenHandlerFactory(
                                hit.getPos().y > 0.5 + pos.getY()
                        );


                if (screenHandlerFactory != null) {
                    player.openHandledScreen(screenHandlerFactory);
                }
            }

            return ActionResult.SUCCESS;
        }
    }

}
