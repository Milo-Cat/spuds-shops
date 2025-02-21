package net.spudacious5705.testinggrounds.datagen;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.spudacious5705.testinggrounds.TestingGrounds;

public class ModTags {
    public static class Blocks {

        public static final TagKey<Block> TESTING_BLOCKS = createTag("testing_blocks");

        private static TagKey<Block> createTag(String name) {
            return TagKey.of(RegistryKeys.BLOCK, Identifier.of(TestingGrounds.MOD_ID, name));
        }
    }

    public static class Items {
        public static final TagKey<Item> TRANSFORMABLE_ITEMS = createTag("transformable_items");

        public static final TagKey<Item> TESTING_ITEMS = createTag("testing_items");

        private static TagKey<Item> createTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, Identifier.of(TestingGrounds.MOD_ID, name));
        }
    }
}
