package net.ltxprogrammer.changed.entity.animation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.minecraft.resources.ResourceLocation;

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
}
