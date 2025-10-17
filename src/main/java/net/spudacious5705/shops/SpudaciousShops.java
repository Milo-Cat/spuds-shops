package net.spudacious5705.shops;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.spudacious5705.shops.block.*;
import net.spudacious5705.shops.block.entity.renderer.ShopIconModels;
import net.spudacious5705.shops.config.ConfigHandler;
import net.spudacious5705.shops.item.ModItemGroups;
import net.spudacious5705.shops.item.ModItems;
import net.spudacious5705.shops.properties.ModProperties;
import net.spudacious5705.shops.screen.ModScreenHandlers;
import net.spudacious5705.shops.screen.networking.NetworkHelper;
import org.slf4j.Logger;

import static net.spudacious5705.shops.block.ModBlocks.postRegistryTasks;

//import net.spudacious5705.shops.command.DebugShopsStatesCommand;

@Mod(SpudaciousShops.MOD_ID)
public class SpudaciousShops{
	public static final String MOD_ID = "spudaciousshops";
	public static final Logger LOGGER = LogUtils.getLogger();

	public SpudaciousShops(FMLJavaModLoadingContext context) {

        IEventBus modEventBus = context.getModEventBus();

        ConfigHandler.initialise();

        ModItems.registerModItems(modEventBus);
		ModBlocks.registerModBlocks(modEventBus);

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        ModBlockTags.initialise();
        ModProperties.registerModProperties();

		ModBlockEntities.registerBlockEntities(modEventBus);
		ModScreenHandlers.registerScreenHandlers(modEventBus);

		ModItemGroups.register(modEventBus);

		//DebugShopsStatesCommand.register(); //for DEBUG purposes only

		NetworkHelper.register();
	}

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID,path);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        LOGGER.info("SETTING UP SPUD'S SHOPS...");

        postRegistryTasks.forEach(Runnable::run);
        PostRegAssigner.runAllAssigners();
        ShopIconModels.initialise();
        VariantResources.register();
        //Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    public static ResourceLocation getResource(String path){
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }



}