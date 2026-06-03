package net.ltxprogrammer.changed.ability.tree.events;

import com.mojang.serialization.Codec;

import java.util.function.Predicate;

public abstract class AbstractPointEvent<T> implements Predicate<T> {
    protected final int reward;

    public AbstractPointEvent(int reward) {
        this.reward = reward;
    }

    public abstract Codec<? extends AbstractPointEvent<?>> getCodec();

    public int getPoints() {
        return reward;
    }
}
