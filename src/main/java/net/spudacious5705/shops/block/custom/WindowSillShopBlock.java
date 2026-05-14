package net.spudacious5705.shops.block.custom;

import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.spudacious5705.shops.block.VariantResources;
import net.spudacious5705.shops.block.entity.WindowSillShopEntity;
import org.jetbrains.annotations.Nullable;

public class WindowSillShopBlock extends AbstractShopBlock{

    public final Item STONE_TYPE;

    public static final VoxelShape SHAPE = Block.createCuboidShape(0, -1.0, -1.0, 16.0, 2.0, 17.0);
    public static final VoxelShape SHAPE_ROTATED = Block.createCuboidShape(-1.0, -1.0, 0, 17.0, 2.0, 16.0);

    public WindowSillShopBlock(Settings settings, Item stoneType) {
        super(settings, WindowShopBlockState::new);
        this.STONE_TYPE = stoneType;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new WindowSillShopEntity(pos,state);
    }

    @Override
    protected boolean onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player) {
        Item item = stack.getItem();
        if(item != this.STONE_TYPE) {
            if (VariantResources.WINDOW_SILL.containsKey(item)) {
                WindowSillShopBlock newSill = net.spudacious5705.shops.block.VariantResources.WINDOW_SILL.get(item);

                if (newSill.getDefaultState() instanceof WindowShopBlockState defaultShopState) {
                    if (!player.isCreative()) {
                        stack.decrement(1);

                        world.spawnEntity(new ItemEntity(world, pos.getX() + 0.5f, pos.getY() + 0.3f, pos.getZ() + 0.5f, STONE_TYPE.getDefaultStack(), 0f, 0.1f, 0f));
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
                .with(BREAKABLE, originalState.get(BREAKABLE));
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return
                world.isClient() ?
                        (world1, pos, state1, blockEntity) -> {
                            if (blockEntity instanceof WindowSillShopEntity be) {
                                be.renderTick();
                            }
                        }
                        :
                        (world1, pos, shopState, blockEntity) -> {
                            if(blockEntity instanceof WindowSillShopEntity be){
                                be.serverTick((ServerWorld) world1, pos, (WindowShopBlockState) shopState);
                            }
                        };
    }

    public static class WindowShopBlockState extends AbstractShopBlockState {


        public WindowShopBlockState(Block block, Reference2ObjectArrayMap<Property<?>, Comparable<?>> reference2ObjectArrayMap, MapCodec<BlockState> mapCodec) {
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
                case EAST, WEST -> SHAPE_ROTATED;
                default -> SHAPE;
            };
        }

        @Override
        protected boolean isStateReplacedValid(BlockState newShopState) {
            return newShopState instanceof WindowShopBlockState;
        }
    }

    @Override
    public TagKey<Block> getPreferredTool() {
        return BlockTags.PICKAXE_MINEABLE;
    }
}
