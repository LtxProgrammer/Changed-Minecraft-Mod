package net.ltxprogrammer.changed.client.tfanimations;

import net.ltxprogrammer.changed.entity.TransfurCause;
import net.minecraft.Util;

import java.util.HashMap;
import java.util.Map;

public class TransfurAnimations {
    // TODO this animation is just a stand-in to get the animator code set up
    public static final AnimationDefinition BASIC_ANIMATION = AnimationDefinition.Builder.withLength(6.0F)
            .addAnimation(Limb.HEAD, new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.6667F, KeyframeAnimations.degreeVec(40.0F, -20.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.375F, KeyframeAnimations.degreeVec(32.5F, -20.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.0F, KeyframeAnimations.degreeVec(2.023F, -11.3578F, 0.364F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.5F, KeyframeAnimations.degreeVec(9.4839F, -1.3639F, 0.7153F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.9167F, KeyframeAnimations.degreeVec(9.48F, -1.36F, 0.72F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.2083F, KeyframeAnimations.degreeVec(14.48F, -1.36F, 0.72F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.3333F, KeyframeAnimations.degreeVec(4.48F, -1.36F, 0.72F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.0F, KeyframeAnimations.degreeVec(4.48F, -1.36F, 0.72F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.7083F, KeyframeAnimations.degreeVec(27.3469F, 10.223F, 5.4417F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(5.5417F, KeyframeAnimations.degreeVec(27.3469F, 10.223F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(6.0F, KeyframeAnimations.degreeVec(9.48F, -1.36F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation(Limb.RIGHT_ARM, new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.7917F, KeyframeAnimations.degreeVec(-63.8712F, -46.9358F, -19.716F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.2083F, KeyframeAnimations.degreeVec(-63.87F, -46.94F, -19.72F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.4167F, KeyframeAnimations.degreeVec(-70.4316F, -26.1322F, -8.8999F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.0F, KeyframeAnimations.degreeVec(-81.9225F, -14.2948F, -4.6096F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.5F, KeyframeAnimations.degreeVec(-131.2159F, -27.9331F, 7.1886F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.9167F, KeyframeAnimations.degreeVec(-131.22F, -27.93F, 7.19F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.2083F, KeyframeAnimations.degreeVec(-124.6357F, -24.1225F, 3.5822F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.3333F, KeyframeAnimations.degreeVec(-115.5299F, -17.8891F, -0.8888F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.0F, KeyframeAnimations.degreeVec(-73.0299F, -17.8891F, -0.8888F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.7083F, KeyframeAnimations.degreeVec(-73.03F, -17.89F, -0.89F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(5.5417F, KeyframeAnimations.degreeVec(-73.03F, -17.89F, -0.89F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(6.0F, KeyframeAnimations.degreeVec(-40.53F, -17.89F, -0.89F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation(Limb.LEFT_ARM, new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.7917F, KeyframeAnimations.degreeVec(-59.1325F, 12.9525F, 7.6307F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.2083F, KeyframeAnimations.degreeVec(-59.13F, 12.95F, 7.63F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.375F, KeyframeAnimations.degreeVec(-58.4308F, 17.2267F, 10.3135F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.5F, KeyframeAnimations.degreeVec(-59.3987F, 10.8011F, 6.3244F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.8333F, KeyframeAnimations.degreeVec(-50.2522F, 1.234F, -1.7749F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.1667F, KeyframeAnimations.degreeVec(-92.7522F, 1.234F, -1.7749F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.5F, KeyframeAnimations.degreeVec(-132.8374F, 18.1735F, -16.8601F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.9167F, KeyframeAnimations.degreeVec(-132.84F, 18.17F, -16.86F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.2083F, KeyframeAnimations.degreeVec(-126.8531F, 14.4734F, -13.3511F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.3333F, KeyframeAnimations.degreeVec(-116.1535F, 9.9485F, -5.4374F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.0F, KeyframeAnimations.degreeVec(-71.1535F, 9.9485F, -5.4374F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.7083F, KeyframeAnimations.degreeVec(-71.15F, 9.95F, -5.44F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(5.5417F, KeyframeAnimations.degreeVec(-71.15F, 9.95F, -5.44F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(6.0F, KeyframeAnimations.degreeVec(-23.65F, 9.95F, -5.44F), AnimationChannel.Interpolations.CATMULLROM)
            )).build();

    public static final Map<TransfurCause, AnimationDefinition> CAUSE_ASSOCIATION = Util.make(new HashMap<>(), map -> {
        map.put(TransfurCause.GRAB_REPLICATE, BASIC_ANIMATION);
    });

    public static AnimationDefinition getAnimationFromCause(TransfurCause cause) {
        if (!CAUSE_ASSOCIATION.containsKey(cause) && cause.getParent().isPresent())
            return getAnimationFromCause(cause.getParent().get());
        else
            return CAUSE_ASSOCIATION.getOrDefault(cause, BASIC_ANIMATION);
    }
}
