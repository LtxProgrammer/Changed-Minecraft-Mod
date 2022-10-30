package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.recipe.InfuserRecipes;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ChangedRecipeSerializers {
    public static DeferredRegister<RecipeSerializer<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Changed.MODID);

    public static RegistryObject<RecipeSerializer<InfuserRecipes.InfuserRecipe>> INFUSER_RECIPE = REGISTRY.register("infuser", InfuserRecipes.InfuserRecipe.Serializer::new);
}
