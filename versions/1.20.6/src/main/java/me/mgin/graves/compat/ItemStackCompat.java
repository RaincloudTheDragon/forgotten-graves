package me.mgin.graves.compat;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

/**
 * Compatibility layer for ItemStack operations that have changed in Minecraft 1.20.5
 */
public class ItemStackCompat {

    /**
     * Checks if an ItemStack has a custom name
     */
    public static boolean hasCustomName(ItemStack stack) {
        try {
            // Try the direct method first (pre-1.20.5)
            try {
                return (boolean) stack.getClass().getMethod("hasCustomName").invoke(stack);
            } catch (NoSuchMethodException e) {
                // Fall back to checking if the display name is different from the default name
                Text displayName = stack.getName();
                Text defaultName = (Text) stack.getItem().getClass().getMethod("getName", ItemStack.class).invoke(stack.getItem(), stack);
                return !displayName.equals(defaultName);
            }
        } catch (Exception e) {
            // Log the error and return a safe default
            System.err.println("Error checking custom name: " + e.getMessage());
            return false;
        }
    }

    /**
     * Gets the NBT data from an ItemStack
     */
    public static NbtCompound getNbt(ItemStack stack) {
        try {
            // Try the direct method first (pre-1.20.5)
            try {
                return (NbtCompound) stack.getClass().getMethod("getNbt").invoke(stack);
            } catch (NoSuchMethodException e) {
                // Fall back to the new method (1.20.5+)
                NbtCompound nbt = new NbtCompound();
                stack.getClass().getMethod("writeNbt", NbtCompound.class).invoke(stack, nbt);
                return nbt;
            }
        } catch (Exception e) {
            // Log the error and return a safe default
            System.err.println("Error getting NBT data: " + e.getMessage());
            return new NbtCompound();
        }
    }

    /**
     * Damages an ItemStack
     */
    public static void damage(ItemStack stack, int amount, LivingEntity entity, Hand hand) {
        try {
            // Try different method signatures
            try {
                // Try the 1.20.5+ method signature with reflection for sendToolBreakStatus
                stack.getClass().getMethod("damage", int.class, LivingEntity.class, java.util.function.Consumer.class)
                    .invoke(stack, amount, entity, (java.util.function.Consumer<LivingEntity>) (p) -> {
                        if (p instanceof PlayerEntity) {
                            try {
                                // Use reflection to call sendToolBreakStatus
                                p.getClass().getMethod("sendToolBreakStatus", Hand.class).invoke(p, hand);
                            } catch (Exception ex) {
                                System.err.println("Error sending tool break status: " + ex.getMessage());
                            }
                        }
                    });
            } catch (NoSuchMethodException e) {
                // Fall back to a simpler approach
                try {
                    // Just damage the item without the callback
                    stack.getClass().getMethod("damage", int.class, LivingEntity.class).invoke(stack, amount, entity);
                } catch (NoSuchMethodException ex) {
                    // If all else fails, try to decrement the item's durability directly
                    int damage = (int) stack.getClass().getMethod("getDamage").invoke(stack);
                    stack.getClass().getMethod("setDamage", int.class).invoke(stack, damage + amount);
                }
            }
        } catch (Exception e) {
            // Log the error
            System.err.println("Error damaging item: " + e.getMessage());
        }
    }
} 