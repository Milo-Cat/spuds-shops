package net.spudacious5705.shops.block.entity.renderer;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.spudacious5705.shops.block.entity.AngledShopEntity;
import net.spudacious5705.shops.util.CushionModel;

public class AngledShopBlockRenderer implements BlockEntityRenderer<AngledShopEntity> {

    private final BlockEntityRendererProvider.Context context;

    private final CushionModel model;

    public AngledShopBlockRenderer(BlockEntityRendererProvider.Context ctx) {
        this.context = ctx;
        this.model = new CushionModel(ctx.bakeLayer(CushionModel.LAYER_LOCATION));
    }


    @Override
    public void render(AngledShopEntity shop, float tickDelta, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay) {
        ItemDisplayContext mode;
        final AngledShopEntity.RendererData data = shop.rendererData();
        Font font = this.context.getFont();

        poseStack.pushPose();
        poseStack.translate(0.5f,0f,0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(
                        switch (shop.getCachedFacingDirection()) {
                            case EAST -> 270f;
                            case SOUTH -> 180f;
                            case WEST -> 90f;
                            default -> 0f;
                        }));
        poseStack.translate(-0.5f,0f,-0.5f);

        this.model.renderToBuffer(
                poseStack,
                bufferSource.getBuffer(RenderType.entitySolid(shop.getCushionTextureID())),
                light,
                overlay,
                1f,1f,1f,1f
        );

        poseStack.popPose();

        if(data == null){
            return;
        }
        data.frameAccumulator();
        if (data.shopFunctional()) {

            //render item being sold
            poseStack.pushPose();
            poseStack.translate(0.5f, 0.955f, 0.5f);

            poseStack.mulPose(Axis.YP.rotationDegrees(data.rotation()));
            poseStack.mulPose(Axis.XP.rotationDegrees(-67.5f));

            if (data.stockDisplayType()) {
                poseStack.scale(0.3f, 0.3f, 0.3f);
                mode = ItemDisplayContext.NONE;
            } else {
                poseStack.scale(0.4f, 0.4f, 0.4f);
                mode = ItemDisplayContext.GUI;
            }
            this.context.getItemRenderer().render(
                    data.displayItem(),
                    mode,
                    false,
                    poseStack,
                    bufferSource,
                    light,
                    overlay,
                    this.context.getItemRenderer().getModel(data.displayItem(), null, null, 0)
            );

            poseStack.popPose();

            //render price (count of currency)
            poseStack.pushPose();



            if(data.direction() == Direction.NORTH){
                poseStack.translate(0.57f, 0.514375f, 0.0525f);
                poseStack.mulPose(Axis.ZP.rotationDegrees(180.0f));
                poseStack.mulPose(Axis.XP.rotationDegrees(-22.5f));
                poseStack.mulPose(Axis.YP.rotationDegrees(0.0f));
            }
            if(data.direction() == Direction.EAST){
                poseStack.translate(0.9475f, 0.514375f, 0.57f);
                poseStack.mulPose(Axis.ZP.rotationDegrees(180.0f));
                poseStack.mulPose(Axis.YP.rotationDegrees(90.0f));
                poseStack.mulPose(Axis.XP.rotationDegrees(-22.5f));

            }
            if(data.direction() == Direction.SOUTH){
                poseStack.translate(0.43f, 0.514375f, 0.9475f);
                poseStack.mulPose(Axis.ZP.rotationDegrees(180.0f));
                poseStack.mulPose(Axis.YP.rotationDegrees(180.0f));
                poseStack.mulPose(Axis.XP.rotationDegrees(-22.5f));
            }
            if(data.direction() == Direction.WEST){
                poseStack.translate(0.0525f, 0.514375f, .43f);
                poseStack.mulPose(Axis.ZP.rotationDegrees(180.0f));
                poseStack.mulPose(Axis.YP.rotationDegrees(270.0f));
                poseStack.mulPose(Axis.XP.rotationDegrees(-22.5f));
            }


            poseStack.scale(0.018f, 0.018f, 0.018f);

            font.drawInBatch(
                    data.text(),
                    data.width(),
                    -4f,
                    0xffffff,
                    false,
                    poseStack.last().pose(),
                    bufferSource,
                    Font.DisplayMode.NORMAL,
                    0,
                    light
            );
            poseStack.popPose();


            //render amount being sold
            poseStack.pushPose();


            poseStack.translate(0.3f, 1.05f, 0.25f);

            poseStack.translate(0.2f, 0f, 0.25f);
            poseStack.mulPose(Axis.YP.rotationDegrees(
                            switch (data.direction()) {
                                case EAST -> 270f;
                                case SOUTH -> 180f;
                                case WEST -> 90f;
                                default -> 0f;
                            }));
            poseStack.translate(-0.2f, 0f, -0.25f);
            

            poseStack.mulPose(Axis.ZP.rotationDegrees(180.0f));
            poseStack.mulPose(Axis.XP.rotationDegrees(-67.5f));


            poseStack.scale(0.018f, 0.018f, 0.018f);

            font.drawInBatch(
                    data.stockQuantity,
                    data.qWidth(),
                    -4f,
                    0xffff00,
                    false,
                    poseStack.last().pose(),
                    bufferSource,
                    Font.DisplayMode.NORMAL,
                    0xffffff,
                    light
            );
            poseStack.popPose();


            //render currency type
            poseStack.pushPose();
            if(data.direction() == Direction.NORTH){
                poseStack.translate(0.385f, 0.535f, 0.0525f);
                poseStack.mulPose(Axis.YP.rotationDegrees(180.0f));
                poseStack.mulPose(Axis.XP.rotationDegrees(-22.5f));
            }
            if(data.direction() == Direction.EAST){
                poseStack.translate(0.9475f, 0.535f,0.385f );
                poseStack.mulPose(Axis.YP.rotationDegrees(90.0f));
                poseStack.mulPose(Axis.XP.rotationDegrees(-22.5f));

            }
            if(data.direction() == Direction.SOUTH){
                poseStack.translate(0.615f, 0.535f, 0.9475);
                poseStack.mulPose(Axis.XP.rotationDegrees(-22.5f));
            }
            if(data.direction() == Direction.WEST){
                poseStack.translate(0.0525f, 0.535f,0.615f );
                poseStack.mulPose(Axis.YP.rotationDegrees(270.0f));
                poseStack.mulPose(Axis.XP.rotationDegrees(-22.5f));
            }
            poseStack.scale(0.18f, 0.18f, 0.18f);
            this.context.getItemRenderer().render(data.paymentType(),
                    ItemDisplayContext.GUI,
                    false,
                    poseStack,
                    bufferSource,
                    light,
                    overlay,
                    this.context.getItemRenderer().getModel(data.displayItem(), null, null, 0));
            poseStack.popPose();

            ShopRenderUtils.renderShopWarns(tickDelta,poseStack,bufferSource,light,overlay,data,context,0.375f);


        }
    }

}