package net.spudacious5705.shops.block.entity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.spudacious5705.shops.block.custom.ShopBlock;
import net.spudacious5705.shops.network.BlockPosPayload;
import net.spudacious5705.shops.screen.ShopScreenHandlerCustomer;
import net.spudacious5705.shops.screen.ShopScreenHandlerOwner;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ShopEntity extends BlockEntity implements ExtendedScreenHandlerFactory<BlockPosPayload>{
    private final int INV_SIZE = 78;
    private final ShopEntity self =  this;
    private  final  DefaultedList<ItemStack> itemStacks = DefaultedList.ofSize(INV_SIZE, ItemStack.EMPTY);

    private final Inventory inv = new Inventory() {

        @Override
        public int size() {
            return INV_SIZE;
        }

        @Override
        public boolean isEmpty() {
            return itemStacks.isEmpty();
        }

        @Override
        public ItemStack getStack(int slot) {
            return itemStacks.get(slot);
        }

        @Override
        public ItemStack removeStack(int slot, int amount) {
            return itemStacks.get(slot).split(amount);
        }

        @Override
        public ItemStack removeStack(int slot) {
            return itemStacks.remove(slot);
        }

        @Override
        public void setStack(int slot, ItemStack stack) {
            itemStacks.set(slot, stack);
        }

        @Override
        public void markDirty() {
            self.markDirty();
        }

        @Override
        public boolean canPlayerUse(PlayerEntity player) {
            return true;
        }

        @Override
        public void clear() {
            itemStacks.clear();
        }
    };

    public Inventory getInventory() {
        return inv;
    }

        /*
    @Override
    public int size() {
        return INV_SIZE;
    }

    @Override
    public boolean isEmpty() {
        return itemStacks.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return itemStacks.get(slot);
    }

    @Override
    public boolean canTransferTo(Inventory hopperInventory, int slot, ItemStack stack) {
        return false;
    }

    @Override
    public ItemStack removeStack(int slot) {
        return null;//itemStacks.remove(slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        //itemStacks.set(slot, stack);
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }*/


    private void update() {
        markDirty();
        if(world != null)
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
    }


    private  static final int PAYMENT_SLOT = 76;
    private  static final int VENDING_SLOT = 77;
    private static final int STOCK_END = 53;
    private static final int PROFIT_END = 75;

    private  final RendererData rendererData;

    private UUID ownerID;

    public ShopEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SHOP_ENTITY, pos, state);
        this.rendererData = new RendererData(this);
    }

    public void setOwnerID(UUID id) {
        if (this.ownerID == null) {
            this.ownerID = id;
        }
        //writeNbt(this.createNbt(RegistryWrapper.WrapperLookup));
        markDirty();
    }

    public boolean hasEnoughStock(){
        int stock = 0;
        Item displayItem = this.getDisplayItem();
        for (int i = 0; i <= STOCK_END; i++) {
            if(this.itemStacks.get(i).getItem() == displayItem){
                stock += this.itemStacks.get(i).getCount();
                if(stock >= this.itemStacks.get(VENDING_SLOT).getCount()){return true;};
            }
        }
        return false;
    }

    public boolean spaceForMoney(){
        int space = 0;
        ItemStack stack;
        for(int i = PROFIT_END; i > STOCK_END; i--) {
            stack = this.itemStacks.get(i);
            if(stack.isEmpty()){
                space += 64;
            } else if (stack.isOf(this.getPaymentType())) {
                space += 64 -stack.getCount();
            }
            if(space >= this.getPrice()){return true;}
        }
        return false;
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Shop");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {

        int result = player.getUuid().compareTo(ownerID);

        if(result==0) return new ShopScreenHandlerOwner(syncId, playerInventory, this, this.inv);

        if(!isShopFunctional()) {return null;}

        return new ShopScreenHandlerCustomer(syncId, playerInventory, this, this.inv);
    }

    @Override
    public BlockState getCachedState() {
        return super.getCachedState();
    }

    @Override
    public void markDirty() {
        world.updateListeners(pos,getCachedState(),getCachedState(),3);
        super.markDirty();
    }

    public Item getPaymentType() {
        return this.inv.getStack(PAYMENT_SLOT).getItem();
    }

    public boolean isShopFunctional(){
        if(null == ownerID){return false;}
        if(null == this.itemStacks){return false;}
        if(this.itemStacks.get(PAYMENT_SLOT).isEmpty()){return false;}
        if(this.itemStacks.get(VENDING_SLOT).isEmpty()){return false;}
        return this.getWorld() != null;
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup wrapper) {
        super.readNbt(nbt, wrapper);
        Inventories.readNbt(nbt, itemStacks, wrapper);
        if(nbt.containsUuid("owner_id")) {
            this.ownerID = nbt.getUuid("owner_id");
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup wrapper) {
        Inventories.writeNbt(nbt, itemStacks, wrapper);
        if(this.ownerID != null) {
            nbt.putUuid("owner_id", this.ownerID);
        }
        super.writeNbt(nbt, wrapper);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }


    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }

    public Direction getFacingDirection() {
        assert this.world != null;
        if(!this.world.isClient()) {return Direction.NORTH;}
        BlockState state = this.world.getBlockState(this.pos);
        if(!(state.getBlock() instanceof ShopBlock)) {return Direction.NORTH;}
        Direction dir = state.get(Properties.HORIZONTAL_FACING);
        if(dir == null){return Direction.NORTH;}
        return dir;
    }

    public Item getDisplayItem() {return this.inv.getStack(VENDING_SLOT).getItem();}

    public int getPrice() {
        if(this.itemStacks.get(PAYMENT_SLOT).isEmpty()){return 0;}
        return this.itemStacks.get(PAYMENT_SLOT).getCount();
    }

    public RendererData getRendererData() {
        return this.rendererData;
    }

    public void tick(World world1, BlockPos pos, BlockState state1) {
        if(world1.isClient()){
            this.rendererData.onTick();
        }
    }

    public boolean isOwner(UUID id) {
        if(ownerID == null){return true;}
        return id.compareTo(ownerID) == 0;
    }

    public boolean canBreak(PlayerEntity player) {
        if(player.isCreative()){return true;}
        return this.isOwner(player.getUuid());
    }

    @Override
    public BlockPosPayload getScreenOpeningData(ServerPlayerEntity player) {
        return new BlockPosPayload(this.pos);
    }

    public void itemScatter(World world, BlockPos pos) {
        itemStacks.set(PAYMENT_SLOT, ItemStack.EMPTY);
        itemStacks.set(VENDING_SLOT, ItemStack.EMPTY);
        ItemScatterer.spawn(world, pos, inv);
    }

    public final class RendererData{

        private final ShopEntity shop;
        public double lastRotation = 0;
        public double targetRotation = 0;
        public double frameRotation = 0;
        public final double doublePi = Math.PI*2;
        private World world;
        private Direction direction = Direction.NORTH;
        private int rotation;
        private float width;
        private int lightLevel;
        private ModelTransformationMode mode;
        private boolean shopFunctional = false;
        private ItemStack paymentType;
        private ItemStack displayItem;
        private String text;
        private float tickAccumulation = 0;
        private boolean displayType = false;
        private boolean tickPassed = true;
        public boolean stockWarning = false;
        public boolean paymentWarning = false;

        public RendererData(ShopEntity shop1){
            this.shop = shop1;
            this.world = shop1.getWorld();
        }

        public void tickAccumulator(float tickDelta){//makes retreiving data periodic instead of on frame
            if (this.tickAccumulation == 0.0f) {

                this.tickAccumulation += (float) Math.random()*40;

                tickPassed = true;

                this.shopFunctional = shop.isShopFunctional();


                if(this.shopFunctional) {
                    this.paymentType = new ItemStack(shop.getPaymentType());

                    this.paymentWarning = !this.shop.spaceForMoney();
                    this.stockWarning = !this.shop.hasEnoughStock();


                    this.displayItem = new ItemStack(shop.getDisplayItem());

                    this.lightLevel = getLightLevel(shop.getWorld(), shop.getPos());

                    this.text = Integer.toString(shop.getPrice());

                    this.direction = shop.getFacingDirection();

                    getRotation();

                    if(shop.getPrice()>=10) {
                        this.width = -7.0f;
                    } else {
                        this.width = -2.5f;
                    }


                    if (displayItem.getItem() instanceof BlockItem) {
                        displayType = true;
                    } else {
                        displayType = false;
                    }
                }
            }

            this.tickAccumulation += tickDelta;
            if (this.tickAccumulation >= 100.0f) {
                this.tickAccumulation= 0.0f;
            }

        }

        private int getLightLevel(World world, BlockPos pos) {
            int bLight = world.getLightLevel(LightType.BLOCK, pos);
            int sLight = world.getLightLevel(LightType.SKY, pos);
            return LightmapTextureManager.pack(bLight, sLight);
        }

        private void getRotation(){
            this.rotation = switch (direction) {
                default -> 180;
                case EAST -> 90;
                case SOUTH -> 0;
                case WEST -> 270;
            };
        }

        public boolean shopFunctional() {
            return this.shopFunctional;
        }

        public boolean displayType() {
            return this.displayType;
        }

        public ItemStack displayItem() {
            return this.displayItem;
        }

        public int lightLevel() {
            return this.lightLevel;
        }

        public World world() {
            return this.world;
        }

        public Direction direction() {
            return this.direction;
        }

        public String text() {

            return this.text;
        }

        public float width() {
            return this.width;
        }

        public ItemStack paymentType() {
            return this.paymentType;
        }

        public int rotation() {
            return this.rotation;
        }

        public boolean updateIconRotation() {
            if(tickPassed){tickPassed = false; return true;}
            return false;
        }

        public void onTick(){
            tickPassed = true;
        }


    }


}
