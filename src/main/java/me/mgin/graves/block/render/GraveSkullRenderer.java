package me.mgin.graves.block.render;

import com.mojang.authlib.GameProfile;
import me.mgin.graves.block.GraveBlockBase;
import me.mgin.graves.block.entity.GraveBlockEntity;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.SkullBlock;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.SkullEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import me.mgin.graves.compat.ProfileCompat;
import net.minecraft.util.Identifier;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.lang.reflect.Field;
import java.lang.reflect.Constructor;

//? if >1.20.5 {
/*import net.minecraft.component.type.ProfileComponent;
*///?} else {
import net.minecraft.nbt.NbtHelper;
//?}

public class GraveSkullRenderer {
    private final EntityModelLoader skullRenderer;

    public GraveSkullRenderer(EntityModelLoader modelLoader) {
        this.skullRenderer = modelLoader;
    }

    public void render(GraveBlockEntity graveEntity, MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                       Direction direction, BlockState state, int light) {
        int decayStage = ((GraveBlockBase) state.getBlock()).getDecayStage().ordinal();

        matrices.push();

        // Set scale and raise the skull up 1/16th of a block
        matrices.scale(0.75f, 0.75f, 0.75f);
        matrices.translate(0, 0.08f, 0);

        // Rotate the skull based on the direction
        rotateSkull(direction, matrices, decayStage);

        // render the skull
        renderSkull(graveEntity, skullRenderer, decayStage, matrices, light, vertexConsumers);

        matrices.pop();
    }

    private void rotateSkull(Direction direction, MatrixStack matrices, int decayStage) {
        switch (direction) {
            case NORTH:
                // 180 deg (Y)
                matrices.multiply(RotationAxis.POSITIVE_Y.rotation(3.14159265f));
                matrices.translate(-1.2, 0.25 - (decayStage * 0.03), -0.99);
                break;
            case SOUTH:
                matrices.translate(0.15, 0.25 - (decayStage * 0.03), 0.34);
                break;
            case EAST:
                // 90 deg (Y)
                matrices.multiply(RotationAxis.POSITIVE_Y.rotation(1.57079633f));
                matrices.translate(-1.2, 0.25 - (decayStage * 0.03), 0.34);
                break;
            case WEST:
                // 270 deg (Y)
                matrices.multiply(RotationAxis.POSITIVE_Y.rotation(4.71239f));
                matrices.translate(0.15, 0.25 - (decayStage * 0.03), -0.99);
                break;
            case UP:
            case DOWN:
                break;
        }

        // 50 deg (X)
        matrices.multiply(RotationAxis.POSITIVE_X.rotation(0.872665f));
    }

    public static void renderSkull(GraveBlockEntity graveEntity, EntityModelLoader modelLoader, int blockAge,
                                   MatrixStack matrices, int light, VertexConsumerProvider vertexConsumers) {
        GameProfile profile = null;
        SkullWrapper skullData = null;
        float yaw = Float.max(10f, blockAge * 12f);

        // Handle player-owned grave skulls
        if (graveEntity.getGraveOwner() != null) {
            profile = graveEntity.getGraveOwner();
            skullData = skulls.get(blockAge >= 2 ? "skeleton_skull" : "player_head");
        }

        // Handle custom grave skulls
        else if (graveEntity.hasGraveSkull()) {
            NbtCompound graveSkull = graveEntity.getGraveSkull();
            String graveSkullValue = graveSkull.getString("Value");

            // Handle non-custom heads (like skeleton, wither skeleton, zombie, creeper, etc).
            // This is set to the item name in Skull.java's handle method.
            if (skulls.containsKey(graveSkullValue)) {
                skullData = skulls.get(graveSkullValue);
            }

            // Handle custom heads (creates a custom profile)
            else {
                profile = getCustomSkullProfile(graveSkull);
                skullData = skulls.get("player_head");
            }
        }

        // Render the skull
        if (skullData != null) {
            SkullBlockEntityModel model = getSkullModel(skullData.model(), modelLoader);
            
            // Get the grave's position for logging
            BlockPos gravePos = graveEntity.getPos();
            String graveLocation = gravePos.getX() + "," + gravePos.getY() + "," + gravePos.getZ();
            
            // Pass the grave location to getSkullLayer for more specific logging
            RenderLayer layer = getSkullLayer(skullData.type(), profile, graveLocation);

            try {
                // Use the same yaw parameter as the original code
                SkullBlockEntityRenderer.renderSkull(
                    null, yaw, 0f, matrices, vertexConsumers, light, model, layer
                );
            } catch (Exception e) {
                // Log the error
                System.err.println("Error rendering skull: " + e.getMessage());
                
                // Try a simpler approach if the first one fails
                try {
                    SkullBlockEntityRenderer.renderSkull(
                        null, 0f, 0f, matrices, vertexConsumers, light, model, layer
                    );
                } catch (Exception ex) {
                    System.err.println("Failed to render skull even with simpler approach: " + ex.getMessage());
                }
            }
        }
    }

