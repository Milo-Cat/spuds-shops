package net.spudacious5705.shops.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.spudacious5705.shops.screen.ModScreenHandlers;
import org.jetbrains.annotations.Nullable;

public class HookShopEntity extends AbstractShopEntity{

    public HookShopEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HOOK_SHOP_ENTITY, pos, state, -2.1f);
    }

    @Override
    public int getTextureId() {
        return 0;
    }

    @Override
    public @Nullable Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
}
