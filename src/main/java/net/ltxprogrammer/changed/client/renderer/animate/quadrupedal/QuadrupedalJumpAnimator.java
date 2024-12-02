package net.ltxprogrammer.changed.client.renderer.animate.quadrupedal;

import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.SpringType;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class QuadrupedalJumpAnimator<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> extends AbstractQuadrupedalAnimator<T, M> {
    public QuadrupedalJumpAnimator(ModelPart torso, ModelPart frontLeftLeg, ModelPart frontRightLeg, ModelPart backLeftLeg, ModelPart backRightLeg) {
        super(torso, frontLeftLeg, frontRightLeg, backLeftLeg, backRightLeg);
    }

    @Override
    public HumanoidAnimator.AnimateStage preferredStage() {
        return HumanoidAnimator.AnimateStage.BOB;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        final float deltaY = Mth.clamp((float) entity.getDeltaMovement(this.core.partialTicks).y, -0.8f, 0.8f);

        torso.xRot += deltaY * -0.5f;
        frontLeftLeg.xRot -= deltaY * -0.5f;
        frontRightLeg.xRot -= deltaY * -0.5f;
        backLeftLeg.xRot -= deltaY * -0.5f;
        backRightLeg.xRot -= deltaY * -0.5f;

        torso.y -= deltaY * 5.0f;
    }
}
