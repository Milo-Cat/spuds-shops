package net.spudacious5705.shops.block.custom;

import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.fabricmc.fabric.api.block.BlockPickInteractionAware;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

import net.minecraft.state.property.*;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.spudacious5705.shops.block.VariantResources;
import net.spudacious5705.shops.block.entity.AngledShopEntity;
import net.spudacious5705.shops.item.custom.ShopItem;
import net.spudacious5705.shops.screen.ScreenSettingsGroup;
import net.spudacious5705.shops.util.CushionResources;
import net.spudacious5705.shops.properties.Colour;
import org.jetbrains.annotations.Nullable;

import java.util.*;


public class AngledShopBlock extends AbstractShopBlock implements BlockPickInteractionAware {

    public static final VoxelShape CULLING_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 17.5);

    public static final VoxelShape BASE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 6.0, 16.0);
    public static final VoxelShape BASE_NORTH = VoxelShapes.union(
            Block.createCuboidShape(0.0, 6.0, 2.0, 16.0, 12.0, 16.0),
            BASE
    );
    public static final VoxelShape BASE_EAST = VoxelShapes.union(
            Block.createCuboidShape(0.0, 6.0, 0.0, 14.0, 12.0, 16.0),
            BASE
    );
    public static final VoxelShape BASE_SOUTH = VoxelShapes.union(
            Block.createCuboidShape(0.0, 6.0, 0.0, 16.0, 12.0, 14.0),
            BASE
    );
    public static final VoxelShape BASE_WEST = VoxelShapes.union(
            Block.createCuboidShape(2.0, 6.0, 0.0, 16.0, 12.0, 16.0),
            BASE
    );

    public static final VoxelShape NORTH_SHAPE = VoxelShapes.union(
            Block.createCuboidShape(1.0, 12, 3.0, 15.0, 15.0, 12.0),
            Block.createCuboidShape(1.0, 15.0, 8.0, 15.0, 17.5, 15.0),
            BASE_NORTH
    );
    public static final VoxelShape EAST_SHAPE = VoxelShapes.union(
            Block.createCuboidShape(4.0, 12, 1.0, 13.0, 15.0, 15.0),
            Block.createCuboidShape(1.0, 15.0, 1.0, 8.0, 17.5, 15.0),
            BASE_EAST
    );
    public static final VoxelShape SOUTH_SHAPE = VoxelShapes.union(
            Block.createCuboidShape(1.0, 12, 4.0, 15.0, 15.0, 13.0),
            Block.createCuboidShape(1.0, 15.0, 1.0, 15.0, 17.5, 8.0),
            BASE_SOUTH
    );
    public static final VoxelShape WEST_SHAPE = VoxelShapes.union(
            Block.createCuboidShape(3.0, 12, 1.0, 12.0, 15.0, 15),
            Block.createCuboidShape(8.0, 15.0, 1.0, 15, 17.5, 15),
            BASE_WEST
    );

    public final Item WOOD_TYPE;
    public final VariantResources.wood_variant VARIANT;

    public AngledShopBlock(Settings settings, Item woodType, VariantResources.wood_variant variant) {
        super(settings, AngledShopBlockState::new);

        net.spudacious5705.shops.block.VariantResources.ANGLED.put(woodType, this);

        WOOD_TYPE = woodType;

        VARIANT = variant;
    }

    public String getWoodName(){
        return VARIANT.name;
    }

    @Override
    public ScreenSettingsGroup getScreenSettings() {
        return ScreenSettingsGroup.createBasicWood(VARIANT);
    }

    @Override
    public Item asItem() {
        return getDefaultColouredShopItem();
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (placer != null) {
            if (placer instanceof PlayerEntity player) {
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity instanceof AngledShopEntity shopEntity) {
                    shopEntity.userSignIn(player);
                    if (itemStack.getItem() instanceof ShopItem item) {
                        shopEntity.setCushionColour(item.colour);
                    }
                }
            }
        }
        super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AngledShopEntity(pos, state);
    }

    @Override
    public ItemStack getPickedStack(BlockState state, BlockView view, BlockPos pos, PlayerEntity player, HitResult result) {
        Colour colour = Colour.RED;
        if(view.getBlockEntity(pos) instanceof AngledShopEntity shopEntity) {
            colour = shopEntity.getCushionColour();
        }
        return getColouredShopItem(colour).getDefaultStack();
    }

    public static class AngledShopBlockState extends AbstractShopBlockState {


        public AngledShopBlockState(Block block, Reference2ObjectArrayMap<Property<?>, Comparable<?>> reference2ObjectArrayMap, MapCodec<BlockState> mapCodec) {
            super(block, reference2ObjectArrayMap, mapCodec);
        }

        @Override
        public VoxelShape getCullingShape(BlockView world, BlockPos pos) {
            return CULLING_SHAPE;
        }

        @Override
        public VoxelShape getOutlineShape(BlockView world, BlockPos pos, ShapeContext context) {
            return switch (this.get(FACING)) {
                case NORTH -> NORTH_SHAPE;
                case SOUTH -> SOUTH_SHAPE;
                case EAST -> EAST_SHAPE;
                case WEST -> WEST_SHAPE;
                default -> CULLING_SHAPE;
            };
        }

        @Override
        protected boolean isStateReplacedValid(BlockState newShopState) {
            return newShopState instanceof AngledShopBlockState;
        }

        @Override
        public VoxelShape getCollisionShape(BlockView world, BlockPos pos) {
            return switch (this.get(FACING)) {
                case NORTH -> BASE_NORTH;
                case SOUTH -> BASE_SOUTH;
                case EAST -> BASE_EAST;
                case WEST -> BASE_WEST;
                default -> CULLING_SHAPE;
            };
        }
    }//end of ShopBlockState

    protected boolean onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player) {
        if (!stack.isEmpty()) {
            Item item = stack.getItem();
            if (world.getBlockEntity(pos) instanceof AngledShopEntity shopEntity) {
                if (CushionResources.DYE_MAP.containsKey(item)) {
                    CushionResources.cushionColourGroup group = CushionResources.DYE_MAP.get(item);
                    if (shopEntity.getCushionColour() != group.colour()) {
                        if(!player.isCreative())stack.decrement(1);
                        shopEntity.setCushionColour(group.colour());
                        world.playSound(player, pos, SoundEvents.ITEM_DYE_USE, SoundCategory.BLOCKS);
                        world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
                        return true;
                    }
                } else if (CushionResources.WOOL_MAP.containsKey(item)) {
                    CushionResources.cushionColourGroup group = CushionResources.WOOL_MAP.get(item);
                    Colour originalColour = shopEntity.getCushionColour();
                    if (originalColour != group.colour()) {
                        shopEntity.setCushionColour(group.colour());
                        if(!player.isCreative()) {
                            stack.decrement(1);
                            group = CushionResources.COLOUR_MAP.get(originalColour);
                            ItemStack releaseStack = new ItemStack(group.wool(), 1);
                            world.spawnEntity(new ItemEntity(world, pos.getX() + 0.5f, pos.getY() + 0.6f, pos.getZ() + 0.5f, releaseStack, 0f, 0.1f, 0f));
                        }
                        world.playSound(player, pos, SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.BLOCKS);
                        world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
                        return true;
                    }
                } else if (VariantResources.ANGLED.containsKey(item)) {
                    AngledShopBlock block = net.spudacious5705.shops.block.VariantResources.ANGLED.get(item);
                    if (WOOD_TYPE != item) {
                        if (block.getDefaultState() instanceof AngledShopBlockState defaultShopState) {
                            if(!player.isCreative()) {
                                stack.decrement(1);
                                world.spawnEntity(new ItemEntity(world, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, WOOD_TYPE.getDefaultStack(), 0f, 0.1f, 0f));
                            }
                            BlockState newBlockState = importProperties(defaultShopState, state);
                            world.setBlockState(pos, newBlockState);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return
                world.isClient() ?
                        (world1, pos, state1, blockEntity) -> {
                            if (blockEntity instanceof AngledShopEntity be) {
                                be.renderTick();
                            }
                        }
                        :
                        (world1, pos, shopState, blockEntity) -> {
                            if(blockEntity instanceof AngledShopEntity be){
                                be.serverTick((ServerWorld) world1, pos, (AngledShopBlockState) shopState);
                            }
                        };
    }

    private static BlockState importProperties(BlockState defaultState, BlockState originalState) {
        return defaultState
                .with(FACING, originalState.get(FACING))
                .with(BREAKABLE, originalState.get(BREAKABLE));
    }

    private final Map<Colour, ShopItem> dropMap = new HashMap<>();

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



