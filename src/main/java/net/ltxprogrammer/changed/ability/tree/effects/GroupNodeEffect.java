package net.ltxprogrammer.changed.ability.tree.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.ability.tree.NodeEffect;
import net.ltxprogrammer.changed.ability.tree.condition.AbstractCondition;
import net.ltxprogrammer.changed.ability.tree.condition.TrueCondition;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Consumer;

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
    public void gatherActiveEffects(IAbstractChangedEntity entity, Consumer<NodeEffect> sink) {
        if (condition.test(entity)) {
            effects.forEach(nodeEffect -> {
                nodeEffect.gatherActiveEffects(entity, sink);
            });
        }
    }

    @Override
    public Codec<? extends NodeEffect> getCodec() {
        return CODEC;
    }

    @Override
    public void buildDescription(Consumer<Component> componentConsumer, boolean negate) {
        super.buildDescription(componentConsumer, negate);

        effects.forEach(effect -> effect.buildDescription(componentConsumer, negate));
    }
}
