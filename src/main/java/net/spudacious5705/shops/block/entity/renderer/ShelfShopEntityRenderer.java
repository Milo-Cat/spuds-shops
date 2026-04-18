package net.spudacious5705.shops.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import net.spudacious5705.shops.block.entity.AbstractShopEntity;
import net.spudacious5705.shops.block.entity.ShelfShopEntity;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nullable;

public class ShelfShopEntityRenderer implements BlockEntityRenderer<ShelfShopEntity, ShelfShopEntityRenderer.RenderState> {

    public static class RenderState extends BlockEntityRenderState {
        public int lightLevel;
        public Direction direction = Direction.NORTH;

        // Bottom shelf
        public boolean bottomFunctional = false;
        public boolean bottomStockDisplayType = false;
        public boolean bottomCurrencyDisplayType = true;
        public float bottomItemLRotation;
        public float bottomItemRRotation;
        public FormattedCharSequence bottomStockQuantity;
        public float bottomQuantityTextWidth;
        public FormattedCharSequence bottomPriceQuantity;
        public float bottomPriceTextWidth;
        public boolean bottomSmallTextPrice;
        public boolean bottomSmallTextProduct;
        public boolean bottomStockWarning;
        public boolean bottomPaymentWarning;
        public boolean bottomRenderIcons;
        public float bottomIconRotation;
        final ItemStackRenderState bottomDisplayItem = new ItemStackRenderState();
        final ItemStackRenderState bottomPaymentItem = new ItemStackRenderState();
        final ItemStackRenderState bottomNoStockIcon = new ItemStackRenderState();
        final ItemStackRenderState bottomRegFullIcon = new ItemStackRenderState();

        // Top shelf
        public boolean topFunctional = false;
        public boolean topStockDisplayType = false;
        public boolean topCurrencyDisplayType = true;
        public float topItemLRotation;
        public float topItemRRotation;
        public FormattedCharSequence topStockQuantity;
        public float topQuantityTextWidth;
        public FormattedCharSequence topPriceQuantity;
        public float topPriceTextWidth;
        public boolean topSmallTextPrice;
        public boolean topSmallTextProduct;
        public boolean topStockWarning;
        public boolean topPaymentWarning;
        public boolean topRenderIcons;
        public float topIconRotation;
        final ItemStackRenderState topDisplayItem = new ItemStackRenderState();
        final ItemStackRenderState topPaymentItem = new ItemStackRenderState();
        final ItemStackRenderState topNoStockIcon = new ItemStackRenderState();
        final ItemStackRenderState topRegFullIcon = new ItemStackRenderState();
    }

    private final BlockEntityRendererProvider.Context context;

