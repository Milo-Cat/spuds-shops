package net.spudacious5705.shops.block;


import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.spudacious5705.shops.SpudaciousShops;

public class ModBlockTags {
    public static final TagKey<Block> SPUDS_SHOPS = TagKey.create(Registries.BLOCK, SpudaciousShops.getResource("spuds_shops"));

    public static void initialise() {}
}
