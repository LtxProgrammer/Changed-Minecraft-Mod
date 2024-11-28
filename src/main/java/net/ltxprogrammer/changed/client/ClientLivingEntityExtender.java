package net.ltxprogrammer.changed.client;

import net.ltxprogrammer.changed.client.animations.AnimationCategory;
import net.ltxprogrammer.changed.client.animations.AnimationInstance;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.stream.Stream;

public interface ClientLivingEntityExtender {
    Stream<AnimationInstance> getOrderedAnimations();

    void addAnimation(AnimationCategory category, AnimationInstance animationInstance);

    @Nullable
    AnimationInstance getAnimation(AnimationCategory category);

    default Optional<AnimationInstance> getAnimationSafe(AnimationCategory category) {
        return Optional.ofNullable(getAnimation(category));
    }

    void clearAnimation(AnimationCategory category);
}
