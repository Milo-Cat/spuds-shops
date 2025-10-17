package net.spudacious5705.shops.block.entity;


import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.spudacious5705.shops.block.custom.AbstractShopBlock;
import net.spudacious5705.shops.block.entity.renderer.ShopRenderUtils;
import net.spudacious5705.shops.config.ConfigHandler;
import net.spudacious5705.shops.item.ModItems;
import net.spudacious5705.shops.item.custom.ContractScroll;
import net.spudacious5705.shops.properties.PermissionLevel;
import net.spudacious5705.shops.screen.ScreenSettingsGroup;
import net.spudacious5705.shops.screen.ShopScreenHandlerCustomer;
import net.spudacious5705.shops.screen.ShopScreenHandlerOwner;
import net.spudacious5705.shops.screen.ToggleButtonID;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.UUID;

import static net.spudacious5705.shops.block.custom.AbstractShopBlock.BREAKABLE;
import static net.spudacious5705.shops.block.entity.ShopInventory.*;
import static net.spudacious5705.shops.item.custom.ContractScroll.isSigned;
import static net.spudacious5705.shops.screen.ShopScreenHandlerOwner.canUseInTrade;

public abstract class AbstractShopEntity extends BlockEntity {

    //region INVENTORY

    protected final ShopInventory shopInventory = ShopInventory.create();

    @NotNull
    public InventoryDelegate getInventoryDelegate(Player player) {
        return new InventoryDelegate(player, this.shopInventory);
    }

    public void itemScatter(Level world, BlockPos pos) {
        ItemScatterer(world,pos, shopInventory.prepForItemScatterer());
        int contractsCount = identificationRecords.size()-1;
        if(contractsCount>0){
            ItemScatterer(world,pos,new ItemStack(ModItems.CONTRACT_SCROLL.get(),contractsCount));
        }
    }

    public ScreenSettingsGroup getScreenSettings() {
        return ((AbstractShopBlock) this.getBlockState().getBlock()).getScreenSettings();
    }

    public final class InventoryDelegate implements Container {
        private final ShopInventory inventory;
        private final PermissionLevel permissions;
        private final UUID reciever_UUID;

        public InventoryDelegate(Player player, ShopInventory items) {
            this.permissions = userSignIn(player);
            this.reciever_UUID = player.getUUID();
            this.inventory = items;
        }

