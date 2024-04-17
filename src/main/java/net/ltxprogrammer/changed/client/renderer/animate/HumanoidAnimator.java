package net.ltxprogrammer.changed.client.renderer.animate;

import net.ltxprogrammer.changed.client.CameraExtender;
import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.item.SpecializedAnimations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class HumanoidAnimator<T extends ChangedEntity, M extends EntityModel<T>> {
    public final M entityModel;
    public float hipOffset = -2.0f;
    public float torsoWidth = 5.0f;
    public float forwardOffset = 0.0f;
    public float torsoLength = 12.0f;
    public float armLength = 12.0f;
    public float legLength = 12.0f;

    public float calculateTorsoPositionY() {
        return hipOffset + (12.0f - legLength) + (12.0f - torsoLength);
    }

    public float calculateLegPositionY() {
        return hipOffset + (12.0f - legLength) + 12.0f;
    }

    public static ModelPart createNullPart(String... children) {
        return new ModelPart(List.of(), Arrays.stream(children).collect(Collectors.toMap(
                Function.identity(),
                name -> new ModelPart(List.of(), Map.of())
        )));
    }

    private final HumanoidModel<?> propertyModel;
    private final PlayerModel<?> propertyModelPlayer;
    private final Map<Integer, Runnable> setupHandsRunnable = new HashMap<>();
    private final EnumMap<AnimateStage, List<Animator<T, M>>> animators;
    private final EnumMap<AnimateStage, List<CameraAnimator<T, M>>> cameraAnimators;
    public HumanoidModel.ArmPose leftArmPose = HumanoidModel.ArmPose.EMPTY;
    public HumanoidModel.ArmPose rightArmPose = HumanoidModel.ArmPose.EMPTY;
    public boolean crouching;
    public float swimAmount = 0.0F;
    public float flyAmount = 0.0F;
    public float fallFlyingAmount = 0.0F;
    public AdvancedHumanoidModel.GrabState grabState = AdvancedHumanoidModel.GrabState.EMPTY;

    public float partialTicks = 0.0F;
    public float ageLerp = 0.0F;
    public float reachOut = 0.0F;

    public void resetVariables() {
        entityModel.attackTime = 0.0F;
        entityModel.riding = false;
        entityModel.young = false;
        
        crouching = false;
        flyAmount = 0.0F;
        fallFlyingAmount = 0.0F;
        swimAmount = 0.0F;

        partialTicks = 0.0F;
        ageLerp = 0.0F;
        reachOut = 0.0F;
        grabState = AdvancedHumanoidModel.GrabState.EMPTY;
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
        boolean shouldSit = entity.isPassenger() && (entity.getVehicle() != null && entity.getVehicle().shouldRiderSit());
        
        entityModel.attackTime = entity.getAttackAnim(partialTicks);
        entityModel.riding = shouldSit;
        entityModel.young = entity.isBaby();

        this.partialTicks = partialTicks;

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
        HumanoidModel.ArmPose humanoidmodel$armpose = AdvancedHumanoidRenderer.getArmPose(entity, InteractionHand.MAIN_HAND);
        HumanoidModel.ArmPose humanoidmodel$armpose1 = AdvancedHumanoidRenderer.getArmPose(entity, InteractionHand.OFF_HAND);
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

        grabState = AdvancedHumanoidModel.GrabState.EMPTY;
        entity.ifAbilityInstance(ChangedAbilities.GRAB_ENTITY_ABILITY.get(), instance -> {
            if (instance.shouldAnimateArms())
                grabState = AdvancedHumanoidModel.GrabState.HOLD;
            else if (instance.getController().getHoldTicks() > 0 && instance.grabbedEntity == null)
                grabState = AdvancedHumanoidModel.GrabState.REACH;
        });
    }

    public HumanoidAnimator(M entityModel) {
        this.entityModel = entityModel;
        this.animators = new EnumMap<>(AnimateStage.class);
        this.cameraAnimators = new EnumMap<>(AnimateStage.class);
        Arrays.stream(AnimateStage.values()).forEach(stage -> animators.put(stage, new ArrayList<>())); // Populate array
        Arrays.stream(AnimateStage.values()).forEach(stage -> cameraAnimators.put(stage, new ArrayList<>())); // Populate array

        this.propertyModel = new HumanoidModel(createNullPart("head", "hat", "body", "right_arm", "left_arm", "right_leg", "left_leg"));
        this.propertyModelPlayer = new PlayerModel(createNullPart("head", "hat", "body", "right_arm", "left_arm", "right_leg", "left_leg",
                "ear", "cloak", "left_sleeve", "right_sleeve", "left_pants", "right_pants", "jacket"), false);
    }

    public void applyPropertyModel(HumanoidModel<?> propertyModel) {
        leftArmPose = propertyModel.leftArmPose;
        rightArmPose = propertyModel.rightArmPose;
        crouching = propertyModel.crouching;
        swimAmount = propertyModel.swimAmount;
        entityModel.attackTime = propertyModel.attackTime;
        entityModel.riding = propertyModel.riding;
        entityModel.young = propertyModel.young;

        for (var anim : animators.get(AnimateStage.INIT)) {
            anim.copyFrom(propertyModel);
        }
    }

    /**
     * This function is only for property purposes, and not to be directly rendered
     * @return A HumanoidModel approximating the current latex model
     */
    public HumanoidModel<?> getPropertyModel(@Nullable EquipmentSlot slot) {
        propertyModel.leftArmPose = leftArmPose;
        propertyModel.rightArmPose = rightArmPose;
        propertyModel.crouching = crouching;
        propertyModel.swimAmount = swimAmount;
        propertyModel.attackTime = entityModel.attackTime;
        propertyModel.riding = entityModel.riding;
        propertyModel.young = entityModel.young;
        if (slot == null) {
            propertyModel.setAllVisible(true);
        } else {
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
        }

        for (var anim : animators.get(AnimateStage.INIT)) {
            anim.copyTo(propertyModel);
        }

        return propertyModel;
    }

    /**
     * This function is only for property purposes, and not to be directly rendered
     * @return A PlayerModel approximating the current latex model
     */
    public PlayerModel<?> getPropertyModelPlayer(@Nullable EquipmentSlot slot) {
        final HumanoidModel<?> humanoidModel = getPropertyModel(slot);
        humanoidModel.copyPropertiesTo((PlayerModel)this.propertyModelPlayer);
        this.propertyModelPlayer.swimAmount = humanoidModel.swimAmount;
        this.propertyModelPlayer.leftSleeve.copyFrom(humanoidModel.leftArm);
        this.propertyModelPlayer.rightPants.copyFrom(humanoidModel.rightLeg);
        this.propertyModelPlayer.leftPants.copyFrom(humanoidModel.leftLeg);
        this.propertyModelPlayer.jacket.copyFrom(humanoidModel.body);

        this.propertyModelPlayer.head.visible = humanoidModel.head.visible;
        this.propertyModelPlayer.hat.visible = humanoidModel.hat.visible;
        this.propertyModelPlayer.body.visible = humanoidModel.body.visible;
        this.propertyModelPlayer.jacket.visible = humanoidModel.body.visible;
        this.propertyModelPlayer.rightArm.visible = humanoidModel.rightArm.visible;
        this.propertyModelPlayer.rightSleeve.visible = humanoidModel.rightArm.visible;
        this.propertyModelPlayer.leftArm.visible = humanoidModel.leftArm.visible;
        this.propertyModelPlayer.leftSleeve.visible = humanoidModel.leftArm.visible;
        this.propertyModelPlayer.rightLeg.visible = humanoidModel.rightLeg.visible;
        this.propertyModelPlayer.rightPants.visible = humanoidModel.rightLeg.visible;
        this.propertyModelPlayer.leftLeg.visible = humanoidModel.leftLeg.visible;
        this.propertyModelPlayer.leftPants.visible = humanoidModel.leftLeg.visible;

        return this.propertyModelPlayer;
    }

    public static <T extends ChangedEntity, M extends EntityModel<T>> HumanoidAnimator<T, M> of(M entityModel) {
        return new HumanoidAnimator<>(entityModel);
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
        AnimateStage.ORDER.forEach(animateStage -> {
            if (animateStage.test(this, entity))
                setupAnimStage(animateStage, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        });
    }

    public void setupCameraAnim(@NotNull CameraExtender camera, @NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        AnimateStage.ORDER.forEach(animateStage -> {
            if (animateStage.test(this, entity))
                setupCameraAnimStage(animateStage, camera, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        });
    }

    public HumanoidAnimator<T, M> forwardOffset(float v) {
        this.forwardOffset = v;
        return this;
    }

    public HumanoidAnimator<T, M> legLength(float v) {
        this.legLength = v;
        return this;
    }

    public HumanoidAnimator<T, M> hipOffset(float v) {
        this.hipOffset = v;
        return this;
    }

    public HumanoidAnimator<T, M> armLength(float v) {
        this.armLength = v;
        return this;
    }

    public HumanoidAnimator<T, M> torsoLength(float v) {
        this.torsoLength = v;
        return this;
    }

    public static enum AnimateStage implements BiPredicate<HumanoidAnimator<?,?>, ChangedEntity> {
        INIT,
        RIDE((animator, latex) -> animator.entityModel.riding),
        SLEEP((animator, latex) -> latex.isSleeping()),
        ATTACK,
        CROUCH((animator, latex) -> animator.crouching),
        STAND((animator, latex) -> !animator.crouching),
        BOB,
        CREATIVE_FLY((animator, latex) -> animator.flyAmount > 0f),
        FALL_FLY((animator, latex) -> latex.isFallFlying()),
        SWIM((animator, latex) -> animator.swimAmount > 0f),
        FINAL;

        public static final List<AnimateStage> ORDER = List.of(INIT, RIDE, SLEEP, ATTACK, CROUCH, STAND, BOB, CREATIVE_FLY, FALL_FLY, SWIM, FINAL);

        private final BiPredicate<HumanoidAnimator<?,?>, ChangedEntity> predicate;

        AnimateStage() {
            this.predicate = AnimateStage::always;
        }

        AnimateStage(BiPredicate<HumanoidAnimator<?,?>, ChangedEntity> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean test(HumanoidAnimator<?, ?> animator, ChangedEntity latex) {
            return predicate.test(animator, latex);
        }

        private static boolean always(HumanoidAnimator<?,?> animator, ChangedEntity entity) {
            return true;
        }
    }

    public static abstract class Animator<T extends ChangedEntity, M extends EntityModel<T>> {
        protected HumanoidAnimator<T, M> core;

        public abstract AnimateStage preferredStage();
        public abstract void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch);
        public void copyTo(HumanoidModel<?> humanoidModel) {}
        public void copyFrom(HumanoidModel<?> humanoidModel) {}

        protected float sinLerp(float sin, float a, float b) {
            return Mth.lerp(sin * 0.5f + 0.5f, a, b);
        }
    }

    public static abstract class CameraAnimator<T extends ChangedEntity, M extends EntityModel<T>> {
        protected HumanoidAnimator<T, M> core;

        // Understandably, users may not want the camera to move heavily while playing.
        public abstract boolean requiresViewBob();
        public abstract AnimateStage preferredStage();
        public abstract void setupAnim(@NotNull CameraExtender camera, @NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch);

        protected float sinLerp(float sin, float a, float b) {
            return Mth.lerp(sin * 0.5f + 0.5f, a, b);
        }
    }

    public HumanoidAnimator<T, M> addAnimator(Animator<T, M> animator) {
        return addAnimator(animator, animator.preferredStage());
    }

    public HumanoidAnimator<T, M> addAnimator(Animator<T, M> animator, AnimateStage stage) {
        animator.core = this;
        animators.computeIfAbsent(stage, s -> new ArrayList<>()).add(animator);
        return this;
    }

    public HumanoidAnimator<T, M> addCameraAnimator(CameraAnimator<T, M> animator) {
        return addCameraAnimator(animator, animator.preferredStage());
    }

    public HumanoidAnimator<T, M> addCameraAnimator(CameraAnimator<T, M> animator, AnimateStage stage) {
        animator.core = this;
        cameraAnimators.computeIfAbsent(stage, s -> new ArrayList<>()).add(animator);
        return this;
    }

    public HumanoidAnimator<T, M> addPreset(Consumer<HumanoidAnimator<T, M>> fn) {
        fn.accept(this);
        return this;
    }

    public void setupHand() {
        setupHandsRunnable.forEach((level, task) -> task.run());
    }

    public HumanoidAnimator<T, M> setupHandsOld(int level, ModelPart leftArm, ModelPart rightArm) {
        setupHandsRunnable.put(level, () -> {
            rightArm.x += 3F;
            leftArm.x += -3F;
            rightArm.z += -1F - forwardOffset;
            leftArm.z += -1F - forwardOffset;
        });
        return this;
    }

    public HumanoidAnimator<T, M> setupHands(int level, ModelPart leftArm, ModelPart rightArm) {
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
        HumanoidAnimator<T, M> tmp = this;
        return new SpecializedAnimations.AnimationHandler.EntityStateContext(entity, entityModel.attackTime, partialTicks) {
            final HumanoidAnimator<T, M> controller = tmp;

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
