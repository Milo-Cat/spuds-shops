package net.spudacious5705.shops;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.spudacious5705.shops.datagen.*;

public class SpudaciousShopsDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		pack.addProvider(ModBlockTagProvider::new);//runs the datagen classes
		pack.addProvider(ModLootTableProvider::new);
		pack.addProvider(ModRecipieProvider::new);//all the crafting and smelting ect recipies
	}
}
