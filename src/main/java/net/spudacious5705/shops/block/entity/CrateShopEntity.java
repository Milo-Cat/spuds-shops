package net.spudacious5705.shops.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class CrateShopEntity extends AbstractShopEntity{

    public CrateShopEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CRATE_SHOP_ENTITY, pos, state, 0f);
    }

    @Override
    public @Nullable Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
}
