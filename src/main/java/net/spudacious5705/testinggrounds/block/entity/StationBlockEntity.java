package net.spudacious5705.testinggrounds.block.entity;

import com.ibm.icu.impl.TextTrieMap;
import com.ibm.icu.impl.number.range.PrefixInfixSuffixLengthHelper;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import net.spudacious5705.testinggrounds.block.ModBlocks;
import net.spudacious5705.testinggrounds.item.ModItemGroups;
import net.spudacious5705.testinggrounds.item.ModItems;
import net.spudacious5705.testinggrounds.screen.StationScreenHandler;

public class StationBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory {

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);

    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;

    protected final PropertyDelegate propertyDelegate;
    private int progress = 0;
    private int maxProgress = 64;

    public StationBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.STATION_BLOCK_ENTITY, pos, state);
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index){
                    case INPUT_SLOT -> StationBlockEntity.this.progress;
                    case OUTPUT_SLOT -> StationBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index){
                    case INPUT_SLOT -> StationBlockEntity.this.progress = value;
                    case OUTPUT_SLOT -> StationBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int size() {
                return 2;
            }
        };
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
        packetByteBuf.writeBlockPos(this.pos);

    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Station");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new StationScreenHandler(syncId, playerInventory, this,this.propertyDelegate);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override //saves nbt data when unloaded
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
        nbt.putInt("station.progress", progress);
    }

    @Override //loads saved nbt data
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory);
        progress = nbt.getInt("station.progress");
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if(!world.isClient()) {
            return;
        }

        if(isOutputSlotEmptyOrReceivable()){
            if(this.hasRecipie()){
                this.increaseCraftProgress();
                markDirty(world,pos,state);

                if(hasCraftingFinished()){
                    this.craftItem();
                    this.resetProgress();
                }
            } else {
                this.resetProgress();
            }
        } else {
            this.resetProgress();
            markDirty(world,pos,state);
        }

    }

    private void resetProgress() {
        this.progress = 0;
    }

    private void craftItem() {
        this.removeStack(INPUT_SLOT,1);
        ItemStack result = new ItemStack(ModItems.SUSPICIOUS_SUBSTANCE);

        this.setStack(OUTPUT_SLOT, new ItemStack(result.getItem(),getStack(OUTPUT_SLOT).getCount() + result.getCount()));
    }

    private boolean hasCraftingFinished() {
        return progress >= maxProgress;
    }

    private void increaseCraftProgress() {
        progress ++;
    }

    private boolean hasRecipie() {
        ItemStack result = new ItemStack(ModItems.SUSPICIOUS_SUBSTANCE);
        boolean hasIngredients = getStack(INPUT_SLOT).getItem() == ModBlocks.SUSPICIOUS_BLOCK.asItem();

        return hasIngredients && canInsertAmountIntoOutputSlot(result) && canInsertItemIntoOutputSlot(result.getItem());
    }

    private boolean canInsertItemIntoOutputSlot(Item item) {
        return this.getStack(OUTPUT_SLOT).getItem() == item || this.getStack(OUTPUT_SLOT).isEmpty();
    }

    private boolean canInsertAmountIntoOutputSlot(ItemStack result) {
        return this.getStack(OUTPUT_SLOT).getCount() + result.getCount() <= getStack(OUTPUT_SLOT).getMaxCount();
    }

    private boolean isOutputSlotEmptyOrReceivable() {
        return this.getStack(OUTPUT_SLOT).isEmpty() || this.getStack(INPUT_SLOT).getCount() < this.getStack(OUTPUT_SLOT).getMaxCount();
    }
}
