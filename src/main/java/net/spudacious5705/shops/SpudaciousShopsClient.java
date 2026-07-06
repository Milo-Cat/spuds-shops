package net.spudacious5705.shops;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.spudacious5705.shops.block.ModBlockEntities;
import net.spudacious5705.shops.block.ModBlocks;
import net.spudacious5705.shops.block.entity.renderer.*;
import net.spudacious5705.shops.block.resources.CushionModel;
import net.spudacious5705.shops.block.resources.CushionResources;
import net.spudacious5705.shops.block.resources.CushionTextures;
import net.spudacious5705.shops.screen.ModScreenHandlers;
import net.spudacious5705.shops.screen.ScreenResourcesClient;
import net.spudacious5705.shops.screen.ShopScreenCustomer;
import net.spudacious5705.shops.screen.networking.NetworkHelper;
import net.spudacious5705.shops.screen.owner_screen.ShopScreenOwner;

public class SpudaciousShopsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModBlocks.getAllShops().forEach(shop -> BlockRenderLayerMap.INSTANCE.putBlock(shop.get(), RenderType.cutout()));

        CushionTextures.initialiseCushionTextures();
        CushionResources.initialise();

        EntityModelLayerRegistry.registerModelLayer(CushionModel.LAYER_LOCATION, CushionModel::getTexturedModelData);

        MenuScreens.register(ModScreenHandlers.SHOP_SCREEN_HANDLER_OWNER.get(), ShopScreenOwner::new);
        MenuScreens.register(ModScreenHandlers.SHOP_SCREEN_HANDLER_CUSTOMER.get(), ShopScreenCustomer::new);

        BlockEntityRendererRegistry.register(ModBlockEntities.ANGLED_SHOP_ENTITY.get(), AngledShopBlockRenderer::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.WINDOW_SHOP_ENTITY.get(), WindowSillShopEntityRenderer::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.HOOK_SHOP_ENTITY.get(), HookShopEntityRenderer::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.RUG_SHOP_ENTITY.get(), RugShopEntityRenderer::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.CRATE_SHOP_ENTITY.get(), CrateShopEntityRenderer::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.SHELF_SHOP_ENTITY.get(), ShelfShopEntityRenderer::new);

        NetworkHelper.initialiseClient();
        ScreenResourcesClient.init();
    }
}
