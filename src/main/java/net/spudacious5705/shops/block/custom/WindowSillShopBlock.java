package net.spudacious5705.shops.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.spudacious5705.shops.block.ModBlockEntities;
import net.spudacious5705.shops.block.PostRegAssigner;
import net.spudacious5705.shops.block.VariantResources;
import net.spudacious5705.shops.block.entity.WindowSillShopEntity;
import org.jetbrains.annotations.Nullable;

public class WindowSillShopBlock extends AbstractShopBlock{

    public Item STONE_TYPE;

    public static final VoxelShape SHAPE = createCuboidShape(0, -1.0, -1.0, 16.0, 2.0, 17.0);
    public static final VoxelShape SHAPE_ROTATED = createCuboidShape(-1.0, -1.0, 0, 17.0, 2.0, 16.0);

    public WindowSillShopBlock(BlockBehaviour.Properties properties, PostRegAssigner<Item> stoneTypeAssigner) {
        super(properties);
        stoneTypeAssigner.assignTo(o -> STONE_TYPE = o);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WindowSillShopEntity(pos,state);
    }

    @Override
    protected boolean onUseWithItem(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player) {
        Item item = stack.getItem();
        if(item != this.STONE_TYPE) {
            if(world.getBlockEntity(pos) instanceof WindowSillShopEntity shopEntity) {
                if (VariantResources.WINDOW_SILL.containsKey(item)) {
                    BlockState newSill = net.spudacious5705.shops.block.VariantResources.WINDOW_SILL.get(item).defaultBlockState();


                    if (!player.isCreative()) {
                        stack.shrink(1);

                        world.addFreshEntity(new ItemEntity(world, pos.getX() + 0.5f, pos.getY() + 0.3f, pos.getZ() + 0.5f, STONE_TYPE.getDefaultInstance(), 0f, 0.1f, 0f));
                    }

                    world.setBlockAndUpdate(pos, copyValues(newSill, state, FACING));
                    shopEntity.forceUpdateClient();
                    return true;

                }
            }
        }

        return false;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if(ModBlockEntities.WINDOW_SHOP_ENTITY.get() == type) {
            return level.isClientSide
                    ? (lvl, pos, st, be) -> ((WindowSillShopEntity)be).renderTick()
                    : (lvl, pos, st, be) -> ((WindowSillShopEntity)be).serverTick((ServerLevel) lvl, pos,  st);

        }
        return null;
    }


    @Override
    public VoxelShape getGenericShape(BlockState state) {
        return switch (state.getValue(FACING)) {
            case EAST, WEST -> SHAPE_ROTATED;
            default -> SHAPE;
        };
    }

    @Override
    protected boolean isStateReplacedValid(BlockState newShopState) {
        return newShopState.getBlock() instanceof WindowSillShopBlock;
    }

    @Override
    public TagKey<Block> getPreferredTool() {
        return BlockTags.MINEABLE_WITH_PICKAXE;
    }
}
