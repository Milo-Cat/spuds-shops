package net.spudacious5705.shops.lootcondition;


import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;

import static net.spudacious5705.shops.SpudaciousShops.MOD_ID;
import static net.spudacious5705.shops.SpudaciousShops.id;

@EventBusSubscriber(modid = MOD_ID)
public class ModLootConditions {
    public static final Identifier RESOURCE_LOCATION = id("matches_colour");

    public static final LootItemConditionType MATCHES_ENUM = new LootItemConditionType(MatchingCushionColourCondition.CODEC);

    @SubscribeEvent
    public static void registerLootConditions(RegisterEvent event) {
        event.register(Registries.LOOT_CONDITION_TYPE, helper -> helper.register(RESOURCE_LOCATION, MATCHES_ENUM));
    }
}