    public ShelfShopEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        this.context = ctx;
    }

    @Override
    public RenderState createRenderState() {
        return new RenderState();
    }

    @Override
    public void extractRenderState(ShelfShopEntity blockEntity, RenderState renderState, float partialTick,
                                   @NonNull Vec3 cameraPosition, @Nullable ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);

        AbstractShopEntity.RendererData dataBottom = blockEntity.rendererData();
        AbstractShopEntity.RendererData dataTop = blockEntity.rendererDataTop();
        dataBottom.frameAccumulator();
        dataTop.frameAccumulator();

        renderState.direction = blockEntity.getCachedFacingDirection();
        renderState.lightLevel = getLightLevel(blockEntity.getLevel(), blockEntity.getBlockPos());

        // Bottom shelf
        renderState.bottomFunctional = dataBottom.shopFunctional();
        if (dataBottom.shopFunctional()) {
            boolean bottomIsBlock = blockEntity.getLevel() != null
                    && dataBottom.displayItem().getItem() instanceof BlockItem bi
                    && bi.getBlock().defaultBlockState().isCollisionShapeFullBlock(blockEntity.getLevel(), blockEntity.getBlockPos());
            renderState.bottomStockDisplayType = dataBottom.stockDisplayType() && bottomIsBlock;
            renderState.bottomCurrencyDisplayType = dataBottom.currencyDisplayType();
            renderState.bottomItemLRotation = blockEntity.furtherDataBottom().itemLrotation;
            renderState.bottomItemRRotation = blockEntity.furtherDataBottom().itemRrotation;
            renderState.bottomStockQuantity = context.font().split(FormattedText.of(dataBottom.stockQuantity), 999).getFirst();
            renderState.bottomPriceQuantity = context.font().split(FormattedText.of(dataBottom.priceQuantity), 999).getFirst();
            renderState.bottomSmallTextPrice = dataBottom.useSmallTextPrice();
            renderState.bottomSmallTextProduct = dataBottom.useSmallTextProduct();
            renderState.bottomQuantityTextWidth = dataBottom.qWidth();
            renderState.bottomPriceTextWidth = dataBottom.width();
            ItemDisplayContext displayCtx = renderState.bottomStockDisplayType ? ItemDisplayContext.NONE : ItemDisplayContext.GUI;
            context.itemModelResolver().updateForTopItem(renderState.bottomDisplayItem,
                    dataBottom.displayItem(), displayCtx, blockEntity.getLevel(), null, 0);
            ItemDisplayContext currencyCtx = dataBottom.currencyDisplayType() ? ItemDisplayContext.NONE : ItemDisplayContext.GUI;
            context.itemModelResolver().updateForTopItem(renderState.bottomPaymentItem,
                    dataBottom.paymentItem(), currencyCtx, blockEntity.getLevel(), null, 0);

            renderState.bottomStockWarning = dataBottom.stockWarning;
            renderState.bottomPaymentWarning = dataBottom.paymentWarning;
            renderState.bottomRenderIcons = dataBottom.renderIcons();
            if (renderState.bottomRenderIcons && (renderState.bottomStockWarning || renderState.bottomPaymentWarning)) {
                renderState.bottomIconRotation = ShopRenderUtils.updateIconRotation(dataBottom, partialTick);
                ShopRenderUtils.extractIconState(context, renderState.bottomNoStockIcon, renderState.bottomRegFullIcon);
            }
        }

        // Top shelf
        renderState.topFunctional = dataTop.shopFunctional();
        if (dataTop.shopFunctional()) {
            boolean topIsBlock = blockEntity.getLevel() != null
                    && dataTop.displayItem().getItem() instanceof BlockItem bi
                    && bi.getBlock().defaultBlockState().isCollisionShapeFullBlock(blockEntity.getLevel(), blockEntity.getBlockPos());
            renderState.topStockDisplayType = dataTop.stockDisplayType() && topIsBlock;
            renderState.topCurrencyDisplayType = dataTop.currencyDisplayType();
            renderState.topItemLRotation = blockEntity.furtherDataTop().itemLrotation;
            renderState.topItemRRotation = blockEntity.furtherDataTop().itemRrotation;
            renderState.topStockQuantity = context.font().split(FormattedText.of(dataTop.stockQuantity), 999).getFirst();
            renderState.topPriceQuantity = context.font().split(FormattedText.of(dataTop.priceQuantity), 999).getFirst();
            renderState.topSmallTextPrice = dataTop.useSmallTextPrice();
            renderState.topSmallTextProduct = dataTop.useSmallTextProduct();
            renderState.topQuantityTextWidth = dataTop.qWidth();
            renderState.topPriceTextWidth = dataTop.width();
            ItemDisplayContext displayCtx = renderState.topStockDisplayType ? ItemDisplayContext.NONE : ItemDisplayContext.GUI;
            context.itemModelResolver().updateForTopItem(renderState.topDisplayItem,
                    dataTop.displayItem(), displayCtx, blockEntity.getLevel(), null, 0);
            ItemDisplayContext currencyCtx = dataTop.currencyDisplayType() ? ItemDisplayContext.NONE : ItemDisplayContext.GUI;
            context.itemModelResolver().updateForTopItem(renderState.topPaymentItem,
                    dataTop.paymentItem(), currencyCtx, blockEntity.getLevel(), null, 0);

            renderState.topStockWarning = dataTop.stockWarning;
            renderState.topPaymentWarning = dataTop.paymentWarning;
            renderState.topRenderIcons = dataTop.renderIcons();
            if (renderState.topRenderIcons && (renderState.topStockWarning || renderState.topPaymentWarning)) {
                renderState.topIconRotation = ShopRenderUtils.updateIconRotation(dataTop, partialTick);
                ShopRenderUtils.extractIconState(context, renderState.topNoStockIcon, renderState.topRegFullIcon);
            }
        }
    }

    private int getLightLevel(@Nullable Level level, BlockPos pos) {
        if (level == null) return LightTexture.pack(0, 0);
        return LightTexture.pack(
                level.getBrightness(LightLayer.BLOCK, pos),
                level.getBrightness(LightLayer.SKY, pos)
        );
    }

    @Override
    public void submit(RenderState renderState, @NonNull PoseStack poseStack,
                       @NonNull SubmitNodeCollector nodeCollector, @NonNull CameraRenderState cameraRenderState) {
        if (!renderState.bottomFunctional && !renderState.topFunctional) return;

        // Global direction rotation
        poseStack.pushPose();
        poseStack.translate(0.5f, 0.5f, 0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(switch (renderState.direction) {
            case EAST -> 270f;
            case SOUTH -> 180f;
            case WEST -> 90f;
            default -> 0f;
        }));

        // Bottom shelf
        if (renderState.bottomFunctional) {
            renderShelf(poseStack, nodeCollector, renderState.lightLevel,
                    renderState.bottomStockDisplayType, renderState.bottomCurrencyDisplayType,
                    renderState.bottomItemLRotation, renderState.bottomItemRRotation,
                    renderState.bottomStockQuantity, renderState.bottomQuantityTextWidth, renderState.bottomSmallTextProduct,
                    renderState.bottomPriceQuantity, renderState.bottomPriceTextWidth, renderState.bottomSmallTextPrice,
                    renderState.bottomDisplayItem, renderState.bottomPaymentItem);
        }

        // Top shelf (offset upward by 0.44 as in original)
        poseStack.translate(0f, 0.44f, 0f);
        if (renderState.topFunctional) {
            renderShelf(poseStack, nodeCollector, renderState.lightLevel,
                    renderState.topStockDisplayType, renderState.topCurrencyDisplayType,
                    renderState.topItemLRotation, renderState.topItemRRotation,
                    renderState.topStockQuantity, renderState.topQuantityTextWidth, renderState.topSmallTextProduct,
                    renderState.topPriceQuantity, renderState.topPriceTextWidth, renderState.topSmallTextPrice,
                    renderState.topDisplayItem, renderState.topPaymentItem);
        }

        poseStack.popPose();

        // Warning icons for both shelves
        ShopRenderUtils.submitShopWarns(poseStack, nodeCollector, renderState.lightLevel,
                renderState.bottomStockWarning, renderState.bottomPaymentWarning, renderState.bottomRenderIcons,
                renderState.bottomIconRotation, -1.05f, 0.3f,
                renderState.bottomNoStockIcon, renderState.bottomRegFullIcon);
        ShopRenderUtils.submitShopWarns(poseStack, nodeCollector, renderState.lightLevel,
                renderState.topStockWarning, renderState.topPaymentWarning, renderState.topRenderIcons,
                renderState.topIconRotation, -0.6f, 0.3f,
                renderState.topNoStockIcon, renderState.topRegFullIcon);
    }

    private void renderShelf(PoseStack poseStack, SubmitNodeCollector nodeCollector, int lightLevel,
                              boolean stockDisplayType, boolean currencyDisplayType,
                              float itemLRotation, float itemRRotation,
                              FormattedCharSequence stockQuantity, float quantityTextWidth, boolean smallTextProduct,
                              FormattedCharSequence priceQuantity, float priceTextWidth, boolean smallTextPrice,
                              ItemStackRenderState displayItem, ItemStackRenderState paymentItem) {
        float itemTranslationFactor;
        Axis rotationAxis;
        int renderCount;

        // Display items on shelf
        poseStack.pushPose();
        poseStack.translate(0f, -0.216f, 0.3f);
        if (stockDisplayType) {
            poseStack.scale(0.2f, 0.2f, 0.2f);
            itemTranslationFactor = 1.15f;
            rotationAxis = Axis.YP;
            renderCount = 1;
        } else {
            poseStack.translate(0f, -0.08f, 0f);
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0f));
            poseStack.scale(0.4f, 0.4f, 0.4f);
            itemTranslationFactor = 0.5f;
            rotationAxis = Axis.ZP;
            renderCount = 3;
        }

        for (int y = 0; y < renderCount; y++) {
            // Left item
            poseStack.pushPose();
            poseStack.translate(-itemTranslationFactor, 0f, -y * 0.05f);
            poseStack.mulPose(rotationAxis.rotationDegrees(itemLRotation + (y + 1) * 55f));
            displayItem.submit(poseStack, nodeCollector, lightLevel, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();

            // Right item
            poseStack.pushPose();
            poseStack.translate(itemTranslationFactor, 0f, -y * 0.05f);
            poseStack.mulPose(rotationAxis.rotationDegrees(itemRRotation + (y + 1) * 55f));
            displayItem.submit(poseStack, nodeCollector, lightLevel, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }
        poseStack.popPose();

        if (stockDisplayType) {
            // Price text (shelf surface, centre, flat)
            poseStack.pushPose();
            poseStack.translate(-0.02f, -0.3124f, 0.16f);
            poseStack.mulPose(Axis.ZP.rotationDegrees(180f));
            poseStack.mulPose(Axis.XP.rotationDegrees(-90f));
            float textSize = smallTextPrice ? 0.015f : 0.02f;
            poseStack.scale(textSize, textSize, -textSize);
            nodeCollector.submitText(poseStack, priceTextWidth, -4f, priceQuantity,
                    false, Font.DisplayMode.POLYGON_OFFSET, lightLevel, 0xFFffffff, 0, 0);
            poseStack.popPose();

            // Stock quantity text (back wall, centred)
            poseStack.pushPose();
            poseStack.translate(-0.02f, -0.17f, 0.43749f);
            poseStack.mulPose(Axis.ZP.rotationDegrees(180f));
            textSize = smallTextProduct ? 0.015f : 0.02f;
            poseStack.scale(textSize, textSize, -textSize);
            nodeCollector.submitText(poseStack, quantityTextWidth, -4f, stockQuantity,
                    false, Font.DisplayMode.POLYGON_OFFSET, lightLevel, 0xFFffff00, 0, 0);
            poseStack.popPose();

            // Payment item (shelf surface, flat and centred between display items)
            poseStack.pushPose();
            poseStack.translate(0f, -0.32f, 0.18f);
            poseStack.mulPose(Axis.XP.rotationDegrees(90f));
            if (currencyDisplayType) {
                poseStack.scale(0.16f, 0.16f, 0.16f);
            } else {
                poseStack.scale(0.22f, 0.22f, 0.22f);
            }
            paymentItem.submit(poseStack, nodeCollector, lightLevel, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        } else {
            // Price text
            poseStack.pushPose();
            poseStack.translate(-0.08f, -0.16f, 0.43749f);
            poseStack.mulPose(Axis.ZP.rotationDegrees(180f));
            float textSize = smallTextPrice ? 0.012f : 0.016f;
            poseStack.scale(textSize, textSize, -textSize);
            nodeCollector.submitText(poseStack, priceTextWidth, -4f, priceQuantity,
                    false, Font.DisplayMode.POLYGON_OFFSET, lightLevel, 0xFFffffff, 0, 0);
            poseStack.popPose();

            // Stock quantity text
            poseStack.pushPose();
            poseStack.translate(0.2f, -0.16f, 0.43749f);
            poseStack.mulPose(Axis.ZP.rotationDegrees(180f));
            textSize = smallTextProduct ? 0.012f : 0.016f;
            poseStack.scale(textSize, textSize, -textSize);
            nodeCollector.submitText(poseStack, quantityTextWidth, -4f, stockQuantity,
                    false, Font.DisplayMode.POLYGON_OFFSET, lightLevel, 0xFFffff00, 0, 0);
            poseStack.popPose();

            // Payment item
            poseStack.pushPose();
            poseStack.translate(-0.245f, -0.16f, 0.43f);
            if (currencyDisplayType) {
                poseStack.mulPose(Axis.XP.rotationDegrees(20f));
                poseStack.mulPose(Axis.YP.rotationDegrees(180f));
                poseStack.scale(0.16f, 0.16f, 0.16f);
            } else {
                poseStack.mulPose(Axis.YP.rotationDegrees(180f));
                poseStack.scale(0.22f, 0.22f, 0.22f);
            }
            paymentItem.submit(poseStack, nodeCollector, lightLevel, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }
    }
}
