package net.ltxprogrammer.changed.entity.animation;

import net.minecraft.world.entity.LivingEntity;

public interface AnimationParameters {
    /**
     * Allows the parameters to predicate the association, so that defined animations will play when appropriate for given conditions.
     * @param association the defined animation to predicate.
     * @return Either ALLOW or DEFAULT to allow the animation to play.
     * ALLOW results will override DEFAULT results. DENY will prevent the animation from playing.
     * The chosen animation will be selected at random from all the ALLOW results, or all the DEFAULT results if
     * no ALLOW results were specified.
     */
    AnimationAssociation.Match matchesAssociation(AnimationAssociation association);

    /**
     * Allows the parameters to force the animation to end.
     * @param livingEntity entity being animated.
     * @param totalTime total time in animation.
     * @return true when the animation should end.
     */
    default boolean shouldEndAnimation(LivingEntity livingEntity, float totalTime) {
        return false;
    }

    /**
     * Allows the parameters to loop the animation.
     * @param livingEntity entity being animated.
     * @param totalTime total time in animation.
     * @return true if the animation should be looping.
     */
    default boolean shouldLoop(LivingEntity livingEntity, float totalTime) {
        return false;
    }

    /**
     * Allows the parameters to control the playback speed of the animation.
     * This method is called each tick.
     * @param livingEntity entity being animated.
     * @param totalTime total time in animation.
     * @return A multiplier to the delta time of the animation. Default: 100%
     */
    default float getPlaybackSpeed(LivingEntity livingEntity, float totalTime) {
        return 1.0f;
    }
}
