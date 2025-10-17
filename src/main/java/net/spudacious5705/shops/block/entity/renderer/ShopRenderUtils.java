package net.spudacious5705.shops.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.spudacious5705.shops.block.entity.AbstractShopEntity.RendererData;

import static java.lang.Math.atan2;
import static net.spudacious5705.shops.block.entity.renderer.ShopIconModels.NO_STOCK;
import static net.spudacious5705.shops.block.entity.renderer.ShopIconModels.REG_FULL;

public interface ShopRenderUtils {

    static void renderShopWarns(float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay, final RendererData data, BlockEntityRendererProvider.Context context, float yOffset) {
        renderShopWarns(tickDelta, matrices, vertexConsumers, light, overlay, data, context, yOffset, 0.5f);
    }

    static void renderShopWarns(float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay, final RendererData data, BlockEntityRendererProvider.Context context, float yOffset, float scale) {
        if(data.shopFunctional() && data.renderIcons()) {
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

                matrices.pushPose();
                matrices.translate(0.5f, 1.4f + yOffset, 0.5f);
                matrices.mulPose(Axis.YP.rotation((float) data.lastRotation));
                matrices.mulPose(Axis.YP.rotationDegrees(90.0f));
                matrices.scale(scale, scale, scale);

                if (data.stockWarning && data.paymentWarning) {


                    matrices.translate(0.5f, 0.0f, 0.0f);

                    context.getItemRenderer().render(NO_STOCK, ItemDisplayContext.GUI,
                            false,
                            matrices,
                            vertexConsumers,
                            light,
                            overlay,
                            context.getItemRenderer().getModel(NO_STOCK, null, null, 0)
                    );
                    matrices.translate(-1.0f, 0.0f, 0.0f);
                    context.getItemRenderer().render(REG_FULL, ItemDisplayContext.GUI,
                            false,
                            matrices,
                            vertexConsumers,
                            light,
                            overlay,
                            context.getItemRenderer().getModel(REG_FULL, null, null, 0)
                    );
                    matrices.popPose();
                } else if (data.stockWarning) {
                    context.getItemRenderer().render(NO_STOCK, ItemDisplayContext.GUI,
                            false,
                            matrices,
                            vertexConsumers,
                            light,
                            overlay,
                            context.getItemRenderer().getModel(NO_STOCK, null, null, 0)
                    );
                    matrices.popPose();
                } else {
                    context.getItemRenderer().render(REG_FULL, ItemDisplayContext.GUI,
                            false,
                            matrices,
                            vertexConsumers,
                            light,
                            overlay,
                            context.getItemRenderer().getModel(REG_FULL, null, null, 0)
                    );
                    matrices.popPose();
                }
            }
        }

    }

    static double calcTargetRotation(RendererData data) {
        Player player1 = Minecraft.getInstance().player;
        if (player1 != null) {
            double px = player1.getX();
            double pz = player1.getZ();
            double dx = data.x();
            double dz = data.z();
            double x = px - (dx + 0.5f);
            double z = pz - (dz + 0.5f);
            return -atan2(z, x);
        }
        return data.targetRotation;
    }
}