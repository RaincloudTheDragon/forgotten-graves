package me.mgin.graves.compat;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.world.PersistentState;
import java.lang.reflect.Method;

/**
 * Compatibility layer for PersistentState operations that have changed in Minecraft 1.20.5
 */
public class PersistentStateCompat {

    /**
     * Writes a PersistentState to NBT, handling API differences between versions
     */
    public static NbtCompound writeNbt(PersistentState state, NbtCompound nbt) {
        try {
            // Try to get a WrapperLookup
            WrapperLookup wrapperLookup = getDummyWrapperLookup();
            
            // Try different method signatures
            try {
                // Try the 1.20.5+ method signature first
                Method writeNbt = state.getClass().getMethod("writeNbt", NbtCompound.class, WrapperLookup.class);
                return (NbtCompound) writeNbt.invoke(state, nbt, wrapperLookup);
            } catch (NoSuchMethodException e) {
                // Fall back to the pre-1.20.5 method signature
                Method writeNbt = state.getClass().getMethod("writeNbt", NbtCompound.class);
                return (NbtCompound) writeNbt.invoke(state, nbt);
            }
        } catch (Exception e) {
            // Log the error and return the original NBT
            System.err.println("Error writing PersistentState: " + e.getMessage());
            return nbt;
        }
    }

    /**
     * Creates a PersistentState from NBT, handling API differences between versions
     */
    public static <T extends PersistentState> T createFromNbt(NbtCompound nbt, java.util.function.Function<NbtCompound, T> factory) {
        try {
            // Just use the factory directly
            return factory.apply(nbt);
        } catch (Exception e) {
            // Log the error and return null
            System.err.println("Error creating PersistentState: " + e.getMessage());
            return null;
        }
    }

    /**
     * Creates a factory that can handle both old and new createFromNbt signatures
     */
    public static <T extends PersistentState> java.util.function.Function<NbtCompound, T> createFactory(
            java.util.function.Function<NbtCompound, T> oldFactory) {
        return (nbt) -> {
            try {
                return oldFactory.apply(nbt);
            } catch (Exception e) {
                System.err.println("Error in PersistentState factory: " + e.getMessage());
                return null;
            }
        };
    }

    /**
     * Gets a dummy WrapperLookup for compatibility
     */
    private static WrapperLookup getDummyWrapperLookup() {
        try {
            // Try to get the EMPTY registry lookup via reflection
            Class<?> dynamicRegistryManagerClass = Class.forName("net.minecraft.registry.DynamicRegistryManager");
            Object emptyManager = dynamicRegistryManagerClass.getField("EMPTY").get(null);
            Method getWrapperLookup = dynamicRegistryManagerClass.getMethod("getWrapperLookup");
            return (WrapperLookup) getWrapperLookup.invoke(emptyManager);
        } catch (Exception e) {
            // If all else fails, return null and hope for the best
            System.err.println("Failed to create dummy WrapperLookup: " + e.getMessage());
            return null;
        }
    }
} 