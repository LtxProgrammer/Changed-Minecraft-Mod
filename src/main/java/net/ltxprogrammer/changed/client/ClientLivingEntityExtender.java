package net.ltxprogrammer.changed.client;

import net.ltxprogrammer.changed.entity.animation.AnimationCategory;
import net.ltxprogrammer.changed.client.animations.AnimationDefinition;
import net.ltxprogrammer.changed.client.animations.AnimationInstance;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface ClientLivingEntityExtender {
    Stream<AnimationInstance> getOrderedAnimations();

    void addAnimation(AnimationCategory category, AnimationInstance animationInstance);

    @Nullable
    AnimationInstance getAnimation(AnimationCategory category);

    @Nullable
    AnimationInstance getAnimation(AnimationCategory category, Supplier<AnimationDefinition> definition);

    default Optional<AnimationInstance> getAnimationSafe(AnimationCategory category) {
        return Optional.ofNullable(getAnimation(category));
    }

    void clearAnimation(AnimationCategory category);

    void clearAnimation(AnimationCategory category, Supplier<AnimationDefinition> definition);
}
