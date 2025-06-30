package net.spudacious5705.shops.block.entity.renderer;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import net.spudacious5705.shops.block.entity.AbstractShopEntity;
import net.spudacious5705.shops.block.entity.HookShopEntity;

public class HookShopEntityRenderer implements BlockEntityRenderer<HookShopEntity>, ShopRenderUtils {

    private final BlockEntityRendererFactory.Context context;

    public HookShopEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.context = ctx;
    }

    @Override
    public void render(HookShopEntity shop, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ModelTransformationMode mode;
        final HookShopEntity.RendererData data = shop.rendererData();
        if(data == null){
            return;
        }
        data.frameAccumulator();

        if (data.shopFunctional()) {


            //global rotation and translation
            matrices.push();
            matrices.translate(0.5f, 0.5f, 0.5f);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(
                            switch (data.direction()) {
                                case EAST -> 270f;
                                case SOUTH -> 180f;
                                case WEST -> 90f;
                                default -> 0f;
                            }),
                    0f, 0f, 0f);


            //render item being sold
            matrices.push();


            if (data.stockDisplayType()) {
                matrices.translate(0f, -0.25f, 0f);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f));
                matrices.scale(0.5f, 0.5f, 0.5f);
                mode = ModelTransformationMode.NONE;
            } else {
                matrices.translate(0f, -0.5f, 0f);
                matrices.scale(0.7f, 0.7f, 0.7f);
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(45.0f));
                mode = ModelTransformationMode.GUI;
            }



            this.context.getItemRenderer().renderItem(data.displayItem(), mode, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, data.world(), 1);
            matrices.pop();

            matrices.push();

            //render price (count of currency)
            matrices.translate(0.05f, 0.18f, -0.03126f);
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0f));

            matrices.push();
            matrices.scale(0.016f, 0.016f, -0.016f);
            this.context.getTextRenderer().draw(
                    data.text(),
                    data.width(),
                    -4f,
                    0xffffff,
                    false,
                    matrices.peek().getPositionMatrix(),
                    vertexConsumers,
                    TextRenderer.TextLayerType.NORMAL,
                    0,
                    light
            );
            matrices.pop();

            matrices.push();
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180f),0.05f, 0f, 0.03126f);
            matrices.scale(0.016f, 0.016f, -0.016f);
            this.context.getTextRenderer().draw(
                    data.text(),
                    data.width(),
                    -4f,
                    0xffffff,
                    false,
                    matrices.peek().getPositionMatrix(),
                    vertexConsumers,
                    TextRenderer.TextLayerType.NORMAL,
                    0,
                    light
            );
            matrices.pop();
            matrices.pop();


            //render currency type
            float r;
            float scaleFactor;
            if(data.currencyDisplayType()){
                //a block is being rendered
                scaleFactor = 0.16f;
                r = -0.06f;
            } else {
                //an item is being rendered
                scaleFactor = 0.22f;
                r = -0.04f;
            }
            matrices.translate(-0.12f, 0.2f, r);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180f),0f, 0f, 0f);
            matrices.push();
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180f),-0.12f, 0f, r);

            matrices.scale(scaleFactor, scaleFactor, scaleFactor);
            this.context.getItemRenderer().renderItem(data.paymentType(), ModelTransformationMode.GUI, light, overlay, matrices, vertexConsumers, data.world(), 1);
            matrices.pop();
            matrices.push();
            matrices.scale(scaleFactor, scaleFactor, scaleFactor);
            this.context.getItemRenderer().renderItem(data.paymentType(), ModelTransformationMode.GUI, light, overlay, matrices, vertexConsumers, data.world(), 1);
            matrices.pop();

            matrices.pop();
            ShopRenderUtils.renderShopWarns(tickDelta, matrices, vertexConsumers, light, overlay, data, context, -2.1f);
        }
    }
}
