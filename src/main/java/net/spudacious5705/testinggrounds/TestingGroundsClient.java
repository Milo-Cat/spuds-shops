package net.spudacious5705.testinggrounds;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.spudacious5705.testinggrounds.block.ModBlocks;
import net.spudacious5705.testinggrounds.block.entity.ModBlockEntities;
import net.spudacious5705.testinggrounds.block.entity.renderer.InscriberBlockEntityRenderer;
import net.spudacious5705.testinggrounds.block.entity.renderer.ShopBlockEntityRenderer;

public class TestingGroundsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

    }
}