        public PermissionLevel checkPermissions(){
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
        public ItemStack removeItemNoUpdate(int pSlot) {
            return null;
        }

        @Override
        public void setChanged() {
            assert level != null;
            level.sendBlockUpdated(worldPosition,getBlockState(),getBlockState(),3);
            isShopFunctional();
            AbstractShopEntity.this.setChanged();
        }


        @Override
        public @NotNull ItemStack getItem(int slot) {
            if(slot>this.getContainerSize()||slot<0) return ItemStack.EMPTY;

            if(slot>PROFIT_END) {
                return inventory.get(slot);
            }

            if(permissions.canViewShopScreen()) return inventory.get(slot);

            return ItemStack.EMPTY;
        }


        public void trade(Inventory playerInv){
            NonNullList<ItemStack> vendList;
            boolean tradeCreative = toggleSettings.getOrDefault(ToggleButtonID.CreativeToggle,false);
            if(tradeCreative) {
                vendList = NonNullList.withSize(1, ItemStack.EMPTY);
                vendList.add(0, inventory.getVendingStack().copy());
            } else {
                vendList = takeItems(inventory.getVendingStack(), inventory::get, 0, STOCK_END);
            }
            NonNullList<ItemStack> payList = takeItems(inventory.getPaymentStack(), playerInv::getItem,0,36);

            if(!tradeCreative) {
                //place players payment into register
                ItemStack allowStack = inventory.getPaymentStack();
                ItemStack storageStack;
                int space;
                int ptr = 0;
                for (int i = STOCK_END + 1; i <= PROFIT_END; i++) {
                    storageStack = inventory.get(i);
                    if (canUseInTrade(storageStack, allowStack) || storageStack.isEmpty()) {
                        while (ptr < (payList.size()) && (storageStack.getCount() < storageStack.getMaxStackSize())) {
                            space = getAvalableSpace(storageStack);
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

            int ptr = 0;
            boolean success = true;
            while(success && ptr<(vendList.size())){
                success = playerInv.add(vendList.get(ptr));
            }

            Player player = playerInv.player;
            ItemScatterer(player.level(),player.getOnPos(),vendList);

        }

        public boolean canTrade(Player playerEntity) {
            if(!toggleSettings.getOrDefault(ToggleButtonID.CreativeToggle,false)) {
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


        private void errorMessage(String message, Player player){
            if(player.level().isClientSide()) {
                player.displayClientMessage(Component.literal(message), true);
            }
        }

        @Override
        public void clearContent() {

        }

        private interface miniDelegate{
            ItemStack getStack(int index);
        }

        private static NonNullList<ItemStack> takeItems(ItemStack retrieveStack, miniDelegate inventory, int start, int end){
            NonNullList<ItemStack> list = NonNullList.create();
            int moneyRequired = retrieveStack.getCount();
            for (int i = start; i <= end; i++) {
                ItemStack stack = inventory.getStack(i);
                if(canUseInTrade(stack,retrieveStack)){
                    if(stack.getCount()>=moneyRequired){
                        addToList(list,stack.split(moneyRequired));
                        break;
                    }
                    moneyRequired -= stack.getCount();
                    addToList(list,stack);
                }
            }
            return list;
        }

        private static void addToList(NonNullList<ItemStack> list, ItemStack stack){
            if(list.isEmpty()){
                list.add(stack.copyAndClear());
                return;
            }
            int end = list.size()-1;
            ItemStack listEnd = list.get(end);
            int space = getAvalableSpace(listEnd);
            ItemStack split = stack.split(space);
            list.set(end,
                    split.copyWithCount(
                            listEnd.getCount()+
                                    split.getCount()
                    ));
            if(!split.isEmpty()){
                list.add(stack.copyAndClear());
            }
        }

        private static int getAvalableSpace(ItemStack stack){
            return Math.max(stack.getMaxStackSize()-stack.getCount(), 0);
        }

        @Override
        public @NotNull ItemStack removeItem(int slot, int amount) {

            if(slot>this.getContainerSize()||slot<0) return ItemStack.EMPTY;

            if(slot<PAYMENT_SLOT){
                if(permissions.canTakeItems()) return inventory.split(slot, amount);
            } else if(permissions.canEditTrades()){
                inventory.split(slot, amount);
            }

            return ItemStack.EMPTY;
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
            if(slot>=PAYMENT_SLOT){
                if(this.permissions.canEditTrades()){
                    inventory.set(slot, stack);
                }
            } else if(this.permissions.canImportStock()){
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
    }

    //FIXME: these 2 methods are a bit of a hack. Need to create an extension of ScreenHandlers
    @Nullable
    public final InventoryDelegate getOtherInventoryDelegate(Player player){
        ShopInventory inv = otherInventory();

        if(inv != null){
            return new InventoryDelegate(player,inv);
        }

        return null;
    }
    @Nullable
    protected ShopInventory otherInventory(){
        return null;
    }

    //endregion


    //region IDENTIFICATION
    protected String ownerName = PlayerID.EMPTY.name;

    private final ArrayList<PlayerID> identificationRecords = new ArrayList<>(1);
    @Nullable
    public final player_ID_Records_Delegate getRecordsDelegate(Player player) {
        PermissionLevel perm = userSignIn(player);
        if(perm.canViewShopScreen()){
            return new player_ID_Records_Delegate(perm,player.getUUID());
        }
        return null;
    }

    private record PlayerID(UUID uuid, String name, PermissionLevel permissionLevel){

        public static final PlayerID EMPTY = new PlayerID(new UUID(0,0),"##OWNER NAME NULL##",PermissionLevel.CUSTOMER);

        public static PlayerID fromContract(ItemStack contract, PermissionLevel permissionLevel) {
            CompoundTag nbt = contract.getTag();
            if(nbt != null) {
                if (ContractScroll.isSigned(contract)) {//technically dont need this 2nd check
                    return new PlayerID(
                            nbt.getUUID(ContractScroll.NBTuuid),
                            nbt.getString(ContractScroll.NBTname),
                            permissionLevel
                    );
                }
            }
            return null;
        }

        public static void initialise(){}
    }

    public static void initialiseStaticMethods(){
        PlayerID.initialise();
    }

    @MagicConstant
    private static final int contractsInvSize = 24;
    private final NonNullList<ItemStack> contracts = NonNullList.withSize(contractsInvSize, ItemStack.EMPTY);/// assign to using nbt read write

    public final class player_ID_Records_Delegate implements Container {
        private final PermissionLevel perms;
        private final UUID userUUID;

        public player_ID_Records_Delegate(PermissionLevel perms, UUID userUUID) {
            this.perms = perms;
            this.userUUID = userUUID;
        }

        public ItemStack insertContract(ItemStack contract, int index){
            if(canEditThat(index)){
                if(contract != null) {
                    if (isSigned(contract)) {

                        PlayerID id = PlayerID.fromContract(contract, permFromIndex(index));

                        if(id != null) {

                            if(identificationRecords.stream().noneMatch(playerID -> playerID.uuid.compareTo(id.uuid)==0)) {

                                if(contracts.get(index) == ItemStack.EMPTY){
                                    identificationRecords.add(id);
                                    contracts.set(index, contract);
                                    this.setChanged();
                                    return ItemStack.EMPTY;
                                }
                            }
                        }
                    }
                }
            }
            return contract;
        }

        private static PermissionLevel permFromIndex(int index) {
            return PermissionLevel.fromInt(4-(index/6));
        }

        public static boolean checkAction(ItemStack contract, int index){
            if(checkIndex(index)){
                if(contract.getItem()==ModItems.CONTRACT_SCROLL.get()){
                    return ContractScroll.isSigned(contract);
                }
            }
            return false;
        }

        private static boolean checkIndex(int index){
            return index<contractsInvSize&&index>=0;
        }

        public boolean canEditThat(int index) {
            if(checkIndex(index)) {
                if (perms.canEditPermissions()) {
                    PermissionLevel perm = permFromIndex(index);
                    if (perm.asInt() < PermissionLevel.MANAGER.asInt()) {
                        return true;
                    } else if(perms == PermissionLevel.OWNER){
                        return true;
                    }
                    return belongsToInteractor(fetchContract(index,false));
                }
            }
            return false;
        }

        @Override
        public int getContainerSize() {
            return contractsInvSize;
        }

        @Override
        public boolean isEmpty() {
            return contracts.isEmpty();
        }

        private ItemStack fetchContract(int index, boolean remove){
            if(checkIndex(index)) {
                //return contracts.get(index);
                PermissionLevel perm = permFromIndex(index);

                PlayerID[] array = identificationRecords.stream()
                        .filter(PlayerID -> PlayerID.permissionLevel == perm)
                        .toArray(PlayerID[]::new);

                int i = index%6;

                if(array.length>i) {

                    PlayerID id = array[i];
                    ItemStack record = RecordToContract(id);

                    if(remove){

                        identificationRecords.removeIf(playerID -> playerID.uuid.compareTo(id.uuid)==0);

                        if(level instanceof ServerLevel server){
                            server.players().stream().filter(
                                    player -> player.getUUID().compareTo(id.uuid) == 0
                            ).findFirst().ifPresent(Player::closeContainer);
                        }

                        contracts.set(index, ItemStack.EMPTY);
                        this.setChanged();
                    }


                    return record;
                }
            }
            return ItemStack.EMPTY;
        }

        @Override
        public @NotNull ItemStack getItem(int index) {
            return fetchContract(index,false);
        }

        @Override
        public @NotNull ItemStack removeItem(int index, int amount) {//stack is always size of 1
            return removeItem(index);
        }

        @Override
        public ItemStack removeItemNoUpdate(int pSlot) {
            return null;
        }

        public ItemStack removeItem(int index) {
            if(canEditThat(index)) {
                ItemStack contract = fetchContract(index, true);
                this.setChanged();
                return contract;
            }
            return ItemStack.EMPTY;
        }

        @Override
        public void setItem(int index, @NotNull ItemStack contract) {
        }

        @Override
        public void setChanged() {
            if(level != null) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
            copyRecordsToContracts();
            AbstractShopEntity.this.setChanged();
        }


        @Override
        public boolean stillValid(@NotNull Player player) {
            return perms.canEditPermissions();
        }

        /**
         * does nothing
         */

        public boolean belongsToInteractor(@Nullable ItemStack stack) {
            if(stack != null) {
                UUID uuid = ContractScroll.getUUID(stack);
                if (uuid != null) {
                    return 0 == uuid.compareTo(userUUID);
                }
            }
            return false;
        }

        public void selfDemote(Player player) {
            if(player.getUUID().compareTo(userUUID)==0){
                identificationRecords.removeIf(playerID -> playerID.uuid.compareTo(userUUID)==0);
                this.setChanged();
            }
        }

        @Override
        public void clearContent() {

        }
    }

    private static ItemStack RecordToContract(PlayerID id){

        ItemStack stack = new ItemStack(ModItems.CONTRACT_SCROLL.get());

        CompoundTag nbt = new CompoundTag();

        nbt.putString("player_name", id.name);
        nbt.putUUID("player_uuid", id.uuid);

        stack.setTag(nbt);

        return stack.setHoverName(Component.literal("Contract - "+id.name));
    }

    public PermissionLevel quickUserSignIn(@NotNull Player player){

        UUID signIn = player.getUUID();

        PlayerID id = identificationRecords.stream().filter(record -> record.uuid.compareTo(signIn)==0).findFirst().orElse(null);

        if(id != null){
            return id.permissionLevel;
        }

        if(player.isCreative()) return PermissionLevel.SERVER_ADMIN;
        return PermissionLevel.CUSTOMER;
    }

    public PermissionLevel userSignIn(Player player) {

        if(toggleSettings.getOrDefault(ToggleButtonID.CreativeToggle,false)){
            if(!player.isCreative()){
                return PermissionLevel.CUSTOMER;
            }
        }

        if(identificationRecords.isEmpty()) {
            identificationRecords.add(new PlayerID(player.getUUID(), player.getName().getString(), PermissionLevel.OWNER));
            this.setChanged();
            return PermissionLevel.OWNER;
        }

        if(identificationRecords.stream().noneMatch(playerID -> playerID.permissionLevel==PermissionLevel.OWNER)){
            //if no owner is found, upgrade all next highest rank

            int maxValue = identificationRecords.stream()
                    .mapToInt(record -> record.permissionLevel.asInt())
                    .max()
                    .orElse(-1);


            identificationRecords.replaceAll(
                    record ->
                            record.permissionLevel.asInt() == maxValue ?
                                    new PlayerID(record.uuid,record.name,PermissionLevel.OWNER) :
                                    record
            );
            this.setChanged();
        }

        return quickUserSignIn(player);
    }


    public final class settings_Delegate {
        private final boolean isCreative;
        private final boolean canEditSettings;

        private settings_Delegate(PermissionLevel perms, Player player){
            isCreative = player.isCreative();
            canEditSettings = perms.canEditTrades();
        }

        public boolean getState(@NotNull ToggleButtonID ID){
            return toggleSettings.getOrDefault(ID, ConfigHandler.getDefaultToggleSetting(ID));
        }

        public boolean attemptSetState(@NotNull ToggleButtonID ID, @NotNull Boolean state){
            if(
                    canEditSettings
                            &&
                            (
                                    ID != ToggleButtonID.CreativeToggle
                                            ||
                                            isCreative
                            )
            ){
                toggleSettings.put(ID,state);
                setChanged();
                checkShouldRenderParticles();
                return true;
            }

            return false;
        }


        public boolean isPlayerCreative() {
            return isCreative;
        }
    }

    public settings_Delegate getSettingsDelegate(Player player) {
        return new settings_Delegate(
                quickUserSignIn(player),
                player
        );
    }


    //endregion


    //region NBT

    @Override
    public void setChanged() {
        if(isClient) {
            forceUpdateRenderData();
        }
        super.setChanged();

    }

    @MagicConstant
    private static final PermissionLevel[] CONTRACT_PERMS = {
            PermissionLevel.OWNER,
            PermissionLevel.MANAGER,
            PermissionLevel.SUPERVISOR,
            PermissionLevel.CLERK
    };
    private void copyRecordsToContracts(){
        ownerName = identificationRecords.stream().filter(playerID -> playerID.permissionLevel==PermissionLevel.OWNER).findFirst().orElse(PlayerID.EMPTY).name;
        contracts.clear();
        int indexModifier = 0;
        for(PermissionLevel lvl : CONTRACT_PERMS){
            PlayerID[] filtered = identificationRecords.stream().filter(record -> record.permissionLevel==lvl).toArray(PlayerID[]::new);
            int itterations = filtered.length;
            if(itterations>6) itterations = 6;
            for(int i = 0; i < itterations; i++){
                contracts.set(i+indexModifier, RecordToContract(filtered[i]));
            }
            indexModifier+=6;
        }
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void forceUpdateClient() {
        if(level instanceof ServerLevel server) {
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
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        ContainerHelper.loadAllItems(tag, shopInventory);
        identificationRecords.clear();

        if (tag.contains(CONTRACTS, Tag.TAG_LIST)) {
            ListTag contractList = tag.getList(CONTRACTS, Tag.TAG_COMPOUND);

            for (int index = 0; index < contractList.size(); index++) {
                CompoundTag contract = contractList.getCompound(index);

                String name = contract.getString(CONTRACT_NAME);
                UUID uuid = contract.getUUID(CONTRACT_UUID);
                PermissionLevel perms = PermissionLevel.fromInt(contract.getInt(CONTRACT_LEVEL));

                if (perms.asInt() > 0) {
                    identificationRecords.add(new PlayerID(uuid, name, perms));
                }
            }
        }

        copyRecordsToContracts();

        if (tag.hasUUID("owner_id")) {
            UUID ownerID = tag.getUUID("owner_id");
            String name = tag.contains("owner_name") ? tag.getString("owner_name") : ownerID.toString();
            identificationRecords.add(new PlayerID(ownerID, name, PermissionLevel.OWNER));
        }

        if (tag.contains("decay_timer")) {
            this.decayTimer = tag.getInt("decay_timer");
        }

        for (ToggleButtonID id : ToggleButtonID.values()) {
            String nbtName = "toggle_" + id.getSerialised();
            if (tag.contains(nbtName)) {
                toggleSettings.put(id, tag.getBoolean(nbtName));
            } else {
                toggleSettings.put(id, ConfigHandler.getDefaultToggleSetting(id));
            }
        }

        checkShouldRenderParticles();
    }

    @MagicConstant
    private static final String CONTRACT_NAME = "contract_name";
    @MagicConstant
    private static final String CONTRACT_UUID = "contract_uuid";
    @MagicConstant
    private static final String CONTRACT_LEVEL = "contract_lvl";
    @MagicConstant
    private static final String CONTRACTS = "contracts";

    @Override
    protected void saveAdditional(CompoundTag tag) {
        ContainerHelper.saveAllItems(tag, shopInventory);

        ListTag contractList = new ListTag();

        for (PlayerID id : identificationRecords) {
            CompoundTag contractNBT = new CompoundTag();
            contractNBT.putString(CONTRACT_NAME, id.name);
            contractNBT.putUUID(CONTRACT_UUID, id.uuid);
            contractNBT.putInt(CONTRACT_LEVEL, id.permissionLevel.asInt());
            contractList.add(contractNBT);
        }

        if (!contractList.isEmpty()) {
            tag.put(CONTRACTS, contractList);
        }

        tag.putInt("decay_timer", this.decayTimer);

        for (ToggleButtonID id : ToggleButtonID.values()) {
            Boolean v = toggleSettings.get(id);
            if (v != null) {
                tag.putBoolean("toggle_" + id.getSerialised(), v);
            }
        }

        checkShouldRenderParticles();
    }



    @Override
    public @NotNull CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    //endregion


    public <SHOP extends AbstractShopEntity>AbstractShopEntity(BlockEntityType<SHOP> type, BlockPos pos, BlockState state, float particleOffset) {
        super(type, pos, state);
        this.particleOffset = particleOffset;

        if (FMLEnvironment.dist == Dist.CLIENT) {
            createRendererData();
            isClient = true;
        } else {
            isClient = false;
        }
    }

    private final boolean isClient;

    @OnlyIn(Dist.CLIENT)
    protected void createRendererData(){
        this.rendererData = new RendererData(shopInventory);
    }

    private boolean decayed = false;
    private boolean shouldRenderParticles = false;
    private void checkShouldRenderParticles(){
        shouldRenderParticles = toggleSettings.getOrDefault(ToggleButtonID.EffectsToggle, false)
                &&
                !toggleSettings.getOrDefault(ToggleButtonID.CreativeToggle,false);
    }

    protected void editBreakability(ServerLevel level, BlockPos pos, BlockState state, boolean breakable) {
        level.setBlock(pos, state.setValue(BREAKABLE, breakable), 3);
    }

    public void serverTick(ServerLevel world, BlockPos pos, BlockState shopState) {

        if(decayTimer > -1) {
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

                    identificationRecords.clear();
                    editBreakability(world, pos,shopState, true);
                    shopState.trySetValue(BREAKABLE, true);
                    breakableTicks = 140;

                }
            } else {
                decayTimer++;
            }
        }

        checkIntervalTimer--;
        if(checkIntervalTimer<0){
            checkIntervalTimer=6000;
            //intervaled functionality check in case of bug and for startup.
            checkShouldRenderParticles();
            if (isShopFunctional()) {
                if(decayTimer<0){
                    decayTimer=0;
                    //start decay timer if not already started
                }
            } else {
                decayTimer = -1;
                decayed = false;
            }
        }
        if(!shopState.getValue(BREAKABLE))return;

        if (breakableTicks > 0) {
            if(!decayed) {
                breakableTicks--;
            }
            return;
        }

        if (breakableTicks < 0) {
            // Shop has become breakable; start the countdown (140 ticks)
            breakableTicks = 140;
        } else {
            editBreakability(world, pos,shopState, false);
            breakableTicks = -1; // Reset
        }
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
                PermissionLevel perms = userSignIn(player);


                InventoryDelegate inventoryDelegate = openTop?getOtherInventoryDelegate(player):getInventoryDelegate(player);
                player_ID_Records_Delegate recordsDelegate = new player_ID_Records_Delegate(perms, player.getUUID());
                settings_Delegate set_del = new settings_Delegate(perms, player);


                if (perms.canViewShopScreen()) {
                    return new ShopScreenHandlerOwner(syncId, playerInventory, AbstractShopEntity.this, inventoryDelegate, recordsDelegate, set_del);
                }

                if (!isShopFunctional()) {
                    return null;
                }

                return new ShopScreenHandlerCustomer(syncId, playerInventory, AbstractShopEntity.this, inventoryDelegate);
            }
        };
    }



    public abstract int getTextureId();

    //endregion
    

    //region STATE LOGIC
    

    public boolean canBreak(Player player) {
        if(player.isCreative()||decayed)return true;
        if(identificationRecords.isEmpty()){
            return userSignIn(player).canBreakBlock();
        }
        return quickUserSignIn(player).canBreakBlock();
    }


    public Component cantBreakMessage() {
        return Component.literal("Cannot break - Owned by " + ownerName);
    }

    protected int decayTimer = -1;

    protected static final int hourInTicks = 72000;

    public boolean isShopFunctional(){
        if(managementFunctional()&&hasTrade()){
            decayTimer = -1;
            decayed = false;
            return true;
        }
        if(decayTimer < 0){
            decayTimer = 0;//starts decay timer.
        }
        return false;
    }

    public boolean managementFunctional(){
        if(level != null) {
            return !identificationRecords.isEmpty();
        }
        return false;
    }

    protected boolean hasTrade(){
        return shopInventory.tradeFunctional();
    }

    protected int checkIntervalTimer = 200;//short initial check interval for server restarts

    protected int breakableTicks = -1;

    protected final EnumMap<ToggleButtonID, Boolean> toggleSettings = new EnumMap<>(ToggleButtonID.class);

    //endregion


    //region RENDERING
    @OnlyIn(Dist.CLIENT)
    public void renderTick() {
        this.rendererData.onTick();
    }

    @OnlyIn(Dist.CLIENT)
    protected RendererData rendererData;

    @OnlyIn(Dist.CLIENT)
    public RendererData rendererData(){return  rendererData;}
    //Only call from the CLIENT
    @OnlyIn(Dist.CLIENT)
    public void forceUpdateRenderData() {
        rendererData.update();
    }

    final float particleOffset;

    public Direction getCachedFacingDirection(){
        return this.getBlockState().getValue(AbstractShopBlock.FACING);
    }

    @OnlyIn(Dist.CLIENT)
    public class RendererData{

        protected final ShopInventory inventory;
        public double lastRotation = 0;
        public double targetRotation = 0;
        public double frameRotation = 0;
        public final double doublePi = Math.PI*2;
        public String stockQuantity;
        protected Direction direction = Direction.NORTH;
        protected int rotation;
        protected float width;
        protected boolean shopFunctional = false;
        protected ItemStack paymentItem;
        protected ItemStack displayItem;
        protected String text;
        protected int frameAccumulation = 380;
        protected boolean stockDisplayType = false;
        protected boolean currencyDisplayType = true;
        protected boolean shouldUpdate = true;
        public boolean stockWarning = false;
        public boolean paymentWarning = false;
        protected float qWidth;

        public RendererData(@NotNull ShopInventory inv){
            this.inventory = inv;
        }


        public void update(){

            this.shopFunctional = isShopFunctional() && inventory.tradeFunctional();

            if(this.shopFunctional) {
                this.paymentItem = inventory.getPaymentStack();

                this.stockQuantity = Integer.toString(inventory.getVendingStack().getCount());

                boolean bl = stockWarning || paymentWarning;

                this.paymentWarning = inventory.paymentRegisterFull();
                this.stockWarning = inventory.outOfStock();

                if(!bl){
                    if(stockWarning || paymentWarning){
                        //warnings have just been activated
                        this.targetRotation = ShopRenderUtils.calcTargetRotation(this);
                        this.lastRotation = this.targetRotation;
                    }
                }


                this.displayItem = inventory.getVendingStack();

                //this.lightLevel = getLightLevel(shop.getWorld(), shop.getPos());

                this.text = Integer.toString(inventory.getPrice());

                this.direction = getCachedFacingDirection();

                getRotation();

                if(inventory.getPrice()>=10) {
                    this.width = -7.0f;
                } else {
                    this.width = -2.5f;
                }

                if(inventory.getVendingQuantity()>=10) {
                    this.qWidth = -7.0f;
                } else {
                    this.qWidth = -2.5f;
                }

                Minecraft mc = Minecraft.getInstance();

                if(displayItem.getItem() instanceof BlockItem){
                    BakedModel model = mc.getItemRenderer().getModel(displayItem, null, null, 0);
                    stockDisplayType = model.isGui3d();
                } else {
                    stockDisplayType = false;
                }

                if(paymentItem.getItem() instanceof BlockItem){
                    BakedModel model = mc.getItemRenderer().getModel(paymentItem, null, null, 0);
                    currencyDisplayType = model.isGui3d();
                } else {
                    currencyDisplayType = false;
                }


            } else {
                this.displayItem = ItemStack.EMPTY;
                this.paymentItem = ItemStack.EMPTY;
            }
        }

        public void frameAccumulator(){//makes retrieving data periodic instead of on frame
            if (this.frameAccumulation == 0) {

                this.frameAccumulation += (int)(Math.random()*40);//adds some randomness so shops aren't all updating at the same time

                shouldUpdate = true;

                update();
            }

            this.frameAccumulation++;
            if (this.frameAccumulation >= 400) {
                this.frameAccumulation = 0;
            }

        }

        /** MAY BE REQUIRED FOR HIGHER/LOWER VERSIONS
         * private int getLightLevel(World view, BlockPos pos) {
         *    int bLight = view.getLightLevel(LightType.BLOCK, pos);
         *    int sLight = view.getLightLevel(LightType.SKY, pos);
         * return LightmapTextureManager.pack(bLight, sLight);
         * }
         * * */


        private void getRotation(){
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

            return this.text;
        }

        public float width() {
            return this.width;
        }
        public float qWidth() {
            return this.qWidth;
        }

        public ItemStack paymentType() {
            return this.paymentItem;
        }

        public int rotation() {
            return this.rotation;
        }

        public boolean updateIconRotation() {
            if(shouldUpdate){
                shouldUpdate = false; return true;}
            return false;
        }

        public void onTick(){
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
