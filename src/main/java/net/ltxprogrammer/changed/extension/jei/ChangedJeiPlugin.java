package net.ltxprogrammer.changed.extension.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IIngredientManager;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.gui.InfuserScreen;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.ltxprogrammer.changed.init.ChangedRecipeTypes;
import net.ltxprogrammer.changed.item.Syringe;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;

@JeiPlugin
public class ChangedJeiPlugin implements IModPlugin {
    InfuserRecipeCategory infuserRecipeCategory;
    PurifierRecipeCategory purifierRecipeCategory;

    @Override
    public ResourceLocation getPluginUid() {
        return Changed.modResource("jei_plugin");
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ChangedItems.LATEX_SYRINGE.get(), (itemStack, context) -> {
            var variant = Syringe.getVariant(itemStack);
            if (variant != null)
                return variant.getFormId().toString();
            else
                return IIngredientSubtypeInterpreter.NONE;
        });
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ChangedItems.LATEX_TIPPED_ARROW.get(), (itemStack, context) -> {
            var variant = Syringe.getVariant(itemStack);
            if (variant != null)
                return variant.getFormId().toString();
            else
                return IIngredientSubtypeInterpreter.NONE;
        });
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {

    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var guiHelper = registration.getJeiHelpers().getGuiHelper();

        registration.addRecipeCategories(
                infuserRecipeCategory = new InfuserRecipeCategory(guiHelper)
        );
        registration.addRecipeCategories(
                purifierRecipeCategory = new PurifierRecipeCategory(guiHelper)
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        IIngredientManager ingredientManager = registration.getIngredientManager();
        IIngredientHelper<ItemStack> ingredientHelper = ingredientManager.getIngredientHelper(VanillaTypes.ITEM_STACK);
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        registration.addRecipes(infuserRecipeCategory.getRecipeType(), recipeManager.getAllRecipesFor(ChangedRecipeTypes.INFUSER_RECIPE));
        registration.addRecipes(purifierRecipeCategory.getRecipeType(), recipeManager.getAllRecipesFor(ChangedRecipeTypes.PURIFIER_RECIPE));
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(InfuserScreen.class, 88, 32, 28, 23, RecipeTypes.CRAFTING);
    }
}
