package net.spudacious5705.shops.item.custom;

import com.mojang.serialization.DataResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class ContractScroll extends Item {
    public static final String NBTuuid = "player_uuid";
    public static final String NBTname = "player_name";


    public static void writeUuid(CompoundTag tag, UUID uuid) {
        UUIDUtil.CODEC.encodeStart(NbtOps.INSTANCE, uuid).ifSuccess(value -> tag.put(NBTuuid, value));
    }

    public static Optional<UUID> readUuid(CompoundTag tag) {
        if (tag.contains(NBTuuid)) {
            return UUIDUtil.CODEC.parse(NbtOps.INSTANCE, tag.get(NBTuuid)).result();
        }
        return Optional.empty();
    }

    public ContractScroll(Item.Properties properties) {
        super(properties.rarity(Rarity.UNCOMMON).stacksTo(1));
    }

    public static void writeData(ItemStack stack, Player user) {
        writeData(stack,
                user.getName().getString(),
                user.getUUID()
        );
    }

    public static void writeData(ItemStack stack, String name, UUID uuid) {
        CompoundTag nbt = new CompoundTag();

        DataResult<Tag> uuidTag = UUIDUtil.CODEC.encodeStart(NbtOps.INSTANCE, uuid);
        nbt.putString(NBTname, name);
        nbt.put(NBTuuid, uuidTag.getOrThrow());

        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));

        stack.set(DataComponents.CUSTOM_NAME, Component.literal("Contract - " + name));
    }

    public static boolean isSigned(ItemStack stack) {
        return getUUID(stack) != null;
    }

    @Nullable
    public static UUID getUUID(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data != null) {
            var u = readUuid(data.copyTag());
            if(u.isPresent()){
                return u.get();
            }
        }
        return null;
    }

    @Override
    public @NonNull InteractionResult use(@NotNull Level world, Player user, @NotNull InteractionHand hand) {

        ItemStack stack = user.getItemInHand(hand);

        if (isSigned(stack)) {
            return InteractionResult.PASS;
        }

        writeData(stack, user);

        BlockPos pos = user.getOnPos();
        world.playSound(user, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 1f, 1f);

        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return isSigned(stack);
    }

    @Override
    public void appendHoverText(
            ItemStack stack, @NonNull TooltipContext context, @NonNull TooltipDisplay tooltipDisplay,
            @NonNull Consumer<Component> tooltipAdder, @NonNull TooltipFlag flag
    ) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data != null) {
            CompoundTag tag = data.copyTag();
            readUuid(tag).ifPresent(uuid ->
                    tooltipAdder.accept(Component.literal("Signed by - " + tag.getString(NBTname)))
            );
        }
    }
}
