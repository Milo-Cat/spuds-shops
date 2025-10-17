package net.spudacious5705.shops.lootcondition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.spudacious5705.shops.block.entity.AngledShopEntity;

public class MatchingCushionColourCondition implements LootItemCondition {
    private final String expectedColourName;

    public MatchingCushionColourCondition(String expectedColourName) {
        this.expectedColourName = expectedColourName;
    }

    @Override
    public LootItemConditionType getType() {
        return ModLootConditions.MATCHES_ENUM;
    }

    @Override
    public boolean test(LootContext lootContext) {
        BlockEntity blockEntity = lootContext.getParam(LootContextParams.BLOCK_ENTITY);
        if (blockEntity instanceof AngledShopEntity shop) {
            return shop.getCushionColour().matchesString(expectedColourName);
        }
        return false;
    }

    public static class ConditionSerializer implements Serializer<MatchingCushionColourCondition> {
        @Override
        public void serialize(JsonObject json, MatchingCushionColourCondition condition, JsonSerializationContext context) {
            json.addProperty("expected_colour", condition.expectedColourName);
        }

        @Override
        public MatchingCushionColourCondition deserialize(JsonObject json, JsonDeserializationContext context) {
            String colourName = json.get("expected_colour").getAsString();
            return new MatchingCushionColourCondition(colourName);
        }
    }
}
