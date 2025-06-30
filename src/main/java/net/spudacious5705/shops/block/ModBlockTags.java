package net.spudacious5705.shops.block;

import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.spudacious5705.shops.SpudaciousShops;

public class ModBlockTags {
    public static final TagKey<Block> SPUDS_SHOPS = TagKey.of(RegistryKeys.BLOCK, SpudaciousShops.id("spuds_shops"));

    public static void initialise() {}
}
