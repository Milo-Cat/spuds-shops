package net.spudacious5705.shops.util.registry;

import net.minecraft.world.item.Item;

public class DeferredItem<T extends Item> extends DeferredHolder<Item, T> {
    DeferredItem(net.minecraft.resources.ResourceKey<Item> key) {
        super(key);
    }
}
