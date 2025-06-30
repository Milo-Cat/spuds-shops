package net.spudacious5705.shops.block.entity.renderer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.spudacious5705.shops.block.entity.AbstractShopEntity;
import net.spudacious5705.shops.item.ModItems;
import net.spudacious5705.shops.block.entity.AbstractShopEntity.RendererData;

public interface ShopRenderUtils {

    static void renderShopWarns(float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, final RendererData data, BlockEntityRendererFactory.Context context, float yOffset) {
        renderShopWarns(tickDelta, matrices, vertexConsumers, light, overlay, data, context, yOffset, 0.5f);
    }

    static void renderShopWarns(float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, final RendererData data, BlockEntityRendererFactory.Context context, float yOffset, float scale) {
        if(data.shopFunctional()) {
            if (data.stockWarning || data.paymentWarning) {

                if (data.updateIconRotation()) {
                    data.targetRotation = calcTargetRotation(data);
                }

                data.frameRotation = (data.targetRotation - data.lastRotation);
                if (data.frameRotation > Math.PI) {
                    data.frameRotation -= Math.PI * 2;
                } else if (data.frameRotation < -Math.PI) {
                    data.frameRotation += Math.PI * 2;
                }

                data.frameRotation *= tickDelta * 0.08;

                data.lastRotation += data.frameRotation;

                if (data.lastRotation > Math.PI) {
                    data.lastRotation -= data.doublePi;
                } else if (data.lastRotation < -Math.PI) {
                    data.lastRotation += data.doublePi;
                }

                matrices.push();
                matrices.translate(0.5f, 1.4f + yOffset, 0.5f);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotation((float) data.lastRotation));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0f));
                matrices.scale(scale, scale, scale);

                if (data.stockWarning && data.paymentWarning) {


                    matrices.translate(0.5f, 0.0f, 0.0f);

                    context.getItemRenderer().renderItem(new ItemStack(ModItems.STOCK_WARNING), ModelTransformationMode.GUI, light, overlay, matrices, vertexConsumers, data.world(), 1);
                    matrices.translate(-1.0f, 0.0f, 0.0f);
                    context.getItemRenderer().renderItem(new ItemStack(ModItems.PAYMENT_WARNING), ModelTransformationMode.GUI, light, overlay, matrices, vertexConsumers, data.world(), 1);
                    matrices.pop();
                } else if (data.stockWarning) {
                    context.getItemRenderer().renderItem(new ItemStack(ModItems.STOCK_WARNING), ModelTransformationMode.GUI, light, overlay, matrices, vertexConsumers, data.world(), 1);
                    matrices.pop();
                } else {
                    context.getItemRenderer().renderItem(new ItemStack(ModItems.PAYMENT_WARNING), ModelTransformationMode.GUI, light, overlay, matrices, vertexConsumers, data.world(), 1);
                    matrices.pop();
                }
            }
        }

    }

    static double calcTargetRotation(RendererData data) {
        PlayerEntity player1 = MinecraftClient.getInstance().player;
        if (player1 != null) {
            double px = player1.getX();
            double pz = player1.getZ();
            double dx = data.x();
            double dz = data.z();
            double x = px - (dx + 0.5f);
            double z = pz - (dz + 0.5f);
            return -MathHelper.atan2(z, x);
        }
        return data.targetRotation;
    }
}