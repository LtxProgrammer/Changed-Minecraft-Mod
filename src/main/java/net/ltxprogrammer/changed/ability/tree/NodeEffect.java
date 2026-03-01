package net.ltxprogrammer.changed.ability.tree;

import com.mojang.serialization.Codec;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.util.Cacheable;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class NodeEffect {
    public static final Codec<NodeEffect> EFFECT_CODEC = ChangedRegistry.ABILITY_NODE_EFFECTS.get().getCodec().dispatch("type",
            NodeEffect::getCodec, Function.identity());

    private final Cacheable<NodeEffect> cachedClientNodeEffect = Cacheable.of(this::createClientNodeEffect);

    /**
     * Used by the node effect to provide itself and/or sub effects that are considered active by their own conditions.
     * The nodes provided to sink are cached to limit the performance hit of checking node conditions multiple times per-tick.
     * @param entity entity the effect is for
     * @param sink forward node effects to listener
     */
    public abstract void gatherActiveEffects(IAbstractChangedEntity entity, Consumer<NodeEffect> sink);

    public abstract Codec<? extends NodeEffect> getCodec();

    @Nullable
    protected NodeEffect createClientNodeEffect() {
        return null;
    }

    /**
     * Provided NodeEffect is guaranteed to be the same instance
     */
    public final Optional<NodeEffect> getClientNodeEffect() {
        return cachedClientNodeEffect.getOptional();
    }
}
