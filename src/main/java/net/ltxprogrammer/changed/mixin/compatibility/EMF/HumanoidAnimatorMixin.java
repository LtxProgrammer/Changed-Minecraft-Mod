package net.ltxprogrammer.changed.mixin.compatibility.EMF;

import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.extension.RequiredMods;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.parts.EMFModelPartWithState;

@Mixin(value = HumanoidAnimator.class, remap = false)
@RequiredMods("entity_model_features")
public abstract class HumanoidAnimatorMixin<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> {
    @Shadow public abstract void applyPropertyModel(HumanoidModel<?> propertyModel);

    @Shadow
    @Final
    public M entityModel;

    @Inject(method = "setupAnim", at = @At("RETURN"))
    public void copyPlayerAnimations(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        var player = entity.getUnderlyingPlayer();
        if (player == null)
            return;

        HumanoidModel<?> model = this.entityModel;
        this.entityModel.syncPropertyModel(entity);

        if (Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player) instanceof LivingEntityRenderer<?,?> playerRenderer) {
            if (playerRenderer.getModel() instanceof HumanoidModel playerModel &&
                    playerModel.body instanceof EMFModelPartWithState emfPart) {
                // Ensure vanilla player model is setup
                playerModel.setupAnim(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
                PartPose headPose = mergePositionRotation(playerModel.head, model.head);
                PartPose bodyPose = mergePositionRotation(playerModel.body, model.body);
                PartPose leftArmPose = mergePositionRotation(playerModel.leftArm, model.leftArm);
                PartPose rightArmPose = mergePositionRotation(playerModel.rightArm, model.rightArm);
                PartPose leftLegPose = mergePositionRotation(playerModel.leftLeg, model.leftLeg);
                PartPose rightLegPose = mergePositionRotation(playerModel.rightLeg, model.rightLeg);
                playerModel.head.loadPose(headPose);
                playerModel.body.loadPose(bodyPose);
                playerModel.leftArm.loadPose(leftArmPose);
                playerModel.rightArm.loadPose(rightArmPose);
                playerModel.leftLeg.loadPose(leftLegPose);
                playerModel.rightLeg.loadPose(rightLegPose);

                // Run animation code to vanilla model
                emfPart.getRoot().animate();

                // Apply animation differences to advanced model
                computeNewPartPose(headPose, playerModel.head, model.head);
                computeNewPartPose(bodyPose, playerModel.body, model.body);
                computeNewPartPose(leftArmPose, playerModel.leftArm, model.leftArm);
                computeNewPartPose(rightArmPose, playerModel.rightArm, model.rightArm);
                computeNewPartPose(leftLegPose, playerModel.leftLeg, model.leftLeg);
                computeNewPartPose(rightLegPose, playerModel.rightLeg, model.rightLeg);

                model.hat.copyFrom(model.head);

                this.applyPropertyModel(model);
            }
        }
    }

    @Unique
    private void computeNewPartPose(PartPose preAnimation, ModelPart vanillaPart, ModelPart advancedPart) {
        advancedPart.x += vanillaPart.x - preAnimation.x;
        advancedPart.y += vanillaPart.y - preAnimation.y;
        advancedPart.z += vanillaPart.z - preAnimation.z;
        advancedPart.xRot += vanillaPart.xRot - preAnimation.xRot;
        advancedPart.yRot += vanillaPart.yRot - preAnimation.yRot;
        advancedPart.zRot += vanillaPart.zRot - preAnimation.zRot;
    }

    @Unique
    private PartPose mergePositionRotation(ModelPart position, ModelPart rotation) {
        return PartPose.offsetAndRotation(
                position.x, position.y, position.z,
                rotation.xRot, rotation.yRot, rotation.zRot
        );
    }
}