    /**
     * Contains information about the skull and its model layers.
     */
    public static Map<String, SkullWrapper> skulls = new HashMap<>() {
        {
            put("wither_skeleton_skull",
                new SkullWrapper(SkullBlock.Type.WITHER_SKELETON, EntityModelLayers.WITHER_SKELETON_SKULL));
            put("skeleton_skull",
                new SkullWrapper(SkullBlock.Type.SKELETON, EntityModelLayers.SKELETON_SKULL));
            put("player_head",
                new SkullWrapper(SkullBlock.Type.PLAYER, EntityModelLayers.PLAYER_HEAD));
            put("zombie_head",
                new SkullWrapper(SkullBlock.Type.ZOMBIE, EntityModelLayers.ZOMBIE_HEAD));
            put("creeper_head",
                new SkullWrapper(SkullBlock.Type.CREEPER, EntityModelLayers.CREEPER_HEAD));
            //? if >=1.20 {
            put("piglin_head",
                new SkullWrapper(SkullBlock.Type.PIGLIN, EntityModelLayers.PIGLIN_HEAD));
            //?}
        }
    };

    /**
     * Generates a new SkullBlockEntityModel based on the given model.
     */
    public static SkullBlockEntityModel getSkullModel(EntityModelLayer model, EntityModelLoader modelLoader) {
        SkullBlockEntityModel skull = new SkullEntityModel(modelLoader.getModelPart(model));
        skull.setHeadRotation(1f, 2f, 2f);
        return skull;
    }

    /**
     * Generate a RenderLayer for the given SkullType.
     */
    public static RenderLayer getSkullLayer(SkullBlock.SkullType skullType, @Nullable GameProfile profile) {
        return getSkullLayer(skullType, profile, null);
    }

