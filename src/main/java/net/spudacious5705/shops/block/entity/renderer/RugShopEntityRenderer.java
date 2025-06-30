package net.spudacious5705.shops.block.entity.renderer;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.RotationAxis;
import net.spudacious5705.shops.block.entity.AbstractShopEntity;
import net.spudacious5705.shops.block.entity.RugShopEntity;

import static net.minecraft.util.math.MathHelper.clamp;

public class RugShopEntityRenderer implements BlockEntityRenderer<RugShopEntity>, ShopRenderUtils {

    private final BlockEntityRendererFactory.Context context;

    public RugShopEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.context = ctx;
    }


    @Override
    public void render(RugShopEntity shop, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ModelTransformationMode mode;
        final RugShopEntity.RendererData data = shop.rendererData();
        final RugShopEntity.RugRenderData furtherData = shop.furtherData();
        if(data == null){
            return;
        }

        long currentNanoTime = System.nanoTime();
        float delta = (currentNanoTime-shop.lastNanoTime)*0.00000004f;
        shop.lastNanoTime=currentNanoTime;

        data.frameAccumulator();

        if (data.shopFunctional()) {

            //render item being sold
            matrices.push();

            matrices.translate(0.5f, 0.18f, 0.5f);
            matrices.push();

            if (data.stockDisplayType()) {
                matrices.scale(0.35f, 0.35f, 0.35f);
                mode = ModelTransformationMode.NONE;
            } else {
                matrices.translate(0f, -0.16f, 0f);
                matrices.scale(0.8f, 0.8f, 0.8f);
                mode = ModelTransformationMode.GUI;
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

            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(foo));

            furtherData.itemRotationY = foo;

            rand = (float)(Math.random()*0.05f);
            foo = furtherData.itemRotationX;

            if(furtherData.rotateDirectionX) {
                foo = (foo + delta * (1.25f + rand)) % 360;
            } else{
                foo = (foo - delta * (1.25f + rand)) % 360;
            }

            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(foo));

            furtherData.itemRotationX = foo;

            foo = furtherData.itemRotationSpeedZ;
            foo = (float) clamp(foo+((Math.random())-0.5f)*delta*0.05f,-0.3f,0.3f);
            furtherData.itemRotationSpeedZ = foo;

            foo = (furtherData.itemRotationZ+foo)%360f;

            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(foo));

            furtherData.itemRotationZ = foo;


            matrices.scale(0.8f, 0.8f, 0.8f);

            this.context.getItemRenderer().renderItem(data.displayItem(), mode, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, data.world(), 1);
            matrices.pop();

            //render price (count of currency)
            matrices.push();
            matrices.translate(-0.27f,-0.178f,-0.37f);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-135f));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90f));
            matrices.scale(0.02f, 0.02f, 0.02f);

            this.context.getTextRenderer().draw(
                    data.text(),
                    data.width(),
                    -4f,
                    0xffffff,
                    false,
                    matrices.peek().getPositionMatrix(),
                    vertexConsumers,
                    TextRenderer.TextLayerType.NORMAL,
                    0,
                    light
            );
            matrices.pop();

            matrices.push();
            matrices.translate(0.27f,-0.178f,0.37f);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(45f));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90f));
            matrices.scale(0.02f, 0.02f, 0.02f);

            this.context.getTextRenderer().draw(
                    data.text(),
                    data.width(),
                    -4f,
                    0xffffff,
                    false,
                    matrices.peek().getPositionMatrix(),
                    vertexConsumers,
                    TextRenderer.TextLayerType.NORMAL,
                    0,
                    light
            );
            matrices.pop();


            //render amount being sold


            matrices.push();
            matrices.translate(0.37f,-0.178f,-0.37f);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(135f));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90f));
            matrices.scale(0.02f, 0.02f, 0.02f);

            this.context.getTextRenderer().draw(
                    data.stockQuantity,
                    data.qWidth(),
                    -4f,
                    0xffff00,
                    false,
                    matrices.peek().getPositionMatrix(),
                    vertexConsumers,
                    TextRenderer.TextLayerType.NORMAL,
                    0x000000,
                    light
            );
            matrices.pop();
            matrices.push();
            matrices.translate(-0.37f,-0.178f,0.37f);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-45f));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90f));
            matrices.scale(0.02f, 0.02f, 0.02f);

            this.context.getTextRenderer().draw(
                    data.stockQuantity,
                    data.qWidth(),
                    -4f,
                    0xffff00,
                    false,
                    matrices.peek().getPositionMatrix(),
                    vertexConsumers,
                    TextRenderer.TextLayerType.NORMAL,
                    0x000000,
                    light
            );
            matrices.pop();

            matrices.translate(-0.39f,-0.178f,-0.23f);

            float scale;
            boolean oppType;
            if(data.currencyDisplayType()){
                //for block item
                mode = ModelTransformationMode.NONE;
                scale = 0.16f;
                oppType = true;
            }else{
                //for normal item
                mode = ModelTransformationMode.GUI;
                oppType = false;
                scale = 0.25f;
            }
            //render currency type
            matrices.push();
            if(oppType){
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-45f));
            } else {
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90f));
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-45f));
            }
            matrices.scale(scale, scale, scale);

            this.context.getItemRenderer().renderItem(data.paymentType(), mode, light, overlay, matrices, vertexConsumers, data.world(), 1);
            matrices.pop();

            matrices.push();
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180f),0.39f,0f,0.23f);
            if(oppType){
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-45f));
            } else {
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90f));
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-45f));
            }
            matrices.scale(scale, scale, scale);

            this.context.getItemRenderer().renderItem(data.paymentType(), mode, light, overlay, matrices, vertexConsumers, data.world(), 1);
            matrices.pop();
            matrices.pop();

            ShopRenderUtils.renderShopWarns(tickDelta, matrices, vertexConsumers, light, overlay, data, context, -0.5f);
        }
    }
}
