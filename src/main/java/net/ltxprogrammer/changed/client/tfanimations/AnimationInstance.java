package net.ltxprogrammer.changed.client.tfanimations;

import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.util.Transition;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.util.Mth;

import java.util.HashMap;
import java.util.Map;

public class AnimationInstance {
    private final AnimationDefinition animation;
    private final Map<Limb, PartPose> baseline = new HashMap<>();

    public AnimationInstance(AnimationDefinition animation) {
        this.animation = animation;
    }

    public void captureBaseline(HumanoidModel<?> model) {
        /* Capture model position baseline */
        animation.channels.keySet().stream().filter(Limb::isVanillaPart).forEach(limb -> {
            baseline.put(limb, limb.getModelPartSafe(model).map(ModelPart::storePose).orElse(null));
        });
    }

    public void captureBaseline(AdvancedHumanoidModel<?> model) {
        /* Capture model position baseline */
        animation.channels.keySet().forEach(limb -> {
            baseline.put(limb, limb.getModelPartSafe(model).map(ModelPart::storePose).orElse(null));
        });
    }

    private void animateLimb(Limb limb, ModelPart part, float time, float transition) {
        if (part == null)
            return;

        final var base = baseline.get(limb);
        final var channelList = animation.channels.get(limb);
        if (channelList == null)
            return;

        channelList.forEach(channel -> {
            channel.animate(animation, part, time);
            if (base != null && channel.getTarget() == AnimationChannel.Targets.POSITION) {
                part.x += base.x;
                part.y += base.y;
                part.z += base.z;
            }
        });

        part.loadPose(TransfurAnimator.lerpPartPose(part.storePose(), base, transition));
    }

    public void animate(HumanoidModel<?> model, float time) {
        captureBaseline(model);

        final float in = Mth.clamp(Mth.map(time, 0.0f, animation.transitionDuration, 1.0f, 0.0f), 0.0f, 1.0f);
        final float out = Mth.clamp(Mth.map(time, animation.length - animation.transitionDuration, animation.length, 0.0f, 1.0f), 0.0f, 1.0f);
        final float transition = Transition.easeInOutSine(Mth.clamp(in + out, 0.0f, 1.0f));

        animation.channels.keySet().stream().filter(Limb::isVanillaPart).forEach(limb -> animateLimb(limb, limb.getModelPart(model), time, transition));
    }

    public void animate(AdvancedHumanoidModel<?> model, float time) {
        captureBaseline(model);

        final float in = Mth.clamp(Mth.map(time, 0.0f, animation.transitionDuration, 1.0f, 0.0f), 0.0f, 1.0f);
        final float out = Mth.clamp(Mth.map(time, animation.length - animation.transitionDuration, animation.length, 0.0f, 1.0f), 0.0f, 1.0f);
        final float transition = Transition.easeInOutSine(Mth.clamp(in + out, 0.0f, 1.0f));

        animation.channels.keySet().forEach(limb -> animateLimb(limb, limb.getModelPart(model), time, transition));
    }
}
