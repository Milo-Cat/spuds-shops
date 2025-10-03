package net.spudacious5705.shops;

import net.fabricmc.api.ModInitializer;

import net.minecraft.util.Identifier;
import net.spudacious5705.shops.block.ModBlockTags;
import net.spudacious5705.shops.block.ModBlocks;
import net.spudacious5705.shops.block.VariantResources;
import net.spudacious5705.shops.block.entity.ModBlockEntities;
import net.spudacious5705.shops.config.ConfigHandler;
import net.spudacious5705.shops.item.ModItemGroups;
import net.spudacious5705.shops.item.ModItems;
import net.spudacious5705.shops.lootcondition.ModLootConditions;
import net.spudacious5705.shops.properties.ModProperties;
import net.spudacious5705.shops.screen.ModScreenHandlers;
import net.spudacious5705.shops.screenNetworking.NetworkHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SpudaciousShops implements ModInitializer {
	public static final String MOD_ID = "spudaciousshops";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

        ConfigHandler.initialise();
		ModBlockTags.initialise();
        ModProperties.registerModProperties();

		ModBlocks.registerModBlocks();
		ModItems.registerModItems();
		VariantResources.register();

		ModBlockEntities.registerBlockEntities();
		ModScreenHandlers.registerScreenHandlers();

		ModItemGroups.initialise();

		//DebugShopsStatesCommand.register(); //for DEBUG purposes only

		ModLootConditions.registerLootConditions();

		NetworkHelper.initialise();
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}

}