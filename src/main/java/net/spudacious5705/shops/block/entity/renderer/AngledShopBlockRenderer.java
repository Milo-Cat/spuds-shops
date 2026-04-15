package net.spudacious5705.shops.block.entity.renderer;


import com.mojang.authlib.minecraft.client.MinecraftClient;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MaterialMapper;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import net.spudacious5705.shops.block.entity.AbstractShopEntity;
import net.spudacious5705.shops.block.entity.AngledShopEntity;
import net.spudacious5705.shops.block.resources.CushionModel;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nullable;
import java.util.List;

public class AngledShopBlockRenderer implements BlockEntityRenderer<AngledShopEntity, AngledShopBlockRenderer.AngledShopEntityRenderState> {

    public static class AngledShopEntityRenderState extends BlockEntityRenderState {
        public int lightLevel;

        public boolean stockDisplayType = false;
        public boolean currencyDisplayType = true;

        public FormattedCharSequence stockQuantity;
        protected float quantityTextWidth;
        public boolean stockWarning = false;
        public boolean paymentWarning = false;
        protected Direction direction = Direction.NORTH;
        protected int rotation;
        protected boolean smallTextPrice;
        protected boolean smallTextProduct;
        protected boolean shopFunctional = false;
        final ItemStackRenderState paymentItem = new ItemStackRenderState();
        final ItemStackRenderState displayItem = new ItemStackRenderState();
        protected FormattedCharSequence priceQuantity;
        protected float priceTextWidth;
    }

    private final BlockEntityRendererProvider.Context context;

    private final CushionModel model;

    private final ModelPart cushion;

    public static final MaterialMapper MAPPER = new MaterialMapper(TextureAtlas.LOCATION_BLOCKS, "entity/conduit");
    public static final Material SHELL_TEXTURE = MAPPER.defaultNamespaceApply("base");

    public AngledShopBlockRenderer(BlockEntityRendererProvider.Context ctx) {
        this.context = ctx;
        this.cushion = ctx.bakeLayer(CushionModel.LAYER_LOCATION);
        this.model = new CushionModel(ctx.bakeLayer(CushionModel.LAYER_LOCATION));
    }

    @Override
    public AngledShopEntityRenderState createRenderState() {
        return new AngledShopEntityRenderState();
    }

