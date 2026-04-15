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
import net.spudacious5705.shops.block.entity.WindowSillShopEntity;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nullable;

public class WindowSillShopEntityRenderer implements BlockEntityRenderer<WindowSillShopEntity, WindowSillShopEntityRenderer.RenderState> {

    public static class RenderState extends BlockEntityRenderState {
        public int lightLevel;
        public boolean shopFunctional = false;
        public Direction direction = Direction.NORTH;
        public int rotation;
        public boolean stockDisplayType = false;
        public FormattedCharSequence stockQuantity;
        public float quantityTextWidth;
        public FormattedCharSequence priceQuantity;
        public float priceTextWidth;
        public boolean smallTextPrice;
        public boolean smallTextProduct;
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

    public WindowSillShopEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        this.context = ctx;
    }

    @Override
    public RenderState createRenderState() {
        return new RenderState();
    }

    @Override
    public void extractRenderState(WindowSillShopEntity blockEntity, RenderState renderState, float partialTick,
                                   @NonNull Vec3 cameraPosition, @Nullable ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        AbstractShopEntity.RendererData data = blockEntity.rendererData();
        data.frameAccumulator();
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);

        renderState.direction = data.direction();
        renderState.shopFunctional = data.shopFunctional();
        renderState.lightLevel = getLightLevel(blockEntity.getLevel(), blockEntity.getBlockPos());
        renderState.rotation = data.rotation();

        if (data.shopFunctional()) {
            renderState.stockDisplayType = data.stockDisplayType();
            renderState.stockQuantity = context.font().split(FormattedText.of(data.stockQuantity), 999).getFirst();
            renderState.priceQuantity = context.font().split(FormattedText.of(data.priceQuantity), 999).getFirst();
            renderState.smallTextPrice = data.useSmallTextPrice();
            renderState.smallTextProduct = data.useSmallTextProduct();
            renderState.quantityTextWidth = data.qWidth();
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

        // Display item — position depends on direction
        poseStack.pushPose();
        switch (renderState.direction) {
            case NORTH -> poseStack.translate(0.6f, 0.4f, 0.45f);
            case EAST  -> poseStack.translate(0.55f, 0.4f, 0.6f);
            case SOUTH -> poseStack.translate(0.4f, 0.4f, 0.55f);
            case WEST  -> poseStack.translate(0.45f, 0.4f, 0.4f);
            default    -> poseStack.translate(0.6f, 0.4f, 0.45f);
        }
        poseStack.mulPose(Axis.YP.rotationDegrees(renderState.rotation));
        if (renderState.stockDisplayType) {
            poseStack.scale(0.5f, 0.5f, 0.5f);
        } else {
            poseStack.scale(0.8f, 0.8f, 0.8f);
            poseStack.mulPose(Axis.XP.rotationDegrees(-90f));
            poseStack.translate(0f, 0f, -0.3f);
        }
        renderState.displayItem.submit(poseStack, nodeCollector, renderState.lightLevel, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();

        float dirRot = switch (renderState.direction) {
            case EAST -> 270f;
            case SOUTH -> 180f;
            case WEST -> 90f;
            default -> 0f;
        };

        // Price text on the sill front edge
        poseStack.pushPose();
        poseStack.translate(0.5f, 0.0f, 0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(dirRot));
        poseStack.translate(-0.5f, 0.0f, -0.5f);
        poseStack.translate(0.30125f, 0.22f, 0.9167f);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0f));
        poseStack.mulPose(Axis.XP.rotationDegrees(-22.5f));
        float tSc = renderState.smallTextPrice ? 0.014f : 0.018f;
        poseStack.scale(tSc, tSc, -tSc);
        nodeCollector.submitText(poseStack, renderState.priceTextWidth, -4f, renderState.priceQuantity,
                false, Font.DisplayMode.POLYGON_OFFSET, renderState.lightLevel, 0xFFffffff, 0, 0);
        poseStack.popPose();

        // Stock quantity text — horizontal on sill top
        poseStack.pushPose();
        poseStack.translate(0.5f, 0.0f, 0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(dirRot));
        poseStack.translate(-0.5f, 0.0f, -0.5f);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0f));
        poseStack.mulPose(Axis.XP.rotationDegrees(-90f));
        poseStack.translate(-0.15f, -0.126f, -0.15f);
        float sQs = renderState.smallTextProduct ? 0.020f : 0.025f;
        poseStack.scale(sQs, sQs, sQs);
        nodeCollector.submitText(poseStack, renderState.quantityTextWidth, -4f, renderState.stockQuantity,
                false, Font.DisplayMode.POLYGON_OFFSET, renderState.lightLevel, 0xFFffff00, 0, 0);
        poseStack.popPose();

        // Payment item on sill front edge
        poseStack.pushPose();
        poseStack.translate(0.5f, 0f, 0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(dirRot));
        poseStack.translate(-0.5f, 0f, -0.5f);
        poseStack.translate(0.13125f, 0.22f, 0.9167f);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0f));
        poseStack.mulPose(Axis.XP.rotationDegrees(157.5f));
        poseStack.scale(0.18f, 0.18f, 0.18f);
        renderState.paymentItem.submit(poseStack, nodeCollector, renderState.lightLevel, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();

        ShopRenderUtils.submitShopWarns(poseStack, nodeCollector, renderState.lightLevel,
                renderState.stockWarning, renderState.paymentWarning, renderState.renderIcons,
                renderState.iconRotation, -0.5f, 0.5f,
                renderState.noStockIcon, renderState.regFullIcon);
    }
}
