package net.ltxprogrammer.changed.ability.tree.requirements;

import com.mojang.serialization.Codec;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.init.ChangedRegistry;

import java.util.function.Function;
import java.util.function.Predicate;

public abstract class AbstractRequirement implements Predicate<IAbstractChangedEntity> {
    public static final Codec<AbstractRequirement> CONDITION_CODEC = ChangedRegistry.PURCHASE_REQUIREMENTS.get().getCodec().dispatch("type",
            AbstractRequirement::getCodec, Function.identity());

    public abstract Codec<? extends AbstractRequirement> getCodec();
}
