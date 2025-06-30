package net.spudacious5705.shops.item.custom;

import net.minecraft.item.BlockItem;
import net.spudacious5705.shops.block.custom.AngledShopBlock;
import net.spudacious5705.shops.properties.Colour;

public class ShopItem extends BlockItem {

    public final Colour colour;


    public ShopItem(AngledShopBlock block, Settings settings, Colour colour) {
        super(block, settings);
        this.colour = colour;
    }

}
