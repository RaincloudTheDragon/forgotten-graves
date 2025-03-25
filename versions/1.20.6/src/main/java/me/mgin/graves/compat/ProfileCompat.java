package me.mgin.graves.compat;

import com.mojang.authlib.GameProfile;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import java.lang.reflect.Method;
import java.util.UUID;
import java.lang.reflect.Constructor;

/**
 * Compatibility layer for GameProfile and NbtHelper operations that have changed in Minecraft 1.20.5
 */
public class ProfileCompat {
    // Static flags to prevent repeated error logging
    private static boolean loggedClassNotFound = false;
    private static boolean loggedConstructorError = false;
    private static boolean loggedGeneralError = false;

    /**
     * Converts a GameProfile to NBT
     */
    public static NbtCompound writeGameProfile(NbtCompound nbt, GameProfile profile) {
        try {
            // Try the direct method first (pre-1.20.5)
            try {
                Method writeGameProfile = NbtHelper.class.getMethod("writeGameProfile", NbtCompound.class, GameProfile.class);
                return (NbtCompound) writeGameProfile.invoke(null, nbt, profile);
            } catch (NoSuchMethodException e) {
                // Fall back to manual implementation for 1.20.5+
                if (profile != null) {
                    if (profile.getId() != null) {
                        nbt.putUuid("Id", profile.getId());
                    }
                    if (profile.getName() != null) {
                        nbt.putString("Name", profile.getName());
                    }
                }
                return nbt;
            }
        } catch (Exception e) {
            // Log the error and return the original NBT
            System.err.println("Error writing GameProfile: " + e.getMessage());
            return nbt;
        }
    }

    /**
     * Reads a GameProfile from NBT
     */
    public static GameProfile toGameProfile(NbtCompound nbt) {
        try {
            // Try the direct method first (pre-1.20.5)
            try {
                Method toGameProfile = NbtHelper.class.getMethod("toGameProfile", NbtCompound.class);
                return (GameProfile) toGameProfile.invoke(null, nbt);
            } catch (NoSuchMethodException e) {
                // Fall back to manual implementation for 1.20.5+
                if (nbt.contains("Id") && nbt.contains("Name")) {
                    UUID uuid = nbt.getUuid("Id");
                    String name = nbt.getString("Name");
                    return new GameProfile(uuid, name);
                }
                return null;
            }
        } catch (Exception e) {
            // Log the error and return null
            System.err.println("Error reading GameProfile: " + e.getMessage());
            return null;
        }
    }

    /**
     * Converts a GameProfile to a ProfileComponent (for 1.20.5+)
     * 
     * This method attempts to create a ProfileComponent using the GameProfile's UUID and name.
     * If it fails, it returns null and the caller should handle the fallback case.
     */
    public static Object toProfileComponent(GameProfile profile) {
        if (profile == null || profile.getId() == null) {
            return null;
        }
        
        try {
            // Try to find the ProfileComponent class
            Class<?> profileComponentClass;
            try {
                profileComponentClass = Class.forName("net.minecraft.component.type.ProfileComponent");
            } catch (ClassNotFoundException e) {
                // Class doesn't exist, return null - only log this once
                if (!loggedClassNotFound) {
                    System.err.println("ProfileComponent class not found: " + e.getMessage());
                    loggedClassNotFound = true;
                }
                return null;
            }
            
            // Try the direct constructor with GameProfile - this seems to work based on logs
            try {
                Constructor<?> constructor = profileComponentClass.getConstructor(GameProfile.class);
                Object component = constructor.newInstance(profile);
                return component;
            } catch (Exception e) {
                // Only log this once
                if (!loggedConstructorError) {
                    System.err.println("Error creating ProfileComponent with GameProfile constructor: " + e.getMessage());
                    loggedConstructorError = true;
                }
            }
            
            // If the constructor approach fails, return null without trying other approaches
            // This avoids the flood of error messages
            return null;
            
        } catch (Exception e) {
            // Log the error and return null - only log this once
            if (!loggedGeneralError) {
                System.err.println("Error creating ProfileComponent: " + e.getMessage());
                loggedGeneralError = true;
            }
            return null;
        }
    }
} 