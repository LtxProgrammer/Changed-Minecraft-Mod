package net.ltxprogrammer.changed.ability.tree.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.ability.tree.NodeEffect;
import net.ltxprogrammer.changed.ability.tree.condition.AbstractCondition;
import net.ltxprogrammer.changed.ability.tree.condition.TrueCondition;
import net.ltxprogrammer.changed.init.ChangedRegistry;

import java.util.function.Consumer;

public class UnlockActiveAbilityNodeEffect extends NodeEffect {
    public final AbstractCondition condition;
    public final AbstractAbility<?> ability;
    public final int amplifier;

    public static final Codec<UnlockActiveAbilityNodeEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            AbstractCondition.CONDITION_CODEC.fieldOf("condition").orElse(TrueCondition.INSTANCE).forGetter(effect -> effect.condition),
            ChangedRegistry.ABILITY.get().getCodec().fieldOf("ability").forGetter(effect -> effect.ability),
            Codec.INT.fieldOf("amplifier").orElse(0).forGetter(effect -> effect.amplifier)
    ).apply(instance, UnlockActiveAbilityNodeEffect::new));

    public UnlockActiveAbilityNodeEffect(AbstractCondition condition, AbstractAbility<?> ability, int amplifier) {
        this.condition = condition;
        this.ability = ability;
        this.amplifier = amplifier;
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
}
