package net.ltxprogrammer.changed.entity.animation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.minecraft.resources.ResourceLocation;

public class TransfurAnimationParameters implements AnimationParameters {
    public static final Codec<TransfurAnimationParameters> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            ResourceLocation.CODEC.fieldOf("variantId").forGetter(param -> param.variantId)
    ).apply(builder, TransfurAnimationParameters::new));

    public final ResourceLocation variantId;

    public TransfurAnimationParameters(ResourceLocation variantId) {
        this.variantId = variantId;
    }

    public TransfurAnimationParameters(TransfurVariant<?> variant) {
        this.variantId = variant.getFormId();
    }

    private static final ResourceLocation VARIANT_FIELD = Changed.modResource("variant");

    @Override
    public AnimationAssociation.Match apply(AnimationAssociation animationSetup) {
        return animationSetup.fieldEqualsResourceLocation(VARIANT_FIELD, variantId);
    }
}
