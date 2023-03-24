package net.ltxprogrammer.changed.client.renderer.animate;

import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.NotNull;

public class ArmSetTwoStandAnimator<T extends LatexEntity, M extends EntityModel<T>> extends LatexAnimator.Animator<T, M> {
    public final ModelPart leftArm;
    public final ModelPart rightArm;
    public final ModelPart leftArm2;
    public final ModelPart rightArm2;

    public ArmSetTwoStandAnimator(ModelPart leftArm, ModelPart rightArm, ModelPart leftArm2, ModelPart rightArm2) {
        this.leftArm = leftArm;
        this.rightArm = rightArm;
        this.leftArm2 = leftArm2;
        this.rightArm2 = rightArm2;
    }

    @Override
    public LatexAnimator.AnimateStage preferredStage() {
        return LatexAnimator.AnimateStage.FINAL;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        leftArm.y += 2.0f;
        rightArm.y += 2.0F;
    }
}
