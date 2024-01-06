package net.ltxprogrammer.changed.extension.rei;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultCraftingDisplay;
import net.ltxprogrammer.changed.recipe.InfuserRecipe;
import net.ltxprogrammer.changed.recipe.PurifierRecipe;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Collections;
import java.util.Optional;

public class PurifierRecipeDisplay extends DefaultCraftingDisplay<PurifierRecipe> {
    public PurifierRecipeDisplay(PurifierRecipe recipe) {
        super(
                Collections.singletonList(EntryIngredients.ofIngredient(recipe.getIngredient())),
                Collections.singletonList(EntryIngredients.of(recipe.getResult())),
                Optional.of(recipe)
        );
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return ChangedReiPlugin.PURIFIER;
    }

    @Override
    public int getWidth() {
        return 1;
    }

    @Override
    public int getHeight() {
        return 1;
    }

    @Override
    public int getInputWidth(int craftingWidth, int craftingHeight) {
        return 1;
    }

    @Override
    public int getInputHeight(int craftingWidth, int craftingHeight) {
        return 1;
    }

    @Override
    public boolean isShapeless() {
        return true;
    }
}
