package net.ltxprogrammer.changed.item.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.logging.LogUtils;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedLootItemFunctions;
import net.ltxprogrammer.changed.item.Syringe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class RandomVariantFunction extends LootItemConditionalFunction {
    private static final Logger LOGGER = LogUtils.getLogger();
    final List<ResourceLocation> variants;

    RandomVariantFunction(LootItemCondition[] p_80418_, List<ResourceLocation> variant) {
        super(p_80418_);
        this.variants = variant;
    }

    public LootItemFunctionType getType() {
        return ChangedLootItemFunctions.RANDOM_VARIANT;
    }

    public ItemStack run(ItemStack itemStack, LootContext context) {
        if (variants != null && !variants.isEmpty()) {
            Syringe.setUnpureVariant(itemStack, variants.get(context.getRandom().nextInt(variants.size())));
        }
        return itemStack;
    }

    public static class Builder extends LootItemConditionalFunction.Builder<RandomVariantFunction.Builder> {
        private List<ResourceLocation> variants = new ArrayList<>();

        protected RandomVariantFunction.Builder getThis() {
            return this;
        }

        public RandomVariantFunction.Builder withVariant(LatexVariant<?> variant) {
            this.variants.add(variant.getFormId());
            return this;
        }

        public RandomVariantFunction.Builder withVariant(ResourceLocation variant) {
            this.variants.add(variant);
            return this;
        }

        public RandomVariantFunction.Builder withAllVariants() {
            LatexVariant.PUBLIC_LATEX_FORMS.forEach((name, variant) -> {
                this.variants.add(name);
            });
            return this;
        }

        public LootItemFunction build() {
            return buildTyped();
        }

        public RandomVariantFunction buildTyped() {
            return new RandomVariantFunction(this.getConditions(), this.variants);
        }
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<RandomVariantFunction> {
        public void serialize(JsonObject json, RandomVariantFunction function, JsonSerializationContext context) {
            super.serialize(json, function, context);
            if (function.variants != null) {
                json.addProperty("variant", function.variants.toString());
            }
        }

        public RandomVariantFunction deserialize(JsonObject json, JsonDeserializationContext context, LootItemCondition[] lootItemConditions) {
            Builder builder = new Builder();
            ResourceLocation variant = null;
            if (json.has("variants")) {
                for (var element : GsonHelper.getAsJsonArray(json, "variants"))
                    builder.withVariant(ResourceLocation.tryParse(element.getAsString()));
            }

            else {
                builder.withAllVariants();
            }

            return builder.buildTyped();
        }
    }
}