package net.spudacious5705.shops;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;
import net.spudacious5705.shops.block.ModBlockTags;
import net.spudacious5705.shops.block.ModBlocks;
import net.spudacious5705.shops.block.ModBlockEntities;
import net.spudacious5705.shops.block.entity.renderer.ShopIconModels;
import net.spudacious5705.shops.block.resources.VariantResources;
import net.spudacious5705.shops.config.ConfigHandler;
import net.spudacious5705.shops.item.ModItemGroups;
import net.spudacious5705.shops.item.ModItems;
import net.spudacious5705.shops.lootcondition.ModLootConditions;
import net.spudacious5705.shops.properties.ModProperties;
import net.spudacious5705.shops.screen.ModScreenHandlers;
import net.spudacious5705.shops.screen.networking.NetworkHelper;
import net.spudacious5705.shops.util.PostRegAssigner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.spudacious5705.shops.block.ModBlocks.postRegistryTasks;

public class SpudaciousShops implements ModInitializer {
    public static final String MOD_ID = "spudaciousshops";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModItems.registerModItems(null);
        ModBlocks.registerModBlocks(null);

        ModBlockTags.initialise();
        ModProperties.registerModProperties();

        ModBlockEntities.registerBlockEntities(null);
        ModScreenHandlers.registerScreenHandlers(null);

        ModItemGroups.register(null);

        LOGGER.info("Setting up Spud's Shops...");
        ConfigHandler.initialise();

        postRegistryTasks.forEach(Runnable::run);
        PostRegAssigner.runAllAssigners();
        ShopIconModels.initialise();
        VariantResources.register();

        ModLootConditions.registerLootConditions();
        NetworkHelper.initialise();
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
