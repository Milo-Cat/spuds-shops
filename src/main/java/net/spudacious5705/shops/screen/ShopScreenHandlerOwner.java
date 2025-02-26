package net.spudacious5705.shops.screen;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.spudacious5705.shops.block.entity.ShopEntity;

public class ShopScreenHandlerOwner extends ScreenHandler {
    private final Inventory shopInventory;
    private final PropertyDelegate propertyDelegate;
    public final ShopEntity shop;

    public ShopScreenHandlerOwner(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, playerInventory.player.getWorld().getBlockEntity(buf.readBlockPos()),
                new ArrayPropertyDelegate(1));
    }




    private  static final int PAYMENT_SLOT = 76;
    private  static final int VENDING_SLOT = 77;
    private static final int profit_itemStacks_start = 54;
    private static final int profit_itemStacks_range = 21;
    private static final int stock_itemStacks_start = 0;
    private static final int stock_itemStacks_range = 53;

    public ShopScreenHandlerOwner(int syncId, PlayerInventory playerInventory, BlockEntity blockEntity, PropertyDelegate arrayPropertyDelegate) {
        super(ModScreenHandlers.SHOP_SCREEN_HANDLER_OWNER, syncId);
        checkSize(((Inventory) blockEntity), 78 );
        this.shopInventory = ((Inventory) blockEntity);
        playerInventory.onOpen(playerInventory.player);
        this.propertyDelegate = arrayPropertyDelegate;
        this.shop = (ShopEntity) blockEntity;


        addPlayerInventory(playerInventory);

        addShopInventory();

        this.addSlot(new shop_set_slot(shopInventory, PAYMENT_SLOT,23,11));
        this.addSlot(new shop_set_slot(shopInventory, VENDING_SLOT,23,49));


        this.addProperties(arrayPropertyDelegate);



    }

    public void addShopInventory(){
        int offsetx = 60;
        int offsety = 10;

        for (int i = 0; i<6; ++i){
            for (int j = 0; j<9; ++j){
                this.addSlot(new Slot(shopInventory,j+i*9,offsetx + j*18,offsety + i*18));
            }
        }
        offsetx += -44;
        offsety += 112;

        for (int i = 0; i<11; ++i){
            this.addSlot(new Slot(shopInventory,profit_itemStacks_start+i,offsetx+i*18,offsety));
        }
        offsety += 18;

        for (int i = 0; i<11; ++i){
            this.addSlot(new Slot(shopInventory,profit_itemStacks_start+11+i,offsetx+i*18,offsety));
        }



    }


    private void addPlayerInventory(PlayerInventory playerInventory) {

        int offsetx = 33;
        int offsety = 172;

        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new player_slot(playerInventory, l + i * 9 + 9, offsetx + l * 18, offsety + i * 18));
            }
        }
        offsety += 58;
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new player_slot(playerInventory, i, offsetx + i * 18, offsety));
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot == null || !slot.hasStack()) {return newStack;}
        ItemStack originalStack = slot.getStack();
        newStack = originalStack.copy();

        if(this.slots.get(invSlot) instanceof shop_set_slot){return ItemStack.EMPTY;}

        if(this.slots.get(invSlot) instanceof player_slot){
            if (!this.insertItem(originalStack, 36, 90, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (!this.insertItem(originalStack, 0, 35, false)) {
                return ItemStack.EMPTY;
            }
        }


        if (originalStack.isEmpty()) {
            slot.setStack(ItemStack.EMPTY);
        } else {
            slot.markDirty();
        }

        return newStack;

    }

    @Override
    protected boolean insertItem(ItemStack stack, int startIndex, int endIndex, boolean fromLast) {
        return super.insertItem(stack, startIndex, endIndex, fromLast);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.shopInventory.canPlayerUse(player);
    }

    class player_slot extends Slot {

        public player_slot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        public boolean isPlayerSlot() {return true;}
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
            return this.getStack().getItem() == stack.getItem() || this.getStack().isEmpty();
        }



        @Override
        public ItemStack insertStack(ItemStack newStack, int count) {

            if (newStack.isEmpty()) {return newStack;}

            ItemStack thisStack = this.getStack();

            if (thisStack.isEmpty() || thisStack.getItem() != newStack.getItem()) {
                this.setStack(newStack.copy());
                return newStack;
            }

            int addition = thisStack.getCount() + count;

            if(addition>+64){
                thisStack.setCount(64);
            } else {
                thisStack.setCount(addition);
            }

            this.setStack(thisStack);

            return newStack;
        }

        @Override
        public boolean canTakePartial(PlayerEntity player) {
            return false;
        }
    }
}
