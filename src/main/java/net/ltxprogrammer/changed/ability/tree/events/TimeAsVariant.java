package net.ltxprogrammer.changed.ability.tree.events;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class TimeAsVariant extends StatPointEvent {
    public static final Codec<TimeAsVariant> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("reward").forGetter(event -> event.reward),
            Codec.INT.fieldOf("ticks").forGetter(event -> event.divisor)
    ).apply(instance, TimeAsVariant::new));

    @Override
    public Codec<? extends AbstractPointEvent<?>> getCodec() {
        return CODEC;
    }

    public TimeAsVariant(int reward, int divisor) {
        super(reward, divisor);
    }
}
