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
import net.spudacious5705.shops.block.entity.ShelfShopEntity;

public class ShelfShopEntityRenderer implements BlockEntityRenderer<ShelfShopEntity>, ShopRenderUtils {

    private final BlockEntityRendererFactory.Context context;

    public ShelfShopEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.context = ctx;
    }


    @Override
    public void render(ShelfShopEntity shop, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {

        final ShelfShopEntity.RendererData data1 = shop.rendererData();
        final ShelfShopEntity.RendererData data2 = shop.rendererDataTop();

        //global rotation and translation
        matrices.push();
        matrices.translate(0.5f, 0.5f, 0.5f);

        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(
                        switch (shop.getCachedFacingDirection()) {
                            case EAST -> 270f;
                            case SOUTH -> 180f;
                            case WEST -> 90f;
                            default -> 0f;
                        }),
                0f, 0f, 0f);

        //render bottom
        renderShelf(data1, tickDelta, matrices, vertexConsumers, light, overlay, shop.furtherDataBottom());

        //render top
        matrices.translate(0f, 0.44f, 0f);
        renderShelf(data2, tickDelta, matrices, vertexConsumers, light, overlay, shop.furtherDataTop());

        matrices.pop();

        ShopRenderUtils.renderShopWarns(tickDelta, matrices, vertexConsumers, light, overlay, data1, context, -1.05f, 0.3f);

        ShopRenderUtils.renderShopWarns(tickDelta, matrices, vertexConsumers, light, overlay, data2, context, -0.6f, 0.3f);

    }

    private void renderShelf(ShelfShopEntity.RendererData data, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, ShelfShopEntity.ShelfRenderData furtherData){
        ModelTransformationMode mode;
        float itemTranslationFactor;
        RotationAxis rotationAxis;
        int renderCount;
        if(data != null){
            data.frameAccumulator();
            if (data.shopFunctional()) {


                //render item being sold
                matrices.push();
                matrices.translate(0f, -0.216f, 0.3f);
                if (data.stockDisplayType()) {
                    //matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f));
                    matrices.scale(0.2f, 0.2f, 0.2f);
                    mode = ModelTransformationMode.NONE;
                    itemTranslationFactor = 1.15f;
                    rotationAxis = RotationAxis.POSITIVE_Y;
                    renderCount=1;
                } else {
                    matrices.translate(0f, -0.08f, 0f);
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0f));
                    matrices.scale(0.4f, 0.4f, 0.4f);
                    mode = ModelTransformationMode.GUI;
                    itemTranslationFactor = 0.5f;
                    rotationAxis = RotationAxis.POSITIVE_Z;
                    renderCount=3;
                }

                for(int y = 0; y<renderCount; y++) {
                    //left
                    matrices.push();
                    matrices.translate(-itemTranslationFactor, 0f, -y*0.05f);
                    matrices.multiply(rotationAxis.rotationDegrees(furtherData.itemLrotation+(y+1)*55f));
                    this.context.getItemRenderer().renderItem(data.displayItem(), mode, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, data.world(), 1);
                    matrices.pop();

                    //right
                    matrices.push();
                    matrices.translate(itemTranslationFactor, 0f, -y*0.05f);
                    matrices.multiply(rotationAxis.rotationDegrees(furtherData.itemRrotation+(y+1)*55f));
                    this.context.getItemRenderer().renderItem(data.displayItem(), mode, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, data.world(), 1);
                    matrices.pop();
                }
                matrices.pop();


                if (data.stockDisplayType()) {
                    //for blocks
                    //render price (count of currency)


                    matrices.push();
                    matrices.translate(-0.02f, -0.3124f, 0.16f);
                    matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180f), 0f, 0f, 0f);
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90f), 0f, 0f, 0f);
                    matrices.scale(0.02f, 0.02f, -0.02f);
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
                    matrices.translate(-0.02f, -0.17f, 0.43749f);
                    matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180f), 0f, 0f, 0f);
                    matrices.scale(0.022f, 0.022f, -0.022f);
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
                    float scaleFactor;
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180f));
                    if (data.currencyDisplayType()) {
                        //a block is being rendered
                        scaleFactor = 0.16f;
                        matrices.translate(0f, -0.265f, -0.35f);
                        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-30f));
                    } else {
                        //an item is being rendered
                        scaleFactor = 0.35f;
                        matrices.translate(0f, -0.308f, -0.34f);
                        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90f));
                    }


                    matrices.scale(scaleFactor, scaleFactor, scaleFactor);
                    this.context.getItemRenderer().renderItem(data.paymentType(), ModelTransformationMode.GUI, light, overlay, matrices, vertexConsumers, data.world(), 1);
                    matrices.pop();
                } else {
                    //render price (count of currency)


                    matrices.push();
                    matrices.translate(-0.08f, -0.16f, 0.43749f);
                    matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180f), 0f, 0f, 0f);
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

                    //render amount being sold
                    matrices.push();
                    matrices.translate(0.2f, -0.16f, 0.43749f);
                    matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180f), 0f, 0f, 0f);
                    matrices.scale(0.016f, 0.016f, -0.016f);
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
                    matrices.translate(-0.245f, -0.16f, 0.43f);
                    float scaleFactor;
                    if (data.currencyDisplayType()) {
                        //a block is being rendered
                        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(20f));
                        scaleFactor = 0.16f;
                    } else {
                        //an item is being rendered
                        scaleFactor = 0.22f;
                    }

                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180f));

                    matrices.scale(scaleFactor, scaleFactor, scaleFactor);
                    this.context.getItemRenderer().renderItem(data.paymentType(), ModelTransformationMode.GUI, light, overlay, matrices, vertexConsumers, data.world(), 1);
                    matrices.pop();
                }
            }
        }
    }
}
