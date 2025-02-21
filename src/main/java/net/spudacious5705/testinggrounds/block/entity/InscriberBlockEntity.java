package net.spudacious5705.testinggrounds.block.entity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.spudacious5705.testinggrounds.item.ModItems;
import net.spudacious5705.testinggrounds.screen.InscriberScreenHandler;
import org.jetbrains.annotations.Nullable;

public class InscriberBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory {
    private  final  DefaultedList<ItemStack> itemStacks = DefaultedList.ofSize(2, ItemStack.EMPTY);

    private  static final int INPUT_SLOT = 0;
    private  static final int OUTPUT_SLOT = 1;

    protected final PropertyDelegate propertyDelegate;

    private int progress = 0;
    private int maxProgress = 100;
    private int toggle = 5;

    public InscriberBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.INSCRIBER_BLOCK_ENTITY, pos, state);
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> InscriberBlockEntity.this.progress;
                    case 1 -> InscriberBlockEntity.this.maxProgress;
                    case 2 -> InscriberBlockEntity.this.toggle;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> InscriberBlockEntity.this.progress = value;
                    case 1 -> InscriberBlockEntity.this.maxProgress = value;
                };
            }

            @Override
            public int size() {
                return 3;
            }
        };
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
        packetByteBuf.writeBlockPos(this.pos);
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Inscriber");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new InscriberScreenHandler(syncId, playerInventory, this,  this.propertyDelegate);
    }


    @Override
    public DefaultedList<ItemStack> getItems() {
        return itemStacks;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, itemStacks);
        nbt.putInt("inscriber_block.progress", progress);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, itemStacks);
        progress = nbt.getInt("inscriber_block.progress");
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if(world.isClient()) return;
        if(this.hasRecipie()) {
            if (isOutputSlotEmptyOrRecieveable()) {
                this.increaseProgress();
                markDirty(world, pos, state);

                if (hasCraftingFinished()) {
                    this.craftItem();
                    this.resetProgress();
                }

            } else {
                this.resetProgress();
            }
        } else {
            this.resetProgress();
            markDirty(world, pos, state);
        }
    }

    private void craftItem() {
        this.removeStack(INPUT_SLOT, 1);
        ItemStack result = new ItemStack(ModItems.RUNIC_GOLD);

        this.setStack(OUTPUT_SLOT, new ItemStack(result.getItem(), getStack(OUTPUT_SLOT).getCount() + result.getCount()));
    }

    private boolean hasCraftingFinished() {
        return progress >= maxProgress;
    }

    private void resetProgress() {
        this.progress = 0;
    }

    private void increaseProgress() {
        progress++;
    }

    private boolean isOutputSlotEmptyOrRecieveable() {
        return this.getStack(OUTPUT_SLOT).isEmpty() || this.getStack(OUTPUT_SLOT).getCount() < this.getStack(OUTPUT_SLOT).getMaxCount();
    }

    private boolean hasRecipie() {
        ItemStack result = new ItemStack(ModItems.RUNIC_GOLD);
        boolean hasInput = getStack(INPUT_SLOT).getItem() == Items.GOLD_INGOT;

        return hasInput && canInsertAmountOutput(result) && canInsertItemOutput(result.getItem());
    }

    private boolean canInsertItemOutput(Item item) {
        return this.getStack(OUTPUT_SLOT).getItem() == item || this.getStack(OUTPUT_SLOT).isEmpty();
    }

    private boolean canInsertAmountOutput(ItemStack result) {
        return this.getStack(OUTPUT_SLOT).getCount() + result.getCount() <= getStack(OUTPUT_SLOT).getMaxCount();
    }
}
