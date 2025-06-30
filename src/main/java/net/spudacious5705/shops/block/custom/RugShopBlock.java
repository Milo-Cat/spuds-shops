package net.spudacious5705.shops.block.custom;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.fabricmc.fabric.api.block.v1.FabricBlock;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.FabricTagKey;
import net.fabricmc.fabric.impl.datagen.FabricTagBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.SideShapeType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.data.server.tag.TagProvider;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.spudacious5705.shops.block.VariantResources;
import net.spudacious5705.shops.block.entity.AbstractShopEntity;
import net.spudacious5705.shops.block.entity.ModBlockEntities;
import net.spudacious5705.shops.block.entity.RugShopEntity;
import org.jetbrains.annotations.Nullable;

import static net.spudacious5705.shops.block.ModBlocks.settingsCarpet;


public class RugShopBlock extends AbstractShopBlock{

    public static final BooleanProperty CONNECTED_NORTH = Properties.NORTH;
    public static final BooleanProperty CONNECTED_EAST = Properties.EAST;
    public static final BooleanProperty CONNECTED_SOUTH = Properties.SOUTH;
    public static final BooleanProperty CONNECTED_WEST = Properties.WEST;

    public static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 0, 16, 1, 16);

    public final Item CARPET;
    public final String COLOUR;

    public RugShopBlock(Block carpet, String colour) {
        super(settingsCarpet, RugShopBlockState::new);
        CARPET = carpet.asItem();
        COLOUR = colour;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new RugShopEntity(pos,state);
    }

    @Override
    protected boolean onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player) {
        Item item = stack.getItem();
        if(item != this.CARPET) {
            if (net.spudacious5705.shops.block.VariantResources.RUGS_CARPET.containsKey(item)) {
                RugShopBlock newRug = net.spudacious5705.shops.block.VariantResources.RUGS_CARPET.get(item);

                if (newRug.getDefaultState() instanceof RugShopBlockState defaultShopState) {
                    if(!player.isCreative()) {
                        stack.decrement(1);

                        world.spawnEntity(new ItemEntity(world, pos.getX() + 0.5f, pos.getY() + 0.2f, pos.getZ() + 0.5f, CARPET.getDefaultStack(), 0f, 0.1f, 0f));
                    }

                    BlockState newBlockState = importProperties(defaultShopState, state);
                    world.setBlockState(pos, newBlockState);

                    return true;
                }

            }
            if (net.spudacious5705.shops.block.VariantResources.RUGS_DYE.containsKey(item)) {
                RugShopBlock newRug = VariantResources.RUGS_DYE.get(item);

                if (newRug.getDefaultState() instanceof RugShopBlockState defaultShopState) {
                    if(!player.isCreative()) {
                        stack.decrement(1);
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
                .with(CONNECTED_NORTH, originalState.get(CONNECTED_NORTH))
                .with(CONNECTED_EAST, originalState.get(CONNECTED_EAST))
                .with(CONNECTED_SOUTH, originalState.get(CONNECTED_SOUTH))
                .with(CONNECTED_WEST, originalState.get(CONNECTED_WEST))
                .with(BREAKABLE, originalState.get(BREAKABLE));
    }

    @Override
    protected AbstractShopBlockState defaultStateProperties(AbstractShopBlockState state) {
        return (AbstractShopBlockState) state
                .with(CONNECTED_NORTH, false)
                .with(CONNECTED_EAST, false)
                .with(CONNECTED_SOUTH, false)
                .with(CONNECTED_WEST, false);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(CONNECTED_NORTH).add(CONNECTED_EAST).add(CONNECTED_SOUTH).add(CONNECTED_WEST);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState();
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return
                world.isClient() ?
                        (world1, pos, state1, blockEntity) -> {
                            if (blockEntity instanceof RugShopEntity be) {
                                be.renderTick();
                            }
                        }
                        :
                        (world1, pos, shopState, blockEntity) -> {
                            if(blockEntity instanceof RugShopEntity be){
                                be.serverTick((ServerWorld) world1, pos, (RugShopBlockState) shopState);
                            }
                        };
    }

    public static class RugShopBlockState extends AbstractShopBlockState {


        public RugShopBlockState(Block block, Reference2ObjectArrayMap<Property<?>, Comparable<?>> reference2ObjectArrayMap, MapCodec<BlockState> mapCodec) {
            super(block, reference2ObjectArrayMap, mapCodec);
        }

        @Override
        public VoxelShape getCullingShape(BlockView world, BlockPos pos) {
            return SHAPE;
        }

        @Override
        public VoxelShape getOutlineShape(BlockView world, BlockPos pos, ShapeContext context) {
            return SHAPE;
        }

        @Override
        public VoxelShape getCollisionShape(BlockView world, BlockPos pos) {
            return SHAPE;
        }

        @Override
        public BlockState rotate(BlockRotation rotation) {
            return this;
        }

        @Override
        public BlockState mirror(BlockMirror mirror) {
            return this;
        }

        @Override
        public BlockState getStateForNeighborUpdate(Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
            if(!world.isClient()){
                BooleanProperty CONNECTION = switch(direction){
                    case NORTH -> CONNECTED_NORTH;
                    case SOUTH -> CONNECTED_SOUTH;
                    case EAST -> CONNECTED_EAST;
                    case WEST -> CONNECTED_WEST;
                    default -> null;
                };

                if(CONNECTION != null){
                    return this.with(
                        CONNECTION,
                        this.getBlock().equals(neighborState.getBlock())
                );
                }
            }
            return this;
        }

        @Override
        protected boolean isStateReplacedValid(BlockState newState) {
            return newState instanceof RugShopBlockState;
        }

        @Override
        public boolean canPlaceAt(WorldView world, BlockPos pos) {
            return world.getBlockState(pos.down()).isSideSolid(world,pos.up(),Direction.UP, SideShapeType.FULL);
        }
    }

    @Override
    public TagKey<Block> getPreferredTool() {
        return TagKey.of(RegistryKeys.BLOCK, Identifier.of("fabric", "my_block_tag"));
    }
}
