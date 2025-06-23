package net.spudacious5705.shops.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.spudacious5705.shops.SpudaciousShops;
import net.spudacious5705.shops.block.custom.AbstractShopBlock;
import net.spudacious5705.shops.block.entity.renderer.ShopRenderUtils;
import net.spudacious5705.shops.item.ModItems;
import net.spudacious5705.shops.item.custom.ContractScroll;
import net.spudacious5705.shops.properties.PermissionLevel;
import net.spudacious5705.shops.screen.ModScreenHandlers;
import net.spudacious5705.shops.screen.ScreenSettingsGroup;
import net.spudacious5705.shops.screen.ShopScreenHandlerCustomer;
import net.spudacious5705.shops.screen.ShopScreenHandlerOwner;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static net.spudacious5705.shops.block.entity.ShopInventory.*;
import static net.spudacious5705.shops.item.custom.ContractScroll.isSigned;
import static net.spudacious5705.shops.screen.ShopScreenHandlerOwner.canUseInTrade;

public abstract class AbstractShopEntity extends BlockEntity implements ExtendedScreenHandlerFactory<ModScreenHandlers.ShopScreenPayload>{

    //region INVENTORY

    protected final ShopInventory shopInventory = ShopInventory.create();

    @NotNull
    public InventoryDelegate getInventoryDelegate(PlayerEntity player) {
        return new InventoryDelegate(player, this.shopInventory);
    }

    public void itemScatter(World world, BlockPos pos) {
        ItemScatterer.spawn(world,pos,shopInventory.prepForItemScatterer());
        int contractsCount = identificationRecords.size()-1;
        if(contractsCount>0){
            ItemScatterer.spawn(world,pos.getX(),pos.getY(),pos.getZ(),new ItemStack(ModItems.CONTRACT_SCROLL,contractsCount));
        }
    }

    public ScreenSettingsGroup getScreenSettings() {
        return ((AbstractShopBlock) this.getCachedState().getBlock()).getScreenSettings();
    }

    public final class InventoryDelegate implements Inventory{
        private final ShopInventory inventory;
        private final PermissionLevel permissions;
        private final UUID reciever_UUID;

        public InventoryDelegate(PlayerEntity player, ShopInventory items) {
            this.permissions = userSignIn(player);
            this.reciever_UUID = player.getUuid();
            this.inventory = items;
        }

        public PermissionLevel checkPermissions(){
            return permissions;
        }

        @Override
        public int size() {
            return inventory.size();
        }

        @Override
        public boolean isEmpty() {
            return inventory.isEmpty();
        }

        @Override
        public ItemStack getStack(int slot) {
            if(slot>this.size()||slot<0) return ItemStack.EMPTY;

            if(slot>PROFIT_END) {
                return inventory.get(slot);
            }

            if(permissions.canViewShopScreen()) return inventory.get(slot);

            return ItemStack.EMPTY;
        }


        public void trade(PlayerInventory playerInv){
            DefaultedList<ItemStack> vendList = takeItems(inventory.getVendingStack(), inventory::get, 0, STOCK_END);
            DefaultedList<ItemStack> payList = takeItems(inventory.getPaymentStack(), playerInv::getStack,0,36);


            ItemStack allowStack = inventory.getPaymentStack();
            ItemStack storageStack;
            int space;
            int ptr = 0;
            for(int i = STOCK_END+1; i <= PROFIT_END; i++){
                storageStack = inventory.get(i);
                if(canUseInTrade(storageStack,allowStack)||storageStack.isEmpty()){
                    while (ptr<(payList.size()) && (storageStack.getCount() < storageStack.getMaxCount())){
                        space = getAvalableSpace(storageStack);
                        if(storageStack.isEmpty()){
                            storageStack = payList.get(ptr).copyAndEmpty();
                        }else {
                            storageStack.setCount(payList.get(ptr).split(space).getCount() + storageStack.getCount());
                        }
                        inventory.set(i,storageStack);
                        if(payList.get(ptr).isEmpty()){
                            ptr++;
                        }
                    }
                }
            }

            ptr = 0;
            boolean success = true;
            while(success && ptr<(vendList.size())){
                success = playerInv.insertStack(vendList.get(ptr));
            }

            PlayerEntity player = playerInv.player;
            ItemScatterer.spawn(player.getWorld(),player.getBlockPos(),vendList);

        }

