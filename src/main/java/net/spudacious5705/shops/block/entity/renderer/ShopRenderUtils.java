package net.spudacious5705.shops.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.spudacious5705.shops.block.entity.AbstractShopEntity;

import static java.lang.Math.atan2;

public class ShopRenderUtils {

    /**
     * Called from extractRenderState to interpolate icon rotation toward the player.
     * Modifies data.lastRotation/frameRotation and returns the current rotation as float.
     */
    public static float updateIconRotation(AbstractShopEntity.RendererData data, float partialTick) {
        if (data.updateIconRotation()) {
            data.targetRotation = calcTargetRotation(data);
        }
        data.frameRotation = (data.targetRotation - data.lastRotation);
        if (data.frameRotation > Math.PI) {
            data.frameRotation -= Math.PI * 2;
        } else if (data.frameRotation < -Math.PI) {
            data.frameRotation += Math.PI * 2;
        }
        data.frameRotation *= partialTick * 0.08;
        data.lastRotation += data.frameRotation;
        if (data.lastRotation > Math.PI) {
            data.lastRotation -= data.doublePi;
        } else if (data.lastRotation < -Math.PI) {
            data.lastRotation += data.doublePi;
        }
        return (float) data.lastRotation;
    }

    /** Populate warning icon render states. Call from extractRenderState. */
    public static void extractIconState(BlockEntityRendererProvider.Context context,
                                        ItemStackRenderState noStockState,
                                        ItemStackRenderState regFullState) {
        context.itemModelResolver().updateForTopItem(noStockState, ShopIconModels.NO_STOCK,
                ItemDisplayContext.GUI, null, null, 0);
        context.itemModelResolver().updateForTopItem(regFullState, ShopIconModels.REG_FULL,
                ItemDisplayContext.GUI, null, null, 0);
    }

    /** Render warning icons. Call from submit(). */
    public static void submitShopWarns(PoseStack poseStack, SubmitNodeCollector nodeCollector, int lightLevel,
                                       boolean stockWarning, boolean paymentWarning, boolean renderIcons,
                                       float iconRotation, float yOffset, float scale,
                                       ItemStackRenderState noStockState, ItemStackRenderState regFullState) {
        if (!renderIcons || (!stockWarning && !paymentWarning)) return;

        poseStack.pushPose();
        poseStack.translate(0.5f, 1.4f + yOffset, 0.5f);
        poseStack.mulPose(Axis.YP.rotation(iconRotation));
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0f));
        poseStack.scale(scale, scale, scale);

        if (stockWarning && paymentWarning) {
            poseStack.pushPose();
            poseStack.translate(0.5f, 0.0f, 0.0f);
            noStockState.submit(poseStack, nodeCollector, lightLevel, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
            poseStack.pushPose();
            poseStack.translate(-0.5f, 0.0f, 0.0f);
            regFullState.submit(poseStack, nodeCollector, lightLevel, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        } else if (stockWarning) {
            noStockState.submit(poseStack, nodeCollector, lightLevel, OverlayTexture.NO_OVERLAY, 0);
        } else {
            regFullState.submit(poseStack, nodeCollector, lightLevel, OverlayTexture.NO_OVERLAY, 0);
        }

        poseStack.popPose();
    }

    private static double calcTargetRotation(AbstractShopEntity.RendererData data) {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            double x = player.getX() - (data.x() + 0.5);
            double z = player.getZ() - (data.z() + 0.5);
            return -atan2(z, x);
        }
        return data.targetRotation;
    }
}
