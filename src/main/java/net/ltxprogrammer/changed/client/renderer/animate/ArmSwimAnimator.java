package net.ltxprogrammer.changed.client.renderer.animate;

import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import org.jetbrains.annotations.NotNull;

public class ArmSwimAnimator<T extends LatexEntity, M extends EntityModel<T>> extends LatexAnimator.Animator<T, M> {
    public final ModelPart leftArm;
    public final ModelPart rightArm;

    public ArmSwimAnimator(ModelPart leftArm, ModelPart rightArm) {
        this.leftArm = leftArm;
        this.rightArm = rightArm;
    }

    @Override
    public LatexAnimator.AnimateStage preferredStage() {
        return LatexAnimator.AnimateStage.SWIM;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!entity.isUsingItem()) {
            float f5 = limbSwing % 26.0F;
            HumanoidArm humanoidarm = core.getAttackArm(entity);
            float f1 = humanoidarm == HumanoidArm.RIGHT && core.entityModel.attackTime > 0.0F ? 0.0F : core.swimAmount;
            float f2 = humanoidarm == HumanoidArm.LEFT && core.entityModel.attackTime > 0.0F ? 0.0F : core.swimAmount;
            if (f5 < 14.0F) {
                leftArm.xRot = LatexAnimator.rotlerpRad(f2, leftArm.xRot, 0.0F);
                rightArm.xRot = Mth.lerp(f1, rightArm.xRot, 0.0F);
                leftArm.yRot = LatexAnimator.rotlerpRad(f2, leftArm.yRot, (float)Math.PI);
                rightArm.yRot = Mth.lerp(f1, rightArm.yRot, (float)Math.PI);
                leftArm.zRot = LatexAnimator.rotlerpRad(f2, leftArm.zRot, (float)Math.PI + 1.8707964F * this.quadraticArmUpdate(f5) / this.quadraticArmUpdate(14.0F));
                rightArm.zRot = Mth.lerp(f1, rightArm.zRot, (float)Math.PI - 1.8707964F * this.quadraticArmUpdate(f5) / this.quadraticArmUpdate(14.0F));
            } else if (f5 >= 14.0F && f5 < 22.0F) {
                float f6 = (f5 - 14.0F) / 8.0F;
                leftArm.xRot = LatexAnimator.rotlerpRad(f2, leftArm.xRot, ((float)Math.PI / 2F) * f6);
                rightArm.xRot = Mth.lerp(f1, rightArm.xRot, ((float)Math.PI / 2F) * f6);
                leftArm.yRot = LatexAnimator.rotlerpRad(f2, leftArm.yRot, (float)Math.PI);
                rightArm.yRot = Mth.lerp(f1, rightArm.yRot, (float)Math.PI);
                leftArm.zRot = LatexAnimator.rotlerpRad(f2, leftArm.zRot, 5.012389F - 1.8707964F * f6);
                rightArm.zRot = Mth.lerp(f1, rightArm.zRot, 1.2707963F + 1.8707964F * f6);
            } else if (f5 >= 22.0F && f5 < 26.0F) {
                float f3 = (f5 - 22.0F) / 4.0F;
                leftArm.xRot = LatexAnimator.rotlerpRad(f2, leftArm.xRot, ((float)Math.PI / 2F) - ((float)Math.PI / 2F) * f3);
                rightArm.xRot = Mth.lerp(f1, rightArm.xRot, ((float)Math.PI / 2F) - ((float)Math.PI / 2F) * f3);
                leftArm.yRot = LatexAnimator.rotlerpRad(f2, leftArm.yRot, (float)Math.PI);
                rightArm.yRot = Mth.lerp(f1, rightArm.yRot, (float)Math.PI);
                leftArm.zRot = LatexAnimator.rotlerpRad(f2, leftArm.zRot, (float)Math.PI);
                rightArm.zRot = Mth.lerp(f1, rightArm.zRot, (float)Math.PI);
            }
        }
    }

    private float quadraticArmUpdate(float factor) {
        return -65.0F * factor + factor * factor;
    }
}
