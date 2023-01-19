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

public class SetVariantFunction extends LootItemConditionalFunction {
    private static final Logger LOGGER = LogUtils.getLogger();
    final ResourceLocation variant;

    SetVariantFunction(LootItemCondition[] p_80418_, ResourceLocation variant) {
        super(p_80418_);
        this.variant = variant;
    }

    public LootItemFunctionType getType() {
        return ChangedLootItemFunctions.SET_COUNT;
    }

    public ItemStack run(ItemStack itemStack, LootContext context) {
        if (variant != null)
            Syringe.setUnpureVariant(itemStack, variant);
        return itemStack;
    }

    public static class Builder extends LootItemConditionalFunction.Builder<SetVariantFunction.Builder> {
        private ResourceLocation variant = null;

        protected SetVariantFunction.Builder getThis() {
            return this;
        }

        public SetVariantFunction.Builder withVariant(LatexVariant<?> variant) {
            this.variant = variant.getFormId();
            return this;
        }

        public SetVariantFunction.Builder withVariant(ResourceLocation variant) {
            this.variant = variant;
            return this;
        }

        public LootItemFunction build() {
            return new SetVariantFunction(this.getConditions(), this.variant);
        }
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<SetVariantFunction> {
        public void serialize(JsonObject json, SetVariantFunction function, JsonSerializationContext context) {
            super.serialize(json, function, context);
            if (function.variant != null) {
                json.addProperty("variant", function.variant.toString());
            }
        }

        public SetVariantFunction deserialize(JsonObject json, JsonDeserializationContext context, LootItemCondition[] lootItemConditions) {
            ResourceLocation variant = null;
            if (json.has("variant")) {
                variant = ResourceLocation.tryParse(GsonHelper.getAsString(json, "variant"));
            }

            return new SetVariantFunction(lootItemConditions, variant);
        }
    }
}