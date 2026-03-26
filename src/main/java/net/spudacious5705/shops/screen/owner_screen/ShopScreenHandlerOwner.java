package net.spudacious5705.shops.screen.owner_screen;


import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.network.PacketDistributor;
import net.spudacious5705.shops.block.entity.AbstractShopEntity;
import net.spudacious5705.shops.config.ConfigHandler;
import net.spudacious5705.shops.item.ModItems;
import net.spudacious5705.shops.permission.PermissionLevel;
import net.spudacious5705.shops.permission.PermissionManager;
import net.spudacious5705.shops.screen.AShopScreenHandler;
import net.spudacious5705.shops.screen.ModScreenHandlers;
import net.spudacious5705.shops.screen.ScreenSettingsGroup;
import net.spudacious5705.shops.screen.ToggleButtonID;
import net.spudacious5705.shops.screen.networking.ShopTabSyncPkt;
import net.spudacious5705.shops.screen.networking.ToggleSyncPkt;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static net.spudacious5705.shops.block.entity.ShopInventory.PAYMENT_SLOT;
import static net.spudacious5705.shops.block.entity.ShopInventory.VENDING_SLOT;
import static net.spudacious5705.shops.permission.PermissionManager.player_ID_Records_Delegate.checkAction;
import static net.spudacious5705.shops.screen.ScreenResources.WARNING_TEXTURE;

public class ShopScreenHandlerOwner extends AShopScreenHandler {

    //region variables

    @MagicConstant
    static final int SELLER_TAB = 1;
    @MagicConstant
    static final int SETTINGS_TAB = 2;
    @MagicConstant
    static final int CUSTOMER_TAB = 3;
    @MagicConstant
    static final int WARNING_TAB = 4;
    private static final int profit_itemStacks_start = 54;
    private static final int EXPECTED_CONTAINER_SIZE = 78;
    final PermissionLevel perms;
    final List<TogglableSlot> tabSettingsSlots = new ArrayList<>();
    private final PermissionManager<AbstractShopEntity>.player_ID_Records_Delegate ID_RECORDS_DELEGATE;
    private final List<TogglableSlot> tabSellerSlots = new ArrayList<>();
    shop_trade_slot PaymentSlot;


    //endregion variables

    //region utilities
    shop_trade_slot VendingSlot;
    shop_trade_slot[] TradeDefSlots;
    private int activeTab = SELLER_TAB;
    private int playerInvEnd;
    private int shopInvEnd;
    private int tradeInvEnd;
    private int contractsInvEnd;

    public ShopScreenHandlerOwner(int syncId, Inventory playerInventory, BlockPos pos, boolean openTop, AbstractShopEntity shop) {//clientInit
        super(ModScreenHandlers.SHOP_SCREEN_HANDLER_OWNER.get(), syncId, playerInventory, openTop, shop);
        Player player = playerInventory.player;

        this.perms = shopInventory.checkPermissions();

        ID_RECORDS_DELEGATE = shop.getRecordsDelegate(player);

        finishSetup();
    }

    //endregion utilities

    //region menu open/setup/close

    public ShopScreenHandlerOwner(
            int syncId,
            Inventory playerInventory,
            AbstractShopEntity shop,
            AbstractShopEntity.InventoryDelegate inventoryDelegate,
            PermissionManager<AbstractShopEntity>.player_ID_Records_Delegate recordsDelegate,
            AbstractShopEntity.settings_Delegate settingsDelegate
    ) {//serverInit
        super(ModScreenHandlers.SHOP_SCREEN_HANDLER_OWNER.get(), syncId, inventoryDelegate, settingsDelegate, playerInventory, null);
        checkContainerSize(shopInventory, EXPECTED_CONTAINER_SIZE);
        this.perms = shopInventory.checkPermissions();
        this.ID_RECORDS_DELEGATE = recordsDelegate;

        finishSetup();
    }

    public static void playWarnSound(@NotNull Player player) {
        player.playSound(
                SoundEvents.NOTE_BLOCK_GUITAR.value()
        );
    }

    public static ShopScreenHandlerOwner create(int syncId, Inventory playerInv, FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        boolean openTop = buf.readBoolean();

        Player player = playerInv.player;
        if (player.level().getBlockEntity(pos) instanceof AbstractShopEntity shop) {
            return new ShopScreenHandlerOwner(syncId, playerInv, pos, openTop, shop);
        }

        Minecraft.getInstance().setScreen(null);
        return null;
    }

    public void selfDemotePlayer(Player player) {
        if (activeTab == WARNING_TAB) {
            ID_RECORDS_DELEGATE.selfDemote(player);
        }
    }

    public int getActiveTab() {
        return activeTab;
    }

    public boolean isPlayerCreative() {
        return SETTINGS_DELEGATE.isPlayerCreative();
    }

