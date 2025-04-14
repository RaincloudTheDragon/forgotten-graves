package net.fabricmc.fabric.api.datagen.v1.provider;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.Item;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;

import java.util.concurrent.CompletableFuture;

/**
 * Temporary compatibility class for FabricRecipeProvider
 * This allows compilation with both 1.20.6+ and pre-1.20.6 code
 */
public class FabricRecipeProvider {
    protected final FabricDataOutput output;
    
    /**
     * Constructor for 1.20.6+ where registry lookup is not required
     */
    public FabricRecipeProvider(FabricDataOutput output) {
        this.output = output;
    }
    
    /**
     * Constructor for pre-1.20.6 where registry lookup is required
     */
    public FabricRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        this.output = output;
    }
    
    /**
     * Generate recipes - to be overridden by subclasses
     */
    public void generate(RecipeExporter exporter) {
        // To be overridden in subclasses
    }
    
    /**
     * Helper method for recipe criteria
     */
    public static String hasItem(Item item) {
        return "has_" + item.toString();
    }
    
    /**
     * Helper method for recipe criteria
     */
    public static ItemPredicate conditionsFromItem(Item item) {
        return ItemPredicate.Builder.create().items(item).build();
    }
    
    /**
     * Helper method for recipe criteria
     */
    public static ItemPredicate conditionsFromTag(TagKey<Item> tag) {
        return ItemPredicate.Builder.create().tag(tag).build();
    }
} 