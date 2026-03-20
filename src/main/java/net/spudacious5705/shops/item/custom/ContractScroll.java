package net.spudacious5705.shops.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
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
import net.minecraft.world.item.component.CustomData;
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
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level world, Player user, @NotNull InteractionHand hand) {

        ItemStack stack = user.getItemInHand(hand);

        if (isSigned(stack)) {
            return InteractionResultHolder.pass(stack);
        }

        writeData(stack,user);

        BlockPos pos = user.getOnPos();
        world.playSound(user, pos.getX(),pos.getY(),pos.getZ(), SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 1f,1f);

        return InteractionResultHolder.success(stack);
    }

    public static void writeData(ItemStack stack, Player user){
        writeData(stack,
                user.getName().getString(),
                user.getUUID()
        );
    }

    public static void writeData(ItemStack stack, String name, UUID uuid){
        CompoundTag nbt = new CompoundTag();

        nbt.putString(NBTname, name);
        nbt.putUUID(NBTuuid, uuid);

        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));

        stack.set(DataComponents.CUSTOM_NAME, Component.literal("Contract - "+name));
    }

    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return isSigned(stack);
    }

    public static final String NBTuuid = "player_uuid";
    public static final String NBTname = "player_name";

    public static boolean isSigned(ItemStack stack) {
        return getUUID(stack) != null;
    }

    @Nullable
    public static UUID getUUID(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data != null) {
            CompoundTag tag = data.copyTag();
            return tag.getUUID(NBTuuid);
        }
        return null;
    }

    @Override
    public void appendHoverText(
            ItemStack stack, @NotNull TooltipContext context,
            @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data != null) {
            CompoundTag tag = data.copyTag();
            if(tag.hasUUID(NBTuuid)) {
                tooltip.add(Component.literal("Signed by - " + tag.getString(NBTname)));
            }
        }
    }
}
