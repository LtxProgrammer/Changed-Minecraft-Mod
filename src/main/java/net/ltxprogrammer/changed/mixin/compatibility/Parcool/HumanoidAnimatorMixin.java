package net.ltxprogrammer.changed.mixin.compatibility.Parcool;

import com.alrex.parcool.client.animation.system.AnimatableModelPart;
import com.alrex.parcool.client.animation.system.BlendingModelTransform;
import com.alrex.parcool.client.animation.system.IPlayerAnimatorHolder;
import com.alrex.parcool.client.animation.system.data.Transform;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.extension.RequiredMods;
import net.minecraft.client.model.HumanoidModel;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = HumanoidAnimator.class, remap = false)
@RequiredMods("parcool")
public abstract class HumanoidAnimatorMixin<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> {
    @Shadow public abstract void applyPropertyModel(HumanoidModel<?> propertyModel);

    @Shadow @Final public M entityModel;

    @Shadow protected abstract void setupAnimStage(HumanoidAnimator.AnimateStage stage, @NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch);

    @WrapMethod(method = "setupAnim")
    public void onSetupAnimHead(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Operation<Void> original) {
        if (!entity.isFallFlying() && !entity.isPassenger() && entity.getUnderlyingPlayer() instanceof IPlayerAnimatorHolder holder) {
            BlendingModelTransform transform = holder.getParCoolPlayerAnimator().getCurrentTransformation();

            //this.parcool$resetModel();
            // Our equivalent of ^^^
            this.setupAnimStage(HumanoidAnimator.AnimateStage.INIT, entity, 0f, 0f, 0f, 0f, 0f);
            this.setupAnimStage(HumanoidAnimator.AnimateStage.STAND, entity, 0f, 0f, 0f, 0f, 0f);
            this.entityModel.syncPropertyModel(entity);

            if (transform == null) {
                original.call(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
                return;
            }

            this.entityModel.syncPropertyModel(entity);

            if (transform.isOverwriting()) {
                Transform headTransform = transform.transformation().transforms().get(AnimatableModelPart.HEAD);
                if (headTransform != null) {
                    headTransform.apply(this.entityModel.head);
                }

                Transform rATransform = transform.transformation().transforms().get(AnimatableModelPart.RIGHT_ARM);
                if (rATransform != null) {
                    rATransform.apply(this.entityModel.rightArm);
                }

                Transform rLTransform = transform.transformation().transforms().get(AnimatableModelPart.RIGHT_LEG);
                if (rLTransform != null) {
                    rLTransform.apply(this.entityModel.rightLeg);
                }

                Transform lATransform = transform.transformation().transforms().get(AnimatableModelPart.LEFT_ARM);
                if (lATransform != null) {
                    lATransform.apply(this.entityModel.leftArm);
                }

                Transform lLTransform = transform.transformation().transforms().get(AnimatableModelPart.LEFT_LEG);
                if (lLTransform != null) {
                    lLTransform.apply(this.entityModel.leftLeg);
                }
            } else {
                original.call(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
                float blendingFactor = transform.blendFactor();
                Transform headTransform = transform.transformation().transforms().get(AnimatableModelPart.HEAD);
                if (headTransform != null) {
                    headTransform.applyInQuaternion(this.entityModel.head, blendingFactor, true);
                }

                Transform rATransform = transform.transformation().transforms().get(AnimatableModelPart.RIGHT_ARM);
                if (rATransform != null) {
                    rATransform.applyInQuaternion(this.entityModel.rightArm, blendingFactor, false);
                }

                Transform rLTransform = transform.transformation().transforms().get(AnimatableModelPart.RIGHT_LEG);
                if (rLTransform != null) {
                    rLTransform.applyInQuaternion(this.entityModel.rightLeg, blendingFactor, false);
                }

                Transform lATransform = transform.transformation().transforms().get(AnimatableModelPart.LEFT_ARM);
                if (lATransform != null) {
                    lATransform.applyInQuaternion(this.entityModel.leftArm, blendingFactor, false);
                }

                Transform lLTransform = transform.transformation().transforms().get(AnimatableModelPart.LEFT_LEG);
                if (lLTransform != null) {
                    lLTransform.applyInQuaternion(this.entityModel.leftLeg, blendingFactor, false);
                }

                Transform.NO_TRANSFORMATION.applyInQuaternion(this.entityModel.body, blendingFactor, true);
            }

            this.applyPropertyModel(this.entityModel);
        } else {
            original.call(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        }
    }
}
