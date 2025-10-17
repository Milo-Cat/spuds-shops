package net.spudacious5705.shops.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.spudacious5705.shops.block.ModBlockEntities;
import net.spudacious5705.shops.block.VariantResources;
import net.spudacious5705.shops.block.entity.CrateShopEntity;
import net.spudacious5705.shops.screen.ScreenSettingsGroup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CrateShopBlock extends AbstractShopBlock{

    public static final VoxelShape CULLING_SHAPE = createCuboidShape(0, -1.0, -1.0, 16.0, 2.0, 17.0);

    public static final VoxelShape SHAPE_NORTH = Shapes.or(
            createCuboidShape(1.0, 0.0, 0.0, 15.0, 8.0, 8.0),
            createCuboidShape(1.0, 4.0, 4.0, 15.0, 12.0, 12.0),
            createCuboidShape(1.0, 8.0, 8.0, 15.0, 16.0, 16.0)
    );
    public static final VoxelShape SHAPE_WEST = Shapes.or(
            createCuboidShape(0.0, 0.0, 1.0, 8.0, 8.0, 15.0),
            createCuboidShape(4.0, 4.0, 1.0, 12.0, 12.0, 15.0),
            createCuboidShape(8.0, 8.0, 1.0, 16.0, 16.0, 15.0)
    );
    public static final VoxelShape SHAPE_SOUTH = Shapes.or(
            createCuboidShape(1.0, 0.0, 8.0, 15.0, 8.0, 16.0),
            createCuboidShape(1.0, 4.0, 4.0, 15.0, 12.0, 12.0),
            createCuboidShape(1.0, 8.0, 0.0, 15.0, 16.0, 8.0)
    );
    public static final VoxelShape SHAPE_EAST = Shapes.or(
            createCuboidShape(8.0, 0.0, 1.0, 16.0, 8.0, 15.0),
            createCuboidShape(4.0, 4.0, 1.0, 12.0, 12.0, 15.0),
            createCuboidShape(0.0, 8.0, 1.0, 8.0, 16.0, 15.0)
    );

    public CrateShopBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public ScreenSettingsGroup getScreenSettings() {
        return ScreenSettingsGroup.createBasicWood(VariantResources.wood_variant.SPRUCE);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CrateShopEntity(pos,state);
    }


    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if(ModBlockEntities.CRATE_SHOP_ENTITY.get() == type) {
            return level.isClientSide
                    ? (lvl, pos, st, be) -> ((CrateShopEntity)be).renderTick()
                    : (lvl, pos, st, be) -> ((CrateShopEntity)be).serverTick((ServerLevel) lvl, pos,  st);

        }
        return null;
    }


    @Override
    protected boolean isStateReplacedValid(BlockState newShopState) {
        return newShopState.getBlock() instanceof CrateShopBlock;
    }

    @Override
    protected boolean onUseWithItem(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player) {
        return false;
    }

    @Override
    public @NotNull VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return CULLING_SHAPE;
    }

    @Override
    protected VoxelShape getGenericShape(BlockState state){
        return switch (state.getValue(FACING)) {
            case EAST -> SHAPE_EAST;
            case SOUTH -> SHAPE_SOUTH;
            case WEST -> SHAPE_WEST;
            default -> SHAPE_NORTH;
        };
    }
}
