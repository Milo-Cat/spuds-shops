package net.spudacious5705.testinggrounds.block.entity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.spudacious5705.testinggrounds.screen.StationScreenHandler;
import net.spudacious5705.testinggrounds.screen.StorageScreenHandler;
import org.jetbrains.annotations.Nullable;

public class StorageBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory {


    public StorageBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.STORAGE_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
        packetByteBuf.writeBlockPos(this.pos);
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Storage");
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new StorageScreenHandler(syncId, playerInventory, this);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return null;
    }
}
