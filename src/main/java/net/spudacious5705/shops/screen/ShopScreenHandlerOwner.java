package net.spudacious5705.shops.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.spudacious5705.shops.block.entity.AbstractShopEntity;
import net.spudacious5705.shops.item.ModItems;
import net.spudacious5705.shops.properties.PermissionLevel;
import net.spudacious5705.shops.screenNetworking.ShopScreenPayload;
import net.spudacious5705.shops.screenNetworking.TabSyncPayload;
import net.spudacious5705.shops.screenNetworking.ToggleSyncPayload;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static net.spudacious5705.shops.block.entity.AbstractShopEntity.player_ID_Records_Delegate.checkAction;
import static net.spudacious5705.shops.block.entity.ShopInventory.PAYMENT_SLOT;
import static net.spudacious5705.shops.block.entity.ShopInventory.VENDING_SLOT;

public class ShopScreenHandlerOwner extends ScreenHandler {

    private final AbstractShopEntity.settings_Delegate SETTINGS_DELEGATE;

    void initiateWarn(WarningActivator function) {
        warningActivator = function;
    }


    public void selfDemotePlayer(ServerPlayerEntity player) {
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

    interface WarningActivator{
        void openWarnScreen();
    }
    private SettingsUpdater SETTINGS_UPDATER = null;
    interface SettingsUpdater{
        void updateSettings(ToggleSyncPayload.ToggleButtonID id, boolean state);
    }

    private WarningActivator warningActivator;

    final AbstractShopEntity.InventoryDelegate shopInventory;
    //private final PropertyDelegate propertyDelegate;

    private final AbstractShopEntity.player_ID_Records_Delegate ID_RECORDS_DELEGATE;

    final PlayerInventory playerInventory;

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

    public ShopScreenHandlerOwner(int syncId, PlayerInventory playerInventory, ShopScreenPayload payload) {//clientInit
        super(ModScreenHandlers.SHOP_SCREEN_HANDLER_OWNER, syncId);
        BlockPos pos = payload.pos();
        boolean openTop = payload.openTop();
        PlayerEntity player = playerInventory.player;

        this.playerInventory = playerInventory;
        playerInventory.onOpen(player);

        if(player.getWorld().getBlockEntity(pos) instanceof AbstractShopEntity shop) {

            AbstractShopEntity.InventoryDelegate inventoryDelegate = null;
            if (openTop) {
                inventoryDelegate = shop.getOtherInventoryDelegate(player);
            }
            if (inventoryDelegate == null) {
                inventoryDelegate = shop.getInventoryDelegate(player);
            }

            checkSize(inventoryDelegate, 78 );

            this.shopInventory = inventoryDelegate;
            this.perms = shopInventory.checkPermissions();

            this.SCREEN_SETTINGS = shop.getScreenSettings();

            ID_RECORDS_DELEGATE = shop.getRecordsDelegate(player);

            SETTINGS_DELEGATE = shop.getSettingsDelegate(player);

            finishSetup();
        } else {
            MinecraftClient.getInstance().setScreen(null);
            this.shopInventory = null;
            this.SCREEN_SETTINGS = null;
            this.perms = PermissionLevel.CUSTOMER;
            ID_RECORDS_DELEGATE = null;
            SETTINGS_DELEGATE = null;
        }
    }

    public ShopScreenHandlerOwner(int syncId, PlayerInventory playerInventory, AbstractShopEntity.InventoryDelegate inventory, @Nullable AbstractShopEntity.player_ID_Records_Delegate idRecordsDelegate, AbstractShopEntity.settings_Delegate settingsDelegate, ScreenSettingsGroup screen_settings) {//serverInit
        super(ModScreenHandlers.SHOP_SCREEN_HANDLER_OWNER, syncId);
        checkSize(inventory, 78 );
        this.shopInventory = inventory;
        this.perms = shopInventory.checkPermissions();
        this.SCREEN_SETTINGS = screen_settings;
        this.playerInventory = playerInventory;
        this.ID_RECORDS_DELEGATE = idRecordsDelegate;
        this.SETTINGS_DELEGATE = settingsDelegate;
        playerInventory.onOpen(playerInventory.player);
        finishSetup();
    }

    private void finishSetup() {


        addPlayerInventory(playerInventory);

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

    @Environment(EnvType.CLIENT)
    public void updateTabSelectionClientside(int tab){
        activeTab = tab;
        ClientPlayNetworking.send(new TabSyncPayload(activeTab));
        updateTabSelection();
    }

    public void updateTabSelectionServerside(int tab){
        activeTab = tab;
    }


    public boolean toggleButtonServersideUpdate(ToggleSyncPayload.ToggleButtonID button, boolean state) {
        SETTINGS_DELEGATE.attemptSetState(button,state);
        return SETTINGS_DELEGATE.getState(button);
    }

    @Environment(EnvType.CLIENT)
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

    @Environment(EnvType.CLIENT)
    public void updateToggleButtonFromPacket(ToggleSyncPayload.ToggleButtonID button, boolean state) {
        SETTINGS_UPDATER.updateSettings(button,state);
    }

    @Environment(EnvType.CLIENT)
    public boolean handleToggleButtonInput(ToggleSyncPayload.ToggleButtonID button, boolean state) {
        if(SETTINGS_DELEGATE.attemptSetState(button,state)){
            ClientPlayNetworking.send(new ToggleSyncPayload(button.getSerialised(), state));
            return state;
        }
        return !state;//failure, return original state
    }

    @Environment(EnvType.CLIENT)
    public boolean getStateOfSetting(ToggleSyncPayload.ToggleButtonID button) {
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
        offsetx += -44;
        offsety += 110;

        for (int i = 0; i<11; ++i){
            createShopInvSlot(profit_itemStacks_start+i,offsetx+i*18,offsety);
        }
        offsety += 18;

        for (int i = 0; i<11; ++i){
            createShopInvSlot(profit_itemStacks_start+11+i,offsetx+i*18,offsety);
        }

    }

    private void createShopInvSlot(int index, int x, int y){
        TogglableSlot slot = new TogglableSlot(shopInventory, index, x, y);
        tabSellerSlots.add(slot);
        addSlot(slot);
    }


    private void addPlayerInventory(PlayerInventory playerInventory) {

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
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        if(activeTab==CUSTOMER_TAB){
            while(shopInventory.canTrade(player)){
                shopInventory.trade(playerInventory);
            }
            return ItemStack.EMPTY;
        }
        if(activeTab!=SELLER_TAB){return ItemStack.EMPTY;}

        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (!slot.hasStack()) {return newStack;}
        ItemStack originalStack = slot.getStack();
        newStack = originalStack.copy();

        if(this.slots.get(invSlot) instanceof shop_trade_slot){return ItemStack.EMPTY;}
        if(this.slots.get(invSlot) instanceof contract_slot){return ItemStack.EMPTY;}

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

    public ScreenSettingsGroup getSettings() {
        return this.SCREEN_SETTINGS;
    }

    static class TogglableSlot extends Slot {
        private boolean toggled = true;
        public TogglableSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean isEnabled() {
            return toggled;
        }

        public void enable(){
            toggled=true;
        }

        public void disable(){
            toggled=false;
        }
    }

    ;

    class player_slot extends TogglableSlot {
        /**
         *
         * Automatically adds itself to the neccecary lists
         */
        public player_slot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
            playerInvSlots.add(this);
            addSlot(this);
        }

        public boolean isPlayerSlot() {return true;}
    }


    class contract_slot extends TogglableSlot {

        private final ContractVerifier contractVerifier;

        private interface ContractVerifier{
            boolean belongsToInteractor(ItemStack contract);
        }
        /**
         *
         * Automatically adds itself to the neccecary lists
         */
        public contract_slot(AbstractShopEntity.player_ID_Records_Delegate inventory, int index, int x, int y) {
            super(inventory, index, x, y);
            tabSettingsSlots.add(this);
            contractVerifier = inventory::checkContract;
            addSlot(this);
            this.disable();
        }

        @Override
        public int getMaxItemCount(){return 1;}

        @Override
        public boolean canInsert(ItemStack stack) {
            if(checkAction(stack, this.getIndex())){
                return this.getStack().isEmpty();
            }
            return false;
        }

        @Override
        public ItemStack insertStack(ItemStack stack) {
            return insertStack(stack,0);
        }

        @Override
        public ItemStack insertStack(ItemStack stack, int count) {
            if (!stack.isEmpty()&&stack.getItem() == ModItems.CONTRACT_SCROLL) {
                return ((AbstractShopEntity.player_ID_Records_Delegate) inventory).insertContract(stack, this.getIndex());
            }
            return stack;
        }

        @Override
        public ItemStack takeStack(int amount) {
            if(contractVerifier.belongsToInteractor(this.getStack())){
                if(warningActivator!=null) {
                    warningActivator.openWarnScreen();
                }
                return ItemStack.EMPTY;
            }
            return inventory.removeStack(this.getIndex());
        }

        @Override
        public boolean canTakeItems(PlayerEntity player) {
            if(contractVerifier.belongsToInteractor(this.getStack())){
                return true;
            }
            return inventory.canPlayerUse(player);
        }

        @Override
        public int getMaxItemCount(ItemStack stack) {
            return 1;
        }

        @Override
        public boolean canTakePartial(PlayerEntity player) {
            return false;
        }//always stack size of 1 or 0
    }

    class shop_trade_slot extends TogglableSlot {

        public shop_trade_slot(AbstractShopEntity.InventoryDelegate inventory, int index, int x, int y) {
            super(inventory, index, x, y);
            tabSellerSlots.add(this);
            addSlot(this);
        }

        @Override
        public ItemStack takeStack(int amount) {
            this.inventory.removeStack(this.getIndex(), amount);

            return ItemStack.EMPTY;
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            if(canMerge(stack, this.getStack())){
                return true;
            }
            this.setStack(ItemStack.EMPTY);
            return false;
        }

        @Override
        public ItemStack insertStack(ItemStack stack, int count) {
            ItemStack oldStack = this.getStack();

            if(stack.getItem() == oldStack.getItem()){
                count += oldStack.getCount();
                if(count>64)count=64;
                this.inventory.setStack(this.getIndex(),stack.copyWithCount(count));
            }else {
                this.inventory.setStack(this.getIndex(), stack.copyWithCount(count));
            }
            return stack;
        }

        @Override
        public boolean canTakePartial(PlayerEntity player) {
            return true;
        }

        @Override
        public ItemStack insertStack(ItemStack stack) {
            insertStack(stack,stack.getCount());
            return stack;
        }

    }

    class shop_payment_slot extends TogglableSlot {

        public shop_payment_slot(AbstractShopEntity.InventoryDelegate inventory, int index, int x, int y) {
            super(inventory, index, x, y);
            tabCustomerSlots.add(this);
            addSlot(this);
            this.disable();
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return false;
        }

        @Override
        public boolean canTakePartial(PlayerEntity player) {
            return false;
        }

        @Override
        public boolean canTakeItems(PlayerEntity playerEntity) {
            return false;
        }

        @Override
        public ItemStack insertStack(ItemStack stack, int count) {
            return stack;
        }

        @Override
        public ItemStack insertStack(ItemStack stack) {
            return stack;
        }

        @Override
        public void setStack(ItemStack stack) {
        }

        @Override
        public ItemStack getStack() {
            return super.getStack();
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
        public ItemStack takeStack(int amount) {
            handler.trade();
            return ItemStack.EMPTY;
        }

        @Override
        public boolean canTakeItems(PlayerEntity playerEntity) {
            return shopInventory.canTrade(playerEntity);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return false;
        }

        @Override
        public boolean canTakePartial(PlayerEntity player) {
            return false;
        }

        @Override
        public ItemStack insertStack(ItemStack stack, int amount) {
            return stack;
        }

        @Override
        public void setStack(ItemStack stack) {}
    }

    private void trade() {
        this.shopInventory.trade(playerInventory);
    }

    public static boolean canMerge(ItemStack stack, ItemStack otherStack) {
        return ItemStack.areItemsAndComponentsEqual(stack, otherStack);
    }

}