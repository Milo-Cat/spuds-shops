package net.spudacious5705.shops.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.spudacious5705.shops.block.entity.RugShopEntity;

import static net.minecraft.util.Mth.clamp;


public class RugShopEntityRenderer implements BlockEntityRenderer<RugShopEntity>, ShopRenderUtils {

    private final BlockEntityRendererProvider.Context context;

    public RugShopEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        this.context = ctx;
    }


    @Override
    public void render(RugShopEntity shop, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        ItemDisplayContext mode;
        final RugShopEntity.RendererData data = shop.rendererData();
        final RugShopEntity.RugRenderData furtherData = shop.furtherData();
        Font font = this.context.getFont();
        if(data == null){
            return;
        }

        long currentNanoTime = System.nanoTime();
        float delta = (currentNanoTime-shop.lastNanoTime)*0.00000004f;
        shop.lastNanoTime=currentNanoTime;

        data.frameAccumulator();

        if (data.shopFunctional()) {

            //render item being sold
            matrices.pushPose();

            matrices.translate(0.5f, 0.18f, 0.5f);
            matrices.pushPose();

            if (data.stockDisplayType()) {
                matrices.scale(0.35f, 0.35f, 0.35f);
                mode = ItemDisplayContext.NONE;
            } else {
                matrices.translate(0f, -0.16f, 0f);
                matrices.scale(0.8f, 0.8f, 0.8f);
                mode = ItemDisplayContext.GUI;
            }
            matrices.translate(0f, 0.5f, 0f);


            float foo = furtherData.itemHeight;

            float rand = (float)(Math.random() * 0.02f);
            foo = (foo + delta*(0.02f+rand)) % 6.28318530718f ;



            matrices.translate(0f, Math.sin(foo)*0.15f, 0f);

            furtherData.itemHeight = foo;

            rand = (float)(Math.random() * 0.03);

            foo = furtherData.itemRotationY;
            if(furtherData.rotateDirectionY) {
                foo = (foo + delta * (0.2f + rand)) % 360;
            } else{
                foo = (foo - delta * (0.2f + rand)) % 360;
            }

            matrices.mulPose(Axis.YP.rotationDegrees(foo));

            furtherData.itemRotationY = foo;

            rand = (float)(Math.random()*0.05f);
            foo = furtherData.itemRotationX;

            if(furtherData.rotateDirectionX) {
                foo = (foo + delta * (1.25f + rand)) % 360;
            } else{
                foo = (foo - delta * (1.25f + rand)) % 360;
            }

            matrices.mulPose(Axis.XP.rotationDegrees(foo));

            furtherData.itemRotationX = foo;

            foo = furtherData.itemRotationSpeedZ;
            foo = (float) clamp(foo+((Math.random())-0.5f)*delta*0.05f,-0.3f,0.3f);
            furtherData.itemRotationSpeedZ = foo;

            foo = (furtherData.itemRotationZ+foo)%360f;

            matrices.mulPose(Axis.ZP.rotationDegrees(foo));

            furtherData.itemRotationZ = foo;


            matrices.scale(0.8f, 0.8f, 0.8f);

            this.context.getItemRenderer().render(data.displayItem(), mode,
                    false,
                    matrices,
                    vertexConsumers,
                    light,
                    overlay,
                    this.context.getItemRenderer().getModel(data.displayItem(), null, null, 0)
            );
            matrices.popPose();

            //render price (count of currency)
            matrices.pushPose();
            matrices.translate(-0.27f,-0.178f,-0.37f);
            matrices.mulPose(Axis.YP.rotationDegrees(-135f));
            matrices.mulPose(Axis.XP.rotationDegrees(90f));
            matrices.scale(0.02f, 0.02f, 0.02f);

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
            matrices.translate(0.27f,-0.178f,0.37f);
            matrices.mulPose(Axis.YP.rotationDegrees(45f));
            matrices.mulPose(Axis.XP.rotationDegrees(90f));
            matrices.scale(0.02f, 0.02f, 0.02f);

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
            matrices.translate(0.37f,-0.178f,-0.37f);
            matrices.mulPose(Axis.YP.rotationDegrees(135f));
            matrices.mulPose(Axis.XP.rotationDegrees(90f));
            matrices.scale(0.02f, 0.02f, 0.02f);

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
            matrices.translate(-0.37f,-0.178f,0.37f);
            matrices.mulPose(Axis.YP.rotationDegrees(-45f));
            matrices.mulPose(Axis.XP.rotationDegrees(90f));
            matrices.scale(0.02f, 0.02f, 0.02f);

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

            matrices.translate(-0.39f,-0.178f,-0.23f);

            float scale;
            boolean oppType;
            if(data.currencyDisplayType()){
                //for block item
                mode = ItemDisplayContext.NONE;
                scale = 0.16f;
                oppType = true;
            }else{
                //for normal item
                mode = ItemDisplayContext.GUI;
                oppType = false;
                scale = 0.25f;
            }
            //render currency type
            matrices.pushPose();
            if(oppType){
                matrices.mulPose(Axis.YP.rotationDegrees(-45f));
            } else {
                matrices.mulPose(Axis.XP.rotationDegrees(90f));
                matrices.mulPose(Axis.ZP.rotationDegrees(-45f));
            }
            matrices.scale(scale, scale, scale);

            this.context.getItemRenderer().render(data.paymentType(), mode,
                    false,
                    matrices,
                    vertexConsumers,
                    light,
                    overlay,
                    this.context.getItemRenderer().getModel(data.displayItem(), null, null, 0)
            );
            matrices.popPose();

            matrices.pushPose();
            matrices.translate(0.39f, 0.3f, 0.23f);
            matrices.mulPose(Axis.YP.rotationDegrees(180f));
            matrices.translate(-0.39f, -0.3f, -0.23f);
            if(oppType){
                matrices.mulPose(Axis.YP.rotationDegrees(-45f));
            } else {
                matrices.mulPose(Axis.XP.rotationDegrees(90f));
                matrices.mulPose(Axis.ZP.rotationDegrees(-45f));
            }
            matrices.scale(scale, scale, scale);

            this.context.getItemRenderer().render(data.paymentType(), mode,
                    false,
                    matrices,
                    vertexConsumers,
                    light,
                    overlay,
                    this.context.getItemRenderer().getModel(data.displayItem(), null, null, 0)
            );
            matrices.popPose();
            matrices.popPose();

            ShopRenderUtils.renderShopWarns(tickDelta, matrices, vertexConsumers, light, overlay, data, context, -0.5f);
        }
    }
}
