package net.ltxprogrammer.changed.ability.tree.events;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class OnWingFlap extends AbstractPointEvent<NullCriteria> {
    public static final Codec<OnWingFlap> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("reward").forGetter(event -> event.reward)
    ).apply(instance, OnWingFlap::new));

    @Override
    public Codec<? extends AbstractPointEvent<?>> getCodec() {
        return CODEC;
    }


    public OnWingFlap(int reward) {
        super(reward);
    }

    @Override
    public boolean test(NullCriteria criteria) {
        return true;
    }
}