        public boolean canTrade(PlayerEntity playerEntity){
            if (inventory.outOfStock()) {
                errorMessage("Shop is out of stock", playerEntity);
                return false;
            }
            if (inventory.paymentRegisterFull()) {
                errorMessage("Shop cannot store any more currency", playerEntity);
                return false;
            }
            if (inventory.isPlayerPoor(playerEntity)) {
                errorMessage("You do not have enough currency", playerEntity);
                return false;
            }
            return true;
        }


        private void errorMessage(String message, PlayerEntity player){
            if(player.getWorld().isClient()) {
                player.sendMessage(Text.of(message), true);
            }
        }


        private interface miniDelegate{
            ItemStack getStack(int index);
        }

        private static DefaultedList<ItemStack> takeItems(ItemStack retrieveStack, miniDelegate inventory, int start, int end){
            DefaultedList<ItemStack> list = DefaultedList.ofSize(end-start);
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

        private static void addToList(DefaultedList<ItemStack> list, ItemStack stack){
            if(list.isEmpty()){
                list.add(stack.copyAndEmpty());
                return;
            }
            int end = list.size()-1;
            ItemStack listEnd = list.get(end);
            int space = getAvalableSpace(listEnd);
            list.set(end,
                    listEnd.copyWithCount(
                            listEnd.getCount()+
                                    stack.split(space).getCount()
                    ));
            if(!stack.isEmpty()){
                list.add(stack.copyAndEmpty());
            }
        }

        private static int getAvalableSpace(ItemStack stack){
            return Math.max(stack.getMaxCount()-stack.getCount(), 0);
        }

        @Override
        public ItemStack removeStack(int slot, int amount) {

            if(slot>this.size()||slot<0) return ItemStack.EMPTY;

            if(slot<PAYMENT_SLOT){
                if(permissions.canTakeItems()) return inventory.split(slot, amount);
            } else if(permissions.canEditTrades()){
                inventory.split(slot, amount);
            }

            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeStack(int slot) {
            return removeStack(slot,64);
        }

        @Override
        public void setStack(int slot, ItemStack stack) {
            if(slot>=PAYMENT_SLOT){
                if(this.permissions.canEditTrades()){
                    inventory.set(slot, stack);
                }
            } else if(this.permissions.canImportStock()){
                inventory.set(slot, stack);
            }
        }

        @Override
        public void markDirty() {
            assert world != null;
            world.updateListeners(pos,getCachedState(),getCachedState(),3);
            isShopFunctional();
            AbstractShopEntity.this.markDirty();
        }

        @Override
        public boolean canPlayerUse(PlayerEntity player) {
            return player.getUuid().compareTo(reciever_UUID) == 0;
        }


        @Override
        public void clear() {
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
    public final InventoryDelegate getOtherInventoryDelegate(PlayerEntity player){
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
    public final player_ID_Records_Delegate getRecordsDelegate(PlayerEntity player) {
        PermissionLevel perm = userSignIn(player);
        if(perm.canViewShopScreen()){
            return new player_ID_Records_Delegate(perm,player.getUuid());
        }
        return null;
    }

    private record PlayerID(UUID uuid, String name, PermissionLevel permissionLevel){

        public static final PlayerID EMPTY = new PlayerID(new UUID(0,0),"##OWNER NAME NULL##",PermissionLevel.CUSTOMER);

        public static PlayerID fromContract(ItemStack contract, PermissionLevel permissionLevel) {

            if (ContractScroll.isSigned(contract)) {

                Text nameText = ContractScroll.getPlayerName(contract);
                UUID uuid = ContractScroll.getUUID(contract);

                String name = nameText == null ? "##OWNER NAME NULL##" : nameText.getString();

                return new PlayerID(
                        uuid,
                        name,
                        permissionLevel
                );
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
    private final DefaultedList<ItemStack> contracts = DefaultedList.ofSize(contractsInvSize, ItemStack.EMPTY);/// assign to using nbt read write

    public final class player_ID_Records_Delegate implements Inventory{
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
                                    markDirty();
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
                if(contract.getItem()==ModItems.CONTRACT_SCROLL){
                    return ContractScroll.isSigned(contract);
                }
            }
            return false;
        }

        private static boolean checkIndex(int index){
            return index<contractsInvSize&&index>=0;
        }

        private boolean canEditThat(int index) {
            if(checkIndex(index)) {
                if (perms.canEditPermissions()) {
                    PermissionLevel perm = permFromIndex(index);
                    if (perm.asInt() < PermissionLevel.MANAGER.asInt()) {
                        return true;
                    } else if(perms == PermissionLevel.OWNER){
                        return true;
                    }
                    return checkContract(fetchContract(index,false));
                }
            }
            return false;
        }

        @Override
        public int size() {
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

                        if(world instanceof ServerWorld server){
                            server.getPlayers().stream().filter(
                                    player -> player.getUuid().compareTo(id.uuid) == 0
                            ).findFirst().ifPresent(ServerPlayerEntity::closeHandledScreen);
                        }

                        contracts.set(index, ItemStack.EMPTY);
                        markDirty();
                    }


                    return record;
                }
            }
            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack getStack(int index) {
            return fetchContract(index,false);
        }

        @Override
        public ItemStack removeStack(int index, int amount) {//stack is always size of 1
            return removeStack(index);
        }

        @Override
        public ItemStack removeStack(int index) {
            if(canEditThat(index)) {
                ItemStack contract = fetchContract(index, true);
                markDirty();
                return contract;
            }
            return ItemStack.EMPTY;
        }

        @Override
        public void setStack(int index, ItemStack contract) {
        }

        @Override
        public void markDirty() {
            if(world != null) {
                world.updateListeners(pos, getCachedState(), getCachedState(), 3);
            }
            copyRecordsToContracts();
            AbstractShopEntity.this.markDirty();
        }


        @Override
        public boolean canPlayerUse(PlayerEntity player) {
            return perms.canEditPermissions();
        }

        /**
         * does nothing
         */
        @Override
        public void clear() {
        }

        public boolean checkContract(@Nullable ItemStack stack) {
            if(stack != null) {
                UUID uuid = ContractScroll.getUUID(stack);
                if (uuid != null) {
                    return 0 == uuid.compareTo(userUUID);
                }
            }
            return false;
        }

        public void selfDemote(ServerPlayerEntity player) {
            if(player.getUuid().compareTo(userUUID)==0){
                identificationRecords.removeIf(playerID -> playerID.uuid.compareTo(userUUID)==0);
                markDirty();
            }
        }
    }

    private static ItemStack RecordToContract(PlayerID id){

        ItemStack stack = new ItemStack(ModItems.CONTRACT_SCROLL);

        ContractScroll.sign(stack, Text.of(id.name), id.uuid);

        return stack;
    }

    public PermissionLevel quickUserSignIn(@NotNull PlayerEntity player){

        UUID signIn = player.getUuid();

        PlayerID id = identificationRecords.stream().filter(record -> record.uuid.compareTo(signIn)==0).findFirst().orElse(null);

        if(id != null){
            return id.permissionLevel;
        }

        if(player.isCreative()) return PermissionLevel.SERVER_ADMIN;
        return PermissionLevel.CUSTOMER;
    }

    public PermissionLevel userSignIn(PlayerEntity player) {

        if(identificationRecords.isEmpty()) {
            identificationRecords.add(new PlayerID(player.getUuid(), player.getName().getString(), PermissionLevel.OWNER));
            markDirty();
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
            markDirty();
        }

        return quickUserSignIn(player);
    }


    //endregion


    //region NBT

    @Override
    public void markDirty() {
        if(isClient) {
            forceUpdateRenderData();
        }
        super.markDirty();
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
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        Inventories.readNbt(nbt, shopInventory,registryLookup);
        identificationRecords.clear();

        if(nbt.contains(CONTRACTS, NbtCompound.LIST_TYPE)){
            NbtList contractList = nbt.getList(CONTRACTS, NbtElement.COMPOUND_TYPE);

            for (int index = 0; index < contractList.size(); index++) {
                NbtCompound contract = contractList.getCompound(index);

                String name = contract.getString(CONTRACT_NAME);
                UUID uuid = contract.getUuid(CONTRACT_UUID);
                PermissionLevel perms = PermissionLevel.fromInt(contract.getInt(CONTRACT_LEVEL));

                if(perms.asInt()>0){
                    identificationRecords.add(
                            new PlayerID(
                                    uuid,
                                    name,
                                    perms
                            )
                    );
                }

            }
        }

        copyRecordsToContracts();

        if(nbt.containsUuid("owner_id")) {//convert legacy owner to updated system
            UUID ownerID = nbt.getUuid("owner_id");
            String name;
            if(nbt.contains("owner_name")) {
                name = nbt.getString("owner_name");
            } else {
                name = ownerID.toString();
            }

            identificationRecords.add(new PlayerID(ownerID, name, PermissionLevel.OWNER));

        }


        if(nbt.contains("decay_timer")) {
            this.decayTimer = nbt.getInt("decay_timer");
        }
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
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        Inventories.writeNbt(nbt, shopInventory, false,registryLookup);

        NbtList contractList = new NbtList();

        for(PlayerID id :identificationRecords){

            NbtCompound contractNBT = new NbtCompound();

            contractNBT.putString(CONTRACT_NAME,id.name);
            contractNBT.putUuid(CONTRACT_UUID,id.uuid);
            contractNBT.putInt(CONTRACT_LEVEL,id.permissionLevel.asInt());

            contractList.add(contractNBT);
        }

        if (!contractList.isEmpty()) {
            nbt.put(CONTRACTS, contractList);
        }


        nbt.putInt("decay_timer",this.decayTimer);
        super.writeNbt(nbt,registryLookup);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }

    
    //endregion


    public <SHOP extends AbstractShopEntity>AbstractShopEntity(BlockEntityType<SHOP> type, BlockPos pos, BlockState state, float particleOffset) {
        super(type, pos, state);
        this.particleOffset = particleOffset;

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            createRendererData();
            isClient = true;
        } else {
            isClient = false;
        }
    }

    private final boolean isClient;

    @Environment(EnvType.CLIENT)
    protected void createRendererData(){
        this.rendererData = new RendererData(shopInventory);
    }

    private boolean decayed = false;

    public void serverTick(ServerWorld world, BlockPos pos, AbstractShopBlock.AbstractShopBlockState shopState) {

        if(decayTimer > -1) {
            if (decayTimer > hourInTicks) {

                if (world.random.nextFloat() < 0.05f) {
                    for (int i = 0; i < 3; i++) {
                        world.spawnParticles(ParticleTypes.ANGRY_VILLAGER, pos.getX() + .2 + world.random.nextFloat(), pos.getY() + world.random.nextFloat() + particleOffset, pos.getZ() + world.random.nextFloat(), 1, 0, 0, 0, 0);
                    }
                }

                if (shopState.unbreakable()) {

                    decayed = true;

                    identificationRecords.clear();
                    shopState.makeBreakable(world, pos);
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
        if(shopState.unbreakable())return;

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
            shopState.makeUnbreakable(world, pos);
            breakableTicks = -1; // Reset
        }
    }


    //region SCREEN
    @Deprecated
    @Override
    public @Nullable final ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {

        SpudaciousShops.LOGGER.debug("Attempted to directly retrieve ScreenHandler from shop");

        return null;
    }

    public ExtendedScreenHandlerFactory<ModScreenHandlers.ShopScreenPayload> createScreenHandlerFactory(boolean openTop) {

        return new ExtendedScreenHandlerFactory<>() {

            @Override
            public ModScreenHandlers.ShopScreenPayload getScreenOpeningData(ServerPlayerEntity player) {
                return new ModScreenHandlers.ShopScreenPayload(pos,openTop);
            }

            @Override
            public Text getDisplayName() {
                return Text.literal("Shop");
            }

            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                PermissionLevel perms = userSignIn(player);


                InventoryDelegate inventoryDelegate = openTop?getOtherInventoryDelegate(player):getInventoryDelegate(player);
                player_ID_Records_Delegate recordsDelegate = new player_ID_Records_Delegate(perms, player.getUuid());

                if (perms.canViewShopScreen()) {
                    return new ShopScreenHandlerOwner(syncId, playerInventory, inventoryDelegate, recordsDelegate, getScreenSettings());
                }

                if (!isShopFunctional()) {
                    return null;
                }

                return new ShopScreenHandlerCustomer(syncId, playerInventory, inventoryDelegate, getTextureId());
            }
        };
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Shop");
    }

    public abstract int getTextureId();

    @Override
    public ModScreenHandlers.ShopScreenPayload getScreenOpeningData(ServerPlayerEntity player) {
        return new ModScreenHandlers.ShopScreenPayload(pos,false);
    }

    //endregion
    

    //region STATE LOGIC

    @Override
    public BlockState getCachedState() {
        return super.getCachedState();
    }

    @Nullable
    @Override
    public abstract Packet<ClientPlayPacketListener> toUpdatePacket();

    public boolean isUnbreakable(PlayerEntity player) {
        if(player.isCreative()||decayed)return false;
        if(identificationRecords.isEmpty()){
            return !userSignIn(player).canBreakBlock();
        }
        return !quickUserSignIn(player).canBreakBlock();
    }


    public Text cantBreakMessage() {
        return Text.of("Cannot break - Owned by " + ownerName);
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
        if(world != null) {
            return !identificationRecords.isEmpty();
        }
        return false;
    }

    protected boolean hasTrade(){
        return shopInventory.tradeFunctional();
    }

    protected int checkIntervalTimer = 200;//short initial check interval for server restarts

    protected int breakableTicks = -1;
    //endregion


    //region RENDERING
    @Environment(EnvType.CLIENT)
    public void renderTick() {
        this.rendererData.onTick();
    }

    @Environment(EnvType.CLIENT)
    protected RendererData rendererData;

    @Environment(EnvType.CLIENT)
    public RendererData rendererData(){return  rendererData;}
    //Only call from the CLIENT
    @Environment(EnvType.CLIENT)
    public void forceUpdateRenderData() {
        rendererData.update();
    }

    final float particleOffset;

    public Direction getCachedFacingDirection(){
        Direction direction = this.getCachedState().get(Properties.HORIZONTAL_FACING);
        if(direction == null) return Direction.NORTH;
        return direction;
    }

    @Environment(EnvType.CLIENT)
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

                MinecraftClient client = MinecraftClient.getInstance();

                if(displayItem.getItem() instanceof BlockItem){
                    BakedModel model = client.getItemRenderer().getModel(displayItem, null, null, 0);
                    stockDisplayType = model.hasDepth();
                } else {
                    stockDisplayType = false;
                }

                if(paymentItem.getItem() instanceof BlockItem){
                    BakedModel model = client.getItemRenderer().getModel(paymentItem, null, null, 0);
                    currencyDisplayType = model.hasDepth();
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

        public World world() {
            return world;
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
            return pos.getX();
        }

        public int y() {
            return pos.getY();
        }

        public int z() {
            return pos.getZ();
        }
    }
    //endregion


}
