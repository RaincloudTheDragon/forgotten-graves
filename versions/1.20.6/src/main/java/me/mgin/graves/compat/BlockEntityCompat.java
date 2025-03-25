package me.mgin.graves.compat;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;

/**
 * Compatibility layer for BlockEntity operations that have changed in Minecraft 1.20.5
 */
public class BlockEntityCompat {

    /**
     * Reads NBT data into a BlockEntity, handling API differences between versions
     */
    public static void readNbt(BlockEntity blockEntity, NbtCompound nbt) {
        try {
            // Try to get the registry lookup from the block entity's world
            WrapperLookup registryLookup = getRegistryLookup(blockEntity);
            
            // Use reflection to call the appropriate method based on parameter count
            try {
                // Try the 1.20.5+ method signature first
                blockEntity.getClass().getMethod("readNbt", NbtCompound.class, WrapperLookup.class)
                    .invoke(blockEntity, nbt, registryLookup);
            } catch (NoSuchMethodException e) {
                // Fall back to the pre-1.20.5 method signature
                blockEntity.getClass().getMethod("readNbt", NbtCompound.class)
                    .invoke(blockEntity, nbt);
            }
        } catch (Exception e) {
            // Log the error and fall back to a simple approach
            System.err.println("Error reading NBT data: " + e.getMessage());
            // This is a last resort and may not work correctly
            try {
                blockEntity.getClass().getMethod("readNbt", NbtCompound.class)
                    .invoke(blockEntity, nbt);
            } catch (Exception ex) {
                System.err.println("Failed to read NBT data: " + ex.getMessage());
            }
        }
    }

    /**
     * Writes BlockEntity data to NBT, handling API differences between versions
     */
    public static NbtCompound writeNbt(BlockEntity blockEntity, NbtCompound nbt) {
        try {
            // Try to get the registry lookup from the block entity's world
            WrapperLookup registryLookup = getRegistryLookup(blockEntity);
            
            // Use reflection to call the appropriate method based on parameter count
            try {
                // Try the 1.20.5+ method signature first
                blockEntity.getClass().getMethod("writeNbt", NbtCompound.class, WrapperLookup.class)
                    .invoke(blockEntity, nbt, registryLookup);
            } catch (NoSuchMethodException e) {
                // Fall back to the pre-1.20.5 method signature
                blockEntity.getClass().getMethod("writeNbt", NbtCompound.class)
                    .invoke(blockEntity, nbt);
            }
            return nbt;
        } catch (Exception e) {
            // Log the error and fall back to a simple approach
            System.err.println("Error writing NBT data: " + e.getMessage());
            // This is a last resort and may not work correctly
            try {
                blockEntity.getClass().getMethod("writeNbt", NbtCompound.class)
                    .invoke(blockEntity, nbt);
            } catch (Exception ex) {
                System.err.println("Failed to write NBT data: " + ex.getMessage());
            }
            return nbt;
        }
    }

    /**
     * Creates an NBT compound from a BlockEntity, handling API differences between versions
     */
    public static NbtCompound toNbt(BlockEntity blockEntity) {
        NbtCompound nbt = new NbtCompound();
        return writeNbt(blockEntity, nbt);
    }

    /**
     * Gets the registry lookup from a block entity's world
     */
    private static WrapperLookup getRegistryLookup(BlockEntity blockEntity) {
        if (blockEntity.getWorld() == null) {
            // If the world is null, return a default registry lookup
            // In 1.20.5, we need to use a different approach to get the WrapperLookup
            try {
                // Try to get the EMPTY registry lookup via reflection
                return (WrapperLookup) DynamicRegistryManager.class.getField("EMPTY_WRAPPER_LOOKUP").get(null);
            } catch (Exception e) {
                // If that fails, create a dummy WrapperLookup
                return createDummyWrapperLookup();
            }
        }
        
        try {
            // Try to get the registry manager from the world
            Object registryManager = blockEntity.getWorld().getClass().getMethod("getRegistryManager")
                .invoke(blockEntity.getWorld());
            
            // Try to get the wrapper lookup from the registry manager
            return (WrapperLookup) registryManager.getClass().getMethod("getWrapperLookup")
                .invoke(registryManager);
        } catch (Exception e) {
            // If that fails, create a dummy WrapperLookup
            return createDummyWrapperLookup();
        }
    }
    
    /**
     * Creates a dummy WrapperLookup for compatibility
     */
    private static WrapperLookup createDummyWrapperLookup() {
        try {
            // Try to get the EMPTY registry lookup via reflection
            return (WrapperLookup) DynamicRegistryManager.class.getField("EMPTY_WRAPPER_LOOKUP").get(null);
        } catch (Exception e) {
            // If all else fails, return null and hope for the best
            System.err.println("Failed to create dummy WrapperLookup: " + e.getMessage());
            return null;
        }
    }
} 