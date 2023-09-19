package net.ltxprogrammer.changed.client.renderer.animate;

import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.item.SpecializedAnimations;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

public class LatexAnimator<T extends LatexEntity, M extends EntityModel<T>> {
    public final M entityModel;
    public float hipOffset = -2.0f;
    public float torsoWidth = 5.0f;
    public float forwardOffset = 0.0f;
    public float torsoLength = 12.0f;
    public float armLength = 12.0f;
    public float legLength = 12.0f;

    private final HumanoidModel<?> propertyModel;
    private final Map<Integer, Runnable> setupHandsRunnable = new HashMap<>();
    private final EnumMap<AnimateStage, List<Animator<T, M>>> animators = new EnumMap<>(AnimateStage.class);
    public HumanoidModel.ArmPose leftArmPose = HumanoidModel.ArmPose.EMPTY;
    public HumanoidModel.ArmPose rightArmPose = HumanoidModel.ArmPose.EMPTY;
    public boolean crouching;
    public float swimAmount;

    public LatexAnimator(M entityModel) {
        this.entityModel = entityModel;
        this.propertyModel = new HumanoidModel(new ModelPart(Collections.emptyList(),
                Map.of("head", new ModelPart(Collections.emptyList(), Collections.emptyMap()), "hat", new ModelPart(Collections.emptyList(), Collections.emptyMap()), "body",
                        new ModelPart(Collections.emptyList(), Collections.emptyMap()), "right_arm", new ModelPart(Collections.emptyList(), Collections.emptyMap()), "left_arm",
                        new ModelPart(Collections.emptyList(), Collections.emptyMap()), "right_leg", new ModelPart(Collections.emptyList(), Collections.emptyMap()), "left_leg",
                        new ModelPart(Collections.emptyList(), Collections.emptyMap()))));
    }

    /**
     * This function is only for property purposes, and not to be directly rendered
     * @return A HumanoidModel approximating the current latex model
     */
    public HumanoidModel<?> getPropertyModel(EquipmentSlot slot) {
        propertyModel.leftArmPose = leftArmPose;
        propertyModel.rightArmPose = rightArmPose;
        propertyModel.crouching = crouching;
        propertyModel.swimAmount = swimAmount;
        propertyModel.attackTime = entityModel.attackTime;
        propertyModel.riding = entityModel.riding;
        propertyModel.young = entityModel.young;
        propertyModel.setAllVisible(false);
        switch (slot) {
            case HEAD:
                propertyModel.head.visible = true;
                propertyModel.hat.visible = true;
                break;
            case CHEST:
                propertyModel.body.visible = true;
                propertyModel.leftArm.visible = true;
                propertyModel.rightArm.visible = true;
                break;
            case LEGS:
                propertyModel.body.visible = true;
            case FEET:
                propertyModel.leftLeg.visible = true;
                propertyModel.rightLeg.visible = true;
                break;
        }

        return propertyModel;
    }

    public static <T extends LatexEntity, M extends EntityModel<T>> LatexAnimator<T, M> of(M entityModel) {
        return new LatexAnimator<>(entityModel);
    }

    private void setupAnimStage(AnimateStage stage, @NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        var animatorList = animators.get(stage);
        if (animatorList == null) return;

        animatorList.forEach(animator -> {
            animator.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        });
    }

    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        setupAnimStage(AnimateStage.INIT, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        setupAnimStage(AnimateStage.ATTACK, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        if (entityModel.riding)
            setupAnimStage(AnimateStage.RIDE, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        if (entity.isSleeping())
            setupAnimStage(AnimateStage.SLEEP, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        if (crouching)
            setupAnimStage(AnimateStage.CROUCH, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        else
            setupAnimStage(AnimateStage.STAND, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        setupAnimStage(AnimateStage.BOB, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        if (entity.isFallFlying())
            setupAnimStage(AnimateStage.FALL_FLY, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        if (swimAmount > 0f)
            setupAnimStage(AnimateStage.SWIM, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        setupAnimStage(AnimateStage.FINAL, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    public LatexAnimator<T, M> forwardOffset(float v) {
        this.forwardOffset = v;
        return this;
    }

    public LatexAnimator<T, M> legLength(float v) {
        this.legLength = v;
        return this;
    }

    public LatexAnimator<T, M> hipOffset(float v) {
        this.hipOffset = v;
        return this;
    }

    public LatexAnimator<T, M> armLength(float v) {
        this.armLength = v;
        return this;
    }

    public LatexAnimator<T, M> torsoLength(float v) {
        this.torsoLength = v;
        return this;
    }

    public static enum AnimateStage {
        INIT,
        RIDE,
        SLEEP,
        ATTACK,
        CROUCH,
        STAND,
        BOB,
        FALL_FLY,
        SWIM,
        FINAL;

        public static final List<AnimateStage> ORDER = List.of(INIT, RIDE, SLEEP, ATTACK, CROUCH, STAND, BOB, FALL_FLY, SWIM);
    }

    public static abstract class Animator<T extends LatexEntity, M extends EntityModel<T>> {
        protected LatexAnimator<T, M> core;

        public abstract AnimateStage preferredStage();
        public abstract void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch);
    }

    public LatexAnimator<T, M> addAnimator(Animator<T, M> animator) {
        return addAnimator(animator, animator.preferredStage());
    }

    public LatexAnimator<T, M> addAnimator(Animator<T, M> animator, AnimateStage stage) {
        animator.core = this;
        animators.computeIfAbsent(stage, s -> new ArrayList<>()).add(animator);
        return this;
    }

    public LatexAnimator<T, M> addPreset(Consumer<LatexAnimator<T, M>> fn) {
        fn.accept(this);
        return this;
    }

    public void setupHand() {
        setupHandsRunnable.forEach((level, task) -> task.run());
    }

    public LatexAnimator<T, M> setupHands(int level, ModelPart leftArm, ModelPart rightArm) {
        setupHandsRunnable.put(level, () -> {
            rightArm.x += 3F;
            leftArm.x += -3F;
            rightArm.z += -1F - forwardOffset;
            leftArm.z += -1F - forwardOffset;
        });
        return this;
    }

    public SpecializedAnimations.AnimationHandler.EntityStateContext entityContextOf(T entity, float partialTicks) {
        LatexAnimator<T, M> tmp = this;
        return new SpecializedAnimations.AnimationHandler.EntityStateContext(entity, entityModel.attackTime, partialTicks) {
            final LatexAnimator<T, M> controller = tmp;

            @Override
            public HumanoidArm getAttackArm() {
                return controller.getAttackArm(entity);
            }
        };
    }

    public HumanoidArm getAttackArm(T entity) {
        HumanoidArm humanoidarm = entity.getMainArm();
        return entity.swingingArm == InteractionHand.MAIN_HAND ? humanoidarm : humanoidarm.getOpposite();
    }

    public static float rotlerpRad(float lerp, float rotO, float rot) {
        float f = (rot - rotO) % ((float)Math.PI * 2F);
        if (f < -(float)Math.PI) {
            f += ((float)Math.PI * 2F);
        }

        if (f >= (float)Math.PI) {
            f -= ((float)Math.PI * 2F);
        }

        return rotO + lerp * f;
    }
}
