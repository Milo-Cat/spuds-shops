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
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import net.spudacious5705.shops.block.entity.AbstractShopEntity;
import net.spudacious5705.shops.block.entity.RugShopEntity;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nullable;

import static net.minecraft.util.Mth.clamp;

public class RugShopEntityRenderer implements BlockEntityRenderer<RugShopEntity, RugShopEntityRenderer.RenderState> {

    public static class RenderState extends BlockEntityRenderState {
        public int lightLevel;
        public boolean shopFunctional = false;
        public boolean stockDisplayType = false;
        // Animated values captured from furtherData each frame
        public float itemHeight;
        public float itemRotationY;
        public float itemRotationX;
        public float itemRotationZ;
        // Text
        public FormattedCharSequence stockQuantity;
        public float quantityTextWidth;
        public FormattedCharSequence priceQuantity;
        public float priceTextWidth;
        public boolean smallTextPrice;
        public boolean smallTextProduct;
        // Currency display
        public boolean currencyDisplayType = true;
        // Warning icons
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

    public RugShopEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        this.context = ctx;
    }

    @Override
    public RenderState createRenderState() {
        return new RenderState();
    }

    @Override
    public void extractRenderState(RugShopEntity blockEntity, RenderState renderState, float partialTick,
                                   @NonNull Vec3 cameraPosition, @Nullable ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        AbstractShopEntity.RendererData data = blockEntity.rendererData();
        data.frameAccumulator();
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);

        renderState.shopFunctional = data.shopFunctional();
        renderState.lightLevel = getLightLevel(blockEntity.getLevel(), blockEntity.getBlockPos());

