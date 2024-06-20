package net.ltxprogrammer.changed.client.renderer.animate.upperbody;

import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.NotNull;

public class UpperBodyCrouchAnimator<T extends ChangedEntity, M extends EntityModel<T>> extends AbstractUpperBodyAnimator<T, M> {
    public UpperBodyCrouchAnimator(ModelPart head, ModelPart torso, ModelPart leftArm, ModelPart rightArm) {
        super(head, torso, leftArm, rightArm);
    }

    @Override
    public HumanoidAnimator.AnimateStage preferredStage() {
        return HumanoidAnimator.AnimateStage.CROUCH;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        torso.xRot = 0.5F;
        rightArm.xRot += 0.4F;
        leftArm.xRot += 0.4F;

        torso.y = 4.2f + core.calculateTorsoPositionY();
        head.y = torso.y;
        leftArm.y = 2.0F + torso.y;
        rightArm.y = 2.0F + torso.y;
    }
}
