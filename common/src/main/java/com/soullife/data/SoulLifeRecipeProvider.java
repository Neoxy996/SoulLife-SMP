package com.soullife.data;

import java.util.function.Consumer;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Items;

public class SoulLifeRecipeProvider extends RecipeProvider {

    public SoulLifeRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> recipeOutput) {
        // Dragon Egg Recipe
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.DRAGON_EGG)
            .pattern("cec")
            .pattern("ene")
            .pattern("cic")
            .define('c', Items.CRYING_OBSIDIAN)
            .define('e', Items.END_CRYSTAL)
            .define('n', Items.NETHER_STAR)
            .define('i', Items.NETHERITE_INGOT)
            .unlockedBy("has_nether_star", has(Items.NETHER_STAR))
            .save(recipeOutput);
    }
}
