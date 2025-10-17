package net.spudacious5705.shops.lootcondition;


import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegisterEvent;
import net.spudacious5705.shops.SpudaciousShops;

@Mod.EventBusSubscriber(modid = SpudaciousShops.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModLootConditions {
    public static final ResourceLocation RESOURCE_LOCATION = ResourceLocation.fromNamespaceAndPath(SpudaciousShops.MOD_ID, "matches_colour");

    public static final LootItemConditionType MATCHES_ENUM = new LootItemConditionType(new MatchingCushionColourCondition.ConditionSerializer());

    @SubscribeEvent
    public static void registerLootConditions(RegisterEvent event) {
        event.register(Registries.LOOT_CONDITION_TYPE, helper -> {
            helper.register(RESOURCE_LOCATION, MATCHES_ENUM);
        });
    }
}

