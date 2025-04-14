package me.mgin.graves.compat;

import com.mojang.authlib.GameProfile;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;

/**
 * Helper class for serialization operations in Minecraft 1.20.5+
 */
public class SerializationHelper {

    /**
     * Gets a WrapperLookup instance, trying multiple approaches
     */
    public static WrapperLookup getWrapperLookup() {
        try {
            // First, try getting it directly from DynamicRegistryManager.EMPTY
            try {
                // This is the preferred approach in newer versions
                // Using reflection because the method may not exist in all versions
                Method getWrapperLookupMethod = DynamicRegistryManager.class.getMethod("getWrapperLookup");
                return (WrapperLookup) getWrapperLookupMethod.invoke(DynamicRegistryManager.EMPTY);
            } catch (Exception e) {
                // Ignore and try other approaches
            }
            
            // Try to get the static field
            try {
                Field field = DynamicRegistryManager.class.getField("EMPTY_WRAPPER_LOOKUP");
                return (WrapperLookup) field.get(null);
            } catch (Exception e) {
                // Ignore and try next approach
            }
            
            // Try alternative field names or other classes that might have a lookup
            try {
                Field field = Registries.class.getField("WRAPPER_LOOKUP");
                return (WrapperLookup) field.get(null);
            } catch (Exception e) {
                // Final fallback - just log an informational message instead of error
                System.out.println("[SerializationHelper] Unable to get WrapperLookup through standard methods - will use basic serialization");
                return null;
            }
        } catch (Exception e) {
            System.out.println("[SerializationHelper] All attempts to get WrapperLookup failed - using basic serialization");
            return null;
        }
    }

    /**
     * Writes a GameProfile to NBT
     */
    public static NbtCompound writeGameProfile(NbtCompound nbt, GameProfile profile) {
        if (profile == null) {
            return nbt;
        }

        NbtCompound profileNbt = new NbtCompound();
        
        if (profile.getId() != null) {
            profileNbt.putUuid("Id", profile.getId());
        }
        
        if (profile.getName() != null) {
            profileNbt.putString("Name", profile.getName());
        }
        
        nbt.put("GraveOwner", profileNbt);
        return nbt;
    }

    /**
     * Reads a GameProfile from NBT
     */
    public static GameProfile readGameProfile(NbtCompound nbt) {
        if (nbt == null || !nbt.contains("GraveOwner", NbtElement.COMPOUND_TYPE)) {
            return null;
        }

        NbtCompound profileNbt = nbt.getCompound("GraveOwner");
        UUID id = null;
        String name = null;

        if (profileNbt.contains("Id")) {
            id = profileNbt.getUuid("Id");
        }

        if (profileNbt.contains("Name")) {
            name = profileNbt.getString("Name");
        }

        return new GameProfile(id, name);
    }

    /**
     * Writes inventories to NBT
     */
    public static NbtCompound writeInventories(NbtCompound nbt, Map<String, DefaultedList<ItemStack>> inventories, WrapperLookup registryLookup) {
        if (nbt == null) {
            nbt = new NbtCompound();
        }

        // Create compounds to store inventories and item counts
        NbtCompound inventoriesNbt = new NbtCompound();
        NbtCompound itemCountNbt = new NbtCompound();

        // Process each inventory
        for (Map.Entry<String, DefaultedList<ItemStack>> entry : inventories.entrySet()) {
            String id = entry.getKey();
            DefaultedList<ItemStack> inventory = entry.getValue();

            if (inventory == null || inventory.isEmpty()) {
                continue;
            }

            // Store the item count
            itemCountNbt.putInt(id, inventory.size());

            // Create a list for this inventory's items
            NbtList itemsNbt = new NbtList();

            // Add each non-empty item to the list
            for (int i = 0; i < inventory.size(); i++) {
                ItemStack stack = inventory.get(i);
                if (stack == null || stack.isEmpty()) {
                    continue;
                }

                NbtCompound itemNbt = new NbtCompound();
                itemNbt.putInt("Slot", i);

                // Use our new serialization method
                serializeItemStack(stack, itemNbt, registryLookup);
                itemsNbt.add(itemNbt);
            }

            // Add this inventory to the inventories compound
            inventoriesNbt.put(id, itemsNbt);
        }

        // Add the inventories and item count to the main tag
        if (!inventoriesNbt.isEmpty()) {
            nbt.put("Inventories", inventoriesNbt);
        }
        if (!itemCountNbt.isEmpty()) {
            nbt.put("ItemCount", itemCountNbt);
        }

        return nbt;
    }

