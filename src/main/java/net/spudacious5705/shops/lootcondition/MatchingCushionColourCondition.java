package net.spudacious5705.shops.lootcondition;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.spudacious5705.shops.block.entity.AngledShopEntity;
import java.util.Set;

public class MatchingCushionColourCondition implements LootCondition {
    public static final MapCodec<MatchingCushionColourCondition> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.STRING.fieldOf("expected_colour").forGetter(c -> c.expectedColourName)
            ).apply(instance, MatchingCushionColourCondition::new)
    );

    private final String expectedColourName;

    public MatchingCushionColourCondition(String expectedColourName) {
        this.expectedColourName = expectedColourName;
    }

    @Override
    public boolean test(LootContext context) {
        BlockEntity blockEntity = context.get(LootContextParameters.BLOCK_ENTITY);
        if (blockEntity instanceof AngledShopEntity shop) {
            return shop.getCushionColour().matchesString(expectedColourName);
        }
        return false;
    }

    @Override
    public LootConditionType getType() {
        return ModLootConditions.MATCHES_ENUM;
    }

    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return Set.of(LootContextParameters.BLOCK_ENTITY);
    }
}

