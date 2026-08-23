package net.ltxprogrammer.changed.entity.animation;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.datagen.animations.AnimationCriteriaBuilder;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.minecraft.resources.ResourceLocation;

import java.util.Set;

public class TransfurAnimationParameters implements AnimationParameters {
    public static final Codec<TransfurAnimationParameters> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            ResourceLocation.CODEC.fieldOf("variantId").forGetter(param -> param.variantId),
            TransfurCause.CODEC.fieldOf("cause").forGetter(param -> param.cause)
    ).apply(builder, TransfurAnimationParameters::new));

    public final ResourceLocation variantId;
    public final TransfurCause cause;

    public TransfurAnimationParameters(ResourceLocation variantId, TransfurCause cause) {
        this.variantId = variantId;
        this.cause = cause;
    }

    public TransfurAnimationParameters(TransfurVariant<?> variant, TransfurCause cause) {
        this.variantId = variant.getFormId();
        this.cause = cause;
    }

    private static final ResourceLocation VARIANT_FIELD = Changed.modResource("variant");
    private static final ResourceLocation CAUSE_FIELD = Changed.modResource("cause");

    @Override
    public AnimationAssociation.Match matchesAssociation(AnimationAssociation animationSetup) {
        return animationSetup.fieldContainsResourceLocation(VARIANT_FIELD, variantId)
                .and(() -> animationSetup.fieldContainsString(CAUSE_FIELD, cause.getSerializedName()));
    }

    public static CriteriaBuilder criteriaBuilder() {
        return new CriteriaBuilder();
    }

    public static class CriteriaBuilder implements AnimationCriteriaBuilder {
        private final Set<ResourceLocation> variants = new ObjectArraySet<>();
        private final Set<String> causes = new ObjectArraySet<>();

        private CriteriaBuilder() {}

        public CriteriaBuilder addVariant(ResourceLocation variantId) {
            variants.add(variantId);
            return this;
        }

        public CriteriaBuilder addVariant(TransfurVariant<?> variant) {
            variants.add(variant.getFormId());
            return this;
        }

        /// Not required to be called, but it makes the builder code easier to understand
        public CriteriaBuilder matchAnyVariant() {
            variants.clear();
            return this;
        }

        public CriteriaBuilder addCause(String causeId) {
            causes.add(causeId);
            return this;
        }

        public CriteriaBuilder addCause(TransfurCause cause) {
            causes.add(cause.getSerializedName());
            return this;
        }

        /// Not required to be called, but it makes the builder code easier to understand
        public CriteriaBuilder matchAnyCause() {
            causes.clear();
            return this;
        }

        @Override
        public JsonObject build() {
            JsonObject object = new JsonObject();

            if (variants.size() == 1) {
                object.addProperty(VARIANT_FIELD.toString(), variants.iterator().next().toString());
            } else if (!variants.isEmpty()) {
                JsonArray array = new JsonArray();
                for (ResourceLocation variantId : variants)
                    array.add(variantId.toString());
                object.add(VARIANT_FIELD.toString(), array);
            }

            if (causes.size() == 1) {
                object.addProperty(CAUSE_FIELD.toString(), causes.iterator().next());
            } else if (!causes.isEmpty()) {
                JsonArray array = new JsonArray();
                for (String causeId : causes)
                    array.add(causeId);
                object.add(CAUSE_FIELD.toString(), array);
            }

            return object;
        }
    }
}
