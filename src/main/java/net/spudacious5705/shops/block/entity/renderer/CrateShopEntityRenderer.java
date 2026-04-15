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
import net.spudacious5705.shops.block.entity.CrateShopEntity;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nullable;

public class CrateShopEntityRenderer implements BlockEntityRenderer<CrateShopEntity, CrateShopEntityRenderer.RenderState> {

    // Per-item positions relative to crate centre (x, y, z, rotY, rotX) — from original 1.21.1 branch
    private static final TranslationFactor[] DISPLAY_TRANSLATIONS_BLOCK = {
            new TranslationFactor(0.02f, 0.2f, 0.33f, -8f, 0f),
            new TranslationFactor(-0.23f, 0.53f, 0.26f, -4f, -4f),
            new TranslationFactor(0.02f, 0.4f, 0.28f, 175f, 5f),
            new TranslationFactor(0.08f, 0.6f, 0.28f, -175f, 5f),
            new TranslationFactor(0.23f, 0.7f, 0.22f, 17f, -8f),
            new TranslationFactor(-0.25f, 0.34f, 0.29f, -170f, 0f),
            new TranslationFactor(-0.23f, 0.7f, 0.24f, 170f, 187f),
            new TranslationFactor(-0.28f, 0.2f, 0.33f, -150f, 10f),
            new TranslationFactor(0.24f, 0.22f, 0.35f, 165f, 0f),
            new TranslationFactor(0.25f, 0.45f, 0.33f, -10f, -8f),
            new TranslationFactor(0.23f, 0.79f, 0.23f, 170f, -170f),
            new TranslationFactor(-0.25f, 0.79f, 0.18f, -30f, 10f),
            new TranslationFactor(-0.04f, 0.79f, 0.22f, 45f, 18f)
    };
    private static final TranslationFactor[] DISPLAY_TRANSLATIONS_ITEM = {
            new TranslationFactor(0.02f, 0.2f, 0.33f, -8f, 0f),
            new TranslationFactor(-0.23f, 0.53f, 0.26f, -4f, -4f),
            new TranslationFactor(0.02f, 0.4f, 0.28f, 175f, 5f),
            new TranslationFactor(0.08f, 0.6f, 0.28f, -175f, 5f),
            new TranslationFactor(0.23f, 0.7f, 0.22f, 17f, -8f),
            new TranslationFactor(-0.25f, 0.34f, 0.29f, -170f, 0f),
            new TranslationFactor(-0.23f, 0.7f, 0.24f, 170f, 187f),
            new TranslationFactor(-0.24f, 0.18f, 0.24f, -170f, -16f),
            new TranslationFactor(0.24f, 0.16f, 0.3f, 165f, 0f),
            new TranslationFactor(0.22f, 0.45f, 0.3f, -10f, -8f),
            new TranslationFactor(0.23f, 0.72f, 0.19f, 170f, -170f),
            new TranslationFactor(-0.04f, 0.77f, 0.26f, 0f, -14f)
    };

    public static class RenderState extends BlockEntityRenderState {
        public int lightLevel;
        public boolean shopFunctional = false;
        public Direction direction = Direction.NORTH;
        public boolean stockDisplayType = false;
        public FormattedCharSequence stockQuantity;
        public float quantityTextWidth;
        public FormattedCharSequence priceQuantity;
        public float priceTextWidth;
        public boolean smallTextPrice;
        public boolean smallTextProduct;
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

    public CrateShopEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        this.context = ctx;
    }

    @Override
    public RenderState createRenderState() {
        return new RenderState();
    }

    @Override
    public void extractRenderState(CrateShopEntity blockEntity, RenderState renderState, float partialTick,
                                   @NonNull Vec3 cameraPosition, @Nullable ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        AbstractShopEntity.RendererData data = blockEntity.rendererData();
        data.frameAccumulator();
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);

        renderState.direction = data.direction();
        renderState.shopFunctional = data.shopFunctional();
        renderState.lightLevel = getLightLevel(blockEntity.getLevel(), blockEntity.getBlockPos());

