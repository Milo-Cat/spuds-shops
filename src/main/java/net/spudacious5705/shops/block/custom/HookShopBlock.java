package net.spudacious5705.shops.block.custom;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.SideShapeType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.spudacious5705.shops.block.entity.AbstractShopEntity;
import net.spudacious5705.shops.block.entity.HookShopEntity;
import net.spudacious5705.shops.block.entity.ModBlockEntities;
import org.jetbrains.annotations.Nullable;


public class HookShopBlock extends AbstractShopBlock{

    public static final VoxelShape SHAPE = Block.createCuboidShape(5, -1.0, 5, 11.0, 16.0, 11.0);

    public HookShopBlock(Settings settings) {
        super(settings, HookShopBlockState::new);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new HookShopEntity(pos,state);
    }

    @Override
    protected boolean onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player) {
        return false;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return
                world.isClient() ?
                        (world1, pos, state1, blockEntity) -> {
                            if (blockEntity instanceof HookShopEntity be) {
                                be.renderTick();
                            }
                        }
                        :
                        (world1, pos, shopState, blockEntity) -> {
                    if(blockEntity instanceof HookShopEntity be){
                        be.serverTick((ServerWorld) world1, pos, (HookShopBlockState) shopState);
                    }
                };
    }

    public static class HookShopBlockState extends AbstractShopBlockState {


        public HookShopBlockState(Block block, Reference2ObjectArrayMap<Property<?>, Comparable<?>> reference2ObjectArrayMap, MapCodec<BlockState> mapCodec) {
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
        protected boolean isStateReplacedValid(BlockState newShopState) {
            return newShopState instanceof HookShopBlockState;
        }

        @Override
        public boolean canPlaceAt(WorldView world, BlockPos pos) {
            return world.getBlockState(pos.up()).isSideSolid(world,pos.up(), Direction.DOWN, SideShapeType.CENTER);
        }
    }

    @Override
    public TagKey<Block> getPreferredTool() {
        return BlockTags.PICKAXE_MINEABLE;
    }
}
