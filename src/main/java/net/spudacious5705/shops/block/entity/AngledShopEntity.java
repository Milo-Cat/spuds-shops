package net.spudacious5705.shops.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.spudacious5705.shops.util.CushionTextures;
import net.spudacious5705.shops.properties.Colour;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class AngledShopEntity extends AbstractShopEntity{

    /**
     * Do not read from directly in case of null value
     * Use getCushionColour()
     */
    private Colour cushionColour;

    public AngledShopEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ANGLED_SHOP_ENTITY, pos, state, 0.375f);
    }

    @Override
    public @Nullable Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    private static final String COLOUR_NBT_TAG = "cushion_colour";

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        if(nbt.contains(COLOUR_NBT_TAG)) {
            this.cushionColour = Colour.fromId(nbt.getInt(COLOUR_NBT_TAG));
        }else{
            this.cushionColour = Colour.RED;
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        nbt.putInt(COLOUR_NBT_TAG, this.getCushionColour().getId());
        super.writeNbt(nbt, registryLookup);
    }

    public Colour getCushionColour() {
        return this.cushionColour == null ? Colour.RED : this.cushionColour;
    }

    public void setCushionColour(@NotNull Colour colour){
        this.cushionColour = colour;
    }

    public Identifier getCushionTextureID() {
        return CushionTextures.TEXTURE_MAP.get(getCushionColour());
    }

}
