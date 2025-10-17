package net.spudacious5705.shops.block.entity;


import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.spudacious5705.shops.block.ModBlockEntities;
import net.spudacious5705.shops.properties.Colour;
import net.spudacious5705.shops.util.CushionTextures;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class AngledShopEntity extends AbstractShopEntity{

    /**
     * Do not read from directly in case of null value
     * Use getCushionColour()
     */
    private Colour cushionColour;

    public AngledShopEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ANGLED_SHOP_ENTITY.get(), pos, state, 0.375f);
    }

    private static final String COLOUR_NBT_TAG = "cushion_colour";

    @Override
    public int getTextureId() {
        return 0;
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        if(tag.contains(COLOUR_NBT_TAG)) {
            this.cushionColour = Colour.fromId(tag.getInt(COLOUR_NBT_TAG));
        }else{
            this.cushionColour = Colour.RED;
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.putInt(COLOUR_NBT_TAG, this.getCushionColour().getId());
        super.saveAdditional(tag);
    }

    public Colour getCushionColour() {
        return this.cushionColour == null ? Colour.ORANGE : this.cushionColour;
    }

    public void setCushionColour(@NotNull Colour colour){
        this.cushionColour = colour;
    }

    public ResourceLocation getCushionTextureID() {
        return CushionTextures.TEXTURE_MAP.get(getCushionColour());
    }

}
