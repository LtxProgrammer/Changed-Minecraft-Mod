package net.ltxprogrammer.changed.ability.tree.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ltxprogrammer.changed.ability.tree.AbilityCounter;
import net.ltxprogrammer.changed.ability.tree.NodeEffect;
import net.ltxprogrammer.changed.ability.tree.condition.AbstractCondition;
import net.ltxprogrammer.changed.ability.tree.condition.TrueCondition;

import java.util.List;

public class GroupNodeEffect extends NodeEffect {
    public static final Codec<GroupNodeEffect> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            AbstractCondition.CONDITION_CODEC.fieldOf("condition").orElse(TrueCondition.INSTANCE).forGetter(node -> node.condition),
            Codec.list(NodeEffect.EFFECT_CODEC).fieldOf("effects").forGetter(node -> node.effects)
    ).apply(builder, GroupNodeEffect::new));

    public final AbstractCondition condition;
    public final List<NodeEffect> effects;

    public GroupNodeEffect(AbstractCondition condition, List<NodeEffect> effects) {
        this.condition = condition;
        this.effects = effects;
    }

    @Override
    public void applyEffect(AbilityCounter counter) {
        if (condition.test(counter.entity))
            effects.forEach(effect -> effect.applyEffect(counter));
    }

    @Override
    public Codec<? extends NodeEffect> getCodec() {
        return CODEC;
    }
}
