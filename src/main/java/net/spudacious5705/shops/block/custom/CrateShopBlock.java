package net.spudacious5705.shops.block.custom;

import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.spudacious5705.shops.block.VariantResources;
import net.spudacious5705.shops.block.entity.CrateShopEntity;
import net.spudacious5705.shops.screen.ScreenSettingsGroup;
import org.jetbrains.annotations.Nullable;

public class CrateShopBlock extends AbstractShopBlock{

    public static final VoxelShape CULLING_SHAPE = Block.createCuboidShape(0, -1.0, -1.0, 16.0, 2.0, 17.0);

    public static final VoxelShape SHAPE_NORTH = VoxelShapes.union(
            Block.createCuboidShape(1.0, 0.0, 0.0, 15.0, 8.0, 8.0),
            Block.createCuboidShape(1.0, 4.0, 4.0, 15.0, 12.0, 12.0),
            Block.createCuboidShape(1.0, 8.0, 8.0, 15.0, 16.0, 16.0)
    );
    public static final VoxelShape SHAPE_WEST = VoxelShapes.union(
            Block.createCuboidShape(0.0, 0.0, 1.0, 8.0, 8.0, 15.0),
            Block.createCuboidShape(4.0, 4.0, 1.0, 12.0, 12.0, 15.0),
            Block.createCuboidShape(8.0, 8.0, 1.0, 16.0, 16.0, 15.0)
    );
    public static final VoxelShape SHAPE_SOUTH = VoxelShapes.union(
            Block.createCuboidShape(1.0, 0.0, 8.0, 15.0, 8.0, 16.0),
            Block.createCuboidShape(1.0, 4.0, 4.0, 15.0, 12.0, 12.0),
            Block.createCuboidShape(1.0, 8.0, 0.0, 15.0, 16.0, 8.0)
    );
    public static final VoxelShape SHAPE_EAST = VoxelShapes.union(
            Block.createCuboidShape(8.0, 0.0, 1.0, 16.0, 8.0, 15.0),
            Block.createCuboidShape(4.0, 4.0, 1.0, 12.0, 12.0, 15.0),
            Block.createCuboidShape(0.0, 8.0, 1.0, 8.0, 16.0, 15.0)
    );

    public CrateShopBlock(Settings settings) {
        super(settings, CrateShopState::new);
    }

    @Override
    public ScreenSettingsGroup getScreenSettings() {
        return ScreenSettingsGroup.createBasicWood(VariantResources.wood_variant.SPRUCE);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CrateShopEntity(pos,state);
    }

    @Override
    protected boolean onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player) {
        return false;
    }

    private static BlockState importProperties(BlockState defaultState, BlockState originalState) {
        return defaultState
                .with(FACING, originalState.get(FACING))
                .with(BREAKABLE, originalState.get(BREAKABLE));
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return
                world.isClient() ?
                        (world1, pos, state1, blockEntity) -> {
                            if (blockEntity instanceof CrateShopEntity be) {
                                be.renderTick();
                            }
                        }
                        :
                        (world1, pos, shopState, blockEntity) -> {
                            if(blockEntity instanceof CrateShopEntity be){
                                be.serverTick((ServerWorld) world1, pos, (CrateShopState) shopState);
                            }
                        };
    }

    public static class CrateShopState extends AbstractShopBlockState {


        public CrateShopState(Block block, Reference2ObjectArrayMap<Property<?>, Comparable<?>> reference2ObjectArrayMap, MapCodec<BlockState> mapCodec) {
            super(block, reference2ObjectArrayMap, mapCodec);
        }

        @Override
        public VoxelShape getCullingShape(BlockView world, BlockPos pos) {
            return getShape();
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
            return switch (this.get(FACING)) {
                case EAST -> SHAPE_EAST;
                case SOUTH -> SHAPE_SOUTH;
                case WEST -> SHAPE_WEST;
                default -> SHAPE_NORTH;
            };
        }

        @Override
        protected boolean isStateReplacedValid(BlockState newShopState) {
            return newShopState instanceof CrateShopState;
        }
    }
}
