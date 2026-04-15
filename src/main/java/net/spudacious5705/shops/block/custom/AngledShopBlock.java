package net.spudacious5705.shops.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.spudacious5705.shops.block.ModBlockEntities;
import net.spudacious5705.shops.block.entity.AngledShopEntity;
import net.spudacious5705.shops.block.resources.CushionResources;
import net.spudacious5705.shops.block.resources.VariantResources;
import net.spudacious5705.shops.item.custom.ShopItem;
import net.spudacious5705.shops.properties.Colour;
import net.spudacious5705.shops.screen.ScreenSettingsGroup;
import net.spudacious5705.shops.util.PostRegAssigner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;

import static net.spudacious5705.shops.block.resources.VariantResources.ANGLED;

/**
 * Angled shop block with a cushion surface and custom wood variant support.
 *
 * This block uses shape variants for each facing direction and supports
 * recoloring and wood-type swapping on player interaction.
 */
public class AngledShopBlock extends AbstractShopBlock {

    public static final VoxelShape CULLING_SHAPE = createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 17.5);

    public static final VoxelShape BASE = createCuboidShape(0.0, 0.0, 0.0, 16.0, 6.0, 16.0);
    public static final VoxelShape BASE_NORTH = Shapes.or(
            createCuboidShape(0.0, 6.0, 2.0, 16.0, 12.0, 16.0),
            BASE
    );
    public static final VoxelShape NORTH_SHAPE = Shapes.or(
            createCuboidShape(1.0, 12, 3.0, 15.0, 15.0, 12.0),
            createCuboidShape(1.0, 15.0, 8.0, 15.0, 17.5, 15.0),
            BASE_NORTH
    );
    public static final VoxelShape BASE_EAST = Shapes.or(
            createCuboidShape(0.0, 6.0, 0.0, 14.0, 12.0, 16.0),
            BASE
    );
    public static final VoxelShape EAST_SHAPE = Shapes.or(
            createCuboidShape(4.0, 12, 1.0, 13.0, 15.0, 15.0),
            createCuboidShape(1.0, 15.0, 1.0, 8.0, 17.5, 15.0),
            BASE_EAST
    );
    public static final VoxelShape BASE_SOUTH = Shapes.or(
            createCuboidShape(0.0, 6.0, 0.0, 16.0, 12.0, 14.0),
            BASE
    );
    public static final VoxelShape SOUTH_SHAPE = Shapes.or(
            createCuboidShape(1.0, 12, 4.0, 15.0, 15.0, 13.0),
            createCuboidShape(1.0, 15.0, 1.0, 15.0, 17.5, 8.0),
            BASE_SOUTH
    );
    public static final VoxelShape BASE_WEST = Shapes.or(
            createCuboidShape(2.0, 6.0, 0.0, 16.0, 12.0, 16.0),
            BASE
    );
    public static final VoxelShape WEST_SHAPE = Shapes.or(
            createCuboidShape(3.0, 12, 1.0, 12.0, 15.0, 15),
            createCuboidShape(8.0, 15.0, 1.0, 15, 17.5, 15),
            BASE_WEST
    );
    public final VariantResources.wood_variant VARIANT;

    /**
     * Maps cushion colours to the correct dropped shop item when the block is cloned.
     */
    private final Map<Colour, ShopItem> dropMap = new HashMap<>();

    /** The base wood item used for type swapping on interaction. */
    public Item WOOD_TYPE;

    public AngledShopBlock(BlockBehaviour.Properties settings, PostRegAssigner<Item> woodTypeAssigner, VariantResources.wood_variant variant) {
        super(settings);

        woodTypeAssigner.assignTo(o -> WOOD_TYPE = o);

        VARIANT = variant;
    }

    @Override
    public ScreenSettingsGroup getScreenSettings() {
        return ScreenSettingsGroup.createBasicWood(VARIANT);
    }

    @Override
    public @NotNull Item asItem() {
        return getDefaultColouredShopItem();
    }

    @Override
    public void setPlacedBy(@NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        if (placer != null) {
            if (placer instanceof Player player) {
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity instanceof AngledShopEntity shopEntity) {
                    shopEntity.userSignIn(player);
                    if (stack.getItem() instanceof ShopItem item) {
                        shopEntity.setCushionColour(item.colour);
                    }
                }
            }
        }
        super.setPlacedBy(world, pos, state, placer, stack);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new AngledShopEntity(pos, state);
    }

    @Override
    public @NonNull ItemStack getCloneItemStack(
            LevelReader level, @NonNull BlockPos pos,
            @NonNull BlockState state, boolean includeData, @NonNull Player player
    ) {
        Colour colour = Colour.RED;
        if (level.getBlockEntity(pos) instanceof AngledShopEntity shopEntity) {
            colour = shopEntity.getCushionColour();
        }
        return getColouredShopItem(colour).getDefaultInstance();
    }

    @Override
    public @NotNull VoxelShape getShape(
            @NotNull BlockState state, @NotNull BlockGetter pLevel,
            @NotNull BlockPos pPos, @NotNull CollisionContext pContext
    ) {
        return switch (state.getValue(FACING)) {
            case NORTH -> NORTH_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case EAST -> EAST_SHAPE;
            case WEST -> WEST_SHAPE;
            default -> CULLING_SHAPE;
        };
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(
            @NotNull BlockState state, @NotNull BlockGetter level,
            @NotNull BlockPos pos, @NotNull CollisionContext context
    ) {
        return switch (state.getValue(FACING)) {
            case NORTH -> BASE_NORTH;
            case SOUTH -> BASE_SOUTH;
            case EAST -> BASE_EAST;
            case WEST -> BASE_WEST;
            default -> CULLING_SHAPE;
        };
    }

    @Override
    public @NotNull VoxelShape getOcclusionShape(
            @NotNull BlockState state
    ) {
        return CULLING_SHAPE;
    }

    @Override
    protected VoxelShape getGenericShape(BlockState state) {
        return BASE;
    }

    @Override
    protected boolean isStateReplacedValid(BlockState newShopState) {
        return newShopState.getBlock() instanceof AngledShopBlock;
    }

    /**
     * Handles item-based interaction for angled shop blocks.
     *
     * Supports dyeing the cushion, changing wool appearance, and swapping the
     * wooden base type using registered variant items.
     */
    protected boolean onUseWithItem(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player) {
        if (!stack.isEmpty()) {
            Item item = stack.getItem();
            if (world.getBlockEntity(pos) instanceof AngledShopEntity shopEntity) {
                if (CushionResources.DYE_MAP.containsKey(item)) {
                    CushionResources.cushionColourGroup group = CushionResources.DYE_MAP.get(item);
                    if (shopEntity.getCushionColour() != group.colour()) {
                        if (!player.isCreative()) stack.shrink(1);
                        shopEntity.setCushionColour(group.colour());
                        world.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS);
                        world.sendBlockUpdated(pos, state, state, 3);
                        shopEntity.forceUpdateClient();
                        return true;
                    }
                } else if (CushionResources.WOOL_MAP.containsKey(item)) {
                    CushionResources.cushionColourGroup group = CushionResources.WOOL_MAP.get(item);
                    Colour originalColour = shopEntity.getCushionColour();
                    if (originalColour != group.colour()) {
                        shopEntity.setCushionColour(group.colour());
                        if (!player.isCreative()) {
                            stack.shrink(1);
                            group = CushionResources.COLOUR_MAP.get(originalColour);
                            ItemStack releaseStack = new ItemStack(group.wool(), 1);
                            world.addFreshEntity(new ItemEntity(world, pos.getX() + 0.5f, pos.getY() + 0.6f, pos.getZ() + 0.5f, releaseStack, 0f, 0.1f, 0f));
                        }
                        world.playSound(null, pos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS);
                        world.sendBlockUpdated(pos, state, state, 3);
                        shopEntity.forceUpdateClient();
                        return true;
                    }
                } else if (ANGLED.containsKey(item)) {
                    if (WOOD_TYPE != item) {
                        AngledShopBlock block = ANGLED.get(item);
                        if (!player.isCreative()) {
                            stack.shrink(1);
                            ItemEntity droppedItem = new ItemEntity(
                                    world,
                                    pos.getX() + 0.5,
                                    pos.getY() + 0.5,
                                    pos.getZ() + 0.5,
                                    WOOD_TYPE.getDefaultInstance()
                            );
                            droppedItem.setDeltaMovement(0, 0.1, 0);
                            world.addFreshEntity(droppedItem);
                        }
                        world.playSound(null, pos, SoundEvents.WOOD_STEP, SoundSource.BLOCKS);//TODO add this to fabric
                        world.setBlockAndUpdate(pos, copyValues(block.defaultBlockState(), state, FACING));
                        shopEntity.forceUpdateClient();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        if (ModBlockEntities.ANGLED_SHOP_ENTITY.get() == type) {
            return level.isClientSide()
                    ? (lvl, pos, st, be) -> ((AngledShopEntity) be).renderTick()
                    : (lvl, pos, st, be) -> ((AngledShopEntity) be).serverTick((ServerLevel) lvl, pos, st);

        }
        return null;
    }

    public void addDropItem(ShopItem shopItem, Colour colour) {
        dropMap.put(colour, shopItem);
    }

    public ShopItem getColouredShopItem(@Nullable Colour colour) {
        return dropMap.getOrDefault(colour, getDefaultColouredShopItem());
    }

    public ShopItem getDefaultColouredShopItem() {
        return dropMap.get(Colour.RED);
    }
}