    /**
     * Reads inventories from NBT
     */
    public static void readInventories(NbtCompound nbt, Map<String, DefaultedList<ItemStack>> inventories, WrapperLookup registryLookup) {
        if (nbt == null || !nbt.contains("Inventories")) {
            return;
        }

        NbtCompound inventoriesNbt = nbt.getCompound("Inventories");
        NbtCompound itemCountNbt = nbt.contains("ItemCount") ? nbt.getCompound("ItemCount") : new NbtCompound();

        // Process each inventory
        for (String id : inventoriesNbt.getKeys()) {
            try {
                NbtList itemsNbt = inventoriesNbt.getList(id, NbtElement.COMPOUND_TYPE);

                // Determine inventory size
                int size = itemCountNbt.contains(id) ? itemCountNbt.getInt(id) : 0;

                if (size == 0) {
                    // Try to determine size from the items
                    for (int i = 0; i < itemsNbt.size(); i++) {
                        NbtCompound itemNbt = itemsNbt.getCompound(i);
                        if (itemNbt.contains("Slot")) {
                            int slot = itemNbt.getInt("Slot");
                            size = Math.max(size, slot + 1);
                        }
                    }
                }

                if (size > 0) {
                    // Create inventory
                    DefaultedList<ItemStack> inventory = DefaultedList.ofSize(size, ItemStack.EMPTY);

                    // Fill inventory with items
                    for (int i = 0; i < itemsNbt.size(); i++) {
                        NbtCompound itemNbt = itemsNbt.getCompound(i);
                        if (itemNbt.contains("Slot")) {
                            int slot = itemNbt.getInt("Slot");

                            // Create the ItemStack using multiple approaches
                            ItemStack stack = deserializeItemStack(itemNbt, registryLookup);

                            if (slot >= 0 && slot < inventory.size()) {
                                inventory.set(slot, stack);
                            }
                        }
                    }

                    // Add the inventory to the map
                    inventories.put(id, inventory);
                    System.out.println("[SerializationHelper] Successfully loaded inventory '" + id + "' with " + size + " slots");
                }
            } catch (Exception e) {
                System.out.println("[SerializationHelper] Error reading inventory '" + id + "': " + e.getMessage());
            }
        }
    }

    /**
     * Deserializes an ItemStack from NBT using multiple approaches for compatibility
     */
    private static ItemStack deserializeItemStack(NbtCompound itemNbt, WrapperLookup registryLookup) {
        // Try multiple approaches to deserialize the ItemStack
        try {
            // Try using reflection with direct fromNbt method first
            try {
                Method fromNbtMethod = ItemStack.class.getMethod("fromNbt", NbtCompound.class);
                return (ItemStack) fromNbtMethod.invoke(null, itemNbt);
            } catch (Exception e) {
                // Continue to next approach
            }
            
            // Try using reflection with registry lookup
            if (registryLookup != null) {
                try {
                    Method fromNbtMethod = ItemStack.class.getMethod("fromNbt", NbtCompound.class, WrapperLookup.class);
                    return (ItemStack) fromNbtMethod.invoke(null, itemNbt, registryLookup);
                } catch (Exception e) {
                    // Continue to next approach
                }
            }
            
            // Manual fallback: create the item from registry and apply properties
            return createItemStackManually(itemNbt);
        } catch (Exception e) {
            System.out.println("[SerializationHelper] All ItemStack deserialization methods failed: " + e.getMessage());
            // Last resort: return empty stack
            return ItemStack.EMPTY;
        }
    }

