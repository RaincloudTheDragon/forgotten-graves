package net.minecraft.data;

import java.util.concurrent.CompletableFuture;

/**
 * Compatibility interface for DataProvider
 */
public interface DataProvider {
    /**
     * Gets the name of this provider
     */
    String getName();
    
    /**
     * Run this provider with the data writer
     */
    CompletableFuture<?> run(DataWriter writer);
    
    /**
     * DataWriter interface for compatibility
     */
    interface DataWriter {
        // Placeholder for compatibility
    }
    
    /**
     * Context interface for compatibility 
     */
    interface Context {
        // Placeholder for compatibility
    }
} 