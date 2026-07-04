package net.ltxprogrammer.changed.advancements.critereon;

import com.google.common.collect.ImmutableSet;
import com.google.gson.*;
import net.ltxprogrammer.changed.advancements.MatchMode;
import net.ltxprogrammer.changed.data.RegistryElementPredicate;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.minecraft.util.GsonHelper;

import javax.annotation.Nullable;
import java.util.Set;

public class TransfurPredicate {
    public static final TransfurPredicate ANY = new TransfurPredicate();

    private final MatchMode matchMode;
    private final Set<RegistryElementPredicate<TransfurVariant<?>>> elementPredicates;

    public TransfurPredicate() {
        this.matchMode = MatchMode.ALL_OF;
        this.elementPredicates = Set.of();
    }

    public TransfurPredicate(MatchMode matchMode, Set<RegistryElementPredicate<TransfurVariant<?>>> elementPredicates) {
        this.matchMode = matchMode;
        this.elementPredicates = elementPredicates;
    }

    public boolean matches(TransfurVariantInstance<?> form) {
        if (this == ANY)
            return true;
        var variant = form.getParent();
        return matchMode.apply(elementPredicates.stream(), predicate -> predicate.test(variant));
    }

    public static TransfurPredicate fromJson(@Nullable JsonElement json) {
        if (json != null && !json.isJsonNull()) {
            JsonObject jsonObject = GsonHelper.convertToJsonObject(json, "form");

            MatchMode matchMode = MatchMode.ANY_OF;
            ImmutableSet.Builder<RegistryElementPredicate<TransfurVariant<?>>> builder = ImmutableSet.builder();
            if (jsonObject.has("variants")) {
                JsonArray variants = jsonObject.getAsJsonArray("variants");
                variants.forEach(element ->
                        builder.add(RegistryElementPredicate.parseStringElementOrTag(ChangedRegistry.TRANSFUR_VARIANT.get(), element.getAsString()))
                );
            } else {
                matchMode = MatchMode.ALL_OF; // Default eval to true if there are no variants
            }

            if (jsonObject.has("match")) {
                matchMode = MatchMode.fromSerial(GsonHelper.getAsString(jsonObject, "match"))
                        .getOrThrow(false, JsonSyntaxException::new);
            }

            var builtSet = builder.build();
            if (matchMode != MatchMode.ALL_OF && builtSet.isEmpty())
                throw new IllegalArgumentException(matchMode.getSerializedName() + " match mode cannot be used with an empty set of variants");
            if (matchMode == MatchMode.ALL_OF && builtSet.isEmpty())
                return ANY;
            return new TransfurPredicate(matchMode, builtSet);
        } else {
            return ANY;
        }
    }

    public JsonElement serializeToJson() {
        if (this == ANY)
            return JsonNull.INSTANCE;
        else {
            JsonObject jsonObject = new JsonObject();
            if (!this.elementPredicates.isEmpty()) {
                JsonArray jsonArray = new JsonArray();

                for (var predicate : this.elementPredicates) {
                    jsonArray.add(predicate.toString());
                }

                jsonObject.add("variants", jsonArray);
            }

            jsonObject.addProperty("match", this.matchMode.getSerializedName());
            return jsonObject;
        }
    }
}
