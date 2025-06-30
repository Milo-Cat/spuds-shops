package net.spudacious5705.shops.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.spudacious5705.shops.block.entity.AbstractShopEntity;
import net.spudacious5705.shops.screenNetworking.ShopScreenPayload;

import static net.minecraft.block.Block.dropStack;

public class ShopScreenHandlerCustomer extends ScreenHandler {
    private final AbstractShopEntity.InventoryDelegate shopInventory;
    final ScreenSettingsGroup.ScreenSettings SCREEN_TEXTURE_ID;
    private final PlayerInventory playerInventory;



    private  static final int PAYMENT_SLOT = 76;
    private  static final int VENDING_SLOT = 77;
    private static final int STOCK_END = 53;
    private static final int PROFIT_END = 75;



    public ShopScreenHandlerCustomer(int syncId, PlayerInventory playerInventory, ShopScreenPayload payload) {//clientInit
        super(ModScreenHandlers.SHOP_SCREEN_HANDLER_CUSTOMER, syncId);
        BlockPos pos = payload.pos();
        boolean openTop = payload.openTop();
        PlayerEntity player = playerInventory.player;

        this.playerInventory = playerInventory;

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

            this.SCREEN_TEXTURE_ID = shop.getScreenSettings().CUSTOMER();

            finishSetup();
        } else {
            MinecraftClient.getInstance().setScreen(null);
            this.shopInventory = null;
            this.SCREEN_TEXTURE_ID = null;
        }
    }

    public ShopScreenHandlerCustomer(int syncId, PlayerInventory playerInventory, AbstractShopEntity.InventoryDelegate inventory, ScreenSettingsGroup screen_settings) {//serverInit
        super(ModScreenHandlers.SHOP_SCREEN_HANDLER_CUSTOMER, syncId);
        this.shopInventory = inventory;
        this.SCREEN_TEXTURE_ID = screen_settings.CUSTOMER();
        this.playerInventory = playerInventory;
        finishSetup();
    }

    private void finishSetup(){

        playerInventory.onOpen(playerInventory.player);


        addPlayerInventory(playerInventory);
        addCustomerInventory();

    }

    public Identifier texture() {
        return this.SCREEN_TEXTURE_ID.textureID();
    }

    public void addCustomerInventory() {
        this.addSlot(new shop_payment_slot(shopInventory, PAYMENT_SLOT, 80-34, 11+25));
        this.addSlot(new shop_vendor_slot(shopInventory, VENDING_SLOT, 80+35, 59-23, this));
    }

    public void addPlayerInventory(PlayerInventory playerInv) {
        addPlayerInventory(playerInv,8,84);
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
        while(shopInventory.canTrade(player)){
            shopInventory.trade(playerInventory);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    static class shop_payment_slot extends Slot {

        public shop_payment_slot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
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
    }

    class shop_vendor_slot extends Slot {
        private final ShopScreenHandlerCustomer handler;
        public shop_vendor_slot(AbstractShopEntity.InventoryDelegate inventory, int index, int x, int y, ShopScreenHandlerCustomer handler) {
            super(inventory, index, x, y);
            this.handler = handler;
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

}