    @Override
    public void extractRenderState(
            AngledShopEntity blockEntity, AngledShopEntityRenderState renderState, float partialTick,
            @NonNull Vec3 cameraPosition, @Nullable ModelFeatureRenderer.CrumblingOverlay breakProgress
    ) {
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
            renderState.quantityTextWidth = data.qWidth();
            renderState.priceQuantity = context.font().split(FormattedText.of(data.priceQuantity), 999).getFirst();
            renderState.priceTextWidth = data.width();
            renderState.smallTextPrice = data.useSmallTextPrice();
            renderState.smallTextProduct = data.useSmallTextProduct();
            context.itemModelResolver().updateForTopItem(renderState.displayItem,
                    data.displayItem(), ItemDisplayContext.FIXED, blockEntity.getLevel(), null, 0);
            context.itemModelResolver().updateForTopItem(renderState.paymentItem,
                    data.paymentItem(), ItemDisplayContext.FIXED, blockEntity.getLevel(), null, 0);
        }
    }

    private int getLightLevel(@Nullable Level level, BlockPos pos) {
        if (level == null) return LightTexture.pack(0,0);
        return LightTexture.pack(
                level.getBrightness(LightLayer.BLOCK, pos),
                level.getBrightness(LightLayer.SKY, pos)
        );
    }

    @Override
    public void submit(
            AngledShopEntityRenderState renderState, @NonNull PoseStack poseStack,
            @NonNull SubmitNodeCollector nodeCollector, @NonNull CameraRenderState cameraRenderState
    ) {
        ItemDisplayContext mode;
        Font font = this.context.font();

        poseStack.pushPose();
        poseStack.translate(0.5f, 0f, 0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(
                switch (renderState.direction) {
                    case EAST -> 270f;
                    case SOUTH -> 180f;
                    case WEST -> 90f;
                    default -> 0f;
                }));
        poseStack.translate(-0.5f, 0f, -0.5f);

        nodeCollector.submitModelPart(
                cushion,
                poseStack,
                SHELL_TEXTURE.renderType(RenderTypes::entitySolid),
                renderState.lightCoords,
                OverlayTexture.NO_OVERLAY,
                context.materials().get(SHELL_TEXTURE),
                -1,
                renderState.breakProgress
        );

        /*this.model.renderToBuffer(
                poseStack,
                bufferSource.getBuffer(RenderTypes.entitySolid(AngledShopEntity.getCushionTextureID())),
                renderState.light,
                overlay,
                1
        );*/

        poseStack.popPose();

        if (renderState.shopFunctional) {

            //render item being sold
            poseStack.pushPose();
            poseStack.translate(0.5f, 0.955f, 0.5f);

            poseStack.mulPose(Axis.YP.rotationDegrees(renderState.rotation));
            poseStack.mulPose(Axis.XP.rotationDegrees(-67.5f));

            if (renderState.stockDisplayType) {
                poseStack.scale(0.3f, 0.3f, 0.3f);
                mode = ItemDisplayContext.NONE;
            } else {
                poseStack.scale(0.4f, 0.4f, 0.4f);
                mode = ItemDisplayContext.GUI;
            }

            renderState.displayItem.submit(
                    poseStack, nodeCollector, renderState.lightLevel,
                    OverlayTexture.NO_OVERLAY, 0);


            poseStack.popPose();

            //render price (count of currency)
            poseStack.pushPose();


            switch (renderState.direction) {
                case NORTH -> {
                    poseStack.translate(0.57f, 0.514375f, 0.0525f);
                    poseStack.mulPose(Axis.ZP.rotationDegrees(180.0f));
                    poseStack.mulPose(Axis.XP.rotationDegrees(-22.5f));
                    poseStack.mulPose(Axis.YP.rotationDegrees(0.0f));
                }
                case EAST -> {
                    poseStack.translate(0.9475f, 0.514375f, 0.57f);
                poseStack.mulPose(Axis.ZP.rotationDegrees(180.0f));
                poseStack.mulPose(Axis.YP.rotationDegrees(90.0f));
                poseStack.mulPose(Axis.XP.rotationDegrees(-22.5f));
                }
                case SOUTH -> {
                    poseStack.translate(0.43f, 0.514375f, 0.9475f);
                    poseStack.mulPose(Axis.ZP.rotationDegrees(180.0f));
                    poseStack.mulPose(Axis.YP.rotationDegrees(180.0f));
                    poseStack.mulPose(Axis.XP.rotationDegrees(-22.5f));
                }
                case WEST -> {
                    poseStack.translate(0.0525f, 0.514375f, .43f);
                    poseStack.mulPose(Axis.ZP.rotationDegrees(180.0f));
                    poseStack.mulPose(Axis.YP.rotationDegrees(270.0f));
                    poseStack.mulPose(Axis.XP.rotationDegrees(-22.5f));
                }
            }


            float tSc = renderState.smallTextPrice ? 0.014f : 0.018f;

            poseStack.scale(tSc, tSc, tSc);

            nodeCollector.submitText(
                    poseStack,
                    renderState.priceTextWidth,
                    -4f,
                    renderState.priceQuantity,
                    false,
                    Font.DisplayMode.POLYGON_OFFSET,
                    renderState.lightLevel,
                    0xFFffffff,
                    0,
                    0
            );

            poseStack.popPose();


            //render amount being sold
            poseStack.pushPose();


            poseStack.translate(0.3f, 1.05f, 0.25f);

            poseStack.translate(0.2f, 0f, 0.25f);
            poseStack.mulPose(Axis.YP.rotationDegrees(
                    switch (renderState.direction) {
                        case EAST -> 270f;
                        case SOUTH -> 180f;
                        case WEST -> 90f;
                        default -> 0f;
                    }));
            poseStack.translate(-0.2f, 0f, -0.25f);


            poseStack.mulPose(Axis.ZP.rotationDegrees(180.0f));
            poseStack.mulPose(Axis.XP.rotationDegrees(-67.5f));

            tSc = renderState.smallTextProduct ? 0.014f : 0.018f;

            poseStack.scale(tSc, tSc, tSc);


            nodeCollector.submitText(
                    poseStack,
                    renderState.quantityTextWidth,
                    -4f,
                    renderState.stockQuantity,
                    false,
                    Font.DisplayMode.POLYGON_OFFSET,
                    renderState.lightLevel,
                    0xFFffff00,
                    0,
                    0
            );

            poseStack.popPose();


            //render currency type
            poseStack.pushPose();
            switch (renderState.direction) {
                case NORTH -> {
                    poseStack.translate(0.385f, 0.535f, 0.0525f);
                    poseStack.mulPose(Axis.YP.rotationDegrees(180.0f));
                    poseStack.mulPose(Axis.XP.rotationDegrees(-22.5f));
                }
                case EAST -> {
                    poseStack.translate(0.9475f, 0.535f, 0.385f);
                    poseStack.mulPose(Axis.YP.rotationDegrees(90.0f));
                    poseStack.mulPose(Axis.XP.rotationDegrees(-22.5f));

                }
                case SOUTH -> {
                    poseStack.translate(0.615f, 0.535f, 0.9475);
                    poseStack.mulPose(Axis.XP.rotationDegrees(-22.5f));
                }
                case WEST -> {
                    poseStack.translate(0.0525f, 0.535f, 0.615f);
                    poseStack.mulPose(Axis.YP.rotationDegrees(270.0f));
                    poseStack.mulPose(Axis.XP.rotationDegrees(-22.5f));
                }
            }

            poseStack.scale(0.18f, 0.18f, 0.18f);

            renderState.paymentItem.submit(
                    poseStack, nodeCollector, renderState.lightLevel,
                    OverlayTexture.NO_OVERLAY, 0);

            poseStack.popPose();

            //ShopRenderUtils.renderShopWarns(renderState, poseStack, nodeCollector, cameraRenderState, context, 0.375f);


        }
    }
}