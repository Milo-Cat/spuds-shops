package net.spudacious5705.shops.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.spudacious5705.shops.block.entity.HookShopEntity;

public class HookShopEntityRenderer implements BlockEntityRenderer<HookShopEntity>, ShopRenderUtils {

    private final BlockEntityRendererProvider.Context context;

    public HookShopEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        this.context = ctx;
    }

    @Override
    public void render(HookShopEntity shop, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        ItemDisplayContext mode;
        final HookShopEntity.RendererData data = shop.rendererData();
        Font font = this.context.getFont();
        if(data == null){
            return;
        }
        data.frameAccumulator();

        if (data.shopFunctional()) {


            //global rotation and translation
            matrices.pushPose();
            matrices.translate(0.5f, 0.5f, 0.5f);
            matrices.mulPose(Axis.YP.rotationDegrees(
                            switch (data.direction()) {
                                case EAST -> 270f;
                                case SOUTH -> 180f;
                                case WEST -> 90f;
                                default -> 0f;
                            }));//used to have centre 0,0,0


            //render item being sold
            matrices.pushPose();


            if (data.stockDisplayType()) {
                matrices.translate(0f, -0.25f, 0f);
                matrices.mulPose(Axis.YP.rotationDegrees(180.0f));
                matrices.scale(0.5f, 0.5f, 0.5f);
                mode = ItemDisplayContext.NONE;
            } else {
                matrices.translate(0f, -0.5f, 0f);
                matrices.scale(0.7f, 0.7f, 0.7f);
                matrices.mulPose(Axis.ZP.rotationDegrees(45.0f));
                mode = ItemDisplayContext.GUI;
            }



            this.context.getItemRenderer().render(data.displayItem(), mode,
                    false,
                    matrices,
                    vertexConsumers,
                    light,
                    overlay,
                    this.context.getItemRenderer().getModel(data.displayItem(), null, null, 0)
            );
            matrices.popPose();

            matrices.pushPose();

            //render price (count of currency)
            matrices.translate(0.05f, 0.18f, -0.03126f);
            matrices.mulPose(Axis.ZP.rotationDegrees(180.0f));

            matrices.pushPose();
            matrices.scale(0.016f, 0.016f, -0.016f);
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

            matrices.pushPose();
            matrices.translate(0.05f, 0f, 0.03126f);
            matrices.mulPose(Axis.YP.rotationDegrees(180f));
            matrices.translate(-0.05f, 0f, -0.03126f);
            matrices.scale(0.016f, 0.016f, -0.016f);
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
            matrices.popPose();


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
            matrices.mulPose(Axis.YP.rotationDegrees(180f));//was rotated around 0,0,0
            matrices.pushPose();
            matrices.translate(-0.12f, 0f, r);
            matrices.mulPose(Axis.YP.rotationDegrees(180f));
            matrices.translate(0.12f, 0f, -r);

            matrices.scale(scaleFactor, scaleFactor, scaleFactor);
            this.context.getItemRenderer().render(data.paymentType(), ItemDisplayContext.GUI,
                    false,
                    matrices,
                    vertexConsumers,
                    light,
                    overlay,
                    this.context.getItemRenderer().getModel(data.displayItem(), null, null, 0)
            );
            matrices.popPose();
            matrices.pushPose();
            matrices.scale(scaleFactor, scaleFactor, scaleFactor);
            this.context.getItemRenderer().render(data.paymentType(), ItemDisplayContext.GUI,                    false,
                    matrices,
                    vertexConsumers,
                    light,
                    overlay,
                    this.context.getItemRenderer().getModel(data.displayItem(), null, null, 0)
            );
            matrices.popPose();

            matrices.popPose();
            ShopRenderUtils.renderShopWarns(tickDelta, matrices, vertexConsumers, light, overlay, data, context, -2.1f);
        }
    }
}
