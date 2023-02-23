package net.ltxprogrammer.changed.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.ints.IntList;
import net.ltxprogrammer.changed.entity.Gender;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.*;
import net.ltxprogrammer.changed.util.MapUtil;
import net.ltxprogrammer.changed.util.TagUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class InfuserRecipes {
    public static class InfuserRecipe implements Recipe<SimpleContainer> {
        static int MAX_WIDTH = 3;
        static int MAX_HEIGHT = 3;
        private final ResourceLocation id;
        final String group;
        public final boolean gendered;
        public final ResourceLocation form;
        final NonNullList<Ingredient> ingredients;
        private final boolean isSimple;

        public static final Map<? extends Item, Function<ItemStack, ItemStack>> INFUSER_BASE_CONVERSION =
                new MapUtil.HashBuilder<Item, Function<ItemStack, ItemStack>>()
                .put(Items.ARROW, stack -> new ItemStack(ChangedItems.LATEX_TIPPED_ARROW.get(), Math.min(stack.getCount(), 16)))
                .put(ChangedItems.BLOOD_SYRINGE.get(), stack -> new ItemStack(ChangedItems.LATEX_SYRINGE.get()))
                .finish();

        public static ItemStack getBaseFor(ItemStack stack) {
            var func = INFUSER_BASE_CONVERSION.get(stack.getItem());
            if (func != null)
                return func.apply(stack);
            else
                return ItemStack.EMPTY;
        }

        public static List<ItemStack> getAllowedInputs() {
            List<ItemStack> list = new ArrayList<>();
            INFUSER_BASE_CONVERSION.keySet().forEach(key -> {
                list.add(new ItemStack(key));
            });
            return list;
        }

        public List<ItemStack> getPossibleResults() {
            List<ItemStack> list = new ArrayList<>();
            getAllowedInputs().forEach(baseItem -> {
                baseItem.setCount(baseItem.getMaxStackSize());
                var newItem = INFUSER_BASE_CONVERSION.get(baseItem.getItem()).apply(baseItem);

                if (gendered) {
                    for (Gender gender : Gender.values()) {
                        list.add(processItem(newItem.copy(), gender));
                    }
                }

                else {
                    list.add(processItem(newItem, Gender.MALE));
                }
            });
            return list;
        }

        public InfuserRecipe(ResourceLocation id, String group, boolean gendered, ResourceLocation form, NonNullList<Ingredient> ingredients) {
            this.id = id;
            this.group = group;
            this.gendered = gendered;
            this.form = form;
            this.ingredients = ingredients;
            this.isSimple = ingredients.stream().allMatch(Ingredient::isSimple);
        }

        public ResourceLocation getId() {
            return this.id;
        }

        public RecipeSerializer<?> getSerializer() {
            return ChangedRecipeSerializers.INFUSER_RECIPE.get();
        }

        public String getGroup() {
            return this.group;
        }

        public ItemStack getResultItem() {
            return new ItemStack(ChangedItems.LATEX_SYRINGE.get());
        }

        public ItemStack processItem(ItemStack stack, Gender gender) {
            CompoundTag tag = stack.getOrCreateTag();
            if (gendered)
                TagUtil.putResourceLocation(tag, "form", gender.convertToGendered(form));
            else
                TagUtil.putResourceLocation(tag, "form", form);
            tag.putBoolean("safe", false);
            return stack;
        }

        public NonNullList<Ingredient> getIngredients() {
            return this.ingredients;
        }

        public boolean matches(SimpleContainer p_44262_, Level p_44263_) {
            StackedContents stackedcontents = new StackedContents();
            java.util.List<ItemStack> inputs = new java.util.ArrayList<>();
            int i = 0;

            for(int j = 0; j < p_44262_.getContainerSize(); ++j) {
                ItemStack itemstack = p_44262_.getItem(j);
                if (!itemstack.isEmpty()) {
                    ++i;
                    if (isSimple)
                        stackedcontents.accountStack(itemstack, 1);
                    else inputs.add(itemstack);
                }
            }

            return i == this.ingredients.size() && (isSimple ? stackedcontents.canCraft(this, (IntList)null) : net.minecraftforge.common.util.RecipeMatcher.findMatches(inputs,  this.ingredients) != null);
        }

        public ItemStack assemble(SimpleContainer p_44260_) {
            return this.getResultItem();
        }

        public boolean canCraftInDimensions(int p_44252_, int p_44253_) {
            return p_44252_ * p_44253_ >= this.ingredients.size();
        }

        public Component getNameFor(Level level, Gender gender) {
            ResourceLocation formId = form;
            if (gendered)
                formId = new ResourceLocation(formId + "/" + gender.toString().toLowerCase());
            LatexVariant<?> variant = ChangedRegistry.LATEX_VARIANT.get().getValue(formId);
            if (variant == null)
                return new TranslatableComponent("syringe." + form);
            LatexEntity entity = ChangedEntities.getCachedEntity(level, variant.getEntityType());
            Component component = entity.getDisplayName();
            entity.remove(Entity.RemovalReason.DISCARDED);
            return component;
        }

        public static class Serializer extends net.minecraftforge.registries.ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<InfuserRecipe> {
            public InfuserRecipe fromJson(ResourceLocation p_44290_, JsonObject p_44291_) {
                String s = GsonHelper.getAsString(p_44291_, "group", "");
                NonNullList<Ingredient> nonnulllist = itemsFromJson(GsonHelper.getAsJsonArray(p_44291_, "ingredients"));
                if (nonnulllist.isEmpty()) {
                    throw new JsonParseException("No ingredients for infuser recipe");
                } else if (nonnulllist.size() > MAX_WIDTH * MAX_HEIGHT) {
                    throw new JsonParseException("Too many ingredients for infuser recipe. The maximum is " + (MAX_WIDTH * MAX_HEIGHT));
                } else {
                    boolean gendered = GsonHelper.getAsBoolean(p_44291_, "gendered", false);
                    ResourceLocation form = new ResourceLocation(GsonHelper.getAsString(p_44291_, "form"));
                    return new InfuserRecipe(p_44290_, s, gendered, form, nonnulllist);
                }
            }

            private static NonNullList<Ingredient> itemsFromJson(JsonArray p_44276_) {
                NonNullList<Ingredient> nonnulllist = NonNullList.create();

                for (int i = 0; i < p_44276_.size(); ++i) {
                    Ingredient ingredient = Ingredient.fromJson(p_44276_.get(i));
                    if (net.minecraftforge.common.ForgeConfig.SERVER.skipEmptyShapelessCheck.get() || !ingredient.isEmpty()) {
                        nonnulllist.add(ingredient);
                    }
                }

                return nonnulllist;
            }

            public InfuserRecipe fromNetwork(ResourceLocation p_44293_, FriendlyByteBuf p_44294_) {
                String s = p_44294_.readUtf();
                int i = p_44294_.readVarInt();
                NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i, Ingredient.EMPTY);

                for (int j = 0; j < nonnulllist.size(); ++j) {
                    nonnulllist.set(j, Ingredient.fromNetwork(p_44294_));
                }

                boolean gendered = p_44294_.readBoolean();
                ResourceLocation form = p_44294_.readResourceLocation();
                return new InfuserRecipe(p_44293_, s, gendered, form, nonnulllist);
            }

            public void toNetwork(FriendlyByteBuf p_44281_, InfuserRecipe p_44282_) {
                p_44281_.writeUtf(p_44282_.group);
                p_44281_.writeVarInt(p_44282_.ingredients.size());

                for (Ingredient ingredient : p_44282_.ingredients) {
                    ingredient.toNetwork(p_44281_);
                }

                p_44281_.writeBoolean(p_44282_.gendered);
                p_44281_.writeResourceLocation(p_44282_.form);
            }
        }

        @Override
        public RecipeType<?> getType() {
            return ChangedRecipeTypes.INFUSER_RECIPE;
        }
    }
}
