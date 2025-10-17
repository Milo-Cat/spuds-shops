package net.spudacious5705.shops.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.spudacious5705.shops.block.entity.WindowSillShopEntity;

public class WindowSillShopEntityRenderer implements BlockEntityRenderer<WindowSillShopEntity>, ShopRenderUtils {

    private final BlockEntityRendererProvider.Context context;

    public WindowSillShopEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        this.context = ctx;
    }

    @Override
    public void render(WindowSillShopEntity shop, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        ItemDisplayContext mode;
        final WindowSillShopEntity.RendererData data = shop.rendererData();
        Font font = this.context.getFont();
        if(data == null){
            return;
        }
        data.frameAccumulator();

        if (data.shopFunctional()) {

            //render item being sold
            matrices.pushPose();
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

            matrices.mulPose(Axis.YP.rotationDegrees(data.rotation()));

            if (data.stockDisplayType()) {
                matrices.scale(0.5f, 0.5f, 0.5f);
                mode = ItemDisplayContext.NONE;
            } else {
                matrices.scale(0.8f, 0.8f, 0.8f);
                matrices.mulPose(Axis.XP.rotationDegrees(-90f));
                matrices.translate(0f, 0f, -0.3f);
                mode = ItemDisplayContext.GUI;
            }



            this.context.getItemRenderer().render(
                    data.displayItem(),
                    mode,
                    false,
                    matrices,
                    vertexConsumers,
                    light,
                    overlay,
                    context.getItemRenderer().getModel(data.displayItem(), null, null, 0)
            );
            matrices.popPose();

            //render price (count of currency)
            matrices.pushPose();

            matrices.translate(0.5f,0.0f,0.5f);
            matrices.mulPose(Axis.YP.rotationDegrees(
                            switch (data.direction()) {
                                case EAST -> 270f;
                                case SOUTH -> 180f;
                                case WEST -> 90f;
                                default -> 0f;
                            }));
            matrices.translate(-0.5f,0.0f,-0.5f);

            matrices.translate(0.30125f, 0.22f, 0.9167f);

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

            matrices.translate(0.5f,0.0f,0.5f);
            matrices.mulPose(Axis.YP.rotationDegrees(
                            switch (data.direction()) {
                                case EAST -> 270f;
                                case SOUTH -> 180f;
                                case WEST -> 90f;
                                default -> 0f;
                            }));
            matrices.translate(-0.5f,0.0f,-0.5f);

            matrices.mulPose(Axis.ZP.rotationDegrees(180.0f));
            matrices.mulPose(Axis.XP.rotationDegrees(-90f));

            matrices.translate(-0.15f, -0.126f, -0.15f);

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
                    0x000000,
                    light
            );
            matrices.popPose();


            //render currency type
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

            matrices.translate(0.13125f, 0.22f, 0.9167f);

            matrices.mulPose(Axis.ZP.rotationDegrees(180.0f));
            matrices.mulPose(Axis.XP.rotationDegrees(157.5f));

            matrices.scale(0.18f, 0.18f, 0.18f);
            
            this.context.getItemRenderer().render(data.paymentType(), ItemDisplayContext.GUI,
                    false,
                    matrices,
                    vertexConsumers,
                    light,
                    overlay,
                    context.getItemRenderer().getModel(data.displayItem(), null, null, 0)
            );
            matrices.popPose();

            ShopRenderUtils.renderShopWarns(tickDelta, matrices, vertexConsumers, light, overlay, data, context, -0.5f);
        }
    }
}
