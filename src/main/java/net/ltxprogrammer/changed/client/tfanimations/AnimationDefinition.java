package net.ltxprogrammer.changed.client.tfanimations;

import com.google.common.collect.ImmutableMap;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.minecraft.client.model.HumanoidModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnimationDefinition {
    public final float length;
    public final float transitionDuration;
    public final ImmutableMap<Limb, List<AnimationChannel>> channels;

    public AnimationDefinition(float length, float transitionDuration, Map<Limb, List<AnimationChannel>> channels) {
        this.length = length;
        this.transitionDuration = transitionDuration;
        this.channels = ImmutableMap.copyOf(channels);
    }

    public AnimationInstance createInstance(HumanoidModel<?> model) {
        final var instance = new AnimationInstance(this);
        instance.captureBaseline(model);
        return instance;
    }

    public AnimationInstance createInstance(AdvancedHumanoidModel<?> model) {
        final var instance = new AnimationInstance(this);
        instance.captureBaseline(model);
        return instance;
    }

    public float getLength() {
        return length;
    }

    public static class Builder {
        private final float length;
        private final Map<Limb, List<AnimationChannel>> animations = new HashMap<>();

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

        public AnimationDefinition build() {
            return new AnimationDefinition(length, transitionDuration, animations);
        }
    }
}
