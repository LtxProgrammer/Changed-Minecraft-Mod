package net.ltxprogrammer.changed.ability.tree;

import com.mojang.serialization.Codec;
import net.ltxprogrammer.changed.init.ChangedRegistry;

import java.util.function.Function;

public abstract class NodeEffect {
    public static final Codec<NodeEffect> EFFECT_CODEC = ChangedRegistry.ABILITY_NODE_EFFECTS.get().getCodec().dispatch("type",
            NodeEffect::getCodec, Function.identity());

    public abstract void applyEffect(AbilityCounter counter);

    public abstract Codec<? extends NodeEffect> getCodec();
}
