package net.spudacious5705.shops.block.entity;


import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.loading.FMLEnvironment;
import net.spudacious5705.shops.block.ModBlockEntities;
import org.jetbrains.annotations.NotNull;

import static net.spudacious5705.shops.block.entity.ShopInventory.ItemScatterer;

/**
 * Block entity for the shelf shop variant.
 *
 * <p>Maintains two inventories and separate render data for the top and bottom shelf displays.</p>
 */
public class ShelfShopEntity extends AbstractShopEntity {

    /** Secondary inventory used for the top shelf row. */
    private final ShopInventory shopInventoryTop;

    @OnlyIn(Dist.CLIENT)
    protected ShelfRenderData furtherDataTop;
    @OnlyIn(Dist.CLIENT)
    protected ShelfRenderData furtherDataBottom;
    @OnlyIn(Dist.CLIENT)
    protected RendererData rendererDataTop;

    public ShelfShopEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SHELF_SHOP_ENTITY.get(), pos, state, -0.3f);
        this.shopInventoryTop = ShopInventory.create(toggleSettings);
        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            createRendererDataForShelf();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public ShelfRenderData furtherDataBottom() {
        return furtherDataBottom;
    }

    @OnlyIn(Dist.CLIENT)
    public ShelfRenderData furtherDataTop() {
        return furtherDataTop;
    }

    @Override
    protected @NotNull ShopInventory otherInventory() {
        return shopInventoryTop;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void forceUpdateRenderData() {
        super.forceUpdateRenderData();
        rendererDataTop.update();
    }

    @Override
    public void itemScatter(Level world, BlockPos pos) {
        super.itemScatter(world, pos);
        ItemScatterer(world, pos, shopInventoryTop.prepForItemScatterer());

    }

    @Override
    public void renderTick() {
        this.rendererData.onTick();
        this.rendererDataTop.onTick();
    }

    @OnlyIn(Dist.CLIENT)
    protected void createRendererDataForShelf() {
        this.furtherDataTop = new ShelfRenderData();
        this.furtherDataBottom = new ShelfRenderData();
        this.rendererDataTop = new RendererData(shopInventoryTop);
    }

    @OnlyIn(Dist.CLIENT)
    public RendererData rendererDataTop() {
        return rendererDataTop;
    }

    @Override
    protected boolean hasTrade() {
        return shopInventory.tradeFunctional() || shopInventoryTop.tradeFunctional();
    }

    @Override
    protected void saveAdditional(@NotNull ValueOutput output) {


        ValueOutput.TypedOutputList<ItemStackWithSlot> typedoutputlist = output.list("ItemsTwo", ItemStackWithSlot.CODEC);

        for (int i = 0; i < shopInventoryTop.size(); i++) {
            ItemStack itemstack = shopInventoryTop.get(i);
            if (!itemstack.isEmpty()) {
                typedoutputlist.add(new ItemStackWithSlot(i, itemstack));
            }
        }

        super.saveAdditional(output);
    }

    @Override
    protected void loadAdditional(@NotNull ValueInput input) {
        super.loadAdditional(input);

        for (ItemStackWithSlot itemstackwithslot : input.listOrEmpty("ItemsTwo", ItemStackWithSlot.CODEC)) {
            if (itemstackwithslot.isValidInContainer(shopInventoryTop.size())) {
                shopInventoryTop.set(itemstackwithslot.slot(), itemstackwithslot.stack());
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class ShelfRenderData {
        public final float itemLrotation = (float) ((Math.random() * 90) + 80f);
        public final float itemRrotation = (float) ((Math.random() * 90) + 80f);
    }


}
