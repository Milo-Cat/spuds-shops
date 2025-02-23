package net.spudacious5705.testinggrounds.block.entity.renderer;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
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
import net.minecraft.util.Colors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.spudacious5705.testinggrounds.block.entity.ShopEntity;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class ShopBlockEntityRenderer implements BlockEntityRenderer<ShopEntity> {

    private final BlockEntityRendererFactory.Context context;

    public ShopBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.context = ctx;
    }

    @Override
    public void render(ShopEntity shop, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        ItemStack stack = new ItemStack(shop.getPaymentType());
        stack = new ItemStack(Items.SAND);

        if (!stack.isEmpty()) {
            int rotation = getRotation(shop);

            matrices.push();
            matrices.translate(0.5f, 0.55f, 0.43f);
            matrices.scale(0.8f, 0.8f, 0.8f);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(67.5f));

            Matrix4f matx = new Matrix4f();

            this.context.getItemRenderer().renderItem(stack, ModelTransformationMode.GROUND, getLightLevel(shop.getWorld(), shop.getPos()), OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, shop.getWorld(), 1);
            //this.context.getTextRenderer().draw("64", 0.0f,0.0f, Colors.BLACK, true, matx, vertexConsumers , TextRenderer.TextLayerType.NORMAL, 0,16);
            matrices.pop();
        }
    }

    private int getLightLevel(World world, BlockPos pos) {
        int bLight = world.getLightLevel(LightType.BLOCK, pos);
        int sLight = world.getLightLevel(LightType.SKY, pos);
        return LightmapTextureManager.pack(bLight, sLight);
    }

    private int getRotation(ShopEntity shop){
        switch (shop.getFacingDirection()) {
            default:
                return 0;
            case EAST:
                return 90;
            case SOUTH:
                return 180;
            case WEST:
                return 270;
        }
    }
}
