package net.spudacious5705.shops;


import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegistryObject;
import net.spudacious5705.shops.block.ModBlockEntities;
import net.spudacious5705.shops.block.ModBlocks;
import net.spudacious5705.shops.block.custom.AbstractShopBlock;
import net.spudacious5705.shops.block.entity.renderer.*;
import net.spudacious5705.shops.block.resources.CushionModel;
import net.spudacious5705.shops.block.resources.CushionResources;
import net.spudacious5705.shops.block.resources.CushionTextures;
import net.spudacious5705.shops.screen.ModScreenHandlers;

import static net.spudacious5705.shops.SpudaciousShops.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class SpudaciousShopsClient{

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event){



        event.enqueueWork(//() -> {

            ModScreenHandlers::registerScreens

            /*ModBlocks.getAllShops().forEach((RegistryObject<? extends AbstractShopBlock> shop) -> {
                ItemBlockRenderTypes.setRenderLayer(shop.get(), RenderType.translucent());
            });
        }*/
        );


        CushionTextures.initialiseCushionTextures();
        CushionResources.initialise();

        

    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.ANGLED_SHOP_ENTITY.get(), AngledShopBlockRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.WINDOW_SHOP_ENTITY.get(), WindowSillShopEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.HOOK_SHOP_ENTITY.get(), HookShopEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.RUG_SHOP_ENTITY.get(), RugShopEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.CRATE_SHOP_ENTITY.get(), CrateShopEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.SHELF_SHOP_ENTITY.get(), ShelfShopEntityRenderer::new);
    }
    

    @SubscribeEvent
    public static void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(CushionModel.LAYER_LOCATION, CushionModel::getTexturedModelData);
    }
}
