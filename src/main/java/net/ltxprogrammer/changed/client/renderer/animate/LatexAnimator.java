package net.ltxprogrammer.changed.client.renderer.animate;

import net.ltxprogrammer.changed.client.CameraExtender;
import net.ltxprogrammer.changed.client.renderer.LatexHumanoidRenderer;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.item.SpecializedAnimations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
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
    private final EnumMap<AnimateStage, List<Animator<T, M>>> animators;
    private final EnumMap<AnimateStage, List<CameraAnimator<T, M>>> cameraAnimators;
    public HumanoidModel.ArmPose leftArmPose = HumanoidModel.ArmPose.EMPTY;
    public HumanoidModel.ArmPose rightArmPose = HumanoidModel.ArmPose.EMPTY;
    public boolean crouching;
    public float swimAmount = 0.0F;
    public float flyAmount = 0.0F;
    public float fallFlyingAmount = 0.0F;

    public float ageLerp = 0.0F;
    public float reachOut = 0.0F;

    public void resetVariables() {
        crouching = false;
        flyAmount = 0.0F;
        fallFlyingAmount = 0.0F;
        swimAmount = 0.0F;
        ageLerp = 0.0F;
        reachOut = 0.0F;
    }

    protected float distanceTo(@NotNull T entity, @NotNull Entity other, float partialTicks) {
        Vec3 entityPos = entity.getPosition(partialTicks);
        Vec3 otherPos = other.getPosition(partialTicks);
        float f = (float)(entityPos.x - otherPos.x);
        float f1 = (float)(entityPos.y - otherPos.y);
        float f2 = (float)(entityPos.z - otherPos.z);
        return Mth.sqrt(f * f + f1 * f1 + f2 * f2);
    }

    public void setupVariables(T entity, float partialTicks) {
        LivingEntity target = entity.getTarget();
        reachOut = target != null ?
                Mth.clamp(Mth.inverseLerp(this.distanceTo(entity, target, partialTicks), 5.0f, 2.0f), 0.0f, 1.0f) : 0.0f;
        if (!entity.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty() || !entity.getItemBySlot(EquipmentSlot.OFFHAND).isEmpty())
            reachOut = 0.0F;
        final float ageAdjusted = (entity.tickCount + partialTicks) * 0.33333334F * 0.25F * 0.15f;
        float ageSin = Mth.sin(ageAdjusted * Mth.PI * 0.5f);
        float ageCos = Mth.cos(ageAdjusted * Mth.PI * 0.5f);
        ageLerp = Mth.lerp(1.0f - Mth.abs(Mth.positiveModulo(ageAdjusted, 2.0f) - 1.0f),
                ageSin * ageSin * ageSin * ageSin, 1.0f - (ageCos * ageCos * ageCos * ageCos));
        float fallFlyingTicks = (float)entity.getFallFlyingTicks();
        fallFlyingAmount = Mth.clamp(fallFlyingTicks * fallFlyingTicks / 100.0F, 0.0F, 1.0F);
        swimAmount = entity.getSwimAmount(partialTicks);
        flyAmount = entity.getFlyAmount(partialTicks);
        crouching = entity.isCrouching();
        HumanoidModel.ArmPose humanoidmodel$armpose = LatexHumanoidRenderer.getArmPose(entity, InteractionHand.MAIN_HAND);
        HumanoidModel.ArmPose humanoidmodel$armpose1 = LatexHumanoidRenderer.getArmPose(entity, InteractionHand.OFF_HAND);
        if (humanoidmodel$armpose.isTwoHanded()) {
            humanoidmodel$armpose1 = entity.getOffhandItem().isEmpty() ? HumanoidModel.ArmPose.EMPTY : HumanoidModel.ArmPose.ITEM;
        }

        if (entity.getMainArm() == HumanoidArm.RIGHT) {
            rightArmPose = humanoidmodel$armpose;
            leftArmPose = humanoidmodel$armpose1;
        } else {
            rightArmPose = humanoidmodel$armpose1;
            leftArmPose = humanoidmodel$armpose;
        }
    }

    public LatexAnimator(M entityModel) {
        this.entityModel = entityModel;
        this.animators = new EnumMap<>(AnimateStage.class);
        this.cameraAnimators = new EnumMap<>(AnimateStage.class);
        Arrays.stream(AnimateStage.values()).forEach(stage -> animators.put(stage, new ArrayList<>())); // Populate array
        Arrays.stream(AnimateStage.values()).forEach(stage -> cameraAnimators.put(stage, new ArrayList<>())); // Populate array

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

        for (var anim : animators.get(AnimateStage.INIT)) {
            anim.copyTo(propertyModel);
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

    private void setupCameraAnimStage(AnimateStage stage, @NotNull CameraExtender camera, @NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        var animatorList = cameraAnimators.get(stage);
        if (animatorList == null) return;
        boolean bobView = Minecraft.getInstance().options.bobView;

        animatorList.forEach(animator -> {
            if (animator.requiresViewBob() && !bobView)
                return;
            animator.setupAnim(camera, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
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
        if (flyAmount > 0f)
            setupAnimStage(AnimateStage.CREATIVE_FLY, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        if (entity.isFallFlying())
            setupAnimStage(AnimateStage.FALL_FLY, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        if (swimAmount > 0f)
            setupAnimStage(AnimateStage.SWIM, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        setupAnimStage(AnimateStage.FINAL, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    public void setupCameraAnim(@NotNull CameraExtender camera, @NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        setupCameraAnimStage(AnimateStage.INIT, camera, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        setupCameraAnimStage(AnimateStage.ATTACK, camera, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        if (entityModel.riding)
            setupCameraAnimStage(AnimateStage.RIDE, camera, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        if (entity.isSleeping())
            setupCameraAnimStage(AnimateStage.SLEEP, camera, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        if (crouching)
            setupCameraAnimStage(AnimateStage.CROUCH, camera, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        else
            setupCameraAnimStage(AnimateStage.STAND, camera, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        setupCameraAnimStage(AnimateStage.BOB, camera, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        if (flyAmount > 0f)
            setupCameraAnimStage(AnimateStage.CREATIVE_FLY, camera, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        if (entity.isFallFlying())
            setupCameraAnimStage(AnimateStage.FALL_FLY, camera, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        if (swimAmount > 0f)
            setupCameraAnimStage(AnimateStage.SWIM, camera, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        setupCameraAnimStage(AnimateStage.FINAL, camera, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
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
        CREATIVE_FLY,
        FALL_FLY,
        SWIM,
        FINAL;

        public static final List<AnimateStage> ORDER = List.of(INIT, RIDE, SLEEP, ATTACK, CROUCH, STAND, BOB, FALL_FLY, SWIM);
    }

    public static abstract class Animator<T extends LatexEntity, M extends EntityModel<T>> {
        protected LatexAnimator<T, M> core;

        public abstract AnimateStage preferredStage();
        public abstract void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch);
        public void copyTo(HumanoidModel<?> humanoidModel) {}

        protected float sinLerp(float sin, float a, float b) {
            return Mth.lerp(sin * 0.5f + 0.5f, a, b);
        }
    }

    public static abstract class CameraAnimator<T extends LatexEntity, M extends EntityModel<T>> {
        protected LatexAnimator<T, M> core;

        // Understandably, users may not want the camera to move heavily while playing.
        public abstract boolean requiresViewBob();
        public abstract AnimateStage preferredStage();
        public abstract void setupAnim(@NotNull CameraExtender camera, @NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch);

        protected float sinLerp(float sin, float a, float b) {
            return Mth.lerp(sin * 0.5f + 0.5f, a, b);
        }
    }

    public LatexAnimator<T, M> addAnimator(Animator<T, M> animator) {
        return addAnimator(animator, animator.preferredStage());
    }

    public LatexAnimator<T, M> addAnimator(Animator<T, M> animator, AnimateStage stage) {
        animator.core = this;
        animators.computeIfAbsent(stage, s -> new ArrayList<>()).add(animator);
        return this;
    }

    public LatexAnimator<T, M> addCameraAnimator(CameraAnimator<T, M> animator) {
        return addCameraAnimator(animator, animator.preferredStage());
    }

    public LatexAnimator<T, M> addCameraAnimator(CameraAnimator<T, M> animator, AnimateStage stage) {
        animator.core = this;
        cameraAnimators.computeIfAbsent(stage, s -> new ArrayList<>()).add(animator);
        return this;
    }

    public LatexAnimator<T, M> addPreset(Consumer<LatexAnimator<T, M>> fn) {
        fn.accept(this);
        return this;
    }

    public void setupHand() {
        setupHandsRunnable.forEach((level, task) -> task.run());
    }

    public LatexAnimator<T, M> setupHandsOld(int level, ModelPart leftArm, ModelPart rightArm) {
        setupHandsRunnable.put(level, () -> {
            rightArm.x += 3F;
            leftArm.x += -3F;
            rightArm.z += -1F - forwardOffset;
            leftArm.z += -1F - forwardOffset;
        });
        return this;
    }

    public LatexAnimator<T, M> setupHands(int level, ModelPart leftArm, ModelPart rightArm) {
        setupHandsRunnable.put(level, () -> {
            rightArm.x = -2.5F;
            leftArm.x = 2.5F;
            rightArm.y = 1F;
            leftArm.y = 1F;
            rightArm.z = 0F;
            leftArm.z = 0F;

            rightArm.xRot = 0f;
            rightArm.yRot = 0f;
            rightArm.zRot = 0.05f;
            leftArm.xRot = 0f;
            leftArm.yRot = 0f;
            leftArm.zRot = -0.05f;
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
