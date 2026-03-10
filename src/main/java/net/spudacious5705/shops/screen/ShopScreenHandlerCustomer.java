package net.spudacious5705.shops.screen;


import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;
import net.spudacious5705.shops.block.entity.AbstractShopEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;


public class ShopScreenHandlerCustomer extends AbstractContainerMenu {
    private final AbstractShopEntity.InventoryDelegate shopInventory;
    final ScreenSettingsGroup SCREEN_SETTINGS;
    private final Inventory playerInventory;
    private final BlockPos pos;

    private final AbstractShopEntity.settings_Delegate SETTINGS_DELEGATE;
    private final boolean shopStyle;




    private  static final int PAYMENT_SLOT = 76;
    private  static final int VENDING_SLOT = 77;
    private static final int STOCK_END = 53;
    private static final int PROFIT_END = 75;



    public ShopScreenHandlerCustomer(int syncId, Inventory playerInv, FriendlyByteBuf buf) {//clientInit
        super(ModScreenHandlers.SHOP_SCREEN_HANDLER_CUSTOMER.get(), syncId);
        this.pos = buf.readBlockPos();
        boolean openTop = buf.readBoolean();
        Player player = playerInv.player;

        this.playerInventory = playerInv;

        if(player.level().getBlockEntity(pos) instanceof AbstractShopEntity shop) {
            AbstractShopEntity.InventoryDelegate inventoryDelegate = openTop ?
                    shop.getOtherInventoryDelegate(player)
                    :
                    shop.getInventoryDelegate(player);


            if (inventoryDelegate != null && inventoryDelegate.getContainerSize() != 78) {
                throw new IllegalArgumentException("Inventory size must be 78");
            }


            this.shopInventory = inventoryDelegate;

            this.SCREEN_SETTINGS = shop.getScreenSettings();

            this.SETTINGS_DELEGATE = shop.getSettingsReader();

            this.shopStyle = SETTINGS_DELEGATE.getState(ToggleButtonID.SelectableTradeToggle);

            finishSetup();
        } else {
            Minecraft.getInstance().setScreen(null);
            this.shopInventory = null;
            this.SCREEN_SETTINGS = null;
            this.SETTINGS_DELEGATE = null;
            this.shopStyle = false;
        }
    }

    public ShopScreenHandlerCustomer(int syncId, Inventory playerInv, AbstractShopEntity shop, AbstractShopEntity.InventoryDelegate inventoryDelegate) {//serverInit
        super(ModScreenHandlers.SHOP_SCREEN_HANDLER_CUSTOMER.get(), syncId);
        this.shopInventory = shop.getInventoryDelegate(playerInv.player);
        this.pos = shop.getBlockPos();
        this.SCREEN_SETTINGS = null;
        this.SETTINGS_DELEGATE = shop.getSettingsReader();
        this.shopStyle = SETTINGS_DELEGATE.getState(ToggleButtonID.SelectableTradeToggle);
        this.playerInventory = playerInv;
        shop.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(iItemHandler -> {
            this.addSlot(new SlotItemHandler(iItemHandler, 0, 80, 11));
            this.addSlot(new SlotItemHandler(iItemHandler, 1, 80, 59));
        });
        finishSetup();
    }



    private void finishSetup(){

        playerInventory.startOpen(playerInventory.player);


        addPlayerInventory(playerInventory);
        addCustomerInventory();

    }

    public ScreenSettingsGroup getSettings() {
        return this.SCREEN_SETTINGS;
    }

    public void addCustomerInventory() {
        if(shopStyle){
            int offsetx = 21;
            int offsety = 35;

            for (int i = 0; i < 6; ++i) {
                for (int j = 0; j < 9; ++j) {
                    this.addSlot(new shop_vendor_slot(shopInventory, j + i * 9, offsetx + j * 21, offsety + i * 20));
                }
            }

            this.addSlot(new shop_payment_slot(shopInventory, PAYMENT_SLOT, 71, 20));
        } else {
            this.addSlot(new shop_payment_slot(shopInventory, PAYMENT_SLOT, 80 - 34, 11 + 25));
            this.addSlot(new shop_vendor_slot(shopInventory, VENDING_SLOT, 80 + 35, 59 - 23));
        }
    }

    public void addPlayerInventory(Inventory playerInv) {
        addPlayerInventory(playerInv,8,84);
    }

    private void addPlayerInventory(Inventory playerInventory, int offsetx, int offsety) {
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
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int pIndex) {
        /*
        int tradeCount = 0;//todo fix this
        while(tradeCount<64&&shopInventory.canTrade(player)){
            trade();
            tradeCount++;
        }*/
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return this.playerInventory.player.level().getBlockEntity(this.pos) instanceof AbstractShopEntity &&
                player.distanceToSqr(this.pos.getCenter()) <= 64.0;
    }

    static class shop_payment_slot extends Slot {

        public shop_payment_slot(AbstractShopEntity.InventoryDelegate inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public @NotNull Optional<ItemStack> tryRemove(int pCount, int pDecrement, Player pPlayer) {
            return Optional.empty();
        }

        @Override
        public boolean mayPlace(ItemStack pStack) {
            return false;
        }

        @Override
        public boolean mayPickup(Player pPlayer) {
            return false;
        }

        @Override
        public ItemStack safeInsert(ItemStack pStack) {
            return pStack;
        }

        @Override
        public ItemStack safeInsert(ItemStack pStack, int pIncrement) {
            return pStack;
        }

        @Override
        public ItemStack safeTake(int pCount, int pDecrement, Player pPlayer) {
            return ItemStack.EMPTY;
        }

        /**
         * DO NOT OVERRIDE
         * this method is for syncing and not accessible by the player
        @Override
        public void set(ItemStack pStack)
         **/

        @Override
        public void setByPlayer(ItemStack pStack) {
        }
    }

    class shop_vendor_slot extends Slot {
        public shop_vendor_slot(AbstractShopEntity.InventoryDelegate inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public @NotNull Optional<ItemStack> tryRemove(int pCount, int pDecrement, Player pPlayer) {
            if(this.hasItem())attemptTrade(this.getSlotIndex(), pPlayer);
            return Optional.empty();
        }

        @Override
        public ItemStack safeTake(int pCount, int pDecrement, Player pPlayer) {
            if(this.hasItem())attemptTrade(this.getSlotIndex(), pPlayer);
            return ItemStack.EMPTY;
        }

        @Override
        public boolean mayPickup(@NotNull Player playerEntity) {
            return shopInventory.canTrade(playerEntity);
        }

        @Override
        public boolean mayPlace(ItemStack s) {
            return false;
        }

        @Override
        public ItemStack safeInsert(ItemStack pStack) {
            return pStack;
        }

        @Override
        public ItemStack safeInsert(ItemStack pStack, int pIncrement) {
            return pStack;
        }



    }

    private void attemptTrade(int index, Player player){
        if(SETTINGS_DELEGATE.getState(ToggleButtonID.SelectableTradeToggle) != shopStyle){
            if(player instanceof ServerPlayer sp) {
                sp.doCloseContainer();
            }
            return;
        }
        if(shopInventory.canTrade(player)){
            if(shopStyle){//standard trade
                this.shopInventory.tradeWithSelection(playerInventory, index);
            } else {//selectable trade
                this.shopInventory.trade(playerInventory);
            }
        }
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        playerInventory.stopOpen(player); // Notify close
    }

}