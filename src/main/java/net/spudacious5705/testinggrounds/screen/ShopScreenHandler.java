package net.spudacious5705.testinggrounds.screen;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.spudacious5705.testinggrounds.block.custom.ShopBlock;
import net.spudacious5705.testinggrounds.block.entity.ImplementedInventory;
import net.spudacious5705.testinggrounds.block.entity.ShopEntity;
import org.jetbrains.annotations.Nullable;

public class ShopScreenHandler extends ScreenHandler {
    private final Inventory shopInventory;
    private final PropertyDelegate propertyDelegate;
    public final ShopEntity shop;
    private int userUUIDhash;
    private final PlayerInventory playerInv;

    public ShopScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, playerInventory.player.getWorld().getBlockEntity(buf.readBlockPos()),
                new ArrayPropertyDelegate(1));
    }

    private final boolean screenTypeSeller;



    private  static final int PAYMENT_SLOT = 76;
    private  static final int VENDING_SLOT = 77;
    private  static final int SET_PAYMENT_SLOT = 78;
    private  static final int SET_VENDING_SLOT = 79;
    private static final int profit_itemStacks_start = 54;
    private static final int profit_itemStacks_range = 21;
    private static final int stock_itemStacks_start = 0;
    private static final int stock_itemStacks_range = 53;

    public ShopScreenHandler(int syncId, PlayerInventory playerInventory, BlockEntity blockEntity, PropertyDelegate arrayPropertyDelegate) {
        super(ModScreenHandlers.SHOP_SCREEN_HANDLER, syncId);
        checkSize(((Inventory) blockEntity), 80 );
        this.shopInventory = ((Inventory) blockEntity);
        playerInventory.onOpen(playerInventory.player);
        this.propertyDelegate = arrayPropertyDelegate;
        this.shop = (ShopEntity) blockEntity;
        this.userUUIDhash = playerInventory.player.getUuid().hashCode();
        this.playerInv = playerInventory;

        this.addSlot(new Slot(shopInventory, 78, 20, 11));
        this.addSlot(new Slot(shopInventory, 79, 20, 59));


        this.screenTypeSeller = false;
        this.addProperties(arrayPropertyDelegate);



    }

    public void addCustomerInventory() {

    }

    public void addShopInventory(){
        int offsetx = 34;
        int offsety = -33;
        for (int i = 0; i<6; ++i){
            for (int j = 0; j<9; ++j){
                this.addSlot(new Slot(shopInventory,j+i*9,offsetx + j*18,offsety + i*18));
            }
        }
        offsetx = -10;
        offsety = 79;

        for (int i = 0; i<11; ++i){
            this.addSlot(new Slot(shopInventory,profit_itemStacks_start+i,offsetx+i*18,offsety));
        }
        offsety = 97;

        for (int i = 0; i<11; ++i){
            this.addSlot(new Slot(shopInventory,profit_itemStacks_start+11+i,offsetx+i*18,offsety));
        }


    }


    public void addPlayerInventory(boolean sts) {
        if (sts) {
            addPlayerInventory(playerInv,7,129);
        } else {
            addPlayerInventory(playerInv,8,84);
        }
    }

    private void addPlayerInventory(PlayerInventory playerInventory, int offsetx, int offsety) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, offsetx + l * 18, offsety + i * 18));
            }
        }
        offsety += 58;
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, offsetx + i * 18, offsety));
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.shopInventory.size()) {
                if (!this.insertItem(originalStack, this.shopInventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.shopInventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return newStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.shopInventory.canPlayerUse(player);
    }

    public boolean isScreenTypeSeller() {
        return propertyDelegate.get(0) == userUUIDhash;
    }

    class shop_payment_slot extends Slot {

        private final ShopEntity shop;

        public shop_payment_slot(Inventory inventory, int index, int x, int y, ShopEntity shop1) {
            super(inventory, index, x, y);
            this.shop = shop1;
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return stack.isOf(shop.getPaymentType());
        }
    }

    class shop_vendor_slot extends Slot {
        private final ShopEntity shop;
        public shop_vendor_slot(Inventory inventory, int index, int x, int y, ShopEntity shop1) {
            super(inventory, index, x, y);
            this.shop = shop1;

        }

        @Override
        protected void onTake(int amount) {
            super.onTake(amount);
        }

        @Override
        public ItemStack takeStack(int amount) {

            return super.takeStack(amount);
        }



        @Override
        public boolean canInsert(ItemStack stack) {
            return false;
        }
    }

    class shop_set_slot extends Slot {

        public shop_set_slot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public ItemStack takeStack(int amount) {
            super.takeStack(amount);
            return ItemStack.EMPTY;

        }

        @Override
        public boolean canInsert(ItemStack stack) {
            setStack(stack);
            return false;
        }

        @Override
        public boolean canTakePartial(PlayerEntity player) {
            return false;
        }
    }
}
