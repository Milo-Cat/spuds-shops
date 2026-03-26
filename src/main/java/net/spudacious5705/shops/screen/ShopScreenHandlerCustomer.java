package net.spudacious5705.shops.screen;


import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.spudacious5705.shops.block.entity.AbstractShopEntity;
import net.spudacious5705.shops.screen.owner_screen.ShopScreenHandlerOwner;
import org.jetbrains.annotations.NotNull;


public class ShopScreenHandlerCustomer extends AShopScreenHandler {


    private static final int PAYMENT_SLOT = 76;
    private static final int VENDING_SLOT = 77;
    private static final int STOCK_END = 53;
    private static final int PROFIT_END = 75;
    private int playerInvEnd;
    private boolean lastState = false;

    public ShopScreenHandlerCustomer(int syncId, Inventory playerInventory, BlockPos pos, boolean openTop, AbstractShopEntity shop) {//clientInit
        super(ModScreenHandlers.SHOP_SCREEN_HANDLER_CUSTOMER.get(), syncId, playerInventory, openTop, shop);

        finishSetup();

    }


    public ShopScreenHandlerCustomer(int syncId, Inventory playerInventory, AbstractShopEntity shop, AbstractShopEntity.InventoryDelegate inventoryDelegate) {//serverInit
        super(ModScreenHandlers.SHOP_SCREEN_HANDLER_CUSTOMER.get(), syncId, inventoryDelegate, shop.getSettingsReader(), playerInventory, null);

        /*shop.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(iItemHandler -> {
            this.addSlot(new SlotItemHandler(iItemHandler, 0, 80, 11));
            this.addSlot(new SlotItemHandler(iItemHandler, 1, 80, 59));
        });*/
        finishSetup();
    }

    public static ShopScreenHandlerCustomer create(int syncId, Inventory playerInv, FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        boolean openTop = buf.readBoolean();
        Player player = playerInv.player;
        if (player.level().getBlockEntity(pos) instanceof AbstractShopEntity shop) {
            return new ShopScreenHandlerCustomer(syncId, playerInv, pos, openTop, shop);
        }

        Minecraft.getInstance().setScreen(null);
        return null;
    }

    private void finishSetup() {

        playerInventory.startOpen(playerInventory.player);


        addPlayerInventory(playerInventory, 33, 174);
        playerInvEnd = slots.size() - 1;
        addCustomerInventory();

    }

    private void addCustomerInventory() {

        int offsetx = 21;
        int offsety = 35;

        for (int i = 0; i < 6; ++i) {
            for (int j = 0; j < 9; ++j) {
                shop_vendor_slot slot = new shop_vendor_slot(shopInventory, j + i * 9, offsetx + j * 21, offsety + i * 20);
                slot.enable();
                tabTradeSelectSlots.add(slot);
            }
        }

        shop_payment_slot lockedSlot = new shop_payment_slot(shopInventory, PAYMENT_SLOT, 105, 12);
        lockedDownSlots[0] = lockedSlot.index;
        tabTradeSelectSlots.add(lockedSlot);
        lockedSlot.enable();

        lockedSlot = new shop_payment_slot(shopInventory, PAYMENT_SLOT, 71, 126);
        lockedDownSlots[1] = lockedSlot.index;
        tabTradeMonoSlots.add(lockedSlot);
        lockedSlot.enable();

        ShopScreenHandlerOwner.shop_vendor_slot monoVendorSlot = new shop_vendor_slot(shopInventory, VENDING_SLOT, 140, 126);
        monoVendorSlotIndex = monoVendorSlot.index;
        tabTradeMonoSlots.add(monoVendorSlot);
        monoVendorSlot.enable();

        updateTradeSlots(true);

    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int invSlot) {
        if (!SETTINGS_DELEGATE.getState(ToggleButtonID.SelectableTradeToggle) && invSlot == monoVendorSlotIndex) {
            //do a bunch of trades
            int tradeCount = 0;
            while (tradeCount < 64 & shopInventory.canTrade(player)) {
                shopInventory.trade(playerInventory);
                tradeCount++;
            }
            return ItemStack.EMPTY;

        }
        if (invSlot <= playerInvEnd) {
            //quickmove within player inv
            if (invSlot < 9) {
                //from hotbar
                return executeQuickMove(invSlot, 9, playerInvEnd);
            } else {
                //from uhhh... not hotbar
                return executeQuickMove(invSlot, 0, 8);
            }
        }

        return ItemStack.EMPTY;
    }

    public Identifier getBackgroundTexture() {
        boolean state = SETTINGS_DELEGATE.getState(ToggleButtonID.SelectableTradeToggle);
        if (state != lastState) {
            updateTradeSlots(true);
            lastState = state;
        }
        return state ?
                SCREEN_SETTINGS.CUSTOMER_MULTI().textureID() :
                SCREEN_SETTINGS.CUSTOMER().textureID();
    }

    public Boolean showNBToffNotif() {
        return SETTINGS_DELEGATE.getState(ToggleButtonID.IgnoreNBTToggle) && !SETTINGS_DELEGATE.getState(ToggleButtonID.SelectableTradeToggle);
    }
}