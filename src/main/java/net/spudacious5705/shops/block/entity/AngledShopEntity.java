package net.spudacious5705.shops.block.entity;


import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.spudacious5705.shops.block.ModBlockEntities;
import net.spudacious5705.shops.properties.Colour;
import net.spudacious5705.shops.block.resources.CushionTextures;
import org.jetbrains.annotations.NotNull;


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
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider holder) {
        super.loadAdditional(tag, holder);
        if(tag.contains(COLOUR_NBT_TAG)) {
            this.cushionColour = Colour.fromId(tag.getInt(COLOUR_NBT_TAG));
        }else{
            this.cushionColour = Colour.RED;
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider holder) {
        tag.putInt(COLOUR_NBT_TAG, this.getCushionColour().getId());
        super.saveAdditional(tag, holder);
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
