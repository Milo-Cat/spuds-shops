package net.spudacious5705.shops.permission;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.spudacious5705.shops.block.entity.AbstractShopEntity;
import net.spudacious5705.shops.item.ModItems;
import net.spudacious5705.shops.item.custom.ContractScroll;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Supplier;

import static net.spudacious5705.shops.item.custom.ContractScroll.*;
import static net.spudacious5705.shops.permission.PermissionLevel.*;

public class PermissionManager<B extends BlockEntity> implements IBlockPermissions<B>{

    private final B OwnerBlock;

    private final Supplier<Boolean> isCreativeSettingOn;

    public PermissionManager(B ownerBlock, Supplier<Boolean> isCreativeSettingOn) {
        OwnerBlock = ownerBlock;
        this.isCreativeSettingOn = isCreativeSettingOn;
    }

    @MagicConstant
    private static final int contractsInvSize = 24;
    private final NonNullList<ItemStack> contracts = NonNullList.withSize(contractsInvSize, ItemStack.EMPTY);

    public int contractCount(){return identificationRecords.size();}

    @MagicConstant
    private static final String CONTRACT_NAME = "contract_name";
    @MagicConstant
    private static final String CONTRACT_UUID = "contract_uuid";
    @MagicConstant
    private static final String CONTRACT_LEVEL = "contract_lvl";
    @MagicConstant
    private static final String CONTRACTS = "contracts";

    public void load(@NotNull CompoundTag tag) {
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
    }

    public void save(CompoundTag tag) {
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
    }


    /// assign to using nbt read write

    record PlayerID(UUID uuid, String name, PermissionLevel permissionLevel){

        public static final PlayerID EMPTY = new PlayerID(new UUID(0,0),"##OWNER NAME NULL##",PermissionLevel.CUSTOMER);

        public static PlayerID fromContract(ItemStack contract, PermissionLevel permissionLevel) {
            CustomData data = contract.get(DataComponents.CUSTOM_DATA);
            if (data != null) {
                CompoundTag tag = data.copyTag();
                if(tag.hasUUID(NBTuuid)){
                    return new PlayerID(
                    tag.getUUID(NBTuuid),
                            tag.getString(NBTname),
                            permissionLevel
                    );
                }
            }
            return null;
        }

    }

    public void copyRecordsToContracts(){
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

    private static ItemStack RecordToContract(PlayerID id){

        ItemStack stack = new ItemStack(ModItems.CONTRACT_SCROLL.get());

        ContractScroll.writeData(stack,id.name,id.uuid);

        return stack;
    }

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

    public boolean canBreakBlock(Player player, boolean decayed) {
        if(player.isCreative()||decayed)return true;
        if(identificationRecords.isEmpty()){
            return userSignIn(player).canBreakBlock();
        }
        return quickUserSignIn(player).canBreakBlock();
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

        if(isCreativeSettingOn.get()){
            if(!player.isCreative()){
                return PermissionLevel.CUSTOMER;
            }
        }

        if(identificationRecords.isEmpty()) {
            identificationRecords.add(new PlayerID(player.getUUID(), player.getName().getString(), PermissionLevel.OWNER));
            Level level = OwnerBlock.getLevel();
            if (level != null && !level.isClientSide()) {
                OwnerBlock.setChanged();
                ((AbstractShopEntity) OwnerBlock).forceUpdateClient();
            }
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
            Level level = OwnerBlock.getLevel();
            if (level != null && !level.isClientSide()) {
                OwnerBlock.setChanged();
                ((AbstractShopEntity) OwnerBlock).forceUpdateClient();
            }
        }

        return quickUserSignIn(player);
    }

    public void clearPermissions() {
        identificationRecords.clear();
    }

    public boolean containsNoPermissions() {
        return identificationRecords.isEmpty();
    }

    public Component cantBreakMessage() {
        return Component.literal("Cannot break - Owned by " + ownerName);
    }

    public player_ID_Records_Delegate createDelegate(PermissionLevel perms, UUID uuid) {
        return new player_ID_Records_Delegate(perms, uuid);
    }

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

                        if(OwnerBlock.getLevel() instanceof ServerLevel server){
                            server.players().stream().filter(
                                    player -> player.getUUID().compareTo(id.uuid) == 0
                            ).findFirst().ifPresent(p -> {
                                if (p instanceof net.minecraft.server.level.ServerPlayer) {
                                    ((net.minecraft.server.level.ServerPlayer) p).closeContainer();
                                }
                            });
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
        public @NotNull ItemStack removeItemNoUpdate(int pSlot) {
            return ItemStack.EMPTY;
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
            Level level = OwnerBlock.getLevel();
            if(level != null) {
                level.sendBlockUpdated(OwnerBlock.getBlockPos(), OwnerBlock.getBlockState(), OwnerBlock.getBlockState(), 3);
            }
            copyRecordsToContracts();
            OwnerBlock.setChanged();
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
                OwnerBlock.setChanged();
            }
        }

        @Override
        public void clearContent() {

        }
    }

}
