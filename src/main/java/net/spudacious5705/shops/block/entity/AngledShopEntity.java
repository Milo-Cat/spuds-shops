package net.spudacious5705.shops.block.entity;


import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.spudacious5705.shops.block.ModBlockEntities;
import net.spudacious5705.shops.block.resources.CushionTextures;
import net.spudacious5705.shops.properties.Colour;
import org.jetbrains.annotations.NotNull;


public class AngledShopEntity extends AbstractShopEntity {

    private static final String COLOUR_NBT_TAG = "cushion_colour";
    /**
     * Do not read from directly in case of null value
     * Use getCushionColour()
     */
    private Colour cushionColour;

    public AngledShopEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ANGLED_SHOP_ENTITY.get(), pos, state, 0.375f);
    }

    @Override
    protected void loadAdditional(@NotNull ValueInput input) {
        super.loadAdditional(input);
        this.cushionColour = Colour.fromId(input.getIntOr(COLOUR_NBT_TAG, 0));
    }

    @Override
    protected void saveAdditional(@NotNull ValueOutput output) {
        output.putInt(COLOUR_NBT_TAG, this.getCushionColour().getId());
        super.saveAdditional(output);
    }

    public Colour getCushionColour() {
        return this.cushionColour == null ? Colour.ORANGE : this.cushionColour;
    }

    public void setCushionColour(@NotNull Colour colour) {
        this.cushionColour = colour;
    }

    public Identifier getCushionTextureID() {
        return CushionTextures.TEXTURE_MAP.get(getCushionColour());
    }

}
