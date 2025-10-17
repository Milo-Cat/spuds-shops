package net.spudacious5705.shops.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static net.spudacious5705.shops.screen.ShopScreenHandlerOwner.canUseInTrade;

public class ShopInventory extends NonNullList<ItemStack> {

    protected static final int INV_SIZE = 78;
    public static final int PAYMENT_SLOT = 76;
    public static final int VENDING_SLOT = 77;
    protected static final int STOCK_END = 53;
    protected static final int PROFIT_END = 75;

    protected ShopInventory(List<ItemStack> delegate) {
        super(delegate, ItemStack.EMPTY);
    }

    public static ShopInventory create(){
        List<ItemStack> stacks = new ArrayList<>(Collections.nCopies(INV_SIZE,ItemStack.EMPTY));
        return new ShopInventory(stacks);
    }

    boolean outOfStock(){
        int stock = 0;
        ItemStack vend = get(VENDING_SLOT);
        ItemStack stockStack;
        for (int i = 0; i <= STOCK_END; i++) {
            stockStack = get(i);
            if(canUseInTrade(vend,stockStack)){
                stock += stockStack.getCount();
                if(stock >= vend.getCount()){return false;}
            }
        }
        return true;
    }

    boolean paymentRegisterFull(){
        int space = 0;
        ItemStack paymentSlot;
        ItemStack paymentType = get(PAYMENT_SLOT);
        int price = paymentType.getCount();
        for(int i = PROFIT_END; i > STOCK_END; i--) {
            paymentSlot = get(i);
            if(paymentSlot.isEmpty()){
                space += paymentType.getMaxStackSize();
            } else if(canUseInTrade(paymentSlot,paymentType)){
                space += paymentSlot.getMaxStackSize() - paymentSlot.getCount();
            }
            if(space >= price){return false;}
        }
        return true;
    }

    boolean isPlayerPoor(Player player) {
        Inventory inv = player.getInventory();
        ItemStack payment = getPaymentStack();
        int money = 0;
        for (int i = 0; i <= 36; i++) {
            if(canUseInTrade(inv.getItem(i),payment)){
                money += inv.getItem(i).getCount();
                if(money >= payment.getCount()){return false;}
            }
        }
        return true;
    }

    public ShopInventory prepForItemScatterer() {
        set(PAYMENT_SLOT, ItemStack.EMPTY);
        set(VENDING_SLOT, ItemStack.EMPTY);
        return this;
    }

    public ItemStack getVendingStack() {return get(VENDING_SLOT).copy();}

    public ItemStack getPaymentStack() {return get(PAYMENT_SLOT).copy();}

    @Override
    public int size() {
        return INV_SIZE;
    }

    public int getVendingQuantity() {
        return getVendingStack().getCount();
    }

    public int getPrice() {
        return get(PAYMENT_SLOT).getCount();
    }

    public ItemStack split(int slot, int amount) {
        return get(slot).split(amount);
    }

    public Item getDisplayItem() {
        return get(VENDING_SLOT).getItem();
    }

    public Item getPaymentType() {
        return get(PAYMENT_SLOT).getItem();
    }

    public boolean tradeFunctional() {
        return !(
                tradeNonFunctional()
        );
    }

    public boolean tradeNonFunctional() {
        boolean bl = get(PAYMENT_SLOT).isEmpty()  ||  get(VENDING_SLOT).isEmpty();
        return bl;
    }

    public static void ItemScatterer(Level world, BlockPos pos, ItemStack itemStack){
        Containers.dropItemStack(world,pos.getX(),pos.getY(),pos.getZ(),itemStack);
    }
    public static void ItemScatterer(Level world, BlockPos pos, NonNullList<ItemStack> inventory){
        Containers.dropContents(world,pos,inventory);
    }
}