    /**
     * Creates an ItemStack manually from NBT when all other methods fail
     */
    private static ItemStack createItemStackManually(NbtCompound itemNbt) {
        try {
            // Basic approach: get item from registry
            if (itemNbt.contains("id")) {
                String idString = itemNbt.getString("id");
                Identifier itemId = new Identifier(idString);
                
                // Get count, default to 1
                int count = itemNbt.contains("Count") ? 
                           (itemNbt.get("Count").getType() == NbtElement.BYTE_TYPE ? 
                            itemNbt.getByte("Count") : itemNbt.getInt("Count")) : 1;
                
                // Create the stack
                ItemStack stack = new ItemStack(Registries.ITEM.get(itemId), count);
                
                // Apply damage if present
                if (itemNbt.contains("Damage")) {
                    stack.setDamage(itemNbt.getInt("Damage"));
                }
                
                // Apply tag if present
                if (itemNbt.contains("tag")) {
                    try {
                        Method setNbtMethod = ItemStack.class.getMethod("setNbt", NbtCompound.class);
                        setNbtMethod.invoke(stack, itemNbt.getCompound("tag").copy());
                    } catch (Exception e) {
                        // If setNbt fails, we could apply critical tags manually
                        // This would require custom code for each important tag type
                        // For now, just log that we couldn't apply complex NBT data
                        if (itemNbt.contains("tag") && !itemNbt.getCompound("tag").isEmpty()) {
                            System.out.println("[SerializationHelper] Could not apply complex NBT data to item: " + 
                                Registries.ITEM.getId(stack.getItem()));
                        }
                    }
                }
                
                return stack;
            }
        } catch (Exception e) {
            System.out.println("[SerializationHelper] Error in manual ItemStack creation: " + e.getMessage());
        }
        
        return ItemStack.EMPTY;
    }

