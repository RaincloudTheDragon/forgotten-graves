package net.fabricmc.fabric.api.datagen.v1;

/**
 * Compatibility interface for DataGeneratorEntrypoint
 */
public interface DataGeneratorEntrypoint {
    /**
     * Called when data generation begins
     */
    void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator);
} 