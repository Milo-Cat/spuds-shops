package net.spudacious5705.shops.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class ContractScroll extends Item {
    public ContractScroll(Item.Properties properties) {
        super(properties.rarity(Rarity.UNCOMMON).stacksTo(1));
    }


    @Override
    public @NotNull Rarity getRarity(@NotNull ItemStack pStack) {
        return Rarity.UNCOMMON;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level world, Player user, @NotNull InteractionHand hand) {

        ItemStack stack = user.getItemInHand(hand);

        if (isSigned(stack)) {
            return InteractionResultHolder.pass(stack);
        }

        CompoundTag nbt = new CompoundTag();

        String name = user.getName().getString();

        nbt.putString(NBTname, name);
        nbt.putUUID(NBTuuid, user.getUUID());

        stack.setTag(nbt);

        stack.setHoverName(Component.literal("Contract - "+name));

        BlockPos pos = user.getOnPos();
        world.playSound(user, pos.getX(),pos.getY(),pos.getZ(), SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 1f,1f);

        return InteractionResultHolder.success(stack);
    }

    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return isSigned(stack);
    }

    public static final String NBTuuid = "player_uuid";
    public static final String NBTname = "player_name";

    public static boolean isSigned(ItemStack stack) {
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            return tag != null && tag.contains(NBTuuid);
        }
        return false;
    }

    @Nullable
    public static UUID getUUID(ItemStack stack) {
        if (isSigned(stack)) {
            CompoundTag tag = stack.getTag();
            return tag != null ? tag.getUUID(NBTuuid) : null;
        }
        return null;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        if (isSigned(stack)) {
            assert stack.getTag() != null;
            tooltip.add(Component.literal("Signed by - " + stack.getTag().getString(NBTname)));
        }
    }
}
