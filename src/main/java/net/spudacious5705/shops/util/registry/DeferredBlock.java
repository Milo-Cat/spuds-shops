package net.spudacious5705.shops.util.registry;

import net.minecraft.world.level.block.Block;

public class DeferredBlock<T extends Block> extends DeferredHolder<Block, T> {
    DeferredBlock(net.minecraft.resources.ResourceKey<Block> key) {
        super(key);
    }
}
