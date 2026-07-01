package net.ltxprogrammer.changed.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.ltxprogrammer.changed.init.ChangedRecipeSerializers;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class NameVariantRecipe implements CraftingRecipe {
    private final ResourceLocation id;
    final String group;
    final Item variantHoldingItem;
    final ItemStack displayItem;

    public NameVariantRecipe(ResourceLocation id, String group, Item variantHoldingItem) {
        this.id = id;
        this.group = group;
        this.variantHoldingItem = variantHoldingItem;
        this.displayItem = new ItemStack(variantHoldingItem);
        this.displayItem.getOrCreateTag().putString("variantName", "Your name here");
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public CraftingBookCategory category() {
        return CraftingBookCategory.MISC;
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        int nameTagStacks = 0;
        int itemStacks = 0;

        for(int j = 0; j < container.getContainerSize(); ++j) {
            ItemStack itemstack = container.getItem(j);
            if (!itemstack.isEmpty()) {
                if (itemstack.is(Items.NAME_TAG) && itemstack.hasCustomHoverName())
                    ++nameTagStacks;
                else if (itemstack.is(variantHoldingItem)) {
                    ++itemStacks;
                } else {
                    return false;
                }
            }
        }

        return nameTagStacks == 1 && itemStacks == 1;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        ItemStack variantHoldingStack = ItemStack.EMPTY;
        ItemStack nameTagStack = ItemStack.EMPTY;

        for (int j = 0; j < container.getContainerSize(); ++j) {
            ItemStack itemstack = container.getItem(j);
            if (!itemstack.isEmpty()) {
                if (itemstack.is(Items.NAME_TAG))
                    nameTagStack = itemstack;
                else if (itemstack.is(variantHoldingItem))
                    variantHoldingStack = itemstack;
            }
        }

        var copy = variantHoldingStack.copy();
        copy.getOrCreateTag().put("variantName", nameTagStack.getTagElement("display").get("Name"));
        return copy;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return displayItem;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ChangedRecipeSerializers.NAME_VARIANT_RECIPE.get();
    }

    @Override
    public @NotNull String getGroup() {
        return group;
    }

    public static class Serializer implements RecipeSerializer<NameVariantRecipe> {
        public NameVariantRecipe fromJson(ResourceLocation id, JsonObject json) {
            String group = GsonHelper.getAsString(json, "group", "");
            Item variantHoldingItem = ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(json.get("item").getAsString()));
            assert variantHoldingItem != null;
            return new NameVariantRecipe(id, group, variantHoldingItem);
        }

        public NameVariantRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            String group = buffer.readUtf();
            Item item = ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(buffer.readUtf()));
            return new NameVariantRecipe(id, group, item);
        }

        public void toNetwork(FriendlyByteBuf buffer, NameVariantRecipe recipe) {
            buffer.writeUtf(recipe.group);
            buffer.writeUtf(ForgeRegistries.ITEMS.getKey(recipe.variantHoldingItem).toString());
        }
    }
}