        if (data.shopFunctional()) {
            renderState.stockDisplayType = data.stockDisplayType();
            renderState.stockQuantity = context.font().split(FormattedText.of(data.stockQuantity), 999).getFirst();
            renderState.priceQuantity = context.font().split(FormattedText.of(data.priceQuantity), 999).getFirst();
            renderState.smallTextPrice = data.useSmallTextPrice();
            renderState.smallTextProduct = data.useSmallTextProduct();
            renderState.quantityTextWidth = data.qWidth();
            renderState.priceTextWidth = data.width();
            ItemDisplayContext ctx = data.stockDisplayType() ? ItemDisplayContext.NONE : ItemDisplayContext.GUI;
            context.itemModelResolver().updateForTopItem(renderState.displayItem,
                    data.displayItem(), ctx, blockEntity.getLevel(), null, 0);
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

        // Global: direction rotation + centre on block
        poseStack.pushPose();
        poseStack.translate(0.5f, 0f, 0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(switch (renderState.direction) {
            case EAST -> 270f;
            case SOUTH -> 180f;
            case WEST -> 90f;
            default -> 0f;
        }));
        poseStack.translate(0f, 0f, 0f); // already at 0.5,0,0.5

        // Items inside crate
        poseStack.pushPose();
        float scaleX, scaleY, scaleZ;
        float rotXBonus;
        TranslationFactor[] translations;

        if (renderState.stockDisplayType) {
            scaleX = 0.4f; scaleY = 0.31667f; scaleZ = 0.4f;
            poseStack.translate(0f, -0.18f, -0.08f);
            poseStack.translate(0f, 0.3f, 0f);
            poseStack.mulPose(Axis.XP.rotationDegrees(40f));
            poseStack.translate(0f, -0.3f, 0f);
            rotXBonus = -40f;
            poseStack.scale(0.75f, 0.75f, 0.75f);
            translations = DISPLAY_TRANSLATIONS_BLOCK;
        } else {
            scaleX = 0.5f; scaleY = 0.5f; scaleZ = 0.8f;
            poseStack.translate(0f, 0.3f, 0f);
            poseStack.mulPose(Axis.XP.rotationDegrees(40f));
            poseStack.translate(0f, -0.3f, 0f);
            rotXBonus = 0f;
            translations = DISPLAY_TRANSLATIONS_ITEM;
        }

        for (TranslationFactor t : translations) {
            poseStack.pushPose();
            poseStack.translate(t.x, t.y, -t.z);
            poseStack.mulPose(Axis.YP.rotationDegrees(t.ry));
            poseStack.mulPose(Axis.XP.rotationDegrees(t.rx + rotXBonus));
            poseStack.scale(scaleX, scaleY, scaleZ);
            renderState.displayItem.submit(poseStack, nodeCollector, renderState.lightLevel, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }
        poseStack.popPose();

        // Price text (count of currency)
        poseStack.pushPose();
        poseStack.translate(0.06f, 0.14f, -0.664f);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0f));
        poseStack.mulPose(Axis.XP.rotationDegrees(-22.5f));
        float tSc = renderState.smallTextPrice ? 0.014f : 0.018f;
        poseStack.scale(tSc, tSc, tSc);
        nodeCollector.submitText(poseStack, renderState.priceTextWidth, -4f, renderState.priceQuantity,
                false, Font.DisplayMode.POLYGON_OFFSET, renderState.lightLevel, 0xFFffffff, 0, 0);
        poseStack.popPose();

        // Stock quantity text
        poseStack.pushPose();
        poseStack.translate(-0.03f, 0.3f, -0.5975f);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0f));
        poseStack.mulPose(Axis.XP.rotationDegrees(-22.5f));
        tSc = renderState.smallTextProduct ? 0.021f : 0.024f;
        poseStack.scale(tSc, tSc, tSc);
        nodeCollector.submitText(poseStack, renderState.quantityTextWidth, -4f, renderState.stockQuantity,
                false, Font.DisplayMode.POLYGON_OFFSET, renderState.lightLevel, 0xFFffff00, 0, 0);
        poseStack.popPose();

        // Payment item (currency type)
        poseStack.pushPose();
        poseStack.translate(-0.12f, 0.14f, -0.664f);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0f));
        poseStack.mulPose(Axis.XP.rotationDegrees(157.5f));
        poseStack.scale(0.18f, 0.18f, 0.18f);
        renderState.paymentItem.submit(poseStack, nodeCollector, renderState.lightLevel, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();

        poseStack.popPose();

        ShopRenderUtils.submitShopWarns(poseStack, nodeCollector, renderState.lightLevel,
                renderState.stockWarning, renderState.paymentWarning, renderState.renderIcons,
                renderState.iconRotation, 0f, 0.5f,
                renderState.noStockIcon, renderState.regFullIcon);
    }

    record TranslationFactor(float x, float y, float z, float ry, float rx) {}
}
