package net.spudacious5705.shops.block.entity.renderer;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.spudacious5705.shops.block.entity.AbstractShopEntity;
import net.spudacious5705.shops.block.entity.WindowSillShopEntity;

public class WindowSillShopEntityRenderer implements BlockEntityRenderer<WindowSillShopEntity>, ShopRenderUtils {

    private final BlockEntityRendererFactory.Context context;

    public WindowSillShopEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.context = ctx;
    }

    @Override
    public void render(WindowSillShopEntity shop, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ModelTransformationMode mode;
        final WindowSillShopEntity.RendererData data = shop.rendererData();
        if(data == null){
            return;
        }
        data.frameAccumulator();

        if (data.shopFunctional()) {

            //render item being sold
            matrices.push();
            if (data.direction() == Direction.NORTH) {
                matrices.translate(0.6f, 0.4f, 0.45f);
            }
            if (data.direction() == Direction.EAST) {
                matrices.translate(0.55f, 0.4f, 0.6f);
            }
            if (data.direction() == Direction.SOUTH) {
                matrices.translate(0.4f, 0.4f, 0.55f);
            }
            if (data.direction() == Direction.WEST) {
                matrices.translate(0.45f, 0.4f, 0.4f);
            }

            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(data.rotation()));

            if (data.stockDisplayType()) {
                matrices.scale(0.5f, 0.5f, 0.5f);
                mode = ModelTransformationMode.NONE;
            } else {
                matrices.scale(0.8f, 0.8f, 0.8f);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90f));
                matrices.translate(0f, 0f, -0.3f);
                mode = ModelTransformationMode.GUI;
            }



            this.context.getItemRenderer().renderItem(data.displayItem(), mode, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, data.world(), 1);
            matrices.pop();

            //render price (count of currency)
            matrices.push();

            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(
                            switch (data.direction()) {
                                case EAST -> 270f;
                                case SOUTH -> 180f;
                                case WEST -> 90f;
                                default -> 0f;
                            }),
                    0.5f, 0f, 0.5f);

            matrices.translate(0.30125f, 0.22f, 0.9167f);

            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0f));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-22.5f));

            matrices.scale(0.018f, 0.018f, 0.018f);

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


            //render amount being sold
            matrices.push();

            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(
                            switch (data.direction()) {
                                case EAST -> 270f;
                                case SOUTH -> 180f;
                                case WEST -> 90f;
                                default -> 0f;
                            }),
                    0.5f, 0f, 0.5f);

            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0f));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90f));

            matrices.translate(-0.15f, -0.126f, -0.15f);

            matrices.scale(0.025f, 0.025f, 0.025f);

            this.context.getTextRenderer().draw(
                    data.stockQuantity,
                    data.qWidth(),
                    -4f,
                    0xffff00,
                    false,
                    matrices.peek().getPositionMatrix(),
                    vertexConsumers,
                    TextRenderer.TextLayerType.NORMAL,
                    0x000000,
                    light
            );
            matrices.pop();


            //render currency type
            matrices.push();

            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(
                            switch (data.direction()) {
                                case EAST -> 270f;
                                case SOUTH -> 180f;
                                case WEST -> 90f;
                                default -> 0f;
                            }),
                    0.5f, 0f, 0.5f);

            matrices.translate(0.13125f, 0.22f, 0.9167f);

            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0f));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(157.5f));

            matrices.scale(0.18f, 0.18f, 0.18f);
            
            this.context.getItemRenderer().renderItem(data.paymentType(), ModelTransformationMode.GUI, light, overlay, matrices, vertexConsumers, data.world(), 1);
            matrices.pop();

            ShopRenderUtils.renderShopWarns(tickDelta, matrices, vertexConsumers, light, overlay, data, context, -0.5f);
        }
    }
}
