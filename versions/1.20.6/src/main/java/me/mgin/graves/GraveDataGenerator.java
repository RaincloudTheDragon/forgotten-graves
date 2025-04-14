package me.mgin.graves;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import me.mgin.graves.datagen.*;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

/**
 * Entry point for Fabric data generation.
 * This class registers all data generators for the mod.
 */
public class GraveDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        
        // Get an empty registry for compatibility with tag providers
        CompletableFuture<RegistryWrapper.WrapperLookup> lookupFuture = CompletableFuture.completedFuture(null);

        // Register data providers with explicit lambdas to avoid ambiguity
        pack.addProvider((output) -> new GraveBlockTagGenerator(output, lookupFuture));
        pack.addProvider((output) -> new GraveRecipeGenerator(output));
        pack.addProvider((output) -> new GraveItemTagGenerator(output, lookupFuture));
    }
}