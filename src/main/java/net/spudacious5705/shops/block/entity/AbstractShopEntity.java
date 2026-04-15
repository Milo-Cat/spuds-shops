package net.spudacious5705.shops.block.entity;


import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.loading.FMLEnvironment;
import net.spudacious5705.shops.block.custom.AbstractShopBlock;
import net.spudacious5705.shops.item.ModItems;
import net.spudacious5705.shops.permission.IBlockPermissions;
import net.spudacious5705.shops.permission.PermissionLevel;
import net.spudacious5705.shops.permission.PermissionManager;
import net.spudacious5705.shops.screen.ScreenSettingsGroup;
import net.spudacious5705.shops.screen.ShopScreenHandlerCustomer;
import net.spudacious5705.shops.screen.ToggleButtonID;
import net.spudacious5705.shops.screen.owner_screen.ShopScreenHandlerOwner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.List;
import java.util.UUID;

import static net.spudacious5705.shops.block.custom.AbstractShopBlock.BREAKABLE;
import static net.spudacious5705.shops.block.entity.ShopInventory.*;
import static net.spudacious5705.shops.config.ConfigHandler.getDefaultToggleSetting;

public abstract class AbstractShopEntity extends BlockEntity implements IBlockPermissions<AbstractShopEntity> {

    protected static final int hourInTicks = 72000;
    protected final EnumMap<ToggleButtonID, Boolean> toggleSettings = new EnumMap<>(ToggleButtonID.class);

    //region INVENTORY
    protected final PermissionManager<AbstractShopEntity> permissionManager = new PermissionManager<>(
            this,
            () -> toggleSettings.getOrDefault(ToggleButtonID.CreativeToggle, false)
    );
    protected final ShopInventory shopInventory;
    final float particleOffset;
    private final boolean isClient;
    protected int decayTimer = -1;
    protected int checkIntervalTimer = 200; // Short initial interval used to verify shop state after server restart before switching to the normal 6000-tick check.
    protected int breakableTicks = -1;

    //endregion


    //region IDENTIFICATION
    @OnlyIn(Dist.CLIENT)
    protected RendererData rendererData;
    private boolean decayed = false;
    private boolean shouldRenderParticles = false;

