package net.ltxprogrammer.changed.extension.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.ltxprogrammer.changed.recipe.InfuserRecipe;
import net.ltxprogrammer.changed.recipe.PurifierRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PurifierRecipeCategory implements IRecipeCategory<PurifierRecipe> {
    private static final int craftOutputSlot = 0;
    private static final int craftInputSlot1 = 1;

    public static final int WIDTH = 116;
    public static final int HEIGHT = 54;

    private final IDrawable background;
    private final IDrawable icon;
    private final TranslatableComponent localizedName;
    private final ICraftingGridHelper craftingGridHelper;

    public PurifierRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation location = new ResourceLocation("jei", "textures/gui/gui_vanilla.png");
        background = guiHelper.createDrawable(location, 0, 60, WIDTH, HEIGHT);
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ChangedBlocks.PURIFIER.get()));
        localizedName = new TranslatableComponent("container.changed.purifier");
        craftingGridHelper = guiHelper.createCraftingGridHelper(craftInputSlot1);
    }

    @Override
    public Component getTitle() {
        return localizedName;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public ResourceLocation getUid() {
        return Changed.modResource("purifier_recipe");
    }

    @Override
    public Class<? extends PurifierRecipe> getRecipeClass() {
        return PurifierRecipe.class;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, PurifierRecipe recipe, IFocusGroup focuses) {
        var ingredient = recipe.getIngredient();
        craftingGridHelper.setInputs(builder, VanillaTypes.ITEM_STACK, List.of(Arrays.asList(ingredient.getItems())), 1, 1);
        craftingGridHelper.setOutputs(builder, VanillaTypes.ITEM_STACK, List.of(new ItemStack(recipe.getResult())));
    }
}
