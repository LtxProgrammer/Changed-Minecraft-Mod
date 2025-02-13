package net.ltxprogrammer.changed.mixin.compatibility.EMF;

import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModelInterface;
import net.ltxprogrammer.changed.client.renderer.model.TorsoedModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.extension.RequiredMods;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.*;

@Mixin(value = AdvancedHumanoidModel.class, remap = false)
@RequiredMods("entity_model_features")
public abstract class AdvancedHumanoidModelMixin<T extends ChangedEntity> extends PlayerModel<T> implements ArmedModel, HeadedModel, TorsoedModel {
    public AdvancedHumanoidModelMixin(ModelPart p_170821_, boolean p_170822_) {
        super(p_170821_, p_170822_);
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

    @Inject(method = "setupAnim", at = @At("TAIL"))
    public void copyPlayerAnimations(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        var player = entity.getUnderlyingPlayer();
        if (player == null)
            return;
        if (!(this instanceof AdvancedHumanoidModelInterface modelInterface))
            return;

        if (Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player) instanceof LivingEntityRenderer<?,?> playerRenderer) {
            if (playerRenderer.getModel() instanceof HumanoidModel playerModel) {
                // Ensure vanilla player model is setup
                playerModel.setupAnim(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
                PartPose headPose = mergePositionRotation(playerModel.head, this.head);
                PartPose bodyPose = mergePositionRotation(playerModel.body, this.body);
                PartPose leftArmPose = mergePositionRotation(playerModel.leftArm, this.leftArm);
                PartPose rightArmPose = mergePositionRotation(playerModel.rightArm, this.rightArm);
                PartPose leftLegPose = mergePositionRotation(playerModel.leftLeg, this.leftLeg);
                PartPose rightLegPose = mergePositionRotation(playerModel.rightLeg, this.rightLeg);
                playerModel.head.copyFrom(this.head);
                playerModel.body.copyFrom(this.body);
                playerModel.leftArm.copyFrom(this.leftArm);
                playerModel.rightArm.copyFrom(this.rightArm);
                playerModel.leftLeg.copyFrom(this.leftLeg);
                playerModel.rightLeg.copyFrom(this.rightLeg);
                if (playerModel.body instanceof EMFModelPartWithState emfPart) {
                    // Run animation code to vanilla model
                    emfPart.allKnownStateVariants.get(emfPart.currentModelVariant).animation().run();
                }

                // Apply animation differences to advanced model
                computeNewPartPose(headPose, playerModel.head, this.head);
                computeNewPartPose(bodyPose, playerModel.body, this.body);
                computeNewPartPose(leftArmPose, playerModel.leftArm, this.leftArm);
                computeNewPartPose(rightArmPose, playerModel.rightArm, this.rightArm);
                computeNewPartPose(leftLegPose, playerModel.leftLeg, this.leftLeg);
                computeNewPartPose(rightLegPose, playerModel.rightLeg, this.rightLeg);

                this.hat.copyFrom(this.head);
                this.jacket.copyFrom(this.body);
                this.leftSleeve.copyFrom(this.leftArm);
                this.rightSleeve.copyFrom(this.rightArm);
                this.leftPants.copyFrom(this.leftLeg);
                this.rightPants.copyFrom(this.rightLeg);

                modelInterface.getAnimator(entity).applyPropertyModelLimbs(this);
            }
        }
    }
}
