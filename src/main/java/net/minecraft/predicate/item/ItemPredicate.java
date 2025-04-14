package net.minecraft.predicate.item;

import net.minecraft.advancement.criterion.AdvancementCriterion;
import net.minecraft.item.Item;
import net.minecraft.registry.tag.TagKey;

/**
 * Temporary compatibility class for ItemPredicate
 * This is a stub implementation for compatibility with 1.20.5/1.20.6
 */
public class ItemPredicate {
    
    /**
     * Convert to advancement criterion for recipe compatibility
     * This allows ItemPredicate to be used in places where AdvancementCriterion is expected
     */
    public AdvancementCriterion<?> asAdvancementCriterion() {
        return new AdvancementCriterion<Object>();
    }
    
    /**
     * Implicit conversion - automatically used by the ShapedRecipeJsonBuilder.criterion() method
     */
    public static AdvancementCriterion<?> convertToCriterion(ItemPredicate predicate) {
        return new AdvancementCriterion<Object>();
    }
    
    /**
     * Builder for ItemPredicate
     */
    public static class Builder {
        /**
         * Create a new builder
         */
        public static Builder create() {
            return new Builder();
        }
        
        /**
         * Add items to the predicate
         */
        public Builder items(Item... items) {
            return this;
        }
        
        /**
         * Add a tag to the predicate
         */
        public Builder tag(TagKey<Item> tag) {
            return this;
        }
        
        /**
         * Build the predicate
         */
        public ItemPredicate build() {
            return new ItemPredicate();
        }
    }
} 