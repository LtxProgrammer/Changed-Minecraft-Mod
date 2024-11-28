package net.ltxprogrammer.changed.client.animations;

import net.ltxprogrammer.changed.client.ClientLivingEntityExtender;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.client.tfanimations.TransfurAnimator;
import net.ltxprogrammer.changed.util.Transition;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AnimationInstance {
    private final AnimationDefinition animation;
    private final Map<Limb, PartPose> baseline = new HashMap<>();
    private final Map<LivingEntity, AnimationInstance> entities = new HashMap<>(0);
    private float time = 0.0f;
    private float timeO = 0.0f;

    public AnimationInstance(AnimationDefinition animation) {
        this.animation = animation;
    }

    public void captureBaseline(HumanoidModel<?> model) {
        /* Capture model position baseline */
        animation.channels.keySet().stream().filter(Limb::isVanillaPart).forEach(limb -> {
            baseline.put(limb, limb.getModelPartSafe(model).map(ModelPart::storePose).orElse(null));
        });
    }

    public void resetToBaseline(HumanoidModel<?> model) {
        animation.channels.keySet().stream().filter(Limb::isVanillaPart).filter(baseline::containsKey).forEach(limb -> {
            limb.getModelPartSafe(model).ifPresent(part -> part.loadPose(baseline.get(limb)));
        });
    }

    public void captureBaseline(AdvancedHumanoidModel<?> model) {
        /* Capture model position baseline */
        animation.channels.keySet().forEach(limb -> {
            baseline.put(limb, limb.getModelPartSafe(model).map(ModelPart::storePose).orElse(null));
        });
    }

    /**
     * Adds reference entity to use in animation. Does nothing if entity is already referenced, or if definition doesn't have a slot.
     * Adds new animation to the entity's prop animation category, if the animation is defined.
     * @param livingEntity Entity to animate
     */
    public void addEntity(LivingEntity livingEntity) {
        if (entities.containsKey(livingEntity))
            return;

        if (animation.entityProps.size() <= entities.size())
            return;

        final ResourceLocation id = animation.entityProps.get(entities.size());
        final AnimationInstance instance = new AnimationInstance(AnimationDefinitions.getAnimation(id));
        entities.put(livingEntity, instance);
        ((ClientLivingEntityExtender)livingEntity).addAnimation(AnimationCategory.PROP, instance);
    }

    public void addItem(ItemStack item) {
        // TODO: Item animation track, either lock to limb or be keyframed
    }

    private void animateLimb(Limb limb, ModelPart part, float time, float transition) {
        if (part == null)
            return;

        final var base = baseline.get(limb);
        final var channelList = animation.channels.get(limb);
        if (channelList == null)
            return;

        channelList.forEach(channel -> {
            if (channel.isDone(time))
                return;

            channel.animate(animation, part, time);
            if (base != null && channel.getTarget() == AnimationChannel.Target.POSITION) {
                part.x += base.x;
                part.y += base.y;
                part.z += base.z;
            }
        });

        part.loadPose(TransfurAnimator.lerpPartPose(part.storePose(), base, transition));
    }

    public void animate(HumanoidModel<?> model, float partialTicks) {
        captureBaseline(model);

        final float time = Mth.lerp(partialTicks, this.timeO, this.time);

        final float in = Mth.clamp(Mth.map(time, 0.0f, animation.transitionDuration, 1.0f, 0.0f), 0.0f, 1.0f);
        final float out = Mth.clamp(Mth.map(time, animation.length - animation.transitionDuration, animation.length, 0.0f, 1.0f), 0.0f, 1.0f);
        final float transition = Transition.easeInOutSine(Mth.clamp(in + out, 0.0f, 1.0f));

        animation.channels.keySet().stream().filter(Limb::isVanillaPart).forEach(limb -> animateLimb(limb, limb.getModelPart(model), time, transition));
    }

    public void animate(AdvancedHumanoidModel<?> model, float partialTicks) {
        captureBaseline(model);

        final float time = Mth.lerp(partialTicks, this.timeO, this.time);

        final float in = Mth.clamp(Mth.map(time, 0.0f, animation.transitionDuration, 1.0f, 0.0f), 0.0f, 1.0f);
        final float out = Mth.clamp(Mth.map(time, animation.length - animation.transitionDuration, animation.length, 0.0f, 1.0f), 0.0f, 1.0f);
        final float transition = Transition.easeInOutSine(Mth.clamp(in + out, 0.0f, 1.0f));

        animation.channels.keySet().forEach(limb -> animateLimb(limb, limb.getModelPart(model), time, transition));
    }

    public void setTime(float time) {
        this.time = time;
        this.timeO = time;
    }

    public void tickTime() {
        this.timeO = this.time;
        this.time += 1.0f / 20.0f;
    }

    public boolean isDone() {
        return animation.channels.values().stream().flatMap(List::stream).allMatch(channel -> channel.isDone(time)) &&
                entities.keySet().stream()
                        .map(entity -> ((ClientLivingEntityExtender)entity).getAnimationSafe(AnimationCategory.PROP))
                        .filter(Optional::isPresent).map(Optional::get).allMatch(AnimationInstance::isDone);
    }

    public void clear() {
        entities.forEach((entity, instance) -> ((ClientLivingEntityExtender)entity).clearAnimation(AnimationCategory.PROP));
    }
}
