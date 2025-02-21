package net.spudacious5705.testinggrounds;

import net.fabricmc.api.ModInitializer;

import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.spudacious5705.testinggrounds.block.ModBlocks;
import net.spudacious5705.testinggrounds.block.entity.ModBlockEntities;
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


	}

}