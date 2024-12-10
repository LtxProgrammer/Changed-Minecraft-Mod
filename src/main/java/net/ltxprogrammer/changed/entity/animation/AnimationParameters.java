package net.ltxprogrammer.changed.entity.animation;

import net.minecraft.world.entity.LivingEntity;

public interface AnimationParameters {
    AnimationAssociation.Match matchesAssociation(AnimationAssociation association);

    default boolean shouldEndAnimation(LivingEntity livingEntity, float totalTime) {
        return false;
    }

    default boolean shouldLoop(LivingEntity livingEntity, float totalTime) {
        return false;
    }
}
