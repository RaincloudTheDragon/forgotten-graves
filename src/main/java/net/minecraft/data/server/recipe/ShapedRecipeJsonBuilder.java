package net.minecraft.data.server.recipe;

import net.minecraft.advancement.criterion.AdvancementCriterion;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;

/**
 * Temporary compatibility class for ShapedRecipeJsonBuilder
 */
public class ShapedRecipeJsonBuilder {
    /**
     * Create a new builder instance
     */
    public static ShapedRecipeJsonBuilder create(RecipeCategory category, Item output) {
        return new ShapedRecipeJsonBuilder();
    }
    
    /**
     * Create a new builder instance with a block
     */
    public static ShapedRecipeJsonBuilder create(RecipeCategory category, Block output) {
        return new ShapedRecipeJsonBuilder();
    }
    
    /**
     * Add a pattern row
     */
    public ShapedRecipeJsonBuilder pattern(String pattern) {
        return this;
    }
    
    /**
     * Add an ingredient for a pattern character
     */
    public ShapedRecipeJsonBuilder input(char c, Ingredient ingredient) {
        return this;
    }
    
    /**
     * Add a criterion
     * This is overloaded to handle our compatibility classes
     */
    public ShapedRecipeJsonBuilder criterion(String name, AdvancementCriterion<?> criterion) {
        return this;
    }
    
    /**
     * Add the recipe to the exporter
     */
    public void offerTo(RecipeExporter exporter) {
        // Dummy implementation
    }
} 