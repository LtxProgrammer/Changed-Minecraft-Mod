package net.ltxprogrammer.changed.client.renderer.animate.upperbody;

import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.item.SpecializedAnimations;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractUpperBodyAnimator<T extends ChangedEntity, M extends EntityModel<T>> extends HumanoidAnimator.Animator<T, M> {
    public final ModelPart head;
    public final ModelPart torso;
    public final ModelPart leftArm;
    public final ModelPart rightArm;

    protected SpecializedAnimations.AnimationHandler.UpperModelContext upperModelContext() {
        AbstractUpperBodyAnimator<T, M> tmp = this;
        return new SpecializedAnimations.AnimationHandler.UpperModelContext(leftArm, rightArm, torso, head) {
            final AbstractUpperBodyAnimator<T, M> controller = tmp;

            @Override
            public ModelPart getArm(HumanoidArm humanoidArm) {
                return controller.getArm(humanoidArm);
            }
        };
    }

    protected void poseRightArmForItem(T entity) {
        switch(core.rightArmPose) {
            case EMPTY:
                rightArm.yRot = 0.0F;
                break;
            case BLOCK:
                rightArm.xRot = rightArm.xRot * 0.5F - 0.9424779F;
                rightArm.yRot = (-(float)Math.PI / 6F);
                break;
            case ITEM:
                rightArm.xRot = rightArm.xRot * 0.5F - ((float)Math.PI / 10F);
                rightArm.yRot = 0.0F;
                break;
            case THROW_SPEAR:
                rightArm.xRot = rightArm.xRot * 0.5F - (float)Math.PI;
                rightArm.yRot = 0.0F;
                break;
            case BOW_AND_ARROW:
                rightArm.yRot = -0.1F + head.yRot;
                leftArm.yRot = 0.1F + head.yRot + 0.4F;
                rightArm.xRot = (-(float)Math.PI / 2F) + head.xRot;
                leftArm.xRot = (-(float)Math.PI / 2F) + head.xRot;
                break;
            case CROSSBOW_CHARGE:
                AnimationUtils.animateCrossbowCharge(rightArm, leftArm, entity, true);
                break;
            case CROSSBOW_HOLD:
                AnimationUtils.animateCrossbowHold(rightArm, leftArm, head, true);
                break;
            case SPYGLASS:
                rightArm.xRot = Mth.clamp(head.xRot - 1.9198622F - (entity.isCrouching() ? 0.2617994F : 0.0F), -2.4F, 3.3F);
                rightArm.yRot = head.yRot - 0.2617994F;
        }

    }

    protected void poseLeftArmForItem(T entity) {
        switch(core.leftArmPose) {
            case EMPTY:
                leftArm.yRot = 0.0F;
                break;
            case BLOCK:
                leftArm.xRot = leftArm.xRot * 0.5F - 0.9424779F;
                leftArm.yRot = ((float)Math.PI / 6F);
                break;
            case ITEM:
                leftArm.xRot = leftArm.xRot * 0.5F - ((float)Math.PI / 10F);
                leftArm.yRot = 0.0F;
                break;
            case THROW_SPEAR:
                leftArm.xRot = leftArm.xRot * 0.5F - (float)Math.PI;
                leftArm.yRot = 0.0F;
                break;
            case BOW_AND_ARROW:
                rightArm.yRot = -0.1F + head.yRot - 0.4F;
                leftArm.yRot = 0.1F + head.yRot;
                rightArm.xRot = (-(float)Math.PI / 2F) + head.xRot;
                leftArm.xRot = (-(float)Math.PI / 2F) + head.xRot;
                break;
            case CROSSBOW_CHARGE:
                AnimationUtils.animateCrossbowCharge(rightArm, leftArm, entity, true);
                break;
            case CROSSBOW_HOLD:
                AnimationUtils.animateCrossbowHold(rightArm, leftArm, head, true);
                break;
            case SPYGLASS:
                leftArm.xRot = Mth.clamp(head.xRot - 1.9198622F - (entity.isCrouching() ? 0.2617994F : 0.0F), -2.4F, 3.3F);
                leftArm.yRot = head.yRot + 0.2617994F;
        }
    }

    protected ModelPart getArm(HumanoidArm humanoidArm) {
        return humanoidArm == HumanoidArm.LEFT ? leftArm : rightArm;
    }

    public AbstractUpperBodyAnimator(ModelPart head, ModelPart torso, ModelPart leftArm, ModelPart rightArm) {
        this.head = head;
        this.torso = torso;
        this.leftArm = leftArm;
        this.rightArm = rightArm;
    }

    @Override
    public void copyTo(HumanoidModel<?> humanoidModel) {
        super.copyTo(humanoidModel);
        humanoidModel.body.copyFrom(this.torso);
        humanoidModel.leftArm.copyFrom(this.leftArm);
        humanoidModel.rightArm.copyFrom(this.rightArm);
    }

    @Override
    public void copyFrom(HumanoidModel<?> humanoidModel) {
        super.copyFrom(humanoidModel);
        this.torso.copyFrom(humanoidModel.body);
        this.leftArm.copyFrom(humanoidModel.leftArm);
        this.rightArm.copyFrom(humanoidModel.rightArm);
    }

    // Hook for moonlight mixin
    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}
}
