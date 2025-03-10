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
            // Try to get it from DynamicRegistryManager.EMPTY using reflection
            Method getWrapperLookupMethod = DynamicRegistryManager.class.getMethod("getWrapperLookup");
            return (WrapperLookup) getWrapperLookupMethod.invoke(DynamicRegistryManager.EMPTY);
        } catch (Exception e) {
            try {
                // Try to get it from a static field
                return (WrapperLookup) DynamicRegistryManager.class.getField("EMPTY_WRAPPER_LOOKUP").get(null);
            } catch (Exception e2) {
                System.err.println("Failed to get WrapperLookup: " + e2.getMessage());
                return null;
            }
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

                            // Create the ItemStack using reflection
                            ItemStack stack = ItemStack.EMPTY;
                            try {
                                if (registryLookup != null) {
                                    try {
                                        // Try to use the method with registry lookup
                                        Method fromNbtMethod = ItemStack.class.getMethod("fromNbt", NbtCompound.class, WrapperLookup.class);
                                        stack = (ItemStack) fromNbtMethod.invoke(null, itemNbt, registryLookup);
                                    } catch (Exception e) {
                                        // Fall back to the old method
                                        Method fromNbtMethod = ItemStack.class.getMethod("fromNbt", NbtCompound.class);
                                        stack = (ItemStack) fromNbtMethod.invoke(null, itemNbt);
                                    }
                                } else {
                                    // Fallback for when registryLookup is null
                                    try {
                                        // Get the item from the registry
                                        Identifier itemId = new Identifier(itemNbt.getString("id"));
                                        stack = new ItemStack(Registries.ITEM.get(itemId), itemNbt.getInt("Count"));
                                        
                                        // Apply damage if present
                                        if (itemNbt.contains("Damage")) {
                                            stack.setDamage(itemNbt.getInt("Damage"));
                                        }
                                        
                                        // Apply NBT data if present
                                        if (itemNbt.contains("tag")) {
                                            try {
                                                Method setNbtMethod = ItemStack.class.getMethod("setNbt", NbtCompound.class);
                                                setNbtMethod.invoke(stack, itemNbt.getCompound("tag").copy());
                                            } catch (Exception e) {
                                                // Ignore if method doesn't exist
                                            }
                                        }
                                    } catch (Exception e) {
                                        System.err.println("Error reading item from NBT: " + e.getMessage());
                                        stack = ItemStack.EMPTY;
                                    }
                                }
                            } catch (Exception e) {
                                System.err.println("Error creating ItemStack from NBT: " + e.getMessage());
                                
                                // Try a more basic approach
                                if (itemNbt.contains("id")) {
                                    try {
                                        String id2 = itemNbt.getString("id");
                                        int count = itemNbt.contains("Count") ? itemNbt.getInt("Count") : 1;
                                        
                                        // Try to get the item from the registry
                                        stack = new ItemStack(Registries.ITEM.get(new Identifier(id2)), count);
                                    } catch (Exception ex) {
                                        System.err.println("Failed to create basic ItemStack: " + ex.getMessage());
                                    }
                                }
                            }

                            if (slot >= 0 && slot < inventory.size()) {
                                inventory.set(slot, stack);
                            }
                        }
                    }

                    // Add the inventory to the map
                    inventories.put(id, inventory);
                    System.out.println("Successfully loaded inventory '" + id + "' with " + size + " slots");
                }
            } catch (Exception e) {
                System.err.println("Error reading inventory '" + id + "': " + e.getMessage());
            }
        }
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
            System.out.println("Time: " + nbt.getLong("time"));
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
            // Try using the component-based approach first (1.20.5+)
            try {
                // Get the ItemStack.writeComponentsNbt method
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
            Method writeNbtMethod = ItemStack.class.getMethod("writeNbt", NbtCompound.class);
            writeNbtMethod.invoke(stack, nbt);
            return nbt;
        } catch (Exception e) {
            System.err.println("Error serializing item: " + e.getMessage());
            
            // Last resort: manually write basic item data
            try {
                // Get the item ID
                Identifier id = Registries.ITEM.getId(stack.getItem());
                nbt.putString("id", id.toString());
                nbt.putByte("Count", (byte) stack.getCount());
                
                // Try to get damage value if applicable
                try {
                    Method getDamageMethod = stack.getClass().getMethod("getDamage");
                    int damage = (int) getDamageMethod.invoke(stack);
                    if (damage > 0) {
                        nbt.putInt("Damage", damage);
                    }
                } catch (Exception ignored) {
                    // Item doesn't have damage
                }
                
                return nbt;
            } catch (Exception ex) {
                System.err.println("Failed to manually serialize item: " + ex.getMessage());
                return nbt;
            }
        }
    }
} 