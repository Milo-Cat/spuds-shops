package net.spudacious5705.testinggrounds.block.entity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.spudacious5705.testinggrounds.screen.ShopScreenHandler;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ShopEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory {
    private  final  DefaultedList<ItemStack> itemStacks = DefaultedList.ofSize(80, ItemStack.EMPTY);

    private Item currency = null;
    private int costQuantity = 0;
    private int vendQuantity = 0;
    private Item vendItem = null;


    private  static final int PAYMENT_SLOT = 76;
    private  static final int VENDING_SLOT = 77;
    private  static final int SET_PAYMENT_SLOT = 78;
    private  static final int SET_VENDING_SLOT = 79;
    private static final int profit_itemStacks_start = 54;
    private static final int profit_itemStacks_range = 21;
    private static final int stock_itemStacks_start = 0;
    private static final int stock_itemStacks_range = 53;

    protected final PropertyDelegate propertyDelegate;

    private int ownerID = 0;

    public ShopEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SHOP_ENTITY, pos, state);
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return ShopEntity.this.ownerID;
            }

            @Override
            public void set(int index, int value) {
            }

            @Override
            public int size() {
                return 1;
            }
        };
    }

    public void setOwnerID(UUID id) {
        if (ownerID == 0) {
            this.ownerID = id.hashCode();
        }
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
        packetByteBuf.writeBlockPos(this.pos);
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Shop");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new ShopScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return itemStacks;
    }

    public Item getPaymentType() {
        return currency;
    }

    public boolean isShopFunctional(){
        if(0 == ownerID){return false;}
        if(currency == null){return false;}
        if(vendItem == null){return false;}
        if(vendQuantity == 0){return false;}
        if(costQuantity == 0){return false;}
        return true;
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if(world.isClient()) return;
        if(this.isShopFunctional()){}
        markDirty(world, pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, itemStacks);
        ownerID = nbt.getInt("owner_id");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, itemStacks);
        nbt.putInt("owner_id", ownerID);
    }
}
