package net.spudacious5705.shops;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.spudacious5705.shops.block.ModBlockEntities;
import net.spudacious5705.shops.block.ModBlockTags;
import net.spudacious5705.shops.block.ModBlocks;
import net.spudacious5705.shops.block.entity.renderer.ShopIconModels;
import net.spudacious5705.shops.block.resources.VariantResources;
import net.spudacious5705.shops.config.ConfigHandler;
import net.spudacious5705.shops.item.ModItemGroups;
import net.spudacious5705.shops.item.ModItems;
import net.spudacious5705.shops.properties.ModProperties;
import net.spudacious5705.shops.screen.ModScreenHandlers;
import net.spudacious5705.shops.util.PostRegAssigner;
import org.slf4j.Logger;

import static net.spudacious5705.shops.block.ModBlocks.postRegistryTasks;

@Mod(SpudaciousShops.MOD_ID)
public class SpudaciousShops {
    public static final String MOD_ID = "spudaciousshops";
    public static final Logger LOGGER = LogUtils.getLogger();

    public SpudaciousShops(IEventBus modEventBus, ModContainer modContainer)
    {
        ModItems.registerModItems(modEventBus);
        ModBlocks.registerModBlocks(modEventBus);

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        ModBlockTags.initialise();
        ModProperties.registerModProperties();

        ModBlockEntities.registerBlockEntities(modEventBus);
        ModScreenHandlers.registerScreenHandlers(modEventBus);

        ModItemGroups.register(modEventBus);
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("SETTING UP SPUD'S SHOPS...");

        ConfigHandler.initialise();

        postRegistryTasks.forEach(Runnable::run);
        PostRegAssigner.runAllAssigners();
        ShopIconModels.initialise();
        VariantResources.register();
    }

}