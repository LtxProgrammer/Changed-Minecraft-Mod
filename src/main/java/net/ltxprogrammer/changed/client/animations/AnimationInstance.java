package net.ltxprogrammer.changed.client.animations;

import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.client.tfanimations.TransfurAnimator;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.animation.AnimationCategory;
import net.ltxprogrammer.changed.entity.animation.AnimationParameters;
import net.ltxprogrammer.changed.entity.animation.NoParameters;
import net.ltxprogrammer.changed.util.Transition;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;
import java.util.concurrent.atomic.AtomicReference;

public class AnimationInstance {
    protected final AnimationDefinition animation;
    private final Map<LivingEntity, AnimationInstance> entities = new HashMap<>(0);
    private float time = 0.0f;
    private float timeO = 0.0f;
    @NotNull
    private final LivingEntity hostEntity;
    @NotNull
    private final AnimationParameters parameters;
    @Nullable
    private final LivingEntity parentEntity;

    public AnimationInstance(AnimationDefinition animation, @NotNull LivingEntity hostEntity, @Nullable AnimationParameters parameters) {
        this.animation = animation;
        this.hostEntity = hostEntity;
        this.parameters = parameters == null ? NoParameters.INSTANCE : parameters;
        this.parentEntity = null;
    }

    private AnimationInstance(AnimationDefinition animation, @NotNull LivingEntity hostEntity, @Nullable AnimationParameters parameters, @Nullable LivingEntity parentEntity) {
        this.animation = animation;
        this.hostEntity = hostEntity;
        this.parameters = parameters == null ? NoParameters.INSTANCE : parameters;
        this.parentEntity = parentEntity;
    }

    /**
     * Adds reference entity to use in animation. Does nothing if entity is already referenced, or if definition doesn't have a slot.
     * Adds new animation to the entity's prop animation category, if the animation is defined.
     * @param livingEntity Entity to animate
     */
    public void addEntity(@Nullable LivingEntity livingEntity) {
        if (livingEntity == null)
            return;

        if (entities.containsKey(livingEntity))
            return;

        if (animation.entityProps.size() <= entities.size())
            return;

        final ResourceLocation id = animation.entityProps.get(entities.size());
        final AnimationInstance instance = new AnimationInstance(AnimationDefinitions.getAnimation(id), livingEntity, null, hostEntity);
        entities.put(livingEntity, instance);
        AnimationContainer.getForEntityOrCreate(livingEntity).addAnimation(AnimationCategory.PROP, instance);
    }

    public void addItem(ItemStack item) {
        // TODO: Item animation track, either lock to limb or be keyframed
    }

    private void animateLimb(List<AnimationChannel> channelList, ModelPart part, PartPose base, float time, float transition) {
        if (part == null)
            return;

        channelList.forEach(channel -> {
            if (channel.isDone(time))
                return;

            channel.animate(animation, part, time);
            if (channel.getTarget() == AnimationChannel.Target.POSITION) {
                part.x += base.x;
                part.y += base.y;
                part.z += base.z;
            }
        });

        part.loadPose(TransfurAnimator.lerpPartPose(part.storePose(), base, transition));
    }

    private PartPose animateLimb(List<AnimationChannel> channelList, PartPose part, PartPose base, float time, float transition) {
        AtomicReference<PartPose> partPoseRef = new AtomicReference<>(part);

        channelList.forEach(channel -> {
            if (channel.isDone(time))
                return;

            PartPose preStore = channel.animate(animation, partPoseRef.get(), time);
            if (channel.getTarget() == AnimationChannel.Target.POSITION) {
                partPoseRef.set(PartPose.offsetAndRotation(
                        preStore.x + base.x,
                        preStore.y + base.y,
                        preStore.z + base.z,
                        preStore.xRot,
                        preStore.yRot,
                        preStore.zRot));
            }

            else {
                partPoseRef.set(preStore);
            }
        });

        return TransfurAnimator.lerpPartPose(partPoseRef.getAcquire(), base, transition);
    }

