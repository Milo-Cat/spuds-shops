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
import java.util.Objects;
import java.util.function.Supplier;

public class ShopInventory extends NonNullList<ItemStack> {

    protected static final int INV_SIZE = 78;
    public static final int PAYMENT_SLOT = 76;
    public static final int VENDING_SLOT = 77;
    protected static final int STOCK_END = 53;
    protected static final int PROFIT_END = 75;

    private final Supplier<Boolean> tradeConfig;

    protected ShopInventory(List<ItemStack> delegate, Supplier<Boolean> tradeConfig) {
        super(delegate, ItemStack.EMPTY);
        this.tradeConfig = tradeConfig;
    }

    public static ShopInventory create(Supplier<Boolean> tradeConfig){
        List<ItemStack> stacks = new ArrayList<>(Collections.nCopies(INV_SIZE,ItemStack.EMPTY));
        return new ShopInventory(stacks, tradeConfig);
    }

    public boolean canUseAsPayment(ItemStack stack) {
        ItemStack payment = this.getPaymentStack();
        return payment.is(stack.getItem()) && Objects.equals(payment.getTag(), stack.getTag());
    }

    public boolean canUseAsProduct(ItemStack stack) {
        ItemStack product = this.getVendingStack();
        if (tradeConfig.get()) {
            return product.is(stack.getItem());
        }
        return product.is(stack.getItem()) && Objects.equals(product.getTag(), stack.getTag());
    }

    boolean outOfStock(){
        int stock = 0;
        ItemStack vend = getVendingStack();
        ItemStack stockStack;
        for (int i = 0; i <= STOCK_END; i++) {
            stockStack = get(i);
            if(canUseAsProduct(stockStack)){
                stock += stockStack.getCount();
                if(stock >= vend.getCount()){return false;}
            }
        }
        return true;
    }

    boolean paymentRegisterFull(){
        int space = 0;
        ItemStack paymentSlot;
        ItemStack paymentType = getPaymentStack();
        int price = paymentType.getCount();
        for(int i = PROFIT_END; i > STOCK_END; i--) {
            paymentSlot = get(i);
            if(paymentSlot.isEmpty()){
                space += paymentType.getMaxStackSize();
            } else if(canUseAsPayment(paymentSlot)){
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
            if(canUseAsPayment(inv.getItem(i))){
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
