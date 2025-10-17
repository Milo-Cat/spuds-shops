package net.spudacious5705.shops.block.custom;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.spudacious5705.shops.block.ModBlockEntities;
import net.spudacious5705.shops.block.PostRegAssigner;
import net.spudacious5705.shops.block.VariantResources;
import net.spudacious5705.shops.block.entity.RugShopEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

import static net.spudacious5705.shops.block.ModBlocks.settingsCarpet;


public class RugShopBlock extends AbstractShopBlock{

    public static final BooleanProperty CONNECTED_NORTH = BooleanProperty.create("north");
    public static final BooleanProperty CONNECTED_EAST = BooleanProperty.create("east");
    public static final BooleanProperty CONNECTED_SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty CONNECTED_WEST = BooleanProperty.create("west");

    public static final VoxelShape SHAPE = createCuboidShape(0, 0, 0, 16, 1, 16);

    public Item CARPET;
    public final String COLOUR;

    public RugShopBlock(PostRegAssigner<Item> carpetAssigner, String colour) {
        super(settingsCarpet);
        carpetAssigner.assignTo(o -> CARPET = o);
        COLOUR = colour;
    }

    @Override
    protected void registerDefaultStateTemplate() {
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(CONNECTED_NORTH, false)
                .setValue(CONNECTED_EAST, false)
                .setValue(CONNECTED_SOUTH, false)
                .setValue(CONNECTED_WEST, false)
                .setValue(BREAKABLE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(
                CONNECTED_NORTH,
                CONNECTED_EAST,
                CONNECTED_SOUTH,
                CONNECTED_WEST,
                BREAKABLE
        );
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RugShopEntity(pos,state);
    }

    @Override
    protected boolean onUseWithItem(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player) {
        Item item = stack.getItem();
        if(item != this.CARPET) {
            if(world.getBlockEntity(pos) instanceof RugShopEntity shopEntity) {
                if (VariantResources.RUGS_CARPET.containsKey(item)) {
                    RugShopBlock newRug = VariantResources.RUGS_CARPET.get(item);


                    if (!player.isCreative()) {
                        stack.shrink(1);
                        world.addFreshEntity(new ItemEntity(world, pos.getX() + 0.5f, pos.getY() + 0.2f, pos.getZ() + 0.5f, CARPET.getDefaultInstance(), 0f, 0.1f, 0f));
                    }
                    world.playSound(null, pos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS);


                    world.setBlockAndUpdate(pos, copyValues(newRug.defaultBlockState(), state, CONNECTED_NORTH, CONNECTED_EAST, CONNECTED_SOUTH, CONNECTED_WEST));
                    shopEntity.forceUpdateClient();
                    return true;


                } else if (VariantResources.RUGS_DYE.containsKey(item)) {
                    RugShopBlock newRug = VariantResources.RUGS_DYE.get(item);
                    if (newRug == state.getBlock()) return false;


                    if (!player.isCreative()) {
                        stack.shrink(1);
                    }
                    world.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS);//TODO rework this section on fabric to match

                    world.setBlockAndUpdate(pos, copyValues(newRug.defaultBlockState(), state, CONNECTED_NORTH, CONNECTED_EAST, CONNECTED_SOUTH, CONNECTED_WEST));
                    shopEntity.forceUpdateClient();
                    return true;
                }
            }
            }

        return false;
    }



    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if(ModBlockEntities.RUG_SHOP_ENTITY.get() == type) {
            return level.isClientSide
                    ? (lvl, pos, st, be) -> ((RugShopEntity)be).renderTick()
                    : (lvl, pos, st, be) -> ((RugShopEntity)be).serverTick((ServerLevel) lvl, pos,  st);

        }
        return null;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Level world = ctx.getLevel();

        BlockPos pos = ctx.getClickedPos();

        BlockPos attachedPos = pos.relative(Direction.DOWN);
        BlockState attachedState = world.getBlockState(attachedPos);
        if(!attachedState.isFaceSturdy(world, attachedPos, Direction.UP))return null;


        BlockState state = this.defaultBlockState();
        Function<Direction, Boolean> getValForDir = direction -> matchingCarpet(world, pos, state, direction);

        return state
                .setValue(BREAKABLE, false)
                .setValue(CONNECTED_NORTH, getValForDir.apply(Direction.NORTH))
                .setValue(CONNECTED_EAST, getValForDir.apply(Direction.EAST))
                .setValue(CONNECTED_SOUTH, getValForDir.apply(Direction.SOUTH))
                .setValue(CONNECTED_WEST, getValForDir.apply(Direction.WEST));
    }

    private boolean matchingCarpet(Level world, BlockPos pos, BlockState state, Direction dir){
        return world.getBlockState(pos.relative(dir)).getBlock() == state.getBlock();
    }


    @Override
    public BlockState rotate(BlockState state, LevelAccessor level, BlockPos pos, Rotation direction) {
        return state;
    }

    @Override
    public @NotNull BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState;
    }

    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState state, Direction direction, @NotNull BlockState neighborState, @NotNull LevelAccessor pLevel, BlockPos pos, BlockPos neighborPos) {

        BooleanProperty CONNECTION = switch(direction){
            case NORTH -> CONNECTED_NORTH;
            case SOUTH -> CONNECTED_SOUTH;
            case EAST -> CONNECTED_EAST;
            case WEST -> CONNECTED_WEST;
            default -> null;
        };

        if(CONNECTION == null)return state;

        boolean targetValue = neighborState.getBlock() == state.getBlock();

        return state.setValue(CONNECTION,targetValue);
    }



    @Override
    protected boolean isStateReplacedValid(BlockState newShopState) {
        return newShopState.getBlock() instanceof RugShopBlock;
    }

    @Override
    protected VoxelShape getGenericShape(BlockState state) {
        return SHAPE;
    }

    @Override
    public TagKey<Block> getPreferredTool() {
        return BlockTags.MINEABLE_WITH_HOE;
    }
}
