package me.mgin.graves;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import me.mgin.graves.datagen.*;

/**
 * Entry point for Fabric data generation.
 * This class registers all data generators for the mod.
 */
public class GraveDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        // Register data providers
        pack.addProvider(GraveBlockTagGenerator::new);
        pack.addProvider(GraveRecipeGenerator::new);
        pack.addProvider(GraveItemTagGenerator::new);
    }
}