package net.ltxprogrammer.changed.ability.tree;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ltxprogrammer.changed.ability.tree.condition.AbstractCondition;
import net.ltxprogrammer.changed.ability.tree.condition.TrueCondition;

import java.util.List;

public class ToggleGroupNodeEffect extends NodeEffect {
    public static final Codec<ToggleGroupNodeEffect> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            AbstractCondition.CONDITION_CODEC.fieldOf("condition").orElse(TrueCondition.INSTANCE).forGetter(node -> node.condition),
            Codec.list(NodeEffect.EFFECT_CODEC).fieldOf("effects").forGetter(node -> node.effects),
            Codec.INT.fieldOf("chargeDuration").orElse(0).forGetter(node -> node.chargeDuration),
            Codec.INT.fieldOf("activeDuration").orElse(Integer.MAX_VALUE).forGetter(node -> node.activeDuration),
            Codec.INT.fieldOf("cooldownDuration").orElse(0).forGetter(node -> node.cooldownDuration),
            Codec.BOOL.fieldOf("canCancel").orElse(true).forGetter(node -> node.canCancel)
    ).apply(builder, ToggleGroupNodeEffect::new));

    public final AbstractCondition condition;
    public final List<NodeEffect> effects;
    public final int chargeDuration;
    public final int activeDuration;
    public final int cooldownDuration;
    public final boolean canCancel;

    public ToggleGroupNodeEffect(AbstractCondition condition, List<NodeEffect> effects,
                                 int chargeDuration, int activeDuration, int cooldownDuration,
                                 boolean canCancel) {
        this.condition = condition;
        this.effects = effects;
        this.chargeDuration = chargeDuration;
        this.activeDuration = activeDuration;
        this.cooldownDuration = cooldownDuration;
        this.canCancel = canCancel;
    }

    @Override
    public void applyEffect(AbilityCounter counter) {
        // TODO: give entity an ability to toggle sub effects
    }

    @Override
    public Codec<? extends NodeEffect> getCodec() {
        return CODEC;
    }
}
