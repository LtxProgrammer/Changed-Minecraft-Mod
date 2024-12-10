package net.ltxprogrammer.changed.client.animations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

import java.util.Optional;

public class TimedSoundEffect {
    private final float time;
    private final SoundEvent soundEvent;
    private final float pitchLow;
    private final float pitchHigh;
    private final float volume;

    public static final Codec<TimedSoundEffect> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.FLOAT.fieldOf("time").forGetter(effect -> effect.time),
            SoundEvent.CODEC.fieldOf("sound").forGetter(effect -> effect.soundEvent),
            Codec.FLOAT.optionalFieldOf("pitchLow").orElse(Optional.of(1.0f)).forGetter(effect -> Optional.of(effect.pitchLow)),
            Codec.FLOAT.optionalFieldOf("pitchHigh").orElse(Optional.of(1.0f)).forGetter(effect -> Optional.of(effect.pitchHigh)),
            Codec.FLOAT.optionalFieldOf("pitch").forGetter(effect -> Optional.empty()),
            Codec.FLOAT.fieldOf("volume").orElse(1.0f).forGetter(effect -> effect.volume)
    ).apply(builder, (time, sound, pitchLow, pitchHigh, pitch, volume) -> new TimedSoundEffect(time, sound,
            pitch.or(() -> pitchLow).orElseThrow(),
            pitch.or(() -> pitchHigh).orElseThrow(), volume)));

    public TimedSoundEffect(float time, SoundEvent soundEvent, float pitchLow, float pitchHigh, float volume) {
        this.time = time;
        this.soundEvent = soundEvent;
        this.pitchLow = Math.min(pitchLow, pitchHigh);
        this.pitchHigh = Math.max(pitchLow, pitchHigh);
        this.volume = volume;
    }

    public void play(LivingEntity entity) {
        entity.playSound(this.soundEvent, this.volume, Mth.lerp(entity.getRandom().nextFloat(), this.pitchLow, this.pitchHigh));
    }

    public void playIfInRange(LivingEntity entity, float timeO, float time) {
        if (this.time >= timeO && this.time < time)
            play(entity);
    }
}
