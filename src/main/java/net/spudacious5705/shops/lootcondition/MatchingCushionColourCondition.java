package net.spudacious5705.shops.lootcondition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.spudacious5705.shops.block.entity.AngledShopEntity;
import org.jetbrains.annotations.NotNull;

public record MatchingCushionColourCondition(String expectedColourName) implements LootItemCondition {

    public static final MapCodec<MatchingCushionColourCondition> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(Codec.STRING.fieldOf("expected_colour").forGetter(MatchingCushionColourCondition::expectedColourName))
                    .apply(instance, MatchingCushionColourCondition::new)
    );

    @Override
    public @NotNull LootItemConditionType getType() {
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
}
