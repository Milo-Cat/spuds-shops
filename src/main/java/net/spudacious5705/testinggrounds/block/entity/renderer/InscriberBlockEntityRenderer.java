package net.spudacious5705.testinggrounds.block.entity.renderer;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.spudacious5705.testinggrounds.block.entity.InscriberBlockEntity;
import net.spudacious5705.testinggrounds.block.entity.ShopEntity;

public class InscriberBlockEntityRenderer implements BlockEntityRenderer<InscriberBlockEntity> {
    private final ItemRenderer rend;
    private final BlockEntityRendererFactory.Context context;
    public InscriberBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.rend = ctx.getItemRenderer();
        this.context = ctx;
    }

    @Override
    public void render(InscriberBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        ItemStack s = new ItemStack(Items.APPLE,1);
        matrices.push();
        matrices.translate(0.5f, 0.8f, 0.5f);
        matrices.scale(0.5f, 0.5f, 0.5f);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(270));

        World world = entity.getWorld();
        BlockPos pos = entity.getPos();

        this.rend.renderItem(s, ModelTransformationMode.GROUND, 16, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, entity.getWorld(), 0);
        matrices.pop();
    }

    private int getLightLevel(World world, BlockPos pos) {
        int bLight = world.getLightLevel(LightType.BLOCK, pos);
        int sLight = world.getLightLevel(LightType.SKY, pos);
        return LightmapTextureManager.pack(bLight, sLight);
    }


}
