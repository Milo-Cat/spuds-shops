package net.spudacious5705.shops.util;

import net.minecraft.item.Item;
import net.spudacious5705.shops.block.custom.AbstractShopBlock;

import java.util.List;

public record MaterialShopPair<S extends AbstractShopBlock>(Item material, S shop) {

    public static <S extends AbstractShopBlock> MaterialShopPair<S> findShopByMaterial(List<MaterialShopPair<S>> list, Item targ){
        return list.stream()
                .filter(entry -> entry.material().equals(targ))  // Find matching item
                .findFirst()  // Get the first match
                .orElse(null);  // Return null if not found
    }
}
