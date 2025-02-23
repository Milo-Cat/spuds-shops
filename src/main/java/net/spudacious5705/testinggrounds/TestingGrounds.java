package net.spudacious5705.testinggrounds;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.spudacious5705.testinggrounds.block.ModBlocks;
import net.spudacious5705.testinggrounds.block.entity.ModBlockEntities;
import net.spudacious5705.testinggrounds.block.entity.renderer.InscriberBlockEntityRenderer;
import net.spudacious5705.testinggrounds.block.entity.renderer.ShopBlockEntityRenderer;
import net.spudacious5705.testinggrounds.item.ModItemGroups;
import net.spudacious5705.testinggrounds.item.ModItems;
import net.spudacious5705.testinggrounds.screen.InscriberScreen;
import net.spudacious5705.testinggrounds.screen.ModScreenHandlers;
import net.spudacious5705.testinggrounds.screen.StationScreen;
import net.spudacious5705.testinggrounds.screen.StorageScreen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestingGrounds implements ModInitializer {
	public static final String MOD_ID = "testinggrounds";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItemGroups.registerItemGroups();

		ModItems.registerModItems();
		ModBlocks.registerModBlocks();

		ModBlockEntities.registerBlockEntities();
		ModScreenHandlers.registerScreenHandlers();

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

		BlockEntityRendererFactories.register(ModBlockEntities.INSCRIBER_BLOCK_ENTITY, InscriberBlockEntityRenderer::new);
		BlockEntityRendererFactories.register(ModBlockEntities.SHOP_ENTITY, ShopBlockEntityRenderer::new);

	}

}