    /**
     * Generate a RenderLayer for the given SkullType with additional grave location info for logging.
     */
    public static RenderLayer getSkullLayer(SkullBlock.SkullType skullType, @Nullable GameProfile profile, @Nullable String graveLocation) {
        // Run debug once
        if (!loggedProfiles.contains("debug-run")) {
            debugSkinTextures();
            loggedProfiles.add("debug-run");
        }
        
        // For non-player skulls or null profiles, use the standard method
        if (skullType != SkullBlock.Type.PLAYER || profile == null) {
            try {
                Method getRenderLayerMethod = SkullBlockEntityRenderer.class
                    .getMethod("getRenderLayer", SkullBlock.SkullType.class);
                return (RenderLayer) getRenderLayerMethod.invoke(null, skullType);
            } catch (Exception e) {
                return getDefaultSkullTexture(skullType);
            }
        }
        
        // Log player info once per grave to help with debugging
        if (profile.getId() != null) {
            String playerName = profile.getName() != null ? profile.getName() : "Unknown";
            
            String cacheKey = playerName + "-" + profile.getId();
            
            if (graveLocation != null) {
                cacheKey += "-" + graveLocation;
            }
            
            if (!loggedProfiles.contains(cacheKey)) {
                boolean isSlim = isSlimModel(profile.getId());
                System.out.println("Grave skull info - Player: " + playerName + 
                                  ", UUID: " + profile.getId() + 
                                  ", isSlim: " + isSlim + 
                                  ", Location: " + (graveLocation != null ? graveLocation : "unknown"));
                loggedProfiles.add(cacheKey);
            }
        }
        
        // Try the new approach for 1.20.5 using our ProfileCompat helper
        try {
            Object profileComponent = ProfileCompat.toProfileComponent(profile);
            
            if (profileComponent != null) {
                // Use the ProfileComponent with SkullBlockEntityRenderer
                try {
                    Class<?> profileComponentClass = Class.forName("net.minecraft.component.type.ProfileComponent");
                    Method getRenderLayerMethod = SkullBlockEntityRenderer.class
                        .getMethod("getRenderLayer", SkullBlock.SkullType.class, profileComponentClass);
                    return (RenderLayer) getRenderLayerMethod.invoke(null, skullType, profileComponent);
                } catch (Exception e) {
                    System.out.println("Error using ProfileComponent with getRenderLayer: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("Error creating ProfileComponent: " + e.getMessage());
        }
        
        // Try the direct ProfileComponent approach from the original code
        try {
            // Try to create a ProfileComponent directly
            Class<?> profileComponentClass = Class.forName("net.minecraft.component.type.ProfileComponent");
            Constructor<?> constructor = profileComponentClass.getConstructor(GameProfile.class);
            Object profileComponent = constructor.newInstance(profile);
            
            // Use the ProfileComponent with SkullBlockEntityRenderer
            Method getRenderLayerMethod = SkullBlockEntityRenderer.class
                .getMethod("getRenderLayer", SkullBlock.SkullType.class, profileComponentClass);
            return (RenderLayer) getRenderLayerMethod.invoke(null, skullType, profileComponent);
        } catch (Exception e) {
            System.out.println("ProfileComponent approach failed: " + e.getMessage());
            
            // Fall back to GameProfile approach (pre-1.20.5)
            try {
                Method getRenderLayerMethod = SkullBlockEntityRenderer.class
                    .getMethod("getRenderLayer", SkullBlock.SkullType.class, GameProfile.class);
                return (RenderLayer) getRenderLayerMethod.invoke(null, skullType, profile);
            } catch (Exception e2) {
                System.out.println("GameProfile approach failed: " + e2.getMessage());
                
                // Fall back to default skins based on UUID
                if (profile.getId() != null) {
                    boolean isSlim = isSlimModel(profile.getId());
                    if (isSlim) {
                        return RenderLayer.getEntityCutoutNoCull(new Identifier("minecraft", "textures/entity/player/slim/alex.png"));
                    } else {
                        return RenderLayer.getEntityCutoutNoCull(new Identifier("minecraft", "textures/entity/player/wide/steve.png"));
                    }
                }
                
                // Last resort: use the default player skin
                return RenderLayer.getEntityCutoutNoCull(new Identifier("minecraft", "textures/entity/player/wide/steve.png"));
            }
        }
    }
    
    /**
     * Gets the default texture for a skull type.
     */
    private static RenderLayer getDefaultSkullTexture(SkullBlock.SkullType skullType) {
        return switch (skullType.toString()) {
            case "SKELETON" -> RenderLayer.getEntityCutoutNoCull(new Identifier("textures/entity/skeleton/skeleton.png"));
            case "WITHER_SKELETON" -> RenderLayer.getEntityCutoutNoCull(new Identifier("textures/entity/skeleton/wither_skeleton.png"));
            case "ZOMBIE" -> RenderLayer.getEntityCutoutNoCull(new Identifier("textures/entity/zombie/zombie.png"));
            case "CREEPER" -> RenderLayer.getEntityCutoutNoCull(new Identifier("textures/entity/creeper/creeper.png"));
            case "DRAGON" -> RenderLayer.getEntityCutoutNoCull(new Identifier("textures/entity/enderdragon/dragon.png"));
            case "PIGLIN" -> RenderLayer.getEntityCutoutNoCull(new Identifier("textures/entity/piglin/piglin.png"));
            default -> RenderLayer.getEntityCutoutNoCull(new Identifier("textures/entity/skeleton/skeleton.png"));
        };
    }

    /**
     * Converts a UUID to a byte array.
     */
    private static byte[] getBytesFromUUID(UUID uuid) {
        long msb = uuid.getMostSignificantBits();
        long lsb = uuid.getLeastSignificantBits();
        byte[] buffer = new byte[16];
        
        for (int i = 0; i < 8; i++) {
            buffer[i] = (byte) (msb >>> 8 * (7 - i));
        }
        for (int i = 8; i < 16; i++) {
            buffer[i] = (byte) (lsb >>> 8 * (15 - i));
        }
        
        return buffer;
    }

    /**
     * Leverages Minecraft's NbtHelper to create a profile with the appropriate
     * texture, signature, and owner.
     */
    public static GameProfile getCustomSkullProfile(NbtCompound graveSkull) {
        return ProfileCompat.toGameProfile(graveSkull);
    }

    /**
     * Wrapper that lets you bundle the type and model for a given skull.
     */
    public record SkullWrapper(SkullBlock.SkullType type, EntityModelLayer model) { }

    // Cache to avoid spamming the console with the same profile logs
    private static final Set<String> loggedProfiles = new HashSet<>();

    /**
     * Determines if a player model should be slim (Alex) based on the UUID.
     * This uses the same algorithm as Minecraft.
     */
    private static boolean isSlimModel(UUID uuid) {
        return (uuid.hashCode() & 1) == 1;
    }

    /**
     * Attempts to find the correct skin texture for a player.
     * This is used for debugging purposes.
     */
    private static void debugSkinTextures() {
        try {
            // Try to find all skin-related fields and methods
            System.out.println("=== DEBUG SKIN TEXTURES ===");
            
            // Check DefaultSkinHelper
            try {
                Class<?> defaultSkinHelper = Class.forName("net.minecraft.client.util.DefaultSkinHelper");
                System.out.println("Found DefaultSkinHelper class");
                
                // List all fields
                Field[] fields = defaultSkinHelper.getDeclaredFields();
                System.out.println("DefaultSkinHelper fields:");
                for (Field field : fields) {
                    field.setAccessible(true);
                    System.out.println("  " + field.getName() + " (" + field.getType().getName() + ")");
                    if (field.getType() == Identifier.class) {
                        try {
                            Identifier id = (Identifier) field.get(null);
                            System.out.println("    Value: " + id);
                        } catch (Exception e) {
                            System.out.println("    Could not get value: " + e.getMessage());
                        }
                    }
                }
                
                // List all methods
                Method[] methods = defaultSkinHelper.getDeclaredMethods();
                System.out.println("DefaultSkinHelper methods:");
                for (Method method : methods) {
                    System.out.println("  " + method.getName() + "(" + 
                                      java.util.Arrays.stream(method.getParameterTypes())
                                          .map(Class::getSimpleName)
                                          .collect(java.util.stream.Collectors.joining(", ")) + 
                                      ") -> " + method.getReturnType().getSimpleName());
                }
            } catch (ClassNotFoundException e) {
                System.out.println("DefaultSkinHelper class not found");
            }
            
            // Check SkullBlockEntityRenderer
            try {
                System.out.println("SkullBlockEntityRenderer methods:");
                Method[] methods = SkullBlockEntityRenderer.class.getDeclaredMethods();
                for (Method method : methods) {
                    System.out.println("  " + method.getName() + "(" + 
                                      java.util.Arrays.stream(method.getParameterTypes())
                                          .map(Class::getSimpleName)
                                          .collect(java.util.stream.Collectors.joining(", ")) + 
                                      ") -> " + method.getReturnType().getSimpleName());
                }
            } catch (Exception e) {
                System.out.println("Error inspecting SkullBlockEntityRenderer: " + e.getMessage());
            }
            
            System.out.println("=== END DEBUG ===");
        } catch (Exception e) {
            System.out.println("Error in debugSkinTextures: " + e.getMessage());
        }
    }
}
