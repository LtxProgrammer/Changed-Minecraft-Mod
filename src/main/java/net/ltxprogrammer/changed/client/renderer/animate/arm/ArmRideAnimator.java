package net.ltxprogrammer.changed.client.renderer.animate.arm;

import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.NotNull;

public class ArmRideAnimator<T extends ChangedEntity, M extends EntityModel<T>> extends HumanoidAnimator.Animator<T, M> {
    public final ModelPart leftArm;
    public final ModelPart rightArm;

    public ArmRideAnimator(ModelPart leftArm, ModelPart rightArm) {
        this.leftArm = leftArm;
        this.rightArm = rightArm;
    }

    @Override
    public HumanoidAnimator.AnimateStage preferredStage() {
        return HumanoidAnimator.AnimateStage.RIDE;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        rightArm.xRot += (-(float)Math.PI / 5F);
        leftArm.xRot += (-(float)Math.PI / 5F);
    }
}
