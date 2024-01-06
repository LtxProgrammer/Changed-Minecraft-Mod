package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.recipe.InfuserRecipe;
import net.ltxprogrammer.changed.recipe.PurifierRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ChangedRecipeSerializers {
    public static DeferredRegister<RecipeSerializer<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Changed.MODID);

    public static RegistryObject<RecipeSerializer<InfuserRecipe>> INFUSER_RECIPE = REGISTRY.register("infuser", InfuserRecipe.Serializer::new);
    public static RegistryObject<RecipeSerializer<PurifierRecipe>> PURIFIER_RECIPE = REGISTRY.register("purifier", PurifierRecipe.Serializer::new);
}
