package net.spudacious5705.shops.screen;


import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.spudacious5705.shops.SpudaciousShops;
import net.spudacious5705.shops.block.entity.AbstractShopEntity;
import net.spudacious5705.shops.item.ModItems;
import net.spudacious5705.shops.properties.PermissionLevel;
import net.spudacious5705.shops.screen.networking.NetworkHelper;
import net.spudacious5705.shops.screen.networking.ShopTabSyncPkt;
import net.spudacious5705.shops.screen.networking.ToggleSyncPkt;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static net.spudacious5705.shops.block.entity.AbstractShopEntity.player_ID_Records_Delegate.checkAction;
import static net.spudacious5705.shops.block.entity.ShopInventory.PAYMENT_SLOT;
import static net.spudacious5705.shops.block.entity.ShopInventory.VENDING_SLOT;

public class ShopScreenHandlerOwner extends AbstractContainerMenu {

    private final AbstractShopEntity.settings_Delegate SETTINGS_DELEGATE;



    void initiateWarn(WarningActivator function) {
        warningActivator = function;
    }


    public void selfDemotePlayer(Player player) {
        if(activeTab==WARNING_TAB){
            ID_RECORDS_DELEGATE.selfDemote(player);
        }
    }

    public int getActiveTab(){
        return activeTab;
    }

    public boolean isPlayerCreative() {
        return SETTINGS_DELEGATE.isPlayerCreative();
    }



    @Override
    public boolean stillValid(@NotNull Player player) {
        return this.shopInventory.stillValid(player);
    }

    public void close() {
        playerInventory.player.closeContainer();
    }

    interface WarningActivator{
        void openWarnScreen();
    }
    private SettingsUpdater SETTINGS_UPDATER = null;
    interface SettingsUpdater{
        void updateSettings(ToggleButtonID id, boolean state);
    }

    private WarningActivator warningActivator;

    final AbstractShopEntity.InventoryDelegate shopInventory;
    //private final PropertyDelegate propertyDelegate;

    private final AbstractShopEntity.player_ID_Records_Delegate ID_RECORDS_DELEGATE;

    final Inventory playerInventory;

    final PermissionLevel perms;

    final ScreenSettingsGroup SCREEN_SETTINGS;

    private final List<TogglableSlot> playerInvSlots = new ArrayList<>();
    private final List<TogglableSlot> tabSellerSlots = new ArrayList<>();
    final List<TogglableSlot> tabSettingsSlots = new ArrayList<>();
    private final List<TogglableSlot> tabCustomerSlots = new ArrayList<>();
    private widgetCollection widgets;

    interface widgetCollection{
        void setToVal(boolean value);
    }

    void setWidgetFunction(widgetCollection c){
        widgets = c;
    }

    private static final int profit_itemStacks_start = 54;


    private static final int EXPECTED_CONTAINER_SIZE = 78;

    public ShopScreenHandlerOwner(int syncId, Inventory playerInventory, FriendlyByteBuf buf) {//clientInit
        super(ModScreenHandlers.SHOP_SCREEN_HANDLER_OWNER.get(), syncId);
        BlockPos pos = buf.readBlockPos();
        boolean openTop = buf.readBoolean();
        Player player = playerInventory.player;

        this.playerInventory = playerInventory;

        if(player.level().getBlockEntity(pos) instanceof AbstractShopEntity shop) {

            AbstractShopEntity.InventoryDelegate inventoryDelegate = openTop ?
                    shop.getOtherInventoryDelegate(player)
                    :
                    shop.getInventoryDelegate(player);


            if (inventoryDelegate != null && inventoryDelegate.getContainerSize() != EXPECTED_CONTAINER_SIZE) {
                throw new IllegalArgumentException("Inventory size must be 78");
            }

            this.shopInventory = inventoryDelegate;
            this.perms = shopInventory.checkPermissions();

            this.SCREEN_SETTINGS = shop.getScreenSettings();

            ID_RECORDS_DELEGATE = shop.getRecordsDelegate(player);

            SETTINGS_DELEGATE = shop.getSettingsDelegate(player);

            finishSetup();
        } else {
            player.closeContainer();
            this.shopInventory = null;
            this.SCREEN_SETTINGS = null;
            this.perms = PermissionLevel.CUSTOMER;
            ID_RECORDS_DELEGATE = null;
            SETTINGS_DELEGATE = null;
        }
    }