    public float computeTransition(float partialTicks) {
        float time = Mth.lerp(partialTicks, this.timeO, this.time);

        final float in = Mth.clamp(Mth.map(time, 0.0f, animation.transitionDuration, 1.0f, 0.0f), 0.0f, 1.0f);
        final float out = Mth.clamp(Mth.map(time, animation.length - animation.transitionDuration, animation.length, 0.0f, 1.0f), 0.0f, 1.0f);

        if (parameters.shouldLoop(hostEntity, this.time))
            return Transition.easeInOutSine(Mth.clamp(in, 0.0f, 1.0f));
        else
            return Transition.easeInOutSine(Mth.clamp(in + out, 0.0f, 1.0f));
    }

    public float computeTime(float partialTicks) {
        if (parameters.shouldLoop(hostEntity, this.time))
            return Mth.positiveModulo(Mth.lerp(partialTicks, this.timeO, this.time), animation.length);
        else
            return Mth.lerp(partialTicks, this.timeO, this.time);
    }

    @Nullable
    public LivingEntity getParentEntity() {
        return parentEntity;
    }

    protected void animate(HumanoidModel<?> model, Map<ModelPartIdentifier, PartPose> baseline, float partialTicks) {
        final float time = computeTime(partialTicks);
        final float transition = computeTransition(partialTicks);

        animation.channels.keySet().stream().filter(ModelPartIdentifier::isVanillaPart).forEach(limb -> {
            animateLimb(animation.channels.get(limb), limb.getModelPart(model), baseline.get(limb), time, transition);
        });
    }

    protected void animate(AdvancedHumanoidModel<?> model, ChangedEntity entity, Map<ModelPartIdentifier, PartPose> baseline, float partialTicks) {
        final float time = computeTime(partialTicks);
        final float transition = computeTransition(partialTicks);

        animation.channels.forEach((limb, either) -> {
            animateLimb(animation.channels.get(limb), limb.getModelPart(model, entity), baseline.get(limb), time, transition);
        });
    }

    protected void animate(EntityModel<?> model, LivingEntity renderEntity, Map<ModelPartIdentifier, PartPose> baseline, float partialTicks) {
        if (model instanceof AdvancedHumanoidModel<?> advancedHumanoid && renderEntity instanceof ChangedEntity changedEntity)
            this.animate(advancedHumanoid, changedEntity, baseline, partialTicks);
        else if (model instanceof HumanoidModel<?> humanoid)
            this.animate(humanoid, baseline, partialTicks);
    }

    public PartPose animatePartAs(Limb limb, PartPose modelPart, float partialTicks) {
        final float time = computeTime(partialTicks);
        final float transition = computeTransition(partialTicks);

        var channels = animation.channels.get(ModelPartIdentifier.forLimb(limb));
        if (channels == null)
            return modelPart;

        return animateLimb(channels, modelPart, modelPart, time, transition);
    }

    public PartPose animatePartAs(ModelPartIdentifier limb, PartPose modelPart, float partialTicks) {
        final float time = computeTime(partialTicks);
        final float transition = computeTransition(partialTicks);

        var channels = animation.channels.get(limb);
        if (channels == null)
            return modelPart;

        return animateLimb(channels, modelPart, modelPart, time, transition);
    }

    private void playSounds(float timeO, float time) {
        animation.soundEffects.forEach(soundEffect -> {
            soundEffect.playIfInRange(this.hostEntity, timeO, time);
        });
    }

    public void setTime(float time) {
        playSounds(this.timeO, time);
        this.time = time;
        this.timeO = time;
    }

    public void tickTime() {
        this.timeO = this.time;
        this.time += (1.0f / 20.0f) * parameters.getPlaybackSpeed(hostEntity, this.time);
        playSounds(this.timeO, this.time);
    }

    public boolean isDone() {
        if (parameters.shouldEndAnimation(hostEntity, this.time))
            return true;
        if (parameters.shouldLoop(hostEntity, this.time))
            return false;

        return animation.channels.values().stream().flatMap(List::stream).allMatch(channel -> channel.isDone(time)) &&
                entities.keySet().stream()
                        .map(AnimationContainer::getForEntity).filter(Optional::isPresent).map(Optional::get)
                        .map(container -> container.getAnimationSafe(AnimationCategory.PROP))
                        .filter(Optional::isPresent).map(Optional::get).allMatch(AnimationInstance::isDone);
    }

    public void clear() {
        entities.forEach(
                (entity, instance) -> AnimationContainer.getForEntity(entity).ifPresent(
                        container -> container.clearAnimation(AnimationCategory.PROP)));
    }

    public AnimationDefinition getDefinition() {
        return animation;
    }
}
