package me.mgin.graves.datagen;

import me.mgin.graves.block.GraveBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.tag.ItemTags;

//? if >1.20.1 {
import net.minecraft.data.server.recipe.RecipeExporter;
//?} else {
/*import net.minecraft.data.server.recipe.RecipeJsonProvider;
import java.util.function.Consumer;
*///?}

public class GraveRecipeGenerator extends FabricRecipeProvider {
    //? if >=1.20.5 {
    public GraveRecipeGenerator(FabricDataOutput output) {
        super(output);
    }
    //?} else {
    /*public GraveRecipeGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> lookup) {
        super(output, lookup);
    }
    *///?}

    @Override
    //? if >1.20.1 {
    public void generate(RecipeExporter exporter) {
    //?} else {
    /*public void generate(Consumer<RecipeJsonProvider> exporter) {
    *///?}
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, GraveBlocks.GRAVE)
            .pattern("S")
            .pattern("D")
            .input('S', Ingredient.fromTag(ItemTags.STONE_TOOL_MATERIALS))
            .input('D', Ingredient.ofItems(Items.DIRT))
            .criterion(
                FabricRecipeProvider.hasItem(Items.DIRT),
                ItemPredicate.convertToCriterion(FabricRecipeProvider.conditionsFromItem(Items.DIRT))
            )
            .criterion(
                "stone_tool_materials",
                ItemPredicate.convertToCriterion(FabricRecipeProvider.conditionsFromTag(ItemTags.STONE_TOOL_MATERIALS))
            )
            .offerTo(exporter);
    }
}
