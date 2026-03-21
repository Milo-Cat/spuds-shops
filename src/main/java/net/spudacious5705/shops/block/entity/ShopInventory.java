package net.spudacious5705.shops.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.spudacious5705.shops.screen.ToggleButtonID;

import java.util.*;
import java.util.function.Supplier;

//todo BUGFIX:
//toggling NBT match disables warning icon
public class ShopInventory extends NonNullList<ItemStack> {

    protected static final int INV_SIZE = 78;
    public static final int PAYMENT_SLOT = 76;
    public static final int VENDING_SLOT = 77;
    protected static final int STOCK_END = 53;
    protected static final int PROFIT_END = 75;

    private final Supplier<Boolean> ignoreNBT;
    private final Supplier<Boolean> selectableTrade;

    protected ShopInventory(List<ItemStack> delegate, Supplier<Boolean> ignoreNBT, Supplier<Boolean> selectableTrade) {
        super(delegate, ItemStack.EMPTY);
        this.ignoreNBT = ignoreNBT;
        this.selectableTrade = selectableTrade;
    }

    public static ShopInventory create(EnumMap<ToggleButtonID, Boolean> toggleSettings){



        List<ItemStack> stacks = new ArrayList<>(Collections.nCopies(INV_SIZE,ItemStack.EMPTY));
        return new ShopInventory(stacks,
                () -> toggleSettings.getOrDefault(ToggleButtonID.IgnoreNBTToggle,true),
                () -> toggleSettings.getOrDefault(ToggleButtonID.SelectableTradeToggle,true)
                );
    }


    public boolean canUseAsPayment(ItemStack stack) {
        ItemStack payment = this.getPaymentStack();
        return payment.is(stack.getItem()) && Objects.equals(payment.getTag(), stack.getTag());
    }

    public boolean canUseAsProduct(ItemStack stack) {
        ItemStack product = this.getVendingStack();
        if (ignoreNBT.get()) {
            return product.is(stack.getItem());
        }
        return product.is(stack.getItem()) && Objects.equals(product.getTag(), stack.getTag());
    }

    boolean outOfStock(){
        if(selectableTrade.get()){//select trade
            for (int i = 0; i <= STOCK_END; i++) {
                if(!get(i).isEmpty())return false;
            }
            return true;
        }
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

    private int displayIndex = -1;
    public ItemStack getDisplayStack() {
        if(!selectableTrade.get()) return get(VENDING_SLOT).copy();
        displayIndex++;
        if(displayIndex>STOCK_END){
            displayIndex = -1;
            return get(VENDING_SLOT).copy();
        }

        ItemStack stack;
        for(int i = displayIndex; i <= STOCK_END; i++){
            stack = get(i);
            if(!stack.isEmpty()){
                displayIndex = i;
                return stack.copy();
            }
        }

        displayIndex = -1;
        return get(VENDING_SLOT).copy();
    }

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
        return get(PAYMENT_SLOT).isEmpty()  ||  get(VENDING_SLOT).isEmpty();
    }

    public static void ItemScatterer(Level world, BlockPos pos, ItemStack itemStack){
        Containers.dropItemStack(world,pos.getX(),pos.getY(),pos.getZ(),itemStack);
    }
    public static void ItemScatterer(Level world, BlockPos pos, NonNullList<ItemStack> inventory){
        Containers.dropContents(world,pos,inventory);
    }
}
