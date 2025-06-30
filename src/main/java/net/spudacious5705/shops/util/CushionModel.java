// Made with Blockbench 4.12.3
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports

package net.spudacious5705.shops.util;

import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.spudacious5705.shops.SpudaciousShops;

public class CushionModel extends Model {
	public static final EntityModelLayer LAYER_LOCATION = new EntityModelLayer(SpudaciousShops.id("main"), "cushion_model");

	private final ModelPart main;

	public CushionModel(ModelPart root) {
        super(RenderLayer::getEntitySolid);
        this.main = root.getChild("main");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData main = modelPartData.addChild("main", ModelPartBuilder.create(), ModelTransform.of(8.0F, -5.0F, 12.0F, 0.0F, 3.1416F, 0.0F));
		main.addChild("cussion_r1", ModelPartBuilder.create().uv(0, 0).cuboid(-13.0F, 4.0F, -10.0F, 10.0F, 1.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(8.0F, 13.0F, 8.0F, 0.3927F, 0.0F, 0.0F));

		return TexturedModelData.of(modelData, 32, 32);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
		main.render(matrices, vertices, light, overlay);
	}
}