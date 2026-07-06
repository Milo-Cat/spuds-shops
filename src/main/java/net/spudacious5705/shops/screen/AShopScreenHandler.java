package net.spudacious5705.shops.screen;


import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.spudacious5705.shops.block.entity.AbstractShopEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AShopScreenHandler extends AbstractContainerMenu {

    public final AbstractShopEntity.InventoryDelegate shopInventory;
    protected final AbstractShopEntity.settings_Delegate SETTINGS_DELEGATE;
    public final Inventory playerInventory;
    protected final ScreenSettingsGroup SCREEN_SETTINGS;


    protected AShopScreenHandler(@Nullable MenuType<?> pMenuType, int pContainerId, AbstractShopEntity.InventoryDelegate shopInventory, AbstractShopEntity.settings_Delegate settingsDelegate, Inventory playerInventory, ScreenSettingsGroup screenSettings) {
        super(pMenuType, pContainerId);
        this.shopInventory = shopInventory;
        SETTINGS_DELEGATE = settingsDelegate;
        this.playerInventory = playerInventory;
        SCREEN_SETTINGS = screenSettings;
    }


    protected AShopScreenHandler(@Nullable MenuType<?> pMenuType, int pContainerId, Inventory playerInventory, boolean openTop, AbstractShopEntity shop) {
        super(pMenuType, pContainerId);

        Player player = playerInventory.player;

        this.playerInventory = playerInventory;


        AbstractShopEntity.InventoryDelegate inventoryDelegate = openTop ?
                shop.getOtherInventoryDelegate(player)
                :
                shop.getInventoryDelegate(player);


        if (inventoryDelegate != null && inventoryDelegate.getContainerSize() != 78) {
            throw new IllegalArgumentException("Inventory size must be 78");
        }


        this.shopInventory = inventoryDelegate;

        this.SCREEN_SETTINGS = shop.getScreenSettings();

        this.SETTINGS_DELEGATE = shop.getSettingsDelegate(player);

    }

    protected void attemptTrade(int index, Player player){

        if(shopInventory.canTrade(player)){
            if(SETTINGS_DELEGATE.getState(ToggleButtonID.SelectableTradeToggle)){//standard trade
                this.shopInventory.tradeWithSelection(playerInventory, index);
            } else {//selectable trade
                this.shopInventory.trade(playerInventory);
            }
        }
    }

    protected static class TogglableSlot extends Slot {
        private boolean toggled = true;
        public TogglableSlot(Container inventory, int slot, int x, int y) {
            super(inventory, slot, x, y);
        }

        @Override
        public boolean isActive() {
            return toggled;
        }

        public void enable(){
            toggled=true;
        }

        public void disable(){
            toggled=false;
        }


    }

    protected class shop_vendor_slot extends TogglableSlot {
        public shop_vendor_slot(AbstractShopEntity.InventoryDelegate inventory, int index, int x, int y) {
            super(inventory, index, x, y);
            addSlot(this);
            this.disable();
        }

        @Override
        public @NotNull Optional<ItemStack> tryRemove(int pCount, int pDecrement, @NotNull Player pPlayer) {
            if(this.hasItem())attemptTrade(this.index, pPlayer);
            return Optional.empty();
        }

        @NotNull
        @Override
        public ItemStack safeTake(int pCount, int pDecrement, @NotNull Player pPlayer) {
            if(this.hasItem())attemptTrade(this.index, pPlayer);
            return ItemStack.EMPTY;
        }

        @Override
        public boolean mayPickup(@NotNull Player playerEntity) {
            return true;//shopInventory.canTrade(playerEntity);
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack s) {
            return false;
        }

        @NotNull
        @Override
        public ItemStack safeInsert(@NotNull ItemStack pStack) {
            return pStack;
        }

        @NotNull
        @Override
        public ItemStack safeInsert(@NotNull ItemStack pStack, int pIncrement) {
            return pStack;
        }
    }

    protected class shop_payment_slot extends TogglableSlot {

        public shop_payment_slot(AbstractShopEntity.InventoryDelegate inventory, int slot, int x, int y) {
            super(inventory, slot, x, y);
            addSlot(this);
            this.disable();
        }

        @Override
        public @NotNull Optional<ItemStack> tryRemove(int pCount, int pDecrement, @NotNull Player pPlayer) {
            return Optional.empty();
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack pStack) {
            return false;
        }

        @Override
        public boolean mayPickup(@NotNull Player pPlayer) {
            return false;
        }

        @NotNull
        @Override
        public ItemStack safeInsert(@NotNull ItemStack pStack) {
            return pStack;
        }

        @NotNull
        @Override
        public ItemStack safeInsert(@NotNull ItemStack pStack, int pIncrement) {
            return pStack;
        }

        @NotNull
        @Override
        public ItemStack safeTake(int pCount, int pDecrement, @NotNull Player pPlayer) {
            return ItemStack.EMPTY;
        }

        /**
         * DO NOT OVERRIDE
         * this method is for syncing and not accessible by the player
         public void set(ItemStack pStack)
         **/

        @Override
        public void setByPlayer(@NotNull ItemStack pStack) {
        }
    }

    protected final List<TogglableSlot> playerInvSlots = new ArrayList<>();

    protected class player_slot extends TogglableSlot {
        /**
         *
         * Automatically adds itself to the neccecary lists
         */
        public player_slot(Inventory inventory, int slot, int x, int y) {
            super(inventory, slot, x, y);
            playerInvSlots.add(this);
            addSlot(this);
        }

    }
    protected int[] lockedDownSlots = new int[2];

    @Override
    public void clicked(int pSlotId, int pButton, @NotNull ClickType pClickType, @NotNull Player pPlayer) {
        for (int i : lockedDownSlots) if (i == pSlotId) return;
        super.clicked(pSlotId, pButton, pClickType, pPlayer);
    }

    protected int monoVendorSlotIndex;

    protected final List<TogglableSlot> tabTradeMonoSlots = new ArrayList<>();
    protected final List<TogglableSlot> tabTradeSelectSlots = new ArrayList<>();

    protected void updateTradeSlots(boolean enable){
        if(enable){
            if(SETTINGS_DELEGATE.getState(ToggleButtonID.SelectableTradeToggle)){
                tabTradeSelectSlots.forEach(TogglableSlot::enable);
                tabTradeMonoSlots.forEach(TogglableSlot::disable);
            } else {
                tabTradeSelectSlots.forEach(TogglableSlot::disable);
                tabTradeMonoSlots.forEach(TogglableSlot::enable);
            }
        } else {
            tabTradeSelectSlots.forEach(TogglableSlot::disable);
            tabTradeMonoSlots.forEach(TogglableSlot::disable);
        }
    }

    protected void addPlayerInventory(Inventory playerInventory, int offsetX, int offsetY) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                new player_slot(playerInventory, l + i * 9 + 9, offsetX + l * 18, offsetY + i * 18);
            }
        }
        offsetY += 58;
        for (int i = 0; i < 9; ++i) {
            new player_slot(playerInventory, i, offsetX + i * 18, offsetY);
        }
    }
    protected ItemStack executeQuickMove(int invSlot, int startIndex, int endIndex){
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (!slot.hasItem()) {return newStack;}
        ItemStack originalStack = slot.getItem();
        newStack = originalStack.copy();

        if (!this.moveItemStackTo(originalStack, startIndex, endIndex, false)) {
            return ItemStack.EMPTY;
        }

        if (originalStack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        return newStack;
    }

    abstract public ResourceLocation getBackgroundTexture();

    @Override
    public boolean stillValid(@NotNull Player player) {
        return this.shopInventory.stillValid(player);
    }
    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);
        playerInventory.stopOpen(player); // Notify close
    }
}
