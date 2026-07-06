package net.spudacious5705.shops.lootcondition;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.spudacious5705.shops.SpudaciousShops;

import static net.spudacious5705.shops.SpudaciousShops.id;

public class ModLootConditions {
    public static final ResourceLocation RESOURCE_LOCATION = id("matches_colour");

    public static final LootItemConditionType MATCHES_ENUM = new LootItemConditionType(MatchingCushionColourCondition.CODEC);

    public static void registerLootConditions() {
        net.minecraft.core.Registry.register(
                BuiltInRegistries.LOOT_CONDITION_TYPE,
                RESOURCE_LOCATION,
                MATCHES_ENUM
        );
        SpudaciousShops.LOGGER.info("Registered loot conditions for " + SpudaciousShops.MOD_ID);
    }
}
