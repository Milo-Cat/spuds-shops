// Made with Blockbench 4.12.3
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports

package net.spudacious5705.shops.block.resources;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.Unit;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import org.jetbrains.annotations.NotNull;

import static net.spudacious5705.shops.SpudaciousShops.id;

public class CushionModel extends Model<Unit> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            id("cushion_model"), "main");


    public CushionModel(ModelPart root) {
        super(root.getChild("main"), RenderTypes::entityCutoutNoCull);
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        PartDefinition main = modelPartData.addOrReplaceChild("main", CubeListBuilder.create(), PartPose.offsetAndRotation(8.0F, -5.0F, 12.0F, 0.0F, 3.1416F, 0.0F));
        main.addOrReplaceChild("cussion_r1", CubeListBuilder.create().addBox(-13.0F, 4.0F, -10.0F, 10.0F, 1.0F, 7.0F), PartPose.offsetAndRotation(8.0F, 13.0F, 8.0F, 0.3927F, 0.0F, 0.0F));

        return LayerDefinition.create(modelData, 32, 32);
    }

    public static void register(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(LAYER_LOCATION, CushionModel::getTexturedModelData);
    }


    public void render(PoseStack matrices, VertexConsumer vertices, int light, int overlay) {
        renderToBuffer(matrices, vertices, light, overlay, 1);
    }
}