    public ShopScreenHandlerOwner(
            int syncId,
            Inventory playerInventory,
            AbstractShopEntity shop,
            AbstractShopEntity.InventoryDelegate inventoryDelegate,
            AbstractShopEntity.player_ID_Records_Delegate recordsDelegate,
            AbstractShopEntity.settings_Delegate settingsDelegate
    ) {//serverInit
        super(ModScreenHandlers.SHOP_SCREEN_HANDLER_OWNER.get(), syncId);
        this.shopInventory = inventoryDelegate;
        checkContainerSize(shopInventory, EXPECTED_CONTAINER_SIZE );
        this.perms = shopInventory.checkPermissions();
        this.SCREEN_SETTINGS = null;
        this.playerInventory = playerInventory;
        this.ID_RECORDS_DELEGATE = recordsDelegate;
        this.SETTINGS_DELEGATE = settingsDelegate;

        finishSetup();
    }

    private void finishSetup() {


        addPlayerInventory(playerInventory);
        playerInventory.startOpen(playerInventory.player);
        addShopInventory();

        addShopTrades();

        addContractSlots();

        activeTab = SELLER_TAB;

        addSettingButtons();



    }

    private void addContractSlots() {
        int offsetx = 81;
        int offsety = 69;

        for(int y = 0; y<4; y++) {
            for (int i = 0; i<6; ++i){
                new contract_slot(ID_RECORDS_DELEGATE,y*6+i,offsetx+i*23,offsety+y*23);
            }
        }
    }

    private void addShopTrades(){
        int x = 25;
        int y = 31;

        new shop_trade_slot(shopInventory, PAYMENT_SLOT, x, y);
        new shop_trade_slot(shopInventory, VENDING_SLOT, x, y + 47);

        new shop_payment_slot(shopInventory, PAYMENT_SLOT, 71, 126);
        new shop_vendor_slot(shopInventory, VENDING_SLOT, 140, 126,this);
    }



    @MagicConstant
    static final int SELLER_TAB = 1;
    @MagicConstant
    static final int SETTINGS_TAB = 2;
    @MagicConstant
    static final int CUSTOMER_TAB = 3;
    @MagicConstant
    static final int WARNING_TAB = 4;

    private int activeTab = SELLER_TAB;

    @OnlyIn(Dist.CLIENT)
    public void updateTabSelectionClientside(int tab){
        activeTab = tab;
        NetworkHelper.CHANNEL.sendToServer(new ShopTabSyncPkt(activeTab));
        updateTabSelection();
    }

    public void updateTabSelectionServerside(int tab){
        activeTab = tab;
    }


    public boolean toggleButtonServersideUpdate(ToggleButtonID button, boolean state) {
        SpudaciousShops.LOGGER.debug("packet received: {} - {}", button.getSerialised(), state);
        SETTINGS_DELEGATE.attemptSetState(button,state);
        return SETTINGS_DELEGATE.getState(button);
    }


    @OnlyIn(Dist.CLIENT)
    public boolean updateTabSelectionResponse(int tab){
        if(activeTab != tab){
            activeTab = tab;
            updateTabSelection();
            return true;
        }
        return false;
    }

    public void updateTabSelection(){
        switch (activeTab) {
            case SETTINGS_TAB -> {
                tabSettingsSlots.forEach(TogglableSlot::enable);widgets.setToVal(true);
                playerInvSlots.forEach(TogglableSlot::enable);

                tabSellerSlots.forEach(TogglableSlot::disable);
                tabCustomerSlots.forEach(TogglableSlot::disable);
            }
            case CUSTOMER_TAB -> {
                playerInvSlots.forEach(TogglableSlot::enable);
                tabCustomerSlots.forEach(TogglableSlot::enable);

                tabSettingsSlots.forEach(TogglableSlot::disable);widgets.setToVal(false);
                tabSellerSlots.forEach(TogglableSlot::disable);
            }
            case WARNING_TAB -> {
                tabSettingsSlots.forEach(TogglableSlot::disable);widgets.setToVal(false);
                tabSellerSlots.forEach(TogglableSlot::disable);
                playerInvSlots.forEach(TogglableSlot::disable);
                tabCustomerSlots.forEach(TogglableSlot::disable);
            }
            default -> { //SELLER_TAB
                tabSellerSlots.forEach(TogglableSlot::enable);
                playerInvSlots.forEach(TogglableSlot::enable);

                tabSettingsSlots.forEach(TogglableSlot::disable);widgets.setToVal(false);
                tabCustomerSlots.forEach(TogglableSlot::disable);
            }
        }
    }

