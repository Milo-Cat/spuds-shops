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
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import net.spudacious5705.shops.block.entity.AbstractShopEntity;
import net.spudacious5705.shops.block.entity.HookShopEntity;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nullable;

public class HookShopEntityRenderer implements BlockEntityRenderer<HookShopEntity, HookShopEntityRenderer.RenderState> {

    public static class RenderState extends BlockEntityRenderState {
        public int lightLevel;
        public boolean shopFunctional = false;
        public Direction direction = Direction.NORTH;
        public boolean stockDisplayType = false;
        public boolean currencyDisplayType = true;
        public FormattedCharSequence priceQuantity;
        public float priceTextWidth;
        public boolean smallTextPrice;
        public boolean stockWarning;
        public boolean paymentWarning;
        public boolean renderIcons;
        public float iconRotation;
        final ItemStackRenderState displayItem = new ItemStackRenderState();
        final ItemStackRenderState paymentItem = new ItemStackRenderState();
        final ItemStackRenderState noStockIcon = new ItemStackRenderState();
        final ItemStackRenderState regFullIcon = new ItemStackRenderState();
    }

    private final BlockEntityRendererProvider.Context context;

    public HookShopEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        this.context = ctx;
    }

    @Override
    public RenderState createRenderState() {
        return new RenderState();
    }

    @Override
    public void extractRenderState(HookShopEntity blockEntity, RenderState renderState, float partialTick,
                                   @NonNull Vec3 cameraPosition, @Nullable ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        AbstractShopEntity.RendererData data = blockEntity.rendererData();
        data.frameAccumulator();
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);

        renderState.direction = data.direction();
        renderState.shopFunctional = data.shopFunctional();
        renderState.lightLevel = getLightLevel(blockEntity.getLevel(), blockEntity.getBlockPos());

        if (data.shopFunctional()) {
            renderState.stockDisplayType = data.stockDisplayType();
            renderState.currencyDisplayType = data.currencyDisplayType();
            renderState.priceQuantity = context.font().split(FormattedText.of(data.priceQuantity), 999).getFirst();
            renderState.smallTextPrice = data.useSmallTextPrice();
            renderState.priceTextWidth = data.width();
            ItemDisplayContext displayCtx = data.stockDisplayType() ? ItemDisplayContext.NONE : ItemDisplayContext.GUI;
            context.itemModelResolver().updateForTopItem(renderState.displayItem,
                    data.displayItem(), displayCtx, blockEntity.getLevel(), null, 0);
            context.itemModelResolver().updateForTopItem(renderState.paymentItem,
                    data.paymentItem(), ItemDisplayContext.GUI, blockEntity.getLevel(), null, 0);

            renderState.stockWarning = data.stockWarning;
            renderState.paymentWarning = data.paymentWarning;
            renderState.renderIcons = data.renderIcons();
            if (renderState.renderIcons && (renderState.stockWarning || renderState.paymentWarning)) {
                renderState.iconRotation = ShopRenderUtils.updateIconRotation(data, partialTick);
                ShopRenderUtils.extractIconState(context, renderState.noStockIcon, renderState.regFullIcon);
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
        if (!renderState.shopFunctional) return;

        poseStack.pushPose();
        poseStack.translate(0.5f, 0.5f, 0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(switch (renderState.direction) {
            case EAST -> 270f;
            case SOUTH -> 180f;
            case WEST -> 90f;
            default -> 0f;
        }));

        // Display item hanging from hook
        poseStack.pushPose();
        if (renderState.stockDisplayType) {
            poseStack.translate(0f, -0.25f, 0f);
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0f));
            poseStack.scale(0.5f, 0.5f, 0.5f);
        } else {
            poseStack.translate(0f, -0.5f, 0f);
            poseStack.scale(0.7f, 0.7f, 0.7f);
            poseStack.mulPose(Axis.ZP.rotationDegrees(45.0f));
        }
        renderState.displayItem.submit(poseStack, nodeCollector, renderState.lightLevel, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();

        // Price text — front and back of tag
        poseStack.pushPose();
        poseStack.translate(0.05f, 0.18f, -0.03126f);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0f));
        float textSize = renderState.smallTextPrice ? 0.012f : 0.016f;

        poseStack.pushPose();
        poseStack.scale(textSize, textSize, -textSize);
        nodeCollector.submitText(poseStack, renderState.priceTextWidth, -4f, renderState.priceQuantity,
                false, Font.DisplayMode.POLYGON_OFFSET, renderState.lightLevel, 0xFFffffff, 0, 0);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0.05f, 0f, 0.03126f);
        poseStack.mulPose(Axis.YP.rotationDegrees(180f));
        poseStack.translate(-0.05f, 0f, -0.03126f);
        poseStack.scale(textSize, textSize, -textSize);
        nodeCollector.submitText(poseStack, renderState.priceTextWidth, -4f, renderState.priceQuantity,
                false, Font.DisplayMode.POLYGON_OFFSET, renderState.lightLevel, 0xFFffffff, 0, 0);
        poseStack.popPose();
        poseStack.popPose();

        // Payment item (both sides)
        float scaleFactor = renderState.currencyDisplayType ? 0.16f : 0.22f;
        float r = renderState.currencyDisplayType ? -0.06f : -0.04f;

        poseStack.translate(-0.12f, 0.2f, r);
        poseStack.mulPose(Axis.YP.rotationDegrees(180f));

        poseStack.pushPose();
        poseStack.translate(-0.12f, 0f, r);
        poseStack.mulPose(Axis.YP.rotationDegrees(180f));
        poseStack.translate(0.12f, 0f, -r);
        poseStack.scale(scaleFactor, scaleFactor, scaleFactor);
        renderState.paymentItem.submit(poseStack, nodeCollector, renderState.lightLevel, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.scale(scaleFactor, scaleFactor, scaleFactor);
        renderState.paymentItem.submit(poseStack, nodeCollector, renderState.lightLevel, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();

        poseStack.popPose();

        ShopRenderUtils.submitShopWarns(poseStack, nodeCollector, renderState.lightLevel,
                renderState.stockWarning, renderState.paymentWarning, renderState.renderIcons,
                renderState.iconRotation, -2.1f, 0.5f,
                renderState.noStockIcon, renderState.regFullIcon);
    }
}
