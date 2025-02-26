package net.spudacious5705.shops;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.spudacious5705.shops.block.ModBlocks;
import net.spudacious5705.shops.block.entity.ModBlockEntities;
import net.spudacious5705.shops.block.entity.renderer.ShopBlockEntityRenderer;
import net.spudacious5705.shops.screen.*;

public class SpudaciousShopsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SHOP_BLOCK_BIRCH, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SHOP_BLOCK_OAK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SHOP_BLOCK_WARPED, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SHOP_BLOCK_SPRUCE, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SHOP_BLOCK_CHERRY, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SHOP_BLOCK_MANGROVE, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SHOP_BLOCK_BAMBOO, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SHOP_BLOCK_DARK_OAK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SHOP_BLOCK_CRIMSON, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SHOP_BLOCK_ACACIA, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SHOP_BLOCK_JUNGLE, RenderLayer.getCutout());

        HandledScreens.register(ModScreenHandlers.SHOP_SCREEN_HANDLER_OWNER, ShopScreenOwner::new);
        HandledScreens.register(ModScreenHandlers.SHOP_SCREEN_HANDLER_CUSTOMER, ShopScreenCustomer::new);

        BlockEntityRendererFactories.register(ModBlockEntities.SHOP_ENTITY, ShopBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.SHOP_ENTITY, ShopBlockEntityRenderer::new);

    }
}
