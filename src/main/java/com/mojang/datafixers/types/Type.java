package com.mojang.datafixers.types;

/**
 * Temporary dummy implementation of the Type interface for 1.20.5
 * This allows compilation to succeed until the proper API is available
 * 
 * Note: This will be replaced by the actual Mojang implementation
 * when available in the final API.
 */
public interface Type<T> {
    /** 
     * A dummy implementation that can be used for BlockEntityType.Builder.build()
     */
    static <T> Type<T> dummy() {
        return new Type<T>() {};
    }
    
    /** 
     * Convenience constant for BlockEntityType.Builder.build()
     */
    static final Type<?> DUMMY = dummy();
} 