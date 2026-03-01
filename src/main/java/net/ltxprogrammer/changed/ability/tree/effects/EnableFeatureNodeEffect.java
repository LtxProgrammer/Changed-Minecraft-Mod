package net.ltxprogrammer.changed.ability.tree.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.ability.tree.NodeEffect;
import net.ltxprogrammer.changed.ability.tree.condition.AbstractCondition;
import net.ltxprogrammer.changed.ability.tree.condition.TrueCondition;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantFeature;
import net.ltxprogrammer.changed.init.ChangedRegistry;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Consumer;

public class EnableFeatureNodeEffect extends NodeEffect {
    public static final Codec<EnableFeatureNodeEffect> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            AbstractCondition.CONDITION_CODEC.fieldOf("condition").orElse(TrueCondition.INSTANCE).forGetter(node -> node.condition),
            ChangedRegistry.TRANSFUR_VARIANT_FEATURES.get().getCodec().fieldOf("feature").forGetter(node -> node.feature),
            Codec.DOUBLE.fieldOf("factor").orElse(1.0).forGetter(node -> node.factor)
    ).apply(builder, EnableFeatureNodeEffect::new));

    public final AbstractCondition condition;
    public final TransfurVariantFeature feature;
    public final double factor;

    public EnableFeatureNodeEffect(AbstractCondition condition, TransfurVariantFeature feature, double factor) {
        this.condition = condition;
        this.feature = feature;
        this.factor = factor;
    }

    @Override
    public void gatherActiveEffects(IAbstractChangedEntity entity, Consumer<NodeEffect> sink) {
        if (condition.test(entity))
            sink.accept(this);
    }

    @Override
    public Codec<? extends NodeEffect> getCodec() {
        return CODEC;
    }

    @Nullable
    @Override
    protected NodeEffect createClientNodeEffect() {
        return new EnableFeatureNodeEffect(TrueCondition.INSTANCE, this.feature, this.factor);
    }
}
