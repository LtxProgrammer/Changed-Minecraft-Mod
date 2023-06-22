package net.ltxprogrammer.changed.client.renderer.animate.arm;

import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class AquaticArmSwimAnimator<T extends LatexEntity, M extends EntityModel<T>> extends LatexAnimator.Animator<T, M> {
    public final ModelPart leftArm;
    public final ModelPart rightArm;

    public AquaticArmSwimAnimator(ModelPart leftArm, ModelPart rightArm) {
        this.leftArm = leftArm;
        this.rightArm = rightArm;
    }

    @Override
    public LatexAnimator.AnimateStage preferredStage() {
        return LatexAnimator.AnimateStage.SWIM;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        leftArm.xRot = Mth.lerp(core.swimAmount, leftArm.xRot, 0.3F * Mth.cos(limbSwing * 0.33333334F));
        rightArm.xRot = Mth.lerp(core.swimAmount, rightArm.xRot, 0.3F * Mth.cos(limbSwing * 0.33333334F + (float)Math.PI));
    }
}
