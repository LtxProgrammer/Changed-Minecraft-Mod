package net.ltxprogrammer.changed.client.animations;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.client.ClientLivingEntityExtender;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.animation.AnimationCategory;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Holds all the keyframe animations for an entity. The represent entity for TF'd players map to their host's container.
 * {@link #getForEntity(LivingEntity)} will return the container for the host entity.
 */
public class AnimationContainer {
    private final LivingEntity entity;
    private final Map<AnimationCategory, AnimationInstance> animations = new HashMap<>();
    private final Map<EntityModel<?>, Map<ModelPartIdentifier, PartPose>> baselines = new HashMap<>();
    private static final Map<EntityModel<?>, Pair<LivingEntity, Map<ModelPartIdentifier, PartPose>>> LAST_USED_BASELINE = new HashMap<>();

    protected AnimationContainer(LivingEntity entity) {
        this.entity = entity;
    }

    public static Optional<AnimationContainer> getForEntity(LivingEntity livingEntity) {
        return ((ClientLivingEntityExtender)EntityUtil.maybeGetUnderlying(livingEntity)).getAnimations();
    }

    @NotNull
    public static AnimationContainer getForEntityOrCreate(LivingEntity livingEntity) {
        return ((ClientLivingEntityExtender)EntityUtil.maybeGetUnderlying(livingEntity)).getAnimationsOrCreate(() -> new AnimationContainer(livingEntity));
    }

    public Stream<AnimationInstance> getOrderedAnimations() {
        return animations.values().stream();
    }

    public void addAnimation(AnimationCategory category, AnimationInstance animationInstance) {
        clearAnimation(category);
        animations.put(category, animationInstance);
    }

    public @Nullable AnimationInstance getAnimation(AnimationCategory category) {
        return animations.get(category);
    }

    public Optional<AnimationInstance> getAnimationSafe(AnimationCategory category) {
        return Optional.ofNullable(getAnimation(category));
    }

    public @Nullable AnimationInstance getAnimation(AnimationCategory category, Supplier<AnimationDefinition> definition) {
        final var instance = animations.get(category);
        if (instance == null)
            return null;
        if (instance.getDefinition() != definition.get())
            return null;
        return instance;
    }

    public Optional<AnimationInstance> getAnimationSafe(AnimationCategory category, Supplier<AnimationDefinition> definition) {
        return Optional.ofNullable(getAnimation(category, definition));
    }

    public void clearAnimation(AnimationCategory category) {
        if (!animations.containsKey(category))
            return;

        animations.get(category).clear();
        animations.remove(category);
    }

    public void clearAnimation(AnimationCategory category, Supplier<AnimationDefinition> definition) {
        if (!animations.containsKey(category))
            return;

        if (animations.get(category).getDefinition() != definition.get())
            return;

        animations.get(category).clear();
        animations.remove(category);
    }

    public void tick() {
        animations.entrySet().stream().filter(entry -> entry.getValue().isDone()).collect(Collectors.toSet())
                .forEach(completed -> clearAnimation(completed.getKey()));
        animations.values().forEach(AnimationInstance::tickTime);
        new HashSet<>(baselines.entrySet()).forEach(entry -> {
            if (entry.getValue().isEmpty())
                baselines.remove(entry.getKey());
        });
    }

    public boolean isEmpty() {
        return animations.isEmpty() && (baselines.isEmpty() || baselines.values().stream().allMatch(Map::isEmpty));
    }

    private Map<ModelPartIdentifier, PartPose> getBaselineFor(EntityModel<?> entityModel) {
        return this.baselines.computeIfAbsent(entityModel, model -> new HashMap<>());
    }

    private Map<ModelPartIdentifier, PartPose> captureBaseline(EntityModel<?> entityModel, LivingEntity renderEntity) {
        final var baseline = this.getBaselineFor(entityModel);
        if (entityModel instanceof AdvancedHumanoidModel<?> advancedModel && renderEntity instanceof ChangedEntity entity) {
            animations.values().stream()
                    .flatMap(animationInstance -> animationInstance.animation.channels.keySet().stream())
                    .forEach(modelPartIdentifier -> {
                        modelPartIdentifier.getModelPartSafe(advancedModel, entity).map(ModelPart::storePose).ifPresent(pose -> {
                            baseline.put(modelPartIdentifier, pose);
                        });
                    });
        }
        else if (entityModel instanceof HumanoidModel<?> humanoidModel) {
            animations.values().stream()
                    .flatMap(animationInstance -> animationInstance.animation.channels.keySet().stream())
                    .forEach(modelPartIdentifier -> {
                        modelPartIdentifier.getModelPartSafe(humanoidModel).map(ModelPart::storePose).ifPresent(pose -> {
                            baseline.put(modelPartIdentifier, pose);
                        });
                    });
        }

        return baseline;
    }

    public <T extends LivingEntity> void animateRotations(PoseStack poseStack, float partialTicks) {
        this.getOrderedAnimations().forEach(animationInstance -> {
            animationInstance.animateRotations(poseStack, partialTicks);
        });
    }

    /**
     * Should be called right after the last instruction in a model's setupAnim function.
     * @param entityModel model to animate
     * @param renderEntity entity of the model
     * @param partialTicks partial tick for frame
     */
    public <T extends LivingEntity> void animateModel(EntityModel<T> entityModel, T renderEntity, float partialTicks) {
        final var baseline = this.captureBaseline(entityModel, renderEntity);

        this.getOrderedAnimations().forEach(animationInstance -> {
            animationInstance.animate(entityModel, renderEntity, baseline, partialTicks);
        });

        LAST_USED_BASELINE.put(entityModel, Pair.of(renderEntity, baseline));
    }

    /**
     * Restores the last applied baseline so limbs go back to where they should be.
     * @param entityModel model to reset
     */
    public static <T extends LivingEntity> void resetModel(EntityModel<T> entityModel) {
        if (!LAST_USED_BASELINE.containsKey(entityModel))
            return;
        var pair = LAST_USED_BASELINE.get(entityModel);
        final var baseline = pair.getSecond();
        if (entityModel instanceof AdvancedHumanoidModel<?> advancedModel && pair.getFirst() instanceof ChangedEntity entity) {
            baseline.forEach((modelPartIdentifier, pose) -> {
                modelPartIdentifier.getModelPartSafe(advancedModel, entity).ifPresent(modelPart -> modelPart.loadPose(pose));
            });
        }
        else if (entityModel instanceof HumanoidModel<?> humanoidModel) {
            baseline.forEach((modelPartIdentifier, pose) -> {
                modelPartIdentifier.getModelPartSafe(humanoidModel).ifPresent(modelPart -> modelPart.loadPose(pose));
            });
        }

        baseline.clear();
        LAST_USED_BASELINE.remove(entityModel);
    }
}
