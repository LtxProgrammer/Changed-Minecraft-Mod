package net.ltxprogrammer.changed.client.animations;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ltxprogrammer.changed.entity.animation.AnimationParameters;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnimationDefinition {
    public final float length;
    public final float transitionDuration;
    public final ImmutableMap<Limb, List<AnimationChannel>> channels;
    public final ImmutableList<ResourceLocation> entityProps;
    public final ImmutableList<AnimationChannel> itemProps;
    public final ImmutableList<TimedSoundEffect> soundEffects;

    public static final Codec<AnimationDefinition> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.FLOAT.fieldOf("length").forGetter(definition -> definition.length),
            Codec.FLOAT.fieldOf("transitionDuration").forGetter(definition -> definition.transitionDuration),
            Codec.unboundedMap(
                    Limb.CODEC,
                    Codec.list(AnimationChannel.CODEC)
            ).fieldOf("channels").forGetter(definition -> definition.channels),
            Codec.list(ResourceLocation.CODEC).fieldOf("entities").orElse(List.of()).forGetter(definition -> definition.entityProps),
            Codec.list(AnimationChannel.CODEC).fieldOf("items").orElse(List.of()).forGetter(definition -> definition.itemProps),
            Codec.list(TimedSoundEffect.CODEC).fieldOf("soundEffects").orElse(List.of()).forGetter(definition -> definition.soundEffects)
    ).apply(builder, AnimationDefinition::new));

    public AnimationDefinition(float length, float transitionDuration,
                               Map<Limb, List<AnimationChannel>> channels,
                               List<ResourceLocation> entities,
                               List<AnimationChannel> items,
                               List<TimedSoundEffect> soundEffects) {
        this.length = length;
        this.transitionDuration = transitionDuration;
        this.channels = ImmutableMap.copyOf(channels);
        this.entityProps = ImmutableList.copyOf(entities);
        this.itemProps = ImmutableList.copyOf(items);
        this.soundEffects = ImmutableList.copyOf(soundEffects);
    }

    public float getLength() {
        return length;
    }

    public AnimationInstance createInstance(LivingEntity entity, AnimationParameters parameters) {
        return new AnimationInstance(this, entity, parameters);
    }

    public static class Builder {
        private final float length;
        private final Map<Limb, List<AnimationChannel>> animations = new HashMap<>();
        private final List<ResourceLocation> entities = new ArrayList<>();
        private final List<AnimationChannel> items = new ArrayList<>();
        private final List<TimedSoundEffect> soundEffects = new ArrayList<>();

        private float transitionDuration = 1.25f;

        private Builder(float length) {
            this.length = length;
        }

        public static Builder withLength(float length) {
            return new Builder(length);
        }

        public Builder withTransition(float duration) {
            this.transitionDuration = duration;
            return this;
        }

        public Builder addAnimation(Limb limb, AnimationChannel channel) {
            animations.computeIfAbsent(limb, (l) -> new ArrayList<>()).add(channel);
            return this;
        }

        public Builder addEntity(ResourceLocation entityAnimation) {
            entities.add(entityAnimation);
            return this;
        }

        public Builder addItem(AnimationChannel channel) {
            items.add(channel);
            return this;
        }

        public Builder addSoundEffect(TimedSoundEffect soundEffect) {
            soundEffects.add(soundEffect);
            return this;
        }

        public AnimationDefinition build() {
            return new AnimationDefinition(length, transitionDuration, animations, entities, items, soundEffects);
        }
    }
}
