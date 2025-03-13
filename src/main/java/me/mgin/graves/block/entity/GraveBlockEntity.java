package me.mgin.graves.block.entity;

import com.mojang.authlib.GameProfile;
import me.mgin.graves.block.GraveBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

//? if >=1.20.5 {
import net.minecraft.registry.RegistryWrapper;
//?}

// Add import for Property
import net.minecraft.state.property.Property;

public class GraveBlockEntity extends BlockEntity {
    private GameProfile graveOwner;
    private BlockState state;
    private int xp;
    private int noDecay;
    private String customName;
    private NbtCompound graveSkull;
    private long mstime;
    private final Map<String, DefaultedList<ItemStack>> inventories = new HashMap<>() {
    };

    public GraveBlockEntity(BlockPos pos, BlockState state) {
        super(GraveBlocks.GRAVE_BLOCK_ENTITY, pos, state);
        this.graveOwner = null;
        this.customName = "";
        this.graveSkull = null;
        this.xp = 0;
        this.noDecay = 0;
        this.mstime = 0;
        setState(state);
    }

    /**
     * Set an inventory inside inventories.
     *
     * @param key String
     * @param items {@code DefaultedList<ItemStack>}
     */
    public void setInventory(String key, DefaultedList<ItemStack> items) {
        this.inventories.put(key, items);
        this.markDirty();
    }

    /**
     * Retrieve an inventory from the inventories.
     *
     * @param key String
     * @return {@code DefaultedList<ItemStack>}
     */
    public DefaultedList<ItemStack> getInventory(String key) {
        return this.inventories.get(key);
    }

    /**
     * Store the grave owner's GameProfile.
     *
     * @param profile GameProfile
     */
    public void setGraveOwner(GameProfile profile) {
        this.graveOwner = profile;
        this.markDirty();
    }

    /**
     * Retrieve the grave owner's GameProfile.
     *
     * @return GameProfile
     */
    public GameProfile getGraveOwner() {
        return graveOwner;
    }

    /**
     * Determines whether the player's gameprofile ID matches the grave owner's
     * gameprofile ID.
     *
     * @param player GameProfile
     * @return boolean
     */
    public boolean isGraveOwner(PlayerEntity player) {
        return graveOwner.getId().equals(player.getGameProfile().getId());
    }

    /**
     * Set the GraveBlockEntity's custom name.
     *
     * @param name String
     */
    public void setCustomName(String name) {
        this.customName = name;
        this.markDirty();
    }

    /**
     * Get the GraveBlockEntity's custom name.
     *
     * @return String
     */
    public String getCustomName() {
        return customName;
    }

    /**
     * Determines whether the GraveBlockEntity has a custom name.
     *
     * @return boolean
     */
    public boolean hasCustomName() {
        return customName.length() > 0;
    }

    /**
     * Set GraveBlockEntity's current state.
     *
     * @param state BlockState
     */
    public void setState(BlockState state) {
        this.state = state;
    }

    /**
     * Get GraveBlockEntity's current state.
     *
     * @return BlockState
     */
    public BlockState getState() {
        return state;
    }

    /**
     * Set the stored XP amount.
     *
     * @param xp int
     */
    public void setXp(int xp) {
        this.xp = xp;
        this.markDirty();
    }

    /**
     * Get the stored XP amount.
     *
     * @return int
     */
    public int getXp() {
        return xp;
    }

    /**
     * Set the time the grave was made
     *
     * @param timeInMilliseconds long
     */
    public void setMstime(long timeInMilliseconds) {
        this.mstime = timeInMilliseconds;
        this.markDirty();
    }

    /**
     * Get the time the grave was made (in milliseconds)
     *
     */
    public long getMstime() {
        return mstime;
    }

    /**
     * Set whether the grave should age or not.
     * <p>
     * <strong>Note:</strong> The grave stops aging if the value is set to 1 (one).
     *
     * @param aging int
     */
    public void setNoDecay(int aging) {
        this.noDecay = aging;
        this.markDirty();
    }

    /**
     * Get the current noDecay value.
     *
     * @return int
     */
    public int getNoDecay() {
        return this.noDecay;
    }

    /**
     * Set the GraveBlockEntity's SkinURL OR SkullType string.
     * <p>
     * <strong>Note:</strong> A SkinURL is the base64 encoded string typically
     * attached to custom player heads.
     *
     * @param graveSkull String
     */
    public void setGraveSkull(NbtCompound graveSkull) {
        this.graveSkull = graveSkull;
        this.markDirty();
    }

    /**
     * Retrieve the GraveBlockEntity's SkinURL OR SkullType string.
     * <p>
     * <strong>Note:</strong> A SkinURL is the base64 encoded string typically
     * attached to custom player heads.
     *
     * @return String (SkinURL | SkullType)
     */
    public NbtCompound getGraveSkull() {
        return this.graveSkull;
    }