    public Identifier getBackgroundTexture() {
        return switch (activeTab) {
            case SETTINGS_TAB -> SCREEN_SETTINGS.SETTINGS().textureID();
            case CUSTOMER_TAB -> SETTINGS_DELEGATE.getState(ToggleButtonID.SelectableTradeToggle) ?
                    SCREEN_SETTINGS.CUSTOMER_MULTI().textureID() :
                    SCREEN_SETTINGS.CUSTOMER().textureID();
            case WARNING_TAB -> WARNING_TEXTURE;
            default -> SCREEN_SETTINGS.SELLER().textureID(); //SELLER or ERROR
        };
    }

    public ScreenSettingsGroup getSettings() {
        return this.SCREEN_SETTINGS;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean getStateOfSetting(ToggleButtonID button) {
        return SETTINGS_DELEGATE.getState(button);
    }

    private void openWarnScreen(@NotNull Player player) {//called when player removes their own contract
        if (player.level().isClientSide()) {
            playWarnSound(player);
            ClientPacketDistributor.sendToServer(new ShopTabSyncPkt(WARNING_TAB));
        } else {
            activeTab = WARNING_TAB;
        }
    }

    private void finishSetup() {


        addPlayerInventory(playerInventory, 33, 174);
        playerInventory.startOpen(playerInventory.player);
        playerInvEnd = slots.size() - 1;

        addShopInventory();
        shopInvEnd = slots.size() - 1;

        addShopTrades();
        tradeInvEnd = slots.size() - 1;

        addContractSlots();
        contractsInvEnd = slots.size() - 1;

        activeTab = SELLER_TAB;


    }

    private void addContractSlots() {
        int offsetx = 81;
        int offsety = 69;

        for (int y = 0; y < 4; y++) {
            for (int i = 0; i < 6; ++i) {
                new contract_slot(ID_RECORDS_DELEGATE, y * 6 + i, offsetx + i * 23, offsety + y * 23);
            }
        }
    }

    private void addShopTrades() {
        int x = 25;
        int y = 31;

        PaymentSlot = new shop_trade_slot(shopInventory, PAYMENT_SLOT, x, y);
        VendingSlot = new shop_trade_slot(shopInventory, VENDING_SLOT, x, y + 47);
        TradeDefSlots = new shop_trade_slot[]{PaymentSlot, VendingSlot};

        int offsetx = 21;
        int offsety = 35;

        for (int i = 0; i < 6; ++i) {
            for (int j = 0; j < 9; ++j) {
                tabTradeSelectSlots.add(new shop_vendor_slot(shopInventory, j + i * 9, offsetx + j * 21, offsety + i * 20));
            }
        }

        shop_payment_slot lockedSlot = new shop_payment_slot(shopInventory, PAYMENT_SLOT, 105, 12);
        lockedDownSlots[0] = lockedSlot.index;
        tabTradeSelectSlots.add(lockedSlot);

        lockedSlot = new shop_payment_slot(shopInventory, PAYMENT_SLOT, 71, 126);
        lockedDownSlots[1] = lockedSlot.index;
        tabTradeMonoSlots.add(lockedSlot);

        shop_vendor_slot monoVendorSlot = new shop_vendor_slot(shopInventory, VENDING_SLOT, 140, 126);
        monoVendorSlotIndex = monoVendorSlot.index;
        tabTradeMonoSlots.add(monoVendorSlot);


    }

    public void close() {
        playerInventory.player.closeContainer();
    }

    private void addShopInventory() {
        int offsetx = 59;
        int offsety = 15;

        for (int i = 0; i < 6; ++i) {
            for (int j = 0; j < 9; ++j) {
                createShopInvSlot(j + i * 9, offsetx + j * 18, offsety + i * 18);
            }
        }
        offsetx -= 44;
        offsety += 110;

        for (int i = 0; i < 11; ++i) {
            createShopInvSlot(profit_itemStacks_start + i, offsetx + i * 18, offsety);
        }
        offsety += 18;

        for (int i = 0; i < 11; ++i) {
            createShopInvSlot(profit_itemStacks_start + 11 + i, offsetx + i * 18, offsety);
        }

    }

    private void createShopInvSlot(int slotIndex, int x, int y) {
        TogglableSlot slot = new TogglableSlot(shopInventory, slotIndex, x, y);
        tabSellerSlots.add(slot);
        addSlot(slot);
    }


    //endregion menu open/setup/close

    //region interactions

    //item slots
    @Override
    public void clicked(int pSlotId, int pButton, @NotNull ClickType pClickType, @NotNull Player pPlayer) {
        if (pClickType != ClickType.QUICK_MOVE) {
            if (pSlotId == PaymentSlot.index) {
                tradeWindowPress(PaymentSlot, pButton);
                return;
            } else if (pSlotId == VendingSlot.index) {
                tradeWindowPress(VendingSlot, pButton);
                return;
            }
        }
        super.clicked(pSlotId, pButton, pClickType, pPlayer);//locked down slots handled in super

    }

    void tradeWindowPress(shop_trade_slot slot, int button) {
        if (!perms.canEditTrades()) return;

        ItemStack itemstack = getCarried();
        if (itemstack.isEmpty()) {

            if (slot.hasItem()) {
                int count = slot.getItem().getCount();
                if (count > 0) {
                    if (button == 1) {
                        count = (slot.getItem().getCount() + 1) / 2;
                    }
                    slot.remove(count);
                }
            }


        } else {
            if (itemstack.getItem() == slot.getItem().getItem()) {
                int count = slot.getItem().getCount() + itemstack.getCount();
                int maxCount = itemstack.getMaxStackSize() * ConfigHandler.stackSizeMultiplier;
                count = Math.min(maxCount, count);

                slot.set(itemstack.copyWithCount(count));

            } else if (slot.getItem().isEmpty()) {
                slot.set(itemstack.copy());
            } else {
                slot.set(ItemStack.EMPTY);
            }
        }
    }

    @Override
    protected boolean moveItemStackTo(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
        return super.moveItemStackTo(stack, startIndex, endIndex, reverseDirection);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int invSlot) {
        if (activeTab == CUSTOMER_TAB) {
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
        if (activeTab != SELLER_TAB) {
            return ItemStack.EMPTY;
        }

        if (invSlot > shopInvEnd) {
            //do not quick move from protected slots
            return ItemStack.EMPTY;
        }

        //quick move between player and shop inventory
        if (invSlot <= playerInvEnd) {
            return executeQuickMove(invSlot, playerInvEnd + 1, shopInvEnd);
        }

        return executeQuickMove(invSlot, 0, playerInvEnd);
    }

    //tab/screen
    @OnlyIn(Dist.CLIENT)
    public void updateTabSelectionClientside(int tab) {
        activeTab = tab;
        ClientPacketDistributor.sendToServer(new ShopTabSyncPkt(activeTab));
        updateTabSelection();
    }

    public void updateTabSelectionServerside(int tab) {
        activeTab = tab;
    }

    @OnlyIn(Dist.CLIENT)
    public void updateTabSelectionResponse(int tab) {
        activeTab = tab;
        updateTabSelection();
    }

    public void updateTabSelection() {
        switch (activeTab) {
            case SETTINGS_TAB -> {
                tabSettingsSlots.forEach(TogglableSlot::enable);
                playerInvSlots.forEach(TogglableSlot::enable);

                tabSellerSlots.forEach(TogglableSlot::disable);
                updateTradeSlots(false);
            }
            case CUSTOMER_TAB -> {
                playerInvSlots.forEach(TogglableSlot::enable);
                updateTradeSlots(true);

                tabSettingsSlots.forEach(TogglableSlot::disable);
                tabSellerSlots.forEach(TogglableSlot::disable);
            }
            case WARNING_TAB -> {
                tabSettingsSlots.forEach(TogglableSlot::disable);
                tabSellerSlots.forEach(TogglableSlot::disable);
                playerInvSlots.forEach(TogglableSlot::disable);
                updateTradeSlots(false);
            }
            default -> { //SELLER_TAB
                tabSellerSlots.forEach(TogglableSlot::enable);
                playerInvSlots.forEach(TogglableSlot::enable);

                tabSettingsSlots.forEach(TogglableSlot::disable);
                updateTradeSlots(false);
            }
        }
    }

    //settings buttons
    public boolean toggleButtonServersideUpdate(ToggleButtonID button, boolean state) {
        //SpudaciousShops.LOGGER.debug("packet received: {} - {}", button.getSerialised(), state);
        if (SETTINGS_DELEGATE.attemptSetState(button, state)) {
            shopInventory.setChanged();
        }
        return SETTINGS_DELEGATE.getState(button);
    }

    @OnlyIn(Dist.CLIENT)
    public void updateToggleButtonFromPacket(ToggleButtonID button, boolean state) {
        SETTINGS_DELEGATE.attemptSetState(button, state);
    }

    @OnlyIn(Dist.CLIENT)
    public void handleToggleButtonInput(ToggleButtonID button) {
        boolean state = !SETTINGS_DELEGATE.getState(button);
        if (SETTINGS_DELEGATE.attemptSetState(button, state)) {
            ClientPacketDistributor.sendToServer(new ToggleSyncPkt(button, state));
        }
    }

    //endregion interactions

    //region widgets

    class contract_slot extends TogglableSlot {

        private final PermissionManager<AbstractShopEntity>.player_ID_Records_Delegate contract_delegate;

        /**
         *
         * Automatically adds itself to the neccecary lists
         */
        public contract_slot(PermissionManager<AbstractShopEntity>.player_ID_Records_Delegate inventory, int slot, int x, int y) {
            super(inventory, slot, x, y);
            tabSettingsSlots.add(this);
            this.contract_delegate = inventory;
            addSlot(this);
            this.disable();
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack) {
            if (checkAction(stack, this.getSlotIndex())) {
                return this.getItem().isEmpty();
            }
            return false;
        }

        @Override
        public @NotNull ItemStack safeInsert(@NotNull ItemStack stack) {
            return this.safeInsert(stack, 0);
        }

        @Override
        public @NotNull ItemStack safeInsert(ItemStack stack, int count) {
            if (!stack.isEmpty() && stack.getItem() == ModItems.CONTRACT_SCROLL.get()) {

                return ((PermissionManager.player_ID_Records_Delegate) container).insertContract(stack, this.getSlotIndex());
            }
            return stack;
        }

        @Override
        public @NotNull ItemStack safeTake(int pCount, int pDecrement, @NotNull Player pPlayer) {
            if (contract_delegate.belongsToInteractor(this.getItem())) {
                openWarnScreen(pPlayer);
                return ItemStack.EMPTY;
            }
            return container.removeItem(this.getSlotIndex(), 1);
        }

        @Override
        public @NotNull Optional<ItemStack> tryRemove(int pCount, int pDecrement, @NotNull Player pPlayer) {
            if (contract_delegate.belongsToInteractor(this.getItem())) {
                openWarnScreen(pPlayer);

                return Optional.empty();
            }
            return Optional.of(container.removeItem(this.getSlotIndex(), 1));
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public int getMaxStackSize(@NotNull ItemStack pStack) {
            return 1;
        }

        @Override
        public boolean mayPickup(@NotNull Player player) {
            if (contract_delegate.belongsToInteractor(this.getItem())) {
                return true;
            }
            return contract_delegate.canEditThat(this.getSlotIndex());
        }
        //always stack size of 1 or 0
    }

    class shop_trade_slot extends TogglableSlot {

        public shop_trade_slot(AbstractShopEntity.InventoryDelegate inventory, int slot, int x, int y) {
            super(inventory, slot, x, y);
            addSlot(this);
            this.disable();
        }

        @Override
        public int getMaxStackSize() {
            return this.getMaxStackSize(this.getItem());
        }

        @Override
        public int getMaxStackSize(@NotNull ItemStack pStack) {
            return pStack.getMaxStackSize() * ConfigHandler.stackSizeMultiplier;
        }

        /*
        @NotNull
        @Override
        public ItemStack safeTake(int amount, int shouldDecrement, @NotNull Player pPlayer) {
            this.container.removeItem(this.getSlotIndex(), amount);
            this.setChanged();
            return ItemStack.EMPTY;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            if(stack.getItem() != this.getItem().getItem()) {
                this.set(ItemStack.EMPTY);
                this.setChanged();
                return false;
            }
            return true;
        }

        @Override
        public boolean mayPickup(@NotNull Player pPlayer) {
            return perms.canEditTrades();
        }


        @Override
        public @NotNull Optional<ItemStack> tryRemove(int pCount, int pDecrement, @NotNull Player pPlayer) {
            if(perms.canEditTrades()){
                this.container.removeItem(this.getSlotIndex(), pCount);
                this.setChanged();
            }
            return Optional.empty();
        }

        @Override
        public @NotNull ItemStack remove(int pAmount) {
            if(perms.canEditTrades()){
                this.container.removeItem(this.getSlotIndex(), pAmount);
                this.setChanged();
            }
            return ItemStack.EMPTY;
        }

        @NotNull
        @Override
        public ItemStack safeInsert(@NotNull ItemStack stack) {
            safeInsert(stack,stack.getCount());
            return stack;
        }

        @NotNull
        @Override
        public ItemStack safeInsert(ItemStack stack, int count) {
            ItemStack oldStack = this.getItem();

            if(stack.getItem() == oldStack.getItem()){
                count += oldStack.getCount();
                if(count>256)count=256;
                this.container.setItem(this.getSlotIndex(),stack.copyWithCount(count));
            }else {
                this.container.setItem(this.getSlotIndex(), stack.copyWithCount(count));
            }
            this.setChanged();
            return stack;
        }


        @Override
        public void setByPlayer(@NotNull ItemStack pStack) {
            super.setByPlayer(pStack);
            this.setChanged();
        }*/

        /*
         * DO NOT OVERRIDE
         * this method is for syncing and not accessible by the player
         public void set(ItemStack pStack)
         */
    }

    //endregion widgets


}