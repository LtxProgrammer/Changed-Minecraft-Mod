package net.ltxprogrammer.changed.mixin;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import net.ltxprogrammer.changed.client.RecipeCategories;
import net.ltxprogrammer.changed.init.ChangedRecipeTypes;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;

@Mixin(ClientRecipeBook.class)
public abstract class ClientRecipeBookMixin extends RecipeBook {
    @Inject(method = "categorizeAndGroupRecipes", at = @At("RETURN")) // Inject multicategory recipes after processing
    private static void categorizeAndGroupRecipes(Iterable<Recipe<?>> p_90643_, CallbackInfoReturnable<Map<RecipeBookCategories, List<List<Recipe<?>>>>> ci) {
        Map<RecipeBookCategories, List<List<Recipe<?>>>> map = ci.getReturnValue();
        Table<RecipeBookCategories, String, List<Recipe<?>>> table = HashBasedTable.create();

        for(Recipe<?> recipe : p_90643_) {
            if (!recipe.isSpecial() && !recipe.isIncomplete()) {
                List<RecipeBookCategories> categories = null;
                for (var func : RecipeCategories.MULTICATEGORY_FINDER) {
                    categories = func.apply(recipe);
                    if (categories != null && !categories.isEmpty())
                        break;
                }
                if (categories == null)
                    break;

                for (var recipebookcategories : categories) {
                    String s = recipe.getGroup();
                    if (s.isEmpty()) {
                        map.computeIfAbsent(recipebookcategories, (p_90645_) -> Lists.newArrayList()).add(ImmutableList.of(recipe));
                    } else {
                        List<Recipe<?>> list = table.get(recipebookcategories, s);
                        if (list == null) {
                            list = Lists.newArrayList();
                            table.put(recipebookcategories, s, list);
                            map.computeIfAbsent(recipebookcategories, (p_90641_) -> Lists.newArrayList()).add(list);
                        }

                        list.add(recipe);
                    }
                }
            }
        }
    }
}
