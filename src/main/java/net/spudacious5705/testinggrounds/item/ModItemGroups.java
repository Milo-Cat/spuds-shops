package net.spudacious5705.testinggrounds.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.spudacious5705.testinggrounds.TestingGrounds;
import net.spudacious5705.testinggrounds.block.ModBlocks;

public class ModItemGroups {
    public static final ItemGroup MY_GROUP = Registry.register(Registries.ITEM_GROUP,
            new Identifier(TestingGrounds.MOD_ID, "my_group"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.my_group"))
                    .icon(() -> new ItemStack(ModItems.SUSPICIOUS_SUBSTANCE)).entries((displayContext, entries) -> {
                        entries.add(ModItems.SUSPICIOUS_SUBSTANCE);
                        entries.add(ModItems.RUNIC_GOLD);

                        entries.add(ModBlocks.SUSPICIOUS_BLOCK);
                        entries.add(ModBlocks.DENSE_WOOD_BLOCK);

                        entries.add(ModBlocks.PAINFULL_BLOCK);
                        entries.add(ModBlocks.STATION);

                        entries.add(ModBlocks.MAGIC_BLOCK);

                        entries.add(ModBlocks.SHOP_BLOCK_ACACIA);
                        entries.add(ModBlocks.SHOP_BLOCK_BAMBOO);
                        entries.add(ModBlocks.SHOP_BLOCK_BIRCH);
                        entries.add(ModBlocks.SHOP_BLOCK_CHERRY);
                        entries.add(ModBlocks.SHOP_BLOCK_CRIMSON);
                        entries.add(ModBlocks.SHOP_BLOCK_OAK);
                        entries.add(ModBlocks.SHOP_BLOCK_MANGROVE);
                        entries.add(ModBlocks.SHOP_BLOCK_DARK_OAK);
                        entries.add(ModBlocks.SHOP_BLOCK_SPRUCE);
                        entries.add(ModBlocks.SHOP_BLOCK_WARPED);

                        //entries.add(ModBlocks.TROPHY);

                        entries.add(ModBlocks.INSCRIBER_BLOCK);
                    }).build());

    public static void registerItemGroups() {
    }
}
