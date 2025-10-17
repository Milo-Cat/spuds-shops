package net.spudacious5705.shops.item.custom;

import net.minecraft.world.item.BlockItem;
import net.spudacious5705.shops.block.custom.AngledShopBlock;
import net.spudacious5705.shops.properties.Colour;

public class ShopItem extends BlockItem {

    public final Colour colour;


    public ShopItem(AngledShopBlock block, Properties properties, Colour colour) {
        super(block, properties);
        this.colour = colour;
    }

}
