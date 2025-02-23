package net.spudacious5705.testinggrounds.block.entity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.spudacious5705.testinggrounds.screen.ShopScreenHandlerCustomer;
import net.spudacious5705.testinggrounds.screen.ShopScreenHandlerOwner;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ShopEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory {
    private  final  DefaultedList<ItemStack> itemStacks = DefaultedList.ofSize(78, ItemStack.EMPTY);

    private  static final int PAYMENT_SLOT = 76;
    private  static final int VENDING_SLOT = 77;

    protected final PropertyDelegate propertyDelegate;

    private UUID ownerID;

    public ShopEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SHOP_ENTITY, pos, state);
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return ShopEntity.this.getStack(index).getCount();
            }

            @Override
            public void set(int index, int value) {
            }

            @Override
            public int size() {
                return 0;
            }
        };
    }

    public void setOwnerID(UUID id) {
        if (this.ownerID == null) {
            this.ownerID = id;
        }
        writeNbt(this.createNbt());
        markDirty();
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

        int result = player.getUuid().compareTo(ownerID);

        if(result==0) return new ShopScreenHandlerOwner(syncId, playerInventory, this, this.propertyDelegate);

        return new ShopScreenHandlerCustomer(syncId, playerInventory, this, this.propertyDelegate);
    }

    @Override
    public BlockState getCachedState() {
        return super.getCachedState();
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return itemStacks;
    }

    @Override
    public void markDirty() {
        world.updateListeners(pos,getCachedState(),getCachedState(),3);
        super.markDirty();
    }

    public Item getPaymentType() {
        return itemStacks.get(PAYMENT_SLOT).getItem();
    }

    public boolean isShopFunctional(){
        if(null == ownerID){return false;}
        if(itemStacks.get(PAYMENT_SLOT).isEmpty()){return false;}
        if(itemStacks.get(VENDING_SLOT).isEmpty()){return false;}
        return true;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, itemStacks);
        this.ownerID = nbt.getUuid("owner_id");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, itemStacks);
        if(this.ownerID != null){nbt.putUuid("owner_id", this.ownerID);}
    }

    public void clearFunctionalSlots() {
        this.itemStacks.set(PAYMENT_SLOT, ItemStack.EMPTY);
        this.itemStacks.set(VENDING_SLOT, ItemStack.EMPTY);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    public Direction getFacingDirection() {
        return world.getBlockState(this.getPos()).get(Properties.FACING);
    }
}