    /**
     * Logs the contents of a grave NBT for debugging
     */
    public static void debugGraveNbt(NbtCompound nbt) {
        if (nbt == null) {
            System.out.println("DEBUG: Grave NBT is null");
            return;
        }

        try {
            System.out.println("=== DEBUG: Grave NBT Contents ===");
            System.out.println("Position: " + nbt.getInt("x") + ", " + nbt.getInt("y") + ", " + nbt.getInt("z"));
            System.out.println("Dimension: " + nbt.getString("dimension"));
            System.out.println("Time: " + nbt.getLong("mstime"));
            System.out.println("XP: " + nbt.getInt("XP"));
            
            if (nbt.contains("owner")) {
                NbtCompound ownerTag = nbt.getCompound("owner");
                String name = ownerTag.getString("name");
                String uuid = ownerTag.getString("uuid");
                System.out.println("Owner: " + name + " (" + uuid + ")");
            } else {
                System.out.println("Owner: Not found in NBT");
            }
            
            // Count inventories
            int inventoryCount = 0;
            for (String key : nbt.getKeys()) {
                if (nbt.get(key) instanceof NbtCompound && ((NbtCompound) nbt.get(key)).contains("Items")) {
                    inventoryCount++;
                }
            }
            System.out.println("Inventories found: " + inventoryCount);
            
            // Print inventory details
            for (String key : nbt.getKeys()) {
                if (nbt.get(key) instanceof NbtCompound && ((NbtCompound) nbt.get(key)).contains("Items")) {
                    NbtCompound invTag = nbt.getCompound(key);
                    NbtList itemsList = invTag.getList("Items", 10); // 10 is the type ID for compound tags
                    System.out.println("  Inventory '" + key + "' has " + itemsList.size() + " items");
                    
                    // Debug first few items if needed
                    for (int i = 0; i < Math.min(itemsList.size(), 3); i++) {
                        NbtCompound itemTag = itemsList.getCompound(i);
                        System.out.println("    Item " + i + ": " + itemTag.getString("id") + " x" + itemTag.getByte("Count"));
                    }
                }
            }
            
            // Print item counts
            int itemCountKeys = 0;
            for (String key : nbt.getKeys()) {
                if (key.endsWith("Count")) {
                    itemCountKeys++;
                }
            }
            System.out.println("ItemCount found with keys: " + itemCountKeys);
            for (String key : nbt.getKeys()) {
                if (key.endsWith("Count")) {
                    System.out.println("  " + key + ": " + nbt.getInt(key));
                }
            }
            
            System.out.println("=== END DEBUG ===");
        } catch (Exception e) {
            System.out.println("Error in debugGraveNbt: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Serializes an ItemStack to NBT using the appropriate method for the Minecraft version
     */
    public static NbtCompound serializeItemStack(ItemStack stack, NbtCompound nbt, WrapperLookup registryLookup) {
        if (stack.isEmpty()) {
            return nbt;
        }
        
        try {
            // Try direct serialization first without reflection (most modern approach)
            try {
                // Must use reflection for this since method signature varies by version
                Method writeNbtDirectMethod = ItemStack.class.getMethod("writeNbt", NbtCompound.class);
                writeNbtDirectMethod.invoke(stack, nbt);
                return nbt;
            } catch (Exception directException) {
                // Continue to reflection-based approaches
            }
            
            // Try using the component-based approach first (1.20.5+)
            try {
                Method writeComponentsNbtMethod = ItemStack.class.getMethod("writeComponentsNbt", NbtCompound.class);
                writeComponentsNbtMethod.invoke(stack, nbt);
                return nbt;
            } catch (NoSuchMethodException e) {
                // Method doesn't exist, try the next approach
            }
            
            // Try using the registry-based approach (1.20.x)
            if (registryLookup != null) {
                try {
                    Method writeNbtMethod = ItemStack.class.getMethod("writeNbt", NbtCompound.class, WrapperLookup.class);
                    writeNbtMethod.invoke(stack, nbt, registryLookup);
                    return nbt;
                } catch (NoSuchMethodException e) {
                    // Method doesn't exist, try the next approach
                }
            }
            
            // Fall back to the basic approach (pre-1.20)
            try {
                Method writeNbtMethod = ItemStack.class.getMethod("writeNbt", NbtCompound.class);
                writeNbtMethod.invoke(stack, nbt);
                return nbt;
            } catch (Exception e) {
                // If all reflection methods fail, use a manual approach
                manualItemSerialization(stack, nbt);
                return nbt;
            }
        } catch (Exception e) {
            System.out.println("[SerializationHelper] Error serializing item, using fallback serialization: " + e.getMessage());
            // Manual fallback serialization
            manualItemSerialization(stack, nbt);
            return nbt;
        }
    }

    /**
     * Manual fallback for item serialization when all other methods fail
     */
    private static void manualItemSerialization(ItemStack stack, NbtCompound nbt) {
        // Store essential item data manually
        Identifier id = Registries.ITEM.getId(stack.getItem());
        nbt.putString("id", id.toString());
        nbt.putInt("Count", stack.getCount());
        
        // Store damage if applicable
        if (stack.isDamaged()) {
            nbt.putInt("Damage", stack.getDamage());
        }
        
        // Try to get the tag compound
        try {
            Method getNbtMethod = ItemStack.class.getMethod("getNbt");
            NbtCompound tag = (NbtCompound) getNbtMethod.invoke(stack);
            if (tag != null && !tag.isEmpty()) {
                nbt.put("tag", tag.copy());
            }
        } catch (Exception ignored) {
            // If we can't get the tag compound, just continue without it
        }
    }
} 