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
import net.spudacious5705.shops.block.entity.CrateShopEntity;

public class CrateShopEntityRenderer implements BlockEntityRenderer<CrateShopEntity>, ShopRenderUtils {

    private final BlockEntityRendererFactory.Context context;

    public CrateShopEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.context = ctx;
    }

    record TranslationFactor(float x, float y, float z, float ry, float rx){}

    private static final TranslationFactor[] DISPLAY_TRANSLATIONS_BLOCK = {
            new TranslationFactor(0.02f,0.2f,0.33f,-8f,0f),
            new TranslationFactor(-0.23f,0.53f,0.26f,-4f,-4f),
            new TranslationFactor(0.02f,0.4f,0.28f,175f,5f),
            new TranslationFactor(0.08f,0.6f,0.28f,-175f,5f),
            new TranslationFactor(0.23f,0.7f,0.22f,17f,-8f),
            new TranslationFactor(-0.25f,0.34f,0.29f,-170f,0f),
            new TranslationFactor(-0.23f,0.7f,0.24f,170f,187f),
            new TranslationFactor(-0.28f,0.2f,0.33f,-150f,10f),
            new TranslationFactor(0.24f,0.22f,0.35f,165f,0f),
            new TranslationFactor(0.25f,0.45f,0.33f,-10f,-8f),
            new TranslationFactor(0.23f,0.79f,0.23f,170f,-170f),
            new TranslationFactor(-0.25f,0.79f,0.18f,-30f,10f),
            new TranslationFactor(-0.04f,0.79f,0.22f,45f,18f)
    };
    private static final TranslationFactor[] DISPLAY_TRANSLATIONS_ITEM = {
            new TranslationFactor(0.02f,0.2f,0.33f,-8f,0f),
            new TranslationFactor(-0.23f,0.53f,0.26f,-4f,-4f),
            new TranslationFactor(0.02f,0.4f,0.28f,175f,5f),
            new TranslationFactor(0.08f,0.6f,0.28f,-175f,5f),
            new TranslationFactor(0.23f,0.7f,0.22f,17f,-8f),
            new TranslationFactor(-0.25f,0.34f,0.29f,-170f,0f),
            new TranslationFactor(-0.23f,0.7f,0.24f,170f,187f),
            new TranslationFactor(-0.24f,0.18f,0.24f,-170f,-16f),
            new TranslationFactor(0.24f,0.16f,0.3f,165f,0f),
            new TranslationFactor(0.22f,0.45f,0.3f,-10f,-8f),
            new TranslationFactor(0.23f,0.72f,0.19f,170f,-170f),
            new TranslationFactor(-0.04f,0.77f,0.26f,0f,-14f)
    };

    @Override
    public void render(CrateShopEntity shop, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ModelTransformationMode mode;
        final CrateShopEntity.RendererData data = shop.rendererData();
        if(data == null){
            return;
        }
        data.frameAccumulator();

        if (data.shopFunctional()) {

            //global translation
            matrices.push();
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(
                            switch (data.direction()) {
                                case EAST -> 270f;
                                case SOUTH -> 180f;
                                case WEST -> 90f;
                                default -> 0f;
                            }),
                    0.5f, 0f, 0.5f);
            matrices.translate(0.5f, 0.0f, 0.5f);


            //render item being sold


            TranslationFactor[] DISPLAY_TRANSLATIONS;

            matrices.push();
            float scaleFactorX, scaleFactorY, scaleFactorZ;
            float rotationXbonus;
            if (data.stockDisplayType()) {
                scaleFactorX = 0.4f;
                scaleFactorY = 0.31667f;
                scaleFactorZ = 0.4f;
                mode = ModelTransformationMode.GUI;
                matrices.translate(0f, -0.18f, -0.08f);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(40f),0f,0.3f,0f);
                rotationXbonus = -40f;
                matrices.scale(0.95f, 1.2f, 0.95f);
                DISPLAY_TRANSLATIONS = DISPLAY_TRANSLATIONS_BLOCK;
            } else {
                scaleFactorX = 0.5f;
                scaleFactorY = 0.5f;
                scaleFactorZ = 0.8f;
                mode = ModelTransformationMode.GUI;
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(40f),0f,0.3f,0f);
                rotationXbonus = 0f;
                DISPLAY_TRANSLATIONS = DISPLAY_TRANSLATIONS_ITEM;
            }


            for(TranslationFactor i : DISPLAY_TRANSLATIONS){
                matrices.push();
                matrices.translate(i.x, i.y, -i.z);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(i.ry));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(i.rx+rotationXbonus));
                matrices.scale(scaleFactorX, scaleFactorY, scaleFactorZ);
                this.context.getItemRenderer().renderItem(data.displayItem(), mode, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, data.world(), 1);
                matrices.pop();
            }

            matrices.pop();

            //render price (count of currency)
            matrices.push();

            matrices.translate(0.06f,0.14f,-0.664);

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

            matrices.translate(-0.03f,0.3f,-0.5975);

            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0f));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-22.5f));

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

            matrices.translate(-0.12f,0.14f,-0.664);

            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0f));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(157.5f));

            matrices.scale(0.18f, 0.18f, 0.18f);
            
            this.context.getItemRenderer().renderItem(data.paymentType(), ModelTransformationMode.GUI, light, overlay, matrices, vertexConsumers, data.world(), 1);
            matrices.pop();
            matrices.pop();

            ShopRenderUtils.renderShopWarns(tickDelta, matrices, vertexConsumers, light, overlay, data, context, 0f);
        }
    }
}
