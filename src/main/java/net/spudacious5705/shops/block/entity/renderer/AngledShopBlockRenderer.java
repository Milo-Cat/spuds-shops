package net.spudacious5705.shops.block.entity.renderer;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.spudacious5705.shops.block.entity.AbstractShopEntity;
import net.spudacious5705.shops.block.entity.AngledShopEntity;
import net.spudacious5705.shops.util.CushionModel;

public class AngledShopBlockRenderer implements BlockEntityRenderer<AngledShopEntity> {

    private final BlockEntityRendererFactory.Context context;

    private final CushionModel model;

    public AngledShopBlockRenderer(BlockEntityRendererFactory.Context ctx) {
        this.context = ctx;
        model = new CushionModel(ctx.getLayerModelPart(CushionModel.LAYER_LOCATION));
    }

    @Override
    public void render(AngledShopEntity shop, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ModelTransformationMode mode;
        final AngledShopEntity.RendererData data = shop.rendererData();

        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(
                        switch (shop.getCachedFacingDirection()) {
                            case EAST -> 270f;
                            case SOUTH -> 180f;
                            case WEST -> 90f;
                            default -> 0f;
                        }),
                0.5f,0f,0.5f);

        this.model.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(shop.getCushionTextureID())), light, overlay);
        matrices.pop();

        if(data == null){
            return;
        }
        data.frameAccumulator();
        if (data.shopFunctional()) {

            //render item being sold
            matrices.push();
            matrices.translate(0.5f, 0.955f, 0.5f);

            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(data.rotation()));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-67.5f));

            if (data.stockDisplayType()) {
                matrices.scale(0.3f, 0.3f, 0.3f);
                mode = ModelTransformationMode.NONE;
            } else {
                matrices.scale(0.4f, 0.4f, 0.4f);
                mode = ModelTransformationMode.GUI;
            }

            this.context.getItemRenderer().renderItem(data.displayItem(), mode, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, data.world(), 1);
            matrices.pop();

            //render price (count of currency)
            matrices.push();



            if(data.direction() == Direction.NORTH){
                matrices.translate(0.57f, 0.514375f, 0.0525f);
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0f));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-22.5f));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(0.0f));
            }
            if(data.direction() == Direction.EAST){
                matrices.translate(0.9475f, 0.514375f, 0.57f);
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0f));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0f));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-22.5f));

            }
            if(data.direction() == Direction.SOUTH){
                matrices.translate(0.43f, 0.514375f, 0.9475f);
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0f));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-22.5f));
            }
            if(data.direction() == Direction.WEST){
                matrices.translate(0.0525f, 0.514375f, .43f);
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0f));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(270.0f));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-22.5f));
            }


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


            matrices.translate(0.3f, 1.05f, .25f);

            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(
                            switch (data.direction()) {
                                case EAST -> 270f;
                                case SOUTH -> 180f;
                                case WEST -> 90f;
                                default -> 0f;
                            }),
                    0.2f, 0f, 0.25f);

            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0f));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-67.5f));


            matrices.scale(0.018f, 0.018f, 0.018f);

            this.context.getTextRenderer().draw(
                    data.stockQuantity,
                    data.qWidth(),
                    -4f,
                    0xffff00,
                    false,
                    matrices.peek().getPositionMatrix(),
                    vertexConsumers,
                    TextRenderer.TextLayerType.NORMAL,
                    0xffffff,
                    light
            );
            matrices.pop();


            //render currency type
            matrices.push();
            if(data.direction() == Direction.NORTH){
                matrices.translate(0.385f, 0.535f, 0.0525f);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-22.5f));
            }
            if(data.direction() == Direction.EAST){
                matrices.translate(0.9475f, 0.535f,0.385f );
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0f));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-22.5f));

            }
            if(data.direction() == Direction.SOUTH){
                matrices.translate(0.615f, 0.535f, 0.9475);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-22.5f));
            }
            if(data.direction() == Direction.WEST){
                matrices.translate(0.0525f, 0.535f,0.615f );
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(270.0f));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-22.5f));
            }
            matrices.scale(0.18f, 0.18f, 0.18f);
            this.context.getItemRenderer().renderItem(data.paymentType(), ModelTransformationMode.GUI, light, overlay, matrices, vertexConsumers, data.world(), 1);
            matrices.pop();

            ShopRenderUtils.renderShopWarns(tickDelta,matrices,vertexConsumers,light,overlay,data,context,0.375f);


        }
    }
}