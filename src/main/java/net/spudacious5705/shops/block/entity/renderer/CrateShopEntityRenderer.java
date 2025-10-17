package net.spudacious5705.shops.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.spudacious5705.shops.block.entity.CrateShopEntity;

public class CrateShopEntityRenderer implements BlockEntityRenderer<CrateShopEntity>, ShopRenderUtils {

    private final BlockEntityRendererProvider.Context context;

    public CrateShopEntityRenderer(BlockEntityRendererProvider.Context ctx) {
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
    public void render(CrateShopEntity shop, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        ItemDisplayContext mode;
        final CrateShopEntity.RendererData data = shop.rendererData();
        Font font = this.context.getFont();
        if(data == null){
            return;
        }
        data.frameAccumulator();

        if (data.shopFunctional()) {

            //global translation
            matrices.pushPose();
            matrices.translate(0.5f, 0f, 0.5f);
            matrices.mulPose(Axis.YP.rotationDegrees(
                            switch (data.direction()) {
                                case EAST -> 270f;
                                case SOUTH -> 180f;
                                case WEST -> 90f;
                                default -> 0f;
                            }));
            matrices.translate(-0.5f, 0f, -0.5f);
            matrices.translate(0.5f, 0.0f, 0.5f);


            //render item being sold


            TranslationFactor[] DISPLAY_TRANSLATIONS;

            matrices.pushPose();
            float scaleFactorX, scaleFactorY, scaleFactorZ;
            float rotationXbonus;
            if (data.stockDisplayType()) {
                scaleFactorX = 0.4f;
                scaleFactorY = 0.31667f;
                scaleFactorZ = 0.4f;
                mode = ItemDisplayContext.GUI;
                matrices.translate(0f, -0.18f, -0.08f);
                matrices.translate(0f, 0.3f, 0f);
                matrices.mulPose(Axis.XP.rotationDegrees(40f));
                matrices.translate(0f, -0.3f, 0f);
                rotationXbonus = -40f;
                matrices.scale(0.95f, 1.2f, 0.95f);
                DISPLAY_TRANSLATIONS = DISPLAY_TRANSLATIONS_BLOCK;
            } else {
                scaleFactorX = 0.5f;
                scaleFactorY = 0.5f;
                scaleFactorZ = 0.8f;
                mode = ItemDisplayContext.GUI;
                matrices.translate(0f, 0.3f, 0f);
                matrices.mulPose(Axis.XP.rotationDegrees(40f));
                matrices.translate(0f, -0.3f, 0f);
                rotationXbonus = 0f;
                DISPLAY_TRANSLATIONS = DISPLAY_TRANSLATIONS_ITEM;
            }


            for(TranslationFactor i : DISPLAY_TRANSLATIONS){
                matrices.pushPose();
                matrices.translate(i.x, i.y, -i.z);
                matrices.mulPose(Axis.YP.rotationDegrees(i.ry));
                matrices.mulPose(Axis.XP.rotationDegrees(i.rx+rotationXbonus));
                matrices.scale(scaleFactorX, scaleFactorY, scaleFactorZ);
                this.context.getItemRenderer().render(data.displayItem(), mode,
                        false,
                        matrices,
                        vertexConsumers,
                        light,
                        overlay,
                        this.context.getItemRenderer().getModel(data.displayItem(), null, null, 0)
                );
                matrices.popPose();
            }

            matrices.popPose();

            //render price (count of currency)
            matrices.pushPose();

            matrices.translate(0.06f,0.14f,-0.664);

            matrices.mulPose(Axis.ZP.rotationDegrees(180.0f));
            matrices.mulPose(Axis.XP.rotationDegrees(-22.5f));

            matrices.scale(0.018f, 0.018f, 0.018f);

            font.drawInBatch(
                    data.text(),
                    data.width(),
                    -4f,
                    0xffffff,
                    false,
                    matrices.last().pose(),
                    vertexConsumers,
                    Font.DisplayMode.NORMAL,
                    0,
                    light
            );
            matrices.popPose();


            //render amount being sold
            matrices.pushPose();

            matrices.translate(-0.03f,0.3f,-0.5975);

            matrices.mulPose(Axis.ZP.rotationDegrees(180.0f));
            matrices.mulPose(Axis.XP.rotationDegrees(-22.5f));

            matrices.scale(0.025f, 0.025f, 0.025f);

            font.drawInBatch(
                    data.stockQuantity,
                    data.qWidth(),
                    -4f,
                    0xffff00,
                    false,
                    matrices.last().pose(),
                    vertexConsumers,
                    Font.DisplayMode.NORMAL,
                    0,
                    light
            );
            matrices.popPose();


            //render currency type
            matrices.pushPose();

            matrices.translate(-0.12f,0.14f,-0.664);

            matrices.mulPose(Axis.ZP.rotationDegrees(180.0f));
            matrices.mulPose(Axis.XP.rotationDegrees(157.5f));

            matrices.scale(0.18f, 0.18f, 0.18f);
            
            this.context.getItemRenderer().render(data.paymentType(),
                    ItemDisplayContext.GUI,
                    false,
                    matrices,
                    vertexConsumers,
                    light,
                    overlay,
                    this.context.getItemRenderer().getModel(data.displayItem(), null, null, 0)
            );
            matrices.popPose();
            matrices.popPose();

            ShopRenderUtils.renderShopWarns(tickDelta, matrices, vertexConsumers, light, overlay, data, context, 0f);
        }
    }
}
