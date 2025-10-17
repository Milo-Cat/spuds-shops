package net.spudacious5705.shops.block.entity;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.spudacious5705.shops.block.ModBlockEntities;

public class RugShopEntity extends AbstractShopEntity {

    public long lastNanoTime;

    public RugShopEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RUG_SHOP_ENTITY.get(), pos, state, -0.3f);
    }

    @OnlyIn(Dist.CLIENT)
    protected RugRenderData furtherData;
    @OnlyIn(Dist.CLIENT)
    public RugRenderData furtherData(){
        return furtherData;
    }
    @OnlyIn(Dist.CLIENT)
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
    public Direction getCachedFacingDirection() {
        return Direction.NORTH;
    }

    @Override
    public void serverTick(ServerLevel world, BlockPos pos, BlockState shopState) {
        if(decayTimer<0){
            if (world.random.nextFloat() < 0.05f) {
                world.sendParticles(ParticleTypes.ENCHANT, pos.getX() + 0.325f + world.random.nextFloat()*0.35f, pos.getY() + 0.2f + world.random.nextFloat()*0.2f, pos.getZ() + 0.325f + world.random.nextFloat()*0.35f, 1, 0, 0, 0, -0.2);
            }
        }
        super.serverTick(world, pos, shopState);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected void createRendererData(){
        this.rendererData = new RendererData(shopInventory);
        this.furtherData = new RugRenderData();
    }

    public int getTextureId() {
        return 0;
    }
}
