package net.ltxprogrammer.changed.ability.tree.events;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class DistanceCrouched extends StatPointEvent {
    public static final Codec<DistanceCrouched> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("reward").forGetter(event -> event.reward),
            Codec.INT.fieldOf("centimeters").forGetter(event -> event.divisor)
    ).apply(instance, DistanceCrouched::new));

    @Override
    public Codec<? extends AbstractPointEvent<?>> getCodec() {
        return CODEC;
    }

    public DistanceCrouched(int reward, int distance) {
        super(reward, distance);
    }
}