    /**
     * Determine whether the GraveBlockEntity has a GraveSkull entry.
     *
     * @return boolean
     */
    public boolean hasGraveSkull() {
        if (this.graveSkull == null) {
            return false;
        }
        return !this.graveSkull.isEmpty();
    }

    /**
     * Gets all inventories stored in this grave entity
     */
    public Map<String, DefaultedList<ItemStack>> getInventories() {
        return this.inventories;
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        try {
            // Call the parent method
            super.writeNbt(nbt, registryLookup);
            
            // Write basic properties
            System.out.println("DEBUG: About to write XP to NBT: " + this.xp + " (this.xp value)");
            nbt.putInt("XP", this.xp);
            System.out.println("DEBUG: After writing XP to NBT, checking value: " + nbt.getInt("XP"));
            nbt.putInt("noDecay", this.noDecay);
            nbt.putLong("mstime", this.mstime);
            
            // Write grave owner
            if (this.graveOwner != null) {
                me.mgin.graves.compat.SerializationHelper.writeGameProfile(nbt, this.graveOwner);
            }
            
            // Write custom name
            if (this.customName != null) {
                nbt.putString("CustomName", this.customName);
            }
            
            // Write grave skull
            if (this.graveSkull != null) {
                nbt.put("GraveSkull", this.graveSkull);
            }
            
            // Write inventories
            me.mgin.graves.compat.SerializationHelper.writeInventories(nbt, this.inventories, registryLookup);
            
            // Debug output
            System.out.println("Successfully wrote grave NBT with " + this.inventories.size() + " inventories");
        } catch (Exception e) {
            System.err.println("Error in writeNbt: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        try {
            // Call the parent method
            super.readNbt(nbt, registryLookup);
            
            // Read basic properties
            System.out.println("DEBUG: Reading NBT, contains XP key: " + nbt.contains("XP"));
            if (nbt.contains("XP")) {
                int xpValue = nbt.getInt("XP");
                System.out.println("DEBUG: Read XP from NBT: " + xpValue + " (raw value)");
                this.xp = xpValue;
                System.out.println("DEBUG: After setting this.xp, value is: " + this.xp);
            } else {
                System.out.println("DEBUG: NBT does not contain XP key!");
                // List all keys in the NBT
                System.out.println("DEBUG: Available NBT keys: " + nbt.getKeys());
            }
            
            if (nbt.contains("noDecay")) {
                this.noDecay = nbt.getInt("noDecay");
            }
            
            if (nbt.contains("mstime")) {
                this.mstime = nbt.getLong("mstime");
            }
            
            // Read grave owner
            this.graveOwner = me.mgin.graves.compat.SerializationHelper.readGameProfile(nbt);
            
            // Read custom name
            if (nbt.contains("CustomName")) {
                this.customName = nbt.getString("CustomName");
            }
            
            // Read grave skull
            if (nbt.contains("GraveSkull")) {
                this.graveSkull = nbt.getCompound("GraveSkull");
            }
            
            // Read inventories
            me.mgin.graves.compat.SerializationHelper.readInventories(nbt, this.inventories, registryLookup);
            
            // Debug output
            System.out.println("Successfully read grave NBT with " + this.inventories.size() + " inventories");
            
            // Mark as dirty to ensure changes are saved
            this.markDirty();
        } catch (Exception e) {
            System.err.println("Error in readNbt: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Converts this grave entity to an NBT compound
     */
    public NbtCompound toNbt(RegistryWrapper.WrapperLookup registryLookup) {
        NbtCompound nbt = new NbtCompound();
        
        try {
            // Write basic properties
            nbt.putInt("XP", this.xp);
            System.out.println("DEBUG: Writing XP to NBT: " + this.xp);
            nbt.putInt("noDecay", this.noDecay);
            nbt.putLong("mstime", this.mstime);
            
            // Write grave owner
            if (this.graveOwner != null) {
                me.mgin.graves.compat.SerializationHelper.writeGameProfile(nbt, this.graveOwner);
            }
            
            // Write custom name
            if (this.customName != null) {
                nbt.putString("CustomName", this.customName);
            }
            
            // Write grave skull
            if (this.graveSkull != null) {
                nbt.put("GraveSkull", this.graveSkull);
            }
            
            // Write block state
            if (this.state != null) {
                NbtCompound stateNbt = new NbtCompound();
                stateNbt.putString("Name", this.state.getBlock().toString());
                
                // Store properties if any
                if (!this.state.getEntries().isEmpty()) {
                    NbtCompound propertiesNbt = new NbtCompound();
                    for (Property<?> property : this.state.getProperties()) {
                        String name = property.getName();
                        String value = this.state.get(property).toString();
                        propertiesNbt.putString(name, value);
                    }
                    stateNbt.put("Properties", propertiesNbt);
                }
                
                nbt.put("BlockState", stateNbt);
            }
            
            // Write inventories
            me.mgin.graves.compat.SerializationHelper.writeInventories(nbt, this.inventories, registryLookup);
            
            // Debug output
            System.out.println("Successfully created grave NBT with " + this.inventories.size() + " inventories");
        } catch (Exception e) {
            System.err.println("Error in toNbt: " + e.getMessage());
            e.printStackTrace();
        }
        
        return nbt;
    }
    
    /**
     * Simple version of toNbt without registry lookup
     */
    public NbtCompound toNbt() {
        return toNbt(me.mgin.graves.compat.SerializationHelper.getWrapperLookup());
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        // Use a simple approach that works in both versions
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return this.toNbt(registryLookup);
    }

    /**
     * Directly loads inventories from NBT data, bypassing the normal deserialization process.
     * This is used as a fallback when normal deserialization fails.
     */
    public void loadInventoriesFromNbt(NbtCompound nbt) {
        try {
            // Check if the NBT has the Inventories compound
            if (nbt.contains("Inventories")) {
                NbtCompound inventoriesTag = nbt.getCompound("Inventories");
                
                // Process each inventory
                for (String key : inventoriesTag.getKeys()) {
                    try {
                        // Get the inventory list
                        NbtList itemsList = inventoriesTag.getList(key, 10); // 10 is the type ID for compound tags
                        
                        // Find the maximum slot index to determine inventory size
                        int maxSlot = -1;
                        for (int i = 0; i < itemsList.size(); i++) {
                            NbtCompound itemTag = itemsList.getCompound(i);
                            if (itemTag.contains("Slot")) {
                                int slot = itemTag.getInt("Slot");
                                maxSlot = Math.max(maxSlot, slot);
                            }
                        }
                        
                        // Create inventory of appropriate size
                        if (maxSlot >= 0) {
                            DefaultedList<ItemStack> inventory = DefaultedList.ofSize(maxSlot + 1, ItemStack.EMPTY);
                            
                            // Fill inventory with items
                            for (int i = 0; i < itemsList.size(); i++) {
                                NbtCompound itemTag = itemsList.getCompound(i);
                                if (itemTag.contains("Slot")) {
                                    int slot = itemTag.getInt("Slot");
                                    
                                    // Create the ItemStack using reflection to handle different Minecraft versions
                                    ItemStack stack = ItemStack.EMPTY;
                                    try {
                                        // Try to get a WrapperLookup if available
                                        RegistryWrapper.WrapperLookup registryLookup = null;
                                        if (this.world != null) {
                                            try {
                                                java.lang.reflect.Method getWrapperLookupMethod = this.world.getRegistryManager().getClass().getMethod("getWrapperLookup");
                                                registryLookup = (RegistryWrapper.WrapperLookup) getWrapperLookupMethod.invoke(this.world.getRegistryManager());
                                            } catch (Exception e) {
                                                // Ignore and try without registry lookup
                                            }
                                        }
                                        
                                        // Try to create the ItemStack using the appropriate method
                                        if (registryLookup != null) {
                                            try {
                                                java.lang.reflect.Method fromNbtMethod = ItemStack.class.getMethod("fromNbt", NbtCompound.class, RegistryWrapper.WrapperLookup.class);
                                                stack = (ItemStack) fromNbtMethod.invoke(null, itemTag, registryLookup);
                                            } catch (Exception e) {
                                                // Fall back to the old method
                                                java.lang.reflect.Method fromNbtMethod = ItemStack.class.getMethod("fromNbt", NbtCompound.class);
                                                stack = (ItemStack) fromNbtMethod.invoke(null, itemTag);
                                            }
                                        } else {
                                            // Use the old method
                                            java.lang.reflect.Method fromNbtMethod = ItemStack.class.getMethod("fromNbt", NbtCompound.class);
                                            stack = (ItemStack) fromNbtMethod.invoke(null, itemTag);
                                        }
                                    } catch (Exception e) {
                                        System.err.println("Error creating ItemStack from NBT: " + e.getMessage());
                                        
                                        // Try a more basic approach as a last resort
                                        if (itemTag.contains("id")) {
                                            try {
                                                String id = itemTag.getString("id");
                                                int count = itemTag.contains("Count") ? itemTag.getInt("Count") : 1;
                                                
                                                // Try to get the item from the registry using reflection
                                                Class<?> registryClass = Class.forName("net.minecraft.registry.Registries");
                                                Object itemRegistry = registryClass.getField("ITEM").get(null);
                                                java.lang.reflect.Method getMethod = itemRegistry.getClass().getMethod("get", net.minecraft.util.Identifier.class);
                                                Object item = getMethod.invoke(itemRegistry, new net.minecraft.util.Identifier(id));
                                                
                                                // Create an ItemStack with the item
                                                stack = new ItemStack((net.minecraft.item.Item) item, count);
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
                            
                            // Set the inventory in this grave entity
                            this.setInventory(key, inventory);
                        }
                    } catch (Exception e) {
                        System.err.println("Error loading inventory " + key + ": " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Critical error in loadInventoriesFromNbt: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
