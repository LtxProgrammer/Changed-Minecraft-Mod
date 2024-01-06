package net.ltxprogrammer.changed.extension.rei;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultCraftingDisplay;
import net.ltxprogrammer.changed.recipe.InfuserRecipe;

import java.util.Collections;
import java.util.Optional;

public class InfuserRecipeDisplay extends DefaultCraftingDisplay<InfuserRecipe> {
    public InfuserRecipeDisplay(InfuserRecipe recipe) {
        super(
                EntryIngredients.ofIngredients(recipe.getIngredients()),
                Collections.singletonList(EntryIngredients.ofItemStacks(recipe.getPossibleResults())),
                Optional.of(recipe)
        );
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return ChangedReiPlugin.INFUSER;
    }

    @Override
    public int getWidth() {
        return getInputEntries().size() > 4 ? 3 : 2;
    }

    @Override
    public int getHeight() {
        return getInputEntries().size() > 4 ? 3 : 2;
    }

    @Override
    public int getInputWidth(int craftingWidth, int craftingHeight) {
        return craftingWidth * craftingHeight <= getInputEntries().size() ? craftingWidth : Math.min(getInputEntries().size(), 3);
    }

    @Override
    public int getInputHeight(int craftingWidth, int craftingHeight) {
        return (int) Math.ceil(getInputEntries().size() / (double) getInputWidth(craftingWidth, craftingHeight));
    }

    @Override
    public boolean isShapeless() {
        return true;
    }
}