    void settingsUpdater(SettingsUpdater function) {
        SETTINGS_UPDATER = function;
    }

    @OnlyIn(Dist.CLIENT)
    public void updateToggleButtonFromPacket(ToggleButtonID button, boolean state) {
        SETTINGS_UPDATER.updateSettings(button,state);
    }

    @OnlyIn(Dist.CLIENT)
    public boolean handleToggleButtonInput(ToggleButtonID button, boolean state) {
        if(SETTINGS_DELEGATE.attemptSetState(button,state)){
            NetworkHelper.CHANNEL.sendToServer(new ToggleSyncPkt(button,state));
            return state;
        }
        return !state;//failure, return original state
    }

    @OnlyIn(Dist.CLIENT)
    public boolean getStateOfSetting(ToggleButtonID button) {
        return SETTINGS_DELEGATE.getState(button);
    }

    private void addSettingButtons() {

    }

    private void addShopInventory(){
        int offsetx = 59;
        int offsety = 15;

        for (int i = 0; i<6; ++i){
            for (int j = 0; j<9; ++j){
                createShopInvSlot(j+i*9,offsetx + j*18,offsety + i*18);
            }
        }
        offsetx -= 44;
        offsety += 110;

        for (int i = 0; i<11; ++i){
            createShopInvSlot(profit_itemStacks_start+i,offsetx+i*18,offsety);
        }
        offsety += 18;

        for (int i = 0; i<11; ++i){
            createShopInvSlot(profit_itemStacks_start+11+i,offsetx+i*18,offsety);
        }

    }

    private void createShopInvSlot(int slotIndex, int x, int y){
        TogglableSlot slot = new TogglableSlot(shopInventory, slotIndex, x, y);
        tabSellerSlots.add(slot);
        addSlot(slot);
    }


