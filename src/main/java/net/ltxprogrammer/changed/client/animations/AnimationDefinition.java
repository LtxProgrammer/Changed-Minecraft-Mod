package net.ltxprogrammer.changed.client.animations;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnimationDefinition {
    public final float length;
    public final float transitionDuration;
    public final boolean looped;
    public final ImmutableMap<Limb, List<AnimationChannel>> channels;
    public final ImmutableList<ResourceLocation> entityProps;
    public final ImmutableList<AnimationChannel> itemProps;

    public static final Codec<AnimationDefinition> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.FLOAT.fieldOf("length").forGetter(definition -> definition.length),
            Codec.FLOAT.fieldOf("transitionDuration").forGetter(definition -> definition.transitionDuration),
            Codec.BOOL.fieldOf("looped").orElse(false).forGetter(definition -> definition.looped),
            Codec.unboundedMap(
                    Limb.CODEC,
                    Codec.list(AnimationChannel.CODEC)
            ).fieldOf("channels").forGetter(definition -> definition.channels),
            Codec.list(ResourceLocation.CODEC).fieldOf("entities").orElse(List.of()).forGetter(definition -> definition.entityProps),
            Codec.list(AnimationChannel.CODEC).fieldOf("items").orElse(List.of()).forGetter(definition -> definition.itemProps)
    ).apply(builder, AnimationDefinition::new));

    public AnimationDefinition(float length, float transitionDuration, boolean looped,
                               Map<Limb, List<AnimationChannel>> channels,
                               List<ResourceLocation> entities,
                               List<AnimationChannel> items) {
        this.length = length;
        this.transitionDuration = transitionDuration;
        this.looped = looped;
        this.channels = ImmutableMap.copyOf(channels);
        this.entityProps = ImmutableList.copyOf(entities);
        this.itemProps = ImmutableList.copyOf(items);
    }

    public float getLength() {
        return length;
    }

    public static class Builder {
        private final float length;
        private final Map<Limb, List<AnimationChannel>> animations = new HashMap<>();
        private final List<ResourceLocation> entities = new ArrayList<>();
        private final List<AnimationChannel> items = new ArrayList<>();

        private float transitionDuration = 1.25f;
        private boolean looped = false;

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

        public Builder looped(boolean value) {
            this.looped = value;
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

        public AnimationDefinition build() {
            return new AnimationDefinition(length, transitionDuration, looped, animations, entities, items);
        }
    }
}
