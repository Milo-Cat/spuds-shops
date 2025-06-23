package net.spudacious5705.shops.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.SlabType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.spudacious5705.shops.screen.ModScreenHandlers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ShelfShopEntity extends AbstractShopEntity{

    private final ShopInventory shopInventoryTop = ShopInventory.create();

    @Environment(EnvType.CLIENT)
    protected ShelfRenderData furtherDataTop;
    @Environment(EnvType.CLIENT)
    protected ShelfRenderData furtherDataBottom;

    @Environment(EnvType.CLIENT)
    public ShelfRenderData furtherDataBottom(){return furtherDataBottom;}

    @Environment(EnvType.CLIENT)
    public ShelfRenderData furtherDataTop(){return furtherDataTop;}

    @Environment(EnvType.CLIENT)
    public static class ShelfRenderData {
        public final float itemLrotation = (float) ((Math.random() * 90) + 80f);
        public final float itemRrotation = (float) ((Math.random() * 90) + 80f);
    }

    @Override
    protected @NotNull ShopInventory otherInventory() {
        return shopInventoryTop;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void forceUpdateRenderData() {
        super.forceUpdateRenderData();
        rendererDataTop.update();
    }

    @Override
    public void itemScatter(World world, BlockPos pos) {
        super.itemScatter(world, pos);
        ItemScatterer.spawn(world, pos, shopInventoryTop.prepForItemScatterer());

    }

    @Override
    public void renderTick() {
        this.rendererData.onTick();
        this.rendererDataTop.onTick();
    }

    public ShelfShopEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SHELF_SHOP_ENTITY, pos, state, -0.3f);

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            this.rendererDataTop = new RendererData(shopInventoryTop);
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    protected void createRendererData() {
        this.rendererData = new RendererData(shopInventory);
        this.furtherDataTop = new ShelfRenderData();
        this.furtherDataBottom = new ShelfRenderData();
    }

    @Environment(EnvType.CLIENT)
    protected RendererData rendererDataTop;


    @Environment(EnvType.CLIENT)
    public RendererData rendererDataTop(){return  rendererDataTop;}

    @Override
    public @Nullable Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    protected boolean hasTrade() {
        return shopInventory.tradeFunctional()||shopInventoryTop.tradeFunctional();
    }



    @Override
    public int getTextureId() {
        return 0;
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NbtList nbtList = new NbtList();

        for (int i = 0; i < shopInventoryTop.size(); i++) {
            ItemStack itemStack = shopInventoryTop.get(i);
            if (!itemStack.isEmpty()) {
                NbtCompound nbtCompound = new NbtCompound();
                nbtCompound.putByte("SlotTwo", (byte)i);
                nbtList.add(itemStack.encode(registryLookup, nbtCompound));
            }
        }

        if (!nbtList.isEmpty()) {
            nbt.put("ItemsTwo", nbtList);
        }

        super.writeNbt(nbt, registryLookup);
    }


    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {

        NbtList nbtList = nbt.getList("ItemsTwo", NbtElement.COMPOUND_TYPE);

        for (int i = 0; i < nbtList.size(); i++) {
            NbtCompound nbtCompound = nbtList.getCompound(i);
            int j = nbtCompound.getByte("SlotTwo") & 255;
            if (j < shopInventoryTop.size()) {
                shopInventoryTop.set(j, ItemStack.fromNbt(registryLookup ,nbtCompound).orElse(ItemStack.EMPTY));
            }
        }

        super.readNbt(nbt, registryLookup);
    }
    
    
}
