package net.ltxprogrammer.changed.client.tfanimations;

import net.ltxprogrammer.changed.entity.TransfurCause;
import net.minecraft.Util;

import java.util.HashMap;
import java.util.Map;

public class TransfurAnimations {
    public static final AnimationDefinition BASIC_ANIMATION = AnimationDefinition.Builder.withLength(3.0F)
            .addAnimation(Limb.RIGHT_LEG, new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(-45.1462F, 24.3051F, 22.2691F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5833F, KeyframeAnimations.degreeVec(-47.7532F, -17.0468F, -14.9092F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.3333F, KeyframeAnimations.degreeVec(35.4095F, -37.2209F, 10.3201F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.25F, KeyframeAnimations.degreeVec(57.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            )).build();

    public static final Map<TransfurCause, AnimationDefinition> CAUSE_ASSOCIATION = Util.make(new HashMap<>(), map -> {
        map.put(TransfurCause.GRAB_REPLICATE, BASIC_ANIMATION);
    });
}
