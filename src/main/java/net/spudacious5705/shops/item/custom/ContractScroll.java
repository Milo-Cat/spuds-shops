package net.spudacious5705.shops.item.custom;


import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.spudacious5705.shops.SpudaciousShops;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class ContractScroll extends Item {

    public static class UUIDUtil {
        public static final Codec<UUID> CODEC = Codec.STRING.xmap(
                UUID::fromString,
                UUID::toString
        );
    }

    public static final ComponentType<String> PLAYER_NAME_COMPONENT = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            SpudaciousShops.id("player_name"),
            ComponentType.<String>builder()
                    .codec(Codec.STRING)
                    .build()
    );

    public static final ComponentType<UUID> PLAYER_UUID_COMPONENT = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            SpudaciousShops.id("player_uuid"),
            ComponentType.<UUID>builder()
                    .codec(UUIDUtil.CODEC)
                    .build()
    );

    public ContractScroll(Settings settings) {
        super(settings.rarity(Rarity.UNCOMMON).maxCount(1).component(PLAYER_NAME_COMPONENT,null).component(PLAYER_UUID_COMPONENT,null));
    }


    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (isSigned(stack)) {
            return TypedActionResult.pass(stack);
        }

        sign(stack,user.getName(),user.getUuid());

        BlockPos pos = user.getBlockPos();
        world.playSound(pos.getX(),pos.getY(),pos.getZ(), SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.PLAYERS, 1f,1f,true);

        return TypedActionResult.success(stack);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return isSigned(stack);
    }

    public static boolean isSigned(ItemStack stack){
        if (stack.getComponents().contains(PLAYER_UUID_COMPONENT)) {
            UUID originalUUID = stack.getComponents().get(PLAYER_UUID_COMPONENT);
            return originalUUID != null;
        }
        return false;
    }

    @Nullable
    public static UUID getUUID(ItemStack stack){
        if (stack.getComponents().contains(PLAYER_UUID_COMPONENT)) {
            return stack.getComponents().get(PLAYER_UUID_COMPONENT);
        }
        return null;
    }

    @Nullable
    public static Text getPlayerName(ItemStack stack){
        if (stack.getComponents().contains(PLAYER_NAME_COMPONENT)) {
            return Text.of(stack.getComponents().get(PLAYER_NAME_COMPONENT));
        }
        return null;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        if(isSigned(stack)){
            tooltip.add(Text.of("Signed by - " + getPlayerName(stack)));
        }
    }

    public static void sign(ItemStack stack, Text name, UUID uuid) {
        stack.set(PLAYER_NAME_COMPONENT,name.getString());
        stack.set(PLAYER_UUID_COMPONENT,uuid);
        stack.set(DataComponentTypes.CUSTOM_NAME,Text.of("Contract - "+name));
    }
}
