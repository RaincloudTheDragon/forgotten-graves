package me.mgin.graves.compat;

import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import java.lang.reflect.Method;

/**
 * Compatibility layer for World operations that have changed in Minecraft 1.20.5
 */
public class WorldCompat {

    /**
     * Gets the dimension key value from a World
     */
    public static String getDimensionKey(World world) {
        try {
            // Try different method signatures
            try {
                // Try the pre-1.20.5 method signature first
                Method getDimensionKey = world.getClass().getMethod("getDimensionKey");
                Object dimensionKey = getDimensionKey.invoke(world);
                Method getValue = dimensionKey.getClass().getMethod("getValue");
                Object identifier = getValue.invoke(dimensionKey);
                return identifier.toString();
            } catch (NoSuchMethodException e) {
                // Fall back to the 1.20.5+ method signature
                try {
                    // Try to get the dimension directly
                    Method getDimension = world.getClass().getMethod("getDimension");
                    Object dimension = getDimension.invoke(world);
                    
                    // Try to get the identifier from the dimension
                    Method getIdentifier = dimension.getClass().getMethod("getIdentifier");
                    Identifier identifier = (Identifier) getIdentifier.invoke(dimension);
                    return identifier.toString();
                } catch (NoSuchMethodException ex) {
                    // If that fails, try to get the registry key
                    Method getRegistryKey = world.getClass().getMethod("getRegistryKey");
                    Object registryKey = getRegistryKey.invoke(world);
                    Method getValue = registryKey.getClass().getMethod("getValue");
                    Identifier identifier = (Identifier) getValue.invoke(registryKey);
                    return identifier.toString();
                }
            }
        } catch (Exception e) {
            // Log the error and return a safe default
            System.err.println("Error getting dimension key: " + e.getMessage());
            return "minecraft:overworld"; // Default to overworld
        }
    }
} 