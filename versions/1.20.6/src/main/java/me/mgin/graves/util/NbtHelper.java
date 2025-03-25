package me.mgin.graves.util;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.DynamicRegistryManager;
import java.lang.reflect.Method;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;

import java.util.Date;

public class NbtHelper {
    /**
     * Read an inventory from NBT, handling the new serialization system in 1.20.5
     */
    static public DefaultedList<ItemStack> readInventory(String key, NbtCompound nbt) {
        if (nbt.contains(key)) {
            // Get the item count
            int size = 0;
            if (nbt.contains("ItemCount") && nbt.getCompound("ItemCount").contains(key)) {
                size = nbt.getCompound("ItemCount").getInt(key);
            } else if (nbt.getCompound(key).contains("Size")) {
                size = nbt.getCompound(key).getInt("Size");
            } else {
                // Try to count the items manually
                NbtCompound inventoryNbt = nbt.getCompound(key);
                if (inventoryNbt.contains("Items")) {
                    NbtList itemsList = inventoryNbt.getList("Items", NbtElement.COMPOUND_TYPE);
                    for (int i = 0; i < itemsList.size(); i++) {
                        NbtCompound itemNbt = itemsList.getCompound(i);
                        if (itemNbt.contains("Slot")) {
                            int slot = itemNbt.getInt("Slot");
                            size = Math.max(size, slot + 1);
                        }
                    }
                }
            }
            
            // Create the inventory
            DefaultedList<ItemStack> stacks = DefaultedList.ofSize(size, ItemStack.EMPTY);
            
            // Try to use the new method signature with WrapperLookup
            try {
                // Get a WrapperLookup
                WrapperLookup registryLookup = getWrapperLookup();
                
                if (registryLookup != null) {
                    // Try to find the method that takes a WrapperLookup
                    try {
                        Method readNbtMethod = Inventories.class.getMethod("readNbt", NbtCompound.class, DefaultedList.class, WrapperLookup.class);
                        readNbtMethod.invoke(null, nbt.getCompound(key), stacks, registryLookup);
                        return stacks;
                    } catch (Exception e) {
                        // Fall back to manual deserialization
                        NbtCompound inventoryNbt = nbt.getCompound(key);
                        if (inventoryNbt.contains("Items")) {
                            NbtList itemsList = inventoryNbt.getList("Items", NbtElement.COMPOUND_TYPE);
                            
                            for (int i = 0; i < itemsList.size(); i++) {
                                NbtCompound itemNbt = itemsList.getCompound(i);
                                if (itemNbt.contains("Slot")) {
                                    int slot = itemNbt.getInt("Slot");
                                    if (slot >= 0 && slot < stacks.size()) {
                                        // Try to deserialize the item with the registry lookup
                                        try {
                                            Method fromNbtMethod = ItemStack.class.getMethod("fromNbt", NbtCompound.class, WrapperLookup.class);
                                            ItemStack stack = (ItemStack) fromNbtMethod.invoke(null, itemNbt, registryLookup);
                                            stacks.set(slot, stack);
                                        } catch (Exception ex) {
                                            // Fall back to the old method
                                            try {
                                                Method fromNbtMethod = ItemStack.class.getMethod("fromNbt", NbtCompound.class);
                                                ItemStack stack = (ItemStack) fromNbtMethod.invoke(null, itemNbt);
                                                stacks.set(slot, stack);
                                            } catch (Exception exc) {
                                                // Last resort: try to create a basic item
                                                if (itemNbt.contains("id")) {
                                                    String id = itemNbt.getString("id");
                                                    int count = itemNbt.contains("Count") ? itemNbt.getInt("Count") : 1;
                                                    // Use a safer approach to create an item
                                                    try {
                                                        // Try to get the item from the registry using reflection
                                                        Class<?> registryClass = Class.forName("net.minecraft.registry.Registries");
                                                        Object itemRegistry = registryClass.getField("ITEM").get(null);
                                                        Method getMethod = itemRegistry.getClass().getMethod("get", Identifier.class);
                                                        Object item = getMethod.invoke(itemRegistry, new Identifier(id));
                                                        
                                                        // Create an ItemStack with the item
                                                        stacks.set(slot, new ItemStack((net.minecraft.item.Item) item, count));
                                                    } catch (Exception e3) {
                                                        // If that fails, just log the error
                                                        System.err.println("Failed to create item from id: " + id);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        return stacks;
                    }
                }
            } catch (Exception e) {
                // Ignore and fall back to the old method
            }
            
            // Fall back to the old method
            try {
                Inventories.readNbt(nbt.getCompound(key), stacks, null);
            } catch (Exception e) {
                // If even that fails, log the error
                System.err.println("Failed to read inventory: " + e.getMessage());
            }
            
            return stacks;
        }
        return DefaultedList.ofSize(0);
    }

    /**
     * Writes an inventory to NBT, handling the new serialization system in 1.20.5
     */
    public static NbtCompound writeInventory(String key, DefaultedList<ItemStack> stacks, NbtCompound nbt) {
        if (nbt == null) {
            nbt = new NbtCompound();
        }

        // Store the item count
        NbtCompound itemCount = new NbtCompound();
        itemCount.putInt(key, stacks.size());
        nbt.put("ItemCount", itemCount);

        // Try to use the new method signature with WrapperLookup
        try {
            // Get the WrapperLookup from DynamicRegistryManager.EMPTY
            WrapperLookup registryLookup = getWrapperLookup();
            
            // Try to find the method that takes a WrapperLookup
            Method writeNbtMethod = Inventories.class.getMethod("writeNbt", NbtCompound.class, DefaultedList.class, WrapperLookup.class);
            NbtCompound inventoryNbt = (NbtCompound) writeNbtMethod.invoke(null, new NbtCompound(), stacks, registryLookup);
            nbt.put(key, inventoryNbt);
        } catch (Exception e) {
            // Fall back to the old method
            try {
                nbt.put(key, Inventories.writeNbt(new NbtCompound(), stacks, null));
            } catch (Exception ex) {
                // Last resort: manually serialize each item
                NbtCompound inventoryNbt = new NbtCompound();
                NbtList itemsList = new NbtList();
                
                for (int i = 0; i < stacks.size(); i++) {
                    ItemStack stack = stacks.get(i);
                    if (!stack.isEmpty()) {
                        NbtCompound itemNbt = new NbtCompound();
                        itemNbt.putInt("Slot", i);
                        
                        // Try to serialize the item manually
                        try {
                            // Use reflection to call writeNbt on the ItemStack
                            Method writeNbtMethod = ItemStack.class.getMethod("writeNbt", NbtCompound.class);
                            writeNbtMethod.invoke(stack, itemNbt);
                        } catch (Exception exc) {
                            // If all else fails, just store the item ID
                            itemNbt.putString("id", stack.getItem().toString());
                            itemNbt.putInt("Count", stack.getCount());
                        }
                        
                        itemsList.add(itemNbt);
                    }
                }
                
                inventoryNbt.put("Items", itemsList);
                nbt.put(key, inventoryNbt);
            }
        }

        return nbt;
    }

    /**
     * Writes an inventory to NBT with a WrapperLookup for 1.20.5+
     */
    public static NbtCompound writeInventoryWithLookup(String key, DefaultedList<ItemStack> stacks, NbtCompound nbt, WrapperLookup registryLookup) {
        if (nbt == null) {
            nbt = new NbtCompound();
        }

        // Validate stacks to prevent NPE
        if (stacks == null) {
            System.err.println("Warning: Null inventory stacks for key: " + key);
            return nbt;
        }

        // Store the item count
        NbtCompound itemCount = new NbtCompound();
        itemCount.putInt(key, stacks.size());
        nbt.put("ItemCount", itemCount);

        // Check if registryLookup is null and try to get a valid one if needed
        if (registryLookup == null) {
            registryLookup = getWrapperLookup();
            if (registryLookup == null) {
                System.err.println("Warning: Could not obtain a valid WrapperLookup for serialization");
            }
        }

        try {
            if (registryLookup != null) {
                // Try to use the method that takes a WrapperLookup
                try {
                    // Direct approach first
                    Method writeNbtMethod = Inventories.class.getMethod("writeNbt", NbtCompound.class, DefaultedList.class, WrapperLookup.class);
                    NbtCompound inventoryNbt = (NbtCompound) writeNbtMethod.invoke(null, new NbtCompound(), stacks, registryLookup);
                    nbt.put(key, inventoryNbt);
                    return nbt;
                } catch (Exception e) {
                    System.err.println("Failed to use Inventories.writeNbt with WrapperLookup: " + e.getMessage());
                    // Fall back to manual serialization
                    NbtCompound inventoryNbt = new NbtCompound();
                    NbtList itemsList = new NbtList();
                    
                    for (int i = 0; i < stacks.size(); i++) {
                        ItemStack stack = stacks.get(i);
                        if (stack == null || stack.isEmpty()) {
                            continue;
                        }
                        
                        try {
                            NbtCompound itemNbt = new NbtCompound();
                            itemNbt.putInt("Slot", i);
                            
                            // Try to use the method that takes a WrapperLookup
                            try {
                                Method writeNbtMethod = ItemStack.class.getMethod("writeNbt", NbtCompound.class, WrapperLookup.class);
                                itemNbt = (NbtCompound) writeNbtMethod.invoke(stack, itemNbt, registryLookup);
                            } catch (Exception ex) {
                                // Fall back to the old method
                                try {
                                    Method writeNbtMethod = ItemStack.class.getMethod("writeNbt", NbtCompound.class);
                                    itemNbt = (NbtCompound) writeNbtMethod.invoke(stack, itemNbt);
                                } catch (Exception exc) {
                                    // Last resort: try to write basic item data
                                    itemNbt.putString("id", stack.getItem().toString());
                                    itemNbt.putInt("Count", stack.getCount());
                                }
                            }
                            
                            itemsList.add(itemNbt);
                        } catch (Exception ex) {
                            System.err.println("Failed to serialize item at slot " + i + ": " + ex.getMessage());
                        }
                    }
                    
                    inventoryNbt.put("Items", itemsList);
                    nbt.put(key, inventoryNbt);
                }
            } else {
                // Fall back to the old method if registryLookup is null
                try {
                    // Try to use the old method signature without WrapperLookup
                    Method writeNbtMethod = Inventories.class.getMethod("writeNbt", NbtCompound.class, DefaultedList.class);
                    NbtCompound inventoryNbt = (NbtCompound) writeNbtMethod.invoke(null, new NbtCompound(), stacks);
                    nbt.put(key, inventoryNbt);
                } catch (Exception ex) {
                    System.err.println("Failed to use old Inventories.writeNbt method: " + ex.getMessage());
                    // Manual fallback
                    NbtCompound inventoryNbt = new NbtCompound();
                    NbtList itemsList = new NbtList();
                    
                    for (int i = 0; i < stacks.size(); i++) {
                        ItemStack stack = stacks.get(i);
                        if (stack == null || stack.isEmpty()) {
                            continue;
                        }
                        
                        NbtCompound itemNbt = new NbtCompound();
                        itemNbt.putInt("Slot", i);
                        itemNbt.putString("id", stack.getItem().toString());
                        itemNbt.putInt("Count", stack.getCount());
                        itemsList.add(itemNbt);
                    }
                    
                    inventoryNbt.put("Items", itemsList);
                    nbt.put(key, inventoryNbt);
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to write inventory: " + e.getMessage());
            e.printStackTrace();
            
            // Last resort: try to write a basic inventory
            try {
                NbtCompound inventoryNbt = new NbtCompound();
                NbtList itemsList = new NbtList();
                
                for (int i = 0; i < stacks.size(); i++) {
                    ItemStack stack = stacks.get(i);
                    if (stack == null || stack.isEmpty()) {
                        continue;
                    }
                    
                    NbtCompound itemNbt = new NbtCompound();
                    itemNbt.putInt("Slot", i);
                    itemNbt.putString("id", stack.getItem().toString());
                    itemNbt.putInt("Count", stack.getCount());
                    itemsList.add(itemNbt);
                }
                
                inventoryNbt.put("Items", itemsList);
                nbt.put(key, inventoryNbt);
            } catch (Exception ex) {
                System.err.println("Failed to write basic inventory: " + ex.getMessage());
            }
        }

        return nbt;
    }

    /**
     * Gets a WrapperLookup for serialization, trying multiple approaches
     */
    private static WrapperLookup getWrapperLookup() {
        // Try multiple approaches to get a valid WrapperLookup
        
        // Approach 1: Try to get it from DynamicRegistryManager.EMPTY
        try {
            Method getWrapperLookupMethod = DynamicRegistryManager.class.getMethod("getWrapperLookup");
            return (WrapperLookup) getWrapperLookupMethod.invoke(DynamicRegistryManager.EMPTY);
        } catch (Exception e) {
            // Ignore and try next approach
        }
        
        // Approach 2: Try to get it from a static field
        try {
            return (WrapperLookup) DynamicRegistryManager.class.getField("EMPTY_WRAPPER_LOOKUP").get(null);
        } catch (Exception e) {
            // Ignore and try next approach
        }
        
        // Approach 3: Try to create a new DynamicRegistryManager and get its WrapperLookup
        try {
            // Try to get the createEmptyWithTypes method
            Method createEmptyMethod = DynamicRegistryManager.class.getMethod("createEmptyWithTypes", Class[].class);
            Object dynamicRegistryManager = createEmptyMethod.invoke(null, (Object) new Class[0]);
            Method getWrapperLookupMethod = dynamicRegistryManager.getClass().getMethod("getWrapperLookup");
            return (WrapperLookup) getWrapperLookupMethod.invoke(dynamicRegistryManager);
        } catch (Exception e) {
            // Ignore and try next approach
        }
        
        // Approach 4: Try to use reflection to access the internal registry manager
        try {
            // This is a last resort and might be fragile
            Class<?> registryManagerClass = Class.forName("net.minecraft.registry.DynamicRegistryManager$Immutable");
            Method ofMethod = registryManagerClass.getMethod("of");
            Object registryManager = ofMethod.invoke(null);
            Method getWrapperLookupMethod = registryManager.getClass().getMethod("getWrapperLookup");
            return (WrapperLookup) getWrapperLookupMethod.invoke(registryManager);
        } catch (Exception e) {
            // If all approaches fail, log the error and return null
            System.err.println("Failed to get WrapperLookup: " + e.getMessage());
            return null;
        }
    }

    /**
     * Wrapper for <i>NbtHelper.toGameProfile</i>.
     *
     * @param nbt NbtCompound
     * @return GameProfile
     */
    public static GameProfile toGameProfile(NbtCompound nbt) {
        try {
            // For Minecraft 1.20.5+
            Class<?> gameProfileSerializerClass = Class.forName("net.minecraft.nbt.GameProfileSerializer");
            java.lang.reflect.Method readGameProfileMethod = gameProfileSerializerClass.getMethod("readGameProfile", NbtCompound.class);
            return (GameProfile) readGameProfileMethod.invoke(null, nbt);
        } catch (Exception e) {
            // Fallback
            return null;
        }
    }

    /**
     * Wrapper for <i>NbtHelper.writeGameProfile</i>.
     *
     * @param nbt NbtCompound
     * @param profile GameProfile
     * @return NbtCompound
     */
    public static NbtCompound writeGameProfile(NbtCompound nbt, GameProfile profile) {
        try {
            // For Minecraft 1.20.5+
            Class<?> gameProfileSerializerClass = Class.forName("net.minecraft.nbt.GameProfileSerializer");
            java.lang.reflect.Method writeGameProfileMethod = gameProfileSerializerClass.getMethod("writeGameProfile", NbtCompound.class, GameProfile.class);
            return (NbtCompound) writeGameProfileMethod.invoke(null, nbt, profile);
        } catch (Exception e) {
            // Fallback - just return the original NBT
            return nbt;
        }
    }

    /**
     * Wrapper for <i>NbtHelper.fromNbtProviderString</i>.
     *
     * @param nbtString String
     * @return NbtCompound
     */
    public static NbtCompound fromNbtProviderString(String nbtString) throws CommandSyntaxException {
        return net.minecraft.nbt.NbtHelper.fromNbtProviderString(nbtString);
    }

    /**
     * Wrapper for <i>NbtHelper.toPrettyPrintedText</i>.
     *
     * @param nbt NbtElement
     * @return Text
     */
    public static Text toPrettyPrintedText(NbtElement nbt) {
        return net.minecraft.nbt.NbtHelper.toPrettyPrintedText(nbt);
    }

    /**
     * Creates a new BlockPos based on stored coordinates in the given NBT.
     *
     * @param nbt NbtCompound
     * @return BlockPos
     */
    public static BlockPos readCoordinates(NbtCompound nbt) {
        return new BlockPos(
            nbt.getInt("x"),
            nbt.getInt("y"),
            nbt.getInt("z")
        );
    }

    /**
     * Upgrades any old graves nbt to newer formats
     *
     * @param nbt NbtCompound
     * @return NbtCompound
     */
    public static NbtCompound upgradeOldGraves(NbtCompound nbt) {
        if (nbt.getType("ItemCount") == NbtElement.INT_TYPE)
            nbt = upgradeInventories(nbt);

        if (nbt.contains("noAge"))
            nbt = upgradeNoAge(nbt);

        if (nbt.getLong("mstime") == 0)
            nbt = upgradeMsTime(nbt);

        if (nbt.contains("GraveSkull", NbtElement.STRING_TYPE))
            nbt = removeOldCustomSkullTexture(nbt);

        return nbt;
    }

    /**
     * Converts old graves from having a mstime of 0 to the time they're
     * first seen in the world.
     *
     * @param nbt NbtCompound
     * @return NbtCompound
     */
    private static NbtCompound upgradeMsTime(NbtCompound nbt) {
        nbt.putLong("mstime", (new Date()).getTime());
        return nbt;
    }

    /**
     * Converts noAge key to noDecay key while preserving the value.
     *
     * @param nbt NbtCompound
     * @return NbtCompound
     */
    private static NbtCompound upgradeNoAge(NbtCompound nbt) {
        int noAge = nbt.getInt("noAge");
        nbt.putInt("noDecay", noAge);
        nbt.remove("noAge");
        return nbt;
    }

    /**
     * Converts the old inventory nbt format to the new format.
     *
     * @param nbt NbtCompound
     * @return NbtCompound
     */
    private static NbtCompound upgradeInventories(NbtCompound nbt) {
        // Retrieve the items like normal
        DefaultedList<ItemStack> oldItems = DefaultedList.ofSize(nbt.getInt("ItemCount"), ItemStack.EMPTY);
        Inventories.readNbt(nbt.getCompound("Items"), oldItems, null);

        // Separate the item lists
        DefaultedList<ItemStack> items = DefaultedList.ofSize(0);
        items.addAll(oldItems.subList(0, 41));

        DefaultedList<ItemStack> trinkets = DefaultedList.ofSize(0);
        if (oldItems.size() > 41) {
            trinkets.addAll(oldItems.subList(41, oldItems.size()));
        }

        // Create/store new ItemCount format
        NbtCompound itemCount = new NbtCompound();
        itemCount.putInt("Items", items.size());
        itemCount.putInt("trinkets", trinkets.size());
        nbt.put("ItemCount", itemCount);

        // Store the two inventories
        nbt.put("Items", Inventories.writeNbt(new NbtCompound(), items, null));
        nbt.put("trinkets", Inventories.writeNbt(new NbtCompound(), trinkets, null));

        return nbt;
    }

    private static NbtCompound removeOldCustomSkullTexture(NbtCompound nbt) {
        nbt.remove("GraveSkull");
        return nbt;
    }
}