    private void addPlayerInventory(Inventory playerInventory) {

        int offsetx = 33;
        int offsety = 174;

        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                new player_slot(playerInventory, l + i * 9 + 9, offsetx + l * 18, offsety + i * 18);
            }
        }
        offsety += 58;
        for (int i = 0; i < 9; ++i) {
            new player_slot(playerInventory, i, offsetx + i * 18, offsety);
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int invSlot) {
        if(activeTab==CUSTOMER_TAB){
            int tradeCount = 0;
            while(tradeCount<64&shopInventory.canTrade(player)){
                shopInventory.trade(playerInventory);
                tradeCount++;
            }
            return ItemStack.EMPTY;
        }
        if(activeTab!=SELLER_TAB){return ItemStack.EMPTY;}

        if(this.slots.get(invSlot) instanceof shop_vendor_slot){return ItemStack.EMPTY;}
        if(this.slots.get(invSlot) instanceof shop_trade_slot){return ItemStack.EMPTY;}
        if(this.slots.get(invSlot) instanceof contract_slot){return ItemStack.EMPTY;}

        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (!slot.hasItem()) {return newStack;}
        ItemStack originalStack = slot.getItem();
        newStack = originalStack.copy();


        if(this.slots.get(invSlot) instanceof player_slot){
            if (!this.moveItemStackTo(originalStack, 36, 90, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (!this.moveItemStackTo(originalStack, 0, 35, false)) {
                return ItemStack.EMPTY;
            }
        }


        if (originalStack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        return newStack;
    }

    public ScreenSettingsGroup getSettings() {
        return this.SCREEN_SETTINGS;
    }

    static class TogglableSlot extends Slot {
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

    class player_slot extends TogglableSlot {
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


    class contract_slot extends TogglableSlot {

        private final AbstractShopEntity.player_ID_Records_Delegate contract_delegate;

        /**
         *
         * Automatically adds itself to the neccecary lists
         */
        public contract_slot(AbstractShopEntity.player_ID_Records_Delegate inventory, int slot, int x, int y) {
            super(inventory, slot, x, y);
            tabSettingsSlots.add(this);
            this.contract_delegate = inventory;
            addSlot(this);
            this.disable();
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack) {
            if(checkAction(stack, this.getSlotIndex())){
                return this.getItem().isEmpty();
            }
            return false;
        }

        @Override
        public @NotNull ItemStack safeInsert(@NotNull ItemStack stack) {
            return this.safeInsert(stack,0);
        }

        @Override
        public @NotNull ItemStack safeInsert(ItemStack stack, int count) {
            if (!stack.isEmpty()&&stack.getItem() == ModItems.CONTRACT_SCROLL.get()) {
                return ((AbstractShopEntity.player_ID_Records_Delegate) container).insertContract(stack, this.getSlotIndex());
            }
            return stack;
        }

        @Override
        public @NotNull ItemStack safeTake(int pCount, int pDecrement, @NotNull Player pPlayer) {
            if(contract_delegate.belongsToInteractor(this.getItem())){
                if(warningActivator!=null) {
                    warningActivator.openWarnScreen();
                } else {
                    throw new IllegalStateException("[Spud's Shops] Warning activator not initialised");
                }
                return ItemStack.EMPTY;
            }
            return container.removeItem(this.getSlotIndex(), 1);
        }

        @Override
        public @NotNull Optional<ItemStack> tryRemove(int pCount, int pDecrement, @NotNull Player pPlayer) {
            if(contract_delegate.belongsToInteractor(this.getItem())){
                if(warningActivator!=null) {
                    warningActivator.openWarnScreen();
                } else {
                    throw new IllegalStateException("[Spud's Shops] Warning activator not initialised");
                }
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
            if(contract_delegate.belongsToInteractor(this.getItem())){
                return true;
            }
            return contract_delegate.canEditThat(this.getSlotIndex());
        }
        //always stack size of 1 or 0
    }

    class shop_trade_slot extends TogglableSlot {

        public shop_trade_slot(AbstractShopEntity.InventoryDelegate inventory, int slot, int x, int y) {
            super(inventory, slot, x, y);
            tabSellerSlots.add(this);
            addSlot(this);
        }

        @NotNull
        @Override
        public ItemStack safeTake(int amount, int shouldDecrement, @NotNull Player pPlayer) {
            this.container.removeItem(this.getSlotIndex(), amount);
            return ItemStack.EMPTY;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            if(stack.getItem() != this.getItem().getItem()) {
                this.set(ItemStack.EMPTY);
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
            }
            return Optional.empty();
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
                if(count>64)count=64;
                this.container.setItem(this.getSlotIndex(),stack.copyWithCount(count));
            }else {
                this.container.setItem(this.getSlotIndex(), stack.copyWithCount(count));
            }
            return stack;
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

    class shop_payment_slot extends TogglableSlot {

        public shop_payment_slot(AbstractShopEntity.InventoryDelegate inventory, int slot, int x, int y) {
            super(inventory, slot, x, y);
            tabCustomerSlots.add(this);
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

    class shop_vendor_slot extends TogglableSlot {
        private final ShopScreenHandlerOwner handler;
        public shop_vendor_slot(AbstractShopEntity.InventoryDelegate inventory, int index, int x, int y, ShopScreenHandlerOwner handler) {
            super(inventory, index, x, y);
            this.handler = handler;
            tabCustomerSlots.add(this);
            addSlot(this);
            this.disable();
        }

        @Override
        public @NotNull Optional<ItemStack> tryRemove(int pCount, int pDecrement, @NotNull Player pPlayer) {
            if(shopInventory.canTrade(pPlayer))handler.trade();
            return Optional.empty();
        }

        @NotNull
        @Override
        public ItemStack safeTake(int pCount, int pDecrement, @NotNull Player pPlayer) {
            handler.trade();
            return ItemStack.EMPTY;
        }

        @Override
        public boolean mayPickup(@NotNull Player playerEntity) {
            return shopInventory.canTrade(playerEntity);
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

    private void trade() {
        this.shopInventory.trade(playerInventory);
    }

    public static boolean canUseInTrade(ItemStack stack, ItemStack otherStack) {
        return stack.is(otherStack.getItem())&&Objects.equals(stack.getTag(), otherStack.getTag());

    }

    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);
        playerInventory.stopOpen(player); // Notify close
    }

}