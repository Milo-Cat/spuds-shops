package net.spudacious5705.shops.block.entity.renderer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.spudacious5705.shops.block.entity.ShopEntity;
import net.spudacious5705.shops.item.ModItems;

public class ShopBlockEntityRenderer implements BlockEntityRenderer<ShopEntity> {

    private final BlockEntityRendererFactory.Context context;

    public ShopBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.context = ctx;

    }

    @Override
    public void render(ShopEntity shop, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ModelTransformationMode mode;
        ShopEntity.RendererData data = shop.getRendererData();

        data.tickAccumulator(tickDelta);

        if (data.shopFunctional()) {
            matrices.push();
            matrices.translate(0.5f, 0.58f, 0.5f);

            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(data.rotation()));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-67.5f));

            if (data.displayType()) {
                matrices.scale(0.3f, 0.3f, 0.3f);
                mode = ModelTransformationMode.NONE;
            } else {
                matrices.scale(0.4f, 0.4f, 0.4f);
                mode = ModelTransformationMode.GUI;
            }

            this.context.getItemRenderer().renderItem(data.displayItem(), mode, data.lightLevel(), OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, data.world(), 1);
            matrices.pop();
            matrices.push();



            if(data.direction() == Direction.NORTH){
                matrices.translate(0.57f, 0.139375f, 0.0525f);
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0f));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-22.5f));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(0.0f));
                }
            if(data.direction() == Direction.EAST){
                matrices.translate(0.9475f, 0.139375f, 0.57f);
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0f));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0f));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-22.5f));

                }
            if(data.direction() == Direction.SOUTH){
                matrices.translate(0.43f, 0.139375f, 0.9475f);
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0f));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-22.5f));
                }
            if(data.direction() == Direction.WEST){
                matrices.translate(0.0525f, 0.139375f, .43f);
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0f));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(270.0f));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-22.5f));
                }


            matrices.scale(0.018f, 0.018f, 0.018f);

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
            if(data.direction() == Direction.NORTH){
                matrices.translate(0.385f, 0.16f, 0.0525f);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-22.5f));
            }
            if(data.direction() == Direction.EAST){
                matrices.translate(0.9475f, 0.16f,0.385f );
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0f));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-22.5f));

            }
            if(data.direction() == Direction.SOUTH){
                matrices.translate(0.615f, 0.16f, 0.9475);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-22.5f));
            }
            if(data.direction() == Direction.WEST){
                matrices.translate(0.0525f, 0.16f,0.615f );
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(270.0f));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-22.5f));
            }
            matrices.scale(0.18f, 0.18f, 0.18f);
            this.context.getItemRenderer().renderItem(data.paymentType(), ModelTransformationMode.GUI, data.lightLevel(), OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, data.world(), 1);
            matrices.pop();

            if(data.stockWarning || data.paymentWarning){


                if(data.updateIconRotation()) {
                    PlayerEntity player1 = MinecraftClient.getInstance().player;
                    if(player1 != null){
                        double x = player1.getX() - ((double) shop.getPos().getX() + 0.5);
                        double z = player1.getZ() - ((double) shop.getPos().getZ() + 0.5);
                        data.targetRotation = -MathHelper.atan2(z, x);
                    }
                }

                data.frameRotation = (data.targetRotation - data.lastRotation);
                if(data.frameRotation > Math.PI){
                    data.frameRotation -= Math.PI*2;
                } else if (data.frameRotation < -Math.PI){
                    data.frameRotation += Math.PI*2;
                }

                data.frameRotation *= tickDelta*0.08;

                data.lastRotation += data.frameRotation;

                if(data.lastRotation > Math.PI){
                    data.lastRotation -= data.doublePi;
                } else if (data.lastRotation < -Math.PI){
                    data.lastRotation += data.doublePi;
                }

                matrices.push();
                matrices.translate(0.5f, 1.4f, 0.5f);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotation((float)data.lastRotation));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0f));
                matrices.scale(0.5f, 0.5f, 0.5f);

                if(data.stockWarning && data.paymentWarning){


                    matrices.translate(0.5f, 0.0f, 0.0f);

                    this.context.getItemRenderer().renderItem(new ItemStack(ModItems.STOCK_WARNING),ModelTransformationMode.GUI, data.lightLevel(), OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, data.world(), 1);
                    matrices.translate(-1.0f, 0.0f, 0.0f);
                    this.context.getItemRenderer().renderItem(new ItemStack(ModItems.PAYMENT_WARNING),ModelTransformationMode.GUI, data.lightLevel(), OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, data.world(), 1);
                    matrices.pop();
                } else if (data.stockWarning) {
                    this.context.getItemRenderer().renderItem(new ItemStack(ModItems.STOCK_WARNING),ModelTransformationMode.GUI, data.lightLevel(), OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, data.world(), 1);
                    matrices.pop();
                } else {
                    this.context.getItemRenderer().renderItem(new ItemStack(ModItems.PAYMENT_WARNING),ModelTransformationMode.GUI, data.lightLevel(), OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, data.world(), 1);
                    matrices.pop();
                }
            }





        }
    }
}
