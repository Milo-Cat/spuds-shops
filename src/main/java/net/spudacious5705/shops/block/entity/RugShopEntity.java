package net.spudacious5705.shops.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.spudacious5705.shops.block.custom.AbstractShopBlock;
import org.jetbrains.annotations.Nullable;

public class RugShopEntity extends AbstractShopEntity {

    public long lastNanoTime;

    public RugShopEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RUG_SHOP_ENTITY, pos, state, -0.3f);
    }

    @Environment(EnvType.CLIENT)
    protected RugRenderData furtherData;
    @Environment(EnvType.CLIENT)
    public RugRenderData furtherData(){
        return furtherData;
    }
    @Environment(EnvType.CLIENT)
    public static class RugRenderData {
        public float itemRotationY;
        public float itemRotationX;
        public float itemRotationZ;
        public float itemRotationSpeedZ;
        public float itemHeight;
        public final boolean rotateDirectionY;
        public final boolean rotateDirectionX;

        public RugRenderData() {
            this.itemRotationY = (float) (Math.random() * 360);
            this.itemRotationX = (float) (Math.random() * 360);
            this.itemRotationZ = (float) (Math.random() * 360);
            this.itemRotationSpeedZ = (float) (Math.random() * 360);
            this.itemHeight = (float) (Math.random() * Math.PI * 2);
            this.rotateDirectionY = Math.random() > 0.5f;
            this.rotateDirectionX = Math.random() > 0.5f;
        }
    }

    @Override
    public @Nullable Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }


    @Override
    public Direction getCachedFacingDirection() {
        return Direction.NORTH;
    }

    @Override
    public void serverTick(ServerWorld world, BlockPos pos, AbstractShopBlock.AbstractShopBlockState shopState) {
        if(decayTimer<0){
            if (world.random.nextFloat() < 0.05f) {
                world.spawnParticles(ParticleTypes.ENCHANT, pos.getX() + 0.325f + world.random.nextFloat()*0.35f, pos.getY() + 0.2f + world.random.nextFloat()*0.2f, pos.getZ() + 0.325f + world.random.nextFloat()*0.35f, 1, 0, 0, 0, -0.2);
            }
        }
        super.serverTick(world, pos, shopState);
    }

    @Override
    @Environment(EnvType.CLIENT)
    protected void createRendererData(){
        this.rendererData = new RendererData(shopInventory);
        this.furtherData = new RugRenderData();
    }
}