    public <SHOP extends AbstractShopEntity> AbstractShopEntity(BlockEntityType<SHOP> type, BlockPos pos, BlockState state, float particleOffset) {
        super(type, pos, state);
        this.shopInventory = ShopInventory.create(toggleSettings);
        this.particleOffset = particleOffset;

        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            createRendererData();
            isClient = true;
        } else {
            isClient = false;
        }
    }

    @NotNull
    public InventoryDelegate getInventoryDelegate(Player player) {
        return new InventoryDelegate(player, this.shopInventory);
    }

    public void itemScatter(Level world, BlockPos pos) {
        ItemScatterer(world, pos, shopInventory.prepForItemScatterer());
        int contractsCount = permissionManager.contractCount() - 1;
        if (contractsCount > 0) {
            ItemScatterer(world, pos, new ItemStack(ModItems.CONTRACT_SCROLL.get(), contractsCount));
        }
    }

    public ScreenSettingsGroup getScreenSettings() {
        return ((AbstractShopBlock) this.getBlockState().getBlock()).getScreenSettings();
    }


    //endregion


    //region NBT

    // The dual-inventory shop types use a second inventory delegate for the alternate storage page.
    // This is currently handled with a shortcut instead of a dedicated ScreenHandler extension.
    @Nullable
    public final InventoryDelegate getOtherInventoryDelegate(Player player) {
        ShopInventory inv = otherInventory();

        if (inv != null) {
            return new InventoryDelegate(player, inv);
        }

        return null;
    }

    @Nullable
    protected ShopInventory otherInventory() {
        return null;
    }

    public settings_Delegate getSettingsDelegate(Player player) {
        return new settings_Delegate(
                permissionManager.quickUserSignIn(player),
                player
        );
    }

    public settings_Delegate getSettingsReader() {
        return new settings_Delegate();
    }

    public boolean isUnbreakable(Player player) {
        return !permissionManager.canBreakBlock(player, decayed);
    }

    public Component cantBreakMessage() {
        return permissionManager.cantBreakMessage();
    }

    //endregion

    public PermissionLevel userSignIn(Player player) {
        return permissionManager.userSignIn(player);
    }

    public PermissionManager<AbstractShopEntity>.player_ID_Records_Delegate getRecordsDelegate(Player player) {
        return permissionManager.getRecordsDelegate(player);
    }


    //region STATE LOGIC

    @Override
    public void setChanged() {
        if (isClient) {
            forceUpdateRenderData();
        }
        super.setChanged();

    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void forceUpdateClient() {
        if (level instanceof ServerLevel server) {
            BlockPos pos = this.getBlockPos();
            server.getChunkSource().blockChanged(pos);
            Packet<ClientGamePacketListener> packet = getUpdatePacket();
            if (packet != null) {
                List<ServerPlayer> watchers = server.getChunkSource().chunkMap.getPlayers(server.getChunk(pos).getPos(), false);
                for (ServerPlayer player : watchers) {
                    player.connection.send(packet);
                }
            }

        }
    }

    @Override
    protected void loadAdditional(@NotNull ValueInput input) {
        super.loadAdditional(input);

        ContainerHelper.loadAllItems(input, shopInventory);

        permissionManager.load(input);

        this.decayTimer = input.getIntOr("decay_timer", -1);


        for (ToggleButtonID id : ToggleButtonID.values()) {
            String nbtName = "toggle_" + id.getSerialised();
            toggleSettings.put(id, input.getBooleanOr(nbtName, getDefaultToggleSetting(id)));
        }

        checkShouldRenderParticles();
    }

    @Override
    protected void saveAdditional(@NotNull ValueOutput output) {
        ContainerHelper.saveAllItems(output, shopInventory);

        permissionManager.save(output);

        output.putInt("decay_timer", this.decayTimer);

        for (ToggleButtonID id : ToggleButtonID.values()) {
            Boolean v = toggleSettings.getOrDefault(id, getDefaultToggleSetting(id));
            output.putBoolean("toggle_" + id.getSerialised(), v);

        }

        checkShouldRenderParticles();
        super.saveAdditional(output);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @NotNull
    public MenuProvider createScreenHandlerFactory(boolean openTop) {

        return new MenuProvider() {

            @Override
            public @NotNull Component getDisplayName() {
                return Component.literal("");
            }

            @Override
            public @Nullable AbstractContainerMenu createMenu(int syncId, @NotNull Inventory playerInventory, @NotNull Player player) {
                PermissionLevel perms = permissionManager.userSignIn(player);


                InventoryDelegate inventoryDelegate = openTop ? getOtherInventoryDelegate(player) : getInventoryDelegate(player);
                settings_Delegate set_del = new settings_Delegate(perms, player);


                if (perms.canViewShopScreen()) {

                    var permissionsDelegate = permissionManager.createDelegate(perms, player.getUUID());

                    return new ShopScreenHandlerOwner(syncId, playerInventory, AbstractShopEntity.this, inventoryDelegate, permissionsDelegate, set_del);
                }

                return new ShopScreenHandlerCustomer(syncId, playerInventory, AbstractShopEntity.this, inventoryDelegate);
            }
        };
    }

    @OnlyIn(Dist.CLIENT)
    protected void createRendererData() {
        this.rendererData = new RendererData(shopInventory);
    }

    private void checkShouldRenderParticles() {
        shouldRenderParticles = toggleSettings.getOrDefault(ToggleButtonID.EffectsToggle, false)
                &&
                !toggleSettings.getOrDefault(ToggleButtonID.CreativeToggle, false);
    }

    protected void editBreakability(ServerLevel level, BlockPos pos, BlockState state, boolean breakable) {
        level.setBlock(pos, state.setValue(BREAKABLE, breakable), 3);
    }

    public void serverTick(ServerLevel world, BlockPos pos, BlockState shopState) {

        if (decayTimer > -1) {
            if (decayTimer > hourInTicks) {

                if (shouldRenderParticles && world.random.nextFloat() < 0.05f) {
                    for (int i = 0; i < 3; i++) {
                        world.sendParticles(ParticleTypes.ANGRY_VILLAGER, pos.getX() + .2 + world.random.nextFloat(), pos.getY() + world.random.nextFloat() + particleOffset, pos.getZ() + world.random.nextFloat(), 1, 0, 0, 0, 0);
                    }
                }

                boolean breakable = false;
                if (shopState.getBlock() instanceof AbstractShopBlock) {
                    breakable = shopState.getValue(BREAKABLE);
                }

                if (!breakable) {

                    decayed = true;

                    permissionManager.clearPermissions();
                    editBreakability(world, pos, shopState, true);
                    shopState.trySetValue(BREAKABLE, true);
                    breakableTicks = 140;

                }
            } else {
                decayTimer++;
            }
        }

        checkIntervalTimer--;
        if (checkIntervalTimer < 0) {
            checkIntervalTimer = 6000;
            // Periodic integrity check that recovers shop state after startup or when state updates are missed.
            checkShouldRenderParticles();
            if (isShopFunctional()) {
                if (decayTimer < 0) {
                    decayTimer = 0;
                    // Start the decay countdown once the shop becomes non-functional.
                }
            } else {
                decayTimer = -1;
                decayed = false;
            }
        }
        if (!shopState.getValue(BREAKABLE)) return;

        if (breakableTicks > 0) {
            if (!decayed) {
                breakableTicks--;
            }
            return;
        }

        if (breakableTicks < 0) {
            // Shop is now in the grace period before it becomes unbreakable again.
            // This protects against immediate flip-flopping of the BREAKABLE state.
            breakableTicks = 140;
        } else {
            editBreakability(world, pos, shopState, false);
            breakableTicks = -1; // Reset the countdown state after the breakability toggle completes.
        }
    }

    public boolean isShopFunctional() {
        if (managementFunctional() && hasTrade()) {
            decayTimer = -1;
            decayed = false;
            return true;
        }
        if (decayTimer < 0) {
            decayTimer = 0;//starts decay timer.
        }
        return false;
    }

    public boolean managementFunctional() {
        if (level != null) {
            return !permissionManager.containsNoPermissions();
        }
        return false;
    }

    protected boolean hasTrade() {
        return shopInventory.tradeFunctional();
    }

    //endregion

    //region RENDERING
    @OnlyIn(Dist.CLIENT)
    public void renderTick() {
        this.rendererData.onTick();
    }

    @OnlyIn(Dist.CLIENT)
    public RendererData rendererData() {
        return rendererData;
    }

    // Client-side only: update renderer state from the local shop inventory data.
    @OnlyIn(Dist.CLIENT)
    public void forceUpdateRenderData() {
        rendererData.update();
    }

    public Direction getCachedFacingDirection() {
        return this.getBlockState().getValue(AbstractShopBlock.FACING);
    }

    public final class InventoryDelegate implements Container {
        private final ShopInventory inventory;
        private final PermissionLevel permissions;
        private final UUID reciever_UUID;

        public InventoryDelegate(Player player, ShopInventory items) {
            this.permissions = permissionManager.userSignIn(player);
            this.reciever_UUID = player.getUUID();
            this.inventory = items;
        }

        private static void addToList(NonNullList<ItemStack> list, ItemStack stack) {
            if (list.isEmpty()) {
                list.add(stack.copyAndClear());
                return;
            }
            int end = list.size() - 1;
            ItemStack listEnd = list.get(end);
            int space = getAvailableSpace(listEnd);
            ItemStack split = stack.split(space);
            list.set(end,
                    split.copyWithCount(
                            listEnd.getCount() +
                                    split.getCount()
                    ));
            if (!split.isEmpty()) {
                list.add(stack.copyAndClear());
            }
        }

        private static int getAvailableSpace(ItemStack stack) {
            return Math.max(stack.getMaxStackSize() - stack.getCount(), 0);
        }

        public PermissionLevel checkPermissions() {
            return permissions;
        }

        @Override
        public int getContainerSize() {
            return inventory.size();
        }

        @Override
        public boolean isEmpty() {
            return inventory.isEmpty();
        }

        @Override
        public @NotNull ItemStack removeItemNoUpdate(int pSlot) {
            return ItemStack.EMPTY;
        }

        @Override
        public void setChanged() {
            assert level != null;
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            isShopFunctional();
            AbstractShopEntity.this.setChanged();
        }

        @Override
        public @NotNull ItemStack getItem(int slot) {
            if (slot > this.getContainerSize() || slot < 0) return ItemStack.EMPTY;

            /*if(slot>PROFIT_END) {
                return inventory.get(slot);
            }

            //if(permissions.canViewShopScreen())*/
            return inventory.get(slot);

            //return ItemStack.EMPTY;
        }

        public void trade(Inventory playerInv) {
            if (toggleSettings.getOrDefault(ToggleButtonID.SelectableTradeToggle, false))
                return; // Standard trade mode is disabled when selectable trade is active.

            NonNullList<ItemStack> vendList;
            boolean tradeCreative = toggleSettings.getOrDefault(ToggleButtonID.CreativeToggle, false);
            if (tradeCreative) {
                vendList = NonNullList.create();
                vendList.addFirst(inventory.getVendingStack().copy());
            } else {
                vendList = takeItems(inventory.getVendingStack().getCount(), inventory::canUseAsProduct,
                        inventory::get, 0, STOCK_END);
            }
            NonNullList<ItemStack> payList = takeItems(inventory.getPaymentStack().getCount(),
                    inventory::canUseAsPayment, playerInv::getItem, 0, 36);
            acceptPayment(payList);

            int ptr = vendList.size() - 1;
            boolean success = true;
            while (success && ptr >= 0) {
                success = playerInv.add(vendList.get(ptr));
                ptr--;
            }

            Player player = playerInv.player;
            ItemScatterer(player.level(), player.getOnPos(), vendList);

        }

        public void tradeWithSelection(Inventory playerInv, int index) {
            if (!toggleSettings.getOrDefault(ToggleButtonID.SelectableTradeToggle, false))
                return; // This method only applies when selectable trade is active.

            if (index > STOCK_END || index < 0) {
                return; // Invalid selection index outside the shop stock range.
            }
            ItemStack product = inventory.get(index);
            if (product.isEmpty()) {
                return; // No product exists at the selected stock index.
            }
            boolean tradeCreative = toggleSettings.getOrDefault(ToggleButtonID.CreativeToggle, false);

            if (tradeCreative) {
                product = product.copy();
            }

            NonNullList<ItemStack> payList = takeItems(inventory.getPaymentStack().getCount(),
                    inventory::canUseAsPayment, playerInv::getItem, 0, 36);
            acceptPayment(payList);

            playerInv.add(product.copyAndClear());
            if (!product.isEmpty()) {
                Player player = playerInv.player;
                ItemScatterer(player.level(), player.getOnPos(), product);
            }


        }

        public boolean canTrade(Player playerEntity) {
            if (!toggleSettings.getOrDefault(ToggleButtonID.CreativeToggle, false)) {
                if (inventory.outOfStock()) {
                    errorMessage("Shop is out of stock", playerEntity);
                    return false;
                }
                if (inventory.paymentRegisterFull()) {
                    errorMessage("Shop cannot store any more currency", playerEntity);
                    return false;
                }
            }
            if (inventory.isPlayerPoor(playerEntity)) {
                errorMessage("You do not have enough currency", playerEntity);
                return false;
            }
            return true;
        }

        private void acceptPayment(NonNullList<ItemStack> payList) {
            // Move collected payment items from the player into the shop's profit storage slots.
            ItemStack storageStack;
            int space;
            int ptr = 0;
            for (int i = STOCK_END + 1; i <= PROFIT_END; i++) {
                storageStack = inventory.get(i);
                if (inventory.canUseAsPayment(storageStack) || storageStack.isEmpty()) {
                    while (ptr < (payList.size()) && (storageStack.getCount() < storageStack.getMaxStackSize())) {
                        space = getAvailableSpace(storageStack);
                        if (storageStack.isEmpty()) {
                            storageStack = payList.get(ptr).copyAndClear();
                        } else {
                            storageStack.setCount(payList.get(ptr).split(space).getCount() + storageStack.getCount());
                        }
                        inventory.set(i, storageStack);
                        if (payList.get(ptr).isEmpty()) {
                            ptr++;
                        }
                    }
                }
            }

        }

        private void errorMessage(String message, Player player) {
            if (player.level().isClientSide()) {
                player.displayClientMessage(Component.literal(message), true);
            }
        }

        @Override
        public void clearContent() {

        }

        private NonNullList<ItemStack> takeItems(int quantityRequired, IStackQuery stackQueryType, miniDelegate inventory, int start, int end) {
            NonNullList<ItemStack> list = NonNullList.create();
            for (int i = start; i <= end; i++) {
                ItemStack stack = inventory.getStack(i);
                if (stackQueryType.checkCanUse_(stack)) {
                    if (stack.getCount() >= quantityRequired) {
                        addToList(list, stack.split(quantityRequired)); // Collect required quantity from this stack.
                        break;
                    }
                    quantityRequired -= stack.getCount();
                    addToList(list, stack);
                }
            }
            return list;
        }

        @Override
        public @NotNull ItemStack removeItem(int slot, int amount) {

            if (slot > this.getContainerSize() || slot < 0) return ItemStack.EMPTY;

            if (slot < PAYMENT_SLOT) {
                if (permissions.canTakeItems()) return inventory.split(slot, amount);
            } else if (permissions.canEditTrades()) {
                inventory.split(slot, amount);
            }

            return ItemStack.EMPTY;
        }

        @Override
        public void setItem(int slot, @NotNull ItemStack stack) {
            if (slot >= PAYMENT_SLOT) {
                if (this.permissions.canEditTrades()) {
                    inventory.set(slot, stack);
                }
            } else if (this.permissions.canImportStock()) {
                inventory.set(slot, stack);
            }
        }

        @Override
        public boolean stillValid(Player player) {
            return player.getUUID().compareTo(reciever_UUID) == 0;
        }

        public Item getPaymentType() {
            return inventory.getPaymentType();
        }

        public int getPrice() {
            return inventory.getPrice();
        }

        public Item getDisplayItem() {
            return inventory.getDisplayItem();
        }

        private interface miniDelegate {
            ItemStack getStack(int index);
        }

        private interface IStackQuery {
            boolean checkCanUse_(ItemStack stack);
        }
    }

    public final class settings_Delegate {
        private final boolean isCreative;
        private final boolean canEditSettings;

        private settings_Delegate(PermissionLevel perms, Player player) {
            isCreative = player.isCreative();
            canEditSettings = perms.canEditTrades();
        }

        private settings_Delegate() {
            isCreative = false;
            canEditSettings = false;
        }

        public boolean getState(@NotNull ToggleButtonID ID) {
            return toggleSettings.getOrDefault(ID, getDefaultToggleSetting(ID));
        }

        public boolean attemptSetState(@NotNull ToggleButtonID ID, @NotNull Boolean state) {
            if (
                    canEditSettings
                            &&
                            (
                                    ID != ToggleButtonID.CreativeToggle
                                            ||
                                            isCreative
                            )
            ) {
                toggleSettings.put(ID, state);
                setChanged();
                checkShouldRenderParticles();
                return true;
            }

            return false;
        }

        /** Apply a server-confirmed state on the client without a permission check. */
        public void forceSetState(@NotNull ToggleButtonID ID, @NotNull Boolean state) {
            toggleSettings.put(ID, state);
            checkShouldRenderParticles();
        }

        public boolean isPlayerCreative() {
            return isCreative;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public class RendererData {

        public final double doublePi = Math.PI * 2;
        protected final ShopInventory inventory;
        public double lastRotation = 0;
        public double targetRotation = 0;
        public double frameRotation = 0;
        public String stockQuantity;
        public float quantityTextWidth;
        public boolean stockWarning = false;
        public boolean paymentWarning = false;
        public Direction direction = Direction.NORTH;
        public int rotation;
        public boolean smallTextPrice;
        public boolean smallTextProduct;
        public boolean shopFunctional = false;
        public ItemStack paymentItem;
        public ItemStack displayItem;
        public String priceQuantity;
        public float priceTextWidth;
        public int frameAccumulation = 380;
        public boolean stockDisplayType = false;
        public boolean currencyDisplayType = true;
        public boolean shouldUpdate = true;

        public RendererData(@NotNull ShopInventory inv) {
            this.inventory = inv;
        }


        public void update() {

            this.shopFunctional = isShopFunctional() && inventory.tradeFunctional();

            if (this.shopFunctional) {
                this.paymentItem = inventory.getPaymentStack();

                boolean bl = stockWarning || paymentWarning;

                this.paymentWarning = inventory.paymentRegisterFull();
                this.stockWarning = inventory.outOfStock();

                if (!bl) {
                    if (stockWarning || paymentWarning) {
                        // Warning has just become active; reset the icon rotation baseline for consistent alert animation.
                        this.targetRotation = 0;//ShopRenderUtils.calcTargetRotation(this);
                        this.lastRotation = this.targetRotation;
                    }
                }


                this.displayItem = inventory.getDisplayStack();

                this.stockQuantity = Integer.toString(displayItem.getCount());

                //this.lightLevel = getLightLevel(shop.getWorld(), shop.getPos());

                this.priceQuantity = Integer.toString(paymentItem.getCount());

                this.direction = getCachedFacingDirection();

                getRotation();

                if (paymentItem.getCount() >= 100) {
                    this.priceTextWidth = -10.5f;
                    this.smallTextPrice = true;
                } else {
                    this.smallTextPrice = false;
                    if (paymentItem.getCount() >= 10) {
                        this.priceTextWidth = -7.0f;
                    } else {
                        this.priceTextWidth = -2.5f;
                    }
                }

                if (displayItem.getCount() >= 100) {
                    this.quantityTextWidth = -10.5f;
                    this.smallTextProduct = true;
                } else {
                    this.smallTextProduct = false;
                    if (displayItem.getCount() >= 10) {
                        this.quantityTextWidth = -7.0f;
                    } else {
                        this.quantityTextWidth = -2.5f;
                    }
                }

                Minecraft mc = Minecraft.getInstance();

                if (displayItem.getItem() instanceof BlockItem) {
                    //BakedModel model = mc.getItemRenderer().getModel(displayItem, null, null, 0);
                    //stockDisplayType = model.isGui3d();
                    stockDisplayType = true;
                } else {
                    stockDisplayType = false;
                }

                if (paymentItem.getItem() instanceof BlockItem) {
                    //BakedModel model = mc.getItemRenderer().getModel(paymentItem, null, null, 0);
                    //currencyDisplayType = model.isGui3d();
                    currencyDisplayType = true;
                } else {
                    currencyDisplayType = false;
                }


            } else {
                this.displayItem = ItemStack.EMPTY;
                this.paymentItem = ItemStack.EMPTY;
            }
        }

        public void frameAccumulator() {
            // Reduce renderer workload by updating shop display data only once every 400 ticks
            // rather than recalculating it on every render frame.
            if (this.frameAccumulation == 0) {

                this.frameAccumulation += (int) (Math.random() * 40);//adds some randomness so shops aren't all updating at the same time

                shouldUpdate = true;

                update();
            }

            this.frameAccumulation++;
            if (this.frameAccumulation >= 400) {
                this.frameAccumulation = 0;
            }

        }

        /**
         * MAY BE REQUIRED FOR HIGHER/LOWER VERSIONS
         * private int getLightLevel(World view, BlockPos pos) {
         * int bLight = view.getLightLevel(LightType.BLOCK, pos);
         * int sLight = view.getLightLevel(LightType.SKY, pos);
         * return LightmapTextureManager.pack(bLight, sLight);
         * }
         * *
         */


        private void getRotation() {
            this.rotation = switch (direction) {
                case EAST -> 90;
                case SOUTH -> 0;
                case WEST -> 270;
                default -> 180;
            };
        }

        public boolean shopFunctional() {
            return this.shopFunctional;
        }

        public boolean stockDisplayType() {
            return this.stockDisplayType;
        }

        public boolean currencyDisplayType() {
            return this.currencyDisplayType;
        }

        public ItemStack displayItem() {
            return this.displayItem;
        }

        public Level world() {
            return level;
        }

        public Direction direction() {
            return this.direction;
        }

        public String text() {

            return this.priceQuantity;
        }

        public float width() {
            return this.priceTextWidth;
        }

        public boolean useSmallTextPrice() {
            return this.smallTextPrice;
        }

        public boolean useSmallTextProduct() {
            return this.smallTextProduct;
        }

        public float qWidth() {
            return this.quantityTextWidth;
        }

        public ItemStack paymentItem() {
            return this.paymentItem;
        }

        public int rotation() {
            return this.rotation;
        }

        public boolean updateIconRotation() {
            if (shouldUpdate) {
                shouldUpdate = false;
                return true;
            }
            return false;
        }

        public void onTick() {
            shouldUpdate = true;
        }

        public int x() {
            return getBlockPos().getX();
        }

        public int y() {
            return getBlockPos().getY();
        }

        public int z() {
            return getBlockPos().getZ();
        }


        public boolean renderIcons() {
            return shouldRenderParticles;
        }
    }

    //endregion


}
