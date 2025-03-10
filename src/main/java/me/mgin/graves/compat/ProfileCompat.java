package me.mgin.graves.compat;

import com.mojang.authlib.GameProfile;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.Map;
import java.lang.reflect.Constructor;

/**
 * Compatibility layer for GameProfile and NbtHelper operations that have changed in Minecraft 1.20.5
 */
public class ProfileCompat {

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
                // Class doesn't exist, return null
                System.err.println("ProfileComponent class not found: " + e.getMessage());
                return null;
            }
            
            // Try to find the create method (1.20.5 specific)
            try {
                Method createMethod = profileComponentClass.getDeclaredMethod("create", UUID.class, String.class);
                createMethod.setAccessible(true);
                Object component = createMethod.invoke(null, profile.getId(), profile.getName());
                
                if (component != null) {
                    System.out.println("Successfully created ProfileComponent using create(UUID, String) method");
                    return component;
                }
            } catch (Exception e) {
                System.err.println("Error with create(UUID, String) method: " + e.getMessage());
            }
            
            // Try to find the static factory method (1.20.5 specific)
            try {
                Method factoryMethod = profileComponentClass.getDeclaredMethod("factory");
                factoryMethod.setAccessible(true);
                Object factory = factoryMethod.invoke(null);
                
                if (factory != null) {
                    // Try to find the create method on the factory
                    Method createMethod = factory.getClass().getDeclaredMethod("create", UUID.class, String.class);
                    createMethod.setAccessible(true);
                    Object component = createMethod.invoke(factory, profile.getId(), profile.getName());
                    
                    if (component != null) {
                        System.out.println("Successfully created ProfileComponent using factory().create(UUID, String) method");
                        return component;
                    }
                }
            } catch (Exception e) {
                System.err.println("Error with factory method: " + e.getMessage());
            }
            
            // Based on the original code, try the direct constructor with GameProfile
            try {
                Constructor<?> constructor = profileComponentClass.getConstructor(GameProfile.class);
                Object component = constructor.newInstance(profile);
                System.out.println("Successfully created ProfileComponent using GameProfile constructor");
                return component;
            } catch (NoSuchMethodException e) {
                System.err.println("ProfileComponent(GameProfile) constructor not found: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Error creating ProfileComponent with GameProfile constructor: " + e.getMessage());
            }
            
            // Approach 1: Try to use the static factory method of(UUID, String)
            try {
                Method ofMethod = profileComponentClass.getDeclaredMethod("of", UUID.class, String.class);
                ofMethod.setAccessible(true);
                Object component = ofMethod.invoke(null, profile.getId(), profile.getName());
                
                if (component != null) {
                    System.out.println("Successfully created ProfileComponent using of(UUID, String) method");
                    return component;
                }
            } catch (Exception e) {
                System.err.println("Error with of(UUID, String) method: " + e.getMessage());
            }
            
            // Approach 2: Try to use the static factory method of(GameProfile)
            try {
                Method ofMethod = profileComponentClass.getDeclaredMethod("of", GameProfile.class);
                ofMethod.setAccessible(true);
                Object component = ofMethod.invoke(null, profile);
                
                if (component != null) {
                    System.out.println("Successfully created ProfileComponent using of(GameProfile) method");
                    return component;
                }
            } catch (Exception e) {
                System.err.println("Error with of(GameProfile) method: " + e.getMessage());
            }
            
            // Approach 3: Try to use the constructor with UUID and String
            try {
                Constructor<?> constructor = profileComponentClass.getDeclaredConstructor(UUID.class, String.class);
                constructor.setAccessible(true);
                Object component = constructor.newInstance(profile.getId(), profile.getName());
                
                if (component != null) {
                    System.out.println("Successfully created ProfileComponent using UUID, String constructor");
                    return component;
                }
            } catch (Exception e) {
                System.err.println("Error with UUID, String constructor: " + e.getMessage());
            }
            
            // If all approaches fail, return null
            System.err.println("All approaches to create ProfileComponent failed");
            return null;
        } catch (Exception e) {
            // Log the error and return null
            System.err.println("Error creating ProfileComponent: " + e.getMessage());
            return null;
        }
    }
} 