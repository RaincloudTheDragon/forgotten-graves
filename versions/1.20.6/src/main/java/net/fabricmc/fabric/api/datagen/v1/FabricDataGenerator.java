package net.fabricmc.fabric.api.datagen.v1;

import net.minecraft.data.DataProvider;

/**
 * Compatibility class for FabricDataGenerator
 */
public class FabricDataGenerator {
    /**
     * Create a pack
     */
    public Pack createPack() {
        return new Pack();
    }
    
    /**
     * Pack class for compatibility
     */
    public static class Pack {
        /**
         * Add a provider to the pack
         */
        public <T extends DataProvider> Pack addProvider(Factory<T> factory) {
            // Placeholder implementation
            return this;
        }
        
        /**
         * Factory interface for creating data providers
         */
        @FunctionalInterface
        public interface Factory<T extends DataProvider> {
            T create(FabricDataOutput output);
        }
    }
} 