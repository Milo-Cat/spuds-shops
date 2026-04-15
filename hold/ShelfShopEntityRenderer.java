package net.spudacious5705.shops.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.spudacious5705.shops.block.entity.ShelfShopEntity;

public class ShelfShopEntityRenderer implements BlockEntityRenderer<ShelfShopEntity>, ShopRenderUtils {

    private final BlockEntityRendererProvider.Context context;

    public ShelfShopEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        this.context = ctx;
    }

    @Override
    public void render(ShelfShopEntity shop, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        final ShelfShopEntity.RendererData data1 = shop.rendererData();
        final ShelfShopEntity.RendererData data2 = shop.rendererDataTop();

        // Center the render origin on the block and rotate to the correct facing.
        matrices.pushPose();
        matrices.translate(0.5f, 0.5f, 0.5f);
        matrices.mulPose(
                Axis.YP.rotationDegrees(
                        switch (shop.getCachedFacingDirection()) {
                            case EAST -> 270f;
                            case SOUTH -> 180f;
                            case WEST -> 90f;
                            default -> 0f;
                        }
                )
        );

        // Render bottom shelf section.
        renderShelf(data1, tickDelta, matrices, vertexConsumers, light, overlay, shop.furtherDataBottom());

        // Render top shelf section above the bottom shelf.
        matrices.translate(0f, 0.44f, 0f);
        renderShelf(data2, tickDelta, matrices, vertexConsumers, light, overlay, shop.furtherDataTop());

        matrices.popPose();

        // Render any warning indicators for each shelf.
        ShopRenderUtils.renderShopWarns(tickDelta, matrices, vertexConsumers, light, overlay, data1, context, -1.05f, 0.3f);
        ShopRenderUtils.renderShopWarns(tickDelta, matrices, vertexConsumers, light, overlay, data2, context, -0.6f, 0.3f);
    }

    private void renderShelf(ShelfShopEntity.RendererData data, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay, ShelfShopEntity.ShelfRenderData furtherData) {
        ItemDisplayContext mode;
        float itemTranslationFactor;
        Axis rotationAxis;
        int renderCount;
        Font font = this.context.getFont();

        if (data == null) {
            return;
        }

        data.frameAccumulator();
        if (!data.shopFunctional()) {
            return;
        }

        // Render the shop item stack in front of the shelf.
        matrices.pushPose();
        matrices.translate(0f, -0.216f, 0.3f);

        if (data.stockDisplayType()) {
            // Render block-like stock in a small centered display.
            matrices.scale(0.2f, 0.2f, 0.2f);
            mode = ItemDisplayContext.NONE;
            itemTranslationFactor = 1.15f;
            rotationAxis = Axis.YP;
            renderCount = 1;
        } else {
            // Render item-like stock as a larger, tilted display.
            matrices.translate(0f, -0.08f, 0f);
            matrices.mulPose(Axis.XP.rotationDegrees(90.0f));
            matrices.scale(0.4f, 0.4f, 0.4f);
            mode = ItemDisplayContext.GUI;
            itemTranslationFactor = 0.5f;
            rotationAxis = Axis.ZP;
            renderCount = 3;
        }

        for (int y = 0; y < renderCount; y++) {
            // Render left stock item.
            matrices.pushPose();
            matrices.translate(-itemTranslationFactor, 0f, -y * 0.05f);
            matrices.mulPose(rotationAxis.rotationDegrees(furtherData.itemLrotation + (y + 1) * 55f));
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

            // Render right stock item.
            matrices.pushPose();
            matrices.translate(itemTranslationFactor, 0f, -y * 0.05f);
            matrices.mulPose(rotationAxis.rotationDegrees(furtherData.itemRrotation + (y + 1) * 55f));
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
        }

        matrices.popPose();

        if (data.stockDisplayType()) {
            // Block-like stock: render price text, quantity text, and currency block/item.
            matrices.pushPose();
            matrices.translate(-0.02f, -0.3124f, 0.16f);
            matrices.mulPose(Axis.ZP.rotationDegrees(180f));
            matrices.mulPose(Axis.XP.rotationDegrees(180f));
            float textSize = data.useSmallTextPrice() ? 0.015f : 0.02f;
            matrices.scale(textSize, textSize, -textSize);
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
            matrices.translate(-0.02f, -0.17f, 0.43749f);
            matrices.mulPose(Axis.ZP.rotationDegrees(180f));
            textSize = data.useSmallTextProduct() ? 0.015f : 0.02f;
            matrices.scale(textSize, textSize, -textSize);
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

            matrices.pushPose();
            float scaleFactor;
            matrices.mulPose(Axis.YP.rotationDegrees(180f));
            if (data.currencyDisplayType()) {
                // Currency is a block.
                scaleFactor = 0.16f;
                matrices.translate(0f, -0.265f, -0.35f);
                matrices.mulPose(Axis.XP.rotationDegrees(-30f));
            } else {
                // Currency is an item.
                scaleFactor = 0.35f;
                matrices.translate(0f, -0.308f, -0.34f);
                matrices.mulPose(Axis.XP.rotationDegrees(-90f));
            }

            matrices.scale(scaleFactor, scaleFactor, scaleFactor);
            this.context.getItemRenderer().render(
                    data.paymentItem(),
                    ItemDisplayContext.GUI,
                    false,
                    matrices,
                    vertexConsumers,
                    light,
                    overlay,
                    context.getItemRenderer().getModel(data.paymentItem(), null, null, 0)
            );
            matrices.popPose();
        } else {
            // Item-like stock: render price, quantity, and currency item in a different layout.
            matrices.pushPose();
            matrices.translate(-0.08f, -0.16f, 0.43749f);
            matrices.mulPose(Axis.ZP.rotationDegrees(180f));
            float textSize = data.useSmallTextPrice() ? 0.012f : 0.016f;
            matrices.scale(textSize, textSize, -textSize);
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
            matrices.translate(0.2f, -0.16f, 0.43749f);
            matrices.mulPose(Axis.ZP.rotationDegrees(180f));
            textSize = data.useSmallTextProduct() ? 0.012f : 0.016f;
            matrices.scale(textSize, textSize, -textSize);
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

            matrices.pushPose();
            matrices.translate(-0.245f, -0.16f, 0.43f);
            float scaleFactor;
            if (data.currencyDisplayType()) {
                // Currency is a block.
                matrices.mulPose(Axis.XP.rotationDegrees(20f));
                scaleFactor = 0.16f;
            } else {
                // Currency is an item.
                scaleFactor = 0.22f;
            }

            matrices.mulPose(Axis.YP.rotationDegrees(180f));
            matrices.scale(scaleFactor, scaleFactor, scaleFactor);
            this.context.getItemRenderer().render(
                    data.paymentItem(),
                    ItemDisplayContext.GUI,
                    false,
                    matrices,
                    vertexConsumers,
                    light,
                    overlay,
                    context.getItemRenderer().getModel(data.paymentItem(), null, null, 0)
            );
            matrices.popPose();
        }
    }
}