        if (data.shopFunctional()) {
            renderState.stockDisplayType = data.stockDisplayType();
            renderState.currencyDisplayType = data.currencyDisplayType();
            renderState.stockQuantity = context.font().split(FormattedText.of(data.stockQuantity), 999).getFirst();
            renderState.priceQuantity = context.font().split(FormattedText.of(data.priceQuantity), 999).getFirst();
            renderState.smallTextPrice = data.useSmallTextPrice();
            renderState.smallTextProduct = data.useSmallTextProduct();
            renderState.quantityTextWidth = data.qWidth();
            renderState.priceTextWidth = data.width();
            ItemDisplayContext displayCtx = data.stockDisplayType() ? ItemDisplayContext.NONE : ItemDisplayContext.GUI;
            context.itemModelResolver().updateForTopItem(renderState.displayItem,
                    data.displayItem(), displayCtx, blockEntity.getLevel(), null, 0);
            ItemDisplayContext currencyCtx = data.currencyDisplayType() ? ItemDisplayContext.NONE : ItemDisplayContext.GUI;
            context.itemModelResolver().updateForTopItem(renderState.paymentItem,
                    data.paymentItem(), currencyCtx, blockEntity.getLevel(), null, 0);

            // Update animation state on the entity's furtherData, then snapshot into renderState
            RugShopEntity.RugRenderData fd = blockEntity.furtherData();
            long currentNanoTime = System.nanoTime();
            float delta = (currentNanoTime - blockEntity.lastNanoTime) * 0.00000004f;
            blockEntity.lastNanoTime = currentNanoTime;

            float foo = fd.itemHeight;
            float rand = (float) (Math.random() * 0.02f);
            foo = (foo + delta * (0.02f + rand)) % 6.28318530718f;
            fd.itemHeight = foo;
            renderState.itemHeight = foo;

            rand = (float) (Math.random() * 0.03f);
            foo = fd.itemRotationY;
            if (fd.rotateDirectionY) {
                foo = (foo + delta * (0.2f + rand)) % 360;
            } else {
                foo = (foo - delta * (0.2f + rand)) % 360;
            }
            fd.itemRotationY = foo;
            renderState.itemRotationY = foo;

            rand = (float) (Math.random() * 0.05f);
            foo = fd.itemRotationX;
            if (fd.rotateDirectionX) {
                foo = (foo + delta * (1.25f + rand)) % 360;
            } else {
                foo = (foo - delta * (1.25f + rand)) % 360;
            }
            fd.itemRotationX = foo;
            renderState.itemRotationX = foo;

            foo = fd.itemRotationSpeedZ;
            foo = (float) clamp(foo + ((Math.random()) - 0.5f) * delta * 0.05f, -0.3f, 0.3f);
            fd.itemRotationSpeedZ = foo;
            foo = (fd.itemRotationZ + foo) % 360f;
            fd.itemRotationZ = foo;
            renderState.itemRotationZ = foo;

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

        // Display item — floating and rotating above rug
        poseStack.pushPose();
        poseStack.translate(0.5f, 0.18f, 0.5f);
        poseStack.pushPose();

        if (renderState.stockDisplayType) {
            poseStack.scale(0.35f, 0.35f, 0.35f);
        } else {
            poseStack.translate(0f, -0.16f, 0f);
            poseStack.scale(0.8f, 0.8f, 0.8f);
        }

        poseStack.translate(0f, 0.5f, 0f);
        poseStack.translate(0f, (float) Math.sin(renderState.itemHeight) * 0.15f, 0f);
        poseStack.mulPose(Axis.YP.rotationDegrees(renderState.itemRotationY));
        poseStack.mulPose(Axis.XP.rotationDegrees(renderState.itemRotationX));
        poseStack.mulPose(Axis.ZP.rotationDegrees(renderState.itemRotationZ));
        poseStack.scale(0.8f, 0.8f, 0.8f);

        renderState.displayItem.submit(poseStack, nodeCollector, renderState.lightLevel, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();

        // Price text — on two corners of the rug
        poseStack.pushPose();
        poseStack.translate(-0.27f, -0.178f, -0.37f);
        poseStack.mulPose(Axis.YP.rotationDegrees(-135f));
        poseStack.mulPose(Axis.XP.rotationDegrees(90f));
        float textSize = renderState.smallTextPrice ? 0.012f : 0.019f;
        poseStack.scale(textSize, textSize, textSize);
        nodeCollector.submitText(poseStack, renderState.priceTextWidth, -4f, renderState.priceQuantity,
                false, Font.DisplayMode.POLYGON_OFFSET, renderState.lightLevel, 0xFFffffff, 0, 0);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0.27f, -0.178f, 0.37f);
        poseStack.mulPose(Axis.YP.rotationDegrees(45f));
        poseStack.mulPose(Axis.XP.rotationDegrees(90f));
        poseStack.scale(textSize, textSize, textSize);
        nodeCollector.submitText(poseStack, renderState.priceTextWidth, -4f, renderState.priceQuantity,
                false, Font.DisplayMode.POLYGON_OFFSET, renderState.lightLevel, 0xFFffffff, 0, 0);
        poseStack.popPose();

        // Stock quantity text — on the other two corners
        textSize = renderState.smallTextProduct ? 0.012f : 0.019f;

        poseStack.pushPose();
        poseStack.translate(0.37f, -0.178f, -0.37f);
        poseStack.mulPose(Axis.YP.rotationDegrees(135f));
        poseStack.mulPose(Axis.XP.rotationDegrees(90f));
        poseStack.scale(textSize, textSize, textSize);
        nodeCollector.submitText(poseStack, renderState.quantityTextWidth, -4f, renderState.stockQuantity,
                false, Font.DisplayMode.POLYGON_OFFSET, renderState.lightLevel, 0xFFffff00, 0, 0);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(-0.37f, -0.178f, 0.37f);
        poseStack.mulPose(Axis.YP.rotationDegrees(-45f));
        poseStack.mulPose(Axis.XP.rotationDegrees(90f));
        poseStack.scale(textSize, textSize, textSize);
        nodeCollector.submitText(poseStack, renderState.quantityTextWidth, -4f, renderState.stockQuantity,
                false, Font.DisplayMode.POLYGON_OFFSET, renderState.lightLevel, 0xFFffff00, 0, 0);
        poseStack.popPose();

        // Payment item — two opposite corners
        float scale;
        boolean oppType = renderState.currencyDisplayType;
        if (oppType) {
            scale = 0.16f;
        } else {
            scale = 0.25f;
        }

        poseStack.translate(-0.39f, -0.178f, -0.23f);

        poseStack.pushPose();
        if (oppType) {
            poseStack.mulPose(Axis.YP.rotationDegrees(-45f));
        } else {
            poseStack.mulPose(Axis.XP.rotationDegrees(90f));
            poseStack.mulPose(Axis.ZP.rotationDegrees(-45f));
        }
        poseStack.scale(scale, scale, scale);
        renderState.paymentItem.submit(poseStack, nodeCollector, renderState.lightLevel, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0.39f, 0.3f, 0.23f);
        poseStack.mulPose(Axis.YP.rotationDegrees(180f));
        poseStack.translate(-0.39f, -0.3f, -0.23f);
        if (oppType) {
            poseStack.mulPose(Axis.YP.rotationDegrees(-45f));
        } else {
            poseStack.mulPose(Axis.XP.rotationDegrees(90f));
            poseStack.mulPose(Axis.ZP.rotationDegrees(-45f));
        }
        poseStack.scale(scale, scale, scale);
        renderState.paymentItem.submit(poseStack, nodeCollector, renderState.lightLevel, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();

        poseStack.popPose();

        ShopRenderUtils.submitShopWarns(poseStack, nodeCollector, renderState.lightLevel,
                renderState.stockWarning, renderState.paymentWarning, renderState.renderIcons,
                renderState.iconRotation, -0.5f, 0.5f,
                renderState.noStockIcon, renderState.regFullIcon);
    }
}
