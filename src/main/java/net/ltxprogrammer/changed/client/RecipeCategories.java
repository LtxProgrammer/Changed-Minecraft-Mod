package net.ltxprogrammer.changed.client;

import com.google.common.collect.ImmutableList;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.ltxprogrammer.changed.init.ChangedRecipeTypes;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.item.AbdomenArmor;
import net.ltxprogrammer.changed.item.Syringe;
import net.ltxprogrammer.changed.item.TscWeapon;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.client.RecipeBookRegistry;
import org.apache.commons.compress.utils.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static net.minecraft.client.RecipeBookCategories.CRAFTING_EQUIPMENT;

public class RecipeCategories {
    public static final List<RecipeBookCategories> INJECTED_CATEGORIES = new ArrayList<>();
    private static RecipeBookCategories registerBookCategory(String name, ItemStack... icons) {
        var category = RecipeBookCategories.create(name, icons);
        INJECTED_CATEGORIES.add(category);
        return category;
    }

    public static <T extends Recipe<?>> void registerCategoriesFinder(RecipeType<T> recipeType,
                                                                    RecipeBookCategories coreCategory, Function<T, List<RecipeBookCategories>> finder) {
        RecipeBookRegistry.addCategoriesFinder(recipeType, recipe -> coreCategory);
        MULTICATEGORY_FINDER.add(recipe -> {
            try {
                return finder.apply((T)recipe);
            } catch (Exception ignored) {
                return Lists.newArrayList();
            }
        });
    }

    public static <T extends Recipe<?>> void registerTypeCategories(RecipeBookType bookType, RecipeType<T> recipeType,
                                                                    RecipeBookCategories coreCategory, List<RecipeBookCategories> categories, Function<T, List<RecipeBookCategories>> finder) {
        RecipeBookRegistry.addCategoriesToType(bookType, categories);
        registerCategoriesFinder(recipeType, coreCategory, finder);
    }

    public static final List<Function<Recipe<?>, List<RecipeBookCategories>>> MULTICATEGORY_FINDER = new ArrayList<>();

    public static final RecipeBookCategories INFUSER_SEARCH = registerBookCategory("CHANGED_INFUSER_SEARCH", new ItemStack(Items.COMPASS));
    public static final RecipeBookCategories INFUSER_DARK_LATEX = registerBookCategory("CHANGED_INFUSER_DARK_LATEX", new ItemStack(ChangedItems.DARK_LATEX_GOO.get()));
    public static final RecipeBookCategories INFUSER_WHITE_LATEX = registerBookCategory("CHANGED_INFUSER_WHITE_LATEX", new ItemStack(ChangedItems.WHITE_LATEX_GOO.get()));
    public static final RecipeBookCategories INFUSER_AQUATIC = registerBookCategory("CHANGED_INFUSER_AQUATIC", new ItemStack(Items.WATER_BUCKET));
    public static final RecipeBookCategories INFUSER_AERIAL = registerBookCategory("CHANGED_INFUSER_AERIAL", new ItemStack(Items.ELYTRA));
    public static final RecipeBookCategories INFUSER_GENDERED = registerBookCategory("CHANGED_INFUSER_GENDERED",
            Syringe.setVariant(
                    new ItemStack(ChangedItems.LATEX_SYRINGE.get()), LatexVariant.LATEX_SHARK.getFormId()),
            Syringe.setVariant(
                    new ItemStack(ChangedItems.LATEX_SYRINGE.get()), LatexVariant.DARK_LATEX_WOLF.female().getFormId()));

    public static void registerCategories() {
        registerTypeCategories(ChangedRecipeTypes.INFUSER_BOOK, ChangedRecipeTypes.INFUSER_RECIPE, INFUSER_SEARCH, ImmutableList.of(
                INFUSER_SEARCH, INFUSER_DARK_LATEX, INFUSER_WHITE_LATEX, INFUSER_AQUATIC, INFUSER_AERIAL, INFUSER_GENDERED
        ), recipe -> {
            ResourceLocation form = recipe.form;
            if (recipe.gendered)
                form = new ResourceLocation(form.toString() + "/male"); // Default male for preview
            LatexVariant<?> variant = ChangedRegistry.LATEX_VARIANT.get().getValue(form);
            List<RecipeBookCategories> categories = new ArrayList<>();
            if (variant == null)
                return categories;
            if (variant.getBreatheMode().canBreatheWater())
                categories.add(INFUSER_AQUATIC);
            if (variant.canGlide)
                categories.add(INFUSER_AERIAL);
            if (variant.getLatexType() == LatexType.DARK_LATEX)
                categories.add(INFUSER_DARK_LATEX);
            if (variant.getLatexType() == LatexType.WHITE_LATEX)
                categories.add(INFUSER_WHITE_LATEX);
            if (recipe.gendered)
                categories.add(INFUSER_GENDERED);

            return categories;
        });

        registerCategoriesFinder(RecipeType.CRAFTING, RecipeBookCategories.CRAFTING_SEARCH, recipe -> {
            List<RecipeBookCategories> categories = new ArrayList<>();
            if (recipe.getResultItem().getItem() instanceof AbdomenArmor)
                categories.add(CRAFTING_EQUIPMENT);
            else if (recipe.getResultItem().getItem() instanceof TscWeapon)
                categories.add(CRAFTING_EQUIPMENT);
            return categories;
        });
    }
}
